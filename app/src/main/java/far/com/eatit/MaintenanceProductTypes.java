package far.com.eatit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.Utils.Funciones;

public class MaintenanceProductTypes extends AppCompatActivity implements ListableActivity {

    RecyclerView rvList;
    ArrayList<SimpleRowModel> objects;
    SimpleRowEditionAdapter adapter;
    ProductsTypesController productsTypesController;
    ProductsTypesInvController productsTypesInvController;

    ProductsTypes productsType = null;
    Licenses licence;
    String lastSearch = null;

    String type;

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


        ((LinearLayout)findViewById(R.id.llSpinner)).setVisibility(View.GONE);

        rvList = findViewById(R.id.rvList);
        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(MaintenanceProductTypes.this);
        rvList.setLayoutManager(manager);
        adapter = new SimpleRowEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

        refreshList(lastSearch);



    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        try{
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView search = (SearchView) searchItem.getActionView();

        search.setOnQueryTextListener(searchListener);
        }catch(Exception e){
            e.printStackTrace();
        }
        return (super.onCreateOptionsMenu(menu));
    }

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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_delete, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
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
    }
    public void callAddDialog(boolean isNew){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment = null;
        if(isNew){
            newFragment = ProductTypeDialogFragment.newInstance(type,null);
        }else {
            newFragment = ProductTypeDialogFragment.newInstance(type, productsType);
        }

        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(productsType != null){
            description = productsType.getDESCRIPTION();
        }

        final Dialog d = new Dialog(MaintenanceProductTypes.this);
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
                    Funciones.showAlertDependencies(MaintenanceProductTypes.this, msgDependency);
                    d.dismiss();
                    return;
                }

                if(productsType != null){
                    if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                        productsTypesController.deleteFromFireBase(productsType);
                    }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                        productsTypesInvController.deleteFromFireBase(productsType);
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
    }


    @Override
    public void onClick(Object obj) {
         productsType = null;
         SimpleRowModel sr = (SimpleRowModel)obj;
         if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
             productsType = productsTypesController.getProductTypeByCode(sr.getId());
         }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
             productsType = productsTypesInvController.getProductTypeByCode(sr.getId());
         }

    }



    public SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if(!query.equals("")) {
                lastSearch = query;
                refreshList(lastSearch);
                return true;
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(newText.equals("")){
                lastSearch = null;
               refreshList(lastSearch);
                return true;
            }
            return false;
        }
    };


    public String getMsgDependency(){
        String msgDependency ="";
        if(productsType != null){
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
               msgDependency = productsTypesController.hasDependencies(productsType.getCODE());
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY) ){
               msgDependency = productsTypesInvController.hasDependencies(productsType.getCODE());
            }

        }
       return msgDependency;
    }
}
