package far.com.eatit;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

import far.com.eatit.Adapters.CompanyEditionAdapter;
import far.com.eatit.Adapters.Models.CompanyRowModel;
import far.com.eatit.CloudFireStoreObjects.Company;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.Controllers.CompanyController;
import far.com.eatit.Dialogs.AdminCompanyDialogFragment;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Interfases.DialogCaller;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.Utils.Funciones;

public class AdminLicenseCompany extends AppCompatActivity implements ListableActivity, DialogCaller {

    RecyclerView rvList;
    ArrayList<CompanyRowModel> objects;
    CompanyEditionAdapter adapter;
    Company company;
    String lastSearch = null;
    Licenses license;
    FirebaseFirestore fs;
    ArrayList<Company> companies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_w_spinner);

        if(getIntent().getExtras()== null || !getIntent().getExtras().containsKey(CODES.EXTRA_ADMIN_LICENSE) ){
            finish();
            return;
        }
        fs = FirebaseFirestore.getInstance();
        license = (Licenses) getIntent().getSerializableExtra(CODES.EXTRA_ADMIN_LICENSE);

        findViewById(R.id.cvSpinner).setVisibility(View.GONE);

        rvList = findViewById(R.id.rvList);

        objects = new ArrayList<>();
        companies = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(AdminLicenseCompany.this);
        rvList.setLayoutManager(manager);
        adapter = new CompanyEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

        refreshList();
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

    public void setUpListeners(){

        fs.collection(Tablas.generalUsers).document(license.getCODE())
                .collection(Tablas.generalUsersCompany)
                .addSnapshotListener(AdminLicenseCompany.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        companies = new ArrayList<>();
                        for (DocumentSnapshot ds : querySnapshot) {
                            Company t = ds.toObject(Company.class);
                            t.setDocumentReference(ds.getReference());
                            companies.add(t);
                        }
                        refreshList();

                    }
                });
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
        DialogFragment newFragment =  AdminCompanyDialogFragment.newInstance(AdminLicenseCompany.this, (isNew)?null:company, license.getCODE());
        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(company != null){
            description = company.getNAME();
        }

        String msg = "Esta seguro que desea eliminar \'"+description+"\' permanentemente?";
        final Dialog d = Funciones.getCustomDialog2Btn(this,getResources().getColor(R.color.red_700),"Delete", msg,R.drawable.delete,null, null);
        CardView btnAceptar = d.findViewById(R.id.btnPositive);
        CardView btnCancelar = d.findViewById(R.id.btnNegative);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fs.collection(Tablas.generalUsers).document(license.getCODE()).collection(Tablas.generalUsersCompany)
                        .whereEqualTo(CompanyController.CODE, company.getCODE()).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {
                                WriteBatch lote = fs.batch();
                                if(querySnapshot!=null && !querySnapshot.isEmpty()){
                                    for(DocumentSnapshot ds : querySnapshot){
                                        lote.delete(ds.getReference());
                                    }
                                }
                                lote.delete(company.getDocumentReference());
                                lote.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        company = null;
                                        d.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AdminLicenseCompany.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminLicenseCompany.this, e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();
        Window window = d.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);

    }

    public void refreshList(){
        objects.clear();
        for(Company t: companies){
            //String code, String name, String rnc, String address, String address2, String phone, String phone2, String logo, boolean isInserver
            objects.add(new CompanyRowModel(t.getCODE(), t.getNAME(),t.getRNC(), t.getADDRESS(), t.getADDRESS2(), t.getPHONE(), t.getPHONE2(), t.getLOGO(),  true));
        }
        adapter.notifyDataSetChanged();
    }



    @Override
    public void onClick(Object obj) {
        company = null;
        CompanyRowModel sr = (CompanyRowModel)obj;

        for(Company c: companies){
            if(c.getCODE().equals(sr.getCode())){
                company = c;
                break;
            }
        }

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

    @Override
    public void dialogClosed(Object o) {

    }

    public FirebaseFirestore getFs(){
        return fs;
    }
}
