package far.com.eatit.Adapters.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import far.com.eatit.Adapters.Models.PercentRowModel;
import far.com.eatit.Generic.Objects.KV2;
import far.com.eatit.R;

public class SimplePercentRowHolder extends RecyclerView.ViewHolder {
    TextView tvPercent, tvDescription, tvCantidad, tvMonto;

    public SimplePercentRowHolder(View itemView) {
        super(itemView);
        this.tvPercent = itemView.findViewById(R.id.tvPercent);
        this.tvDescription = itemView.findViewById(R.id.tvDescription);
        this.tvCantidad = itemView.findViewById(R.id.tvCantidad);
        this.tvMonto = itemView.findViewById(R.id.tvMonto);
    }

    public void fillData(PercentRowModel item){
        tvDescription.setText(item.getDescription());
        tvPercent.setText(item.getPercent());
        tvCantidad.setText(item.getCantidad());
        tvMonto.setText(item.getMonto());
    }
}
