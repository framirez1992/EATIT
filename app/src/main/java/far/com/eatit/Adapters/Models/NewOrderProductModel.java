package far.com.eatit.Adapters.Models;

import java.util.ArrayList;

import far.com.eatit.Generic.Objects.KV;

public class NewOrderProductModel {
    String codeOrderDetail;
    String codeProduct;
    String name;
    String quantity;
    String measure;
    boolean blocked;
    double price;
    ArrayList<KV> measures;

    public NewOrderProductModel(String codeOrderDetail, String codeProduct, String name,double price, String quantity, String measure,String bloqued, ArrayList<KV> measures){
        this.codeOrderDetail = codeOrderDetail; this.codeProduct = codeProduct;
        this.name = name;this.price = price; this.quantity = quantity; this.measure = measure;
        this.measures = measures;this.blocked = (bloqued.equals("1"));
    }

    public String getCodeOrderDetail() {
        return codeOrderDetail;
    }

    public void setCodeOrderDetail(String codeOrderDetail) {
        this.codeOrderDetail = codeOrderDetail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
