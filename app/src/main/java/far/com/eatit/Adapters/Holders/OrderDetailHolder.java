package far.com.eatit.Adapters.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import far.com.eatit.Adapters.Models.OrderDetailModel;
import far.com.eatit.R;

public class OrderDetailHolder extends RecyclerView.ViewHolder {
    TextView tvQuantity, tvDescription, tvMeasure;
    public OrderDetailHolder(View itemView) {
        super(itemView);
        tvQuantity = itemView.findViewById(R.id.tvQuantity);
        tvDescription = itemView.findViewById(R.id.tvDescription);
        tvMeasure = itemView.findViewById(R.id.tvMeasure);

    }

    public void fillData(OrderDetailModel od){
       tvQuantity.setText(od.getQuantity());
       tvDescription.setText(od.getProduct_name());
       tvMeasure.setText(od.getMeasureDescription());
    }
}