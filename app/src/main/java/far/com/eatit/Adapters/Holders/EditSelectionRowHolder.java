package far.com.eatit.Adapters.Holders;


import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import far.com.eatit.Adapters.Models.EditSelectionRowModel;
import far.com.eatit.R;

public class EditSelectionRowHolder extends RecyclerView.ViewHolder {
    TextView tvDescription;
    EditText etEditable;
    CheckBox cbCheck;
    public EditSelectionRowHolder(View itemView) {
        super(itemView);
        tvDescription = itemView.findViewById(R.id.tvDescription);
        etEditable = itemView.findViewById(R.id.etEdit);
        cbCheck = itemView.findViewById(R.id.cbCheck);
    }

    public void fillData(EditSelectionRowModel obj){
    tvDescription.setText(obj.getDescription());
    etEditable.setText(obj.getText());
    cbCheck.setChecked(obj.isChecked());
    }

    public CheckBox getCbCheck() {
        return cbCheck;
    }

    public EditText getEtEditable() {
        return etEditable;
    }
}
