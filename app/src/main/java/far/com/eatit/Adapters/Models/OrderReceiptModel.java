package far.com.eatit.Adapters.Models;

public class OrderReceiptModel {
    String id, areaID, areaDescription, mesaID, mesaDescription;
    double total;

    public OrderReceiptModel(String id, String areaID, String areaDescription, String mesaID, String mesaDescription, double total){
        this.id = id;
        this.areaID = areaID;
        this.areaDescription = areaDescription;
        this.mesaID = mesaID;
        this.mesaDescription = mesaDescription;
        this.total= total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAreaID() {
        return areaID;
    }

    public void setAreaID(String areaID) {
        this.areaID = areaID;
    }

    public String getAreaDescription() {
        return areaDescription;
    }

    public void setAreaDescription(String areaDescription) {
        this.areaDescription = areaDescription;
    }

    public String getMesaID() {
        return mesaID;
    }

    public void setMesaID(String mesaID) {
        this.mesaID = mesaID;
    }

    public String getMesaDescription() {
        return mesaDescription;
    }

    public void setMesaDescription(String mesaDescription) {
        this.mesaDescription = mesaDescription;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
