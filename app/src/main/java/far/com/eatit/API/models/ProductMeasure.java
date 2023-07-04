package far.com.eatit.API.models;

import android.database.Cursor;

import java.util.Date;

public class ProductMeasure {
    public int id;
    public int idproduct;
    public int idmeasureUnit;
    public double price;
    public double maxPrice;
    public double minPrice;
    public boolean enabled;
    public boolean range;
    public String createDate;
    public String createUser;
    public String updateDate;
    public String updateUser;
    public String deleteDate;
    public String deleteUser;

    public ProductMeasure(){

    }

    public ProductMeasure(int id, int idproduct, int idmeasureUnit, double price, double maxPrice, double minPrice, boolean enabled, boolean range) {
        this.id = id;
        this.idproduct = idproduct;
        this.idmeasureUnit = idmeasureUnit;
        this.price = price;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.enabled = enabled;
        this.range = range;
    }

    public ProductMeasure(int idproduct, int idmeasureUnit, double price, double maxPrice, double minPrice, boolean enabled, boolean range) {
        this.idproduct = idproduct;
        this.idmeasureUnit = idmeasureUnit;
        this.price = price;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.enabled = enabled;
        this.range = range;
    }

    public ProductMeasure(Cursor c){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdproduct() {
        return idproduct;
    }

    public void setIdproduct(int idproduct) {
        this.idproduct = idproduct;
    }

    public int getIdmeasureUnit() {
        return idmeasureUnit;
    }

    public void setIdmeasureUnit(int idmeasureUnit) {
        this.idmeasureUnit = idmeasureUnit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isRange() {
        return range;
    }

    public void setRange(boolean range) {
        this.range = range;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(String deleteDate) {
        this.deleteDate = deleteDate;
    }

    public String getDeleteUser() {
        return deleteUser;
    }

    public void setDeleteUser(String deleteUser) {
        this.deleteUser = deleteUser;
    }
}
