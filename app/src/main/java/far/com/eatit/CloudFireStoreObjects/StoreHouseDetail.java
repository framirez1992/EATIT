package far.com.eatit.CloudFireStoreObjects;

import android.database.Cursor;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

import far.com.eatit.Controllers.StoreHouseDetailController;
import far.com.eatit.Utils.Funciones;

public class StoreHouseDetail {
    private String CODE, CODESTOREHOUSE, CODEPRODUCT, CODEMEASURE;
    private double QUANTITY;
    private @ServerTimestamp
    Date DATE, MDATE;
    public StoreHouseDetail(){

    }
    public StoreHouseDetail(String code, String codeStoreHouse, String codeProduct, String codeMeasure,double quantity){
        this.CODE = code; this.CODESTOREHOUSE = codeStoreHouse; this.CODEPRODUCT = codeProduct; this.CODEMEASURE = codeMeasure;
        this.QUANTITY = quantity;
    }
    public StoreHouseDetail(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(StoreHouseDetailController.CODE));
        this.CODESTOREHOUSE = c.getString(c.getColumnIndex(StoreHouseDetailController.CODESTOREHOUSE));
        this.CODEPRODUCT = c.getString(c.getColumnIndex(StoreHouseDetailController.CODEPRODUCT));
        this.CODEMEASURE = c.getString(c.getColumnIndex(StoreHouseDetailController.CODEMEASURE));
        this.QUANTITY = c.getDouble(c.getColumnIndex(StoreHouseDetailController.QUANTITY));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(StoreHouseDetailController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(StoreHouseDetailController.MDATE)));
    }


    public HashMap<String, Object> toMap(){

        HashMap<String, Object> data = new HashMap<>();
        data.put(StoreHouseDetailController.CODE,CODE);
        data.put(StoreHouseDetailController.CODESTOREHOUSE,CODESTOREHOUSE);
        data.put(StoreHouseDetailController.CODEPRODUCT,CODEPRODUCT);
        data.put(StoreHouseDetailController.CODEMEASURE,CODEMEASURE);
        data.put(StoreHouseDetailController.QUANTITY,QUANTITY);
        data.put(StoreHouseDetailController.DATE, (DATE == null)? FieldValue.serverTimestamp(): DATE);
        data.put(StoreHouseDetailController.MDATE, (MDATE == null)? FieldValue.serverTimestamp():MDATE);

        return  data;

    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getCODESTOREHOUSE() {
        return CODESTOREHOUSE;
    }

    public void setCODESTOREHOUSE(String CODESTOREHOUSE) {
        this.CODESTOREHOUSE = CODESTOREHOUSE;
    }

    public String getCODEPRODUCT() {
        return CODEPRODUCT;
    }

    public void setCODEPRODUCT(String CODEPRODUCT) {
        this.CODEPRODUCT = CODEPRODUCT;
    }

    public String getCODEMEASURE() {
        return CODEMEASURE;
    }

    public void setCODEMEASURE(String CODEMEASURE) {
        this.CODEMEASURE = CODEMEASURE;
    }

    public double getQUANTITY() {
        return QUANTITY;
    }

    public void setQUANTITY(double QUANTITY) {
        this.QUANTITY = QUANTITY;
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
}
