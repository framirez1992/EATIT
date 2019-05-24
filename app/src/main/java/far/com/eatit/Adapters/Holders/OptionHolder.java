package far.com.eatit.Adapters.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import far.com.eatit.Adapters.Models.OptionModel;
import far.com.eatit.R;

public class OptionHolder extends RecyclerView.ViewHolder {
    ImageView img;
    TextView text;
    public OptionHolder(View itemView) {
        super(itemView);
        img = itemView.findViewById(R.id.img);
        text = itemView.findViewById(R.id.text);
    }

    public void fillData(OptionModel om){
        img.setImageResource(om.getImgResource());
        text.setText(om.getText());
    }
}
