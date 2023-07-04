package far.com.eatit.Adapters.Models;

public class SimpleRowModel {
    String id, text;
    boolean inServer;
    Object entity;

    public SimpleRowModel(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public SimpleRowModel(String id, String text, boolean inServer){
        this.id = id;
        this.text = text;
        this.inServer = inServer;
    }

    public SimpleRowModel(String id, String text, Object entity) {
        this.id = id;
        this.text = text;
        this.entity = entity;
        this.inServer = true;
    }

    public Object getEntity() {
        return entity;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    public String getId() {
        return id;
    }
    public String getText() {
        return text;
    }
    public boolean isInServer(){
        return inServer;
    }
}
