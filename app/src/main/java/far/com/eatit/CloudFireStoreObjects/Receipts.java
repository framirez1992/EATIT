package far.com.eatit.CloudFireStoreObjects;

import android.database.Cursor;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

import far.com.eatit.Controllers.ReceiptController;
import far.com.eatit.Utils.Funciones;

public class Receipts {
    String code,ncf, codeuser, codeareadetail, status;
    double subTotal, taxes, discount, total;
    private @ServerTimestamp
    Date date, mdate;

    public Receipts(){

    }
    public Receipts(String code,String codeUser,String codeAreaDetail,String status,  String ncf, double subTotal, double taxes, double discount, double total){
        this.code = code;
        this.codeuser = codeUser;
        this.codeareadetail = codeAreaDetail;
        this.status = status;
        this.ncf = ncf;
        this.subTotal = subTotal;
        this.taxes = taxes;
        this.discount = discount;
        this.total = total;
    }

    public Receipts(Cursor c){
        this.code = c.getString(c.getColumnIndex(ReceiptController.CODE));
        this.codeuser =c.getString(c.getColumnIndex(ReceiptController.CODEUSER));
        this.codeareadetail = c.getString(c.getColumnIndex(ReceiptController.CODEAREADETAIL));
        this.status = c.getString(c.getColumnIndex(ReceiptController.STATUS));
        this.ncf = c.getString(c.getColumnIndex(ReceiptController.NCF));
        this.subTotal = c.getDouble(c.getColumnIndex(ReceiptController.SUBTOTAL));
        this.taxes = c.getDouble(c.getColumnIndex(ReceiptController.TAXES));
        this.discount = c.getDouble(c.getColumnIndex(ReceiptController.DISCOUNT));
        this.date = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ReceiptController.DATE)));
        this.mdate = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ReceiptController.MDATE)));
    }


    public HashMap<String, Object> toMap(){

        HashMap<String, Object> data = new HashMap<>();
        data.put(ReceiptController.CODE,code);
        data.put(ReceiptController.CODEUSER, codeuser);
        data.put(ReceiptController.CODEAREADETAIL, codeareadetail);
        data.put(ReceiptController.STATUS, status);
        data.put(ReceiptController.NCF,ncf);
        data.put(ReceiptController.SUBTOTAL, subTotal);
        data.put(ReceiptController.TAXES, taxes);
        data.put(ReceiptController.DISCOUNT, discount);
        data.put(ReceiptController.TOTAL, total);
        data.put(ReceiptController.DATE, (date == null)? FieldValue.serverTimestamp(): date);
        data.put(ReceiptController.MDATE, (mdate == null)? FieldValue.serverTimestamp():mdate);


        return  data;

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNcf() {
        return ncf;
    }

    public void setNcf(String ncf) {
        this.ncf = ncf;
    }

    public String getCodeuser() {
        return codeuser;
    }

    public void setCodeuser(String codeuser) {
        this.codeuser = codeuser;
    }

    public String getCodeareadetail() {
        return codeareadetail;
    }

    public void setCodeareadetail(String codeareadetail) {
        this.codeareadetail = codeareadetail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getTaxes() {
        return taxes;
    }

    public void setTaxes(double taxes) {
        this.taxes = taxes;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }
}
