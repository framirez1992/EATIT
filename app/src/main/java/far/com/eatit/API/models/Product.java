package far.com.eatit.API.models;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;

public class Product {
    public int id;
    public int idLicense;
    public int idproductType;
    public int idproductSubType;
    public String code;
    public String description;
    public boolean combo;
    public String productTypeDescription;
    public String productSubTypeDescription;
    public String createDate;
    public String createUser;
    public String updateDate;
    public String updateUser;
    public String deleteDate;
    public String deleteUser;

    ArrayList<ProductMeasure> productMeasures;
    public Product(){

    }

    public Product(int idLicense, int idproductType, int idproductSubType, String code, String description, boolean combo) {
        this.idLicense = idLicense;
        this.idproductType = idproductType;
        this.idproductSubType = idproductSubType;
        this.code = code;
        this.description = description;
        this.combo = combo;
    }

    public Product(Cursor c){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdLicense() {
        return idLicense;
    }

    public void setIdLicense(int idLicense) {
        this.idLicense = idLicense;
    }

    public int getIdproductType() {
        return idproductType;
    }

    public void setIdproductType(int idproductType) {
        this.idproductType = idproductType;
    }

    public int getIdproductSubType() {
        return idproductSubType;
    }

    public void setIdproductSubType(int idproductSubType) {
        this.idproductSubType = idproductSubType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCombo() {
        return combo;
    }

    public void setCombo(boolean combo) {
        this.combo = combo;
    }

    public String getProductTypeDescription() {
        return productTypeDescription;
    }

    public void setProductTypeDescription(String productTypeDescription) {
        this.productTypeDescription = productTypeDescription;
    }

    public String getProductSubTypeDescription() {
        return productSubTypeDescription;
    }

    public void setProductSubTypeDescription(String productSubTypeDescription) {
        this.productSubTypeDescription = productSubTypeDescription;
    }

    public ArrayList<ProductMeasure> getProductMeasures() {
        return productMeasures;
    }

    public void setProductMeasures(ArrayList<ProductMeasure> productMeasures) {
        this.productMeasures = productMeasures;
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
