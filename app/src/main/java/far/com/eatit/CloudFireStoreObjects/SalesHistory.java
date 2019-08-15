package far.com.eatit.CloudFireStoreObjects;

import android.database.Cursor;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import far.com.eatit.Controllers.SalesHistoryController;
import far.com.eatit.Utils.Funciones;

public class SalesHistory {
    private String CODE, NOTES, CODEUSER,USERNAME, CODEAREADETAIL,AREADETAILDESCRIPTION, CODEREASON, REASONDESCRIPTION, CODEPRODUCTTYPE,PRODUCTTYPEDESCRIPTION, 
            CODEPRODUCTSUBTYPE,PRODUCTSUBTYPEDESCRIPTION, CODESALESORIGEN, CODERECEIPT;
    private int STATUS;
    private double TOTALDISCOUNT, TOTAL;
    private @ServerTimestamp
    Date DATE, MDATE;
    private List<HashMap<String, Object>> salesdetails;

    public SalesHistory(){

    }

    public SalesHistory(String code,String codeuser,String userName, String codeAreaDetail,String areaDetailDescription, double totalDiscount, double total, 
                        int status, String notes, String codeReason, String reasonDescription, String codeProductType,String productTypeDescription, 
                        String codeProductSubType,String productSubTypeDescription, String codeSalesOrigen, String codeReceipt){
        this.CODE = code; this.TOTALDISCOUNT = totalDiscount;this.TOTAL = total;this.STATUS = status;
        this.NOTES = notes;this.CODEUSER = codeuser;this.USERNAME = userName; this.CODEREASON = codeReason; this.REASONDESCRIPTION = reasonDescription;
        this.CODEAREADETAIL = codeAreaDetail;this.AREADETAILDESCRIPTION = areaDetailDescription; this.CODEPRODUCTTYPE = codeProductType; 
        this.PRODUCTTYPEDESCRIPTION =productTypeDescription; this.CODEPRODUCTSUBTYPE = codeProductSubType;this.PRODUCTSUBTYPEDESCRIPTION = productSubTypeDescription;
        this.CODESALESORIGEN = codeSalesOrigen;this.CODERECEIPT = codeReceipt;
    }
    public SalesHistory(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(SalesHistoryController.CODE));
        this.TOTALDISCOUNT = c.getDouble(c.getColumnIndex(SalesHistoryController.TOTALDISCOUNT));
        this.TOTAL = c.getDouble(c.getColumnIndex(SalesHistoryController.TOTAL));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(SalesHistoryController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(SalesHistoryController.MDATE)));
        this.STATUS = c.getInt(c.getColumnIndex(SalesHistoryController.STATUS));
        this.NOTES = c.getString(c.getColumnIndex(SalesHistoryController.NOTES));
        this.CODEAREADETAIL = c.getString(c.getColumnIndex(SalesHistoryController.CODEAREADETAIL));
        this.AREADETAILDESCRIPTION = c.getString(c.getColumnIndex(SalesHistoryController.AREADETAILDESCRIPTION));
        this.CODEUSER = c.getString(c.getColumnIndex(SalesHistoryController.CODEUSER));
        this.USERNAME = c.getString(c.getColumnIndex(SalesHistoryController.USERNAME));
        this.CODEREASON = c.getString(c.getColumnIndex(SalesHistoryController.CODEREASON));
        this.REASONDESCRIPTION = c.getString(c.getColumnIndex(SalesHistoryController.REASONDESCRIPTION));
        this.CODEPRODUCTTYPE = c.getString(c.getColumnIndex(SalesHistoryController.CODEPRODUCTTYPE));
        this.PRODUCTTYPEDESCRIPTION = c.getString(c.getColumnIndex(SalesHistoryController.PRODUCTTYPEDESCRIPTION));
        this.CODEPRODUCTSUBTYPE = c.getString(c.getColumnIndex(SalesHistoryController.CODEPRODUCTSUBTYPE));
        this.PRODUCTSUBTYPEDESCRIPTION = c.getString(c.getColumnIndex(SalesHistoryController.PRODUCTSUBTYPEDESCRIPTION));
        this.CODESALESORIGEN = c.getString(c.getColumnIndex(SalesHistoryController.CODESALESORIGEN));
        this.CODERECEIPT = c.getString(c.getColumnIndex(SalesHistoryController.CODERECEIPT));
    }


    public HashMap<String, Object> toMap(){

        HashMap<String, Object> data = new HashMap<>();
        data.put(SalesHistoryController.CODE,CODE);
        data.put(SalesHistoryController.TOTALDISCOUNT,TOTALDISCOUNT);
        data.put(SalesHistoryController.TOTAL, TOTAL);
        data.put(SalesHistoryController.DATE, (DATE == null)? FieldValue.serverTimestamp(): DATE);
        data.put(SalesHistoryController.MDATE, (MDATE == null)? FieldValue.serverTimestamp():MDATE);
        data.put(SalesHistoryController.STATUS, STATUS);
        data.put(SalesHistoryController.NOTES, NOTES);
        data.put(SalesHistoryController.CODEUSER, CODEUSER);
        data.put(SalesHistoryController.USERNAME, USERNAME);
        data.put(SalesHistoryController.CODEAREADETAIL, CODEAREADETAIL);
        data.put(SalesHistoryController.AREADETAILDESCRIPTION, AREADETAILDESCRIPTION);
        data.put(SalesHistoryController.CODEREASON, CODEREASON);
        data.put(SalesHistoryController.REASONDESCRIPTION, REASONDESCRIPTION);
        data.put(SalesHistoryController.CODEPRODUCTTYPE, CODEPRODUCTTYPE);
        data.put(SalesHistoryController.PRODUCTTYPEDESCRIPTION, PRODUCTTYPEDESCRIPTION);
        data.put(SalesHistoryController.CODEPRODUCTSUBTYPE, CODEPRODUCTSUBTYPE);
        data.put(SalesHistoryController.PRODUCTSUBTYPEDESCRIPTION, PRODUCTSUBTYPEDESCRIPTION);
        data.put(SalesHistoryController.CODESALESORIGEN, CODESALESORIGEN);
        data.put(SalesHistoryController.CODERECEIPT, CODERECEIPT);
        if(salesdetails != null){
            data.put("salesdetails", salesdetails);
        }

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

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getAREADETAILDESCRIPTION() {
        return AREADETAILDESCRIPTION;
    }

    public void setAREADETAILDESCRIPTION(String AREADETAILDESCRIPTION) {
        this.AREADETAILDESCRIPTION = AREADETAILDESCRIPTION;
    }

    public String getPRODUCTTYPEDESCRIPTION() {
        return PRODUCTTYPEDESCRIPTION;
    }

    public void setPRODUCTTYPEDESCRIPTION(String PRODUCTTYPEDESCRIPTION) {
        this.PRODUCTTYPEDESCRIPTION = PRODUCTTYPEDESCRIPTION;
    }

    public String getPRODUCTSUBTYPEDESCRIPTION() {
        return PRODUCTSUBTYPEDESCRIPTION;
    }

    public void setPRODUCTSUBTYPEDESCRIPTION(String PRODUCTSUBTYPEDESCRIPTION) {
        this.PRODUCTSUBTYPEDESCRIPTION = PRODUCTSUBTYPEDESCRIPTION;
    }

    public void setDetails(ArrayList<SalesDetailsHistory> details){
        ArrayList<HashMap<String, Object>> maps = new ArrayList<>();
        for(SalesDetailsHistory sd: details){
            maps.add(sd.toMap());
        }
        this.salesdetails = maps;
    }
}
