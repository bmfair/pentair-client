package pentair.model.messages;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class AnyMessage {

	public Commands command;

	private Map<String, String> properties = new HashMap<>();

	@JsonAnyGetter
	public Map<String, String> getProperties() {
		return properties;
	}

	@JsonAnySetter
	public void add(String key, String value) {
		properties.put(key, value);
	}

	public String messageID = UUID.randomUUID().toString();

}
