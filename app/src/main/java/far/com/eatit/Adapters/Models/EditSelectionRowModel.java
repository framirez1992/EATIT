package far.com.eatit.Adapters.Models;

public class EditSelectionRowModel {
    String code, description, text;
    boolean checked;
    Object entity;

    public EditSelectionRowModel(String code, String description, String text, boolean checked){
        this.code = code;
        this.description = description;
        this.text = text;
        this.checked = checked;
    }
    public EditSelectionRowModel(String code, String description, String text, boolean checked, Object entity){
        this.code = code;
        this.description = description;
        this.text = text;
        this.checked = checked;
        this.entity = entity;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Object getEntity() {
        return entity;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }
}
