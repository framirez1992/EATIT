package far.com.eatit.Adapters.Models;

public class ReceiptResumeModel {
    String codeAreaDetail, codeProduct, productDescription, codeMeasure, measureDescription, quantity, total;

    public ReceiptResumeModel(String codeAreaDetail, String codeProduct, String productDescription, String codeMeasure, String measureDescription, String quantity, String total){
        this.codeAreaDetail = codeAreaDetail;
        this.codeProduct = codeProduct;
        this.productDescription = productDescription;
        this.codeMeasure = codeMeasure;
        this.measureDescription = measureDescription;
        this.quantity = quantity;
        this.total = total;
    }


    public String getCodeAreaDetail() {
        return codeAreaDetail;
    }

    public void setCodeAreaDetail(String codeAreaDetail) {
        this.codeAreaDetail = codeAreaDetail;
    }

    public String getCodeProduct() {
        return codeProduct;
    }

    public void setCodeProduct(String codeProduct) {
        this.codeProduct = codeProduct;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
