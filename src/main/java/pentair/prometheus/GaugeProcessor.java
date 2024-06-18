package pentair.prometheus;


import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import pentair.map.ValueParser;

public class GaugeProcessor extends AbstractMetricProcessor {

	private final Gauge gauge;

	//private final Logger logger = LogManager.getLogger(GaugeProcessor.class);

	public GaugeProcessor(MetricsGroup manager, ValueParser<Double> parser, boolean alwaysEnabled) {
		super(manager, parser, alwaysEnabled);
		this.gauge = Gauge.build().namespace(NAMESPACE).name(getFullName()).help(getFullName()).create();
		if (isEnabled()) {
			onEnabled();
		}
	}

	public void updateMetric(double newValue) {
		gauge.set(newValue);
	}

	@Override
	protected void onEnabled() {
		CollectorRegistry.defaultRegistry.register(gauge);
	}

	@Override
	protected void onDisabled() {
		CollectorRegistry.defaultRegistry.unregister(gauge);
	}

}
