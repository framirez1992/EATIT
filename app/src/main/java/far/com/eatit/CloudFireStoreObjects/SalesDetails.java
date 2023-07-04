package far.com.eatit.CloudFireStoreObjects;

import android.database.Cursor;

import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Utils.Funciones;

@IgnoreExtraProperties
public class SalesDetails {
    private String CODE,CODESALES, CODEPRODUCT,CODEUND;
    private int POSITION;
    private double QUANTITY, UNIT, PRICE, DISCOUNT;
    private @ServerTimestamp Date DATE, MDATE;

    public SalesDetails(){

    }
    public SalesDetails(String code,String codeSales, String codeProduct, String codeUnd,int position,double quantity, double unit,double price, double discount){
    this.CODE = code; this.CODESALES = codeSales; this.CODEPRODUCT = codeProduct; this.CODEUND = codeUnd;
    this.POSITION = position; this.QUANTITY = quantity; this.UNIT = unit;
    this.PRICE = price; this.DISCOUNT = discount;
    }

    public HashMap<String, Object> toMap(){

        HashMap<String, Object> map = new HashMap<>();
       /* map.put(SalesController.DETAIL_CODE, CODE);
        map.put(SalesController.DETAIL_CODESALES,CODESALES);
        map.put(SalesController.DETAIL_CODEPRODUCT, CODEPRODUCT);
        map.put(SalesController.DETAIL_CODEUND, CODEUND);
        map.put(SalesController.DETAIL_POSITION, POSITION);
        map.put(SalesController.DETAIL_QUANTITY, QUANTITY);
        map.put(SalesController.DETAIL_UNIT, UNIT);
        map.put(SalesController.DETAIL_PRICE, PRICE);
        map.put(SalesController.DETAIL_DISCOUNT, DISCOUNT);
        map.put(SalesController.DETAIL_DATE, (DATE == null)? FieldValue.serverTimestamp(): DATE);
        map.put(SalesController.DETAIL_MDATE, (MDATE == null)?FieldValue.serverTimestamp(): MDATE);*/
        return map;
    }

    public SalesDetails(Cursor c){
        /*this.CODE = c.getString(c.getColumnIndex(SalesController.DETAIL_CODE));
        this.CODESALES = c.getString(c.getColumnIndex(SalesController.DETAIL_CODESALES));
        this.CODEPRODUCT = c.getString(c.getColumnIndex(SalesController.DETAIL_CODEPRODUCT));
        this.CODEUND = c.getString(c.getColumnIndex(SalesController.DETAIL_CODEUND));
        this.POSITION = c.getInt(c.getColumnIndex(SalesController.DETAIL_POSITION));
        this.QUANTITY = c.getDouble(c.getColumnIndex(SalesController.DETAIL_QUANTITY));
        this.UNIT = c.getDouble(c.getColumnIndex(SalesController.DETAIL_UNIT));
        this.PRICE = c.getDouble(c.getColumnIndex(SalesController.DETAIL_PRICE));;
        this.DISCOUNT = c.getDouble(c.getColumnIndex(SalesController.DETAIL_DISCOUNT));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(SalesController.DETAIL_DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(SalesController.DETAIL_MDATE)));
        */

    }

    public SalesDetails(Map<String, Object> map){
        /*this.CODE = map.get(SalesController.DETAIL_CODE).toString();
        this.CODESALES = map.get(SalesController.DETAIL_CODESALES).toString();
        this.CODEPRODUCT = map.get(SalesController.DETAIL_CODEPRODUCT).toString();
        this.CODEUND = map.get(SalesController.DETAIL_CODEUND).toString();
        this.POSITION = Integer.parseInt(map.get(SalesController.DETAIL_POSITION).toString());
        this.QUANTITY = Double.parseDouble(map.get(SalesController.DETAIL_QUANTITY).toString());
        this.UNIT =  Double.parseDouble(map.get(SalesController.DETAIL_UNIT).toString());
        this.PRICE =  Double.parseDouble(map.get(SalesController.DETAIL_PRICE).toString());
        this.DISCOUNT =  Double.parseDouble(map.get(SalesController.DETAIL_DISCOUNT).toString());
        this.DATE = (Date)map.get(SalesController.DETAIL_DATE);
        this.MDATE = (Date)map.get(SalesController.DETAIL_MDATE);*/
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

    public int getPOSITION() {
        return POSITION;
    }

    public void setPOSITION(int POSITION) {
        this.POSITION = POSITION;
    }

    public double getQUANTITY() {
        return QUANTITY;
    }

    public void setQUANTITY(double QUANTITY) {
        this.QUANTITY = QUANTITY;
    }

    public double getUNIT() {
        return UNIT;
    }

    public void setUNIT(double UNIT) {
        this.UNIT = UNIT;
    }

    public double getPRICE() {
        return PRICE;
    }

    public void setPRICE(double PRICE) {
        this.PRICE = PRICE;
    }

    public double getDISCOUNT() {
        return DISCOUNT;
    }

    public void setDISCOUNT(double DISCOUNT) {
        this.DISCOUNT = DISCOUNT;
    }

    public Date getDATE() {
        return DATE;
    }

    public void setDATE(Date DATE) {
        this.DATE = DATE;
    }

    public Date getMDATE() {
        return MDATE;
    }

    public void setMDATE(Date MDATE) {
        this.MDATE = MDATE;
    }

    public String getCODESALES() {
        return CODESALES;
    }

    public void setCODESALES(String CODESALES) {
        this.CODESALES = CODESALES;
    }

}
