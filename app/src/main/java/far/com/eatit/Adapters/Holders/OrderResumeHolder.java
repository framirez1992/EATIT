package far.com.eatit.Adapters.Holders;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.OrderDetailModel;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.R;

public class OrderResumeHolder extends RecyclerView.ViewHolder {
    TextView tvName;
    EditText etCantidad;
    Spinner spnUnitMeasure;
    Button btnLess, btnMore;
    ImageView imgMenu;
    LinearLayout llPadre;
    public OrderResumeHolder(View itemView) {
        super(itemView);
        llPadre = itemView.findViewById(R.id.llParent);
        tvName = itemView.findViewById(R.id.tvName);
        etCantidad = itemView.findViewById(R.id.etQuantity);
        spnUnitMeasure = itemView.findViewById(R.id.spnUnitMeasure);
        imgMenu = itemView.findViewById(R.id.imgMenu);
        btnMore = itemView.findViewById(R.id.btnMore);
        btnLess = itemView.findViewById(R.id.btnLess);
    }

    public void fillData(OrderDetailModel od, ArrayAdapter<KV> adapter){
        tvName.setText(od.getProduct_name());
        etCantidad.setText(od.getQuantity());
        spnUnitMeasure.setAdapter(adapter);
        if(adapter != null){
            spnUnitMeasure.setVisibility(View.VISIBLE);
            spnUnitMeasure.setAdapter(adapter);
            for(int i= 0; i<od.getMeasures().size(); i++){
                if(((KV)od.getMeasures().get(i)).getKey().equals(od.getCodeMeasure())){
                    spnUnitMeasure.setSelection(i);
                    break;
                }
            }
        }else{
            spnUnitMeasure.setVisibility(View.INVISIBLE);
        }

        if(od.isBlocked()){
            btnMore.setEnabled(false);
            btnLess.setEnabled(false);
            spnUnitMeasure.setEnabled(false);
        }else{
            btnMore.setEnabled(true);
            btnLess.setEnabled(true);
            spnUnitMeasure.setEnabled(true);
        }
    }

    public void setBackgroundColor(Resources re, boolean isBloqued){
        if(isBloqued){
            llPadre.setBackgroundColor(re.getColor(R.color.red_200));
        }else{
            llPadre.setBackgroundColor(re.getColor(R.color.white));
        }
    }

    public ImageView getImgMenu() {
        return imgMenu;
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

    public EditText getEtCantidad() {
        return etCantidad;
    }
}
