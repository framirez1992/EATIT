package far.com.eatit;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Adapters.SimpleRowEditionAdapter;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.TableCode;
import far.com.eatit.Controllers.LicenseController;
import far.com.eatit.Controllers.TableCodeController;
import far.com.eatit.Dialogs.TableCodesDialogFragment;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Interfases.ListableActivity;

public class MaintenanceTableCodes extends AppCompatActivity implements ListableActivity {

    RecyclerView rvList;
    ArrayList<SimpleRowModel> objects;
    SimpleRowEditionAdapter adapter;
    TableCodeController tableCodeController;
    TableCode tableCode = null;
    Licenses licence;
    String lastSearch = null;
    Spinner spnTabla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_w_spinner);
        tableCodeController = TableCodeController.getInstance(MaintenanceTableCodes.this);
        licence = LicenseController.getInstance(MaintenanceTableCodes.this).getLicense();


        rvList = findViewById(R.id.rvList);
        spnTabla = findViewById(R.id.spn);
        ((TextView)findViewById(R.id.spnTitle)).setText("Tabla");

        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(MaintenanceTableCodes.this);
        rvList.setLayoutManager(manager);
        adapter = new SimpleRowEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

        tableCodeController.fillSpinner(spnTabla, false);
        spnTabla.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV obj = ((KV)spnTabla.getSelectedItem());
                if(obj.getKey().equals("0"))
                    refreshList();
                else
                    refreshList();//DESCRIPCION
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
        tableCodeController.getReferenceFireStore().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                try {
                    tableCodeController.delete(null, null);//limpia la tabla

                    for (DocumentSnapshot ds : querySnapshot) {

                        TableCode pt = ds.toObject(TableCode.class);
                        tableCodeController.insert(pt);
                    }

                    refreshList();
                }catch (Exception e1){
                    e1.printStackTrace();
                }
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
        DialogFragment newFragment = null;
        if(isNew)
            newFragment = TableCodesDialogFragment.newInstance(null);
        else
            newFragment = TableCodesDialogFragment.newInstance(tableCode);


        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(tableCode != null){
            description = tableCode.getDESCRIPTION();
        }

        final Dialog d = new Dialog(MaintenanceTableCodes.this);
        d.setTitle("Delete");
        d.setContentView(R.layout.msg_2_buttons);
        TextView tvMsg = d.findViewById(R.id.tvMsg);
        Button btnAceptar = d.findViewById(R.id.btnPositive);
        Button btnCancelar = d.findViewById(R.id.btnNegative);

        tvMsg.setText("Esta seguro que desea eliminar \'"+description+"\' permanentemente?");
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tableCode != null){
                    tableCodeController.deleteFromFireBase(tableCode);
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
            where+=" AND "+TableCodeController.DESCRIPTION+" like  ? ";
            x.add(lastSearch+"%");
        }
        if(spnTabla.getSelectedItem() != null && !((KV)spnTabla.getSelectedItem()).getKey().equals("0")){
            where+= "AND "+ TableCodeController.CODETYPE+" = ? ";
            x.add(((KV)spnTabla.getSelectedItem()).getKey());
        }

        if(x.size() > 0){
            args = x.toArray(new String[x.size()]);
        }

        objects.addAll(tableCodeController.getTablaCodeSRM(where,args, null));
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
        tableCode = null;
        SimpleRowModel sr = (SimpleRowModel)obj;
        tableCode = tableCodeController.getTableCodeByCode(sr.getId());

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

