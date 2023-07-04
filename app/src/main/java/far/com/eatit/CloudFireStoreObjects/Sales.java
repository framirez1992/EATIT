package far.com.eatit.CloudFireStoreObjects;

import android.database.Cursor;

import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.protobuf.CodedInputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

@IgnoreExtraProperties
public class Sales {
    private String CODE, NOTES, CODEUSER,CODEAREADETAIL, CODEREASON, REASONDESCRIPTION, CODEPRODUCTTYPE, CODEPRODUCTSUBTYPE, CODESALESORIGEN, CODERECEIPT;
    private int STATUS;
    private double TOTALDISCOUNT, TOTAL;
    private @ServerTimestamp Date DATE, MDATE;
    private List<HashMap<String, Object>> salesdetails;

    public Sales(){

    }

    public Sales(String code,String codeuser,String codeAreaDetail, double totalDiscount, double total, int status, String notes, String codeReason, String reasonDescription, String codeProductType, String codeProductSubType, String codeSalesOrigen, String codeReceipt){
        this.CODE = code; this.TOTALDISCOUNT = totalDiscount;this.TOTAL = total;this.STATUS = status;
        this.NOTES = notes;this.CODEUSER = codeuser; this.CODEREASON = codeReason; this.REASONDESCRIPTION = reasonDescription;
        this.CODEAREADETAIL = codeAreaDetail;this.CODEPRODUCTTYPE = codeProductType; this.CODEPRODUCTSUBTYPE = codeProductSubType;
        this.CODESALESORIGEN = codeSalesOrigen;this.CODERECEIPT = codeReceipt;
    }
    public Sales(Cursor c){
        /*this.CODE = c.getString(c.getColumnIndex(SalesController.CODE));
        this.TOTALDISCOUNT = c.getDouble(c.getColumnIndex(SalesController.TOTALDISCOUNT));
        this.TOTAL = c.getDouble(c.getColumnIndex(SalesController.TOTAL));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(SalesController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(SalesController.MDATE)));
        this.STATUS = c.getInt(c.getColumnIndex(SalesController.STATUS));
        this.NOTES = c.getString(c.getColumnIndex(SalesController.NOTES));
        this.CODEAREADETAIL = c.getString(c.getColumnIndex(SalesController.CODEAREADETAIL));
        this.CODEUSER = c.getString(c.getColumnIndex(SalesController.CODEUSER));
        this.CODEREASON = c.getString(c.getColumnIndex(SalesController.CODEREASON));
        this.REASONDESCRIPTION = c.getString(c.getColumnIndex(SalesController.REASONDESCRIPTION));
        this.CODEPRODUCTTYPE = c.getString(c.getColumnIndex(SalesController.CODEPRODUCTTYPE));
        this.CODEPRODUCTSUBTYPE = c.getString(c.getColumnIndex(SalesController.CODEPRODUCTSUBTYPE));
        this.CODESALESORIGEN = c.getString(c.getColumnIndex(SalesController.CODESALESORIGEN));
        this.CODERECEIPT = c.getString(c.getColumnIndex(SalesController.CODERECEIPT));*/
    }


    public HashMap<String, Object> toMap(){

        HashMap<String, Object> data = new HashMap<>();
       /* data.put(SalesController.CODE,CODE);
        data.put(SalesController.TOTALDISCOUNT,TOTALDISCOUNT);
        data.put(SalesController.TOTAL, TOTAL);
        data.put(SalesController.DATE, (DATE == null)? FieldValue.serverTimestamp(): DATE);
        data.put(SalesController.MDATE, (MDATE == null)? FieldValue.serverTimestamp():MDATE);
        data.put(SalesController.STATUS, STATUS);
        data.put(SalesController.NOTES, NOTES);
        data.put(SalesController.CODEUSER, CODEUSER);
        data.put(SalesController.CODEAREADETAIL, CODEAREADETAIL);
        data.put(SalesController.CODEREASON, CODEREASON);
        data.put(SalesController.REASONDESCRIPTION, REASONDESCRIPTION);
        data.put(SalesController.CODEPRODUCTTYPE, CODEPRODUCTTYPE);
        data.put(SalesController.CODEPRODUCTSUBTYPE, CODEPRODUCTSUBTYPE);
        data.put(SalesController.CODESALESORIGEN, CODESALESORIGEN);
        data.put(SalesController.CODERECEIPT, CODERECEIPT);
        if(salesdetails != null){
        data.put("salesdetails", salesdetails);
        }*/

        return  data;

    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public double getTOTALDISCOUNT() {
        return TOTALDISCOUNT;
    }

    public void setTOTALDISCOUNT(double TOTALDISCOUNT) {
        this.TOTALDISCOUNT = TOTALDISCOUNT;
    }

    public double getTOTAL() {
        return TOTAL;
    }

    public void setTOTAL(double TOTAL) {
        this.TOTAL = TOTAL;
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

    public String getNOTES() {
        return NOTES;
    }

    public void setNOTES(String NOTES) {
        this.NOTES = NOTES;
    }

    public int getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(int STATUS) {
        this.STATUS = STATUS;
    }

    public String getCODEUSER() {
        return CODEUSER;
    }

    public void setCODEUSER(String CODEUSER) {
        this.CODEUSER = CODEUSER;
    }

    public String getCODEREASON() {
        return CODEREASON;
    }

    public void setCODEREASON(String CODEREASON) {
        this.CODEREASON = CODEREASON;
    }

    public String getREASONDESCRIPTION() {
        return REASONDESCRIPTION;
    }

    public void setREASONDESCRIPTION(String REASONDESCRIPTION) {
        this.REASONDESCRIPTION = REASONDESCRIPTION;
    }

    public String getCODEAREADETAIL() {
        return CODEAREADETAIL;
    }

    public void setCODEAREADETAIL(String CODEAREADETAIL) {
        this.CODEAREADETAIL = CODEAREADETAIL;
    }

    public String getCODEPRODUCTTYPE() {
        return CODEPRODUCTTYPE;
    }

    public void setCODEPRODUCTTYPE(String CODEPRODUCTTYPE) {
        this.CODEPRODUCTTYPE = CODEPRODUCTTYPE;
    }

    public String getCODEPRODUCTSUBTYPE() {
        return CODEPRODUCTSUBTYPE;
    }

    public void setCODEPRODUCTSUBTYPE(String CODEPRODUCTSUBTYPE) {
        this.CODEPRODUCTSUBTYPE = CODEPRODUCTSUBTYPE;
    }

    public String getCODESALESORIGEN() {
        return CODESALESORIGEN;
    }

    public void setCODESALESORIGEN(String CODESALESORIGEN) {
        this.CODESALESORIGEN = CODESALESORIGEN;
    }

    public String getCODERECEIPT() {
        return CODERECEIPT;
    }

    public void setCODERECEIPT(String CODERECEIPT) {
        this.CODERECEIPT = CODERECEIPT;
    }

    public void setDetails(ArrayList<SalesDetails> details){
        ArrayList<HashMap<String, Object>> maps = new ArrayList<>();
        for(SalesDetails sd: details){
            maps.add(sd.toMap());
        }
        this.salesdetails = maps;
    }
}
