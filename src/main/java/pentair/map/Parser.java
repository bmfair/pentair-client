package pentair.map;

public interface Parser<T> {

	public T parse(String value) throws FormatException;

	public static final Parser<String> STRING = (String s) -> s;
	public static final Parser<Integer> INTEGER = (String s) -> parseInt(s);
	public static final Parser<Double> DOUBLE = (String s) -> parseDouble(s);
	public static final Parser<Boolean> STATUS_BOOLEAN = (String s) -> parseStatus(s);
	public static final Parser<Double> ON_OFF_DOUBLE = (String s) -> parseOnOffToDouble(s);

	public static Boolean parseStatus(String s) throws FormatException {
		if ("ON".equals(s))
			return true;
		if ("OFF".equals(s))
			return false;
		throw new FormatException(Boolean.class, "STATUS", s);
	}

	public static Double parseOnOffToDouble(String s) throws FormatException {
		if ("ON".equals(s))
			return 1.0;
		if ("OFF".equals(s))
			return 0.0;
		throw new FormatException(Double.class, "ON-OFF", s);
	}
	
	public static Double parseDouble(String s) throws FormatException {
		try {
			return Double.parseDouble(s);
		} catch (Throwable t) {
			throw new FormatException(Double.class, s, t);
		}
	}

	public static Integer parseInt(String s) throws FormatException {
		try {
			return Integer.parseInt(s);
		} catch (Throwable t) {
			throw new FormatException(Integer.class, s, t);
		}
	}

}
