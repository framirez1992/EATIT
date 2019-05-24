package far.com.eatit.Adapters.Models;

public class NotificationRowModel {
    String code,codeMessage, type, title,sender, descripcion;
    int imgResource;
    public NotificationRowModel(String code,String codeMessage, String type, String title,String sender, String description, int resourceImg){
        this.code = code;
        this.codeMessage = codeMessage;
        this.type = type;
        this.title = title;
        this.descripcion = description;
        this.imgResource = resourceImg;
        this.sender = sender;

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getImgResource() {
        return imgResource;
    }

    public void setImgResource(int imgResource) {
        this.imgResource = imgResource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCodeMessage() {
        return codeMessage;
    }

    public void setCodeMessage(String codeMessage) {
        this.codeMessage = codeMessage;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
