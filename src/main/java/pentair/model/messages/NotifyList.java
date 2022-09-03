package pentair.model.messages;

import java.util.List;
import java.util.UUID;

import pentair.map.MapObj;
import pentair.model.ObjectParams;
import pentair.model.ParamList;

public class NotifyList  {

	public String command;
	public long timeSince;
	public long timeNow;
	public int response;
	public String messageID ;
	
	public List<MapObj> objectList;
	public List<MapObj> answer;


}
