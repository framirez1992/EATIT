package far.com.eatit;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import far.com.eatit.API.APIClient;
import far.com.eatit.API.APIInterface;
import far.com.eatit.API.models.LoginResponse;
import far.com.eatit.API.models.ProductSubType;
import far.com.eatit.API.models.ProductType;
import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Adapters.SimpleRowEditionAdapter;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.ProductsSubTypes;
import far.com.eatit.CloudFireStoreObjects.ProductsTypes;
import far.com.eatit.Controllers.LicenseController;
import far.com.eatit.Controllers.ProductsSubTypesController;
import far.com.eatit.Controllers.ProductsSubTypesInvController;
import far.com.eatit.Controllers.ProductsTypesController;
import far.com.eatit.Controllers.ProductsTypesInvController;
import far.com.eatit.Dialogs.ProductSubTypeDialogFragment;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Interfases.DialogCaller;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.Utils.Funciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaintenanceProductSubTypes extends Fragment implements ListableActivity, DialogCaller {

    Main mainActivity;
    String type;
    APIInterface apiInterface;
    LoginResponse loginResponse;

    RecyclerView rvList;
    ArrayList<SimpleRowModel> objects;
    SimpleRowEditionAdapter adapter;
    ProductSubType productsSubType = null;
    String lastSearch = null;
    Spinner spnFamily;


    public MaintenanceProductSubTypes(){

    }
    public static MaintenanceProductSubTypes newInstance(Main mainActivity, String type){
        MaintenanceProductSubTypes fragment = new MaintenanceProductSubTypes();
        fragment.mainActivity = mainActivity;
        fragment.type = type;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loginResponse = Funciones.getLoginResponseData(mainActivity);
        apiInterface = APIClient.getClient(mainActivity).create(APIInterface.class);
        return inflater.inflate(R.layout.maintenance_w_spinner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.llMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptions(true);
            }
        });

        rvList = view.findViewById(R.id.rvList);
        spnFamily = view.findViewById(R.id.spn);
        ((TextView)view.findViewById(R.id.spnTitle)).setText("Familia");

        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(mainActivity);
        rvList.setLayoutManager(manager);
        adapter = new SimpleRowEditionAdapter(mainActivity,this, objects);
        rvList.setAdapter(adapter);

        /*
        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            productsTypesController.fillSpinner(spnFamily, true);
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            productsTypesInvController.fillSpinner(spnFamily, true);
        }
         */

        spnFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV obj = ((KV)spnFamily.getSelectedItem());
                searchEntities(Integer.parseInt(obj.getKey()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fillProductTypes();

    }


    public void callAddDialog(boolean isNew){
        FragmentTransaction ft =mainActivity.getSupportFragmentManager().beginTransaction();
        Fragment prev = mainActivity.getSupportFragmentManager().findFragmentByTag("dialog");

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment = null;
        if(isNew)
            newFragment = ProductSubTypeDialogFragment.newInstance(mainActivity,spnFamily.getAdapter(),type, null, this::dialogClosed);
            else
            newFragment = ProductSubTypeDialogFragment.newInstance(mainActivity,spnFamily.getAdapter(),type,productsSubType, this::dialogClosed);


        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(productsSubType != null){
            description = productsSubType.getDescription();
        }

        final Dialog d = new Dialog(mainActivity);
        d.setTitle("Delete");
        d.setContentView(R.layout.msg_2_buttons);
        TextView tvMsg = d.findViewById(R.id.tvMsg);
        Button btnAceptar = d.findViewById(R.id.btnPositive);
        Button btnCancelar = d.findViewById(R.id.btnNegative);

        tvMsg.setText("Esta seguro que desea eliminar \'"+description+"\' permanentemente?");
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msgDependency = getMsgDependency();
                if(!msgDependency.isEmpty()) {
                    Funciones.showAlertDependencies(mainActivity, msgDependency);
                    d.dismiss();
                    return;
                }
               /*  if(productsSubType != null){
                     if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
                         productsSubTypesController.deleteFromFireBase(productsSubType);
                     }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                         productsSubTypesInvController.deleteFromFireBase(productsSubType);
                     }
                }*/
                d.dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();

    }

    /*
    public void refreshList(){

        objects.clear();
        String where = "1 = 1 ";
        String[] args = null;
        ArrayList<String> x = new ArrayList<>();
        String order = null;

        if(lastSearch != null){
            where+=" AND pst."+ProductsSubTypesController.DESCRIPTION+" like  ? ";
            x.add(lastSearch+"%");
        }

        if(spnFamily.getSelectedItem() != null && !((KV)spnFamily.getSelectedItem()).getKey().equals("0")){
            where+= "AND pt."+ ProductsTypesController.CODE+" = ? ";
            x.add(((KV)spnFamily.getSelectedItem()).getKey());
        }else{
            order = "pst."+ProductsTypesController.DESCRIPTION;
        }

        if(x.size() > 0){
            args = x.toArray(new String[x.size()]);
        }

        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
            objects.addAll(productsSubTypesController.getAllProductSubTypesSRM(where, args, order));
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            objects.addAll(productsSubTypesInvController.getAllProductSubTypesSRM(where, args, order));
        }
        adapter.notifyDataSetChanged();
    }
*/

    @Override
    public void onClick(Object obj) {
        SimpleRowModel sr = (SimpleRowModel)obj;
        productsSubType = (ProductSubType)sr.getEntity();
        /*if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            productsSubType = productsSubTypesController.getProductTypeByCode(sr.getId());
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            productsSubType = productsSubTypesInvController.getProductTypeByCode(sr.getId());
        }
         */
        callAddDialog(false);

    }


    public SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if(!query.equals("")) {
                lastSearch = query;
                //refreshList();
                return true;
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(newText.equals("")){
                lastSearch = null;
                //refreshList();
                return true;
            }
            return false;
        }
    };

    public String getMsgDependency(){
        String msgDependency ="";
       /* if(productsSubType != null){
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                msgDependency = productsSubTypesController.hasDependencies(productsSubType.getCODE());
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY) ){
                msgDependency = productsSubTypesInvController.hasDependencies(productsSubType.getCODE());
            }

        }*/
        return msgDependency;
    }

    private void showOptions(boolean fromMenu) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mainActivity);
        bottomSheetDialog.setContentView(R.layout.admin_licenses_menu);

        View settings = bottomSheetDialog.findViewById(R.id.trSettings);
        View edit = bottomSheetDialog.findViewById(R.id.trEdit);
        View add = bottomSheetDialog.findViewById(R.id.trAdd);

        settings.setVisibility(View.GONE);
        edit.setVisibility(fromMenu?View.GONE:View.VISIBLE);
        add.setVisibility(fromMenu?View.VISIBLE:View.GONE);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                callAddDialog(false);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                callAddDialog(true);
            }
        });
        bottomSheetDialog.show();
    }


    public void searchEntities(int idProductType){
        startLoading();
        apiInterface.getProductSubTypes(loginResponse.getLicense().getId(), idProductType).enqueue(new Callback<List<ProductSubType>>() {
            @Override
            public void onResponse(Call<List<ProductSubType>> call, Response<List<ProductSubType>> response) {
                ArrayList<SimpleRowModel> lrm = new ArrayList<>();
                endLoading();

                if(response.isSuccessful()){
                    List<ProductSubType> list = response.body();
                    for(ProductSubType obj : list){
                        //String id, String text, Object entity
                        lrm.add(new SimpleRowModel(String.valueOf(obj.getId()),obj.getDescription(),obj));
                    }
                }else{
                    Snackbar.make(getView(),response.errorBody().byteStream().toString(), BaseTransientBottomBar.LENGTH_LONG).show();
                }
                refreshList(lrm);

            }

            @Override
            public void onFailure(Call<List<ProductSubType>> call, Throwable t) {
                Snackbar.make(getView(),t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                endLoading();
            }
        });

    }


    private void fillProductTypes(){
        startLoading();
        apiInterface.getProductTypes(loginResponse.getLicense().getId()).enqueue(new Callback<List<ProductType>>() {
            @Override
            public void onResponse(Call<List<ProductType>> call, Response<List<ProductType>> response) {
                ArrayList<KV> lrm = new ArrayList<>();
                endLoading();

                if(response.isSuccessful()){
                    List<ProductType> list = response.body();
                    for(ProductType obj : list){
                        //String id, String text, Object entity
                        lrm.add(new KV(String.valueOf(obj.getId()),obj.getDescription()));
                    }
                }else{
                    Snackbar.make(getView(),response.errorBody().byteStream().toString(), BaseTransientBottomBar.LENGTH_LONG).show();
                }

                ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(mainActivity, android.R.layout.simple_list_item_1,lrm);
                spnFamily.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<ProductType>> call, Throwable t) {
                Snackbar.make(getView(),t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                endLoading();
            }
        });

    }

    public void refreshList(ArrayList<SimpleRowModel> list){
        SimpleRowEditionAdapter la = new SimpleRowEditionAdapter(mainActivity, this,list);
        rvList.setAdapter(la);
        rvList.invalidate();
    }

    private void startLoading(){
        mainActivity.showWaitingDialog();
    }

    private void endLoading(){
        mainActivity.dismissWaitingDialog();
    }

    @Override
    public void dialogClosed(Object o) {
        KV pt = (KV) spnFamily.getSelectedItem();
        searchEntities(Integer.parseInt(pt.getKey()));
    }
}

