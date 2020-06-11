package far.com.eatit.CloudFireStoreObjects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Token implements Serializable {
    private String code;
    private String type;
    private String extradata;
    private boolean autodelete;

    public Token(){

    }
    public Token(String code, boolean autodelete){
        this.code = code;
        this.autodelete = autodelete;
    }

    public Token(String code,String type, String extradata, boolean autodelete){
        this.code = code;
        this.type = type;
        this.extradata = extradata;
        this.autodelete = autodelete;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isAutodelete() {
        return autodelete;
    }

    public void setAutodelete(boolean autodelete) {
        this.autodelete = autodelete;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExtradata() {
        return extradata;
    }

    public void setExtradata(String extradata) {
        this.extradata = extradata;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("type", type);
        map.put("extradata", extradata);
        map.put("autodelete", autodelete);
        return map;
    }
}
