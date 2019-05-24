package far.com.eatit.CloudFireStoreObjects;

import android.database.Cursor;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

import far.com.eatit.Controllers.UserInboxController;
import far.com.eatit.Utils.Funciones;

public class UserInbox {
    private String CODE, CODESENDER, CODEUSER, CODEMESSAGE,SUBJECT, DESCRIPTION, TYPE, CODEICON;
    private int STATUS;
    private @ServerTimestamp Date DATE, MDATE;


    public UserInbox(){

    }
    public UserInbox(String code,String codeSender, String codeUser, String codeMessage,String subject, String description, String type,String codeIcon, int status){
        this.CODE = code; this.CODESENDER = codeSender; this.CODEUSER = codeUser; this.CODEMESSAGE = codeMessage;this.SUBJECT = subject;
        this.DESCRIPTION =description; this.TYPE = type; this.STATUS = status;this.CODEICON = codeIcon;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(UserInboxController.CODE, CODE);
        map.put(UserInboxController.CODESENDER, CODESENDER);
        map.put(UserInboxController.CODEUSER, CODEUSER);
        map.put(UserInboxController.SUBJECT, SUBJECT);
        map.put(UserInboxController.CODEMESSAGE, CODEMESSAGE);
        map.put(UserInboxController.DESCRIPTION, DESCRIPTION);
        map.put(UserInboxController.TYPE, TYPE);
        map.put(UserInboxController.STATUS, STATUS);
        map.put(UserInboxController.CODEICON, CODEICON);
        map.put(UserInboxController.DATE, (DATE == null)? FieldValue.serverTimestamp(): DATE);
        map.put(UserInboxController.MDATE, (MDATE == null)?FieldValue.serverTimestamp(): MDATE);

        return map;
    }

    public UserInbox(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(UserInboxController.CODE));
        this.CODESENDER = c.getString(c.getColumnIndex(UserInboxController.CODESENDER));
        this.CODEUSER = c.getString(c.getColumnIndex(UserInboxController.CODEUSER));
        this.SUBJECT = c.getString(c.getColumnIndex(UserInboxController.SUBJECT));
        this.CODEMESSAGE = c.getString(c.getColumnIndex(UserInboxController.CODEMESSAGE));
        this.DESCRIPTION = c.getString(c.getColumnIndex(UserInboxController.DESCRIPTION));
        this.TYPE = c.getString(c.getColumnIndex(UserInboxController.TYPE));
        this.STATUS = c.getInt(c.getColumnIndex(UserInboxController.STATUS));
        this.CODEICON = c.getString(c.getColumnIndex(UserInboxController.CODEICON));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(UserInboxController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(UserInboxController.MDATE)));
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

    public String getCODEMESSAGE() {
        return CODEMESSAGE;
    }

    public void setCODEMESSAGE(String CODEMESSAGE) {
        this.CODEMESSAGE = CODEMESSAGE;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public int getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(int STATUS) {
        this.STATUS = STATUS;
    }

    public String getCODESENDER() {
        return CODESENDER;
    }

    public void setCODESENDER(String CODESENDER) {
        this.CODESENDER = CODESENDER;
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

    public String getSUBJECT() {
        return SUBJECT;
    }

    public void setSUBJECT(String SUBJECT) {
        this.SUBJECT = SUBJECT;
    }

    public String getCODEICON() {
        return CODEICON;
    }

    public void setCODEICON(String CODEICON) {
        this.CODEICON = CODEICON;
    }
}


