package pentair.prometheus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map.Entry;

import pentair.map.FormatException;
import pentair.map.IValueParser;
import pentair.map.MapObj;
import pentair.map.ValueParser;
import pentair.model.KeyList;
import pentair.model.Keys;
import pentair.model.NamedObjects;
import pentair.model.messages.NotifyList;
import pentair.model.messages.RequestParamList;

/**
 * This holds a group of metrics that are logically related, and allows metrics
 * in the group to be unregistered if the group is disabled based on a supplied
 * lamba.
 * 
 * @author Brian
 *
 */
public class MetricsGroup {

	private static final IValueParser<Boolean> ALWAYS_ON = (NotifyList l) -> true;

	private final String name;

	// private Counter validCounter;

	private final IValueParser<Boolean> enabledParser;
	private boolean isEnabled = false;

	private final Logger logger = LogManager.getLogger(MetricsGroup.class);

	private final List<AbstractMetricProcessor> metrics = new ArrayList<>();

	private List<MetricsGroup> childGroups = new ArrayList<MetricsGroup>();

	private final MetricsGroup parent;

	/**
	 * Populates a map of object names and their keys based on the parsers in this
	 * group, used for creating a request message.
	 * 
	 * @param map
	 * @return
	 */
	public Map<NamedObjects, Set<Keys>> populateKeys(Map<NamedObjects, Set<Keys>> map) {
		for (AbstractMetricProcessor metric : metrics) {
			metric.populateKey(map);
		}
		return map;
	}

	public MetricsGroup(String name, IValueParser<Boolean> enabledParser, MetricsGroup parent) {
		this.name = name;
		this.enabledParser = enabledParser;
		this.parent = parent;
	}

	/**
	 * Consturctor most often used for creating the "root" element.
	 * @param name
	 */
	public MetricsGroup(String name) {
		this(name, ALWAYS_ON, null);
	}

	public RequestParamList createRequest() {
		RequestParamList req = new RequestParamList();
		req.objectList = new ArrayList<KeyList>();
		Map<NamedObjects, Set<Keys>> map = new HashMap<NamedObjects, Set<Keys>>();
		for (MetricsGroup m : childGroups) {
			m.populateKeys(map);
		}
		for (Entry<NamedObjects, Set<Keys>> e : map.entrySet()) {
			KeyList k = new KeyList();
			k.objnam = e.getKey().name();
			k.keys.addAll(e.getValue());
			req.objectList.add(k);
		}
		return req;
	}

	protected final void addGauge(ValueParser<Double> parser, boolean alwaysEnabled) {
		GaugeProcessor child = new GaugeProcessor(this, parser, alwaysEnabled);
		this.metrics.add(child);
	}

	public MetricsGroup addChild(String name, IValueParser<Boolean> enabledParser) {
		MetricsGroup child = new MetricsGroup(name, enabledParser, this);
		this.childGroups.add(child);
		return child;
	}

	public MetricsGroup addBody(NamedObjects body) {
		MetricsGroup child = addChild(body.name(), ValueParser.newStatus(body));
		child.addGauge(ValueParser.newDouble(NamedObjects.SSW11, Keys.PROBE), false);
		child.addGauge(ValueParser.newStatusDouble(body), true);
		return child;
	}

	public boolean isEnabled() {
		return this.isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		if (isEnabled != this.isEnabled) {
			// Our state changed, update as needed
			logger.info("{} state changed to {}", getFullName(), isEnabled);
			if (isEnabled)
				onEnabled();
			else
				onDisabled();
		}
		this.isEnabled = isEnabled;
	}

	public String getFullName() {
		if (parent != null)
			return parent.getFullName() + "_" + getName();
		else
			return getName();
	}

	public String getName() {
		return this.name;
	}

	public MapObj getMap(NotifyList response, NamedObjects objName) {
		for (MapObj m : response.objectList) {
			if (objName.name().equals(m.objnam))
				return m;
		}
		return null;
	}

	/**
	 * Primary method to update metrics when a new message is received. This will
	 * call {@link MetricsGroup#processObject(MapObj)} which in turn will call
	 * {@link #processValue(Entry, double)}. This method is designed to be
	 * overriden.
	 * 
	 * @param response
	 */
	public final void update(NotifyList response) {
		logger.info("{} manager processing update", getFullName());

		// Check if we should be enabled
		Boolean newEnabled = null;
		try {
			newEnabled = enabledParser.parse(response);
		} catch (FormatException e) {
			logger.error("Enable parser failed to read value", e);
		}

		if (newEnabled != null) {
			setEnabled(newEnabled);
		}

		// Update our local metrics first
		for (AbstractMetricProcessor metric : metrics) {
			if (metric.isEnabled()) {
				metric.update(response);
			}
		}

		// Now update any child objects
		for (MetricsGroup child : this.childGroups) {
			child.update(response);
		}

	}

	protected void onEnabled() {
		for (AbstractMetricProcessor p : this.metrics) {
			p.setEnabled(true);
		}
		// TODO: should child groups be enabled/disabled?
	}

	protected void onDisabled() {
		for (AbstractMetricProcessor p : this.metrics) {
			p.setEnabled(false);
		}
		// TODO: should child groups be enabled/disabled?
	}

}
