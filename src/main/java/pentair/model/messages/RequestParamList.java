package pentair.model.messages;

import java.util.List;

import pentair.model.KeyList;
import pentair.model.ObjectParams;

public class RequestParamList extends PentairRequest<KeyList> {

	public RequestParamList() {
	}

	public RequestParamList(List<KeyList> keyList) {
		super(keyList);
	}
	
	public RequestParamList(KeyList... objectList) {
		super(objectList);
	}

	public RequestParamList(ObjectParams... objectList) {
		this(toKeyList(objectList));
	}



}
