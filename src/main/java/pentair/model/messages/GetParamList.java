package pentair.model.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import pentair.map.MapObj;

public class GetParamList {

	public String command = "GetParamList";
	 
	public List<MapObj> objectList = new ArrayList<>();
	
	public String messageID = UUID.randomUUID().toString();

	
}
