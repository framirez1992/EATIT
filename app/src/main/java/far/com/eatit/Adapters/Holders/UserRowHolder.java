package far.com.eatit.Adapters.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import far.com.eatit.Adapters.Models.UserRowModel;
import far.com.eatit.R;

public class UserRowHolder extends RecyclerView.ViewHolder {
    TextView tvCode, tvUserName, tvUserRole;
    CheckBox cbActive;
    ImageView imgMenu,imgTime ;
    public UserRowHolder(View itemView) {
        super(itemView);
        tvCode = itemView.findViewById(R.id.tvCode);
        tvUserName = itemView.findViewById(R.id.tvUserName);
        tvUserRole = itemView.findViewById(R.id.tvUserRole);
        cbActive = itemView.findViewById(R.id.cbActive);
        imgMenu = itemView.findViewById(R.id.imgMenu);
        imgTime = itemView.findViewById(R.id.imgTime);
    }

    public void fillData(UserRowModel urm){
     tvCode.setText(urm.getCode());
     tvUserName.setText(urm.getUserName());
     tvUserRole.setText(urm.getUserRole());
     cbActive.setChecked(urm.isActive());
     imgTime.setVisibility((urm.isInserver())?View.INVISIBLE:View.VISIBLE);
    }

    public ImageView getMenuImage(){
        return imgMenu;
    }
}
