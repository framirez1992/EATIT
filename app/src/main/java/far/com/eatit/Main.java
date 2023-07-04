package far.com.eatit;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.bluetoothlibrary.BluetoothScan;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nullable;

import far.com.eatit.API.models.License;
import far.com.eatit.CloudFireStoreObjects.Devices;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.Token;
import far.com.eatit.CloudFireStoreObjects.UserInbox;
import far.com.eatit.CloudFireStoreObjects.Users;
import far.com.eatit.CloudFireStoreObjects.UsersDevices;
import far.com.eatit.Controllers.DevicesController;
import far.com.eatit.Controllers.LicenseController;
import far.com.eatit.Controllers.ProductsControlController;
import far.com.eatit.Controllers.TokenController;
import far.com.eatit.Controllers.UserControlController;
import far.com.eatit.Controllers.UserInboxController;
import far.com.eatit.Controllers.UsersController;
import far.com.eatit.Controllers.UsersDevicesController;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Utils.Funciones;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    License currentLicense;//for Admin

    Fragment currentFragment;
    MaintenanceFragment fragmentMaintenance;
    LogoFragment logoFragment;
    LicenseController licenseController;
    UsersController usersController;
    DevicesController devicesController;
    UsersDevicesController usersDevicesController;
    UserControlController userControlController;
    UserInboxController userInboxController;
    ProductsControlController productsControlController;
    RelativeLayout rlNotifications;
    CardView cvNotificacions;
    TextView tvNotificationsNumber, tvTotalOrders;
    ImageView imgMenu;

    DrawerLayout drawer;
    NavigationView nav;
    boolean exit;

    Dialog actualizationDialog;
    Dialog waitDialog;
    Dialog errorDialog;
    Dialog successDialog;

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
        userControlController = UserControlController.getInstance(Main.this);

        rlNotifications = findViewById(R.id.rlNotifications);
        cvNotificacions = findViewById(R.id.cvNotifications);
        tvNotificationsNumber = findViewById(R.id.tvNotificationNumber);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nav = (NavigationView) findViewById(R.id.nav_view);
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
                } catch (Exception e) {
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

        setInitialFragment();


    }




    private  void replace(Fragment fragment){
        currentFragment = fragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.details, fragment).commit();
    }

    public void setLoginFragment(){
        if(!(currentFragment instanceof LoginFragment)){
            replace(LoginFragment.newInstance(this));
        }

    }

    public void setLicenseFragment(){
        if(!(currentFragment instanceof AdminLicensesFragment)){
            replace(AdminLicensesFragment.newInstance(this));
        }

    }

    public void setCredentialsFragment(){
        if(!(currentFragment instanceof AdminCredentialsFragment)){
            replace(AdminCredentialsFragment.newInstance(this));
        }

    }

    public void setAdminLicenseSetupFragment(License l){
        currentLicense = l;
        if(!(currentFragment instanceof AdminLicenseSetupFragment)){
            replace(AdminLicenseSetupFragment.newInstance(this,l));
        }

    }

    public void setAdminLicenseTokens(License l){
        currentLicense = l;
        if(!(currentFragment instanceof AdminLicenseTokens)){
            replace(AdminLicenseTokens.newInstance(this,l));
        }
    }

    public void setAdminLicenseDevices(License l){
        currentLicense = l;
        if(!(currentFragment instanceof AdminLicenseDevices)){
            replace(AdminLicenseDevices.newInstance(this,l));
        }
    }

    public void setAdminLicenseUserRole(License l){
        currentLicense = l;
        if(!(currentFragment instanceof AdminLicenseUserRole)){
            replace(AdminLicenseUserRole.newInstance(this,l));
        }
    }

    public void setAdminLicenseUsers(License l){
        currentLicense = l;
        if(!(currentFragment instanceof AdminLicenseUsers)){
            replace(AdminLicenseUsers.newInstance(this,l));
        }
    }

    public void setAdminLicenseCompany(License l){
        currentLicense = l;
        if(!(currentFragment instanceof AdminLicenseCompany)){
            replace(AdminLicenseCompany.newInstance(this,l));
        }
    }

    public void setAdminLicenseUserDevice(License l){
        currentLicense = l;
        if(!(currentFragment instanceof AdminLicenseUserDevice)){
            replace(AdminLicenseUserDevice.newInstance(this,l));
        }
    }

    public void setMainMenuFragment(){
        if(!(currentFragment instanceof MainMenuFragment)){
            replace(MainMenuFragment.newInstance(this));
        }
    }

    public void setMaintenenceFragment(){
        if(!(currentFragment instanceof MaintenanceFragment)){
            replace(MaintenanceFragment.newInstance(this));
        }
    }

    public void setMaintenanceProductTypes(String type){
        if(!(currentFragment instanceof MaintenanceProductTypes)){
            replace(MaintenanceProductTypes.newInstance(this, type));
        }
    }

    public void setMaintenanceProductSubTypes(String type){
        if(!(currentFragment instanceof MaintenanceProductSubTypes)){
            replace(MaintenanceProductSubTypes.newInstance(this, type));
        }
    }

    public void setMaintenanceUnitMeasure(String type){
        if(!(currentFragment instanceof MaintenanceUnitMeasure)){
            replace(MaintenanceUnitMeasure.newInstance(this, type));
        }
    }

    public void setMaintenanceProducts(String type){
        if(!(currentFragment instanceof MaintenanceProducts)){
            replace(MaintenanceProducts.newInstance(this, type));
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        //licenseController.getReferenceFireStore().addSnapshotListener(this, licenceListener);
        //usersController.getReferenceFireStore().addSnapshotListener(this, usersListener);
        //devicesController.getReferenceFireStore(licenseController.getLicense()).addSnapshotListener(this, deviceListener);
        //usersDevicesController.getReferenceFireStore(licenseController.getLicense()).addSnapshotListener(this, userDevicesListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpForUserType();
        //licenseController.setLastUpdateToFireBase();//Actualiza la licencia
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(currentFragment instanceof AdminLicenseCompany || currentFragment instanceof AdminLicenseDevices ||
               currentFragment instanceof AdminLicenseTokens || currentFragment instanceof AdminLicenseUserRole ||
               currentFragment instanceof  AdminLicenseUsers || currentFragment instanceof AdminLicenseUserDevice){
                setAdminLicenseSetupFragment(currentLicense);
            }
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

        if(id == R.id.goActualization){
            goActualization();
        }else if (id == R.id.goMenu) {
            goToOrders();
        } else if (id == R.id.goPendingOrders) {
            goToOrdersBoard();
        } else if (id == R.id.goReports) {
            goToReports();

        } else if (id == R.id.goReceip) {
            goToReceipts();
        } else if (id == R.id.goSavedReceipts) {
            goToSavedReceipts();
        } else if (id == R.id.goConfiguration) {
            //startActivity(new Intent(this, BluetoothScan.class));
        } else {
            changeModule(id);
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public EventListener<QuerySnapshot> licenceListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {

            if(e != null){
                Toast.makeText(Main.this, e.getMessage()+" - "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                return;
            }
            Licenses lic = null;
            if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                licenseController.delete(null, null);
                for(DocumentSnapshot ds: querySnapshot){
                    lic = ds.toObject(Licenses.class);
                    if(lic.getCODE().equals(Funciones.getCodeLicense(Main.this))){
                        licenseController.insert(lic);
                    }
                }
            }
            validateLicence(lic);
        }
    };

    public EventListener<QuerySnapshot> usersListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
            if(e != null){
                Toast.makeText(Main.this, e.getMessage()+" - "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            Users u = null;
            if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                usersController.delete(null, null);

                for(DocumentSnapshot doc: querySnapshot){
                    u = doc.toObject(Users.class);
                    if(u.getCODE().equals(Funciones.getCodeuserLogged(Main.this))){
                        usersController.insert(u);
                        break;
                    }u = null;

                }
            }
            validateUser(u);
        }
    };

    public EventListener<QuerySnapshot> deviceListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {

            if(e != null){
                Toast.makeText(Main.this, e.getMessage()+" - "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                return;
            }
            Devices devices =null;
            if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                devicesController.delete(null, null);

                for(DocumentSnapshot doc: querySnapshot){
                    devices = doc.toObject(Devices.class);
                    if(devices.getCODE().equals(Funciones.getPhoneID(Main.this))) {
                        devicesController.insert(devices);
                        break;
                    }devices = null;
                }
            }
            validateDevices(devices);
        }
    };

    public EventListener<QuerySnapshot> userDevicesListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {

            if(e != null){
                Toast.makeText(Main.this, e.getMessage()+" - "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            boolean valid = false;
            if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                for(DocumentSnapshot doc: querySnapshot){
                    UsersDevices ud = doc.toObject(UsersDevices.class);
                    if(ud.getCODEDEVICE().equals(Funciones.getPhoneID(Main.this))
                            && ud.getCODEUSER().equals(Funciones.getCodeuserLogged(Main.this))){
                        valid = true;
                        break;
                    }ud = null;
                }
            }
            if(!valid){
                exitWithNoLoginCode(CODES.CODE_DEVICES_NOT_ASSIGNED_TO_USER);
            }
        }
    };



    public boolean validateUser(Users u) {

        int code = usersController.validateUser(u);

        if (code == CODES.CODE_USERS_INVALID || code == CODES.CODE_USERS_DISBLED) {
            exitWithNoLoginCode(code);
            return false;
        }

        return true;
    }

    public boolean validateDevices(Devices d) {

        int code = DevicesController.getInstance(Main.this).validateDevice(d);

        if (code == CODES.CODE_DEVICES_UNREGISTERED || code == CODES.CODE_DEVICES_DISABLED) {
            exitWithNoLoginCode(code);
            return false;
        }

        return true;
    }

    public void goActualization(){
        actualizationDialog = new Dialog(Main.this);
        actualizationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actualizationDialog.setContentView(R.layout.dialog_loading_1btn);
        actualizationDialog.findViewById(R.id.llProgress).setVisibility(View.VISIBLE);
        CardView btnOK = actualizationDialog.findViewById(R.id.btnOK);
        btnOK.setVisibility(View.INVISIBLE);
        btnOK.setEnabled(false);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizationDialog.dismiss();
                actualizationDialog = null;
            }
        });
        TokenController.getInstance(Main.this).getTokenByCode(Funciones.getCodeUserDevice(Main.this), new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                actualizationDialog.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);

                if(querySnapshot!= null && querySnapshot.size()>0){
                    Token t = querySnapshot.getDocuments().get(0).toObject(Token.class);
                    Intent i = new Intent(Main.this, MainActualizationCenter.class);
                    i.putExtra(CODES.EXTRA_TOKEN, t);
                    startActivity(i);
                    actualizationDialog.dismiss();
                    actualizationDialog=null;
                }else{
                    ((TextView)actualizationDialog.findViewById(R.id.tvMessage)).setText("Solicite un token de actualizacion");
                    actualizationDialog.findViewById(R.id.btnOK).setVisibility(View.VISIBLE);
                    actualizationDialog.findViewById(R.id.btnOK).setEnabled(true);
                }


            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                actualizationDialog.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                ((TextView)actualizationDialog.findViewById(R.id.tvMessage)).setText(e.getMessage());
                actualizationDialog.findViewById(R.id.btnOK).setVisibility(View.VISIBLE);
                actualizationDialog.findViewById(R.id.btnOK).setEnabled(true);
            }
        });
        actualizationDialog.setCancelable(false);
        actualizationDialog.show();
        Window window = actualizationDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void goToOrders() {

        startActivity(new Intent(Main.this, MainOrders.class));
    }

    public void goToOrdersBoard() {
        startActivity(new Intent(Main.this, MainOrderBoard.class));
    }

    public void goToReports() {
        startActivity(new Intent(Main.this, MainReports.class));
    }

    public void goToReceipts() {
        startActivity(new Intent(Main.this, MainReceipt.class));
    }

    public void goToSavedReceipts() {
        startActivity(new Intent(Main.this, MainReceiptsSaved.class));
    }


    public void notityUnreadMessages() {
        String where = UserInboxController.STATUS + " = ? ";
        String[] args = new String[]{CODES.CODE_USERINBOX_STATUS_NO_READ + ""};
        String orderBy = UserInboxController.MDATE + " DESC, " + UserInboxController.CODEMESSAGE;
        ArrayList<UserInbox> msgs = userInboxController.getUserInbox(where, args, orderBy);

        if (msgs.size() > 0) {
            cvNotificacions.setVisibility(View.VISIBLE);
            tvNotificationsNumber.setText(msgs.size() + "");
        } else {
            cvNotificacions.setVisibility(View.GONE);
            tvNotificationsNumber.setText("0");
        }
    }

    public boolean validateUser() {

        int code = usersController.validateUser(usersController.getUserByCode(Funciones.getCodeuserLogged(Main.this)));

        if (code == CODES.CODE_USERS_INVALID || code == CODES.CODE_USERS_DISBLED) {
            exitWithNoLoginCode(code);
            return false;
        }

        return true;
    }


    public void startActivityLoginFromBegining(int code) {
        Intent intent = new Intent(getApplicationContext(), LoginFragment.class);
        intent.putExtra(CODES.EXTRA_SECURITY_ERROR_CODE, code);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void setUpForUserType() {
        MenuItem mantenimientoInventario = nav.getMenu().findItem(R.id.goMantInventario);
        MenuItem mantenimientoProductos = nav.getMenu().findItem(R.id.goMantProductos);
        MenuItem mantenimientoUsuarios = nav.getMenu().findItem(R.id.goMantUsuarios);
        MenuItem mantenimientoAreas = nav.getMenu().findItem(R.id.goMantAreas);
        MenuItem mantenimientoControles = nav.getMenu().findItem(R.id.goMantControls);
        MenuItem crearOrdenes = nav.getMenu().findItem(R.id.goMenu);
        MenuItem despacharOrdenes = nav.getMenu().findItem(R.id.goPendingOrders);
        MenuItem facturar = nav.getMenu().findItem(R.id.goReceip);
        MenuItem recibos = nav.getMenu().findItem(R.id.goSavedReceipts);
        MenuItem reportes = nav.getMenu().findItem(R.id.goReports);

        mantenimientoInventario.setVisible(false);
        mantenimientoProductos.setVisible(false);
        mantenimientoUsuarios.setVisible(false);
        mantenimientoAreas.setVisible(false);
        mantenimientoControles.setVisible(false);
        crearOrdenes.setVisible(false);
        despacharOrdenes.setVisible(false);
        facturar.setVisible(false);
        recibos.setVisible(false);
        reportes.setVisible(false);

        if (usersController.isSuperUser() || usersController.isAdmin()) {//SU o Administrador
            //mantenimientoInventario.setVisible(usersController.isSuperUser());
            mantenimientoProductos.setVisible(usersController.isSuperUser());
            mantenimientoUsuarios.setVisible(usersController.isSuperUser());
            mantenimientoAreas.setVisible(usersController.isSuperUser());
            mantenimientoControles.setVisible(usersController.isSuperUser());

            crearOrdenes.setVisible(usersController.isSuperUser());
            despacharOrdenes.setVisible(usersController.isSuperUser());
            facturar.setVisible(usersController.isSuperUser());
            recibos.setVisible(true);
            reportes.setVisible(true);
        } else {
            if (UserControlController.getInstance(Main.this).dispatchOrders()) {
                nav.getMenu().findItem(R.id.goPendingOrders).setVisible(true);
            }
            if (UserControlController.getInstance(Main.this).createOrders()) {
                nav.getMenu().findItem(R.id.goMenu).setVisible(true);
            }
            if (UserControlController.getInstance(Main.this).chargeOrders()) {
                nav.getMenu().findItem(R.id.goReceip).setVisible(true);
            }


        }

    }

    public void setInitialFragment() {
        /*if (usersController.isSuperUser() || usersController.isAdmin()) {//SU o Administrador
            fragmentMaintenance = new MaintenanceFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.details, fragmentMaintenance);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        } else {
            logoFragment = new LogoFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.details, logoFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }*/
        setLoginFragment();
    }

    


    public void changeModule(int id) {

        if ((usersController.isSuperUser() || usersController.isAdmin())) {
            fragmentMaintenance.llMaintenanceInventory.setVisibility((id == R.id.goMantInventario) ? View.VISIBLE : View.GONE);
            fragmentMaintenance.llMaintenanceProducts.setVisibility((id == R.id.goMantProductos) ? View.VISIBLE : View.GONE);
            fragmentMaintenance.llMaintenanceUsers.setVisibility((id == R.id.goMantUsuarios) ? View.VISIBLE : View.GONE);
            fragmentMaintenance.llMaintenanceAreas.setVisibility((id == R.id.goMantAreas) ? View.VISIBLE : View.GONE);
            fragmentMaintenance.llMaintenanceControls.setVisibility((id == R.id.goMantControls) ? View.VISIBLE : View.GONE);
            fragmentMaintenance.llMainScreen.setVisibility((id == R.id.goMainScreen) ? View.VISIBLE : View.GONE);
        }


    }

    public boolean validateLicence(Licenses lic) {

        int code = licenseController.validateLicense(lic);
        switch (code) {
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


    public void exitWithNoLoginCode(int code) {
        if (!exit) {
            exit = true;
            Toast.makeText(Main.this, Funciones.gerErrorMessage(code), Toast.LENGTH_LONG).show();
            Funciones.savePreferences(Main.this, CODES.PREFERENCE_LOGIN_BLOQUED, "1");
            Funciones.savePreferences(Main.this, CODES.PREFERENCE_LOGIN_BLOQUED_REASON, code + "");
            //startActivityLoginFromBegining(code);
            setLoginFragment();
        }
    }


    public void showWaitingDialog(){
        if(waitDialog!=null){
            waitDialog.dismiss();
            waitDialog = null;
        }

        waitDialog = Funciones.getWaitDialog(Main.this);

        waitDialog.show();
    }

    public void dismissWaitingDialog(){
        if(waitDialog != null){
            waitDialog.dismiss();
        }
        waitDialog = null;
    }


    public void showErrorDialog(String title, String message){
        if(title == null){title = "";}
        if(message == null){message = "";}

        if(errorDialog!=null){
            errorDialog.dismiss();
            errorDialog = null;
        }

        errorDialog = Funciones.getErrorDialog(Main.this,R.color.red_900,title,message, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissErrorDialog();
            }
        });

        errorDialog.show();
    }

    public void dismissErrorDialog(){
        errorDialog.dismiss();
        errorDialog = null;
    }



    public void showErrorDialogAutoClose(String message, Runnable runnable){
        if(errorDialog!=null){
            errorDialog.dismiss();
            errorDialog = null;
        }

        int colorResource = R.color.red_900;
        String status = "ha ocurrido un error".toUpperCase();
        String msg = message;
        errorDialog = Funciones.getErrorDialogNoButtons(Main.this,colorResource,status,msg);
        errorDialog.show();
        final Timer timerError = new Timer();
        timerError.schedule(new TimerTask() {
            public void run() {
                timerError.cancel(); //this will cancel the timer of the system
                dismissErrorDialog();
                if(runnable != null){
                    runnable.run();
                }

            }
        }, 5000); // the timer will count 5 seconds....
    }

    public void dismissSuccessPaymentDialog(){
        successDialog.dismiss();
        successDialog = null;
    }


    public void showSuccessActionDialog(String message, Runnable runnable){
        if(successDialog!=null){
            successDialog.dismiss();
            successDialog = null;
        }
        successDialog = Funciones.getSucessActionDialog(Main.this,message);
        successDialog.show();
        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {
                if(runnable != null){
                    runnable.run();
                }
                dismissSuccessPaymentDialog();
                timer2.cancel(); //this will cancel the timer of the system
            }
        }, 5000); // the timer will count 5 seconds....
    }




}