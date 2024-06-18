package pentair.pentair_client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import pentair.map.MapObj;
import pentair.model.Keys;
import pentair.model.NamedObjects;

public class Test {

	public static void main(String[] args) throws UnknownHostException, IOException {

//		PentairClient c = new PentairClient();

//		AnyMessage msg = new AnyMessage();
//		msg.command = Commands.GetParamList;
//		msg.add("keys", KeyList.KEYS);

//		GetParamList msg = new GetParamList();
//		msg.command = "RequestParamList";
		//requestKeys(msg.objectList, NamedObjects.B1101);
		//requestKeys(msg.objectList, NamedObjects.B1202);
//		requestKeys(msg.objectList, NamedObjects.C0001);
		//requestKeys(msg.objectList, NamedObjects.PMP01);
		// requestKeys(msg.objectList, NamedObjects.B1101);
//		 requestKeys(msg.objectList, NamedObjects.CHM01);
//		requestKeys(msg.objectList, NamedObjects._A135);
//		requestKeys(msg.objectList, NamedObjects._C10C);
//		requestKeys(msg.objectList, NamedObjects._C105);

//		m.objnam = "ALL";
//		m.keys.add("OBJNAM");
//		m.keys.add("OBJTYP");
//		m.keys.add("SUBTYP");
//		m.keys.add("SNAME");

//		GetQuery msg = new GetQuery();
//		msg.queryName = QueryNames.GetParamList;
//		msg.arguments.add("{\"OBJTYP\" : \"CHM01}\"");

//		RequestParamList msg = new RequestParamList(NamedObjects.CHM01.getParams(), NamedObjects.PMP01.getParams());
//		
//		NotifyList rsp = c.sendReq(msg, NotifyList.class);
//
//		c.getMapper().print(rsp);
//
//		c.close();

	}

	public static void requestKeys(List<MapObj> req, NamedObjects... desired) {
		for (NamedObjects d : desired) {
			MapObj m = new MapObj();
			m.objnam = d.name();
			Collections.addAll(m.keys, Keys.values());
			req.add(m);
		}
	}

}
