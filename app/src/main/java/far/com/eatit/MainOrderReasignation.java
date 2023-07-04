package far.com.eatit;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import far.com.eatit.API.models.Sale;
import far.com.eatit.Adapters.Models.SelectableOrderRowModel;
import far.com.eatit.Adapters.OrderSelectionAdapter;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.Controllers.AreasDetailController;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Controllers.UserControlController;
import far.com.eatit.Controllers.UsersController;
import far.com.eatit.Dialogs.WorkedOrdersDialog;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Interfases.ListableActivity;

public class MainOrderReasignation extends AppCompatActivity implements ListableActivity {


    RecyclerView rvList;
    LinearLayout llParent;
    LinearLayout ll1, ll2, ll3, llSave;
    TextView tvTitle, tvSpn1, tvSpn2, tvSpn3;
    Spinner spn1, spn2, spn3;
    String lastSearch;

    UserControlController userControlController;
    SalesController salesController;
    ArrayList<SelectableOrderRowModel> selected = new ArrayList<>();
    WorkedOrdersDialog workedOrdersDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_assignation);

        llParent = findViewById(R.id.llParent);
        rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(MainOrderReasignation.this));

        ll1 = findViewById(R.id.ll1);
        ll2 = findViewById(R.id.ll2);
        ll3 = findViewById(R.id.ll3);
        llSave = findViewById(R.id.llSave);
        tvTitle = findViewById(R.id.tvTitle);
        tvSpn1 = findViewById(R.id.tvSpn1);
        tvSpn2 = findViewById(R.id.tvSpn2);
        tvSpn3 = findViewById(R.id.tvSpn3);
        spn1 = findViewById(R.id.spn1);
        spn2 = findViewById(R.id.spn2);
        spn3 = findViewById(R.id.spn3);

        tvTitle.setText("Reasignar Ordenes");
        tvSpn1.setText("Desde");
        tvSpn2.setText("Hacia");
        tvSpn3.setText("Responsable");

        AreasDetailController.getInstance(MainOrderReasignation.this).fillSpinnerPendingOrders(spn1);

        spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV ad = (KV)parent.getSelectedItem();
                AreasDetailController.getInstance(MainOrderReasignation.this).fillSpinner(spn2,false);
                removeItem(spn2, ad.getKey());
                search();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV ad = (KV)parent.getSelectedItem();
                UsersController.getInstance(MainOrderReasignation.this).fillSpnUsersCreateOrders(spn3, ad.getKey());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    public void removeItem(Spinner spn, String key){
        for(int i=0; i<spn.getAdapter().getCount(); i++){
            if(((KV)((ArrayAdapter)spn.getAdapter()).getItem(i)).getKey().equals(key)){
                ((ArrayAdapter)spn.getAdapter()).remove(((KV)((ArrayAdapter)spn.getAdapter()).getItem(i)));
                ((ArrayAdapter)spn.getAdapter()).notifyDataSetChanged();
                break;
            }
        }

    }

    public void search(){
        selected.clear();
        if(spn1.getSelectedItem() != null){
            String codeAreaDetail = ((KV)spn1.getSelectedItem()).getKey();
            ArrayList<SelectableOrderRowModel> data = SalesController.getInstance(MainOrderReasignation.this).getSelectableOrderModelsByAreaDetail(/*codeAreaDetail*/0);
            OrderSelectionAdapter adapter = new OrderSelectionAdapter(this, this,data,selected);
            rvList.setAdapter(adapter);
        }

    }

    @Override
    public void onClick(Object obj) {
        SelectableOrderRowModel sor = (SelectableOrderRowModel)obj;
        callWorkedOrdersDialog(SalesController.getInstance(MainOrderReasignation.this).getSaleById(/*sor.getCode()*/0));
    }

    public void save(){
       if(validate()){
           ArrayList<Sale> list = new ArrayList<>();
           KV user = (KV)spn3.getSelectedItem();
           KV codeAreaDetail = (KV)spn2.getSelectedItem();
           for(SelectableOrderRowModel srm : selected){
               Sale s = SalesController.getInstance(MainOrderReasignation.this).getSaleById(/*srm.getCode()*/0);
               s.setIduser(/*user.getKey()*/0);
               s.setIdTable(codeAreaDetail.getKey());
               list.add(s);
           }
           //SalesController.getInstance(MainOrderReasignation.this).sendToFireBase(list,null);
           /////////////////////////////////////////////////////////////////////////////////////////////
           for(Sale s: list){
               SalesController.getInstance(MainOrderReasignation.this).update(s);
           }

           search();
       }
    }

    public boolean validate(){
        if(selected.size() ==0){
            Snackbar.make(llParent, "Debe seleccionar por lo menos una orden", Snackbar.LENGTH_LONG).show();
            return false;
        }else if(spn1.getSelectedItem() == null){
            Snackbar.make(llParent, "Seleccione la mesa  origen", Snackbar.LENGTH_LONG).show();
            return false;
        }else if(spn2.getSelectedItem() == null){
            Snackbar.make(llParent, "Seleccione la mesa destino", Snackbar.LENGTH_LONG).show();
            return false;
        }else if(spn3.getSelectedItem() == null){
            Snackbar.make(llParent, "Seleccione un responsable", Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void callWorkedOrdersDialog(Sale s){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialogWorkedOrders");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        workedOrdersDialog =  WorkedOrdersDialog.newInstance();
        workedOrdersDialog.setShowOnlyDetail(true, s);
        // Create and show the dialog.
        workedOrdersDialog.show(ft, "dialogWorkedOrders");
    }
}
