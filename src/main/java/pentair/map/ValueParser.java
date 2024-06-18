package pentair.map;

import pentair.model.Keys;
import pentair.model.NamedObjects;
import pentair.model.messages.NotifyList;

/**
 * This class extracts a value from a pentair message
 * 
 * @author Brian
 *
 * @param <T>
 */
public class ValueParser<T> implements IValueParser<T>{

	public static ValueParser<Double> newDouble(NamedObjects objName, Keys key) {
		return new ValueParser<>(objName, key, Parser.DOUBLE);
	}

	public static ValueParser<Boolean> newStatus(NamedObjects objName) {
		return new ValueParser<>(objName, Keys.STATUS, Parser.STATUS_BOOLEAN);
	}

	public static ValueParser<Double> newStatusDouble(NamedObjects objName) {
		return new ValueParser<>(objName, Keys.STATUS, Parser.ON_OFF_DOUBLE);
	}

	private final NamedObjects objName;
	private final Keys key;
	private final Parser<T> parser;

	public ValueParser(NamedObjects objName, Keys key, Parser<T> parser) {
		this.objName = objName;
		this.key = key;
		this.parser = parser;
	}

	public Keys getKey() {
		return this.key;
	}

	public NamedObjects getName() {
		return this.objName;
	}

	/**
	 * Extracts the string value, returning null if not found
	 * 
	 * @param response
	 * @return
	 */
	public T parse(NotifyList response) throws FormatException {
		MapObj m = getMap(response, this.objName);
		if (m != null) {
			String valStr = m.params.getProperties().get(key.name());
			if (valStr != null) {
				try {
					return parser.parse(valStr);
				} catch (FormatException e) {
					throw new FormatException(getName().name(), getKey().name(), e);
				}
			}
		}
		return null;
	}

	public MapObj getMap(NotifyList response, NamedObjects objName) {
		for (MapObj m : response.objectList) {
			if (objName.name().equals(m.objnam))
				return m;
		}
		return null;
	}

}
