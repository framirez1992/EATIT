package far.com.eatit.UserMenu.Model;

import java.util.ArrayList;

public class ItemModel {
    private static final String HEADER_TYPE ="h";
    private static final String DETAIL_TYPE="d";
    String type, hexBackground;
    String title, description;
    ArrayList<String> urls;

    public static ItemModel initHeader(String title, String hexBackground){
        ItemModel lm = new ItemModel();
        lm.type = HEADER_TYPE;
        lm.title = title;
        lm.hexBackground = hexBackground;
        return  lm;
    }

    public static ItemModel initDetail(String title, ArrayList<String> urls){
        ItemModel lm = new ItemModel();
        lm.type = DETAIL_TYPE;
        lm.title = title;
        lm.hexBackground = "#FFFFFF";
        lm.urls = urls;
        return  lm;
    }

    public static ItemModel initDetail(String title, String url){
        ItemModel lm = new ItemModel();
        lm.type = DETAIL_TYPE;
        lm.title = title;
        lm.hexBackground = "#FFFFFF";
        lm.urls = new ArrayList<>();
        lm.urls.add(url);
        return  lm;
    }

    public String getHexBackground() {
        return hexBackground;
    }

    public void setHexBackground(String hexBackground) {
        this.hexBackground = hexBackground;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public boolean isHeader(){
        return type.equals(HEADER_TYPE);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }
}
