package far.com.eatit.API.models;

public class LoginResponse {
    String resposeCode;
    String resposeMessage;

    public License license;
    public UserDevice userDevice;
    public UserRole userRole;
    public String[] modules;

    public  LoginResponse(){

    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public UserDevice getUserDevice() {
        return userDevice;
    }

    public void setUserDevice(UserDevice userDevice) {
        this.userDevice = userDevice;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public String[] getModules() {
        return modules;
    }

    public void setModules(String[] modules) {
        this.modules = modules;
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
}
