package far.com.eatit.CloudFireStoreObjects;

import android.database.Cursor;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

import far.com.eatit.Controllers.StoreHouseController;
import far.com.eatit.Utils.Funciones;

public class StoreHouse {
    private String CODE, DESCRIPTION;
    private @ServerTimestamp
    Date DATE, MDATE;
    public StoreHouse(){

    }
    public StoreHouse(String code, String description){
        this.CODE = code; this.DESCRIPTION = description;
    }
    public StoreHouse(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(StoreHouseController.CODE));
        this.DESCRIPTION = c.getString(c.getColumnIndex(StoreHouseController.DESCRIPTION));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(StoreHouseController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(StoreHouseController.MDATE)));
    }


    public HashMap<String, Object> toMap(){

        HashMap<String, Object> data = new HashMap<>();
        data.put(StoreHouseController.CODE,CODE);
        data.put(StoreHouseController.DESCRIPTION,DESCRIPTION);
        data.put(StoreHouseController.DATE, (DATE == null)? FieldValue.serverTimestamp(): DATE);
        data.put(StoreHouseController.MDATE, (MDATE == null)? FieldValue.serverTimestamp():MDATE);

        return  data;

    }
    public String getCODE() {
        return CODE;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
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
