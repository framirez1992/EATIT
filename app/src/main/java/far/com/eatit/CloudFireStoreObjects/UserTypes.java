package far.com.eatit.CloudFireStoreObjects;

import android.database.Cursor;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.HashMap;

import far.com.eatit.Controllers.UserTypesController;
import far.com.eatit.Utils.Funciones;

public class UserTypes {
    private String CODE, DESCRIPTION;
    private int ORDEN;
    private @ServerTimestamp Date DATE, MDATE;
    private DocumentReference documentReference;

    public UserTypes(){

    }
    public UserTypes(String code, String description, int order){
        this.CODE = code; this.DESCRIPTION = description; this.ORDEN = order;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(UserTypesController.CODE, CODE);
        map.put(UserTypesController.DESCRIPTION, DESCRIPTION);
        map.put(UserTypesController.ORDEN, ORDEN);
        map.put(UserTypesController.DATE, (DATE == null)? FieldValue.serverTimestamp(): DATE);
        map.put(UserTypesController.MDATE, (MDATE == null)? FieldValue.serverTimestamp(): MDATE);

        return map;
    }
    public UserTypes(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(UserTypesController.CODE));
        this.DESCRIPTION = c.getString(c.getColumnIndex(UserTypesController.DESCRIPTION));
        this.ORDEN = c.getInt(c.getColumnIndex(UserTypesController.ORDEN));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(UserTypesController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(UserTypesController.MDATE)));
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

    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(DocumentReference documentReference) {
        this.documentReference = documentReference;
    }
}
