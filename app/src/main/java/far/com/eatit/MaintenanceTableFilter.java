package far.com.eatit;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.TableFilterRowModel;
import far.com.eatit.Adapters.TableFilterRowEditionAdapter;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.TableFilter;
import far.com.eatit.CloudFireStoreObjects.UserControl;
import far.com.eatit.Controllers.LicenseController;
import far.com.eatit.Controllers.ProductsSubTypesController;
import far.com.eatit.Controllers.ProductsTypesController;
import far.com.eatit.Controllers.TableFilterController;
import far.com.eatit.Controllers.UserTypesController;
import far.com.eatit.Controllers.UsersController;
import far.com.eatit.Dialogs.TableFilterDialogFragment;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Interfases.ListableActivity;

public class MaintenanceTableFilter extends AppCompatActivity implements ListableActivity {

    RecyclerView rvList;
    Spinner spnTabla, spnTask;
    ArrayList<TableFilterRowModel> objects;
    TableFilterRowEditionAdapter adapter;
    TableFilterController tableFilterController;
    TableFilter tableFilter;
    Licenses licence;
    String lastSearch = null;

    KV table, task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_table_filter);

        tableFilterController = TableFilterController.getInstance(MaintenanceTableFilter.this);
        licence = LicenseController.getInstance(MaintenanceTableFilter.this).getLicense();


        rvList = findViewById(R.id.rvList);
        spnTabla = findViewById(R.id.spn);
        spnTask = findViewById(R.id.spn2);
        ((TextView)findViewById(R.id.spnTitle)).setText("Table");
        ((TextView)findViewById(R.id.spnTitle2)).setText("Task");

        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(MaintenanceTableFilter.this);
        rvList.setLayoutManager(manager);
        adapter = new TableFilterRowEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

        TableFilterController.getInstance(MaintenanceTableFilter.this).fillSpnTables(spnTabla,true);
        spnTabla.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                table = (KV)parent.getSelectedItem();
                TableFilterController.getInstance(MaintenanceTableFilter.this).fillSpnTaskByTable(spnTask,table.getKey());
                refreshList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnTask.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                task = (KV)parent.getSelectedItem();
                refreshList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        refreshList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        tableFilterController.getReferenceFireStore().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                tableFilterController.delete(null, null);//limpia la tabla

                for(DocumentSnapshot ds: querySnapshot){

                    TableFilter mu = ds.toObject(TableFilter.class);
                    tableFilterController.insert(mu);
                }

                refreshList();
            }
        });
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

    public void callAddDialog(boolean isNew){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment =  TableFilterDialogFragment.newInstance((isNew)?null:tableFilter);
        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(tableFilter != null){
            description = tableFilter.getCode();
        }

        final Dialog d = new Dialog(MaintenanceTableFilter.this);
        d.setTitle("Delete");
        d.setContentView(R.layout.msg_2_buttons);
        TextView tvMsg = d.findViewById(R.id.tvMsg);
        Button btnAceptar = d.findViewById(R.id.btnPositive);
        Button btnCancelar = d.findViewById(R.id.btnNegative);

        tvMsg.setText("Esta seguro que desea eliminar \'"+description+"\' permanentemente?");
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tableFilter != null){
                    tableFilterController.deleteFromFireBase(tableFilter);
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

    public void refreshList(){
        objects.clear();
        String where = "1 = 1 ";
        String[] args = null;
        ArrayList<String> x = new ArrayList<>();

        if(lastSearch != null){
            where += "AND (u."+ UsersController.USERNAME+" like  ?  OR "+ UserTypesController.DESCRIPTION+" like ? OR pt."+ ProductsTypesController.DESCRIPTION+" like ? OR "+ ProductsSubTypesController.DESCRIPTION+" like ? )";
            x.add(lastSearch+"%");x.add(lastSearch+"%");x.add(lastSearch+"%");x.add(lastSearch+"%");
        }
        if(table != null && !table.getKey().equals("0")){
            where += " AND tf."+ TableFilterController.TABLES+" like ? ";
            x.add(table.getKey());
        }

        if(task != null && !task.getKey().equals("0")){
            where += " AND tf."+ TableFilterController.TASK+" like ? ";
            x.add(task.getKey());
        }

        if(x.size() > 0){
            args = x.toArray(new String[x.size()]);
        }
        objects.addAll(tableFilterController.getTableFilterRM(where, args, null));
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
        tableFilter = null;
        TableFilterRowModel tf = (TableFilterRowModel)obj;

        tableFilter = tableFilterController.getTableFilterByCode(tf.getCode());

    }

    public SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if(!query.equals("")) {
                lastSearch = query;
                refreshList();
                return true;
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(newText.equals("")){
                lastSearch = null;
                refreshList();
                return true;
            }
            return false;
        }
    };
}
