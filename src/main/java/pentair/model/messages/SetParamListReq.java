package pentair.model.messages;

import pentair.model.ObjectParams;
import pentair.model.ParamList;

public class SetParamListReq extends PentairRequest<ParamList> {

	
	public SetParamListReq() {
	}

	public SetParamListReq(ParamList... objectList) {
		super(objectList);
	}
	
	public SetParamListReq(ObjectParams... params) {
		this(toParamList(true, params));
	}


}
