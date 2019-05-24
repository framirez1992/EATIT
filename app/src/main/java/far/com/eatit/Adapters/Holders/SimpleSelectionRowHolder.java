package far.com.eatit.Adapters.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import far.com.eatit.Adapters.Models.SimpleSeleccionRowModel;
import far.com.eatit.R;

public class SimpleSelectionRowHolder extends RecyclerView.ViewHolder {
    TextView tvName;
    CheckBox cbCheck;
    public SimpleSelectionRowHolder(View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvName);
        cbCheck = itemView.findViewById(R.id.cbCheck);
    }

    public void fillData(SimpleSeleccionRowModel model){
        tvName.setText(model.getName());
        cbCheck.setOnCheckedChangeListener(null);
        cbCheck.setChecked(model.isChecked());

    }

    public CheckBox getCbCheck() {
        return cbCheck;
    }
}