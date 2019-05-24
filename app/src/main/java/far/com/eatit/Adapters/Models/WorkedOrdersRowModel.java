package far.com.eatit.Adapters.Models;

public class WorkedOrdersRowModel {
    String code, date, areaCode, areaDescription, mesaCode, mesaDescription, status;

    public WorkedOrdersRowModel(String code, String date, String areaCode,String areaDescription, String mesaCode, String mesaDescription, String status ){
        this.code = code; this.date = date; this.areaCode = areaCode; this.areaDescription = areaDescription;
        this.mesaCode = mesaCode; this.mesaDescription = mesaDescription; this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaDescription() {
        return areaDescription;
    }

    public void setAreaDescription(String areaDescription) {
        this.areaDescription = areaDescription;
    }

    public String getMesaCode() {
        return mesaCode;
    }

    public void setMesaCode(String mesaCode) {
        this.mesaCode = mesaCode;
    }

    public String getMesaDescription() {
        return mesaDescription;
    }

    public void setMesaDescription(String mesaDescription) {
        this.mesaDescription = mesaDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
