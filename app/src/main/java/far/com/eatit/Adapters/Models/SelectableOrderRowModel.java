package far.com.eatit.Adapters.Models;

public class SelectableOrderRowModel {
    String code, date, areaCode, areaDescription, mesaCode, mesaDescription, status, total,
    codeUser, userName;
    boolean checked;
    public SelectableOrderRowModel(String code, String date, String codeUser, String userName, String areaCode,String areaDescription, String mesaCode, String mesaDescription, String status,String total, boolean checked ){
        this.code = code; this.date = date; this.areaCode = areaCode; this.areaDescription = areaDescription;
        this.mesaCode = mesaCode; this.mesaDescription = mesaDescription; this.status = status;this.total = total;
        this.checked = checked;this.codeUser = codeUser; this.userName = userName;
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

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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
        this.userName = userName;
    }
}
