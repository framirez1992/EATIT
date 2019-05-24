package far.com.eatit.Adapters.Models;

public class OptionModel {
    int id;
    String text;
    int imgResource;

    public OptionModel(int id, String text, int resource){
        this.id = id;
        this.text = text;
        this.imgResource = resource;
    }

    public String getText() {
        return text;
    }

    public int getImgResource() {
        return imgResource;
    }
    public int getId(){
        return id;
    }
}
