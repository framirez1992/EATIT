package far.com.eatit.API.models;

public class ResponseBase {
    String resposeCode;
    String resposeMessage;
    Object data;

    public  ResponseBase(){

    }

    public String getResposeCode() {
        return resposeCode;
    }

    public void setResposeCode(String resposeCode) {
        this.resposeCode = resposeCode;
    }

    public String getResposeMessage() {
        return resposeMessage;
    }

    public void setResposeMessage(String resposeMessage) {
        this.resposeMessage = resposeMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
