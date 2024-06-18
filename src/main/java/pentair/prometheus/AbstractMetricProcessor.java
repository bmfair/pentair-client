package pentair.prometheus;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pentair.map.FormatException;
import pentair.map.ValueParser;
import pentair.model.Keys;
import pentair.model.NamedObjects;
import pentair.model.messages.NotifyList;

public abstract class AbstractMetricProcessor {

	public static final String NAMESPACE = "pentair";

	private boolean isEnabled = false;
	private final boolean alwaysEnabled;

	private final MetricsGroup manager;
	private final ValueParser<Double> parser;

	private final Logger logger = LogManager.getLogger(AbstractMetricProcessor.class);

	public AbstractMetricProcessor(MetricsGroup manager, ValueParser<Double> parser, boolean alwaysEnabled) {
		this.manager = manager;
		this.parser = parser;
		this.alwaysEnabled = alwaysEnabled;
		if (alwaysEnabled) {
			this.isEnabled = true;
		}
	}

	/**
	 * This is used to build a keylist for the request message
	 * 
	 * @param map
	 * @return
	 */
	public Map<NamedObjects, Set<Keys>> populateKey(Map<NamedObjects, Set<Keys>> map) {
		Set<Keys> s = map.getOrDefault(parser.getName(), new HashSet<>());
		s.add(parser.getKey());
		map.put(parser.getName(), s);
		return map;
	}

	public final boolean isEnabled() {
		return this.isEnabled;
	}

	public final void setEnabled(boolean isEnabled) {
		if (alwaysEnabled)
			return;
		if (isEnabled != this.isEnabled) {
			// state change
			if (isEnabled)
				onEnabled();
			else
				onDisabled();
		}
		this.isEnabled = isEnabled;
	}

	protected abstract void onEnabled();

	protected abstract void onDisabled();

	/**
	 * This is the metric's full name (without namespace)
	 * 
	 * @return
	 */
	public final String getFullName() {
		if (manager != null)
			return manager.getFullName() + "_" + getName();
		else
			return getName();
	}

	public final String getName() {
		return this.parser.getName().name() + "_" + this.parser.getKey().name();
	}

	public final void update(NotifyList response) {
		logger.info("{} metric processing update", getFullName());
		try {
			Double newValue = parser.parse(response);
			if (newValue != null) {
				updateMetric(newValue);
			}
		} catch (FormatException e) {
			logger.error("Error parsing value for {}", getFullName(), e);
		}
	}

	protected abstract void updateMetric(double newValue);

}
