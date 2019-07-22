package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.security.cert.TrustAnchor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.meta.When;

import far.com.eatit.Adapters.Models.OrderDetailModel;
import far.com.eatit.Adapters.Models.OrderModel;
import far.com.eatit.Adapters.Models.OrderReceiptModel;
import far.com.eatit.Adapters.Models.PercentRowModel;
import far.com.eatit.Adapters.Models.ReceiptResumeModel;
import far.com.eatit.Adapters.Models.WorkedOrdersRowModel;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.Products;
import far.com.eatit.CloudFireStoreObjects.ProductsControl;
import far.com.eatit.CloudFireStoreObjects.ProductsTypes;
import far.com.eatit.CloudFireStoreObjects.Receipts;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.CloudFireStoreObjects.SalesDetails;
import far.com.eatit.CloudFireStoreObjects.UserControl;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Generic.Objects.KV2;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class SalesController {
    public static final String TABLE_NAME = Tablas.generalUsersSales;
    public static String TABLE_NAME_HISTORY = Tablas.generalUsersSalesHistory;
    public static String CODE = "code",STATUS = "status",NOTES = "notes", DATE = "date",MDATE = "mdate", TOTAL="total",TOTALDISCOUNT = "totaldiscount",
            CODEUSER = "codeuser",CODEAREADETAIL = "codeareadetail", CODEREASON = "codereason" , REASONDESCRIPTION = "reasondescription",
            CODEPRODUCTTYPE = "codeproducttype", CODEPRODUCTSUBTYPE="codeproductsubtype", CODESALESORIGEN = "codesalesorigen", CODERECEIPT = "codereceipt" ;
    String[]columns = new String[]{CODE,STATUS, NOTES, DATE, MDATE, TOTAL, TOTALDISCOUNT, CODEUSER,CODEAREADETAIL, CODEREASON, REASONDESCRIPTION, CODEPRODUCTTYPE, CODEPRODUCTSUBTYPE, CODESALESORIGEN, CODERECEIPT};

    public static final String TABLE_NAME_DETAIL = Tablas.generalUsersSalesDetails;
    public static String TABLE_NAME_DETAIL_HISTORY = Tablas.generalUsersSalesDetailsHistory;
    public static String DETAIL_CODE = "code",DETAIL_CODESALES = "codesales", DETAIL_CODEPRODUCT = "codeproduct",
            DETAIL_DISCOUNT = "discount", DETAIL_POSITION = "position", DETAIL_PRICE = "price",
            DETAIL_QUANTITY = "quantity", DETAIL_UNIT = "unit", DETAIL_CODEUND = "codeund", DETAIL_DATE="date", DETAIL_MDATE="mdate";
    String[]columnsDetails = new String[]{DETAIL_CODE,DETAIL_CODESALES, DETAIL_CODEPRODUCT,DETAIL_CODEUND,DETAIL_DISCOUNT,DETAIL_POSITION,DETAIL_QUANTITY,DETAIL_UNIT,DETAIL_PRICE, DATE, MDATE};


    Context context;
    FirebaseFirestore db;
    DB sqlite;
    private static SalesController instance;

    private SalesController(Context c){
        this.context = c;
        this.db = FirebaseFirestore.getInstance();
        sqlite = DB.getInstance(c);
    }
    public static SalesController getInstance(Context c){
        if(instance == null){
           instance = new SalesController(c);
        }
        return instance;
    }

    public static String  getQueryCreateSales(){
        return getQueryCreateHead(false);
    }
    public static String getQueryCreateSalesDetail(){
        return getQueryCreateDetail(false);
    }
    public static String getQueryCreateSalesHistory(){
        return getQueryCreateHead(true);
    }
    public static String getQueryCreateSalesDetailHistory(){
       return getQueryCreateDetail(true);
    }

    public static String getQueryCreateHead(boolean history){
              String QUERY_CREATE = "CREATE TABLE "+((history)?TABLE_NAME_HISTORY:TABLE_NAME)+" ("
                +CODE+" TEXT,"+STATUS+" TEXT,"+NOTES+" TEXT, "+DATE+" TEXT,"+MDATE+" TEXT, "+TOTAL+" DECIMAL(11, 3), "+TOTALDISCOUNT+" DECIMAL(11, 3), " +
                CODEUSER+" TEXT,"+CODEAREADETAIL+" TEXT, "+CODEREASON+" TEXT, "+REASONDESCRIPTION+" TEXT, "+CODEPRODUCTTYPE+" TEXT, "+CODEPRODUCTSUBTYPE+" TEXT, " +
                CODESALESORIGEN+" TEXT,"+CODERECEIPT+" TEXT )";
              return QUERY_CREATE;
    }

    public static String getQueryCreateDetail(boolean history){
              String QUERY_CREATE_DETAIL = "CREATE TABLE "+((history)?TABLE_NAME_DETAIL:TABLE_NAME_DETAIL_HISTORY)+" ("
                +DETAIL_CODE+" TEXT, "+DETAIL_CODESALES+" TEXT, "+DETAIL_CODEPRODUCT+" TEXT, "+DETAIL_DISCOUNT+" DECIMAL(11,3), "+DETAIL_POSITION+" INTEGER, "
                +DETAIL_PRICE+" DECIMAL(11, 3), "+DETAIL_QUANTITY+" DOUBLE, "+DETAIL_UNIT+" DOUBLE, "+DETAIL_CODEUND+" TEXT, " +
                DETAIL_DATE+" TEXT, "+DETAIL_MDATE+" TEXT)";
              return QUERY_CREATE_DETAIL;

    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersSales);
        return reference;
    }
    public CollectionReference getReferenceDetailFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersSalesDetails);
        return reference;
    }

    public CollectionReference getReferenceMainHistoryFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }

        CollectionReference reference = db.collection(Tablas.generalUsers)
                .document(l.getCODE()).collection(Tablas.generalUsersSalesHistory);
        return reference;
    }

    public CollectionReference getReferenceHistoryFireStore(Sales s){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }

        String date = Funciones.getFormatedDateNoTime(s.getDATE());
        String year = date.substring(0, 4);
        String moth = date.substring(4, 6);

        CollectionReference reference = getReferenceMainHistoryFireStore()
                /*.document(year).collection (moth)*/;
        return reference;
    }

    public CollectionReference getReferenceDetailMainHistoryFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }

        CollectionReference reference = db.collection(Tablas.generalUsers)
                .document(l.getCODE()).collection(Tablas.generalUsersSalesDetailsHistory);
        return reference;
    }

    public CollectionReference getReferenceDetailHistoryFireStore(SalesDetails sd){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        String date = Funciones.getFormatedDateNoTime(sd.getDATE());
        String year = date.substring(0, 4);
        String moth = date.substring(4, 6);

        CollectionReference reference = getReferenceDetailMainHistoryFireStore()
                .document(year).collection(moth);
        return reference;
    }


    public void save(String notes, String codeAreaDetail){
        if(UserControlController.getInstance(context).orderSplit()){
        ArrayList<ArrayList> list = TempOrdersController.getInstance(context).getSplitedTempSale(notes, codeAreaDetail);
        ArrayList<Sales> sales = (ArrayList<Sales>) list.get(0);
        ArrayList<SalesDetails> salesDetails = (ArrayList<SalesDetails>) list.get(1);
        sendToFireBase(sales, salesDetails);

        }else {
            Sales s = TempOrdersController.getInstance(context).getTempSale();
            s.setTOTAL(TempOrdersController.getInstance(context).getSumPrice());
            s.setSTATUS(CODES.CODE_ORDER_STATUS_OPEN);
            s.setNOTES(notes);
            s.setCODEAREADETAIL(codeAreaDetail);
            ArrayList<SalesDetails> arraySd = TempOrdersController.getInstance(context).getTempSalesDetails(s);
            sendToFireBase(s, arraySd);
        }


    }

    public long insert(Sales s){
        return insert(s, false);
    }
    public long insertHistory(Sales s){
        return insert(s, true);
    }
    public long insert(Sales s, boolean history){
        String table = (history)?TABLE_NAME_HISTORY:TABLE_NAME;

        ContentValues cv = new ContentValues();
        cv.put(CODE,s.getCODE());
        cv.put(STATUS, s.getSTATUS());
        cv.put(NOTES, s.getNOTES());
        cv.put(DATE, Funciones.getFormatedDate(s.getDATE()));
        cv.put(MDATE,Funciones.getFormatedDate(s.getMDATE()));
        cv.put(TOTAL,s.getTOTAL());
        cv.put(TOTALDISCOUNT,s.getTOTALDISCOUNT());
        cv.put(CODEUSER, s.getCODEUSER());
        cv.put(CODEAREADETAIL, s.getCODEAREADETAIL());
        cv.put(CODEREASON, s.getCODEREASON());
        cv.put(REASONDESCRIPTION, s.getREASONDESCRIPTION());
        cv.put(CODEPRODUCTTYPE, s.getCODEPRODUCTTYPE());
        cv.put(CODEPRODUCTSUBTYPE, s.getCODEPRODUCTSUBTYPE());
        cv.put(CODESALESORIGEN, s.getCODEPRODUCTSUBTYPE());
        cv.put(CODERECEIPT, s.getCODERECEIPT());

        long result = DB.getInstance(context).getWritableDatabase().insert(table,null,cv);
        return result;
    }

    public long update(Sales s){
        return update(s, false);
    }
    public long updateHistory(Sales s){
        return update(s, true);
    }
    public long update(Sales s, boolean history){
        ContentValues cv = new ContentValues();
        cv.put(CODE,s.getCODE());
        cv.put(STATUS, s.getSTATUS());
        cv.put(NOTES, s.getNOTES());
        cv.put(DATE, Funciones.getFormatedDate(s.getDATE()));
        cv.put(MDATE,Funciones.getFormatedDate(s.getMDATE()));
        cv.put(TOTAL,s.getTOTAL());
        cv.put(TOTALDISCOUNT,s.getTOTALDISCOUNT());
        cv.put(CODEUSER, s.getCODEUSER());
        cv.put(CODEAREADETAIL, s.getCODEAREADETAIL());
        cv.put(CODEREASON, s.getCODEREASON());
        cv.put(REASONDESCRIPTION, s.getREASONDESCRIPTION());
        cv.put(CODEPRODUCTTYPE, s.getCODEPRODUCTTYPE());
        cv.put(CODEPRODUCTSUBTYPE, s.getCODEPRODUCTSUBTYPE());
        cv.put(CODESALESORIGEN, s.getCODEPRODUCTSUBTYPE());
        cv.put(CODERECEIPT, s.getCODERECEIPT());

        String where = CODE+" = ? ";
        String table = (history)?TABLE_NAME_HISTORY:TABLE_NAME;
        long result = DB.getInstance(context).getWritableDatabase().update(table,cv,where, new String[]{s.getCODE()} );
        return result;
    }

    public long delete(String where, String[] args){
        return delete(false, where, args);
    }
    public long deleteHistory(String where, String[] args){
        return delete(true, where, args);
    }
    public long delete(boolean history, String where, String[] args){
        String table =(history)?TABLE_NAME_HISTORY:TABLE_NAME;
        long result = DB.getInstance(context).getWritableDatabase().delete(table,where, args);
        return result;
    }
    public long deleteHeadDetail(Sales s){
        String where = CODE+" = ? ";
        String[]args = new String[]{s.getCODE()};
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        if(result >0){
           where = DETAIL_CODESALES+" = ? ";
           result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME_DETAIL,where, args);
        }
        return result;
    }

    public long insert_Detail(SalesDetails sd){
        return  insert_Detail(sd, false);
    }
    public long insert_DetailHistory(SalesDetails sd){
        return  insert_Detail(sd, true);
    }
    public long insert_Detail(SalesDetails sd, boolean history){
        String tabla = (history)?TABLE_NAME_DETAIL_HISTORY:TABLE_NAME_DETAIL;
        ContentValues cv = new ContentValues();
        cv.put(DETAIL_CODE,sd.getCODE());
        cv.put(DETAIL_CODESALES, sd.getCODESALES());
        cv.put(DETAIL_CODEPRODUCT,sd.getCODEPRODUCT());
        cv.put(DETAIL_DISCOUNT,sd.getDISCOUNT());
        cv.put(DETAIL_POSITION,sd.getPOSITION());
        cv.put(DETAIL_PRICE,sd.getPRICE());
        cv.put(DETAIL_QUANTITY,sd.getQUANTITY());
        cv.put(DETAIL_UNIT,sd.getUNIT());
        cv.put(DETAIL_CODEUND, sd.getCODEUND());
        cv.put(DETAIL_DATE, Funciones.getFormatedDate(sd.getDATE()));
        cv.put(DETAIL_MDATE, Funciones.getFormatedDate(sd.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(tabla,null,cv);
        return result;
    }

    public long update_Detail(SalesDetails sd){
        ContentValues cv = new ContentValues();
        cv.put(DETAIL_CODE,sd.getCODE());
        cv.put(DETAIL_CODESALES, sd.getCODESALES());
        cv.put(DETAIL_CODEPRODUCT,sd.getCODEPRODUCT());
        cv.put(DETAIL_DISCOUNT,sd.getDISCOUNT());
        cv.put(DETAIL_POSITION,sd.getPOSITION());
        cv.put(DETAIL_PRICE,sd.getPRICE());
        cv.put(DETAIL_QUANTITY,sd.getQUANTITY());
        cv.put(DETAIL_UNIT,sd.getUNIT());
        cv.put(DETAIL_CODEUND, sd.getCODEUND());
        cv.put(DETAIL_MDATE, Funciones.getFormatedDate(sd.getMDATE()));

        String where = DETAIL_CODE+"= ?  AND "+DETAIL_CODEPRODUCT+"= ? AND "+DETAIL_CODEUND+" = ?";

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME_DETAIL,cv,where, new String[] {sd.getCODE(), sd.getCODEPRODUCT(), sd.getCODEUND()});
        return result;
    }


    public ArrayList<Sales> getSales(String where, String[] args){
        return getSales(false, where, args);
    }
    public ArrayList<Sales> getSalesHistory(String where, String[] args){
        return getSales(true, where, args);
    }
    public ArrayList<Sales> getSales(boolean history, String where, String[] args){
        String tabla = (history)?TABLE_NAME_HISTORY:TABLE_NAME;
       ArrayList<Sales> result = new ArrayList<>();

        Cursor c = DB.getInstance(context).getReadableDatabase().query(tabla,columns,where, args,null,null,null);
        while(c.moveToNext()){
            result.add(new Sales(c));
        }
        c.close();
        return result;
    }

    public ArrayList<SalesDetails> getSalesDetailsByCodeSales(String code){
        String where = DETAIL_CODESALES+" = ?";
        return getSalesDetails(where, new String[]{code});
    }
    public ArrayList<SalesDetails> getSalesDetails(String where, String[] args){
        return getSalesDetails(false, where, args);
    }
    public ArrayList<SalesDetails> getSalesDetailsHistory(String where, String[] args){
        return getSalesDetails(true, where, args);
    }
    public ArrayList<SalesDetails> getSalesDetails(boolean history, String where, String[] args){
        String tabla = (history)?TABLE_NAME_DETAIL_HISTORY:TABLE_NAME_DETAIL;
        ArrayList<SalesDetails> result = new ArrayList<>();
        Cursor c = DB.getInstance(context).getReadableDatabase().query(tabla,columnsDetails,where, args,null,null,null);
        while(c.moveToNext()){
            result.add(new SalesDetails(c));
        }
        c.close();
        return result;
    }

    public Sales getSaleByCode(String code){
        return getSaleByCode(false, code);
    }
    public Sales getSaleHistoryByCode(String code){
        return getSaleByCode(true, code);
    }
    public Sales getSaleByCode(boolean history, String code){
        String table = (history)?TABLE_NAME_HISTORY:TABLE_NAME;
        Sales s = null;
        String where = CODE+" = ?";
        String[]args = new String[]{code};
        Cursor c = DB.getInstance(context).getReadableDatabase().query(table,columns,where, args,null,null,null);
        if(c.moveToFirst()){
            s = new Sales(c);
        }
        c.close();
        return s;
    }

    public long delete_Detail(String where, String[] args){
        return delete_Detail(false, where, args);
    }
    public long delete_DetailHistory(String where, String[] args){
        return delete_Detail(true, where, args);
    }

    public long delete_Detail(boolean history, String where, String[] args){
        String table = (history)?TABLE_NAME_DETAIL_HISTORY:TABLE_NAME_DETAIL;
        long result = DB.getInstance(context).getWritableDatabase().delete(table,where, args);
        return result;
    }

    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> sales = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersSales).get();
            sales.addOnSuccessListener(onSuccessListener);
            sales.addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getDataDetailsFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> salesDetails = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersSalesDetails).get();
            salesDetails.addOnSuccessListener(onSuccessListener);
            salesDetails.addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<OrderModel> getOrderModels(String where){
        ArrayList<OrderModel> objects = new ArrayList<>();
        String sql = "Select DISTINCT "+CODE+","+STATUS+", "+NOTES+", "+TOTALDISCOUNT+", "+TOTAL+", " +DATE+", "+MDATE+" "+
                "FROM "+TABLE_NAME+" " +
                ((where == null)?"":"WHERE "+where+" ")
                +"ORDER BY "+MDATE+" DESC";
        Cursor c = sqlite.getReadableDatabase().rawQuery(sql, null);
        while(c.moveToNext()){

            String code = c.getString(c.getColumnIndex(CODE));
            String status = c.getString(c.getColumnIndex(STATUS));
            String notes = c.getString(c.getColumnIndex(NOTES));
            String lastUpdate = c.getString(c.getColumnIndex(MDATE));
            String date = c.getString(c.getColumnIndex(DATE));
            String minutos = (lastUpdate == null)?"NONE":String.valueOf(Funciones.calcularMinutos(new Date(), Funciones.parseStringToDate(date)))+" Mins";
            boolean edited = (lastUpdate != null && !(date.equals(lastUpdate)));
            OrderModel om = new OrderModel(code,status, notes,minutos,edited, getOrderDetailModels(code));
            objects.add(om);
        }
        return objects;
    }


    public ArrayList<WorkedOrdersRowModel> getWorkedOrderModels(String where){
        ArrayList<WorkedOrdersRowModel> objects = new ArrayList<>();
        try {
            String sql = "Select DISTINCT s." + CODE + " as CODE, ifnull(ifnull(s." + MDATE + ", s." + DATE + "), 'NONE') as FECHA, a." + AreasController.CODE + " as ACODE,  " +
                    "a." + AreasController.DESCRIPTION + " as ADESCRIPTION,  ad." + AreasDetailController.CODE + " as ADCODE, ad." + AreasDetailController.DESCRIPTION + " as ADDESCRIPTION, s." + STATUS + " as STATUS " +
                    "FROM " + TABLE_NAME + " s  " +
                    "INNER JOIN " + AreasDetailController.TABLE_NAME + " ad on ad." + AreasDetailController.CODE + " = s." + CODEAREADETAIL + " " +
                    "INNER JOIN " + AreasController.TABLE_NAME + " a on a." + AreasController.CODE + " = ad." + AreasDetailController.CODEAREA + " " +
                    ((where == null) ? "" : "WHERE " + where + " ")
                    + "ORDER BY s." + MDATE + " DESC";
            Cursor c = sqlite.getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {

                String code = c.getString(c.getColumnIndex("CODE"));
                String fecha = c.getString(c.getColumnIndex("FECHA"));
                String areaCode = c.getString(c.getColumnIndex("ACODE"));
                String areaDescription = c.getString(c.getColumnIndex("ADESCRIPTION"));
                String areaDetailCode = c.getString(c.getColumnIndex("ADCODE"));
                String areaDetailDescription = c.getString(c.getColumnIndex("ADDESCRIPTION"));
                int s = c.getInt(c.getColumnIndex("STATUS"));
                String status = "NONE";
                switch (s) {
                    case CODES.CODE_ORDER_STATUS_OPEN:
                        status = "Abierta";
                        break;
                    case CODES.CODE_ORDER_STATUS_CLOSED:
                        status = "Cerrada";
                        break;
                    case CODES.CODE_ORDER_STATUS_CANCELED:
                        status = "Cancelada";
                        break;
                    case CODES.CODE_ORDER_STATUS_READY:
                        status = "Lista";
                        break;
                    case CODES.CODE_ORDER_STATUS_DELIVERED:
                        status = "Entregada";
                        break;

                }

                WorkedOrdersRowModel om = new WorkedOrdersRowModel(code, fecha, areaCode, areaDescription, areaDetailCode, areaDetailDescription, status);
                objects.add(om);
            }c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return objects;
    }
    public ArrayList<OrderDetailModel> getOrderDetailModels(String code){

        ArrayList<OrderDetailModel> objects = new ArrayList<>();
        try {
            String where = "sd."+DETAIL_CODESALES + " = '" + code +"'";

            String sql = "Select sd."+DETAIL_CODE+" as CODE,sd."+DETAIL_CODESALES+" as CODESALES,p."+ProductsController.CODE+" AS CODEPRODUCT, " +
                    "p." + ProductsController.DESCRIPTION + " as PRODUCTO, sd." + DETAIL_QUANTITY + " as CANTIDAD, sd." + DETAIL_UNIT + " as UNIDAD," +
                    "m."+MeasureUnitsController.CODE+" AS CODEMEDIDA,  m." + MeasureUnitsController.DESCRIPTION + " AS MEDIDA,  ifnull(pc."+ProductsControlController.BLOQUED+", '0') as BLOQUED, " +
                    "sd."+DETAIL_DATE+" as DATE, sd."+DETAIL_MDATE+" as MDATE "+
                    "FROM " + TABLE_NAME_DETAIL + " sd " +
                    "LEFT JOIN " + ProductsController.TABLE_NAME + " p on sd." + DETAIL_CODEPRODUCT + " = p." + ProductsController.CODE + " " +
                    "LEFT JOIN " + MeasureUnitsController.TABLE_NAME + " m on m." + MeasureUnitsController.CODE + " = sd." + DETAIL_CODEUND + " " +
                    "LEFT JOIN "+ProductsControlController.TABLE_NAME+" pc on pc."+ProductsControlController.CODEPRODUCT+ " = p." + ProductsController.CODE + " " +
                    "WHERE " + where+" " +
                    "GROUP BY p."+ProductsController.CODE+", m."+MeasureUnitsController.CODE ;
            Cursor c = sqlite.getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {

                OrderDetailModel om = new OrderDetailModel(
                        c.getString(c.getColumnIndex("CODEPRODUCT")),
                        c.getString(c.getColumnIndex("CODE")),
                        c.getString(c.getColumnIndex("CODESALES")),
                        c.getString(c.getColumnIndex("PRODUCTO")),
                        c.getString(c.getColumnIndex("CANTIDAD")),
                        c.getString(c.getColumnIndex("CODEMEDIDA")),
                        c.getString(c.getColumnIndex("MEDIDA")),
                        c.getString(c.getColumnIndex("BLOQUED")),
                        ProductsMeasureController.getInstance(context).getProductsMeasureKVByCodeProduct(c.getString(c.getColumnIndex("CODEPRODUCT"))));
                objects.add(om);

            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return objects;
    }


    public void sendToFireBase(Sales s){
        sendToFireBase(s, null);
    }
    public void sendToFireBase(Sales s,  ArrayList<SalesDetails> salesDetails){
        ArrayList<Sales> sales = new ArrayList<>(); sales.add(s);
        sendToFireBase(sales, salesDetails);
    }

    public void sendToFireBase(ArrayList<Sales> sales,  ArrayList<SalesDetails> salesDetails){
        try {
            WriteBatch lote = db.batch();
            for(Sales s: sales) {
                if (s.getMDATE() == null) {
                    lote.set(getReferenceFireStore().document(s.getCODE()), s.toMap());
                } else {
                    lote.update(getReferenceFireStore().document(s.getCODE()), s.toMap());
                }
            }

            if (salesDetails != null){
                for (SalesDetails sd : salesDetails) {//Insertando o actualizando el nuevo detalle
                    if (sd.getMDATE() == null) {
                        lote.set(getReferenceDetailFireStore().document(sd.getCODE()), sd.toMap());
                    } else {
                        lote.update(getReferenceDetailFireStore().document(sd.getCODE()), sd.toMap());
                    }

                }
            }

            lote.commit().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void getAllDataFromFireBase(OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> reference = getReferenceFireStore().get();
            reference.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            Sales object = dc.getDocument().toObject(Sales.class);
                            String where = CODE+" = ?";
                            String[]args = new String[]{object.getCODE()};
                            delete(where, args);
                            insert(object);
                        }
                    }
                }
            }).addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getAllDataDetailFromFireBase(OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> reference = getReferenceDetailFireStore().get();
            reference.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            SalesDetails object = dc.getDocument().toObject(SalesDetails.class);
                            String where = CODE+" = ?";
                            String[]args = new String[]{object.getCODE()};
                            delete(where, args);
                            insert_Detail(object);
                        }
                    }
                }
            }).addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

/*
    public void getAllDataHistoryFromFireBase(OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> reference = getReferenceHistoryFireStore().get();
            reference.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            Sales object = dc.getDocument().toObject(Sales.class);
                            String where = CODE+" = ?";
                            String[]args = new String[]{object.getCODE()};
                            deleteHistory(where, args);
                            insertHistory(object);
                        }
                    }
                }
            }).addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getAllDataDetailHistoryFromFireBase(OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> reference = getReferenceDetailHistoryFireStore().get();
            reference.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            SalesDetails object = dc.getDocument().toObject(SalesDetails.class);
                            String where = CODE+" = ?";
                            String[]args = new String[]{object.getCODE()};
                            delete_DetailHistory(where, args);
                            insert_DetailHistory(object);
                        }
                    }
                }
            }).addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }*/



    public void massiveDelete(ArrayList<Sales> sales){
        try {
            WriteBatch lote = db.batch();
            for(Sales s: sales){
                for(SalesDetails sd: getSalesDetailsByCodeSales(s.getCODE())){
                    lote.delete(getReferenceDetailFireStore().document(sd.getCODE()));
                }
                lote.delete(getReferenceFireStore().document(s.getCODE()));
            }
            lote.commit().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void editDetailToFireBase(final Sales s, final ArrayList<SalesDetails> newsalesDetails){
        WriteBatch lote = db.batch();
        lote.update(getReferenceFireStore().document(s.getCODE()), s.toMap());
        ArrayList<SalesDetails> sdetails = getSalesDetailsByCodeSales(s.getCODE());
        for(SalesDetails sdDelete: getdifference(sdetails, newsalesDetails)){
            lote.delete(getReferenceDetailFireStore().document(sdDelete.getCODE()));
        }

        for(SalesDetails sd: newsalesDetails){//Insertando o actualizando el nuevo detalle
            if(sd.getMDATE() == null){
                lote.set(getReferenceDetailFireStore().document(sd.getCODE()), sd.toMap());
            }else{
                lote.update(getReferenceDetailFireStore().document(sd.getCODE()), sd.toMap());
            }

        }

        lote.commit().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void sendToHistory(ArrayList<Sales> sales){
        try {
            WriteBatch lote = db.batch();
            for(Sales s: sales){
                s.setDetails( getSalesDetailsByCodeSales(s.getCODE()));
                HashMap<String, Object> map =  s.toMap();
                lote.set(getReferenceHistoryFireStore(s).document(s.getCODE()),map);
            }
            lote.commit().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public ArrayList<SalesDetails> getdifference(ArrayList<SalesDetails> sdOriginal, ArrayList<SalesDetails> newsalesDetails){
        ArrayList<SalesDetails>toDelete = new ArrayList<>();
        for(SalesDetails del: sdOriginal){
            boolean delete = true;
            for(SalesDetails update: newsalesDetails){
                if(del.getCODE().equals(update.getCODE())){
                    delete = false;
                    break;
                }
            }

            if(delete)
                toDelete.add(del);


        }

        return toDelete;
    }


    public void fillSpinnerOrderStatus(Spinner spn, boolean addTodos){
        ArrayList<KV> data = new ArrayList<>();
        if(addTodos){
            KV obj = new KV("-1", "TODOS");
            data.add(obj);
        }
        data.add(new KV(CODES.CODE_ORDER_STATUS_OPEN+"","Abiertas") );
        data.add(new KV(CODES.CODE_ORDER_STATUS_CANCELED+"","Canceladas") );
        data.add(new KV(CODES.CODE_ORDER_STATUS_CLOSED+"","Cerradas") );
        data.add(new KV(CODES.CODE_ORDER_STATUS_DELIVERED+"","Entregadas") );
        data.add(new KV(CODES.CODE_ORDER_STATUS_READY+"","Lista") );


        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }


    public HashMap<String, String> getVentasReport(String dateIni, String dateEnd){
        HashMap<String, String> map = new HashMap<>();
        map.put("TO", "0");//TOTAL ORDERS
        map.put("TP", "0");//TOTAL GANADO
        map.put("MS", "");//MOST SALE (mas vendido)
        map.put("MSS", "");//MOST SALE SELLER
        try {
            String sql = "SELECT count(s." + CODE + "), sum(s." + TOTAL + ") " +
                    "FROM " + TABLE_NAME_HISTORY + " s "+
                    "WHERE s."+STATUS+" = '"+CODES.CODE_ORDER_STATUS_CLOSED+"' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') ";
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                String totalOrdenes = c.getString(0);
                String totalMonto = c.getString(1);
                map.put("TO", totalOrdenes);
                map.put("TP", totalMonto);
            }
            c.close();

            sql = "SELECT sum(sd." + DETAIL_QUANTITY + "), p."+ProductsController.DESCRIPTION+" " +
                    "FROM " + TABLE_NAME_DETAIL_HISTORY + " sd " +
                    "INNER JOIN " + TABLE_NAME_HISTORY + " s on s." + CODE + " = sd." + DETAIL_CODESALES + "  AND s."+STATUS+" = '"+CODES.CODE_ORDER_STATUS_CLOSED+"' "+
                    "INNER JOIN "+ ProductsController.TABLE_NAME+" p on p."+ProductsController.CODE+" = sd."+DETAIL_CODEPRODUCT+" "+
                    "WHERE s."+STATUS+" = '"+CODES.CODE_ORDER_STATUS_CLOSED+"'  AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') "+
                    "GROUP BY sd." + DETAIL_CODEPRODUCT + ", p."+ProductsController.DESCRIPTION+" " +
                    "ORDER BY 1 DESC " +
                    "LIMIT 1 ";
            Cursor c2 = DB.getInstance(context).getReadableDatabase().rawQuery(sql,null);
            if (c2.moveToFirst()) {
                map.put("MS", c2.getString(1));
            }
            c2.close();

            sql = "SELECT count(s."+CODE+"), s."+CODEUSER+",u."+UsersController.USERNAME+" " +
                    "FROM "+TABLE_NAME_HISTORY+" s " +
                    "INNER JOIN "+UsersController.TABLE_NAME+" u on u."+UsersController.CODE+" = s."+CODEUSER+" " +
                    "WHERE s."+STATUS+" = '"+CODES.CODE_ORDER_STATUS_CLOSED+"' "+
                    "GROUP BY s."+CODEUSER+", u."+UsersController.USERNAME+" " +
                    "ORDER BY 1 DESC " +
                    "LIMIT 1";

            Cursor c3 = DB.getInstance(context).getReadableDatabase().rawQuery(sql,null);
            if (c3.moveToFirst()) {
                map.put("MSS", c3.getString(2));
            }
            c3.close();

       /* sql = "SELECT ";
        Cursor c3 =*/
        }catch(Exception e){
            e.printStackTrace();
        }
        return map;
    }

    public ArrayList<PercentRowModel> getVentasReportSellers(String status, String dateIni, String dateEnd){

        ArrayList<PercentRowModel> list = new ArrayList<>();
        int total = 0;
        try {
            String sql = "SELECT count(s." + CODE + ") " +
                    "FROM " + TABLE_NAME_HISTORY + " s "+
                    "WHERE s."+STATUS+" = '"+status+"' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') ";
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            if (c.moveToFirst()) {
                total = c.getInt(0);
            }c.close();


            sql = "SELECT count(s." + CODE + ") as cantidad, sum(s."+TOTAL+") as monto, s."+CODEUSER+",u."+UsersController.USERNAME+" as description, ut."+UserTypesController.DESCRIPTION+" as rol " +
                    ",( CAST(count(s." + CODE + ") AS REAL) * 100.0 /(" + total + ")) as percent   " +
                    "FROM " + TABLE_NAME_HISTORY + " s  " +
                    "INNER JOIN "+UsersController.TABLE_NAME+" u on u."+UsersController.CODE+" = s."+CODEUSER+" "+
                    "INNER JOIN "+UserTypesController.TABLE_NAME+" ut on ut."+UserTypesController.CODE+" = u."+UsersController.ROLE+" "+
                    "WHERE s."+STATUS+" = '"+status+"'  AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') "+
                    "GROUP BY s." + CODEUSER + ", u."+UsersController.USERNAME+", ut."+UserTypesController.DESCRIPTION+" " +
                    "ORDER BY 6 DESC, 5 DESC ";
            Cursor c2 = DB.getInstance(context).getReadableDatabase().rawQuery(sql,null);
            while (c2.moveToNext()) {
                list.add(new PercentRowModel(Math.round(c2.getDouble(c2.getColumnIndex("percent"))) + "%",
                        c2.getString(c2.getColumnIndex("description")),
                        c2.getString(c2.getColumnIndex("cantidad")),
                        c2.getString(c2.getColumnIndex("monto"))));
            }c2.close();

       /* sql = "SELECT ";
        Cursor c3 =*/
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public HashMap<String, String> getDevolucionesReport(String dateIni, String dateEnd){
        HashMap<String, String> map = new HashMap<>();
        map.put("TD", "0");//TOTAL DEVUELTO
        map.put("TP", "0");//TOTAL PERDIDA
        map.put("MF", "");//MOTIVO FRECUENTE
        map.put("MRS", "");//MOST RETURN SELLER
        try {
            String sql = "SELECT count(s." + CODE + "), sum(s." + TOTAL + ") " +
                    "FROM " + TABLE_NAME_HISTORY + " s "+
                    "WHERE s."+STATUS+" = '"+CODES.CODE_ORDER_STATUS_CANCELED+"' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') ";
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                String totalOrdenes = c.getString(0);
                String totalMonto = c.getString(1);
                map.put("TD", totalOrdenes);
                map.put("TP", totalMonto);
            }
            c.close();

            sql = "SELECT count(s." + CODE + "), s."+REASONDESCRIPTION+" " +
                    "FROM " + TABLE_NAME_HISTORY + " s  "+
                    "WHERE s."+STATUS+" = '"+CODES.CODE_ORDER_STATUS_CANCELED+"'  AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') "+
                    "GROUP BY s." + REASONDESCRIPTION +" " +
                    "ORDER BY 1 DESC " +
                    "LIMIT 1 ";
            Cursor c2 = DB.getInstance(context).getReadableDatabase().rawQuery(sql,null);
            if (c2.moveToFirst()) {
                map.put("MF", c2.getString(1));
            }
            c2.close();

            sql = "SELECT count(s."+CODE+"), s."+CODEUSER+",u."+UsersController.USERNAME+" " +
                  "FROM "+TABLE_NAME_HISTORY+" s " +
                  "INNER JOIN "+UsersController.TABLE_NAME+" u on u."+UsersController.CODE+" = s."+CODEUSER+" " +
                  "WHERE s."+STATUS+" = '"+CODES.CODE_ORDER_STATUS_CANCELED+"' "+
                  "GROUP BY s."+CODEUSER+", u."+UsersController.USERNAME+" " +
                  "ORDER BY 1 DESC " +
                  "LIMIT 1";

            Cursor c3 = DB.getInstance(context).getReadableDatabase().rawQuery(sql,null);
            if (c3.moveToFirst()) {
                map.put("MRS", c3.getString(2));
            }
            c3.close();

       /* sql = "SELECT ";
        Cursor c3 =*/
        }catch(Exception e){
            e.printStackTrace();
        }
        return map;
    }
/*
    public ArrayList<KV2> getTopSalesProducts(String dateIni, String dateEnd){
        ArrayList<KV2> list = new ArrayList<>();
        try {
            String where = STATUS + " = '" + CODES.CODE_ORDER_STATUS_CLOSED + "' " +
            "AND julianday(substr("+DATE+",1,4)||'-'||substr("+DATE+",5,2)||'-'||substr("+DATE+",7,2)||' '||substr("+DATE+",10,length("+DATE+")))  "+
            "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') ";
            double totalCloseSales = getCountHistory(where);

            String sql = "SELECT count(sd." + DETAIL_CODEPRODUCT + "), p." + ProductsController.DESCRIPTION + " as description, ( CAST(count(sd." + DETAIL_CODEPRODUCT + ") AS REAL) * 100.0 /(" + totalCloseSales + ")) as percent," +
                    "pt."+ProductsTypesController.DESCRIPTION+" as familia " +
                    "FROM " + TABLE_NAME_DETAIL_HISTORY + " sd " +
                    "INNER JOIN " + TABLE_NAME_HISTORY + " s on s." + CODE + " = sd." + DETAIL_CODESALES + "  AND s." + STATUS + " = '" + CODES.CODE_ORDER_STATUS_CLOSED + "' " +
                    "INNER JOIN " + ProductsController.TABLE_NAME + " p on p." + ProductsController.CODE + " = sd." + DETAIL_CODEPRODUCT + " " +
                    "INNER JOIN "+ ProductsTypesController.TABLE_NAME+" pt on pt."+ProductsTypesController.CODE+" = p."+ProductsController.TYPE+" "+
                    "WHERE s." + STATUS + " = '" + CODES.CODE_ORDER_STATUS_CLOSED + "' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') "+
                    "GROUP BY pt." + ProductsTypesController.DESCRIPTION + ", sd." + DETAIL_CODEPRODUCT + ", p." + ProductsController.DESCRIPTION + " " +
                    "HAVING  ( CAST(count(sd." + DETAIL_CODEPRODUCT + ") AS REAL) * 100.0 /(" + totalCloseSales + ")) > 0 "+
                    "ORDER BY 4 DESC, 3 DESC, 2 DESC ";//tipo, porcentaje, descripcion;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                list.add(new KV2(Math.round(c.getDouble(c.getColumnIndex("percent"))) + "%",
                        c.getString(c.getColumnIndex("description")),
                        c.getString(c.getColumnIndex("familia"))));
            }c.close();
        }catch(Exception e){
           e.printStackTrace();
        }

       return list;
    }*/


    public ArrayList<PercentRowModel> getTopSalesProducts(String familia, String grupo, String dateIni, String dateEnd){
        ArrayList<PercentRowModel> list = new ArrayList<>();
        try {
            String wFamilia = (familia != null && !familia.equals("0"))?" AND p."+ ProductsController.TYPE+" = '"+familia+"' ":" ";
            String wGrupo = (grupo != null && !grupo.equals("0"))?" AND p."+ ProductsController.SUBTYPE+" = '"+grupo+"' ":" ";
            String where = " WHERE "+STATUS + " = '" + CODES.CODE_ORDER_STATUS_CLOSED + "' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  "+
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') ";//+
            //        wFamilia+
            //        wGrupo;
            String queryCount = "SELECT sum(sd." + DETAIL_QUANTITY + ") " +
                    "FROM " + TABLE_NAME_DETAIL_HISTORY + " sd " +
                    "INNER JOIN " + TABLE_NAME_HISTORY + " s on s." + CODE + " = sd." + DETAIL_CODESALES + "  " +
                    "INNER JOIN " + ProductsController.TABLE_NAME + " p on p." + ProductsController.CODE + " = sd." + DETAIL_CODEPRODUCT + " " +
                    where;

            Cursor cu = DB.getInstance(context).getReadableDatabase().rawQuery(queryCount, null);
            double totalCloseSales = 0.0;
            if(cu.moveToFirst()){
            totalCloseSales = cu.getDouble(0);
            }cu.close();

            String sql = "SELECT sum(sd." + DETAIL_QUANTITY + ") as cantidad, sum(ifnull(sd."+DETAIL_PRICE+", 0.0)) as monto, p." + ProductsController.DESCRIPTION + " as description, ( CAST(sum(sd." + DETAIL_QUANTITY + ") AS REAL) * 100.0 /(" + totalCloseSales + ")) as percent," +
                    "p."+ProductsController.TYPE+" as "+ProductsController.TYPE+", p."+ProductsController.SUBTYPE+" as "+ProductsController.SUBTYPE+"  " +
                    "FROM " + TABLE_NAME_DETAIL_HISTORY + " sd " +
                    "INNER JOIN " + TABLE_NAME_HISTORY + " s on s." + CODE + " = sd." + DETAIL_CODESALES + "  AND s." + STATUS + " = '" + CODES.CODE_ORDER_STATUS_CLOSED + "' " +
                    "INNER JOIN " + ProductsController.TABLE_NAME + " p on p." + ProductsController.CODE + " = sd." + DETAIL_CODEPRODUCT + " " +
                    "INNER JOIN "+ ProductsTypesController.TABLE_NAME+" pt on pt."+ProductsTypesController.CODE+" = p."+ProductsController.TYPE+" "+
                    "WHERE s." + STATUS + " = '" + CODES.CODE_ORDER_STATUS_CLOSED + "' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') "+
                     wFamilia+
                     wGrupo+
                    "GROUP BY pt." + ProductsTypesController.DESCRIPTION + ", sd." + DETAIL_CODEPRODUCT + ", p." + ProductsController.DESCRIPTION + " " +
                    "HAVING  ( CAST(count(sd." + DETAIL_CODEPRODUCT + ") AS REAL) * 100.0 /(" + totalCloseSales + ")) > 0 ";

            if((familia != null && !familia.equals("0"))){
                sql = "SELECT * FROM ("+sql+") as tabla WHERE "+ProductsController.TYPE+" = '"+familia+"' ";
                if(grupo != null && !grupo.equals("0")){
                   sql+= " AND "+ProductsController.SUBTYPE+" = '"+grupo+"' ";
                }
            }
            sql+= "ORDER BY 4 DESC, 3 DESC ";//porcentaje, descripcion;

            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                list.add(new PercentRowModel(Math.round(c.getDouble(c.getColumnIndex("percent"))) + "%",
                        c.getString(c.getColumnIndex("description")),
                        c.getString(c.getColumnIndex("cantidad")),
                        c.getString(c.getColumnIndex("monto"))));
            }c.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

    public ArrayList<PercentRowModel> getTopSalesGeneraldata(String familia,String dateIni, String dateEnd){
        ArrayList<PercentRowModel> list = new ArrayList<>();
        try {
            String wFamilia = (familia.equals("1"))?" AND p."+ ProductsController.TYPE+" = '"+familia+"' ":" ";
            String where = " WHERE "+STATUS + " = '" + CODES.CODE_ORDER_STATUS_CLOSED + "' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  "+
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') "+
                    wFamilia;

            String queryCount = "SELECT sum(sd." + DETAIL_QUANTITY + ") " +
                    "FROM " + TABLE_NAME_DETAIL_HISTORY + " sd " +
                    "INNER JOIN " + TABLE_NAME_HISTORY + " s on s." + CODE + " = sd." + DETAIL_CODESALES + "  " +
                    "INNER JOIN " + ProductsController.TABLE_NAME + " p on p." + ProductsController.CODE + " = sd." + DETAIL_CODEPRODUCT + " " +
                    where;

            Cursor cu = DB.getInstance(context).getReadableDatabase().rawQuery(queryCount, null);
            double totalCloseSales = 0.0;
            if(cu.moveToFirst()){
                totalCloseSales = cu.getDouble(0);
            }cu.close();


            String sql = "SELECT sum(sd."+DETAIL_QUANTITY+") AS cantidad, sum(ifnull(sd."+DETAIL_PRICE+", 0.0)) as monto,  pt."+ProductsTypesController.DESCRIPTION+" as description, ( CAST(sum(sd."+DETAIL_QUANTITY+") AS REAL) * 100.0 /(" + totalCloseSales + ")) as percent," +
                    "p."+ProductsController.TYPE+" as "+ProductsController.TYPE+", p."+ProductsController.SUBTYPE+" as "+ProductsController.SUBTYPE+" " +
                    "FROM " + TABLE_NAME_DETAIL_HISTORY + " sd " +
                    "INNER JOIN " + TABLE_NAME_HISTORY + " s on s." + CODE + " = sd." + DETAIL_CODESALES + "  " +
                    "INNER JOIN " + ProductsController.TABLE_NAME + " p on p." + ProductsController.CODE + " = sd." + DETAIL_CODEPRODUCT + " " +
                    "INNER JOIN "+ ProductsTypesController.TABLE_NAME+" pt on pt."+ProductsTypesController.CODE+" = p."+ProductsController.TYPE+" "+
                    "WHERE s." + STATUS + " = '" + CODES.CODE_ORDER_STATUS_CLOSED + "' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') "+
                    wFamilia+
                    "GROUP BY p." + ProductsController.TYPE + " " ;
                    //"HAVING  ( CAST(count(sd." + DETAIL_CODEPRODUCT + ") AS REAL) * 100.0 /(" + totalCloseSales + ")) > 0 "+


            if(!familia.equals("0")){
                 sql = "SELECT * FROM ("+sql+") as tabla where "+ProductsController.TYPE+" = '"+familia+"' ";
            }

            sql +=  "ORDER BY 4 DESC, 3 DESC ";// descripcion;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                list.add(new PercentRowModel(Math.round(c.getDouble(c.getColumnIndex("percent"))) + "%",
                        c.getString(c.getColumnIndex("description")),
                        c.getString(c.getColumnIndex("cantidad")),
                        c.getString(c.getColumnIndex("monto"))));
            }c.close();

        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }


    public ArrayList<PercentRowModel> getTopDevolucionesGeneraldata(String dateIni, String dateEnd){
        ArrayList<PercentRowModel> list = new ArrayList<>();
        try {
            String where = " WHERE s."+STATUS + " = '" + CODES.CODE_ORDER_STATUS_CANCELED + "' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  "+
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') ";

            String queryCount = "SELECT count(s." + CODE + ") " +
                    "FROM " + TABLE_NAME_HISTORY + " s " +
                    where;

            Cursor cu = DB.getInstance(context).getReadableDatabase().rawQuery(queryCount, null);
            double totalSales = 0.0;
            if(cu.moveToFirst()){
                totalSales = cu.getDouble(0);
            }cu.close();

            String sql = "SELECT count(s."+CODE+") AS cantidad, sum(ifnull(sd."+DETAIL_PRICE+", 0.0)) as monto,s."+CODEREASON+",  s."+REASONDESCRIPTION+" as description, ( CAST(count(s."+CODE+") AS REAL) * 100.0 /(" + totalSales + ")) as percent " +
                    "FROM " + TABLE_NAME_DETAIL_HISTORY + " sd " +
                    "INNER JOIN " + TABLE_NAME_HISTORY + " s on s." + CODE + " = sd." + DETAIL_CODESALES + "  " +
                    "WHERE s." + STATUS + " = '" + CODES.CODE_ORDER_STATUS_CANCELED + "' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') "+
                    "GROUP BY s." + CODEREASON + " " ;


            sql +=  "ORDER BY 5 DESC, 4 DESC ";// descripcion;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                list.add(new PercentRowModel(Math.round(c.getDouble(c.getColumnIndex("percent"))) + "%",
                        c.getString(c.getColumnIndex("description")),
                        c.getString(c.getColumnIndex("cantidad")),
                        c.getString(c.getColumnIndex("monto"))));
            }c.close();

        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }



    public void getHistoricDataToSearch(int status, Date dateIni, Date dateEnd, OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete,  OnFailureListener failute){
       if(status == -1){
           getReferenceMainHistoryFireStore().
                   whereGreaterThan(DATE, dateIni).//mayor que, ya que las fechas (la que buscamos de la DB) tienen hora, minuto y segundos.
                   whereLessThanOrEqualTo(DATE, dateEnd).get().
                   addOnSuccessListener(success).addOnCompleteListener(complete).
                   addOnFailureListener(failute);
       }else {
           getReferenceMainHistoryFireStore().whereEqualTo(STATUS, status).
                   whereGreaterThan(DATE, dateIni).//mayor que, ya que las fechas (la que buscamos de la DB) tienen hora, minuto y segundos.
                   whereLessThanOrEqualTo(DATE, dateEnd).get().
                   addOnSuccessListener(success).addOnCompleteListener(complete).
                   addOnFailureListener(failute);
       }

    }


    public void proccessQuerySnapshotHistoricData(QuerySnapshot querySnapshot){
        try {
            if (!querySnapshot.isEmpty()) {

                for (DocumentSnapshot doc : querySnapshot) {
                    Sales s = doc.toObject(Sales.class);
                    if (updateHistory(s) <= 0){
                        insertHistory(s);
                    List<Map<String, Object>> x = (List<Map<String, Object>>) doc.getData().get("salesdetails");
                    for (Map<String, Object> m : x) {
                        insert_DetailHistory(new SalesDetails(m));
                    }
                }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Date getLastDateSavedHistory(int status){
        Date date = null;
        String sql = "SELECT "+DATE+" as DATE " +
                "FROM "+TABLE_NAME_HISTORY+" " +
                "WHERE "+STATUS+" = '"+status+"' "+
                "ORDER BY "+DATE+" DESC " +
                "LIMIT 1 ";
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        if(c.moveToFirst()){
            date = Funciones.parseStringToDate(c.getString(c.getColumnIndex("DATE")));
        }c.close();
        return date;
    }

    /**
     * obtiene la fecha mas baja guardada en la base de datos local en la tabla de historico.
     * @param status
     * @return
     */
    public Date getLastInitialDateSavedHistory(int status){
        Date date = null;
        String sql = "SELECT "+DATE+" as DATE " +
                "FROM "+TABLE_NAME_HISTORY+" " +
                "WHERE "+STATUS+" = '"+status+"' "+
                "ORDER BY "+DATE+" ASC " +
                "LIMIT 1 ";
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        if(c.moveToFirst()){
            date = Funciones.parseStringToDate(c.getString(c.getColumnIndex("DATE")));
        }c.close();
        return date;
    }

    public double getCountHistory(String where){
        double result = 0.0;
        String sql = "SELECT COUNT(*) " +
                "FROM "+TABLE_NAME_HISTORY;
        sql+=(where != null)?" WHERE "+where: "";
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        if(c.moveToFirst()){
            result = c.getDouble(0);
        }c.close();
        return result;
    }

    public void fillSpnVista(Spinner spn){
        ArrayList<KV> spnList = new ArrayList<>();
        spnList.add(new KV("0", "Detalle")); spnList.add(new KV("1", "General"));
        spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,spnList));
    }

    public ArrayList<OrderReceiptModel> getOrderReceipt(String codeAreaDetail){

        String status = CODES.CODE_ORDER_STATUS_DELIVERED+"";
        ArrayList<OrderReceiptModel> result = new ArrayList<>();
        String sql = "SELECT a."+AreasController.CODE+" AS CODEAREA, a."+AreasController.DESCRIPTION+" AS  DESCRIPTIONAREA, " +
                "ad."+AreasDetailController.CODE+" AS CODEMESA, ad."+AreasDetailController.DESCRIPTION+" AS DESCRIPTIONMESA, " +
                "sum(s."+SalesController.TOTAL+") AS SALESTOTAL " +
                "FROM "+SalesController.TABLE_NAME+" s " +
                "INNER JOIN "+AreasDetailController.TABLE_NAME+" ad ON s."+CODEAREADETAIL+" = ad."+AreasDetailController.CODE+" " +
                "INNER JOIN "+AreasController.TABLE_NAME+" a ON ad."+AreasDetailController.CODEAREA+" = a."+AreasController.CODE+" " +
                "WHERE s."+SalesController.STATUS+" = ? AND s."+SalesController.CODEAREADETAIL+" = ? " +
                "GROUP BY a."+AreasController.CODE+", ad."+AreasDetailController.CODE;

        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{status, codeAreaDetail});
        while (c.moveToNext()){
            result.add(new OrderReceiptModel(Funciones.generateCode(),
                    c.getString(c.getColumnIndex("CODEAREA")),
                    c.getString(c.getColumnIndex("DESCRIPTIONAREA")),
                    c.getString(c.getColumnIndex("CODEMESA")),
                    c.getString(c.getColumnIndex("DESCRIPTIONMESA")),
                    c.getDouble(c.getColumnIndex("SALESTOTAL"))));
        }c.close();

        return result;

    }

    public ArrayList<ReceiptResumeModel> getOrderReceiptResume(String codeAreaDetail){
        String status = CODES.CODE_ORDER_STATUS_DELIVERED+"";
        ArrayList<ReceiptResumeModel> result = new ArrayList<>();
        String sql = "SELECT sd."+SalesController.DETAIL_CODEPRODUCT+" AS CODEPRODUCT, p."+ProductsController.DESCRIPTION+" AS  PRODUCTDESCRIPTION, " +
                "sd."+SalesController.DETAIL_CODEUND+" AS CODEMEASURE, mu."+MeasureUnitsController.DESCRIPTION+" AS MEASUREDESCRIPTION, "+
                "SUM(sd."+SalesController.DETAIL_QUANTITY+") AS QUANTITY, SUM(sd."+SalesController.DETAIL_PRICE+" * "+SalesController.DETAIL_QUANTITY+" ) AS SALESTOTAL "+
                "FROM "+SalesController.TABLE_NAME+" s " +
                "INNER JOIN "+SalesController.TABLE_NAME_DETAIL+" sd on s."+SalesController.CODE+" = sd."+SalesController.DETAIL_CODESALES+" "+
                "INNER JOIN "+ProductsController.TABLE_NAME+" p on p."+ProductsController.CODE+" = sd."+SalesController.DETAIL_CODEPRODUCT+" "+
                "LEFT JOIN "+MeasureUnitsController.TABLE_NAME+" mu on mu."+MeasureUnitsController.CODE+" = sd."+SalesController.DETAIL_CODEUND+" "+
                "INNER JOIN "+AreasDetailController.TABLE_NAME+" ad ON s."+CODEAREADETAIL+" = ad."+AreasDetailController.CODE+" " +
                "WHERE s."+SalesController.STATUS+" = ? AND s."+SalesController.CODEAREADETAIL+" = ? " +
                "GROUP BY sd."+SalesController.DETAIL_CODEPRODUCT+",sd."+SalesController.DETAIL_CODEUND+" " +
                "ORDER BY p."+ProductsController.DESCRIPTION+",  mu."+MeasureUnitsController.DESCRIPTION;

        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{status, codeAreaDetail});
        while (c.moveToNext()){
            result.add(new ReceiptResumeModel( codeAreaDetail,
                    c.getString(c.getColumnIndex("CODEPRODUCT")),
                    c.getString(c.getColumnIndex("PRODUCTDESCRIPTION")),
                    c.getString(c.getColumnIndex("CODEMEASURE")),
                    c.getString(c.getColumnIndex("MEASUREDESCRIPTION")),
                    c.getString(c.getColumnIndex("QUANTITY")),
                    c.getString(c.getColumnIndex("SALESTOTAL"))));
        }c.close();

        return result;
    }

    public ArrayList<Sales> getDeliveredOrdersByCodeAreadetail(String codeAreaDetail){
        ArrayList<Sales> deliveredOrders = new ArrayList<>();
        String where = SalesController.STATUS+" = ? AND "+SalesController.CODEAREADETAIL+" = ? ";
        Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns, where,new String[]{CODES.CODE_ORDER_STATUS_DELIVERED+"", codeAreaDetail},null,null,null);
        while(c.moveToNext()){
            deliveredOrders.add(new Sales(c));
        }
        return deliveredOrders;
    }


    public Receipts getReceiptByCodeAreadetail(String codeAreaDetail){
        Receipts receipt = null;
        String where = "s."+SalesController.STATUS+" = ? AND s."+SalesController.CODEAREADETAIL+" = ? ";
        String sql = "SELECT SUM(sd."+SalesController.DETAIL_PRICE+" * sd."+SalesController.DETAIL_QUANTITY+" ) AS TOTAL " +
                "FROM "+SalesController.TABLE_NAME+" s " +
                "INNER JOIN "+SalesController.TABLE_NAME_DETAIL+" sd on s."+SalesController.CODE+" = sd."+SalesController.DETAIL_CODESALES+" "+
                " WHERE "+where;
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql,new String[]{CODES.CODE_ORDER_STATUS_DELIVERED+"", codeAreaDetail});
        if(c.moveToFirst()){
            double total = c.getDouble(0);
            receipt = new Receipts(Funciones.generateCode(),Funciones.getCodeuserLogged(context),codeAreaDetail,null,null,total,0.0,0.0,total);
        }c.close();
        return receipt;
    }

    public void closeOrders(Receipts receipt, ArrayList<Sales> deliveredSales){
        if(deliveredSales != null && deliveredSales.size() > 0) {

            for(Sales sales : deliveredSales){
                sales.setSTATUS(CODES.CODE_ORDER_STATUS_CLOSED);
                sales.setCODERECEIPT(receipt.getCode());
                sales.setMDATE(null);//actualizar fecha de ultima actualizacion.


                ArrayList<Sales> s = new ArrayList<>();
                s.add(sales);
                ///////////////////////////////////////////////////////////////////
                ///////////   ENVIANDO AL HISTORICO     ///////////////////////////

                sendToHistory(s);
                ///////////////////////////////////////////////////////////////////

                ///////////////////////////////////////////////////////////////////
                //////      ELIMINANDO DE LA TABLA SALES Y SALES_DETAIL EN FIREBASE   ////////
                massiveDelete(s);
                //////////////////////////////////////////////////////////////////

                ///////////////////////////////////////////////////////////////////
                //////////  ELIMINANDOLA EN EL MOVIL   ///////////////////////////
                deleteHeadDetail(sales);//esto es porque la lista se actualizara antes de que el server retorne la actualizacion.
                //////////////////////////////////////////////////////////////////


            }

        }
    }
}
