package far.com.eatit;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import far.com.eatit.CloudFireStoreObjects.Devices;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.Token;
import far.com.eatit.CloudFireStoreObjects.Users;
import far.com.eatit.CloudFireStoreObjects.UsersDevices;
import far.com.eatit.Controllers.DevicesController;
import far.com.eatit.Controllers.LicenseController;
import far.com.eatit.Controllers.TokenController;
import far.com.eatit.Controllers.UsersController;
import far.com.eatit.Controllers.UsersDevicesController;
import far.com.eatit.DataBase.CloudFireStoreDB;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Interfases.AsyncExecutor;
import far.com.eatit.Interfases.FireBaseOK;
import far.com.eatit.UserMenu.MainUserMenu;
import far.com.eatit.Utils.Funciones;

public class Login extends AppCompatActivity implements OnFailureListener, FireBaseOK, AsyncExecutor {

    FirebaseFirestore db;
    LicenseController licenseController;
    DevicesController devicesController;
    UsersController usersController;

    Token tokenCargaInicial;
    Licenses license = null;
    Devices device = null;
    Users users = null;
    UsersDevices usersDevices=null;

    Dialog cargaInicialDialog;
    LinearLayout llProgressBar;
    EditText etUser, etPassword;
    TextView btnLogin;
    CardView btnAceptar;
    EditText etUserDialog, etKeyDialog;
    TextView tvMessageDialog, tvPhoneID;


    TextView tvMsgToken;
    EditText etToken;
    CardView btnOKToken;
    LinearLayout llProgressBarToken;
    Dialog tokenDialog;

    Users lastUser;
    Devices lastDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(getUngrantedPermissions().size()>0){
            requestPermissions(getUngrantedPermissions());
        }else{
            init();
            initDialog();
            if(Funciones.getPreferencesInt(Login.this, CODES.EXTRA_SECURITY_ERROR_CODE)>-1){
                int code = Funciones.getPreferencesInt(Login.this, CODES.EXTRA_SECURITY_ERROR_CODE);
                ((TextView)findViewById(R.id.tvErrorMsg)).setText(Funciones.gerErrorMessage(code));
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(licenseController != null && licenseController.getLicense() == null) {
            Snackbar.make(findViewById(R.id.root), "Realize una carga inicial", Snackbar.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        new MenuInflater(Login.this).inflate(R.menu.main_menu, menu);
        boolean loginBloqued = Funciones.getPreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED).equals("1");
        menu.findItem(R.id.token).setVisible(loginBloqued);
        menu.findItem(R.id.initialize).setVisible(!loginBloqued);
        return (super.onCreateOptionsMenu(menu));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.initialize:
                showCargaInicialDialog();
                return true;
            case R.id.token:
                showTokenDialog();
                return true;
            case R.id.configuration:
                goToConfiguration();
                return true;

        }
        return false;
    }


    @Override
    public void OnFireBaseEndContact(int code) {
        if(code == 1){
            Funciones.savePreferences(Login.this, CODES.PREFERENCE_LICENSE_CODE, license.getCODE());
            Funciones.savePreferences(Login.this, CODES.PREFERENCE_USERDEVICE_CODE, usersDevices.getCODE());

            if(tokenCargaInicial.isAutodelete()){
                TokenController.getInstance(Login.this).deleteToken(license.getCODE(), tokenCargaInicial.getCode());
            }
            Toast.makeText(Login.this, "Finalizado", Toast.LENGTH_LONG).show();
            endLoading();
            tvMessageDialog.setText("Finalizado");
            cargaInicialDialog.dismiss();
            recreate();

        }
        tokenCargaInicial = null;
        license = null;
        users = null;
        device = null;
        usersDevices = null;

    }


    @Override
    public void sendMessage(String message) {
        tvMessageDialog.setText(message);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        endLoading();
        setMessageCargaInicial(e.getMessage(),R.color.red_700);
    }

    @Override
    public void setMessage(String fechaActual) {


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = true;
        if(requestCode == 123){
            for(int result: grantResults){
                if(result == PackageManager.PERMISSION_DENIED){
                    granted = false;
                    break;
                }
            }
            if(granted){
                recreate();
            }else{
                finish();
            }
        }
    }


    public void init() {
        try {

            FirebaseApp.initializeApp(Login.this);
            db = FirebaseFirestore.getInstance();
            licenseController = LicenseController.getInstance(Login.this);
            devicesController = DevicesController.getInstance(Login.this);
            usersController = UsersController.getInstance(Login.this);



            btnLogin = findViewById(R.id.btnLogin);
            tvPhoneID = findViewById(R.id.tvPhoneID);
            etUser = findViewById(R.id.etUser);
            etPassword = findViewById(R.id.etPass);

        } catch (Exception e) {
            Snackbar.make(findViewById(R.id.root), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Funciones.getPreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED).equals("1")) {
                    AlertDialog a = new AlertDialog.Builder(Login.this).create();
                    a.setTitle("Alerta");
                    a.setMessage(Funciones.gerErrorMessage(Integer.parseInt(Funciones.getPreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED_REASON))));
                    a.show();
                } else {
                    login();
                }
            }
        });

        showPhoneID();
    }

    public void login() {
        try {
            findViewById(R.id.llProgress).setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);

            Licenses l = licenseController.getLicense();
            if(l != null){
                usersController.getUserFromFireBase(etUser.getText().toString(), etPassword.getText().toString())
                        .addOnSuccessListener(onSuccessListenerLogin).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                        btnLogin.setEnabled(true);
                        Snackbar.make(findViewById(R.id.root), e.getMessage().toString(), Snackbar.LENGTH_LONG).show();
                        return;
                    }
                });

            }else{
                btnLogin.setEnabled(true);
                findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                Snackbar.make(findViewById(R.id.root), Funciones.gerErrorMessage(CODES.CODE_LICENSE_NO_LICENSE), Snackbar.LENGTH_LONG).show();
                return;
            }

        } catch (Exception e) {
            Snackbar.make(findViewById(R.id.root), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }

    }



    public void showCargaInicialDialog() {
        cargaInicialDialog.show();
        Window window = cargaInicialDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void startLoading(){
        license = null;
        tvMessageDialog.setText("");
        llProgressBar.setVisibility(View.VISIBLE);
        etKeyDialog.setEnabled(false);
        etUserDialog.setEnabled(false);
        cargaInicialDialog.setCancelable(false);
        btnAceptar.setEnabled(false);
    }
    public void endLoading(){
        btnAceptar.setEnabled(true);
        llProgressBar.setVisibility(View.INVISIBLE);
        etKeyDialog.setEnabled(true);
        etUserDialog.setEnabled(true);
        cargaInicialDialog.setCancelable(true);
    }

    public void startLoadingToken(){
        String intentos = getTokenAttemps();
        tvMsgToken.setText("Intentos: "+intentos+"/3");
        llProgressBarToken.setVisibility(View.VISIBLE);
        etToken.setEnabled(false);
        tokenDialog.setCancelable(false);
        btnOKToken.setEnabled(false);
    }
    public void endLoadingToken(){

        llProgressBarToken.setVisibility(View.INVISIBLE);
        tokenDialog.setCancelable(true);
        String intentos = getTokenAttemps();
        if(Integer.parseInt(intentos) >= 3){
            btnOKToken.setEnabled(false);
            etToken.setEnabled(false);
            tvMsgToken.setText("Agoto el numero de intentos permitidos");
        }else{
            tvMsgToken.setText("Intentos: "+intentos+"/3");
            btnOKToken.setEnabled(true);
            etToken.setEnabled(true);
        }
    }

    public void setMessageCargaInicial(String message){
        setMessageCargaInicial(message, android.R.color.black);
    }
    public void setMessageCargaInicial(String message, int color){
        tvMessageDialog.setText(message);
        tvMessageDialog.setTextColor(getResources().getColor(color));
    }






    OnSuccessListener<QuerySnapshot> onSuccessListenerLogin = new OnSuccessListener<QuerySnapshot>() {

        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {

            if(querySnapshot == null || (querySnapshot!= null && querySnapshot.isEmpty())){
                btnLogin.setEnabled(true);
                findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                Snackbar.make(findViewById(R.id.root), "Error de autenticacion", Snackbar.LENGTH_LONG).show();
                return;
            }
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                if(document != null){
                    lastUser = document.toObject(Users.class);
                    if(!isValidUser(lastUser)){
                        btnLogin.setEnabled(true);
                        findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                        return;
                    }

                    DevicesController.getInstance(Login.this).getFindThisDeviceFromFireBase(licenseController.getLicense(), onSuccessDevice, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                            btnLogin.setEnabled(true);
                            Snackbar.make(findViewById(R.id.root), e.getMessage(), Snackbar.LENGTH_LONG).show();
                            return;
                        }
                    });
                    break;

                }else{
                    Snackbar.make(findViewById(R.id.root), "ERROR obteniendo Usuario", Snackbar.LENGTH_LONG).show();
                }
            }

            btnLogin.setEnabled(true);
            findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);

        }

    };


    public boolean validateDevice(Devices d){

        int code = devicesController.validateDevice(d);

        if(code == CODES.CODE_DEVICES_UNREGISTERED){
            Snackbar.make(findViewById(R.id.root), "Dispositivo no registrado. Contacte con el administrador", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(code == CODES.CODE_DEVICES_DISABLED){
            Snackbar.make(findViewById(R.id.root), "Dispositivo inactivo. Contacte con el administrador", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public boolean isValidUser(Users u){
        int code = usersController.validateUser(u);
        if(code != CODES.CODE_USERS_ENABLED){

            if(code == CODES.CODE_USERS_DISBLED){
                Snackbar.make(findViewById(R.id.root), "Usuario inactivo. Contacte con el administrador", Snackbar.LENGTH_LONG).show();
            }

            if(code == CODES.CODE_USERS_INVALID){
                Snackbar.make(findViewById(R.id.root), "Usuario deshabilitado. Contacte con el administrador", Snackbar.LENGTH_LONG).show();

            }
            return false;
        }

        return true;
    }

    public boolean validateUserCargaInicial(Users u){

        int code = (u != null)?usersController.validateUser(u):CODES.CODE_USERS_INVALID;

        if(code == CODES.CODE_USERS_INVALID || code == CODES.CODE_USERS_DISBLED) {
            setMessageCargaInicial(Funciones.gerErrorMessage(code), R.color.red_700);
            endLoading();
            return false;
        }

        return true;
    }




    public void showPhoneID(){
        tvPhoneID.setText("Device: "+Funciones.getPhoneID(Login.this));
    }



    public int checkPermissions(String permission){
        int check = ContextCompat.checkSelfPermission(Login.this, permission);
        return check;
    }




    public void initDialog(){
        try {
            cargaInicialDialog = new Dialog(Login.this);
            cargaInicialDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            cargaInicialDialog.setContentView(R.layout.dialog_2edit_button);
            llProgressBar = cargaInicialDialog.findViewById(R.id.llProgress);
            etKeyDialog = cargaInicialDialog.findViewById(R.id.etKey);
            etUserDialog = cargaInicialDialog.findViewById(R.id.etUser);
            btnAceptar = cargaInicialDialog.findViewById(R.id.btnCargaInicial);
            tvMessageDialog = cargaInicialDialog.findViewById(R.id.tvMessage);
            btnAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (etKeyDialog.getText().toString().trim().isEmpty() || etUserDialog.getText().toString().trim().isEmpty()) {
                            setMessageCargaInicial("Debe llenar los campos KEY y USER");
                            return;
                        }
                        tokenCargaInicial = null;
                        startLoading();
                        Licenses tempLicense = new Licenses();
                        tempLicense.setCODE(etKeyDialog.getText().toString());

                        UsersDevicesController.getInstance(Login.this).getUserDeviceFromFireBase(tempLicense, etUserDialog.getText().toString(), Funciones.getPhoneID(Login.this), onSuccessUserDevice, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                setMessageCargaInicial(e.getMessage());
                                endLoading();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showTokenDialog(){
        try {
            tokenDialog = new Dialog(Login.this);
            tokenDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            tokenDialog.setContentView(R.layout.dialog_edit_button);
            tvMsgToken = tokenDialog.findViewById(R.id.tvMessage);
            etToken = tokenDialog.findViewById(R.id.etValue);
            btnOKToken = tokenDialog.findViewById(R.id.btnOK);
            llProgressBarToken = tokenDialog.findViewById(R.id.llProgress);
            etToken.setHint("Token");
            etToken.setText(Funciones.getCodeUserDevice(Login.this));
            String intentos = getTokenAttemps();
            if (Integer.parseInt(intentos) >= 3) {
                btnOKToken.setEnabled(false);
                etToken.setEnabled(false);
                tvMsgToken.setText("Agoto el numero de intentos permitidos");
            } else {
                tvMsgToken.setText("Intentos: " + intentos + "/3");
            }


            btnOKToken.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String token = etToken.getText().toString();
                    if (token.equals("")) {
                        return;
                    }
                    if (Integer.parseInt(getTokenAttemps()) >= 3) {
                        endLoadingToken();
                        return;
                    }
                    startLoadingToken();
                    TokenController.getInstance(Login.this).getTokenByCode(Funciones.getCodeUserDevice(Login.this), onSuccessTokenDesbloqueo, onFailureToken);
                }
            });

            tokenDialog.show();
            Window window = tokenDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public String getTokenAttemps(){
        String intentos = Funciones.getPreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED_TOKEN_ATTEMPS);
        if(intentos.equals(""))
            intentos = "0";
        return intentos;
    }


    public OnSuccessListener<DocumentSnapshot> LicenceListener = new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            license = null;
            if(documentSnapshot.exists()){
                    license = documentSnapshot.toObject(Licenses.class);
                    int code = licenseController.validateLicense(license);
                    String msg = ""; int color = R.color.red_700;
                    switch (code){
                        case  CODES.CODE_LICENSE_EXPIRED: msg =Funciones.gerErrorMessage(CODES.CODE_LICENSE_EXPIRED); endLoading(); break;
                        case  CODES.CODE_LICENSE_DISABLED: msg = Funciones.gerErrorMessage(CODES.CODE_LICENSE_DISABLED);  endLoading(); break;
                        case  CODES.CODE_LICENSE_VALID:
                            color = android.R.color.black;
                            DevicesController.getInstance(Login.this).getFindThisDeviceFromFireBase(license, onSuccessDeviceCargaInicial,Login.this);
                            break;
                        default:msg = Funciones.gerErrorMessage(CODES.CODE_LICENSE_INVALID);  endLoading(); break;
                    }
                    setMessageCargaInicial(msg, color);
                return;
            }else{
                setMessageCargaInicial(Funciones.gerErrorMessage(CODES.CODE_LICENSE_INVALID), R.color.red_700);
                endLoading();
            }
        }
    };
    public OnSuccessListener<QuerySnapshot> onSuccessUsersCargaInicial = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            users = null;
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                users = doc.toObject(Users.class);
            }

            if (validateUserCargaInicial(users)) {
                CloudFireStoreDB.getInstance(Login.this, Login.this, Login.this).CargaInicial(license);
                setMessageCargaInicial("Cargando datos...", android.R.color.black);
            }
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessDeviceCargaInicial = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            device = null;
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                device = doc.toObject(Devices.class);
                if(!device.isENABLED()){
                    setMessageCargaInicial(Funciones.gerErrorMessage(CODES.CODE_DEVICES_DISABLED), R.color.red_700);
                    endLoading();
                }else{
                    UsersController.getInstance(Login.this).getQueryUsersByCode(license,etUserDialog.getText().toString(),onSuccessUsersCargaInicial, onComplete,Login.this);
                }
            }else{
                setMessageCargaInicial(Funciones.gerErrorMessage(CODES.CODE_DEVICES_UNREGISTERED), R.color.red_700);
                endLoading();
            }
        }
    };



    OnSuccessListener<QuerySnapshot> onSuccessDevice = new OnSuccessListener<QuerySnapshot>() {

        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {

            if(querySnapshot == null || (querySnapshot != null && querySnapshot.isEmpty()) ){
                btnLogin.setEnabled(true);
                findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                Snackbar.make(findViewById(R.id.root), "Dispositivo no autorizado", Snackbar.LENGTH_LONG).show();
                return;
            }

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                if(document != null ){
                    lastDevice = document.toObject(Devices.class);

                    if(!validateDevice(lastDevice)){
                        lastUser = null;
                        btnLogin.setEnabled(true);
                        findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                        return;
                    }

                    /*UsersDevicesController.getInstance(Login.this).getUserDeviceFromFireBase(licenseController.getLicense(), lastUser.getCODE(), lastDevice.getCODE(), onSuccessUserDevice, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                            btnLogin.setEnabled(true);
                            Snackbar.make(findViewById(R.id.root), e.getMessage(), Snackbar.LENGTH_LONG).show();
                            return;
                        }
                    });*/

                    String lastLicenseCodeSaved = Funciones.getCodeLicense(Login.this);
                    String lastUserDeviceSaved = Funciones.getCodeUserDevice(Login.this);
                    String lastSavedMacAddress = Funciones.getMacAddress(Login.this);
                    if(lastLicenseCodeSaved.isEmpty()){
                        Snackbar.make(findViewById(R.id.root), "Realize una carga inicial. No se encontro licencia", Snackbar.LENGTH_LONG).show();
                    }else{
                        Funciones.clearPreference(Login.this);
                        Funciones.savePreferences(Login.this, CODES.PREFERENCE_LICENSE_CODE, lastLicenseCodeSaved);
                        Funciones.savePreferences(Login.this, CODES.PREFERENCE_USERDEVICE_CODE, lastUserDeviceSaved);
                        Funciones.savePreferences(Login.this, CODES.PREFERENCE_USERSKEY_CODE, lastUser.getCODE());
                        Funciones.savePreferences(Login.this, CODES.PREFERENCE_USERSKEY_USERTYPE, lastUser.getROLE());
                        Funciones.savePreferences(Login.this, CODES.PREFERENCE_BLUETOOTH_MAC_ADDRESS, lastSavedMacAddress);

                        UsersController.getInstance(Login.this).delete(null, null);
                        DevicesController.getInstance(Login.this).delete(null, null);

                        UsersController.getInstance(Login.this).insert(lastUser);
                        DevicesController.getInstance(Login.this).insert(lastDevice);
                        lastUser = null;
                        lastDevice=null;

                        ((TextView)findViewById(R.id.tvErrorMsg)).setText("");

                        Intent i = new Intent(Login.this, Main.class);
                        startActivity(i);
                    }



                }else{
                    Snackbar.make(findViewById(R.id.root), "ERROR obteniendo Device", Snackbar.LENGTH_LONG).show();
                }
            }

        }

    };



    OnSuccessListener<QuerySnapshot> onSuccessUserDevice = new OnSuccessListener<QuerySnapshot>() {

        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {

            usersDevices = null;
            if(querySnapshot == null || (querySnapshot != null && querySnapshot.isEmpty()) ){
               endLoading();
               setMessageCargaInicial("No se encontraron coincidencias");
               return;
            }

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                if(document != null){
                    usersDevices = document.toObject(UsersDevices.class);
                    TokenController.getInstance(Login.this).getTokenByCode(etKeyDialog.getText().toString(), usersDevices.getCODE(), onSuccessTokenCargaInicial, Login.this);
                }else{
                    endLoading();
                    setMessageCargaInicial("No se encontraron coincidencias");
                }
            }

        }

    };


   /* public OnSuccessListener<QuerySnapshot> onSuccessUsersDevicesCargaInicial = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                UsersDevices ud = doc.toObject(UsersDevices.class);
                CloudFireStoreDB.getInstance(Login.this, Login.this, Login.this).CargaInicial(license);
                setMessageCargaInicial("Cargando datos...", android.R.color.black);
                return;
            }
            setMessageCargaInicial("Este dispositivo no esta asociado al usuario", R.color.red_700);
            endLoading();
        }
    };*/


    public OnSuccessListener<QuerySnapshot> onSuccessTokenDesbloqueo = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                Token t = doc.toObject(Token.class);
                if(t.isAutodelete()){
                    TokenController.getInstance(Login.this).deleteFromFireBase(t);
                }

                Funciones.savePreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED, "");
                Funciones.savePreferences(Login.this, CODES.EXTRA_SECURITY_ERROR_CODE, -1);
                Funciones.savePreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED_TOKEN_ATTEMPS, "");
                Funciones.savePreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED_REASON, "");
                ((TextView)findViewById(R.id.tvErrorMsg)).setText("");
                tokenDialog.dismiss();
                recreate();
                return;
            }
            String intentos = getTokenAttemps();
            intentos = String.valueOf(Integer.parseInt(intentos)+1);
            Funciones.savePreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED_TOKEN_ATTEMPS, intentos);

            endLoadingToken();
        }
    };



    public OnSuccessListener<QuerySnapshot> onSuccessTokenCargaInicial = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            tokenCargaInicial = null;
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){

                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                tokenCargaInicial = doc.toObject(Token.class);
                if(tokenCargaInicial.getType().equals(CODES.TOKEN_TYPE_INITIAL_LOAD)){
                    licenseController.getDataFromFireBase(etKeyDialog.getText().toString(), LicenceListener, Login.this);
                }else{
                    endLoading();
                    setMessageCargaInicial("Solicite un token para carga inicial");
                }

            }else{
                endLoading();
                setMessageCargaInicial("Solicite un token para carga inicial");
            }

        }
    };



    public OnCompleteListener onComplete = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            //Fin de query
            if(task.getException() != null){
                tvMessageDialog.setText(task.getException().getMessage().toString());
                endLoading();
            }
        }
    };

    public OnFailureListener onFailureToken = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            tvMsgToken.setText(e.getMessage().toString());
        }
    };

    public void goToConfiguration(){
        startActivity(new Intent(Login.this, AdminConfiguration.class));
    }



    public ArrayList<String> getUngrantedPermissions(){
        ArrayList<String> p = new ArrayList<>();
        if(checkPermissions(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            p.add(Manifest.permission.INTERNET);
        }
        if(checkPermissions(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED){
            p.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if(checkPermissions(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            p.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            p.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(checkPermissions(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            p.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        /*if(checkPermissions(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            p.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }*/
        if(checkPermissions(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED){
            p.add(Manifest.permission.BLUETOOTH);
        }
        if(checkPermissions(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED){
            p.add(Manifest.permission.BLUETOOTH_ADMIN);
        }

        return p;
    }

    public void requestPermissions(ArrayList<String> permissions){
        ActivityCompat.requestPermissions(Login.this,
               /* new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN}*/permissions.toArray(new String[permissions.size()]),
                123);
    }

}