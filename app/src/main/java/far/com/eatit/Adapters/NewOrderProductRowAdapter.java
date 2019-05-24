package far.com.eatit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import far.com.eatit.Adapters.Holders.NewOrderProductHolder;
import far.com.eatit.Adapters.Models.NewOrderProductModel;
import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.CloudFireStoreObjects.SalesDetails;
import far.com.eatit.Controllers.TempOrdersController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.MainOrders;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;

public class NewOrderProductRowAdapter extends RecyclerView.Adapter<NewOrderProductHolder> {

    Activity activity;
    ArrayList<NewOrderProductModel> objects;
    ListableActivity listableActivity;
    int lastPosition = 0;
    public NewOrderProductRowAdapter(Activity act,ListableActivity la, ArrayList<NewOrderProductModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public NewOrderProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new NewOrderProductHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.product_row_new_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewOrderProductHolder holder, final int position) {

        ArrayAdapter adapter = null;
        if( objects.get(position).getMeasures() != null &&  objects.get(position).getMeasures().size() >0){
            adapter = new ArrayAdapter<KV>(activity, android.R.layout.simple_list_item_1,objects.get(position).getMeasures());
        }
        holder.fillData(objects.get(position), adapter);
        holder.setBackgroundColor(activity.getResources(), objects.get(position).isBlocked());

        if(TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProductAndCodeMeasure(
                objects.get(position).getCodeProduct(),
                objects.get(position).getMeasure())!= null) {

            holder.itemView.setBackgroundColor(activity.getResources().getColor(R.color.teal_A100));
            holder.getImgDelete().setVisibility(View.VISIBLE);
        }else{
            holder.itemView.setBackgroundColor(activity.getResources().getColor(R.color.white));
            holder.getImgDelete().setVisibility(View.GONE);
        }

        holder.getImgDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lastPosition = position;
                deleteOrderLine( objects.get(position));
            }
        });
        holder.getBtnLess().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se inhabilita el boton para evitar que el usuario clicke muy rapido y genere inconsistencias en el sistema.
                v.setEnabled(false);

                lastPosition = position;

                SalesDetails sd = TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProductAndCodeMeasure(
                        objects.get(position).getCodeProduct(),
                        objects.get(position).getMeasure());
                if(sd == null){
                    return;
                }
                double newQuantity = sd.getQUANTITY() - 1;
                if(newQuantity <= 0){
                    objects.get(position).setQuantity("0");
                    deleteOrderLine(objects.get(position));
                }else{
                    objects.get(position).setQuantity(""+newQuantity);
                    sd.setQUANTITY(newQuantity);
                    updateOrderLine(sd);
                }
                //se habilita nuevamente el boton

                v.setEnabled(true);

            }
        });
        holder.getBtnMore().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se inhabilita el boton para evitar que el usuario clicke muy rapido y genere inconsistencias en el sistema.
                v.setEnabled(false);
                lastPosition = position;

                SalesDetails sd = TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProductAndCodeMeasure(
                        objects.get(position).getCodeProduct(),
                        objects.get(position).getMeasure());
                if(sd == null){
                    objects.get(position).setQuantity("1");
                    saveOrderLine(objects.get(position));
                    return;
                }
                double newQuantity = sd.getQUANTITY() + 1;
                if(newQuantity > 0){
                    sd.setQUANTITY(newQuantity);
                    updateOrderLine(sd);
                }
                //Habilitando el boton nuevamente para su uso.
                v.setEnabled(true);
            }
        });

        final EditText etQuantity = holder.getEtQuantity();

        holder.getSpnUnitMeasure().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                objects.get(position).setMeasure(((KV)parent.getSelectedItem()).getKey());
                SalesDetails sd = TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProductAndCodeMeasure(
                        objects.get(position).getCodeProduct(),
                        objects.get(position).getMeasure());
                if(sd == null){
                    etQuantity.setText("0");
                }else{
                    etQuantity.setText((int)sd.getQUANTITY()+"");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if(parent!= null && parent.getSelectedItem()!= null){
                    objects.get(position).setMeasure(((KV)parent.getSelectedItem()).getKey());
                }

            }
        });
       /* holder.getImg().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listableActivity.onClick(objects.get(position));
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }


    public void saveOrderLine(NewOrderProductModel opm){
        String code = UUID.randomUUID().toString();
        String codeSale = ((MainOrders)activity).getOrderCode();
        String codeProduct = opm.getCodeProduct();
        String codeUnd = opm.getMeasure();
        int position = Integer.parseInt(Funciones.getSimpleTimeFormat().format(new Date()));
        double quantity = Double.parseDouble(opm.getQuantity());
        double unit = 0;
        double price =0;
        double discount = 0;
        SalesDetails sd = new SalesDetails(code,codeSale, codeProduct, codeUnd, position, quantity, unit, price, discount);
        TempOrdersController.getInstance(activity).insert_Detail(sd);

        ((MainOrders)activity).refreshProductsSearch(lastPosition);
        ((MainOrders)activity).refreshResume();

    }

    public void updateOrderLine(SalesDetails sd){
       /* String code = sd.getCODE();
        String codeSale = sd.getCODESALES();
        String codeProduct = sd.getCODEPRODUCT();
        String codeUnd = sd.getCODEUND();
        int position = sd.getPOSITION();
        double quantity = Double.parseDouble(opm.getQuantity());
        double unit = sd.getUNIT();
        double price =sd.getPRICE();
        double discount = sd.getDISCOUNT();
        SalesDetails sd2 = new SalesDetails(code,codeSale, codeProduct, codeUnd, position, quantity, unit, price, discount);
        */
       sd.setPOSITION(Integer.parseInt(Funciones.getSimpleTimeFormat().format(new Date())));
       TempOrdersController.getInstance(activity).update_Detail(sd);

        ((MainOrders)activity).refreshProductsSearch(lastPosition);
        ((MainOrders)activity).refreshResume();
    }

    public void deleteOrderLine(NewOrderProductModel obj){

        String where = TempOrdersController.DETAIL_CODE+" = ? ";
        String[]args = new String[]{obj.getCodeOrderDetail()};
        TempOrdersController.getInstance(activity).
                delete_Detail(where, args);

        ((MainOrders)activity).refreshProductsSearch(lastPosition);
        ((MainOrders)activity).refreshResume();
    }

}
