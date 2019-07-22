package far.com.eatit.Adapters.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import far.com.eatit.Adapters.Models.ReceiptSavedModel;
import far.com.eatit.R;

public class ReceiptSavedHolder extends RecyclerView.ViewHolder {
    TextView tvCode, tvDate, tvUser, tvArea, tvMesa, tvAmount;
    public ReceiptSavedHolder(View itemView) {
        super(itemView);
        tvCode = itemView.findViewById(R.id.tvCode);
        tvUser = itemView.findViewById(R.id.tvUserName);
        tvDate = itemView.findViewById(R.id.tvDate);
        tvArea = itemView.findViewById(R.id.tvArea);
        tvMesa = itemView.findViewById(R.id.tvMesa);
        tvAmount = itemView.findViewById(R.id.tvTotal);
    }

    public void fillData(ReceiptSavedModel obj){
        tvCode.setText(obj.getCode());
        tvDate.setText(obj.getDate());
        tvUser.setText(obj.getUserName());
        tvArea.setText(obj.getAreaDescription());
        tvMesa.setText(obj.getAreaDetailDescription());
        tvAmount.setText("$"+obj.getTotal());
    }
}
