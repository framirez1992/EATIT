package far.com.eatit.Adapters.Models;

import far.com.eatit.API.models.Product;

public class ProductRowModel {
    String code, description, codeType,codeTypeDesc, codeSubType, codeSubTypeDesc;
    boolean inServer;
    Product product;

    public ProductRowModel(String code, String description, String codeType, String codeTypeDesc, String codeSubType, String codeSubTypeDesc, boolean inServer){
        this.code = code;
        this.description = description;
        this.codeType = codeType;
        this.codeTypeDesc = codeTypeDesc;
        this.codeSubType = codeSubType;
        this.codeSubTypeDesc = codeSubTypeDesc;
        this.inServer = inServer;
    }

    public ProductRowModel(Product product){
        this.code = String.valueOf(product.getId());
        this.description = product.getDescription();
        this.codeType = String.valueOf(product.getIdproductType());
        this.codeTypeDesc = String.valueOf(product.getProductTypeDescription());
        this.codeSubType = String.valueOf(product.getIdproductSubType());
        this.codeSubTypeDesc = String.valueOf(product.getProductSubTypeDescription());
        this.inServer = true;
        this.product = product;
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

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getCodeTypeDesc() {
        return codeTypeDesc;
    }

    public void setCodeTypeDesc(String codeTypeDesc) {
        this.codeTypeDesc = codeTypeDesc;
    }

    public String getCodeSubType() {
        return codeSubType;
    }

    public void setCodeSubType(String codeSubType) {
        this.codeSubType = codeSubType;
    }

    public String getCodeSubTypeDesc() {
        return codeSubTypeDesc;
    }

    public void setCodeSubTypeDesc(String codeSubTypeDesc) {
        this.codeSubTypeDesc = codeSubTypeDesc;
    }

    public boolean isInServer() {
        return inServer;
    }

    public void setInServer(boolean inServer) {
        this.inServer = inServer;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
