package pentair.prometheus;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PromMetric {
	public String instance;
	public String job;

	public String name;

	@JsonProperty("__name__")
	public String getName() {
		return name;
	}

	@JsonProperty("__name__")
	public void setName(String name) {
		this.name = name;
	}

}
