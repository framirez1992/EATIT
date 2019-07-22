package far.com.eatit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

import far.com.eatit.CloudFireStoreObjects.Devices;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.ProductsControl;
import far.com.eatit.CloudFireStoreObjects.UserInbox;
import far.com.eatit.CloudFireStoreObjects.Users;
import far.com.eatit.CloudFireStoreObjects.UsersDevices;
import far.com.eatit.Controllers.DevicesController;
import far.com.eatit.Controllers.LicenseController;
import far.com.eatit.Controllers.ProductsControlController;
import far.com.eatit.Controllers.UserInboxController;
import far.com.eatit.Controllers.UsersController;
import far.com.eatit.Controllers.UsersDevicesController;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Utils.Funciones;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    MaintenanceFragment fragmentMaintenance;
    LogoFragment logoFragment;
    LicenseController licenseController;
    UsersController usersController;
    DevicesController devicesController;
    UsersDevicesController usersDevicesController;
    UserInboxController userInboxController;
    ProductsControlController productsControlController;
    RelativeLayout rlNotifications;
    CardView cvNotificacions;
    TextView tvNotificationsNumber, tvTotalOrders;
    ImageView imgMenu;

    DrawerLayout drawer;
    NavigationView nav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        licenseController = LicenseController.getInstance(Main.this);
        userInboxController = UserInboxController.getInstance(Main.this);
        devicesController = DevicesController.getInstance(Main.this);
        productsControlController = ProductsControlController.getInstance(Main.this);
        usersController = UsersController.getInstance(Main.this);
        usersDevicesController = UsersDevicesController.getInstance(Main.this);

        rlNotifications = findViewById(R.id.rlNotifications);
        cvNotificacions = findViewById(R.id.cvNotifications);
        tvNotificationsNumber = findViewById(R.id.tvNotificationNumber);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nav = (NavigationView)findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);
        imgMenu = findViewById(R.id.imgMenu);

        imgMenu.setVisibility(View.VISIBLE);


        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (nav.isShown()) {
                        drawer.closeDrawer(nav);
                    } else {
                        drawer.openDrawer(nav);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        rlNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //callNotificationsDialog();
            }
        });



       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        setUpForUserType();


    }

    @Override
    protected void onStart() {
        super.onStart();

        productsControlController.getReferenceFireStore().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                try {
                    productsControlController.delete(null, null);
                    for (DocumentSnapshot doc : querySnapshot) {
                        ProductsControl pc = doc.toObject(ProductsControl.class);
                        productsControlController.insert(pc);

                    }
                }catch(Exception e1){
                    e1.printStackTrace();
                }
            }
        });

        licenseController.getReferenceFireStore().addSnapshotListener(licenceListener);
        usersController.getReferenceFireStore().addSnapshotListener(usersListener);
        devicesController.getReferenceFireStore(licenseController.getLicense()).addSnapshotListener(deviceListener);
        usersDevicesController.getReferenceFireStore(licenseController.getLicense()).addSnapshotListener(userDevicesListener);

      /*  userInboxController.getReferenceFireStore().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                try {
                    userInboxController.delete(null, null);
                    for (DocumentSnapshot doc : querySnapshot) {
                        UserInbox ui = doc.toObject(UserInbox.class);
                        if (ui.getCODEUSER().equals(Funciones.getPreferences(Main.this, CODES.PREFERENCE_USERS, CODES.PREFERENCE_USERSKEY_CODE))) {
                            userInboxController.insert(ui);
                        }

                    }
                    notityUnreadMessages();
                }catch(Exception e1){
                    e1.printStackTrace();
                }
            }
        });*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        licenseController.setLastUpdateToFireBase();//Actualiza la licencia
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.goMenu) {
            goToOrders();
        } else if (id == R.id.goPendingOrders) {
            goToOrdersBoard();
        } else if (id == R.id.goReports) {
            goToReports();

        } else if(id == R.id.goReceip){
            goToReceipts();
        }else if(id == R.id.goSavedReceipts){
            goToSavedReceipts();
        } else  {
            changeModule(id);
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

  public EventListener<QuerySnapshot> licenceListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {

            if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {

                   /* Licenses actualLicence = licenseController.getLicense();
                    if (actualLicence == null) {
                        Toast.makeText(Login.this, "Realize una carga inicial ", Toast.LENGTH_LONG).show();
                        return;
                    }*/
                Licenses lic = querySnapshot.getDocuments().get(0).toObject(Licenses.class);
                licenseController.delete(null, null);
                licenseController.insert(lic);
            }
            validateLicence(licenseController.getLicense());
        }
    };

    public EventListener<QuerySnapshot> usersListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {

            if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                usersController.delete(null, null);
                for(DocumentSnapshot doc: querySnapshot){
                    Users u = doc.toObject(Users.class);
                    usersController.insert(u);
                        //break;
                }
            }
            validateUser();
        }
    };

    public EventListener<QuerySnapshot> deviceListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {

            if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                devicesController.delete(null, null);
                for(DocumentSnapshot doc: querySnapshot){
                    Devices d = doc.toObject(Devices.class);
                    if(d.getCODE().equals(Funciones.getPhoneID(Main.this))) {
                        devicesController.insert(d);
                    }
                }
            }
            validateDevices();
        }
    };

    public EventListener<QuerySnapshot> userDevicesListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {

            boolean valid = false;
            if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                for(DocumentSnapshot doc: querySnapshot){
                    UsersDevices ud = doc.toObject(UsersDevices.class);
                    if(ud.getCODEDEVICE().equals(Funciones.getPhoneID(Main.this))
                            && ud.getCODEUSER().equals(Funciones.getCodeuserLogged(Main.this))){
                          valid = true;
                        break;
                    }
                }

               if(!valid){
                   startActivityLoginFromBegining(CODES.CODE_DEVICES_NOT_ASSIGNED_TO_USER);
               }
            }
        }
    };
    public void goToOrders(){

        startActivity(new Intent(Main.this, MainOrders.class));
    }
    public void goToOrdersBoard(){
        startActivity(new Intent(Main.this, OrderBoard.class));
    }
    public void goToReports(){
        startActivity(new Intent(Main.this, MainReports.class));
    }
    public void goToReceipts(){
        startActivity(new Intent(Main.this, MainReceipt.class));
    }
    public void goToSavedReceipts(){
        startActivity(new Intent(Main.this, MainReceiptsSaved.class));
    }

    public void notityUnreadMessages(){
        String where =UserInboxController.STATUS+" = ? ";
        String[] args = new String[]{CODES.CODE_USERINBOX_STATUS_NO_READ+""};
        String orderBy = UserInboxController.MDATE+" DESC, "+UserInboxController.CODEMESSAGE;
        ArrayList<UserInbox> msgs =  userInboxController.getUserInbox(where, args, orderBy);

        if(msgs.size()>0){
            cvNotificacions.setVisibility(View.VISIBLE);
            tvNotificationsNumber.setText(msgs.size()+"");
        }else{
            cvNotificacions.setVisibility(View.GONE);
            tvNotificationsNumber.setText("0");
        }
    }

    public boolean validateUser(){

        int code = usersController.validateUser(usersController.getUserByCode(Funciones.getCodeuserLogged(Main.this)));

        if(code == CODES.CODE_USERS_INVALID || code == CODES.CODE_USERS_DISBLED) {
           exitWithNoLoginCode(code);
            return false;
        }

        return true;
    }

    public boolean validateDevices(){

        int code = DevicesController.getInstance(Main.this).validateDevice();

        if(code == CODES.CODE_DEVICES_UNREGISTERED || code == CODES.CODE_DEVICES_DISABLED) {
            exitWithNoLoginCode(code);
            return false;
        }

        return true;
    }

    public void startActivityLoginFromBegining(int code){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            intent.putExtra(CODES.EXTRA_SECURITY_ERROR_CODE, code);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

    }

    public void setUpForUserType(){
        if(usersController.isSuperUser() || usersController.isAdmin()){//SU o Administrador
            nav.getMenu().findItem(R.id.goMantInventario).setVisible(true);
            nav.getMenu().findItem(R.id.goMantProductos).setVisible(true);
            nav.getMenu().findItem(R.id.goMantUsuarios).setVisible(true);
            nav.getMenu().findItem(R.id.goMantAreas).setVisible(true);
            nav.getMenu().findItem(R.id.goMantControls).setVisible(true);
            nav.getMenu().findItem(R.id.goReports).setVisible(true);

            nav.getMenu().findItem(R.id.goMenu).setVisible(usersController.isSuperUser());
            nav.getMenu().findItem(R.id.goPendingOrders).setVisible(usersController.isSuperUser());

            fragmentMaintenance = new MaintenanceFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.details, fragmentMaintenance);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }else {
            nav.getMenu().findItem(R.id.goMantInventario).setVisible(false);
            nav.getMenu().findItem(R.id.goMantProductos).setVisible(false);
            nav.getMenu().findItem(R.id.goMantUsuarios).setVisible(false);
            nav.getMenu().findItem(R.id.goMantAreas).setVisible(false);
            nav.getMenu().findItem(R.id.goMantControls).setVisible(false);
            nav.getMenu().findItem(R.id.goReports).setVisible(false);

            logoFragment = new LogoFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.details, logoFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

            ft.commit();
        }

        if(usersController.isUserRole(CODES.USERTYPE_CHEFF) || usersController.isUserRole(CODES.USERTYPE_BARTENDER)){
            nav.getMenu().findItem(R.id.goMenu).setVisible(false);
            nav.getMenu().findItem(R.id.goPendingOrders).setVisible(true);
        }else if(usersController.isUserRole(CODES.USERTYPE_MESERO)){
            nav.getMenu().findItem(R.id.goMenu).setVisible(true);
            nav.getMenu().findItem(R.id.goPendingOrders).setVisible(false);
        }


    }

    public void changeModule(int id){

        if((usersController.isSuperUser() || usersController.isAdmin())) {
            fragmentMaintenance.llMaintenanceInventory.setVisibility((id == R.id.goMantInventario) ? View.VISIBLE : View.GONE);
            fragmentMaintenance.llMaintenanceProducts.setVisibility((id == R.id.goMantProductos) ? View.VISIBLE : View.GONE);
            fragmentMaintenance.llMaintenanceUsers.setVisibility((id == R.id.goMantUsuarios) ? View.VISIBLE : View.GONE);
            fragmentMaintenance.llMaintenanceAreas.setVisibility((id == R.id.goMantAreas) ? View.VISIBLE : View.GONE);
            fragmentMaintenance.llMaintenanceControls.setVisibility((id == R.id.goMantControls) ? View.VISIBLE : View.GONE);
            fragmentMaintenance.llMainScreen.setVisibility((id == R.id.goMainScreen) ? View.VISIBLE : View.GONE);
        }


    }

    public boolean validateLicence(Licenses lic){

        int code = licenseController.validateLicense(lic);
        switch (code){
            //Validando vigencia de la licencia.
            case CODES.CODE_LICENSE_EXPIRED:
            case CODES.CODE_LICENSE_DISABLED:
            case CODES.CODE_LICENSE_NO_LICENSE:
                licenceListener = null;
                exitWithNoLoginCode(code);
                return false;

        }

        return true;
    }

/*
    public OnSuccessListener<QuerySnapshot> onSuccessUsers = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                Users u = doc.toObject(Users.class);
                UsersController.getInstance(Main.this).delete(null, null);
                UsersController.getInstance(Main.this).insert(u);
            }

            if(validateUser()){
                DevicesController.getInstance(Main.this).getQueryDevicesByCode(licenseController.getLicense(), Funciones.getPhoneID(Main.this), onSuccessDevice, null, null);
            }
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessDevice = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                Devices d = doc.toObject(Devices.class);
                DevicesController.getInstance(Main.this).delete(null, null);
                DevicesController.getInstance(Main.this).insert(d);
            }

            if(validateDevices()){
                UsersDevicesController.getInstance(Main.this).getQueryusersDevices(licenseController.getLicense(), Funciones.getCodeuserLogged(Main.this), Funciones.getPhoneID(Main.this), onSuccessUsersDevices, null, null);
            }
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessUsersDevices = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                return;
            }

                Toast.makeText(Main.this, Funciones.gerErrorMessage(CODES.CODE_DEVICES_NOT_ASSIGNED_TO_USER), Toast.LENGTH_LONG).show();
                Funciones.savePreferences(Main.this, CODES.PREFERENCE_LOGIN_BLOQUED, "1");
                Funciones.savePreferences(Main.this, CODES.PREFERENCE_LOGIN_BLOQUED_REASON, CODES.CODE_DEVICES_NOT_ASSIGNED_TO_USER+"");
                startActivityLoginFromBegining(CODES.CODE_DEVICES_NOT_ASSIGNED_TO_USER);

        }
    };*/
    public void exitWithNoLoginCode(int code){
        Toast.makeText(Main.this, Funciones.gerErrorMessage(code), Toast.LENGTH_LONG).show();
        Funciones.savePreferences(Main.this, CODES.PREFERENCE_LOGIN_BLOQUED, "1");
        Funciones.savePreferences(Main.this, CODES.PREFERENCE_LOGIN_BLOQUED_REASON, code+"");
        startActivityLoginFromBegining(code);
    }
}
