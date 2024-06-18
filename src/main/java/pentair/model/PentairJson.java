package pentair.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pentair.model.messages.NotifyList;
import pentair.model.messages.PentairMessage;

/**
 * Hello world!
 *
 */
public class PentairJson {
//	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
//		System.out.println("Initializing...");
//
//		//String test = "{\"command\": \"RequestParamList\", \"objectList\": [{\"objnam\": \"CHM01\",\"keys\": [\"ORPVAL\",\"PHVAL\"]},{\"objnam\": \"B1101\", \"keys\": [ \"TEMP\"]}],\"messageID\": \"1038386f-8683-426b-a1a1-81e6bc6f7ff5\"}";
//		PentairJson a = new PentairJson();
//
//		try {
//
//			// a.print(a.read(test));
//			ICHEM c = NamedObjects.CHM01.getParams(ICHEM.class);
//
//			RequestParamList reqKeys = new RequestParamList(c, NamedObjects.B1101.getParams());
//
//			a.print(reqKeys);
//
//			c.ORPTNK = 3;
//			c.PHTNK = 4;
//
//			INTELLI lights = NamedObjects.C0003.getParams(INTELLI.class);
//			lights.STATUS = "ON";
//
//			SetParamListReq reqSet = new SetParamListReq(c, lights);
//
//			// System.out.println("str: " + req);
//			a.print(reqSet);
//
//			testFiles(a);
//
//		} catch (Throwable t) {
//			System.out.println(t);
//		}
//		System.out.println("Exiting...");
//	}

	public static void testFiles(PentairJson a) throws JsonProcessingException, IOException {
		File[] files = new File("data/").listFiles();
		System.out.println("Found " + files.length + " file(s)");
		for (File f : files) {
			a.print(a.read(f));
		}
	}

	private final ObjectMapper objectMapper;

	public PentairJson() {
		JsonFactory jsonFactory = new JsonFactory();
		jsonFactory.configure(Feature.AUTO_CLOSE_TARGET, false)
				.configure(com.fasterxml.jackson.core.JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
		objectMapper = new ObjectMapper(jsonFactory).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.setSerializationInclusion(Include.NON_DEFAULT);
		System.out.println("Object mapper created.");
	}

	public PentairMessage<?> read(String s) throws JsonMappingException, JsonProcessingException {
		System.out.println("Parsing string...");
		return objectMapper.readValue(s, PentairMessage.class);
	}

	public PentairMessage<?> read(File f) throws IOException {
		System.out.println("Parsing file " + f.getName());
		InputStream input = new FileInputStream(f);
		PentairMessage<?> msg = objectMapper.readValue(input, PentairMessage.class);
		input.close();
		return msg;
	}

	public void write(OutputStream out, Object msg)
			throws JsonGenerationException, JsonMappingException, IOException {
		objectMapper.writeValue(out, msg);
	}
	
	public void write(OutputStream out, PentairMessage<?> msg)
			throws JsonGenerationException, JsonMappingException, IOException {
		objectMapper.writeValue(out, msg);
	}

	public void print(PentairMessage<?> msg) throws JsonProcessingException {
		System.out.println();
	}
	
	public String toString(PentairMessage<?> msg) throws JsonProcessingException {
		return objectMapper.writeValueAsString(msg);
	}

	public void print(Object msg) throws JsonProcessingException {
		System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg));
	}

	public PentairMessage<?> read(InputStream in) throws JsonParseException, JsonMappingException, IOException {
		return objectMapper.readValue(in, PentairMessage.class);
	}
	
	public <T> T read(Class<T> msgClass, InputStream in) throws JsonParseException, JsonMappingException, IOException {
		return objectMapper.readValue(in, msgClass);
	}
	
	
	public NotifyList readNotify(InputStream in) throws JsonParseException, JsonMappingException, IOException {
		return objectMapper.readValue(in, NotifyList.class);
	}
	
	public NotifyList readNotify(String in) throws JsonParseException, JsonMappingException, IOException {
		return objectMapper.readValue(in, NotifyList.class);
	}
	
}
