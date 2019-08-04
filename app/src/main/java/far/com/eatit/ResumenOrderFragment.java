package far.com.eatit;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.OrderDetailModel;
import far.com.eatit.Adapters.Models.OrderModel;
import far.com.eatit.Adapters.OrderResumeAdapter;
import far.com.eatit.CloudFireStoreObjects.AreasDetail;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.CloudFireStoreObjects.SalesDetails;
import far.com.eatit.Controllers.AreasController;
import far.com.eatit.Controllers.AreasDetailController;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Controllers.TempOrdersController;
import far.com.eatit.Controllers.UserControlController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResumenOrderFragment extends Fragment {


    RecyclerView rvList;
    LinearLayout llSave;
    LinearLayout llMore;
    LinearLayout llCancel;
    FloatingActionButton btnOrder;
    TempOrdersController tempOrdersController;
    SalesController salesController;
    TextInputEditText etNotas;
    ImageView imgMore;
    LinearLayout llGoMenu;
    Spinner spnAreas, spnMesas;

    MainOrders parentActivity;

    public ResumenOrderFragment() {
        // Required empty public constructor
    }
    public void setParent(MainOrders parent){
        this.parentActivity = parent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        tempOrdersController = TempOrdersController.getInstance(parentActivity);
        salesController = SalesController.getInstance(parentActivity);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resumen_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    public void init(View v){
        llSave = v.findViewById(R.id.llSave);
        llCancel = v.findViewById(R.id.llCancel);
        rvList = v.findViewById(R.id.rvResultList);
        btnOrder = v.findViewById(R.id.btnAddOrder);
        etNotas = v.findViewById(R.id.etNotas);
        imgMore = v.findViewById(R.id.imgMore);
        llMore = v.findViewById(R.id.llMore);
        llGoMenu = v.findViewById(R.id.llGoMenu);
        spnAreas = v.findViewById(R.id.spnAreas);
        spnMesas = v.findViewById(R.id.spnMesas);

        if(llGoMenu != null) {
            llGoMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToMenu();
                }
            });
        }

        rvList.setLayoutManager(new LinearLayoutManager(parentActivity));

        imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(llMore.getVisibility() == View.GONE){
                   llMore.setVisibility(View.VISIBLE);
                   imgMore.setImageResource(R.drawable.ic_arrow_drop_up);
                }else{
                    llMore.setVisibility(View.GONE);
                    imgMore.setImageResource(R.drawable.ic_arrow_drop_down);
                }
            }
        });

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                llSave.setEnabled(false);
                Save();
            }
        });

        llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llCancel.setVisibility(View.GONE);
                parentActivity.prepareNewOrder();
                parentActivity.setThemeNormal();
                parentActivity.refresh();
            }
        });

        setUpSpinnersAreas();
    }


    public AdapterView.OnItemSelectedListener onAreaSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            KV value = (KV)parent.getSelectedItem();
            if(value.getKey().equals("0")){
                spnMesas.setAdapter(null);
                return;
            }
                AreasDetailController.getInstance(parentActivity).fillSpinnerWithAssignedTables(spnMesas, value.getKey());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public boolean validate(){
        if(rvList.getAdapter() == null ||((OrderResumeAdapter)rvList.getAdapter()).hasBlockedProducts()){
            Snackbar.make(getView(), "No puede realizar la orden con productos NO DISPONIBLES.", Snackbar.LENGTH_LONG).show();
            return false;
        }else if(spnAreas.getSelectedItem() == null || spnMesas.getSelectedItem() == null
                || ((KV)spnAreas.getSelectedItem()).getKey().equals("-1") || ((KV)spnMesas.getSelectedItem()).getKey().equals("-1")){
            Snackbar.make(getView(), "Debe seleccionar la mesa", Snackbar.LENGTH_LONG).show();
            return false;
        }else if(rvList.getAdapter() == null ||rvList.getAdapter().getItemCount() ==0){
            Snackbar.make(getView(), "Debe seleccionar al menos 1 item", Snackbar.LENGTH_LONG).show();
            return false;
        }else if(!validQuantitys()){
            Snackbar.make(getView(), "Las cantidades deben ser mayor que 0", Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    public boolean validQuantitys(){
       ArrayList<OrderDetailModel> items  = tempOrdersController.getOrderDetailModels((parentActivity).getOrderCode());
       for(OrderDetailModel odm : items){
           if(odm.getQuantity().equals("") || odm.getQuantity().equals(".") || odm.getQuantity().equalsIgnoreCase("0")){
         return false;
           }
       }

       return true;
    }

    public void Save(){
        if(validate()){
            if(salesController.getSaleByCode(tempOrdersController.getTempSale().getCODE()) != null){
                editOrder();
            }else {
                saveOrder();
            }
            spnAreas.setSelection(0);
        }
        llSave.setEnabled(true);
    }

    public void saveOrder(){
        salesController.save(etNotas.getText().toString(), ((KV)spnMesas.getSelectedItem()).getKey());
        parentActivity.refresh();

    }

    public void editOrder(){
        try {
            Sales s = tempOrdersController.getTempSale();
            s.setTOTAL(tempOrdersController.getSumPrice());
            s.setMDATE(null);
            s.setSTATUS(CODES.CODE_ORDER_STATUS_OPEN);
            s.setNOTES(etNotas.getText().toString());
            s.setCODEAREADETAIL(((KV)spnMesas.getSelectedItem()).getKey());

            salesController.editDetailToFireBase(s, tempOrdersController.getTempSalesDetails(s));

            parentActivity.refresh();
            parentActivity.setThemeNormal();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void refreshList(){

        OrderResumeAdapter adapter = new OrderResumeAdapter(parentActivity, parentActivity, TempOrdersController.getInstance(parentActivity).getOrderDetailModels(((parentActivity).getOrderCode())));
        rvList.setAdapter(adapter);
        rvList.getAdapter().notifyDataSetChanged();
        rvList.invalidate();

    }

    public void prepareResumeForEdition(){
        llCancel.setVisibility(View.VISIBLE);
        Sales s = TempOrdersController.getInstance(parentActivity).getTempSale();
        AreasDetail ad = AreasDetailController.getInstance(parentActivity).getAreasDetailByCode(s.getCODEAREADETAIL());

        etNotas.setText(s.getNOTES());
        spnMesas.setOnItemSelectedListener(null);
        spnAreas.setOnItemSelectedListener(null);
        AreasController.getInstance(parentActivity).fillSpinner(spnAreas, false);
        AreasDetailController.getInstance(parentActivity).fillSpinner(spnMesas,false,ad.getCODEAREA());
        for(int i = 0; i<spnAreas.getAdapter().getCount(); i++){
            if(((KV)spnAreas.getAdapter().getItem(i)).getKey().equals(ad.getCODEAREA())){
                spnAreas.setSelection(i);
                break;
            }
        }
        for(int i = 0; i<spnMesas.getAdapter().getCount(); i++){
            if(((KV)spnMesas.getAdapter().getItem(i)).getKey().equals(ad.getCODE())){
                spnMesas.setSelection(i);
                break;
            }
        }

        spnAreas.setEnabled(false);
        spnMesas.setEnabled(false);

    }


    public void goToMenu(){
        ((MainOrders)getActivity()).showMenu();
        ((MainOrders)getActivity()).refreshProductsSearch(0);
    }

    public void setSelection(int pos){
        rvList.scrollToPosition(pos);
    }

    public void setUpSpinnersAreas(){
        AreasController.getInstance(parentActivity).fillSpinnerAreasForAssignedTables(spnAreas, true);
        spnAreas.setOnItemSelectedListener(onAreaSelected);
        spnAreas.setSelection(0);//TODOS
        spnAreas.setEnabled(true);
        spnMesas.setEnabled(true);
    }
}
