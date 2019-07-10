package far.com.eatit.Adapters.Holders;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.zip.Inflater;

import far.com.eatit.Adapters.Models.OrderDetailModel;
import far.com.eatit.Adapters.Models.OrderModel;
import far.com.eatit.Globales.CODES;
import far.com.eatit.R;

public class OrderHolder extends RecyclerView.ViewHolder {
    TextView tvEstatus, tvOrderNum,tvTime, tvNotes;
    GridLayout gvOrderContent;
    CardView btnRemove, btnReady, btnAlert;
    ImageView imgMenu;
    LinearLayout llPadre;

    public OrderHolder(View itemView) {
        super(itemView);
        llPadre = itemView.findViewById(R.id.llPadre);
        tvEstatus = itemView.findViewById(R.id.tvEstatus);
        tvOrderNum = itemView.findViewById(R.id.tvOrderNumber);
        tvNotes = itemView.findViewById(R.id.tvNotes);
        tvTime = itemView.findViewById(R.id.tvTime);
        gvOrderContent = itemView.findViewById(R.id.gvOrderContent);
        btnRemove = itemView.findViewById(R.id.btnRemove);
        btnAlert = itemView.findViewById(R.id.btnAlert);
        btnReady = itemView.findViewById(R.id.btnReady);
        imgMenu = itemView.findViewById(R.id.imgMenu);
    }

    public void fillData(LayoutInflater inflater, OrderModel om){
        if(om.getStatus().equals(CODES.CODE_ORDER_STATUS_CANCELED+"")){//cancelaron la orden.
            llPadre.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.red_200));
            tvEstatus.setVisibility(View.VISIBLE);tvEstatus.setText("ANULADA");
        }else if(om.getStatus().equals(CODES.CODE_ORDER_STATUS_OPEN+"") && om.isEdited()){
            llPadre.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.amber_200));
            tvEstatus.setVisibility(View.VISIBLE);tvEstatus.setText("EDITADA");
        }else{
            llPadre.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.white));
            tvEstatus.setVisibility(View.GONE);
        }
        tvOrderNum.setText("Orden#: "+om.getOrderNum());
        tvTime.setText(om.getTime());
        tvNotes.setText((om.getNotes()!= null && !om.getNotes().equals(""))?"Notas: "+om.getNotes():"");
        fillOrderContent(inflater, om);
    }

    public void fillOrderContent(LayoutInflater inflater, OrderModel om){

       if(gvOrderContent.getChildCount() > 0)
            gvOrderContent.removeAllViews();

        for(OrderDetailModel o: om.getDetail()){
           LinearLayout tr = (LinearLayout) inflater.inflate(R.layout.line_order_card, gvOrderContent, false);
           TextView tvProductName = tr.findViewById(R.id.tvDescription);
           EditText etQuantity = tr.findViewById(R.id.etQuantity);

           tvProductName.setText(o.getProduct_name()+" "+o.getMeasureDescription());
           etQuantity.setText(o.getQuantity());
           if(o.isBlocked()){
              tr.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.red_200));
           }else{
               tr.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.white));
           }
           gvOrderContent.addView(tr);

       }
    }

    public ImageView getImgMenu() {
        return imgMenu;
    }
}
