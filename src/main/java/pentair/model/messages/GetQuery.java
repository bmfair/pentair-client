package pentair.model.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;


public class GetQuery {

	public enum QueryNames {
		//Confirmed to Work:
		GetHardwareDefinition, GetConfiguration, GetCircuitNames, GetStatusMessageFlags, GetActiveStatusMessages, GetValveConfiguration, GetPumpConfiguration, GetHeaterConfiguration, GetRemotes, GetCircuitTypes,
		
		
		//Not figured out yet:
		GetRegisteredProperties;
	}
	
	public String command = "GetQuery";
	public QueryNames queryName = QueryNames.GetHardwareDefinition; // "GetHardwareDefinition";
	
	 @JsonInclude(JsonInclude.Include.ALWAYS)
	public List<Object> arguments = new ArrayList<>();
	 
	public String messageID = UUID.randomUUID().toString();

	
	

}
