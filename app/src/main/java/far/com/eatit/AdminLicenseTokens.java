package far.com.eatit;

import android.app.Dialog;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;

import javax.annotation.Nullable;

import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Adapters.SimpleRowEditionAdapter;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.Token;
import far.com.eatit.Dialogs.TokenDialogFragment;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.Utils.Funciones;

public class AdminLicenseTokens extends AppCompatActivity implements ListableActivity {

    RecyclerView rvList;
    ArrayList<SimpleRowModel> objects;
    SimpleRowEditionAdapter adapter;

    Token token = null;
    Licenses license;
    String lastSearch = null;
    FirebaseFirestore fs;
    ArrayList<Token> tokens;
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
        tokens = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(AdminLicenseTokens.this);
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
                .collection(Tablas.generalUsersToken)
                .addSnapshotListener(AdminLicenseTokens.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        tokens = new ArrayList<>();
                        for (DocumentSnapshot ds : querySnapshot) {
                            Token t = ds.toObject(Token.class);
                            tokens.add(t);
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
        DialogFragment newFragment = null;
        if(isNew){
            newFragment = TokenDialogFragment.newInstance(this,null,license.getCODE());
        }else {
            newFragment = TokenDialogFragment.newInstance(this,token,license.getCODE());
        }

        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }


    public void callDeleteConfirmation(){

        String msg = "Esta seguro que desea eliminar el token \'"+token.getCODE()+"\' permanentemente?";
        final Dialog d = Funciones.getCustomDialog2Btn(this,getResources().getColor(R.color.red_700),"Delete", msg,R.drawable.delete,null, null);
        CardView btnAceptar = d.findViewById(R.id.btnPositive);
        CardView btnCancelar = d.findViewById(R.id.btnNegative);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fs.collection(Tablas.generalUsers).document(license.getCODE()).collection(Tablas.generalUsersToken).document(token.getCODE()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                token = null;
                                d.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminLicenseTokens.this, e.getMessage(),Toast.LENGTH_LONG).show();
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
        for(Token t: tokens){
            objects.add(new SimpleRowModel(t.getCODE(), t.getCODE()+"  - Auto Delete: "+(t.isAutodelete()?"1":"0"), true));
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
        SimpleRowModel item = (SimpleRowModel)obj;
        for(Token t :tokens){
            if(t.getCODE().equals(item.getId())){
                token = t;
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


  /*  public String getMsgDependency(){
        String msgDependency ="";
        if(productsType != null){
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                msgDependency = productsTypesController.hasDependencies(productsType.getCODE());
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY) ){
                msgDependency = productsTypesInvController.hasDependencies(productsType.getCODE());
            }

        }
        return msgDependency;
    }*/

    public FirebaseFirestore getFs(){
        return fs;
    }
}
