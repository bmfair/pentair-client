package pentair.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import pentair.model.SUBTYPS.ICHEM;
import pentair.model.SUBTYPS.INTELLI;
import pentair.model.SUBTYPS.POOL;
import pentair.model.SUBTYPS.VSF;
import pentair.model.messages.RequestParamList;

import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "SUBTYP")
@JsonSubTypes({ @Type(name = "ICHEM", value = ICHEM.class), @Type(name = "INTELLI", value = INTELLI.class),
		@Type(name = "POOL", value = POOL.class),
		@Type(name = "VSF", value = VSF.class)})
public abstract class ObjectParams {

	public static ObjectMapper KEY_MAPPER = new ObjectMapper().setSerializationInclusion(Include.ALWAYS);
	private static Map<String, String[]> TYPE_KEYS = new HashMap<>();

	// These are not for JSON, but for keeping self reference
	private String objName;
	private String objType;
	private String subType;
	private KeyList keys;

	// These are for JSON
	public String OBJNAM;
	public String OBJTYP;
	public String SUBTYP;

	public ObjectParams() {
	};

	protected ObjectParams(String OBJNAM, String OBJTYP, String SUBTYP) {
		this.OBJNAM = OBJNAM;
		this.OBJTYP = OBJTYP;
		this.SUBTYP = SUBTYP;
		this.objName = OBJNAM;
		this.objType = OBJTYP;
		this.subType = SUBTYP;
		this.keys = new KeyList(OBJNAM, TYPE_KEYS.computeIfAbsent(getClass().getName(), k -> buildKeys()));
	}

	private String[] buildKeys() {
		Set<String> kset = new HashSet<>();
//		kset.add("OBJNAM");
//		kset.add("OBJTYP");
//		kset.add("SUBTYP");

		JsonNode root;
		try {
			root = KEY_MAPPER.readTree(KEY_MAPPER.writeValueAsString(this));
			ObjectNode objectNode = (ObjectNode) root;
			objectNode.fieldNames().forEachRemaining(s -> kset.add(s));
			System.out.println("Key Fields for " + this.SUBTYP + ": " + kset);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Should not have crashed...", e);
		}
		return kset.toArray(new String[0]);
	}

	public String getOBJNAM() {
		return OBJNAM;
	}

	@JsonIgnore
	public String getObjName() {
		return this.objName;
	}

	@JsonProperty("OBJNAM") // Adds deserialization
	private void setOBJNAM(String OBJNAM) {
		this.OBJNAM = OBJNAM;
		this.objName = OBJNAM;
	}

	public String getOBJTYP() {
		return OBJTYP;
	}

	@JsonIgnore
	public String getObjType() {
		return this.objType;
	}

	@JsonProperty("OBJTYP") // Adds deserialization
	private void setOBJTYP(String OBJTYP) {
		this.OBJTYP = OBJTYP;
		this.objType = OBJTYP;
	}

	public String getSUBTYP() {
		return SUBTYP;
	}

	@JsonIgnore
	public String getSubType() {
		return this.subType;
	}

	@JsonProperty("SUBTYP") // Adds deserialization
	private void setSUBTYP(String SUBTYP) {
		this.SUBTYP = SUBTYP;
		this.subType = SUBTYP;
	}

	@JsonIgnore
	public KeyList getKeyObject() {
		return this.keys;
	}

	@JsonIgnore
	public RequestParamList getRequestKey() {
		return new RequestParamList(this.getKeyObject());
	}

	private void clearReadOnly() {
		this.OBJNAM = null;
		this.OBJTYP = null;
		this.SUBTYP = null;
	}

	@JsonIgnore
	public ParamList toParamList(boolean isRequest) {
		if (isRequest) {
			clearReadOnly(); // Wipe out fields we don't want to serialize
		}
		return new ParamList(getObjName(), this);
	}

//	@JsonIgnore
//	public SetParamList getRequestSet() {
//		return new SetParamList(toParamList());
//	}
//	
//	@JsonIgnore
//	public SetParamList getNotifyList() {
//		return new SetParamList(toParamList(false));
//	}

	@Override
	public String toString() {
		try {
			return KEY_MAPPER.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "Error serializing self";
		}
	}

}
