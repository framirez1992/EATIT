package far.com.eatit.Adapters.Models;

import android.database.Cursor;

import java.util.Date;

import far.com.eatit.Utils.Funciones;

public class ReceiptSavedModel {
    String code,status, codeUser,userName, ncf, codeArea, areaDescription, codeAreaDetail, areaDetailDescription;
    double subTotal, taxes, discount, total;
    Date date, mdate;

    public ReceiptSavedModel(Cursor c){
        this.code = c.getString(c.getColumnIndex("CODE"));
        this.status = c.getString(c.getColumnIndex("STATUS"));
        this.codeUser = c.getString(c.getColumnIndex("CODEUSER"));
        this.userName = c.getString(c.getColumnIndex("USERNAME"));
        this.ncf = c.getString(c.getColumnIndex("NCF"));
        this.codeArea = c.getString(c.getColumnIndex("CODEAREA"));
        this.areaDescription = c.getString(c.getColumnIndex("AREADESCRIPTION"));
        this.codeAreaDetail = c.getString(c.getColumnIndex("CODEAREADETAIL"));
        this.areaDetailDescription = c.getString(c.getColumnIndex("AREADETAILDESCRIPTION"));
        this.subTotal = c.getDouble(c.getColumnIndex("SUBTOTAL"));
        this.taxes = c.getDouble(c.getColumnIndex("TAXES"));
        this.discount = c.getDouble(c.getColumnIndex("DISCOUNT"));
        this.total = c.getDouble(c.getColumnIndex("TOTAL"));
        this.date = Funciones.parseStringToDate(c.getString(c.getColumnIndex("DATE")));
        this.mdate = Funciones.parseStringToDate(c.getString(c.getColumnIndex("MDATE")));
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeUser() {
        return codeUser;
    }

    public void setCodeUser(String codeUser) {
        this.codeUser = codeUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        userName = userName;
    }

    public String getNcf() {
        return ncf;
    }

    public void setNcf(String ncf) {
        this.ncf = ncf;
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

    public String getDate() {
        return Funciones.getFormatedDateRepDomHour(date);
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

    public String getCodeArea() {
        return codeArea;
    }

    public void setCodeArea(String codeArea) {
        this.codeArea = codeArea;
    }

    public String getAreaDescription() {
        return areaDescription;
    }

    public void setAreaDescription(String areaDescription) {
        this.areaDescription = areaDescription;
    }

    public String getCodeAreaDetail() {
        return codeAreaDetail;
    }

    public void setCodeAreaDetail(String codeAreaDetail) {
        this.codeAreaDetail = codeAreaDetail;
    }

    public String getAreaDetailDescription() {
        return areaDetailDescription;
    }

    public void setAreaDetailDescription(String areaDetailDescription) {
        this.areaDetailDescription = areaDetailDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
