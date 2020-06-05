package far.com.eatit;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
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

import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Adapters.SimpleRowEditionAdapter;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.UserTypes;
import far.com.eatit.Controllers.UserTypesController;
import far.com.eatit.Controllers.UsersController;
import far.com.eatit.Dialogs.UserTypesDialogFragment;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.Utils.Funciones;

public class AdminLicenseUserTypes extends AppCompatActivity implements ListableActivity {

    RecyclerView rvList;
    ArrayList<SimpleRowModel> objects;
    ArrayList<UserTypes> userTypeList;
    SimpleRowEditionAdapter adapter;
    UserTypes userTypes;
    Licenses license;
    FirebaseFirestore fs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_screen);

        if(getIntent().getExtras()== null || !getIntent().getExtras().containsKey(CODES.EXTRA_ADMIN_LICENSE) ){
            finish();
            return;
        }
        fs = FirebaseFirestore.getInstance();
        license = (Licenses) getIntent().getSerializableExtra(CODES.EXTRA_ADMIN_LICENSE);

        rvList = findViewById(R.id.rvList);
        objects = new ArrayList<>();
        userTypeList = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(AdminLicenseUserTypes.this);
        rvList.setLayoutManager(manager);
        adapter = new SimpleRowEditionAdapter(this,this, objects);
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

        fs.collection(Tablas.generalUsers).document(license.getCODE())
                .collection(Tablas.generalUsersUserTypes)
                .addSnapshotListener(AdminLicenseUserTypes.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        userTypeList = new ArrayList<>();
                        for (DocumentSnapshot ds : querySnapshot) {
                            UserTypes t = ds.toObject(UserTypes.class);
                            t.setDocumentReference(ds.getReference());
                            userTypeList.add(t);
                        }
                        refreshList();

                    }
                });
    }

    public void callAddDialog(boolean isNew){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment =  UserTypesDialogFragment.newInstance((isNew)?null:userTypes);
        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(userTypes != null){
            description = userTypes.getDESCRIPTION();
        }

        final Dialog d = new Dialog(AdminLicenseUserTypes.this);
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
                    Funciones.showAlertDependencies(AdminLicenseUserTypes.this, msgDependency);
                    d.dismiss();
                    return;
                }

                if(userTypes != null){
                    fs.collection(Tablas.generalUsers).document(license.getCODE()).collection(Tablas.generalUsersUserTypes)
                            .whereEqualTo(UserTypesController.CODE, userTypes.getCODE()).get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot querySnapshot) {
                                    WriteBatch lote = fs.batch();
                                    if(querySnapshot!=null && !querySnapshot.isEmpty()){
                                        for(DocumentSnapshot ds : querySnapshot){
                                            lote.delete(ds.getReference());
                                        }
                                    }
                                    lote.delete(userTypes.getDocumentReference());
                                    lote.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            userTypes = null;
                                            d.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AdminLicenseUserTypes.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdminLicenseUserTypes.this, e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
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
        for(UserTypes ut: userTypeList){
            objects.add(new SimpleRowModel(ut.getCODE(), ut.getDESCRIPTION(), true));
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
        userTypes = null;
        SimpleRowModel sr = (SimpleRowModel)obj;
        for(UserTypes ut: userTypeList){
            if(sr.getId().equals(ut.getCODE())){
                userTypes = ut;
                break;
            }
        }

    }

    public SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if(!query.equals("")) {
                //lastSearch = query;
                refreshList();
                return true;
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(newText.equals("")){
               // lastSearch = null;
                refreshList();
                return true;
            }
            return false;
        }
    };

    public String getMsgDependency(){
        UsersController.getInstance(AdminLicenseUserTypes.this).getUsers(null, null, null);
        String msgDependency ="";
        if(userTypes != null){
            msgDependency = "Existen dependencias";//userTypesController.hasDependencies(userTypes.getCODE());

        }
        return msgDependency;
    }

}
