package pentair.model;



public class ParamList extends PentairObject {

	public ObjectParams params;
	
	public ParamList() {};
	
	public ParamList(String objnam) {
		super(objnam);
	}
	public ParamList(String objnam, ObjectParams params) {
		super(objnam);
		this.params = params;
	}

	
	
}
