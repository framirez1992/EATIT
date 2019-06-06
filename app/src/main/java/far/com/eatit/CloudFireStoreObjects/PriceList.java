package far.com.eatit.CloudFireStoreObjects;

import android.database.Cursor;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

import far.com.eatit.Controllers.PriceListController;
import far.com.eatit.Utils.Funciones;

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

    public PriceList(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(PriceListController.CODE));
        this.CODEPRODUCT = c.getString(c.getColumnIndex(PriceListController.CODEPRODUCT));
        this.CODEUND = c.getString(c.getColumnIndex(PriceListController.CODEUND));
        this.PRICE = c.getDouble(c.getColumnIndex(PriceListController.PRICE));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(PriceListController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(PriceListController.MDATE)));
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
