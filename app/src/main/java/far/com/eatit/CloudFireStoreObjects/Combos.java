package far.com.eatit.CloudFireStoreObjects;

import android.database.Cursor;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

import far.com.eatit.Controllers.CombosController;
import far.com.eatit.Utils.Funciones;

@IgnoreExtraProperties
public class Combos {
    private String CODE, CODEPRODUCTCOMBO, CODEPRODUCT,CODEUNDPRODUCT;
    private Object DATE, MDATE;
    public Combos(){

    }
    public Combos(String code, String codeProductCombo, String codeProduct, String codeUnd, Object date, Object mdate){
        this.CODE = code; this.CODEPRODUCTCOMBO = codeProductCombo;
        this.CODEPRODUCT = codeProduct; this.CODEUNDPRODUCT =codeUnd;
        this.DATE = date; this.MDATE = mdate;
    }

    public Combos(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(CombosController.CODE));
        this.CODEPRODUCTCOMBO = c.getString(c.getColumnIndex(CombosController.CODEPRODUCTCOMBO));
        this.CODEPRODUCT =c.getString(c.getColumnIndex(CombosController.CODEPRODUCT));
        this.CODEUNDPRODUCT =c.getString(c.getColumnIndex(CombosController.CODEUNDPRODUCT));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(CombosController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(CombosController.MDATE)));
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getCODEPRODUCTCOMBO() {
        return CODEPRODUCTCOMBO;
    }

    public void setCODEPRODUCTCOMBO(String CODEPRODUCTCOMBO) {
        this.CODEPRODUCTCOMBO = CODEPRODUCTCOMBO;
    }

    public String getCODEPRODUCT() {
        return CODEPRODUCT;
    }

    public void setCODEPRODUCT(String CODEPRODUCT) {
        this.CODEPRODUCT = CODEPRODUCT;
    }

    public String getCODEUNDPRODUCT() {
        return CODEUNDPRODUCT;
    }

    public void setCODEUNDPRODUCT(String CODEUNDPRODUCT) {
        this.CODEUNDPRODUCT = CODEUNDPRODUCT;
    }

    public Object getDATE() {
        return DATE;
    }

    public void setDATE(Object DATE) {
        this.DATE = DATE;
    }

    public Object getMDATE() {
        return MDATE;
    }

    public void setMDATE(Object MDATE) {
        this.MDATE = MDATE;
    }
}
