package far.com.eatit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import far.com.eatit.Adapters.Holders.OrderResumeHolder;
import far.com.eatit.Adapters.Models.OrderDetailModel;
import far.com.eatit.CloudFireStoreObjects.SalesDetails;
import far.com.eatit.Controllers.MeasureUnitsController;
import far.com.eatit.Controllers.TempOrdersController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.MainOrders;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;

public class OrderResumeAdapter extends RecyclerView.Adapter<OrderResumeHolder> {
    ArrayList<OrderDetailModel> objects;
    ListableActivity listableActivity;
    Activity activity;

    public OrderResumeAdapter(Activity act, ListableActivity la, ArrayList<OrderDetailModel> objs){
        this.activity = act;
        this.listableActivity = la;
        this.objects = objs;
    }
    @NonNull
    @Override
    public OrderResumeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderResumeHolder(((LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.order_resume_row ,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderResumeHolder holder, final int position) {

        ArrayAdapter adapter = null;
        if( objects.get(position).getMeasures() != null &&  objects.get(position).getMeasures().size() >0){
            adapter = new ArrayAdapter<KV>(activity, android.R.layout.simple_list_item_1,objects.get(position).getMeasures());
        }

        holder.fillData(objects.get(position), adapter);
        holder.setBackgroundColor(activity.getResources(), objects.get(position).isBlocked());
        holder.getImgMenu().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.registerForContextMenu(v);
                v.showContextMenu();
                listableActivity.onClick(objects.get(position));
            }
        });

        holder.getBtnLess().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SalesDetails sd = TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProductAndCodeMeasure(
                        objects.get(position).getCodeProduct(),
                        objects.get(position).getCodeMeasure());
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

            }
        });
        holder.getBtnMore().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalesDetails sd = TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProductAndCodeMeasure(
                        objects.get(position).getCodeProduct(),
                        objects.get(position).getCodeMeasure());
                if(sd == null){
                    //objects.get(position).setQuantity("1");
                    //saveOrderLine(objects.get(position));
                    return;
                }
                double newQuantity = sd.getQUANTITY() + 1;
                if(newQuantity > 0){
                    sd.setQUANTITY(newQuantity);
                    updateOrderLine(sd);
                }
            }
        });

        final EditText etQuantity = holder.getEtCantidad();

        holder.getSpnUnitMeasure().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                SalesDetails sd = TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProductAndCodeMeasure(
                        objects.get(position).getCodeProduct(),
                        objects.get(position).getCodeMeasure());

                String newMeasure = ((KV)parent.getSelectedItem()).getKey();
                if(newMeasure.equals(sd.getCODEUND())){
                    return;
                }

                sd.setCODEUND(newMeasure);
                objects.get(position).setCodeMeasure(newMeasure);
                updateOrderLine(sd);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if(parent!= null && parent.getSelectedItem()!= null){
                    objects.get(position).setCodeMeasure(((KV)parent.getSelectedItem()).getKey());
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public void saveOrderLine(OrderDetailModel opm){
        String code = Funciones.generateCode();
        String codeSale = ((MainOrders)activity).getOrderCode();
        String codeProduct = opm.getCodeProduct();
        String codeUnd = opm.getCodeMeasure();
        int position = Integer.parseInt(Funciones.getSimpleTimeFormat().format(new Date()));
        double quantity = Double.parseDouble(opm.getQuantity());
        double unit = 0;
        double price =0;
        double discount = 0;
        SalesDetails sd = new SalesDetails(code,codeSale, codeProduct, codeUnd, position, quantity, unit, price, discount);
        TempOrdersController.getInstance(activity).insert_Detail(sd);

        //((MainOrders)activity).refreshProductsSearch(lastPosition);
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

        ((MainOrders)activity).refreshResume();
        ((MainOrders)activity).refreshProductsSearch(0);
    }

    public void deleteOrderLine(OrderDetailModel obj){

        String where = TempOrdersController.DETAIL_CODE+" = ?";
        String[]args = new String[]{obj.getCode()};
        TempOrdersController.getInstance(activity).delete_Detail(where, args);

        ((MainOrders)activity).refreshResume();
        ((MainOrders)activity).refreshProductsSearch(0);

    }

    public boolean hasBlockedProducts(){
        for(OrderDetailModel od: objects){
            if(od.isBlocked()){
                return true;
            }
        }
        return false;
    }
}
