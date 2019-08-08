package far.com.eatit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import far.com.eatit.Adapters.Holders.OrderResumeHolder;
import far.com.eatit.Adapters.Models.OrderDetailModel;
import far.com.eatit.CloudFireStoreObjects.SalesDetails;
import far.com.eatit.Controllers.MeasureUnitsController;
import far.com.eatit.Controllers.TempOrdersController;
import far.com.eatit.Dialogs.AddProductDialog;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.MainOrders;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;

public class OrderResumeAdapter extends RecyclerView.Adapter<OrderResumeHolder> {
    ArrayList<OrderDetailModel> objects;
    ListableActivity listableActivity;
    Activity activity;
    AddProductDialog productDialog;

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
    public void onBindViewHolder(final OrderResumeHolder holder, final int position) {

        ArrayAdapter adapter = null;
        if( objects.get(position).getMeasures() != null &&  objects.get(position).getMeasures().size() >0){
            adapter = new ArrayAdapter<KV>(activity, android.R.layout.simple_list_item_1,objects.get(position).getMeasures());
        }

        holder.fillData(objects.get(position), adapter);
        final Button btnLess =  holder.getBtnLess();
        final Button btnMore = holder.getBtnMore();
        final EditText etQuantity = holder.getEtCantidad();

        holder.setBackgroundColor(activity.getResources(), objects.get(position).isBlocked());
        holder.getImgMenu().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.registerForContextMenu(v);
                v.showContextMenu();
                listableActivity.onClick(objects.get(position));
            }
        });

       btnLess.setOnClickListener(new View.OnClickListener() {
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
                    deleteOrderLine(objects.get(position), position);
                }else{
                    objects.get(position).setQuantity(""+newQuantity);
                    sd.setQUANTITY(newQuantity);
                    updateOrderLine(sd);
                    etQuantity.setText(""+(int)newQuantity);
                }

            }
        });
        btnMore.setOnClickListener(new View.OnClickListener() {
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
                    etQuantity.setText(""+(int)newQuantity);
                }
            }
        });

        etQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAddProductDialog(objects.get(position), holder);
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
        sd.setPOSITION(Integer.parseInt(Funciones.getSimpleTimeFormat().format(new Date())));
        TempOrdersController.getInstance(activity).update_Detail(sd);


        //((MainOrders)activity).refreshResume();
        //((MainOrders)activity).setResumeSelection(lastPos);
        ((MainOrders)activity).refreshProductsSearch(0);
    }

    public void deleteOrderLine(OrderDetailModel obj, int lastPos){

        String where = TempOrdersController.DETAIL_CODE+" = ?";
        String[]args = new String[]{obj.getCode()};
        TempOrdersController.getInstance(activity).delete_Detail(where, args);

        ((MainOrders)activity).refreshResume();
        ((MainOrders)activity).refreshProductsSearch(0);
        ((MainOrders)activity).setResumeSelection(lastPos);


    }

    public boolean hasBlockedProducts(){
        for(OrderDetailModel od: objects){
            if(od.isBlocked()){
                return true;
            }
        }
        return false;
    }


    public void callAddProductDialog(OrderDetailModel obj, OrderResumeHolder holder){
        FragmentTransaction ft = ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction();
        Fragment prev = ((AppCompatActivity)activity).getSupportFragmentManager().findFragmentByTag("AddProductDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        productDialog = AddProductDialog.newInstance(obj, holder, this);
        // Create and show the dialog.
        productDialog.show(ft, "AddProductDialog");
    }

    public void EditLineFromExternal(OrderDetailModel editedLine, OrderResumeHolder holder) {
        SalesDetails sd = TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProductAndCodeMeasure(editedLine.getCodeProduct(), editedLine.getCodeMeasure());
        if (sd == null) {
            return;

        } else {
            double newQuantity = Double.parseDouble(editedLine.getQuantity());
            if (newQuantity > 0) {
                sd.setQUANTITY(newQuantity);
                updateOrderLine(sd);
                holder.getEtCantidad().setText("" + (int) newQuantity);
            }
        }
    }
}
