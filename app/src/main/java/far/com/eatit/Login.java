package far.com.eatit;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import far.com.eatit.CloudFireStoreObjects.Devices;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.CloudFireStoreObjects.Token;
import far.com.eatit.CloudFireStoreObjects.Users;
import far.com.eatit.CloudFireStoreObjects.UsersDevices;
import far.com.eatit.Controllers.DevicesController;
import far.com.eatit.Controllers.LicenseController;
import far.com.eatit.Controllers.TokenController;
import far.com.eatit.Controllers.UsersController;
import far.com.eatit.Controllers.UsersDevicesController;
import far.com.eatit.DataBase.CloudFireStoreDB;
import far.com.eatit.Dialogs.MessageSendDialog;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Interfases.AsyncExecutor;
import far.com.eatit.Interfases.FireBaseOK;
import far.com.eatit.Utils.Funciones;

public class Login extends AppCompatActivity implements OnFailureListener, FireBaseOK, AsyncExecutor {

    FirebaseFirestore db;
    LicenseController licenseController;
    DevicesController devicesController;
    UsersController usersController;


    Licenses license = null;
    Dialog cargaInicialDialog;
    LinearLayout llProgressBar;
    EditText etUser, etPassword;
    Button btnLogin, btnAceptar;
    EditText etUserDialog, etKeyDialog;
    TextView tvMessageDialog, tvPhoneID;


    TextView tvMsgToken;
    EditText etToken;
    Button btnOKToken;
    LinearLayout llProgressBarToken;
    Dialog tokenDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        initDialog();
        if(getIntent().getExtras()!= null && getIntent().getExtras().containsKey(CODES.EXTRA_SECURITY_ERROR_CODE)){
            int code = getIntent().getExtras().getInt(CODES.EXTRA_SECURITY_ERROR_CODE);
            ((TextView)findViewById(R.id.tvErrorMsg)).setText(Funciones.gerErrorMessage(code));
        }
        //Funciones.getDateOnline(Login.this);
        //Sales s = new Sales();
        //s.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(licenseController.getLicense() == null) {
            Snackbar.make(findViewById(R.id.root), "Realize una carga inicial", Snackbar.LENGTH_LONG).show();
        }

        if(Funciones.getPreferencesInt(Login.this, CODES.PREFERENCE_SCREEN_HEIGHT) <=0){
            Funciones.saveScreenMetrics(Login.this);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
/*
            devicesController.getReferenceFireStore(licenseController.getLicense()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                   if(querySnapshot != null) {//los querySnapshot estan viniendo null en ocasiones. por eso se agrego esta condicion.
                       devicesController.delete(null, null);
                       for (DocumentSnapshot dc : querySnapshot) {
                           Devices dev = dc.toObject(Devices.class);
                           devicesController.insert(dev);
                       }
                       validateDevice();

                   }
                }
            });*/





    }



    public void startActivityLoginFromBegining(){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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

        }
        return false;
    }

    public void init() {
        try {
            db = FirebaseFirestore.getInstance();
            licenseController = LicenseController.getInstance(Login.this);
            devicesController = DevicesController.getInstance(Login.this);
            usersController = UsersController.getInstance(Login.this);



            btnLogin = findViewById(R.id.btnLogin);
            tvPhoneID = findViewById(R.id.tvPhoneID);
            etUser = findViewById(R.id.etUser);
            etPassword = findViewById(R.id.etPass);

        } catch (Exception e) {
            e.printStackTrace();
        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Funciones.getPreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED).equals("1")){
                    AlertDialog a = new AlertDialog.Builder(Login.this).create();
                    a.setTitle("Alerta");
                    a.setMessage(Funciones.gerErrorMessage(Integer.parseInt(Funciones.getPreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED_REASON))));
                    a.show();
                }else{
                    login();
                }

              /*  if(checkPermissions(Manifest.permission.SEND_SMS) == 1){
                    Funciones.sendSMS("8099983580", "hola vato");
                }else{
                    ActivityCompat.requestPermissions(Login.this,new String[]{Manifest.permission.SEND_SMS},1);
                }*/


                //AddUsers();
              /* if(Funciones.isNetDisponible(Main.this)){
                    if(Funciones.isOnlineNet()){
                        Funciones.getDateOnline(Main.this);
                    }else{
                        Toast.makeText(Main.this, "No hay conexion a internet", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(Main.this, "No es posible conectarse a la red", Toast.LENGTH_SHORT).show();
                }*/

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
               // if(!validateDevice()){
                //return;
                //}
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


          /*  DocumentReference user =
                    db.collection("users").document("ClienteTest")
                            .collection("Users").document(userCode);
            Task<DocumentSnapshot> ds = user.get();
            ds.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists() && documentSnapshot.getString("pass").equals(pass)) {

                        Toast.makeText(Main.this, "Correcto", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(Main.this, "Usuario o password invalido", Toast.LENGTH_LONG).show();
                    }
                }
            });
            ds.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Main.this, e.getMessage().toLowerCase(), Toast.LENGTH_LONG).show();
                }
            });*/






        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addLicense() {
        try {
            CloudFireStoreDB.getInstance(Login.this, Login.this, Login.this).crearNuevaEstructuraFireStore();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showCargaInicialDialog() {
        cargaInicialDialog.show();
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


    @Override
    public void OnFireBaseEndContact(int code) {
       if(code == 1){
           Toast.makeText(Login.this, "Finalizado", Toast.LENGTH_LONG).show();
           endLoading();
           tvMessageDialog.setText("Finalizado");
           cargaInicialDialog.dismiss();
           //Funciones.getDateOnline(Login.this);
           recreate();

       }
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

    OnSuccessListener<QuerySnapshot> onSuccessListenerLogin = new OnSuccessListener<QuerySnapshot>() {

        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {

            if(querySnapshot == null || querySnapshot.isEmpty()){
                btnLogin.setEnabled(true);
                findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                Snackbar.make(findViewById(R.id.root), "Error de autenticacion", Snackbar.LENGTH_LONG).show();
                return;
            }
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                if(document != null){
                   Users u = document.toObject(Users.class);
                   usersController.delete(null, null);
                   usersController.insert(u);

                  if(!isValidUser(u)){
                      btnLogin.setEnabled(true);
                      findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                     return;
                  }

                    String codeUser = u.getCODE();
                    Funciones.clearPreference(Login.this);
                    Funciones.savePreferences(Login.this, CODES.PREFERENCE_USERSKEY_CODE, codeUser);
                    Funciones.savePreferences(Login.this, CODES.PREFERENCE_USERSKEY_USERTYPE, UsersController.getInstance(Login.this).getUserByCode(codeUser).getROLE());
                    ((TextView)findViewById(R.id.tvErrorMsg)).setText("");

                    Intent i = new Intent(Login.this, Main.class);
                    startActivity(i);

                }else{
                    Snackbar.make(findViewById(R.id.root), "ERROR obteniendo Usuario", Snackbar.LENGTH_LONG).show();
                }
            }

            btnLogin.setEnabled(true);
            findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);

        }

    };


    public boolean validateDevice(){

        int code = devicesController.validateDevice();

        if(code == CODES.CODE_DEVICES_UNREGISTERED){
            Toast.makeText(Login.this, "Dispositivo no registrado. Contacte con el administrador", Toast.LENGTH_LONG).show();
            startActivityLoginFromBegining();
            return false;
        }

        if(code == CODES.CODE_DEVICES_DISABLED){
            Toast.makeText(Login.this, "Dispositivo inactivo. Contacte con el administrador", Toast.LENGTH_LONG).show();
            startActivityLoginFromBegining();
            return false;
        }

        return true;
    }

    public boolean isValidUser(Users u){
        int code = usersController.validateUser(u);
        if(code != CODES.CODE_USERS_ENABLED){

            if(code == CODES.CODE_USERS_DISBLED){
                Toast.makeText(Login.this, "Usuario inactivo. Contacte con el administrador", Toast.LENGTH_LONG).show();
            }

            if(code == CODES.CODE_USERS_INVALID){
                Toast.makeText(Login.this, "Usuario deshabilitado. Contacte con el administrador", Toast.LENGTH_LONG).show();

            }
            return false;
        }

        return true;
    }

    public boolean validateUserCargaInicial(Users u){

        int code = (u != null)?usersController.validateUser(usersController.getUserByCode(u.getCODE())):CODES.CODE_USERS_INVALID;

        if(code == CODES.CODE_USERS_INVALID || code == CODES.CODE_USERS_DISBLED) {
            setMessageCargaInicial(Funciones.gerErrorMessage(code), R.color.red_700);
            endLoading();
            return false;
        }

        return true;
    }




    @Override
    public void setMessage(String fechaActual) {
        /* if(fechaActual != CODES.CODE_ERROR_GET_INTERNET_DATE){//HAY INTERNET
            licenseController.updateLicenciaDiaria(fechaActual);
            //licenseController.setAlarm(fechaActual,7,30);
        }else{//SIN INTERNET
            Toast.makeText(Login.this, "Error obteniendo fecha del servidor", Toast.LENGTH_LONG).show();
        }*/

    }
    public void showPhoneID(){
        tvPhoneID.setText("Device: "+Funciones.getPhoneID(Login.this));
    }
    public void agregarAlServer(){
        /*
        set without merge will overwrite a document or create it if it doesn't exist yet

        set with merge will update fields in the document or create it if it doesn't exists

        update will update fields but will fail if the document doesn't exist

        create will create the document but fail if the document already exists

        xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        CollectionReference cities = db.collection("cities");
        List<ApiFuture<WriteResult>> futures = new ArrayList<>();
        futures.add(cities.document("SF").set(new City("San Francisco", "CA", "USA", false, 860000L)));
        futures.add(cities.document("LA").set(new City("Los Angeles", "CA", "USA", false, 3900000L)));
        futures.add(cities.document("DC").set(new City("Washington D.C.", null, "USA", true, 680000L)));
        futures.add(cities.document("TOK").set(new City("Tokyo", null, "Japan", true, 9000000L)));
        futures.add(cities.document("BJ").set(new City("Beijing", null, "China", true, 21500000L)));
// (optional) block on documents successfully added
        ApiFutures.allAsList(futures).get();

        xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        Para crear o sobrescribir un solo documento, usa el método set():
        Si el documento no existe, se creará. Si el documento existe, su contenido se sobrescribirá con los datos proporcionados,
        a menos que especifiques que los datos se deberían combinar en el documento existente, de la siguiente manera:


        // The option to merge data is not yet available for Java. Instead, call the
        // update method and pass the option to create the document if it's missing.

        //asynchronously update doc, create the document if missing
        Map<String, Object> update = new HashMap<>();
        update.put("capital", true);

        ApiFuture<WriteResult> writeResult =
            db
                .collection("cities")
                .document("BJ")
                .set(update, SetOptions.merge());
        // ...
        System.out.println("Update time : " + writeResult.get().getUpdateTime());


        xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

        */
    }
    public void filtroConWhere(){
        /*
        // Create a reference to the cities collection
        CollectionReference cities = db.collection("cities");
// Create a query against the collection.
        Query query = cities.whereEqualTo("state", "CA");
// retrieve  query results asynchronously using query.get()
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            System.out.println(document.getId());

          xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
El método where() usa tres parámetros: un campo para filtrar, una operación de comparación y un valor.
La comparación puede ser <, <=, ==, > o >=. Para iOS, Android y Java, el operador de comparación se
nombra de forma explícita en el método.Estos son algunos filtros de ejemplo:

Query countryQuery = cities.whereEqualTo("state", "CA");
Query populationQuery = cities.whereLessThan("population", 1000000L);
Query cityQuery = cities.whereGreaterThanOrEqualTo("name", "San Francisco");


También puedes encadenar varios métodos where() para crear consultas más específicas (AND lógica).
Sin embargo, para combinar el operador de igualdad (==) con una comparación de rangos (<, <=, > o >=),
asegúrate de crear un índice personalizado.

Query chainedQuery1 = cities.whereEqualTo("state", "CO")
    .whereEqualTo("name", "Denver");
Query chainedQuery2 = cities.whereEqualTo("state", "CA")
    .whereLessThan("population", 1000000L);



    Limitaciones de las consultas
Cloud Firestore no admite los siguientes tipos de consultas:

Consultas con filtros de rango en diferentes campos.
Consultas únicas que se ejecutan en varias colecciones o subcolecciones. Cada consulta se ejecuta en una sola colección de documentos.
Consultas de miembros individuales de un arreglo.
Consultas con el operador lógico OR. En este caso, deberías crear una consulta independiente para cada condición de OR y combinar los resultados de la consulta en tu app.
Consultas con una cláusula !=. En este caso, deberías dividir la consulta en una de tipo "mayor que" y otra de tipo "menor que". Por ejemplo, pese a que no se admite la cláusula de consulta where("age", "!=", "30"), puedes obtener los mismos resultados si combinas dos consultas: una con la cláusula where("age", "<", "30") y otra con la cláusula where("age", ">", 30).

        }*/
    }

    public void colocandoMarcaDeTiempo(){
        /*
        DocumentReference docRef = db.collection("objects").document("some-id");
// Update the timestamp field with the value from the server
        ApiFuture<WriteResult> writeResult = docRef.update("timestamp", FieldValue.serverTimestamp());
        System.out.println("Update time : " + writeResult.get());*/
    }

    public void Transaccciones(){
        /*
       Transacciones
Cloud Firestore admite operaciones atómicas para la lectura y la escritura de datos.
En un conjunto de operaciones atómicas, todas las operaciones se aplican de manera correcta o no se aplica ninguna de ellas.
Existen dos tipos de operaciones atómicas en Cloud Firestore:

Transacciones: una transacción es un conjunto de operaciones de lectura y de escritura en uno o más documentos.
Escrituras en lotes: una escritura en lotes es un conjunto de operaciones de escritura en uno o más documentos.
Cada transacción o escritura en lote puede escribir en un máximo de 500 documentos.


Actualizar datos con transacciones
Con las bibliotecas cliente de Cloud Firestore, puedes agrupar varias operaciones en una sola transacción.
Las transacciones son útiles cuando quieres actualizar el valor de un campo según su valor actual, o el valor de algún otro campo.
Podrías aumentar un contador con una transacción que lea el valor actual del contador, lo aumente y escriba el valor nuevo en Cloud Firestore.

Una transacción se compone de cualquier número de operaciones get() seguida de cualquier número de operaciones de escritura, como set(), update() o delete().
En caso de una edición simultánea, Cloud Firestore vuelve a ejecutar la transacción completa. Por ejemplo, si una transacción lee documentos y
otro cliente modifica cualquiera de esos documentos, Cloud Firestore vuelve a intentar la transacción.
Esta característica garantiza que la transacción se ejecute en datos coherentes y actualizados.

Las transacciones nunca aplican escrituras de forma parcial. Todas las escrituras se ejecutan al final de una transacción correcta.

Cuando uses transacciones, ten en cuenta lo siguiente:
Las operaciones de escritura se deben ejecutar antes de las operaciones de escritura.
Una función que llama a una transacción (función de transacción) se podría ejecutar más de una vez si una edición simultánea afecta a un documento que la transacción lee.
Las funciones de transacción no deberían modificar el estado de la aplicación directamente.
Las transacciones fallarán cuando el cliente se encuentre sin conexión.
El siguiente ejemplo muestra cómo crear y ejecutar una transacción:


// Initialize doc
final DocumentReference docRef = db.collection("cities").document("SF");
City city = new City("SF");
city.setCountry("USA");
city.setPopulation(860000L);
docRef.set(city).get();

// run an asynchronous transaction
ApiFuture<Void> transaction =
    db.runTransaction(
        new Transaction.Function<Void>() {
          @Override
          public Void updateCallback(Transaction transaction) throws Exception {
            // retrieve document and increment population field
            DocumentSnapshot snapshot = transaction.get(docRef).get();
            long oldPopulation = snapshot.getLong("population");
            transaction.update(docRef, "population", oldPopulation + 1);
            return null;
          }
        });
// block on transaction operation using transaction.get()

xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
Pasar información desde las transacciones
No modifiques el estado de la aplicación dentro de las funciones de transacción.
Esto generará problemas de simultaneidad, debido a que las funciones de transacción pueden ejecutarse varias veces
y no se garantiza que se ejecuten en el procesamiento de IU. En vez de esto, pasa la información que necesites desde
tus funciones de transacción. El siguiente ejemplo se basa el ejemplo anterior para mostrarte cómo pasar información desde una transacción:


final DocumentReference docRef = db.collection("cities").document("SF");
ApiFuture<String> transaction =
    db.runTransaction(
        new Transaction.Function<String>() {
          @Override
          public String updateCallback(Transaction transaction) throws Exception {
            DocumentSnapshot snapshot = transaction.get(docRef).get();
            Long newPopulation = snapshot.getLong("population") + 1;
            // conditionally update based on current population
            if (newPopulation <= 1000000L) {
              transaction.update(docRef, "population", newPopulation);
              return "Population increased to " + newPopulation;
            } else {
              throw new Exception("Sorry! Population is too big.");
            }
          }
        });
// Print information retrieved from transaction
System.out.println(transaction.get());

xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
Error de transacción
Una transacción puede fallar por los siguientes motivos:

La transacción contiene operaciones de lectura después de las operaciones de escritura. Las operaciones de lectura deben ejecutarse siempre antes de cualquier operación de escritura.
La transacción lee un documento que se modificó fuera de la transacción. En este caso, la transacción vuelve a ejecutarse automáticamente. La transacción se vuelve a intentar una cantidad infinita de veces.
Una transacción errónea muestra un error y no escribe en la base de datos. No es necesario que reviertas la transacción; Cloud Firestore lo hace automáticamente.


         */
    }


    public void EscrituraEnLotes(){
      /*
      Cloud Firestore admite operaciones atómicas para la lectura y la escritura de datos.
En un conjunto de operaciones atómicas, todas las operaciones se aplican de manera correcta o no se aplica ninguna de ellas.
Existen dos tipos de operaciones atómicas en Cloud Firestore:

Transacciones: una transacción es un conjunto de operaciones de lectura y de escritura en uno o más documentos.
Escrituras en lotes: una escritura en lotes es un conjunto de operaciones de escritura en uno o más documentos.
Cada transacción o escritura en lote puede escribir en un máximo de 500 documentos.

      Escrituras en lotes
        Si no necesitas leer documentos en tu conjunto de operaciones, puedes ejecutar varias operaciones de escritura
        como un lote único que contiene cualquier combinación de operaciones set(), update() o delete().
                Un lote de escrituras se completa de forma atómica y puede escribir en varios documentos.

        Las escrituras en lotes también son útiles para migrar conjuntos de datos de gran tamaño a Cloud Firestore.
        Una escritura en lote puede contener hasta 500 operaciones y la agrupación de operaciones en lotes reduce el gasto
        de funcionamiento de conexión, lo que da como resultado una migración de datos más rápida.

        Las escrituras en lotes tienen menos casos de errores que las transacciones y usan un código más simple.
        No se ven afectadas por problemas de contención, ya que no dependen de la lectura constante de documentos.
                Las escrituras en lotes se ejecutan incluso cuando el dispositivo del usuario está sin conexión.
        El siguiente ejemplo muestra cómo compilar y confirmar un lote de escrituras:

// Get a new write batch
        WriteBatch batch = db.batch();
// Set the value of 'NYC'
        DocumentReference nycRef = db.collection("cities").document("NYC");
        batch.set(nycRef, new City());

// Update the population of 'SF'
        DocumentReference sfRef = db.collection("cities").document("SF");
        batch.update(sfRef, "population", 1000000L);

// Delete the city 'LA'
        DocumentReference laRef = db.collection("cities").document("LA");
        batch.delete(laRef);

// asynchronously commit the batch
        ApiFuture<List<WriteResult>> future = batch.commit();
// ...
// future.get() blocks on batch commit operation
        for (WriteResult result :future.get()) {
            System.out.println("Update time : " + result.getUpdateTime());
        }

        xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

        Validación de datos para operaciones atómicas
        Para las bibliotecas clientes móviles/web, puedes validar los datos con las Reglas de seguridad de Cloud Firestore.
                Puedes asegurarte de que los documentos relacionados estén siempre actualizados de forma atómica y que siempre sean parte de una
        transacción o escritura en lotes. Usa la función de reglas de seguridad getAfter() para acceder y validar el estado de un documento
        después de se complete un conjunto de operaciones, pero antes de que Cloud Firestore confirme las operaciones.

        Por ejemplo, imagina que la base de datos del ejemplo cities también contiene una colección countries.
                Cada documento country usa un campo last_updated para hacer un seguimiento de la última vez que se actualizó una ciudad relacionada con ese país.
                Las siguientes reglas de seguridad requieren que la actualización de un documento city también actualice de forma atómica el campo last_updated del país relacionado:

        service cloud.firestore {
            match /databases/{database}/documents {
                // If you update a city doc, you must also
                // update the related country's last_updated field.
                match /cities/{city} {
                    allow write: if request.auth.uid != null &&
                            getAfter(
                                    /databases/$(database)/documents/countries/$(request.resource.data.country)
        ).data.last_updated == request.time;
                }

                match /countries/{country} {
                    allow write: if request.auth.uid != null;
                }
            }
        }*/
    }

    public int checkPermissions(String permission){
        int check = ContextCompat.checkSelfPermission(Login.this, permission);
        return check;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){

            if (grantResults.length > 0) {

                boolean granted = true;
                for (int grantResult : grantResults){
                    if (grantResult == PackageManager.PERMISSION_DENIED){
                        granted = false;
                    }
                }
            if(granted){
                //Funciones.sendSMS("8099983580", "hola vato");
            }else {
               Toast.makeText(Login.this, "Denegado", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void initDialog(){
        cargaInicialDialog = new Dialog(Login.this);
        cargaInicialDialog.setContentView(R.layout.dialog_2edit_button);
        llProgressBar = cargaInicialDialog.findViewById(R.id.llProgress);
        etKeyDialog = cargaInicialDialog.findViewById(R.id.etKey);
        etUserDialog =cargaInicialDialog.findViewById(R.id.etUser);
                btnAceptar = cargaInicialDialog.findViewById(R.id.btnCargaInicial);
        tvMessageDialog = cargaInicialDialog.findViewById(R.id.tvMessage);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(etKeyDialog.getText().toString().trim().isEmpty() || etUserDialog.getText().toString().trim().isEmpty()){
                        setMessageCargaInicial("Debe llenar los campos KEY y USER");
                        return;
                    }
                    startLoading();
                    licenseController.getDataFromFireBase(etKeyDialog.getText().toString(), LicenceListener, Login.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showTokenDialog(){
        tokenDialog = new Dialog(Login.this);
        tokenDialog.setContentView(R.layout.dialog_edit_button);
        tvMsgToken = tokenDialog.findViewById(R.id.tvMessage);
        etToken = tokenDialog.findViewById(R.id.etValue);
        btnOKToken = tokenDialog.findViewById(R.id.btnOK);
        llProgressBarToken = tokenDialog.findViewById(R.id.llProgress);
        etToken.setHint("Token");
        etToken.setText("");
        String intentos = getTokenAttemps();
        if(Integer.parseInt(intentos) >= 3){
            btnOKToken.setEnabled(false);
            etToken.setEnabled(false);
            tvMsgToken.setText("Agoto el numero de intentos permitidos");
        }else{
            tvMsgToken.setText("Intentos: "+intentos+"/3");
        }


        btnOKToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = etToken.getText().toString();
                if(token.equals("")){
                    return;
                }
                if(Integer.parseInt(getTokenAttemps()) >= 3){
                    endLoadingToken();
                    return;
                }
               startLoadingToken();
               TokenController.getInstance(Login.this).getQueryTokenByCode(token, onSuccessToken, onCompleteToken, onFailureToken);
            }
        });

        tokenDialog.show();

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
            if(documentSnapshot.exists()){
                try {
                    license = documentSnapshot.toObject(Licenses.class);
                    licenseController.delete(null, null);
                    licenseController.insert(license);
                    int code = licenseController.validateLicense(license);
                    String msg = ""; int color = R.color.red_700;
                    switch (code){
                        case  CODES.CODE_LICENSE_EXPIRED: msg =Funciones.gerErrorMessage(CODES.CODE_LICENSE_EXPIRED); endLoading(); break;
                        case  CODES.CODE_LICENSE_DISABLED: msg = Funciones.gerErrorMessage(CODES.CODE_LICENSE_DISABLED);  endLoading(); break;
                        case  CODES.CODE_LICENSE_VALID:
                            color = android.R.color.black;
                            UsersController.getInstance(Login.this).getQueryUsersByCode(license,etUserDialog.getText().toString(),onSuccessUsers, onComplete,Login.this);
                            break;
                        default:msg = Funciones.gerErrorMessage(CODES.CODE_LICENSE_INVALID);  endLoading(); break;
                    }
                    setMessageCargaInicial(msg, color);
                }catch(Exception e){
                    e.printStackTrace();
                }
                return;
            }else{
                setMessageCargaInicial(Funciones.gerErrorMessage(CODES.CODE_LICENSE_INVALID), R.color.red_700);
                endLoading();
            }
        }
    };
    public OnSuccessListener<QuerySnapshot> onSuccessUsers = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            Users u = null;
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                u = doc.toObject(Users.class);
                usersController.delete(null, null);
                usersController.insert(u);
                //devicesController.getDevices(license, DevicesListener);
            }

            if (validateUserCargaInicial(u)) {
                UsersDevicesController.getInstance(Login.this).getQueryusersDevices(license,u.getCODE(),Funciones.getPhoneID(Login.this),onSuccessUsersDevices,onComplete,Login.this);
            }
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessUsersDevices = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                UsersDevices ud = doc.toObject(UsersDevices.class);
                devicesController.getDevices(license, DevicesListener);
                return;
            }
           setMessageCargaInicial("Este dispositivo no esta asociado al usuario", R.color.red_700);
           endLoading();
        }
    };

    public OnSuccessListener<QuerySnapshot> DevicesListener = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            boolean registrado = false;
            boolean enabled = true;
            int devicesCount = querySnapshot.getDocuments().size();//registrados
            String phoneID = Funciones.getPhoneID(Login.this);
            for(DocumentSnapshot doc : querySnapshot){
                Devices dev = doc.toObject(Devices.class);
                if(dev.getCODE().equals(phoneID)){
                    enabled = dev.isENABLED();
                    registrado = true;
                    break;
                }
            }

            if(registrado && !enabled){//Dispositivo registrado e inactivo
                setMessageCargaInicial("Dispositivo inactivo. contacte con el administrador.", R.color.red_700);
                endLoading();
                return;
            }

            if (!registrado && (devicesCount >= license.getDEVICES())) {
                setMessageCargaInicial(Funciones.gerErrorMessage(CODES.CODE_LICENSE_DEVICES_LIMIT_REACHED),  R.color.red_700);
                endLoading();
                return;
            } else if (registrado || (!registrado && (devicesCount < license.getDEVICES()))) {
                boolean registerDevice =  (!registrado && (devicesCount < license.getDEVICES()));
                CloudFireStoreDB.getInstance(Login.this, Login.this, Login.this).CargaInicial(license,  registerDevice);
                setMessageCargaInicial("Cargando datos...", android.R.color.black);
            }
        }
    };


    public OnSuccessListener<QuerySnapshot> onSuccessToken = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                Token t = doc.toObject(Token.class);
                Funciones.savePreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED, "");
                Funciones.savePreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED_TOKEN_ATTEMPS, "");
                Funciones.savePreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED_REASON, "");

                Licenses actualLicence = LicenseController.getInstance(Login.this).getLicense();
                LicenseController.getInstance(Login.this).getQueryLicenceByCode(actualLicence.getCODE(), onSuccessLicence, onCompleteToken, onFailureToken);
                //TokenController.getInstance(Login.this).deleteFromFireBase(t);
                return;
            }
            String intentos = getTokenAttemps();
            intentos = String.valueOf(Integer.parseInt(intentos)+1);
            Funciones.savePreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED_TOKEN_ATTEMPS, intentos);

            endLoadingToken();
            //setMessageCargaInicial("Invalid User", R.color.red_700);
            //endLoading();
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessLicence = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                Licenses l = doc.toObject(Licenses.class);
                LicenseController.getInstance(Login.this).delete(null, null);
                LicenseController.getInstance(Login.this).insert(l);
                recreate();
                return;
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

    public OnCompleteListener onCompleteToken = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            //Fin de query
            if(task.getException() != null){
                tvMsgToken.setText(task.getException().getMessage().toString());
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

}