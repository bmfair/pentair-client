package pentair.model.messages;

import java.util.List;

import pentair.map.MapObj;

public class NotifyList  {

	public String command;
	public long timeSince;
	public long timeNow;
	public int response;
	public String messageID ;
	
	public List<MapObj> objectList;
	public List<MapObj> answer;


}
