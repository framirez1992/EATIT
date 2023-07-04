package far.com.eatit.Adapters.Holders;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.R;

public class SimpleRowHolder extends RecyclerView.ViewHolder {
    TextView tvText;
    ImageView imgmenu, imgTime;
    public SimpleRowHolder(View itemView) {
        super(itemView);
        tvText = itemView.findViewById(R.id.tvText);
        imgmenu = itemView.findViewById(R.id.imgMenu);
        imgTime = itemView.findViewById(R.id.imgTime);
    }

    public void fillData(SimpleRowModel model){
        tvText.setText(model.getText());
        imgTime.setVisibility((model.isInServer())?View.INVISIBLE:View.VISIBLE);
    }

    public ImageView getMenuImage(){
        return imgmenu;
    }
}
