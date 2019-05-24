package far.com.eatit.CloudFireStoreObjects;

import android.database.Cursor;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

import far.com.eatit.Controllers.AreasController;
import far.com.eatit.Utils.Funciones;

public class Areas {
    private String CODE, DESCRIPTION;
    private int ORDEN;
    private @ServerTimestamp Date DATE, MDATE;

    public Areas(){

    }
    public Areas(String code, String description, int order){
        this.CODE = code; this.DESCRIPTION = description; this.ORDEN = order;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(AreasController.CODE, CODE);
        map.put(AreasController.DESCRIPTION, DESCRIPTION);
        map.put(AreasController.ORDER, ORDEN);
        map.put(AreasController.DATE, (DATE == null)? FieldValue.serverTimestamp():DATE);
        map.put(AreasController.MDATE,  (MDATE == null)? FieldValue.serverTimestamp():MDATE);
        return map;

    }
    public Areas(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(AreasController.CODE));
        this.DESCRIPTION = c.getString(c.getColumnIndex(AreasController.DESCRIPTION));
        this.ORDEN = c.getInt(c.getColumnIndex(AreasController.ORDER));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(AreasController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(AreasController.MDATE)));
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
}
