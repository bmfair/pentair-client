package pentair.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class KeyList extends PentairObject {

	// Likely special keys: "RTHIS"

//	public static final String[] KEYS = { "ABSMAX", "ABSMAXNC", "ABSMIN", "ABSMINNC", "ABSTIM", "ACCEL", "ACT1", "ACT2",
//			"ACT3", "ACT4", "ADDRESS", "ADJ", "ALARM", "ALK", "ALPHA", "AMERCA", "AMPM", "ANLGEXP", "AQUA", "ASSIGN",
//			"AUTO", "AVAIL", "BADGE", "BAKFLO", "BAKTIM", "BASIC", "BATT", "BCOUNT", "BLUE", "BLUER", "BOOST", "BYPASS",
//			"CALC", "CALIB", "CARIB", "CHILD", "CITY", "CLEAN", "CLK24A", "CLRCASC", "CNFG", "COLOR", "COLORWL",
//			"COMERR", "COMETHR", "COMI2C", "COMLNK", "COMSPI", "COMUART", "COMUSB", "CONCUR", "COOL", "COOLING",
//			"COUNTRY", "COVER", "CURRENT", "CUSTOM", "CUTOFF", "CYACID", "CYCTIM", "DAY", "DBGMSG1", "DBGMSG2",
//			"DBGMSG3", "DECR", "DEVCONN", "DEVICE", "DFGATE", "DFLT", "DHCP", "DIMMER", "DISPLYJPG", "DLSTIM",
//			"DLYCNCL", "DLYOFF", "DLYON", "DNSSERV", "DOSE", "DRAIN", "DRVALM", "DRVWRN", "DUAL", "EMAIL", "EMAIL2",
//			"EMPTY", "ENABLE", "END", "ENGLISH", "EXTINSTR", "FAULT", "FD485TEST", "FDCONTACT", "FDDISPLAY", "FDFWVER",
//			"FDRELAY", "FDTEMP", "FDVALVE", "FEATR", "FILTER", "FLOOR", "FLOW", "FLOWDLY", "FREEZE", "FREQ", "GAL",
//			"GENERIC", "GLOW", "GLOWT", "GPM", "GPMNC", "GREEN", "GREENR", "GROUP", "HCOMBO", "HEATING", "HITMP",
//			"HITMPNC", "HNAME", "HOLD", "HR24", "HTMODE", "HTPMP", "HTSRC", "HTSRCTYP", "I10D", "I10P", "I10PS", "I10X",
//			"I5P", "I5PS", "I5X", "I8P", "I8PS", "ICHLOR", "ICP", "INCR", "INTAKE", "INTELLI", "INVALID", "IPADY",
//			"IS10", "IS4", "IS5", "LASTHTR", "LED", "LEFT", "LEGACY", "LIGHT", "LIMIT", "LISTORD", "LITGRN", "LITRED",
//			"LITSHO", "LIVLST", "LOCAL", "LOCKOUT", "LOCX", "LOCY", "LOG", "LOGO", "LOTMP", "LOTMPNC", "LPM", "LSTART",
//			"LSTOP", "LSTTMP", "MACADY", "MAGIC1", "MAGIC2", "MAGICMOD", "MAGNTA", "MAGNTAR", "MANHT", "MANOVR",
//			"MANUAL", "MASKSM", "MASTER", "MAXF", "MAXFNC", "MECH", "MENU", "METRIC", "MINF", "MINFNC", "MIX", "MNFDAT",
//			"MODULE", "MONITOR", "NATUAL", "NETWRK", "NITE", "NOFLO", "NONE", "NORMAL", "OBJNAM", "OBJREV", "OBJTYP",
//			"OCP", "OFFSET", "ORP", "ORPCHK", "ORPFED", "ORPHI", "ORPLIM", "ORPLO", "ORPMOD", "ORPSET", "ORPTIM",
//			"ORPTNK", "ORPTYP", "ORPVAL", "ORPVOL", "OVRFRZ", "OVROFF", "OVRON", "PANID", "PARENT", "PARTY", "PASSWRD",
//			"PERMIT", "PHCHK", "PHFED", "PHHI", "PHLIM", "PHLO", "PHMOD", "PHONE", "PHONE2", "PHOTON", "PHPRIOR",
//			"PHSET", "PHTIM", "PHTNK", "PHTYP", "PHVAL", "PHVOL", "PMPCIRC", "POOL", "POOLFRZ", "POSIT", "PRESS",
//			"PRIM", "PRIMFLO", "PRIMTIM", "PROBE", "PROBENC", "PROGRES", "PROPLST", "PROPNAME", "PWR", "QUALTY",
//			"QUEUE", "RADIO", "RANGE", "REBOOT", "RECALL", "RECENT", "REDR", "REMBTN", "RESET", "RETURN", "RIGHT",
//			"RLY", "RNSTIM", "ROMAN", "ROTATE", "ROYAL", "RPM", "RUNON", "SALT", "SALTLO", "SAML", "SAMMOD", "SAVLST",
//			"SCHED", "SEC", "SENSE", "SERNUM", "SESSION", "SETADV", "SETBASE", "SETPT", "SETTMP", "SETTMPNC", "SHARE",
//			"SHOMNU", "SHUTDOWN", "SINDEX", "SINGLE", "SMART", "SMTSRT", "SNAME", "SOLAR", "SOURCE", "SPA", "SPACMD",
//			"SPEED", "SPIID", "SPILL", "SRIS", "SSET", "STAMSG", "STATE", "STATIC", "SUBNET", "SUBTYP", "SUM", "SUPER",
//			"SUPORT", "SWIM", "SYNC", "SYSTEM", "SYSTIM", "TEMPNC", "THUMP", "TIME", "TIMOUT", "TIMZON", "TOUCHTEST",
//			"TURNS", "TXRPC", "TXRREM", "ULTRA", "UNITS", "UPDATE", "URL", "USE", "VACFLO", "VACTIM", "VALVEXP", "VCAP",
//			"VERYLO", "VLVCIRC", "VOLMOVD", "VOLNC", "VOLT", "VSF", "WAIT", "WAPNET", "WAPPW", "WCP", "WHITE", "WHITER",
//			"XCP", "ZIP", "ZZ_ALL",
//
//			// Not in the list, adding manually:
//			"TEMP" };

	public Set<Keys> keys = new HashSet<>();

	public KeyList() {
	}

	public KeyList(String objnam, String... keys) {
		super(objnam);
		if (keys != null) {
			for (String k : keys) {
				this.keys.add(Keys.valueOf(k));
			}
		}

	}

	public KeyList(NamedObjects objnam, Keys... keys) {
		super(objnam.name());
		if (keys != null)
			Collections.addAll(this.keys, keys);
	}

}
