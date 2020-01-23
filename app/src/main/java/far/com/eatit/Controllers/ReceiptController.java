package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.example.bluetoothlibrary.Printer.Print;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.ReceiptResumeModel;
import far.com.eatit.Adapters.Models.ReceiptSavedModel;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.Receipts;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;

public class ReceiptController {
    public static final String TABLE_NAME ="RECEIPTS";
    //public static final String TABLE_NAME_HISTORY ="RECEIPTS_HISTORY";
    public static  String CODE = "code",CODEUSER = "codeuser",CODEAREADETAIL = "codeareadetail",STATUS = "status",  NCF = "ncf" ,SUBTOTAL="subtotal",TAXES = "taxes", DISCOUNT="discount", TOTAL = "total",
            DATE = "date", MDATE = "mdate";
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT,"+CODEUSER+" TEXT,"+CODEAREADETAIL+" TEXT,"+STATUS+" TEXT, "+NCF+" TEXT,"+SUBTOTAL+" NUMERIC,"+TAXES+" NUMERIC,"+DISCOUNT+" NUMERIC, "+TOTAL+", " +
            ""+DATE+" TEXT, "+MDATE+" TEXT)";
    public static String[] columns = new String[]{CODE,CODEUSER,CODEAREADETAIL,STATUS, NCF,SUBTOTAL,TAXES,DISCOUNT,TOTAL, DATE, MDATE};
    Context context;
    FirebaseFirestore db;
    private static ReceiptController instance;
    private ReceiptController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }
    public static ReceiptController getInstance(Context c){
        if(instance == null){
            instance = new ReceiptController(c);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersReceipts);
        return reference;
    }

    public long insert(Receipts r){
        ContentValues cv = new ContentValues();
        cv.put(CODE,r.getCode() );
        cv.put(CODEUSER, r.getCodeuser());
        cv.put(CODEAREADETAIL, r.getCodeareadetail());
        cv.put(STATUS, r.getStatus());
        cv.put(NCF,r.getNcf());
        cv.put(SUBTOTAL, r.getSubTotal());
        cv.put(TAXES, r.getTaxes());
        cv.put(DISCOUNT, r.getDiscount());
        cv.put(TOTAL, r.getTotal());
        cv.put(DATE, Funciones.getFormatedDate(r.getDate()));
        cv.put(MDATE, Funciones.getFormatedDate(r.getMdate()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public ArrayList<Receipts> getReceipts(String[] camposFiltros, String[]argumentos, String campoOrderBy){

        ArrayList<Receipts> result = new ArrayList<>();
        if(campoOrderBy == null){
            campoOrderBy=DATE;
        }
        try {
            Cursor c =  DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, ((camposFiltros!=null)?DB.getWhereFormat(camposFiltros):null), argumentos, null, null, campoOrderBy);
            while (c.moveToNext()){
                result.add(new Receipts(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    public void getDataFromFireBase(OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> receipts = getReferenceFireStore().get();
            receipts.addOnSuccessListener(onSuccessListener);
            receipts.addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void getAllDataFromFireBase(String key, OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> reference = getReferenceFireStore().get();
            reference.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            Receipts object = dc.getDocument().toObject(Receipts.class);
                            String where = CODE+" = ?";
                            String[]args = new String[]{object.getCode()};
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

    public void sendToFireBase(Receipts receipt){
        try {
            WriteBatch lote = db.batch();
                if (receipt.getMdate() == null) {
                    lote.set(getReferenceFireStore().document(receipt.getCode()), receipt.toMap());
                } else {
                    lote.update(getReferenceFireStore().document(receipt.getCode()), receipt.toMap());
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


    public ArrayList<ReceiptSavedModel> getReceiptsSM(String codeAreaDetail){
        ArrayList<ReceiptSavedModel> result = new ArrayList<>();
        try {
            String sql = "SELECT r." + CODE + " as CODE,r."+STATUS+" as STATUS, r." + CODEUSER + " as CODEUSER, u." + UsersController.USERNAME + " as USERNAME, r." + NCF + " as NCF, " +
                    "ad." + AreasDetailController.CODEAREA + " as CODEAREA, a." + AreasController.DESCRIPTION + " as AREADESCRIPTION, ad." + AreasDetailController.CODE + " as CODEAREADETAIL, ad." + AreasDetailController.DESCRIPTION + " as AREADETAILDESCRIPTION, " +
                    "r." + SUBTOTAL + " as SUBTOTAL, r." + TAXES + " as TAXES, r." + DISCOUNT + " as DISCOUNT, r." + TOTAL + " as TOTAL, r." + DATE + " as DATE, r." + MDATE + " as MDATE " +
                    "FROM " + TABLE_NAME + " r " +
                    "INNER JOIN " + UsersController.TABLE_NAME + " u ON r." + CODEUSER + " = u." + UsersController.CODE + " " +
                    "INNER JOIN " + AreasDetailController.TABLE_NAME + " ad on r." + CODEAREADETAIL + " = ad." + AreasDetailController.CODE + " " +
                    "INNER JOIN " + AreasController.TABLE_NAME + " a on ad." + AreasDetailController.CODEAREA + " = a." + AreasController.CODE + " " +
                    "ORDER BY r." + DATE + " DESC";

            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                result.add(new ReceiptSavedModel(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }







}
