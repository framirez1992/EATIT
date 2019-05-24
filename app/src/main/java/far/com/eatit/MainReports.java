package far.com.eatit;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.SimpleFormatter;

import javax.annotation.Nullable;

import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Interfases.FireBaseOK;
import far.com.eatit.Utils.Funciones;

public class MainReports extends AppCompatActivity implements OnFailureListener,OnCompleteListener,OnCanceledListener, OnSuccessListener<QuerySnapshot>, DatePickerDialog.OnDateSetListener{

    LinearLayout llVentas, llDevoluciones;
    Spinner spnReporte;
    Button btnDesde, btnHasta;
    RecyclerView rvList;
    KV reporte;
    TextView tvTotalOrders, tvTotal, tvProduct, tvHours;//Ventas
    TextView tvTotalDevoluciones, tvTotalPerdidas, tvMotivosFrecuentes;//Devoluciones
    TextView tvTotalOrdenesVendedores, tvTotalGananciaVendedores, tvTotalDevolucionesVendedores, tvTotalPerdidasVendedores, tvVendedorMasVendido, tvVendedorMasDevoluciones;//vendedores.
    int lastDatePressed;
    int processID=0;//NADA
    int CODEPROCESS_SCANHISTORY = 1;//
    String lastFechaIni, lastFechaFin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_reports);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void init(){

        spnReporte = findViewById(R.id.spn);

        rvList = findViewById(R.id.rvList);
        btnDesde = findViewById(R.id.btnDesde);
        btnHasta = findViewById(R.id.btnHasta);

        /////////////////////////////////
        /////  VENTAS     ///////////////
        llVentas = findViewById(R.id.llVentas);
        tvTotalOrders = findViewById(R.id.tvTotalOrdenes);
        tvTotal =findViewById(R.id.tvTotal);
        tvProduct = findViewById(R.id.tvMasVendido);
        tvHours = findViewById(R.id.tvHoraPico);

        //DEVOLUCION
        llDevoluciones = findViewById(R.id.llDevoluciones);
        tvTotalDevoluciones = findViewById(R.id.tvTotalDevoluciones);
        tvTotalPerdidas = findViewById(R.id.tvTotalPerdidas);
        tvMotivosFrecuentes = findViewById(R.id.tvMotivosFrecuentes);

        //VENDEDORES
        tvTotalOrdenesVendedores = findViewById(R.id.tvTotalOrdenesVendedores);
        tvTotalGananciaVendedores = findViewById(R.id.tvTotalGananciaVendedores);
        tvTotalDevolucionesVendedores= findViewById(R.id.tvTotalDevolucionesVendedores);
        tvTotalPerdidasVendedores= findViewById(R.id.tvTotalPerdidasVendedores);
        tvVendedorMasVendido = findViewById(R.id.tvVendedorMasVendido);
        tvVendedorMasDevoluciones= findViewById(R.id.tvVendedorMasDevoluciones);

        /////////////////////////////////////////////////////////////

        fillSpnReportes();
        btnDesde.setText(Funciones.getFormatedDateRepDom(new Date()));
        btnHasta.setText(Funciones.getFormatedDateRepDom(new Date()));

        btnDesde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             lastDatePressed = v.getId();
                showDatePicker();
            }
        });
        btnHasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastDatePressed = v.getId();
                showDatePicker();
            }
        });
        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToReportsDetail(v.getId());
            }
        };
        ((Button)findViewById(R.id.btnProducts)).setOnClickListener(btnListener);
        ((Button)findViewById(R.id.btnHorasPico)).setOnClickListener(btnListener);
        ((Button)findViewById(R.id.btnMotivos)).setOnClickListener(btnListener);
        ((Button)findViewById(R.id.btnVendedorMasVendido)).setOnClickListener(btnListener);
        ((Button)findViewById(R.id.btnVendedorMasDevoluciones)).setOnClickListener(btnListener);

        ((Button)findViewById(R.id.btnSearch)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processID = CODEPROCESS_SCANHISTORY;
                String[] d1 = btnDesde.getText().toString().split("/");
                String[] d2= btnHasta.getText().toString().split("/");
                Calendar c1 = Calendar.getInstance();c1.set(Calendar.YEAR,Integer.parseInt(d1[2]));
                c1.set(Calendar.MONTH,(Integer.parseInt(d1[1]) -1));c1.set(Calendar.DAY_OF_MONTH,Integer.parseInt(d1[0]));
                c1.set(Calendar.HOUR_OF_DAY, 0);c1.set(Calendar.MINUTE, 0);c1.set(Calendar.SECOND, 0);

                Calendar c2 = Calendar.getInstance();c2.set(Calendar.YEAR, Integer.parseInt(d2[2]));
                c2.set(Calendar.MONTH, (Integer.parseInt(d2[1]) -1));c2.set(Calendar.DAY_OF_MONTH, Integer.parseInt(d2[0]));
                //mantener la hora y los minutos para c2.


                if(reporte.getKey().equals(CODES.REPORTS_FILTER_KEY_VENTAS) || reporte.getKey().equals(CODES.REPORTS_FILTER_KEY_DEVOLUCIONES) || reporte.getKey().equals(CODES.REPORTS_FILTER_KEY_VENDEDORES)){

                    int status =-1;
                    if(reporte.getKey().equals(CODES.REPORTS_FILTER_KEY_VENTAS)){
                        status = CODES.CODE_ORDER_STATUS_CLOSED;
                    }else if(reporte.getKey().equals(CODES.REPORTS_FILTER_KEY_DEVOLUCIONES)){
                        status = CODES.CODE_ORDER_STATUS_CANCELED;
                    }

                    Date lastMinDate = SalesController.getInstance(MainReports.this).getLastInitialDateSavedHistory(status);
                    Date lastMaxDate = SalesController.getInstance(MainReports.this).getLastDateSavedHistory(status);

                    //validar que el rango de fhcas este correcto.
                    if(Funciones.fechaMayorQue(c1.getTime(), c2.getTime())){
                        Snackbar.make(findViewById(R.id.llParent), "Rango de fechas invalido", Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    boolean minFromDB;
                    boolean maxFromDB;

                    if(lastMinDate == null && lastMaxDate == null){//no hay data historica, buscar toda la data.
                        minFromDB = false;
                        maxFromDB = false;
                        SalesController.getInstance(MainReports.this).getHistoricDataToSearch(status, c1.getTime(),
                                c2.getTime(),MainReports.this, MainReports.this, MainReports.this);
                    }else {
                        //Si ambas fechas  (inicio - fin) sobrepasan las seleccionadas por el usuario, se buscara directamente desde la base de datos.
                        if((lastMinDate != null && Funciones.fechaMenorQue(lastMinDate, c1.getTime()))
                                && (lastMaxDate != null && Funciones.fechaMayorQue(lastMaxDate, c2.getTime()))){
                            fillData();
                            //Si la fecha de Inicio(seleccionada) es menor a la ultima(mas baja) en mi base de datos pero la fecha maxima es menor a la fecha maxima de mi base de datos, solo buscara
                            //en el server la data que este entre la digitada por el usuario hasta la fecha minima en mi base de datos.
                         }else if((lastMinDate != null && Funciones.fechaMenorQue(c1.getTime(), lastMinDate))
                                && (lastMaxDate != null && Funciones.fechaMayorQue(lastMaxDate, c2.getTime()))){
                            SalesController.getInstance(MainReports.this).getHistoricDataToSearch(status, c1.getTime(),
                                    lastMinDate, MainReports.this, MainReports.this, MainReports.this);
                            //si la fecha de inicio(seleccionado) es mayor que la ultima (mas baja) en mi base de datos pero la fecha maxima es mayor a la ultima de mi base de datos,
                            //solo buscara en el server la data que este entre la ultima de mi base de datos y digitada la digitada por el usuario.
                        } else if((lastMinDate != null && Funciones.fechaMenorQue(lastMinDate, c1.getTime()))
                                && (lastMaxDate != null && Funciones.fechaMayorQue(c2.getTime(), lastMaxDate))){
                            SalesController.getInstance(MainReports.this).getHistoricDataToSearch(status,lastMaxDate,
                                    c2.getTime(), MainReports.this, MainReports.this, MainReports.this);
                        }else{
                           //De lo contrario buscara lo que el usuario especifico.
                            SalesController.getInstance(MainReports.this).getHistoricDataToSearch(status, c1.getTime(),
                                    c2.getTime(), MainReports.this, MainReports.this, MainReports.this);
                        }

                    }
                }

            }
        });
        spnReporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reporte = (KV)parent.getSelectedItem();
                changeReportMode();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        getAllHistoryData();
    }

    public void fillSpnReportes(){
        ArrayList<KV> list = new ArrayList<>();
        list.add(new KV(CODES.REPORTS_FILTER_KEY_VENTAS, "Ventas"));//ventas al dia, ventas al mes
        list.add(new KV(CODES.REPORTS_FILTER_KEY_DEVOLUCIONES, "Devoluciones"));//devoluciones al dia, devoluciones al mes,platillos mas devueltos,  motivos mas comunes.
        list.add(new KV(CODES.REPORTS_FILTER_KEY_INVENTARIOS, "Inventario"));//mas consumidos, menos consumidos, proximos a agotarse,
        list.add(new KV(CODES.REPORTS_FILTER_KEY_VENDEDORES, "Vendedores"));//top ranking mas ventas, mas devoluciones, etc etc



        spnReporte.setAdapter( new ArrayAdapter<KV>(this,android.R.layout.simple_list_item_1,list));

    }


    public void search(){

    }

    public void getAllHistoryData(){
       /* SalesController.getInstance(this).getAllDataHistoryFromFireBase(this);
        SalesController.getInstance(this).getAllDataDetailHistoryFromFireBase(this);*/
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        e.printStackTrace();
    }

    public void showDatePicker(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MainReports.this, MainReports.this, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);c.set(Calendar.MONTH, month);c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String date  = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
        if(lastDatePressed == btnDesde.getId()){
            btnDesde.setText(date);
        }else if(lastDatePressed == btnHasta.getId()){
            btnHasta.setText(date);
        }
    }

    public void goToReportsDetail(int btnID){
        Intent i = new Intent(MainReports.this, ReportsDetail.class);
        i.putExtra(CODES.MAIN_REPORTS_EXTRA_LASTDATEINI, lastFechaIni);
        i.putExtra(CODES.MAIN_REPORTS_EXTRA_LASTDATEEND, lastFechaFin);
        i.putExtra(CODES.MAIN_REPORTS_EXTRA_IDCALLER, btnID);

        if(btnID == R.id.btnProducts) {
            i.putExtra(CODES.MAIN_REPORTS_TOTALORDERS, tvTotalOrders.getText().toString());
        }else if(btnID == R.id.btnMotivos){
            i.putExtra(CODES.MAIN_REPORTS_TOTALORDERS, tvTotalDevoluciones.getText().toString());
        }else if(btnID == R.id.btnVendedorMasVendido){
            i.putExtra(CODES.MAIN_REPORTS_TOTALORDERS, tvTotalOrdenesVendedores.getText().toString());
        }else if(btnID == R.id.btnVendedorMasDevoluciones){
            i.putExtra(CODES.MAIN_REPORTS_TOTALORDERS, tvTotalDevolucionesVendedores.getText().toString());
        }

        startActivity(i);
    }

    @Override
    public void onSuccess(QuerySnapshot querySnapshot) {
        if(processID == CODEPROCESS_SCANHISTORY ){
            if(reporte.getKey().equals(CODES.REPORTS_FILTER_KEY_VENTAS) || reporte.getKey().equals(CODES.REPORTS_FILTER_KEY_DEVOLUCIONES)  || reporte.getKey().equals(CODES.REPORTS_FILTER_KEY_VENDEDORES)) {
                SalesController.getInstance(MainReports.this).proccessQuerySnapshotHistoricData(querySnapshot);
                fillData();
            }

        }

    }

    public void fillData(){
        String[] d1 = btnDesde.getText().toString().split("/");
        String[] d2= btnHasta.getText().toString().split("/");
        lastFechaIni = d1[2]+"-"+d1[1]+"-"+d1[0]+" 00:00:00";
        lastFechaFin = d2[2]+"-"+d2[1]+"-"+d2[0]+" 24:59:59";
        if(reporte.getKey().equals(CODES.REPORTS_FILTER_KEY_VENTAS)) {
           fillDataVentasReport();
        }else if(reporte.getKey().equals(CODES.REPORTS_FILTER_KEY_DEVOLUCIONES)){
            fillDataDevolucionesReport();
        }else if(reporte.getKey().equals(CODES.REPORTS_FILTER_KEY_VENDEDORES)){
            fillDataVendedoresReport();
        }

    }

    public void fillDataVentasReport(){
        HashMap<String, String> map = SalesController.getInstance(MainReports.this).getVentasReport(lastFechaIni, lastFechaFin);
        tvTotalOrders.setText(map.get("TO"));
        tvTotal.setText(map.get("TP"));
        tvProduct.setText(map.get("MS"));
    }
    public void fillDataDevolucionesReport(){
        HashMap<String, String> map = SalesController.getInstance(MainReports.this).getDevolucionesReport(lastFechaIni, lastFechaFin);
        tvTotalDevoluciones.setText(map.get("TD"));
        tvTotalPerdidas.setText(map.get("TP"));
        tvMotivosFrecuentes.setText(map.get("MF"));
    }

    public void fillDataVendedoresReport(){
        HashMap<String, String> mapSales = SalesController.getInstance(MainReports.this).getVentasReport(lastFechaIni, lastFechaFin);
        HashMap<String, String> mapDevs = SalesController.getInstance(MainReports.this).getDevolucionesReport(lastFechaIni, lastFechaFin);
        tvTotalOrdenesVendedores.setText(mapSales.get("TO"));
        tvTotalGananciaVendedores.setText(mapSales.get("TP"));
        tvVendedorMasVendido.setText(mapSales.get("MSS"));
        tvTotalDevolucionesVendedores.setText(mapDevs.get("TD"));
        tvTotalPerdidasVendedores.setText(mapDevs.get("TP"));
        tvVendedorMasDevoluciones.setText(mapDevs.get("MRS"));

    }

    @Override
    public void onComplete(@NonNull Task task) {
        //Fin de query
        if(task.getException() != null){
            Toast.makeText(MainReports.this, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onCanceled() {
        Toast.makeText(MainReports.this, "Cancelado", Toast.LENGTH_LONG).show();
    }

    public void changeReportMode(){

        llVentas.setVisibility((reporte.getKey().equals(CODES.REPORTS_FILTER_KEY_VENTAS))?View.VISIBLE:View.GONE);
        llDevoluciones.setVisibility((reporte.getKey().equals(CODES.REPORTS_FILTER_KEY_DEVOLUCIONES))?View.VISIBLE:View.GONE);
    }
}
