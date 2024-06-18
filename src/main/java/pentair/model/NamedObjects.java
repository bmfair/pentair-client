package pentair.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import pentair.map.MapObj;
import pentair.model.messages.NotifyList;

public enum NamedObjects {

//	B1101("Pool Body", POOL.class), B1202("Spa Body", SPA.class), C0001("Spa Circuit"),
//	C0002("Spillway", GENERIC.class), C0003("Intelli Light", INTELLI.class), C0006("Pool Circuit"),
//	CHM01("IntelliChem", ICHEM.class), FTR01("Floor Cleaner"), H0001("Solar Heater"), H0002("Gas Heater"),
//	HXSLR("Solar Pref"), PMP01("VSF Pump", VSF.class), SSS11("Solar Sensor"), VAL01("Valve A"), VAL02("Valve B"),
//	VAL03("Valve Intake"), VAL04("Valve Return"), _A135("Air Sensor"), SSW11("Water Sensor"), _C105("System Clock"), _C10C("System Clock 2"),
//	p0101("Pump Preset 1");

	B1101("Pool Body", Keys.STATUS), B1202("Spa Body", Keys.STATUS), C0001("Spa Circuit", Keys.STATUS),
	C0002("Spillway", Keys.STATUS), C0003("Intelli Light", Keys.STATUS), C0006("Pool Circuit", Keys.STATUS),
	CHM01("IntelliChem"), FTR01("Floor Cleaner"), H0001("Solar Heater"), H0002("Gas Heater"),
	HXSLR("Solar Pref"), PMP01("VSF Pump"), SSS11("Solar Sensor"), VAL01("Valve A"), VAL02("Valve B"),
	VAL03("Valve Intake"), VAL04("Valve Return"), _A135("Air Sensor"), SSW11("Water Sensor"), _C105("System Clock"), _C10C("System Clock 2"),
	p0101("Pump Preset 1");
	
//	private final String desc;
	private final Set<Keys> keys = new HashSet<Keys>();
	//private final ObjectParams params;

	private NamedObjects(String desc, Keys...keys) {
//		this.desc = desc;
		this.keys.addAll(Arrays.asList(keys));
	}

//	private <T extends ObjectParams> NamedObjects(String desc, Class<T> objType) {
//		this.desc = desc == null ? this.name() : desc;
//		if (objType != null)
//			try {
//				params = objType.getConstructor(String.class).newInstance(this.name());
//			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
//					| InvocationTargetException | NoSuchMethodException e) {
//				e.printStackTrace();
//				throw new IllegalArgumentException("Invalid class type: " + e.getMessage());
//			}
//		else {
//			params = null;
//		}
//	}
//
//	public ObjectParams getParams() {
//		return this.params;
//	}
//	
//	public <T extends ObjectParams> T getParams(Class<T> type) {
//		return type.cast(this.params);
//	}
	
	public MapObj getMap(NotifyList response) {
		for (MapObj m : response.objectList) {
			if (this.name().equals(m.objnam))
				return m;
		}
		return null;
	}

	
//	public static ICHEM CHM01 = CHM01();
//	
//	public static VSF PMP01 = PMP01();
//
//	public static ICHEM CHM01() {
//		return new ICHEM("CHM01");
//	}
//
//	public static VSF PMP01() {
//		return new VSF("PMP01");
//	}
//	
//	public static POOL B1101() {
//		return new POOL("B1101");
//	}
//
//	public static SPA B1202() {
//		return new SPA("B1202");
//	}
//
//	// C0001
//	// {"objnam":"C0001","params":{"OBJNAM":"C0001","OBJTYP":"CIRCUIT","SUBTYP":"SPA","SNAME":"Spa"}}
//
//	/**
//	 * Spillway Circuit
//	 * {"objnam":"C0002","params":{"OBJNAM":"C0002","OBJTYP":"CIRCUIT","SUBTYP":"GENERIC","SNAME":"Spillway"}}
//	 * @return
//	 */
//	public static GENERIC C0002() {
//		return new GENERIC("C0002");
//	}
//
//	/**
//	 * Intellilights
//	 * {"objnam":"C0003","params":{"OBJNAM":"C0003","OBJTYP":"CIRCUIT","SUBTYP":"INTELLI","SNAME":"Pool Light"}}
//	 * 
//	 * @return
//	 */
//	public static INTELLI C0003() {
//		return new INTELLI("C0003");
//	}

//
//	#SSS11 Probe
//
//	#_5451 {  "SERVICE": "AUTO", "FRZ": "0" } #this is the system's ID
//	#_A135 { PROBE: 57 } Air Temp
//	#_C10C Clock
//	#X0052 Solar
//	#PMP01 Main pump { GPM, PWR }
//
//	#C*** are circuits
//	#C0002 spillway
//	#FTR01 in floor cleaner (not a real circuit; I think it's "Feature01")
//	
}
