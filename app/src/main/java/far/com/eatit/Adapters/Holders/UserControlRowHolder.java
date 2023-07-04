package far.com.eatit.Adapters.Holders;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import far.com.eatit.Adapters.Models.UserControlRowModel;
import far.com.eatit.R;

public class UserControlRowHolder extends RecyclerView.ViewHolder {
    TextView tvControl, tvTarget, tvTargetDescription;
    CheckBox cbActive;
    ImageView imgMenu,imgTime ;
    public UserControlRowHolder(View itemView) {
        super(itemView);
        tvControl = itemView.findViewById(R.id.tvControl);
        tvTarget = itemView.findViewById(R.id.tvTarget);
        tvTargetDescription = itemView.findViewById(R.id.tvTargetDescription);
        cbActive = itemView.findViewById(R.id.cbActive);
        imgMenu = itemView.findViewById(R.id.imgMenu);
        imgTime = itemView.findViewById(R.id.imgTime);
    }

    public void fillData(UserControlRowModel u){
        tvControl.setText(u.getDescription());
        tvTarget.setText(u.getTargetDescription());
        tvTargetDescription.setText(u.getTargetCodedescription());
        cbActive.setChecked(u.isActive());
        imgTime.setVisibility((u.isInserver())?View.INVISIBLE:View.VISIBLE);
    }

    public ImageView getMenuImage(){
        return imgMenu;
    }
}
