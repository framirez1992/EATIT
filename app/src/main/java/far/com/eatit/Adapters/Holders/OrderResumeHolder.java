package far.com.eatit.Adapters.Holders;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.OrderDetailModel;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.R;

public class OrderResumeHolder extends RecyclerView.ViewHolder {
    TextView tvName;
    EditText etCantidad;
    TextView tvUnitMeasure;
    Button btnLess, btnMore;
    ImageView imgMenu;
    LinearLayout llPadre;
    public OrderResumeHolder(View itemView) {
        super(itemView);
        llPadre = itemView.findViewById(R.id.llParent);
        tvName = itemView.findViewById(R.id.tvName);
        etCantidad = itemView.findViewById(R.id.etQuantity);
        tvUnitMeasure = itemView.findViewById(R.id.tvUnitMeasure);
        imgMenu = itemView.findViewById(R.id.imgMenu);
        btnMore = itemView.findViewById(R.id.btnMore);
        btnLess = itemView.findViewById(R.id.btnLess);
    }

    public void fillData(OrderDetailModel od, ArrayAdapter<KV> adapter){
        tvName.setText(od.getProduct_name());
        etCantidad.setText(od.getQuantity());
        tvUnitMeasure.setText(od.getMeasureDescription());
        /*spnUnitMeasure.setAdapter(adapter);
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
        }*/

        if(od.isBlocked()){
            btnMore.setEnabled(false);
            btnLess.setEnabled(false);
            //spnUnitMeasure.setEnabled(false);
        }else{
            btnMore.setEnabled(true);
            btnLess.setEnabled(true);
           // spnUnitMeasure.setEnabled(true);
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

    public EditText getEtCantidad() {
        return etCantidad;
    }
}
