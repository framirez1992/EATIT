package far.com.eatit.Adapters.Holders;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import far.com.eatit.Adapters.Models.NewOrderProductModel;
import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.R;

public class NewOrderProductHolder extends RecyclerView.ViewHolder {

    TextView tvDescription;
    EditText etQuantity;
    Button btnLess, btnMore;
    Spinner spnUnitMeasure;
    ImageView imgDelete;
    LinearLayout llPadre;
    public NewOrderProductHolder(View itemView) {
        super(itemView);
        this.llPadre = itemView.findViewById(R.id.llParent);
        this.tvDescription = itemView.findViewById(R.id.tvDescription);
        this.etQuantity = itemView.findViewById(R.id.etQuantity);
        this.spnUnitMeasure = itemView.findViewById(R.id.spnUnitMeasure);
        this.imgDelete = itemView.findViewById(R.id.imgDelete);
        this.btnLess = itemView.findViewById(R.id.btnLess);
        this.btnMore = itemView.findViewById(R.id.btnMore);
    }

    public void fillData(NewOrderProductModel obj, ArrayAdapter<KV> adapter){
        this.tvDescription.setText(obj.getName());
        this.etQuantity.setText(obj.getQuantity());
        this.spnUnitMeasure.setAdapter(adapter);
        this.btnMore.setEnabled(!obj.isBlocked());
        this.btnLess.setEnabled(!obj.isBlocked());
        this.spnUnitMeasure.setEnabled(!obj.isBlocked());

        if(adapter != null){
            spnUnitMeasure.setVisibility(View.VISIBLE);
            spnUnitMeasure.setAdapter(adapter);
//moviendo el spinner a la unidad de medida por defecto del producto.
                for (int i = 0; i < obj.getMeasures().size(); i++) {
                    if (((KV) obj.getMeasures().get(i)).getKey().equals(obj.getMeasure())) {
                        spnUnitMeasure.setSelection(i);
                        break;
                    }
                }

        }else{
            spnUnitMeasure.setVisibility(View.INVISIBLE);
        }


    }

    public void setBackgroundColor(Resources re, boolean isBloqued){
        if(isBloqued){
            llPadre.setBackgroundColor(re.getColor(R.color.red_200));
        }else{
            llPadre.setBackgroundColor(re.getColor(R.color.white));
        }
    }


    public ImageView getImgDelete() {
        return imgDelete;
    }

    public Button getBtnLess() {
        return btnLess;
    }

    public Button getBtnMore() {
        return btnMore;
    }

    public Spinner getSpnUnitMeasure() {
        return spnUnitMeasure;
    }

    public EditText getEtQuantity() {
        return etQuantity;
    }
}
