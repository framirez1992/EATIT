package far.com.eatit.CloudFireStoreObjects;

import android.database.Cursor;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

import far.com.eatit.Controllers.TableFilterController;
import far.com.eatit.Utils.Funciones;

public class TableFilter {
    private String code, tables, user, usertype,producttype,productsubtype, task, filter, enabled;
    private @ServerTimestamp
    Date date, mdate;

    public TableFilter(){

    }
    public TableFilter(String code, String tables, String user, String usertype,String producttype, String productsubtype, String task, String filter, String enabled){
        this.code = code; this.tables = tables; this.user = user; this.usertype = usertype;
        this.producttype = producttype;this.productsubtype = productsubtype; this.task = task; this.filter = filter;
        this.enabled = enabled;
    }
    public TableFilter(Cursor c){
        this.code = c.getString(c.getColumnIndex(TableFilterController.CODE));
        this.tables = c.getString(c.getColumnIndex(TableFilterController.TABLES));
        this.user = c.getString(c.getColumnIndex(TableFilterController.USER));
        this.usertype = c.getString(c.getColumnIndex(TableFilterController.USERTYPE));
        this.producttype = c.getString(c.getColumnIndex(TableFilterController.PRODUCTTYPE));
        this.productsubtype = c.getString(c.getColumnIndex(TableFilterController.PRODUCTSUBTYPE));
        this.task = c.getString(c.getColumnIndex(TableFilterController.TASK));
        this.filter = c.getString(c.getColumnIndex(TableFilterController.FILTER));
        this.enabled = c.getString(c.getColumnIndex(TableFilterController.ENABLED));
        this.date = Funciones.parseStringToDate(c.getString(c.getColumnIndex(TableFilterController.DATE)));
        this.mdate = Funciones.parseStringToDate(c.getString(c.getColumnIndex(TableFilterController.MDATE)));
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(TableFilterController.CODE,code);
        map.put(TableFilterController.TABLES,tables);
        map.put(TableFilterController.USER,user);
        map.put(TableFilterController.USERTYPE,usertype);
        map.put(TableFilterController.PRODUCTTYPE, producttype);
        map.put(TableFilterController.PRODUCTSUBTYPE, productsubtype);
        map.put(TableFilterController.TASK,task);
        map.put(TableFilterController.FILTER,filter);
        map.put(TableFilterController.ENABLED,enabled);
        map.put(TableFilterController.DATE,(date == null)? FieldValue.serverTimestamp():date);
        map.put(TableFilterController.MDATE,(mdate == null)? FieldValue.serverTimestamp():mdate);

        return map;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTable() {
        return tables;
    }

    public void setTable(String table) {
        this.tables = table;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
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

    public String getTables() {
        return tables;
    }

    public void setTables(String tables) {
        this.tables = tables;
    }

    public String getProducttype() {
        return producttype;
    }

    public void setProducttype(String producttype) {
        this.producttype = producttype;
    }

    public String getProductsubtype() {
        return productsubtype;
    }

    public void setProductsubtype(String productsubtype) {
        this.productsubtype = productsubtype;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }
}
