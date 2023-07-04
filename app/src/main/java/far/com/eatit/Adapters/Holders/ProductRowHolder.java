package far.com.eatit.Adapters.Holders;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import far.com.eatit.Adapters.Models.ProductRowModel;
import far.com.eatit.R;

public class ProductRowHolder extends RecyclerView.ViewHolder {
    TextView tvCode, tvName, tvFamily, tvGroup;
    ImageView imgMenu,imgTime ;
    public ProductRowHolder(View itemView) {
        super(itemView);
        tvCode = itemView.findViewById(R.id.tvCode);
        tvName = itemView.findViewById(R.id.tvDescription);
        tvFamily = itemView.findViewById(R.id.tvFamily);
        tvGroup = itemView.findViewById(R.id.tvGroup);
        imgMenu = itemView.findViewById(R.id.imgMenu);
        imgTime = itemView.findViewById(R.id.imgTime);
    }

    public void fillData(ProductRowModel prm){
        tvCode.setText(prm.getCode());
        tvName.setText(prm.getDescription());
        tvFamily.setText(prm.getCodeTypeDesc());
        tvGroup.setText(prm.getCodeSubTypeDesc());
        imgTime.setVisibility((prm.isInServer())?View.INVISIBLE:View.VISIBLE);
    }

    public ImageView getMenuImage(){
        return imgMenu;
    }
}