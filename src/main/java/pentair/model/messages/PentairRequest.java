package pentair.model.messages;

import java.util.List;

import pentair.model.PentairObject;

public class PentairRequest<E extends PentairObject> extends PentairMessage<E> {
	
	public PentairRequest() {
	}
	public PentairRequest(List<E> objects) {
		super(objects);
	}

	public PentairRequest(E[] objects) {
		super(objects);
	}

}
