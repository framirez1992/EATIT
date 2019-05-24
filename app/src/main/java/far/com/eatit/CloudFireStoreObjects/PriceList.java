package far.com.eatit.CloudFireStoreObjects;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class PriceList {
    private String CODE, CODEPRODUCT, CODEUND;
    private double PRICE;
    private Object DATE, MDATE;

    public  PriceList(){

    }
    public PriceList(String code, String codeProduct, String codeUnd, double price, Object date, Object mdate){
        this.CODE = code; this.CODEPRODUCT = codeProduct; this.CODEUND = codeUnd;
        this.PRICE = price; this.DATE = date; this.MDATE = mdate;
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getCODEPRODUCT() {
        return CODEPRODUCT;
    }

    public void setCODEPRODUCT(String CODEPRODUCT) {
        this.CODEPRODUCT = CODEPRODUCT;
    }

    public String getCODEUND() {
        return CODEUND;
    }

    public void setCODEUND(String CODEUND) {
        this.CODEUND = CODEUND;
    }

    public double getPRICE() {
        return PRICE;
    }

    public void setPRICE(double PRICE) {
        this.PRICE = PRICE;
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
