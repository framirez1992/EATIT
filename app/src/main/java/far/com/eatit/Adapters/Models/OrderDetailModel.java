package far.com.eatit.Adapters.Models;

import java.util.ArrayList;

import far.com.eatit.Generic.Objects.KV;

public class OrderDetailModel {
    String codeProduct;
    String product_name;
    String quantity;
    String codeMeasure;
    String measureDescription;
    String code;
    String code_sales;
    boolean blocked;
    ArrayList<KV> measures;

    public OrderDetailModel(String codeProduct, String code, String code_sales, String name, String qty, String codemeasure, String measure,String blocked, ArrayList<KV> measures){
        this.code = code;
        this.code_sales = code_sales;
        this.codeProduct = codeProduct;
        this.product_name = name;
        this.quantity = qty;
        this.codeMeasure = codemeasure;
        this.measureDescription = measure;
        this.measures = measures;
        this.blocked = (blocked.equals("1"));
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCodeMeasure() {
        return codeMeasure;
    }

    public void setCodeMeasure(String codeMeasure) {
        this.codeMeasure = codeMeasure;
    }

    public String getMeasureDescription() {
        return measureDescription;
    }

    public void setMeasureDescription(String measureDescription) {
        this.measureDescription = measureDescription;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode_sales() {
        return code_sales;
    }

    public void setCode_sales(String code_sales) {
        this.code_sales = code_sales;
    }

    public ArrayList<KV> getMeasures() {
        return measures;
    }

    public void setMeasures(ArrayList<KV> measures) {
        this.measures = measures;
    }

    public String getCodeProduct() {
        return codeProduct;
    }

    public void setCodeProduct(String codeProduct) {
        this.codeProduct = codeProduct;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
