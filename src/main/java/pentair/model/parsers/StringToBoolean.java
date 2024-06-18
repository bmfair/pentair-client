package pentair.model.parsers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class StringToBoolean {

	public static class StringToBooleanSerializer extends JsonSerializer<Boolean> {

	    @Override
	    public void serialize(Boolean bool, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
	        generator.writeString(bool ? "ON" : "OFF");
	    }   
	}

	public static class StringToBooleanDeserializer extends JsonDeserializer<Boolean> {

	    @Override
	    public Boolean deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
	    	if ("ON".equals(parser.getText())) return true;
	    	else if ("OFF".equals(parser.getText())) return false;
	    	else {
	    		System.err.println("Error parsing string to boolean: " + parser.getText());
	    	}
	        return false;
	    }       
	}
	
}
