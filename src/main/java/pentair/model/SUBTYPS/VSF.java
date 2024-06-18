package pentair.model.SUBTYPS;

import pentair.model.OBJTYPS.PUMP;

public class VSF extends PUMP {

//	"params" : {
//	      "SETTMPNC" : "1",
//	      "ABSMAX" : "1340",
//	      "MAXF" : "140",
//	      "PRIM" : "OFF",
//	      "SETTMP" : "100",
//	      "SUBTYP" : "VSF",
//	      "SYSTIM" : "5",
//	      "GPM" : "0",
//	      "ALARM" : "OFF",
//	      "LISTORD" : "1",
//	      "OBJTYP" : "PUMP",
//	      "HNAME" : "PMP01",
//	      "SNAME" : "VSF",
//	      "VOLT" : "OFF",
//	      "PWR" : "0",
//	      "PRIMFLO" : "2500",
//	      "CURRENT" : "OFF",
//	      "RPM" : "0",
//	      "STATIC" : "OFF",
//	      "MINF" : "20",
//	      "OBJNAM" : "PMP01",
//	      "ABSMIN" : "134",
//	      "COMUART" : "1",
//	      "PRIMTIM" : "1"
//	    }
	
	public VSF(String OBJNAM) {
		super(OBJNAM, VSF.class.getSimpleName());
	}
	
	public VSF() {
		super("", VSF.class.getSimpleName());
	}
	
	public String STATUS; //":"4"
	public Integer GPM; 
	public Integer PWR; 
	public Integer RPM;
	
}
