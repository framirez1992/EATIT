package far.com.eatit.CloudFireStoreObjects;

import android.database.Cursor;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

import far.com.eatit.Controllers.TableCodeController;
import far.com.eatit.Utils.Funciones;

public class TableCode {
    private String CODE, CODETYPE, CODECONTROL, DESCRIPTION;
    private @ServerTimestamp Date  DATE, MDATE;

    public TableCode(){

    }

    public TableCode(String code, String codeType, String codeControl, String description){
        this.CODE = code; this.CODETYPE = codeType; this.CODECONTROL = codeControl; this.DESCRIPTION = description;
    }
    public TableCode(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(TableCodeController.CODE));
        this.CODETYPE = c.getString(c.getColumnIndex(TableCodeController.CODETYPE));
        this.CODECONTROL = c.getString(c.getColumnIndex(TableCodeController.CODECONTROL));
        this.DESCRIPTION = c.getString(c.getColumnIndex(TableCodeController.DESCRIPTION));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(TableCodeController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(TableCodeController.CODE)));
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(TableCodeController.CODE, CODE);
        map.put(TableCodeController.CODETYPE, CODETYPE);
        map.put(TableCodeController.CODECONTROL, CODECONTROL);
        map.put(TableCodeController.DESCRIPTION, DESCRIPTION);
        map.put(TableCodeController.DATE, (DATE == null)?FieldValue.serverTimestamp():DATE);
        map.put(TableCodeController.MDATE, (MDATE == null)?FieldValue.serverTimestamp():MDATE);

        return map;
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getCODETYPE() {
        return CODETYPE;
    }

    public void setCODETYPE(String CODETYPE) {
        this.CODETYPE = CODETYPE;
    }

    public String getCODECONTROL() {
        return CODECONTROL;
    }

    public void setCODECONTROL(String CODECONTROL) {
        this.CODECONTROL = CODECONTROL;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
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
