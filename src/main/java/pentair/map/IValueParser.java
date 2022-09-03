package pentair.map;

import pentair.model.messages.NotifyList;

/**
 * Takes a NotifyList and extracts a value
 * @author Brian
 *
 * @param <T>
 */
public interface IValueParser<T> {

	public T parse(NotifyList response) throws FormatException;
	
}
