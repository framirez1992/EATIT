package far.com.eatit.Adapters.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import far.com.eatit.Adapters.Models.OrderReceiptModel;
import far.com.eatit.R;

public class OrderReceiptHolder extends RecyclerView.ViewHolder {

    TextView tvOrder, tvArea, tvMesa, tvTotal;

    public OrderReceiptHolder(View itemView) {
        super(itemView);
        this.tvOrder = itemView.findViewById(R.id.tvOrder);
        this.tvArea = itemView.findViewById(R.id.tvArea);
        this.tvMesa = itemView.findViewById(R.id.tvMesa);
        this.tvTotal = itemView.findViewById(R.id.tvTotal);
    }

    public void fillData(OrderReceiptModel obj) {
        this.tvOrder.setText(obj.getId());
        this.tvArea.setText(obj.getAreaDescription());
        this.tvMesa.setText(obj.getMesaDescription());
        this.tvTotal.setText("$" + obj.getTotal());


    }

}