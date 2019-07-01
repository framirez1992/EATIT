package far.com.eatit.Adapters.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import far.com.eatit.Adapters.Models.ReceiptResumeModel;
import far.com.eatit.R;

public class ReceiptResumeHolder extends RecyclerView.ViewHolder {
    TextView tvQuantity, tvMeasure, tvDescription, tvTotal;
    public ReceiptResumeHolder(View itemView) {
        super(itemView);
        tvQuantity = itemView.findViewById(R.id.tvQuantity);
        tvMeasure = itemView.findViewById(R.id.tvMeasure);
        tvDescription = itemView.findViewById(R.id.tvDescription);
        tvTotal = itemView.findViewById(R.id.tvTotal);
    }

    public void fillData(ReceiptResumeModel obj){
        tvQuantity.setText(obj.getQuantity());
        tvMeasure.setText(obj.getMeasureDescription());
        tvDescription.setText(obj.getProductDescription());
        tvTotal.setText(obj.getTotal());
    }
}
