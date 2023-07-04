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
import far.com.eatit.API.models.Product;
import far.com.eatit.API.models.ProductSubType;
import far.com.eatit.API.models.ProductType;
import far.com.eatit.Adapters.Models.ProductRowModel;
import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Adapters.ProductRowEditionAdapter;
import far.com.eatit.Adapters.SimpleRowEditionAdapter;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.Products;
import far.com.eatit.CloudFireStoreObjects.ProductsMeasure;
import far.com.eatit.Controllers.LicenseController;
import far.com.eatit.Controllers.ProductsController;
import far.com.eatit.Controllers.ProductsInvController;
import far.com.eatit.Controllers.ProductsMeasureController;
import far.com.eatit.Controllers.ProductsMeasureInvController;
import far.com.eatit.Controllers.ProductsSubTypesController;
import far.com.eatit.Controllers.ProductsSubTypesInvController;
import far.com.eatit.Controllers.ProductsTypesController;
import far.com.eatit.Controllers.ProductsTypesInvController;
import far.com.eatit.Dialogs.ProductsDialogfragment;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Interfases.DialogCaller;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.Utils.Funciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaintenanceProducts extends Fragment implements ListableActivity, DialogCaller {

    Main mainActivity;
    APIInterface apiInterface;
    LoginResponse loginResponse;

    RecyclerView rvList;
    Spinner spnProductType, spnProductSubType;
    ArrayList<ProductRowModel> objects;
    ProductRowEditionAdapter adapter;
    Product product;
    String lastSearch = null;
    //String lastFamilia;
    //String lastGrupo;
    String type;

    public MaintenanceProducts(){

    }
    public static MaintenanceProducts newInstance(Main mainActivity, String type){
        MaintenanceProducts fragment = new MaintenanceProducts();
        fragment.mainActivity = mainActivity;
        fragment.type = type;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loginResponse = Funciones.getLoginResponseData(mainActivity);
        apiInterface = APIClient.getClient(mainActivity).create(APIInterface.class);
        return inflater.inflate(R.layout.maintenance_w2_spinner, container, false);
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
        spnProductType = view.findViewById(R.id.spn);
        spnProductSubType = view.findViewById(R.id.spn2);
        ((TextView)view.findViewById(R.id.spnTitle)).setText("Familia");
        ((TextView)view.findViewById(R.id.spnTitle2)).setText("Grupo");

        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(mainActivity);
        rvList.setLayoutManager(manager);
        adapter = new ProductRowEditionAdapter(mainActivity,this, objects);
        rvList.setAdapter(adapter);

        /*
        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            ProductsTypesController.getInstance(MaintenanceProducts.this).fillSpinner(spnProductType,true);
            ProductsSubTypesController.getInstance(MaintenanceProducts.this).fillSpinner(spnProductSubType,true);
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            ProductsTypesInvController.getInstance(MaintenanceProducts.this).fillSpinner(spnProductType,true);
            ProductsSubTypesInvController.getInstance(MaintenanceProducts.this).fillSpinner(spnProductSubType,true);
        }
         */


        spnProductType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV familia = (KV)spnProductType.getSelectedItem();

                /*
                if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
                    ProductsSubTypesController.getInstance(MaintenanceProducts.this).fillSpinner(spnProductSubType, true, familia.getKey());
                }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                    ProductsSubTypesInvController.getInstance(MaintenanceProducts.this).fillSpinner(spnProductSubType, true, familia.getKey());
                }*/

                fillProductSubTypes(Integer.parseInt(familia.getKey()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnProductSubType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV grupo = (KV)spnProductSubType.getSelectedItem();
                //lastGrupo = (grupo.getKey().equals("0"))?null:grupo.getKey() ;
                searchEntities(
                        Integer.parseInt(((KV)spnProductType.getSelectedItem()).getKey()),
                        Integer.parseInt(((KV)spnProductSubType.getSelectedItem()).getKey()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        fillProductTypes();
    }



    public void searchEntities(int idProductType, int idProductSubType){
        mainActivity.showWaitingDialog();
        apiInterface.getProducts(loginResponse.getLicense().getId(),idProductType, idProductSubType).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                ArrayList<ProductRowModel> lrm = new ArrayList<>();
                mainActivity.dismissWaitingDialog();

                if(response.isSuccessful()){
                    List<Product> list = response.body();
                    for(Product obj : list){
                        //String code, String description, String codeType, String codeTypeDesc, String codeSubType, String codeSubTypeDesc, boolean inServer
                        lrm.add(new ProductRowModel(obj));
                    }
                }else{
                    Snackbar.make(getView(),response.errorBody().byteStream().toString(), BaseTransientBottomBar.LENGTH_LONG).show();
                }
                refreshList(lrm);

            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Snackbar.make(getView(),t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                mainActivity.dismissWaitingDialog();
            }
        });

    }




    public void callAddDialog(boolean isNew){
        FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
        Fragment prev = mainActivity.getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment =  ProductsDialogfragment.newInstance(mainActivity,type, (isNew)?null:product, this::dialogClosed);
        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        /*
        String description = "";
        if(products != null){
            description = products.getDESCRIPTION();
        }
        final Dialog d = Funciones.getAlertDeleteAllDependencies(MaintenanceProducts.this,description,
                (type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)?productsController.getDependencies(products.getCODE()):productsInvController.getDependencies(products.getCODE())));
        Button btnAceptar = d.findViewById(R.id.btnPositive);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(products != null){
                    if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                        productsController.deleteFromFireBase(products);
                    }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                        productsInvController.deleteFromFireBase(products);
                    }

                }
                d.dismiss();
            }
        });

       d.show();*/

    }


    public void refreshList(ArrayList<ProductRowModel> list){
        ProductRowEditionAdapter la = new ProductRowEditionAdapter(mainActivity, this,list);
        rvList.setAdapter(la);
        rvList.invalidate();
    }

    /*
    public void refreshList(){
        objects.clear();
        String where = "1 = 1 ";
        String[] args = null;

        ArrayList<String> x = new ArrayList<>();
        if(lastSearch != null){
            where+=" AND p."+ProductsController.DESCRIPTION+" like  ? ";
            x.add(lastSearch+"%");
        }
        if(lastFamilia != null){
            where+= "AND pt."+ ProductsTypesController.CODE+" = ? ";
            x.add(lastFamilia);
        }

        if(lastGrupo != null){
            where+= "AND pst."+ProductsSubTypesController.CODE+" = ? ";
            x.add(lastGrupo);
        }

        if(x.size() > 0){
            args = x.toArray(new String[x.size()]);
        }
        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            objects.addAll(productsController.getProductsPRM(where, args, null));
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            objects.addAll(productsInvController.getProductsPRM(where, args, null));
        }

        adapter.notifyDataSetChanged();
    }*/


    @Override
    public void onClick(Object obj) {
        ProductRowModel sr = (ProductRowModel)obj;
        product = sr.getProduct();

       /* if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
            products = productsController.getProductByCode(sr.getCode());
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            products = productsInvController.getProductByCode(sr.getCode());
        }*/
        showOptions(false);

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

    private void fillProductTypes(){
        mainActivity.showWaitingDialog();
        apiInterface.getProductTypes(loginResponse.getLicense().getId()).enqueue(new Callback<List<ProductType>>() {
            @Override
            public void onResponse(Call<List<ProductType>> call, Response<List<ProductType>> response) {
                ArrayList<KV> lrm = new ArrayList<>();
                mainActivity.dismissWaitingDialog();

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
                spnProductType.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<ProductType>> call, Throwable t) {
                Snackbar.make(getView(),t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                mainActivity.dismissWaitingDialog();
            }
        });

    }

    private void fillProductSubTypes(int idProductType){
        mainActivity.showWaitingDialog();
        apiInterface.getProductSubTypes(loginResponse.getLicense().getId(), idProductType).enqueue(new Callback<List<ProductSubType>>() {
            @Override
            public void onResponse(Call<List<ProductSubType>> call, Response<List<ProductSubType>> response) {
                ArrayList<KV> lrm = new ArrayList<>();
                mainActivity.dismissWaitingDialog();

                if(response.isSuccessful()){
                    List<ProductSubType> list = response.body();
                    for(ProductSubType obj : list){
                        //String id, String text, Object entity
                        lrm.add(new KV(String.valueOf(obj.getId()),obj.getDescription()));
                    }
                }else{
                    Snackbar.make(getView(),response.errorBody().byteStream().toString(), BaseTransientBottomBar.LENGTH_LONG).show();
                }

                ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(mainActivity, android.R.layout.simple_list_item_1,lrm);
                spnProductSubType.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<ProductSubType>> call, Throwable t) {
                Snackbar.make(getView(),t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                mainActivity.dismissWaitingDialog();
            }
        });

    }

    @Override
    public void dialogClosed(Object o) {
        searchEntities(getSelectedProductTypeId(), getSelectedProductSubTypeId());
    }

    private int getSelectedProductTypeId(){
      return Integer.parseInt(((KV)spnProductType.getSelectedItem()).getKey());
    }

    private int getSelectedProductSubTypeId(){
        return Integer.parseInt(((KV)spnProductSubType.getSelectedItem()).getKey());
    }
}

