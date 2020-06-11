package far.com.eatit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Adapters.Models.SimpleSeleccionRowModel;
import far.com.eatit.Adapters.SimpleRowEditionAdapter;
import far.com.eatit.Adapters.SimpleSelectionRowAdapter;
import far.com.eatit.CloudFireStoreObjects.Company;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.UserControl;
import far.com.eatit.CloudFireStoreObjects.UserTypes;
import far.com.eatit.CloudFireStoreObjects.Users;
import far.com.eatit.Controllers.UserControlController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.Utils.Funciones;

public class AdminLicenseControls extends AppCompatActivity implements ListableActivity {

    LinearLayout llMain, llControls, llBack;
    Spinner spnType;
    RecyclerView rvList, rvControls;
    ArrayList<SimpleRowModel> objects;
    SimpleRowEditionAdapter adapter;
    SimpleRowModel selectedObject;

    Licenses license;
    String lastSearch = null;
    FirebaseFirestore fs;

    ArrayList<Company> companies;
    ArrayList<UserTypes>userTypes;
    ArrayList<Users> users;
    ArrayList<SimpleSeleccionRowModel> userControls;
    ArrayList<SimpleSeleccionRowModel> selectedUserControls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_license_control);

        if(getIntent().getExtras()== null || !getIntent().getExtras().containsKey(CODES.EXTRA_ADMIN_LICENSE) ){
            finish();
            return;
        }
        fs = FirebaseFirestore.getInstance();
        license = (Licenses) getIntent().getSerializableExtra(CODES.EXTRA_ADMIN_LICENSE);

        llMain = findViewById(R.id.llMain);
        llControls = findViewById(R.id.llControls);
        llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llControls.setVisibility(View.GONE);
                llMain.setVisibility(View.VISIBLE);
            }
        });

        rvControls = findViewById(R.id.rvControls);
        rvList = findViewById(R.id.rvList);
        objects = new ArrayList<>();
        companies = new ArrayList<>();
        userTypes = new ArrayList<>();
        users = new ArrayList<>();

        spnType = findViewById(R.id.spn);
        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(AdminLicenseControls.this);
        rvList.setLayoutManager(manager);
        adapter = new SimpleRowEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

       fillSpinner();

    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

            getMenuInflater().inflate(R.menu.menu_admin_license_controls, menu);
        return (super.onCreateOptionsMenu(menu));
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_admin_license_controls, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionEdit:
                refreshControlsList();
                return true;

            default:return super.onContextItemSelected(item);
        }
    }

    public void setUpListeners(){

        fs.collection(Tablas.generalUsers).document(license.getCODE())
                .collection(Tablas.generalUsersCompany)
                .addSnapshotListener(AdminLicenseControls.this, new EventListener<QuerySnapshot>() {
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

        fs.collection(Tablas.generalUsers).document(license.getCODE())
                .collection(Tablas.generalUsersUserTypes)
                .addSnapshotListener(AdminLicenseControls.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        userTypes = new ArrayList<>();
                        for (DocumentSnapshot ds : querySnapshot) {
                            UserTypes t = ds.toObject(UserTypes.class);
                            t.setDocumentReference(ds.getReference());
                            userTypes.add(t);
                        }
                        refreshList();

                    }
                });

        fs.collection(Tablas.generalUsers).document(license.getCODE())
                .collection(Tablas.generalUsersUsers)
                .addSnapshotListener(AdminLicenseControls.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        users = new ArrayList<>();
                        for (DocumentSnapshot ds : querySnapshot) {
                            Users t = ds.toObject(Users.class);
                            t.setDocumentReference(ds.getReference());
                            users.add(t);
                        }
                        refreshList();

                    }
                });
    }





    public void refreshList(){
        objects.clear();
        if(((KV)spnType.getSelectedItem()).getKey().equals(CODES.USERSCONTROL_TARGET_USER)){
            for(Users t: users){
                objects.add(new SimpleRowModel(t.getCODE(), t.getCODE()+" - "+t.getUSERNAME(), true));
            }
        }else if(((KV)spnType.getSelectedItem()).getKey().equals(CODES.USERSCONTROL_TARGET_USER_ROL)){
            for(UserTypes t: userTypes){
                objects.add(new SimpleRowModel(t.getCODE(), t.getDESCRIPTION(), true));
            }
        }else if(((KV)spnType.getSelectedItem()).getKey().equals(CODES.USERSCONTROL_TARGET_COMPANY)){
            for(Company t: companies){
                objects.add(new SimpleRowModel(t.getCODE(), t.getNAME(), true));
            }
        }

        adapter.notifyDataSetChanged();
    }

    public void refreshControlsList(){
        llMain.setVisibility(View.GONE);
        llControls.setVisibility(View.VISIBLE);

        userControls = new ArrayList<>();
        selectedUserControls = new ArrayList<>();

        if(((KV)spnType.getSelectedItem()).getKey().equals(CODES.USERSCONTROL_TARGET_USER)){

        }else if(((KV)spnType.getSelectedItem()).getKey().equals(CODES.USERSCONTROL_TARGET_USER_ROL)){
            userControls.add(new SimpleSeleccionRowModel(Funciones.generateCode(), CODES.USER_CONTROL_CREATEORDER, false));
            userControls.add(new SimpleSeleccionRowModel(Funciones.generateCode(), CODES.USER_CONTROL_ANULATEORDER, false));
            userControls.add(new SimpleSeleccionRowModel(Funciones.generateCode(), CODES.USER_CONTROL_CHARGE_ORDERS, false));
            userControls.add(new SimpleSeleccionRowModel(Funciones.generateCode(), CODES.USER_CONTROL_DISPATCHORDER, false));
            userControls.add(new SimpleSeleccionRowModel(Funciones.generateCode(), CODES.USER_CONTROL_MODIFYORDER, false));
            userControls.add(new SimpleSeleccionRowModel(Funciones.generateCode(), CODES.USER_CONTROL_PRINTORDERS, false));

            fs.collection(Tablas.generalUsers).document(license.getCODE())
                    .collection(Tablas.generalUsersUserControl)
                    .whereEqualTo(UserControlController.TARGET,CODES.USERSCONTROL_TARGET_USER_ROL)
                    .whereEqualTo(UserControlController.TARGETCODE, selectedObject.getId())
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {

                    if(querySnapshot!= null && querySnapshot.size()>0){
                        for(DocumentSnapshot doc : querySnapshot.getDocuments()){
                            UserControl uc = doc.toObject(UserControl.class);
                            for(SimpleSeleccionRowModel o: userControls){
                                if(uc.getCONTROL().equals(o.getName())){
                                    o.setCode(uc.getCODE());
                                    o.setChecked(true);
                                    selectedUserControls.add(o);
                                    break;
                                }
                            }
                        }
                    }

                    LinearLayoutManager manager = new LinearLayoutManager(AdminLicenseControls.this);
                    rvControls.setLayoutManager(manager);
                    SimpleSelectionRowAdapter ssra = new SimpleSelectionRowAdapter(AdminLicenseControls.this,userControls, selectedUserControls);
                    rvControls.setAdapter(ssra);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });

        }else if(((KV)spnType.getSelectedItem()).getKey().equals(CODES.USERSCONTROL_TARGET_COMPANY)){

        }
    }


    @Override
    public void onClick(Object obj) {
        if(obj instanceof SimpleRowModel){
            selectedObject = (SimpleRowModel)obj;
        }

    }




    public FirebaseFirestore getFs(){
        return fs;
    }

    public void fillSpinner(){
        ArrayList<KV> data = new ArrayList<>();
        data.add(new KV(CODES.USERSCONTROL_TARGET_USER, "User"));
        data.add(new KV(CODES.USERSCONTROL_TARGET_USER_ROL, "Rol"));
        data.add(new KV(CODES.USERSCONTROL_TARGET_COMPANY, "Company"));
        spnType.setAdapter(new ArrayAdapter<KV>(AdminLicenseControls.this,android.R.layout.simple_list_item_1, data));

    }
}
