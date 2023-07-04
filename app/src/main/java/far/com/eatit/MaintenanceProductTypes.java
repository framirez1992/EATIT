package far.com.eatit;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.LinearLayout;
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
import far.com.eatit.API.models.Company;
import far.com.eatit.API.models.Device;
import far.com.eatit.API.models.LoginResponse;
import far.com.eatit.API.models.ProductType;
import far.com.eatit.Adapters.CompanyEditionAdapter;
import far.com.eatit.Adapters.Models.CompanyRowModel;
import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Adapters.SimpleRowEditionAdapter;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.ProductsSubTypes;
import far.com.eatit.CloudFireStoreObjects.ProductsTypes;
import far.com.eatit.Controllers.LicenseController;
import far.com.eatit.Controllers.ProductsSubTypesController;
import far.com.eatit.Controllers.ProductsTypesController;
import far.com.eatit.Controllers.ProductsTypesInvController;
import far.com.eatit.Dialogs.ProductTypeDialogFragment;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Interfases.DialogCaller;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.Utils.Funciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaintenanceProductTypes extends Fragment implements ListableActivity, DialogCaller {

    Main mainActivity;

    APIInterface apiInterface;
    LoginResponse loginResponse;

    RecyclerView rvList;
    ArrayList<SimpleRowModel> objects;
    SimpleRowEditionAdapter adapter;
    ProductType productType = null;
    String lastSearch = null;

    String type;


    public MaintenanceProductTypes(){

    }

    public static MaintenanceProductTypes newInstance(Main mainActivity, String type){
        MaintenanceProductTypes fragment = new MaintenanceProductTypes();
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

        view.findViewById(R.id.llSpinner).setVisibility(View.GONE);

        rvList =  view.findViewById(R.id.rvList);
        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(mainActivity);
        rvList.setLayoutManager(manager);
        adapter = new SimpleRowEditionAdapter(mainActivity,this, objects);
        rvList.setAdapter(adapter);

        searchEntities();
    }

    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_w_spinner);

        if(getIntent().getExtras()== null || !getIntent().getExtras().containsKey(CODES.EXTRA_TYPE_FAMILY) ){
           finish();
           return;
        }

        type = getIntent().getStringExtra(CODES.EXTRA_TYPE_FAMILY);
        productsTypesController = ProductsTypesController.getInstance(MaintenanceProductTypes.this);
        productsTypesInvController = ProductsTypesInvController.getInstance(MaintenanceProductTypes.this);
        licence = LicenseController.getInstance(MaintenanceProductTypes.this).getLicense();





    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
         case R.id.action_new:
           callAddDialog(true);
             return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionEdit:
                callAddDialog(false);
                return true;
            case R.id.actionDelete:
                callDeleteConfirmation();
                return  true;

                default:return super.onContextItemSelected(item);
        }
    }

    /*
    public void setUpListeners(){
        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
            productsTypesController.getReferenceFireStore().addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                    productsTypesController.delete(null, null);//limpia la tabla

                    for (DocumentSnapshot ds : querySnapshot) {

                        ProductsTypes pt = ds.toObject(ProductsTypes.class);
                        productsTypesController.insert(pt);
                    }
                    refreshList(lastSearch);

                }
            });
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            productsTypesInvController.getReferenceFireStore().addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                    productsTypesInvController.delete(null, null);//limpia la tabla

                    for (DocumentSnapshot ds : querySnapshot) {

                        ProductsTypes pt = ds.toObject(ProductsTypes.class);
                        productsTypesInvController.insert(pt);
                    }
                    refreshList(lastSearch);

                }
            });
        }
    }*/
    public void callAddDialog(boolean isNew){
        FragmentTransaction ft =mainActivity.getSupportFragmentManager().beginTransaction();
        Fragment prev = mainActivity.getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment = null;
        if(isNew){
            newFragment = ProductTypeDialogFragment.newInstance(mainActivity,type,null, this::dialogClosed);
        }else {
            newFragment = ProductTypeDialogFragment.newInstance(mainActivity,type, productType, this::dialogClosed);
        }

        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(productType != null){
            description = productType.getDescription();
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

                if(productType != null){
                    if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                        //productsTypesController.deleteFromFireBase(productsType);
                    }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                        //productsTypesInvController.deleteFromFireBase(productsType);
                    }

                }
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
    public void refreshList(String data){

        objects.clear();
        String where = " 1 = 1 ";
        ArrayList<String> values = new ArrayList<>();
        String[] args = null;

        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
            if(data != null){
                where += " AND "+ProductsTypesController.DESCRIPTION+" like ?";
                values.add(data+"%");
            }
            args = values.toArray(new String[values.size()]);

            objects.addAll(productsTypesController.getAllProductTypesSRM(where, args));
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            if(data != null){
                where += " AND "+ProductsTypesInvController.DESCRIPTION+" like ?";
                values.add(data+"%");
            }
            args = values.toArray(new String[values.size()]);
            objects.addAll(productsTypesInvController.getAllProductTypesSRM(where, args));
        }
        adapter.notifyDataSetChanged();


    }*/


    @Override
    public void onClick(Object obj) {
         SimpleRowModel sr = (SimpleRowModel)obj;
         productType = (ProductType) sr.getEntity();
         /*if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
             productsType = productsTypesController.getProductTypeByCode(sr.getId());
         }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
             productsType = productsTypesInvController.getProductTypeByCode(sr.getId());
         }*/
        showOptions(false);
    }



    public SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if(!query.equals("")) {
                lastSearch = query;
                //refreshList(lastSearch);
                return true;
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(newText.equals("")){
                lastSearch = null;
               //refreshList(lastSearch);
                return true;
            }
            return false;
        }
    };


    public String getMsgDependency(){
        String msgDependency ="";
        if(productType != null){
           /* if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
               msgDependency = productsTypesController.hasDependencies(productsType.getCODE());
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY) ){
               msgDependency = productsTypesInvController.hasDependencies(productsType.getCODE());
            }*/

        }
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


    public void searchEntities(){
        startLoading();
        apiInterface.getProductTypes(loginResponse.getLicense().getId()).enqueue(new Callback<List<ProductType>>() {
            @Override
            public void onResponse(Call<List<ProductType>> call, Response<List<ProductType>> response) {
                ArrayList<SimpleRowModel> lrm = new ArrayList<>();
                endLoading();

                if(response.isSuccessful()){
                    List<ProductType> list = response.body();
                    for(ProductType obj : list){
                        //String id, String text, Object entity
                        lrm.add(new SimpleRowModel(String.valueOf(obj.getId()),obj.getDescription(),obj));
                    }
                }else{
                    Snackbar.make(getView(),response.errorBody().byteStream().toString(), BaseTransientBottomBar.LENGTH_LONG).show();
                }
                refreshList(lrm);

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
        searchEntities();
    }
}
