package far.com.eatit.CloudFireStoreObjects;

import android.database.Cursor;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

import far.com.eatit.Controllers.AreasDetailController;
import far.com.eatit.Utils.Funciones;

public class AreasDetail {
    private String CODE,CODEAREA, DESCRIPTION;
    private int ORDEN;
    private @ServerTimestamp Date DATE, MDATE;
    public AreasDetail(){

    }
    public AreasDetail(String code, String codeArea,String description, int order){
        this.CODE = code; this.CODEAREA = codeArea; this.DESCRIPTION = description; this.ORDEN = order;
    }
    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        /*map.put(AreasDetailController.CODE, CODE);
        map.put(AreasDetailController.CODEAREA, CODEAREA);
        map.put(AreasDetailController.DESCRIPTION, DESCRIPTION);
        map.put(AreasDetailController.ORDER, ORDEN);
        map.put(AreasDetailController.DATE, (DATE == null)? FieldValue.serverTimestamp():DATE);
        map.put(AreasDetailController.MDATE, (MDATE == null)? FieldValue.serverTimestamp():MDATE);*/

        return map;

    }
    public AreasDetail(Cursor c){
        /*his.CODE = c.getString(c.getColumnIndex(AreasDetailController.CODE));
        this.CODEAREA = c.getString(c.getColumnIndex(AreasDetailController.CODEAREA));
        this.DESCRIPTION = c.getString(c.getColumnIndex(AreasDetailController.DESCRIPTION));
        this.ORDEN = c.getInt(c.getColumnIndex(AreasDetailController.ORDER));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(AreasDetailController.DATE)));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(AreasDetailController.MDATE)));*/
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public int getORDEN() {
        return ORDEN;
    }

    public void setORDEN(int ORDEN) {
        this.ORDEN = ORDEN;
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

    public String getCODEAREA() {
        return CODEAREA;
    }

    public void setCODEAREA(String CODEAREA) {
        this.CODEAREA = CODEAREA;
    }
}
