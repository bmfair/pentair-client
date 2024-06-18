package pentair.model.SUBTYPS;

import pentair.model.OBJTYPS.CHEM;

public class ICHEM extends CHEM {

//	{"ALK":"80","CALC":"300","COMUART":"1","CYACID":"0","FLOWDLY":"OFF","HNAME":"CHM01","LISTORD":"1",
//		"NOFLO":"OFF",

//		"OBJNAM":"CHM01","OBJTYP":"CHEM",
//		
//		"ORPCHK":"OFF","ORPFED":"ON","ORPHI":"ON","ORPLO":"OFF","ORPMOD":"ON","ORPSET":"650","ORPTIM":"ORPTIM",
//		"ORPTNK":"5","ORPTYP":"ON","ORPVAL":"805","ORPVOL":"7424",
//		
//		"PHCHK":"OFF","PHFED":"ON","PHHI":"OFF","PHLO":"OFF","PHMOD":"ON","PHPRIOR":"OFF","PHSET":"7.4","PHTIM":"PHTIM",
//		"PHTNK":"4","PHTYP":"ON","PHVAL":"7.30","PHVOL":"4352",
//		
//		"PROBE":"OFF","QUALTY":"-0.67","SALT":"0","SHARE":"B1101","SINDEX":"1.89","SNAME":"IntelliChem 1","STATIC":"OFF","SUBTYP":"ICHEM"}

//	@JsonSerialize(using = StringToBooleanSerializer.class)
//	@JsonDeserialize(using = StringToBooleanDeserializer.class)

	public Integer ALK; // 80
	public Integer CALC; // 300
	public Integer CYACID; // 0

	// public Integer COMUART; // 1
	// public Integer LISTORD; // 1;
	// public String HNAME; // CHM01;
	// public String SNAME; //IntelliChem 1
	public String SHARE; // B1101

	public String FLOWDLY; // OFF
	public String NOFLO; // OFF

	public String ORPCHK; // OFF
	public String ORPFED; // ON
	public String ORPHI; // ON
	public String ORPLO; // OFF
	// public String ORPMOD; //ON //Broken?
	public Integer ORPSET; // 750
	public Integer ORPTNK; // 5
	public String ORPTYP; // ON

	/**
	 * Current ORP value <br/>
	 * Example: 731
	 */
	public Double ORPVAL; 

	/**
	 * Current dosing amount <br/>
	 * Example: 7424
	 */
	public Integer ORPVOL;

	public String PHCHK;
	public String PHFED;// ON
	public String PHHI; // OFF
	public String PHLO; // OFF
	// public String PHMOD; //ON
	public String PHPRIOR; // OFF
	public Double PHSET; // 7.4
	public Integer PHTNK; // 5
	public String PHTYP; // ON

	public Double PHVAL; // 7.30
	public Integer PHVOL; // 4352

	public String PROBE;

	// public String CHLOR; // Likely IntelliChlor Salt Gen
	// public String SALT;

	/**
	 * Saturation Index
	 */
	public Double SINDEX;
	public Double QUALTY;

	public ICHEM() {
		super("", ICHEM.class.getSimpleName());
	}

	public ICHEM(String OBJNAM) {
		super(OBJNAM, ICHEM.class.getSimpleName());
	}

}
