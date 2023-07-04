package far.com.eatit.Adapters.Models;

public class OptionModel {
    Object object;
    String text;
    int imgResource;

    public OptionModel(Object o, String text, int resource){
        this.object = o;
        this.text = text;
        this.imgResource = resource;
    }

    public String getText() {
        return text;
    }

    public int getImgResource() {
        return imgResource;
    }

    public Object getObject() {
        return object;
    }
}
