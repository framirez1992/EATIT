package far.com.eatit.Adapters.Holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import far.com.eatit.Adapters.Models.NotificationRowModel;
import far.com.eatit.R;

public class NotificationRowHolder extends RecyclerView.ViewHolder {
    TextView tvTitle, tvSender, tvDescription;
    ImageView imgIcon;
    public NotificationRowHolder(View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        tvSender = itemView.findViewById(R.id.tvSender);
        tvDescription = itemView.findViewById(R.id.tvDescription);
        imgIcon = itemView.findViewById(R.id.imgIcon);
    }

    public void fillData(NotificationRowModel n){
        tvTitle.setText(n.getTitle());
        tvSender.setText(n.getSender());
        tvDescription.setText(n.getDescripcion());
        imgIcon.setImageResource(n.getImgResource());
    }

}
