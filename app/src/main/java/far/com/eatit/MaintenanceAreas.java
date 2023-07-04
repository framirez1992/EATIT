package far.com.eatit;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
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

import far.com.eatit.API.models.Area;
import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Adapters.SimpleRowEditionAdapter;
import far.com.eatit.CloudFireStoreObjects.Areas;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.Controllers.AreasController;
import far.com.eatit.Controllers.LicenseController;
import far.com.eatit.Dialogs.AreasDialogFragment;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.Utils.Funciones;

public class MaintenanceAreas extends AppCompatActivity implements ListableActivity {

    RecyclerView rvList;
    ArrayList<SimpleRowModel> objects;
    SimpleRowEditionAdapter adapter;
    AreasController areasController;
    Area areas = null;
    Licenses licence;
    String lastSearch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_screen);
        areasController = AreasController.getInstance(MaintenanceAreas.this);
        licence = LicenseController.getInstance(MaintenanceAreas.this).getLicense();


        rvList = findViewById(R.id.rvList);
        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(MaintenanceAreas.this);
        rvList.setLayoutManager(manager);
        adapter = new SimpleRowEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

        refreshList(lastSearch);


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
        if(isNew){
            newFragment = AreasDialogFragment.newInstance(null);
        }else {
            newFragment = AreasDialogFragment.newInstance(areas);
        }

        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(areas != null){
            description = areas.getDescription();
        }

        final Dialog d = new Dialog(MaintenanceAreas.this);
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
                    Funciones.showAlertDependencies(MaintenanceAreas.this, msgDependency);
                    d.dismiss();
                    return;
                }

                if(areas != null){
                    //areasController.deleteFromFireBase(areas);
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
        String where = null;
        String[] args = null;
        if(data != null){
            where = AreasController.DESCRIPTION+" like ?";
            args = new String[]{data+"%"};
        }

        objects.addAll(areasController.getAllAreasSRM(where,args));
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
        areas = null;
        SimpleRowModel sr = (SimpleRowModel)obj;

        areas = areasController.getAreaByCode(sr.getId()+"");

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
        if(areas != null){
                //msgDependency = areasController.hasDependencies(areas.getCODE());
        }
        return msgDependency;
    }
}
