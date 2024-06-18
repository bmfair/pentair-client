package pentair.prometheus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PrometheusQuery {

	public static void main(String[] args) throws IOException, URISyntaxException {
		System.out.println(new PrometheusQuery("192.168.1.2:9090").getLatestValue("pentair_chlorine_dose_oz_total"));
	}

	private final String urlPrefix;
	private final ObjectMapper mapper;

	public PrometheusQuery(String hostAndPort) {
		urlPrefix = "http://" + hostAndPort + "/api/v1/query?query=";
		mapper = new ObjectMapper();
	}

	public Double getLatestValue(String metric)
			throws JsonParseException, JsonMappingException, IOException, URISyntaxException {

		URI uri = new URI(urlPrefix + "max_over_time(" + metric + "[90d])");
		// URL url= new URL(urlPrefix + "max_over_time(" + metric + "[90d])");
		HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
		connection.setRequestProperty("accept", "application/json");
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String rsp = in.readLine();
		PromQueryRsp msg = mapper.readValue(rsp, PromQueryRsp.class);
		if ("success".equals(msg.status) && msg.data.result.length > 0) {
			// String time = msg.data.result.value[0];
			String val = msg.data.result[0].value[1];
			return Double.parseDouble(val);
		} else {
			throw new IOException("Cannot set dosing, unknown response from Prometheus: " + rsp);
		}

	}

}
