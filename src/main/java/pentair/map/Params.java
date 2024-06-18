package pentair.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class Params {

//	public Integer RPM;
//	public Integer GPM;
//	public Integer ORPVAL;
	
	public List<MapObj> OBJLIST;
	public List<MapObj> CIRCUITS;
	
	private Map<String, String> properties = new HashMap<>();

//	public String OBJTYP;
	
    @JsonAnyGetter
    public Map<String, String> getProperties() {
        return properties;
    }
    
    @JsonAnySetter
    public void add(String key, String value) {
        properties.put(key, value);
    }
	
	
}
