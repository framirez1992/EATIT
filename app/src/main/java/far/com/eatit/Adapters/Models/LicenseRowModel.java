package far.com.eatit.Adapters.Models;

import java.util.Date;

import far.com.eatit.API.models.License;
//import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.Utils.Funciones;

public class LicenseRowModel {
    String code, clientName;
    boolean enabled;
    License license;


    public LicenseRowModel(License l){
        this.code = l.getCode();
        this.clientName = l.getClientName();
        this.enabled = l.isEnabled();
        this.license = l;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
