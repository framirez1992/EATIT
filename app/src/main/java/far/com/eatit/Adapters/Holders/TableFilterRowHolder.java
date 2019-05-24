package far.com.eatit.Adapters.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import far.com.eatit.Adapters.Models.TableFilterRowModel;
import far.com.eatit.R;

public class TableFilterRowHolder extends RecyclerView.ViewHolder {
    TextView tvTable, tvTask, tvOrigen, tvDestiny;
    CheckBox cbActive;
    ImageView imgMenu,imgTime ;
    public TableFilterRowHolder(View itemView) {
        super(itemView);
        tvTable = itemView.findViewById(R.id.tvTable);
        tvTask = itemView.findViewById(R.id.tvTask);
        tvOrigen = itemView.findViewById(R.id.tvOrigen);
        tvDestiny = itemView.findViewById(R.id.tvDestino);
        cbActive = itemView.findViewById(R.id.cbActive);
        imgMenu = itemView.findViewById(R.id.imgMenu);
        imgTime = itemView.findViewById(R.id.imgTime);
    }

    public void fillData(TableFilterRowModel tfrm){
        tvTable.setText(tfrm.getTables());
        tvTask.setText(tfrm.getTask());
        tvOrigen.setText((tfrm.getProducttype()!= null && !tfrm.getProducttype().equals(""))?tfrm.getProductTypeDescription():tfrm.getProductSubTypeDescription());
        tvDestiny.setText((tfrm.getUsertype()!= null && !tfrm.getUsertype().equals(""))?tfrm.getUserTypeDescription():tfrm.getUserDescription());
        cbActive.setChecked(tfrm.isEnabled());
        imgTime.setVisibility((tfrm.isInserver())?View.INVISIBLE:View.VISIBLE);
    }

    public ImageView getMenuImage(){
        return imgMenu;
    }
}
