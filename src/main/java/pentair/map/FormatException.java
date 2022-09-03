package pentair.map;

public class FormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FormatException(Class<?> type, String value) {
		super("Unrecognized value: " + value + ", for type: " + type.getSimpleName());
	}

	public FormatException(Class<?> type, String value, Throwable t) {
		super("Unrecognized value: " + value + ", for type: " + type.getSimpleName(), t);
	}

	public FormatException(Class<?> type, String fromType, String value) {
		super("Unrecognized value: " + value + ", when mapping " + fromType + " to type: " + type.getSimpleName());
	}

	public FormatException(String name, String key, FormatException parseException) {
		super("Eror parsing object:" + name + ", key: " + key + ", Error: " + parseException, parseException);
	}

}
