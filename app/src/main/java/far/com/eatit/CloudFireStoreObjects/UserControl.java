package far.com.eatit.CloudFireStoreObjects;

import android.database.Cursor;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

import far.com.eatit.Controllers.UserControlController;

public class UserControl {
    private String CODE, CODEUSER, CODEUSERTYPE,  CONTROL,VALUE;
    private @ServerTimestamp
    Date DATE, MDATE;

    public UserControl(){

    }
    public UserControl(String code, String codeUser, String codeUserType, String control, String value){
        this.CODE = code; this.CODEUSER = codeUser;this.CODEUSERTYPE = codeUserType;
        this.CONTROL = control; this.VALUE = value;
    }

    public UserControl(Cursor c){

    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(UserControlController.CODE,CODE);
        map.put(UserControlController.CODEUSER,CODEUSER);
        map.put(UserControlController.CODEUSERTYPE,CODEUSERTYPE);
        map.put(UserControlController.CONTROL,CONTROL);
        map.put(UserControlController.VALUE,VALUE);
        map.put(UserControlController.DATE,(DATE == null)? FieldValue.serverTimestamp():DATE);
        map.put(UserControlController.MDATE,(MDATE == null)? FieldValue.serverTimestamp():MDATE);
        return map;

    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getCODEUSER() {
        return CODEUSER;
    }

    public void setCODEUSER(String CODEUSER) {
        this.CODEUSER = CODEUSER;
    }

    public String getCODEUSERTYPE() {
        return CODEUSERTYPE;
    }

    public void setCODEUSERTYPE(String CODEUSERTYPE) {
        this.CODEUSERTYPE = CODEUSERTYPE;
    }

    public String getCONTROL() {
        return CONTROL;
    }

    public void setCONTROL(String CONTROL) {
        this.CONTROL = CONTROL;
    }

    public String getVALUE() {
        return VALUE;
    }

    public void setVALUE(String VALUE) {
        this.VALUE = VALUE;
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
