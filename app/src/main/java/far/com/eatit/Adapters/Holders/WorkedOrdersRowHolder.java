package far.com.eatit.Adapters.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import far.com.eatit.Adapters.Models.WorkedOrdersRowModel;
import far.com.eatit.R;

public class WorkedOrdersRowHolder extends RecyclerView.ViewHolder {
    TextView tvCode, tvFecha, tvArea, tvMesa, tvStatus;

    public WorkedOrdersRowHolder(View itemView) {
        super(itemView);
        tvCode = itemView.findViewById(R.id.tvOrderNum);
        tvFecha = itemView.findViewById(R.id.tvFecha);
        tvArea = itemView.findViewById(R.id.tvArea);
        tvMesa = itemView.findViewById(R.id.tvMesa);
        tvStatus = itemView.findViewById(R.id.tvStatus);
    }

    public void fillData(WorkedOrdersRowModel w){
        tvCode.setText(w.getCode());
        tvFecha.setText(w.getDate());
        tvArea.setText(w.getAreaDescription());
        tvMesa.setText(w.getMesaDescription());
        tvStatus.setText(w.getStatus());
    }


}