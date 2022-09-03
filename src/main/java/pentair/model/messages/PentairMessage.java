package pentair.model.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.core.JsonProcessingException;

import pentair.model.KeyList;
import pentair.model.ObjectParams;
import pentair.model.ParamList;
import pentair.model.PentairObject;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "command")
@JsonSubTypes({ @Type(name = "RequestParamList", value = RequestParamList.class),
		@Type(name = "NotifyList", value = PentairResponse.class),
		@Type(name = "SetParamList", value = SetParamListReq.class) })
public abstract class PentairMessage<E extends PentairObject> {

	// public String command; //RequestParamList, NotifyList
	public String messageID = UUID.randomUUID().toString(); // UUID: 1038386f-8683-426b-a1a1-81e6bc6f7ff5

	public List<E> objectList;

	public PentairMessage() {
	};

	public PentairMessage(List<E> objects) {
		this.objectList = new ArrayList<>();
		this.objectList.addAll(objects);
	}

	public PentairMessage(E[] objects) {
		this.objectList = new ArrayList<>();
		Collections.addAll(this.objectList, objects);
	}
	
	@Override
	public String toString() {
		try {
			return ObjectParams.KEY_MAPPER.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "Error serializing self";
		}
	}

	public static KeyList[] toKeyList(ObjectParams... params) {
		KeyList[] l = new KeyList[params.length];
		for (int i = 0; i < params.length; i++) {
			l[i] = params[i].getKeyObject();
		}
		return l;
	}
	
	public static ParamList[] toParamList(boolean isRequest, ObjectParams... params) {
		ParamList[] l = new ParamList[params.length];
		for (int i = 0; i < params.length; i++) {
			l[i] = params[i].toParamList(isRequest);
		}
		return l;
	}
	
}
