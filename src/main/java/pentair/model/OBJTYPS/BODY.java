package pentair.model.OBJTYPS;

import pentair.model.ObjectParams;

public class BODY extends ObjectParams {

	public Double TEMP;
	
	
	public BODY(String OBJNAM, String SUBTYP) {
		super(OBJNAM, BODY.class.getSimpleName(), SUBTYP);
	}
	
}
