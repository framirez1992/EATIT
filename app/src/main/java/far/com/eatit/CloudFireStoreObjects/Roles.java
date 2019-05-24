package far.com.eatit.CloudFireStoreObjects;

import android.database.Cursor;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

import far.com.eatit.Controllers.RolesController;
import far.com.eatit.Utils.Funciones;

@IgnoreExtraProperties
public class Roles {
    private String CODE, DESCRIPTION;
    private @ServerTimestamp Date DATE, MDATE;
    public Roles(){

    }
    public Roles(String code, String description){
        this.CODE = code; this.DESCRIPTION = description;
    }
    public Roles(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(RolesController.CODE));
        this.DESCRIPTION = c.getString(c.getColumnIndex(RolesController.DESCRIPTION));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(RolesController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(RolesController.MDATE)));
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
