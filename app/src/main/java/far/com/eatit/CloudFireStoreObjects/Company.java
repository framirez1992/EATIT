package far.com.eatit.CloudFireStoreObjects;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Company {
    private String CODE,NAME,RNC,ADDRESS,ADDRESS2,PHONE, PHONE2;
    private Object DATE, MDATE;
    public Company(){

    }
    public Company(String code, String name, String rnc, String address, String address2, String phone, String phone2, Object date, Object mdate){
        this.CODE = code; this.NAME = name; this.RNC = rnc;
        this.ADDRESS = address; this.ADDRESS2 = address2; this.PHONE = phone;
        this.PHONE2 = phone2; this.DATE = date; this.MDATE = mdate;
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getRNC() {
        return RNC;
    }

    public void setRNC(String RNC) {
        this.RNC = RNC;
    }

    public String getADDRESS() {
        return ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }

    public String getADDRESS2() {
        return ADDRESS2;
    }

    public void setADDRESS2(String ADDRESS2) {
        this.ADDRESS2 = ADDRESS2;
    }

    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
    }

    public String getPHONE2() {
        return PHONE2;
    }

    public void setPHONE2(String PHONE2) {
        this.PHONE2 = PHONE2;
    }

    public Object getDATE() {
        return DATE;
    }

    public void setDATE(Object DATE) {
        this.DATE = DATE;
    }

    public Object getMDATE() {
        return MDATE;
    }

    public void setMDATE(Object MDATE) {
        this.MDATE = MDATE;
    }
}
