package pentair.model.messages;

import pentair.model.ObjectParams;
import pentair.model.ParamList;

public class PentairResponse extends PentairMessage<ParamList> {

	public long timeSince;
	public long timeNow;
	public int response;

	public PentairResponse() {
	}

	public PentairResponse(ParamList... objectList) {
		super(objectList);
		timeSince = 0;
		timeNow = System.currentTimeMillis();
	}

	public PentairResponse(ObjectParams... params) {
		this(toParamList(false, params));
	}

}
