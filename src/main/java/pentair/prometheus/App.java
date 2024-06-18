package pentair.prometheus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Set;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.HTTPServer;
import pentair.map.MapObj;
import pentair.model.KeyList;
import pentair.model.Keys;
import pentair.model.NamedObjects;
import pentair.model.messages.NotifyList;
import pentair.model.messages.RequestParamList;

/**
 * Hello world!
 *
 */
public class App implements Runnable, Consumer<ResilientClient> {

	private static final String NAMESPACE = "pentair";

	private static final String DEFAULT_PENTAIR_IP = "192.168.1.249";
	private static final int DEFAULT_PENTAIR_PORT = 6681;
	private static final int DEFAULT_EXIT_ON_DB_FAIL = 0;

	/**
	 * 0.0.0.11 added resilient client, new SI calculations, and mL to ounce dosing
	 * conversion logic change 0.0.0.12 added periodic send logic because still
	 * wasn't getting failed connections when network was unavailable. Added flush
	 * to log output.
	 */
	private static final String VERSION = "0.0.0.17";

	/**
	 * For querying prometheus for previous counter values after reboot
	 */
	private static final String DEFAULT_PROM_IP_PORT = "192.168.1.2:9090";

	/**
	 * This is private to container and exposed by dockerfile
	 */
	private static final int METRICS_PORT = 8080;

	public static void main(String[] args) throws IOException, URISyntaxException {
		System.out.println("Starting Brian Fair's Pentair Prometheus Metrics Gateway");
		System.out.println("VERSION: " + VERSION);

		// Load values from ENV, falling back to defaults if not present
		Map<String, String> env = System.getenv();
		String pentairIp = env.getOrDefault("PENT_IP", DEFAULT_PENTAIR_IP);
		int pentairPort = Integer.parseInt(env.getOrDefault("PENT_PORT", String.valueOf(DEFAULT_PENTAIR_PORT)));
		String promIpPort = env.getOrDefault("PENT_PROM_IP_PORT", DEFAULT_PROM_IP_PORT);
		boolean exitIfDbFail = Integer
				.parseInt(env.getOrDefault("DEFAULT_EXIT_ON_DB_FAIL", String.valueOf(DEFAULT_EXIT_ON_DB_FAIL))) > 0;

		// Launch our app
		try {
			App a = new App(pentairIp, pentairPort, METRICS_PORT, promIpPort, exitIfDbFail);
			Thread bgThread = new Thread(a);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> a.close()));
			bgThread.start();
		} catch (IOException e) {
			System.err.println("ERROR: Failed to start application, terminating. Exception: " + e);
			e.printStackTrace(System.err);
			System.exit(-1);
		}
	}

	private ResilientClient client;

	/**
	 * The first String key is the NamedObject's name This provides access to
	 * another map, indexed by the Key's name, followed by the metric for that key
	 */
	private Map<String, Map<String, Gauge>> metrics = new HashMap<>();

	private List<KeyList> keyList = new ArrayList<KeyList>(); // For the request message

	private final Set<String> keysNotGauges = new HashSet<String>();

	private List<Gauge> inactiveGauges = new ArrayList<Gauge>();

	private boolean closed = false;
	private HTTPServer server;
	private Counter msgCount, fieldValidCount, fieldInvalidCount;

	// For tracking total amount of chems dosed
	private Counter chlorDoseCount, acidDoseCount;

	private boolean isPumpOff, hasFlowDelay, isNoFlow = true; // We're going to assume the pump is off first

	// For tracking heater status
	private Gauge poolHeatSolar, poolHeatGas, poolTemp, poolOn, spaOn, spaTemp, spaHeatSolar, spaHeatGas, bmfSI, gCYA,
			gTemp, gCalc, gPH, gALK;

	private double tempFactor, alkalinityFactor, calciumFactor, adjTA;

	public App(String ip, int port, int metricsPort, String promIpPort, boolean exitIfDbFail)
			throws IOException, URISyntaxException {

		LogUtil.log().info("Initializing...VERSION: " + VERSION);

		// Things we're going to measure
		addGauges(NamedObjects.CHM01, Keys.ORPVAL, Keys.ORPVOL, Keys.ORPTNK, Keys.ORPSET, Keys.PHVAL, Keys.PHVOL,
				Keys.PHTNK, Keys.PHSET, Keys.NOFLO, Keys.FLOWDLY, Keys.SINDEX, Keys.QUALTY, Keys.ALK, Keys.CALC,
				Keys.CYACID); // Keys.TEMP, //(removed in newer firmware)

		addGauges(NamedObjects.PMP01, Keys.RPM, Keys.GPM, Keys.PWR);
		addGauges(NamedObjects._A135, Keys.PROBE);
		addGauges(NamedObjects.SSS11, Keys.PROBE);

		// This is just for referencing them easier later so I don't have to look them
		// up all the time. I'm calculating SI myself too.
		gCYA = getGauge(NamedObjects.CHM01, Keys.CYACID);

		// gTemp = getGauge(NamedObjects.B1101, Keys.TEMP); //Broken after firmware
		// update
		gTemp = getGauge(NamedObjects._A135, Keys.PROBE);

		gCalc = getGauge(NamedObjects.CHM01, Keys.CALC);
		gPH = getGauge(NamedObjects.CHM01, Keys.PHVAL);
		gALK = getGauge(NamedObjects.CHM01, Keys.ALK);
		bmfSI = Gauge.build().namespace(NAMESPACE).name("bmf_si")
				.help("LSI Saturation Index based on values from IntelliChem").register();

		// We don't want to process these for guage values
		keysNotGauges.add(Keys.NOFLO.name());
		keysNotGauges.add(Keys.FLOWDLY.name());

		poolHeatSolar = Gauge.build().namespace(NAMESPACE).name("pool_heat_solar_on")
				.help("0 indicates heater is off, 1 indicates on").register();
		poolHeatGas = Gauge.build().namespace(NAMESPACE).name("pool_heat_gas_on")
				.help("0 indicates heater is off, 1 indicates on").register();
		poolOn = Gauge.build().namespace(NAMESPACE).name("pool_on").help("0 indicates off, 1 indicates on").register();
		poolTemp = Gauge.build().namespace(NAMESPACE).name("pool_temp").help("Pool water temp in farenheit").create(); // dont
																														// register
																														// yet

		spaHeatSolar = Gauge.build().namespace(NAMESPACE).name("spa_heat_solar_on")
				.help("0 indicates heater is off, 1 indicates on").register();
		spaHeatGas = Gauge.build().namespace(NAMESPACE).name("spa_heat_gas_on")
				.help("0 indicates heater is off, 1 indicates on").register();
		spaOn = Gauge.build().namespace(NAMESPACE).name("spa_on").help("0 indicates off, 1 indicates on").register();

		spaTemp = Gauge.build().namespace(NAMESPACE).name("spa_temp").help("Spa water temp in farenheit").create(); // dont
																													// register

		// yet

		keyList.add(new KeyList(NamedObjects.B1101, Keys.STATUS, Keys.HTMODE));
		keyList.add(new KeyList(NamedObjects.B1202, Keys.STATUS, Keys.HTMODE));
		keyList.add(new KeyList(NamedObjects.SSW11, Keys.PROBE));

		// These we dont want to monitor if the pump isn't on
		inactiveGauges.add(getGauge(NamedObjects.CHM01, Keys.ORPVAL));
		inactiveGauges.add(getGauge(NamedObjects.CHM01, Keys.PHVAL));
		inactiveGauges.add(getGauge(NamedObjects.CHM01, Keys.SINDEX));

		updateChemRegistration();

		// For tracking issues/performance

		msgCount = Counter.build().namespace(NAMESPACE).name("messages_total").help("All messages received").register();
		fieldValidCount = Counter.build().namespace(NAMESPACE).name("valid_field_total")
				.help("Number of async values received and parsed").register();
		fieldInvalidCount = Counter.build().namespace(NAMESPACE).name("invalid_field_total")
				.help("Number of async values received that were null or not parsed").register();

		// Load prior values for dosing to maintain a true total upcount
		initializeChemState(promIpPort, exitIfDbFail);

		// This will now result in a callback to us where we'll send our request
		client = new ResilientClient(ip, port, this);

		// This won't happen until the above is connected and we've sent the request
		NotifyList rsp = client.tryRead(msgCount);
		msgCount.inc();
		LogUtil.log().info("Initial Response", rsp);
		if (rsp.response != 200) {
			LogUtil.log().error("Server returned error", new IOException("Response!=200"), rsp);
			close();
		} else {
			update(true, rsp);
		}

		// Now that everything made it through it's initial state, let's publish metrics
		LogUtil.log().info("Initializing metrics server on port " + metricsPort);
		server = new HTTPServer(metricsPort);
		LogUtil.log().info("Init complete!");
	}

	/**
	 * I didn't want my total dose counters to get reset, so I'm pulling their last
	 * value from prometheus (since it's essentially a database).
	 * 
	 * @param promIpPort
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private void initializeChemState(String promIpPort, boolean exitIfDbFail) throws IOException, URISyntaxException {

		// Just create - we want to set their values and then register
		chlorDoseCount = Counter.build().namespace(NAMESPACE).name("chlorine_dose_oz_total")
				.help("Total ounces of chlorine").create();
		acidDoseCount = Counter.build().namespace(NAMESPACE).name("acid_dose_oz_total").help("Total ounces of acid")
				.create();
//		PrometheusQuery q = new PrometheusQuery(promIpPort);
//		try {
//			initializeChemStateHelper(chlorDoseCount, "pentair_chlorine_dose_oz_total", q);
//			initializeChemStateHelper(acidDoseCount, "pentair_acid_dose_oz_total", q);
//		} catch (IOException e) {
//			// I think it's vital that the above worked, so we should exit if it failed.
//			LogUtil.log().error("Failed to load dosing", e, null);
//			if (exitIfDbFail)
//				throw new IOException("Failed to load historical dosing data from Prometheus, exiting", e);
//		}
		// BMF: Oct 2023, disabled prom lookup and just register the counters
		CollectorRegistry.defaultRegistry.register(chlorDoseCount);
		CollectorRegistry.defaultRegistry.register(acidDoseCount);

	}

//	/**
//	 * This extracts the latest (historic/persisted) value from a specified
//	 * prometheus metric.
//	 * 
//	 * @param c
//	 * @param metric
//	 * @param q
//	 * @throws JsonParseException
//	 * @throws JsonMappingException
//	 * @throws IOException
//	 * @throws URISyntaxException
//	 */
//	private void initializeChemStateHelper(Counter c, String metric, PrometheusQuery q)
//			throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
//		double d = q.getLatestValue(metric);
//		LogUtil.log().info("Recovered " + metric + " value: " + d);
//		// c.inc(d); // TODO: I don't know why I put this here, I've commented out
//		//Update: the above sets the recovered value...without it, this method is pointless...
//		CollectorRegistry.defaultRegistry.register(c);
//	}

	/**
	 * If we don't have flow, then we want to de-register our chem gauges; otherwise
	 * we want to register them
	 */
	private void updateChemRegistration() {
		if (isPumpOff || isNoFlow || hasFlowDelay) {
			LogUtil.log().info("Chem inactive, unregistering gauages");
			for (Gauge g : inactiveGauges) {
				try {
					CollectorRegistry.defaultRegistry.unregister(g);
				} catch (NullPointerException ex) {
					LogUtil.log().warn("Chem gauges already unregistered", null, null);
				}
			}
		} else {
			LogUtil.log().info("Chem active, registering gauages");
			for (Gauge g : inactiveGauges) {
				try {
					CollectorRegistry.defaultRegistry.register(g);
				} catch (IllegalArgumentException ex) {
					LogUtil.log().warn("Chem gauges already registered", null, null);
				}
			}
		}

	}

	private Gauge getGauge(NamedObjects obj, Keys k) {
		return metrics.get(obj.name()).get(k.name());
	}

	private void addGauges(NamedObjects obj, Keys... keys) {
		Map<String, Gauge> tmp = metrics.computeIfAbsent(obj.name(), (String s) -> new HashMap<>());
		this.keyList.add(new KeyList(obj, keys)); // For the request message
		for (Keys k : keys) {
			tmp.put(k.name(), Gauge.build().namespace(NAMESPACE).name(obj.name() + "_" + k.name())
					.help(obj.name() + "'s " + k.name()).register());
		}
	}

	private void updateChemState(NotifyList rsp) {
		boolean needsUpdate = false;
		for (MapObj l : rsp.objectList) {
			if (NamedObjects.PMP01.name().equals(l.objnam)) {
				String s = l.params.getProperties().get(Keys.RPM.name());
				if (s != null) {
					boolean isOff = Integer.parseInt(s) <= 0;
					if (this.isPumpOff != isOff) {
						// Pump turned on or off
						this.isPumpOff = isOff;
						needsUpdate = true;
					}
				}
			} else if (NamedObjects.CHM01.name().equals(l.objnam)) {
				String s = l.params.getProperties().get(Keys.NOFLO.name());
				if (s != null) {
					boolean noFLow = "ON".equals(s);
					if (this.isNoFlow != noFLow) {
						// No flow changed
						this.isNoFlow = noFLow;
						needsUpdate = true;
					}
				}
				s = l.params.getProperties().get(Keys.FLOWDLY.name());
				if (s != null) {
					boolean flowDelay = "ON".equals(s);
					if (this.hasFlowDelay != flowDelay) {
						// No flow changed
						this.hasFlowDelay = flowDelay;
						needsUpdate = true;
					}
				}
			}
		}
		if (needsUpdate)
			updateChemRegistration();
	}

	public double convertDoseToOz(double value) {
		// 21,784 = 3 oz (89 x 256)
		// Makes me think 30x 256 = 1 oz...
		// So divide by 7680 to get to oz
		// return value / 7680.0;

		// Update Nov 2021
		// It could be this is in millileters; e.g. each 256 increment is 1 mL: 29.5735
		// mL = 1 oz ?
		return (value / 256.0) / 29.57352968750042;
	}

	private void updateDose(String key, Counter c, double oldValue, double newValue) {
		/*
		 * OK, so this increments in 256 increments and it also loops around. I think we
		 * should apply a 256 offset since I believe "0" is the first dose
		 */

		// Detect if this is a new dosing routine and reset the old value
		if (newValue == 0 || (newValue < oldValue)) {
			oldValue = 0;
		}
		newValue = newValue + 256; // The old value will have already been offset by 256

		if (newValue < 256) {
			// We should never be here.
			LogUtil.log().warn(
					"Invalid dose value for " + key + ". Old Value: " + oldValue + ", New Value: " + newValue, null,
					null);
		} else {
			double change = newValue - oldValue;
			double ozInc = convertDoseToOz(change);
			// log("Detected " + key + " change " + change + " which = " + ozInc + "oz");
			c.inc(ozInc);
		}
	}

	private void updateDoseIfDosed(String key, double oldValue, double newValue) {
		if (Keys.ORPVOL.name().equals(key)) {
			updateDose(key, chlorDoseCount, oldValue, newValue);
		} else if (Keys.PHVOL.name().equals(key)) {
			updateDose(key, acidDoseCount, oldValue, newValue);
		} else {
			// Ignore. I expect other keys to be passed here that aren't dosing info
		}
	}

	private void updateBody(boolean isInitial, MapObj l, Gauge body, Gauge bodyTemp, Gauge gas, Gauge solar) {
		LogUtil.log().info("Updating body " + l.objnam);
		String status = l.params.getProperties().get(Keys.STATUS.name());

		if ("OFF".equals(status)) {
			// OFF
			body.set(0);
			try {
				if (!isInitial) {
					// By default we're not registered, so don't try if first go
					CollectorRegistry.defaultRegistry.unregister(bodyTemp);
				}
			} catch (NullPointerException e) {
				LogUtil.log().warn("Shouldn't have tried to unregister body temp", e, null);
			}
		} else if ("ON".equals(status)) {
			body.set(1);
			try {
				// By default we're not registered, so still need to do on initial
				CollectorRegistry.defaultRegistry.register(bodyTemp);
			} catch (IllegalArgumentException e) {
				LogUtil.log().warn("Shouldn't have tried to register body temp", e, null);
			}
		} else {
			// TODO: don't change it?
		}

		// Now heat mode
		String htmode = l.params.getProperties().get(Keys.HTMODE.name());

		if ("0".equals(htmode)) {
			// OFF
			gas.set(0);
			solar.set(0);

		} else if ("1".equals(htmode)) {
			// GAS
			gas.set(1);
			solar.set(0);

		} else if ("2".equals(htmode)) {
			// Solar
			gas.set(0);
			solar.set(1);
		} else {
			// TODO: unknown case
			gas.set(0);
			solar.set(0);
		}
	}

	private void updateSI() {
		tempFactor = -0.0000005 * Math.pow(gTemp.get(), 3) + 0.00006 * Math.pow(gTemp.get(), 2) + 0.0117 * gTemp.get()
				- 0.4116;
		adjTA = gALK.get() - (gCYA.get() / 3.0);
		alkalinityFactor = Math.log10(adjTA);
		calciumFactor = Math.log10(gCalc.get()) - 0.4;
		bmfSI.set(gPH.get() + tempFactor + alkalinityFactor + calciumFactor - 12.1);
	}

	private void updateBodies(boolean isInitial, NotifyList rsp) {
		for (MapObj l : rsp.objectList) {
			if (NamedObjects.B1101.name().equals(l.objnam)) {
				updateBody(isInitial, l, poolOn, poolTemp, poolHeatGas, poolHeatSolar);
			} else if (NamedObjects.B1202.name().equals(l.objnam)) {
				updateBody(isInitial, l, spaOn, spaTemp, spaHeatGas, spaHeatSolar);
			} else {
				continue;
			}
		}
	}

	/**
	 * This is the meat of this class, it gets an async repsonse from the server and
	 * updates everything it can.
	 * 
	 * @param isInitial
	 * @param rsp
	 */
	private void update(boolean isInitial, NotifyList rsp) {

		// If there's no object list, abort
		if (rsp.objectList == null || rsp.objectList.size() == 0) {
			fieldInvalidCount.inc();
			LogUtil.log().warn("Response missing object list, ignoring", null, rsp);
			return;
		}

		// Important to do before we process the rest of the message so we dont publish
		// values that aren't true
		updateChemState(rsp);

		updateBodies(isInitial, rsp);

		for (MapObj l : rsp.objectList) {

			// We have a special case for water temp since it switches bodies
			if (NamedObjects.SSW11.name().equals(l.objnam)) {
				String tmp = l.params.getProperties().get(Keys.PROBE.name());
				if (tmp != null) {
					try {
						double d = Double.parseDouble(tmp);
						// It's ok to set on both, since one should be unregistered
						spaTemp.set(d);
						poolTemp.set(d);
					} catch (NumberFormatException e) {
						LogUtil.log().warn("Number format exception parsing body temperature", e, rsp);
					}
				}
			}

			Map<String, Gauge> objMetrics = metrics.get(l.objnam);

			// If we don't have metrics for this, skip and go to next
			if (objMetrics == null) {
				// THe pool object will trigger this - ignore and continue
				// log("Unexpected object name " + l.objnam, new
				// IllegalArgumentException("Unexpected object name"), rsp);
				continue; // Go to the next object
			}

			for (Entry<String, String> entry : l.params.getProperties().entrySet()) {
				if (keysNotGauges.contains(entry.getKey())) {
					continue; // skip this entry
				}
				Gauge g = objMetrics.get(entry.getKey());
				if (g == null) {
					fieldInvalidCount.inc();
					LogUtil.log().warn("Unexpected key for " + l.objnam + ":" + entry.getKey(), null, rsp);
				} else if (entry.getValue() == null) {
					fieldInvalidCount.inc();
					LogUtil.log().warn("Key missing value key for " + l.objnam + ":" + entry.getKey(), null, rsp);
				} else {
					try {
						double d = Double.parseDouble(entry.getValue());
						// log("Processed " + l.objnam + ":" + entry.getKey() + ": " + d);

						if (!isInitial) { // We don't want to add our initial again.
							/*
							 * Note to self - the gauge holding the "present" value below is not to be
							 * confused with The counter that is tracking the total oz level. This gauge is
							 * the raw 256 value from Pentair.
							 */
							// Important to do this before updating the gauge since I'm using it to hold the
							// old value to detect change
							updateDoseIfDosed(entry.getKey(), g.get(), d);
						}
						fieldValidCount.inc();
						g.set(d);
					} catch (NumberFormatException e) {
						fieldInvalidCount.inc();
						LogUtil.log().warn("Failed to parse double from " + l.objnam + ":" + entry.getKey(), e, rsp);
					}
				}
			}
			// After all the gauges are updated, calculate the SI my way:
			updateSI();
		}
	}

	@Override
	public void run() {
		NotifyList rsp = null;
		while (!closed) {
			try {
				rsp = client.tryRead(msgCount);
				update(false, rsp);
			} catch (Throwable e) {
				LogUtil.log().error("Unexpected error, exiting...", e, rsp);
				break;
			}
		}
		close();
	}

	public void close() {
		LogUtil.log().info("Closing...");
		this.closed = true;
		try {
			this.client.close();
			LogUtil.log().info("Client closed.");
		} catch (IOException e) {
			LogUtil.log().warn("Failed trying to close pentair client connection", e, null);
		}
		this.server.close();
		LogUtil.log().info("Server Closed");

		// System.exit(-1); //Do not put this here, it will cause infinite loop and
		// prevent from closing
	}

	private void registerForMessages(ResilientClient c) {
		RequestParamList req = new RequestParamList(keyList.toArray(new KeyList[0]));
		LogUtil.log().info("Sending request", req);
		try {
			c.trySend(req);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Invalid JSON", e);
		}
	}

	/**
	 * This is called when our connection is established or reset
	 */
	@Override
	public void accept(ResilientClient c) {
		registerForMessages(c);
	}

}
