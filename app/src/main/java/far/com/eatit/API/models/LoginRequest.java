package far.com.eatit.API.models;

public class LoginRequest {
    String DeviceCode;
    String UserCode;
    String UserPassword;

    public  LoginRequest(){

    }

    public LoginRequest(String deviceCode, String userCode, String userPassword) {
        DeviceCode = deviceCode;
        UserCode = userCode;
        UserPassword = userPassword;
    }

    public String getDeviceCode() {
        return DeviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        DeviceCode = deviceCode;
    }

    public String getUserCode() {
        return UserCode;
    }

    public void setUserCode(String userCode) {
        UserCode = userCode;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String userPassword) {
        UserPassword = userPassword;
    }
}
