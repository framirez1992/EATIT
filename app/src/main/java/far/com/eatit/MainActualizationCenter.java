package far.com.eatit;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Adapters.SimpleRowEditionAdapter;
import far.com.eatit.Controllers.LicenseController;
import far.com.eatit.Controllers.TableFilterController;
import far.com.eatit.DataBase.CloudFireStoreDB;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Interfases.FireBaseOK;
import far.com.eatit.Interfases.ListableActivity;

public class MainActualizationCenter extends AppCompatActivity implements ListableActivity, OnFailureListener, FireBaseOK {

    RecyclerView rvList;
    ArrayList<SimpleRowModel> objects;
    SimpleRowEditionAdapter adapter;
    String lastSearch = null;
    Spinner spnTables;
    KV table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_w_spinner);


        rvList = findViewById(R.id.rvList);
        spnTables = findViewById(R.id.spn);
        ((TextView)findViewById(R.id.spnTitle)).setText("Modulo");

        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(MainActualizationCenter.this);
        rvList.setLayoutManager(manager);
        adapter = new SimpleRowEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

        TableFilterController.getInstance(MainActualizationCenter.this).fillSpnTables(spnTables,false);
        spnTables.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              table = (KV)parent.getSelectedItem();
              actualizar();
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
        refreshList();
    }


    public void refreshList(){

       /* objects.clear();
        String where = "1 = 1 ";
        String[] args = null;
        ArrayList<String> x = new ArrayList<>();

        if(lastSearch != null){
            where+=" AND pst."+AreasDetailController.DESCRIPTION+" like  ? ";
            x.add(lastSearch+"%");
        }
        if(spnFamily.getSelectedItem() != null && !((KV)spnFamily.getSelectedItem()).getKey().equals("0")){
            where+= "AND pt."+ AreasController.CODE+" = ? ";
            x.add(((KV)spnFamily.getSelectedItem()).getKey());
        }

        if(x.size() > 0){
            args = x.toArray(new String[x.size()]);
        }

        objects.addAll(areasDetailController.getAllAreasDetailSRM(where,args, null));
        adapter.notifyDataSetChanged();*/
    }


    @Override
    public void onClick(Object obj) {
        /*areasDetail = null;
        SimpleRowModel sr = (SimpleRowModel)obj;
        areasDetail = areasDetailController.getAreasDetailByCode(sr.getId());*/

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

    public void actualizar(){
        CloudFireStoreDB.getInstance(this, this, this).ActualizarTabla(LicenseController.getInstance(this).getLicense().getCODE(), table);
    }

    @Override
    public void onFailure(@NonNull Exception e) {

    }

    @Override
    public void OnFireBaseEndContact(int code) {

    }

    @Override
    public void sendMessage(String message) {

    }
}

