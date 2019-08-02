package far.com.eatit.Adapters.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import far.com.eatit.Adapters.Models.SelectableOrderRowModel;
import far.com.eatit.R;

public class SelectableOrderRowHolder extends RecyclerView.ViewHolder {
    TextView tvCode, tvFecha, tvArea, tvMesa, tvStatus, tvTotal, tvUserName;
    CheckBox cbCheck;

    public SelectableOrderRowHolder(View itemView) {
        super(itemView);
        tvCode = itemView.findViewById(R.id.tvOrderNum);
        tvFecha = itemView.findViewById(R.id.tvFecha);
        tvArea = itemView.findViewById(R.id.tvArea);
        tvMesa = itemView.findViewById(R.id.tvMesa);
        tvStatus = itemView.findViewById(R.id.tvStatus);
        tvTotal = itemView.findViewById(R.id.tvTotal);
        cbCheck = itemView.findViewById(R.id.cbCheck);
        tvUserName = itemView.findViewById(R.id.tvUserName);

    }

    public void fillData(SelectableOrderRowModel s){
        tvCode.setText(s.getCode());
        tvFecha.setText(s.getDate());
        tvArea.setText(s.getAreaDescription());
        tvMesa.setText(s.getMesaDescription());
        tvStatus.setText(s.getStatus());
        tvTotal.setText(s.getTotal());
        cbCheck.setChecked(s.isChecked());
        tvUserName.setText(s.getUserName());
    }

    public CheckBox getCbCheck(){
        return cbCheck;
    }
}
