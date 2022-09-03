package pentair.model.OBJTYPS;

import pentair.model.ObjectParams;

public class CIRCUIT extends ObjectParams {

	public CIRCUIT(String OBJNAM, String SUBTYP) {
		super(OBJNAM, CIRCUIT.class.getSimpleName(), SUBTYP);
	}
	
	public String STATUS;
	
}
