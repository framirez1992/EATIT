package far.com.eatit;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import far.com.eatit.Adapters.NotificationsAdapter;
import far.com.eatit.Adapters.WorkedOrdersRowAdapter;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.Controllers.AreasController;
import far.com.eatit.Controllers.AreasDetailController;
import far.com.eatit.Controllers.NotificationsController;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Controllers.UserControlController;
import far.com.eatit.Controllers.UserInboxController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Utils.Funciones;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


    RecyclerView rvList;
    LinearLayout llFiltroMensajes, llFiltroOrdenes;
    Spinner spnFiltroOrdenes, spnAreas, spnMesas;
    Spinner spnFiltroMensajes;
    boolean workedOrders;
    Activity parent;
    KV status, area, mesa;
    KV msgStatus;
    boolean fromReceipt;
    String codeAreaDetail;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public void setWorkedOrders(boolean wo){
        this.workedOrders = wo;
    }

    public void setParent(Activity activity){
        this.parent = activity;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvList = view.findViewById(R.id.rvList);
        spnFiltroMensajes = view.findViewById(R.id.spnFiltroMensajes);
        llFiltroMensajes = view.findViewById(R.id.llFiltroMensajes);
        llFiltroOrdenes = view.findViewById(R.id.llFiltroOrdenes);
        spnFiltroOrdenes = view.findViewById(R.id.spnFiltroOrdenes);
        spnAreas = view.findViewById(R.id.spnAreas);
        spnMesas = view.findViewById(R.id.spnMesas);


        rvList.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        if(fromReceipt){
            llFiltroMensajes.setVisibility(View.GONE);
            llFiltroOrdenes.setVisibility(View.GONE);
            spnAreas.setVisibility(View.GONE);
            spnMesas.setVisibility(View.GONE);
        }else if(workedOrders){
            llFiltroMensajes.setVisibility(View.GONE);
            llFiltroOrdenes.setVisibility(View.VISIBLE);

            SalesController.getInstance(parent).fillSpinnerOrderStatus(spnFiltroOrdenes, false);
            AreasController.getInstance(parent).fillSpinnerAreasForAssignedTables(spnAreas, false);

            spnAreas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> p, View view, int position, long id) {
                     area = (KV)p.getSelectedItem();
                        AreasDetailController.getInstance(parent).fillSpinnerWithAssignedTables(spnMesas, area.getKey());

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spnMesas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> p, View view, int position, long id) {
                    mesa = (KV)p.getSelectedItem();
                    searchOrders();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

           spnFiltroOrdenes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> p, View view, int position, long id) {
                   status = (KV)p.getSelectedItem();
                   searchOrders();
               }

               @Override
               public void onNothingSelected(AdapterView<?> parent) {

               }
           });

        }else{
            llFiltroMensajes.setVisibility(View.VISIBLE);
            llFiltroOrdenes.setVisibility(View.GONE);

            UserInboxController.getInstance(parent).fillSpinnerMessageStatus(spnFiltroMensajes, true);
            spnFiltroMensajes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    msgStatus = (KV)parent.getSelectedItem();
                    refreshList();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        refreshList();
    }

    public void refreshList(){
        if(fromReceipt){
            searchDeliveredOrders();
        }else if(workedOrders){
           searchOrders();
        }else {
            String where = " 1 = 1 ";
            String[] args = null;
            ArrayList<String> values = new ArrayList<>();
            if(msgStatus != null && !msgStatus.getKey().equals("-1")){
                where+= " AND "+UserInboxController.getSTATUS() + " =  "+msgStatus.getKey();
                //values.add(msgStatus.getKey());
            }

            if(values.size() >0){
                args = values.toArray(new String[values.size()]);
            }
            NotificationsAdapter adapter = new NotificationsAdapter(parent, (MainOrders)parent, NotificationsController.getInstance(parent).getNotifications(where, args));
            rvList.setAdapter(adapter);
            rvList.getAdapter().notifyDataSetChanged();
            rvList.invalidate();
        }
    }


    public void searchOrders() {
        String where = " s."+SalesController.IDUSER+" = '"+ Funciones.getCodeuserLogged(parent) +"' ";
        if(status != null && !status.getKey().equals("-1")){
            where += " AND s."+SalesController.STATUS+" = '"+status.getKey()+"' ";
        }
        if(area!= null && !area.getKey().equals("-1")){
            if(mesa != null && !mesa.getKey().equals("-1")){
                where += " AND s."+ SalesController.IDTABLE+" = '"+mesa.getKey()+"' ";
            }else{
                where += " AND a."+AreasController.CODE+" = '"+area.getKey()+"' ";
            }

        }
        WorkedOrdersRowAdapter adapter = new WorkedOrdersRowAdapter(parent, (MainOrders) parent, SalesController.getInstance(parent).getWorkedOrderModels(where));
        rvList.setAdapter(adapter);
        rvList.getAdapter().notifyDataSetChanged();
        rvList.invalidate();
    }

    public void searchDeliveredOrders(){
    String where= " s." + SalesController.IDTABLE + " = '" + codeAreaDetail + "' ";
    where += " AND s." + SalesController.STATUS + " = '" + CODES.CODE_ORDER_STATUS_DELIVERED + "' ";
    WorkedOrdersRowAdapter adapter = new WorkedOrdersRowAdapter(parent, (MainReceipt) parent, SalesController.getInstance(parent).getWorkedOrderModels(where));
    rvList.setAdapter(adapter);
    rvList.getAdapter().notifyDataSetChanged();
    rvList.invalidate();

    }

    public void setFromReceipt(String codeAreaDetail){
        this.fromReceipt = true;
        this.workedOrders = false;
        this.codeAreaDetail = codeAreaDetail;
    }

}
