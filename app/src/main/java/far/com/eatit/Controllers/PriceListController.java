package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;

import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.PriceList;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class PriceListController {
    public static final String TABLE_NAME ="PRICELIST";
    public static  String CODE = "code", CODEPRODUCT = "codeproduct" ,
            CODEUND = "codeund",PRICE = "price", DATE = "date", MDATE="mdate";
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT, "+CODEPRODUCT+" TEXT, "+CODEUND+" TEXT, "+PRICE+" DECIMAL(11,3), "+DATE+" TEXT, "+MDATE+" TEXT )";
    public static String[] columns = {CODE, CODEPRODUCT, CODEUND, PRICE, DATE, MDATE};
    Context context;
    FirebaseFirestore db;
    private static PriceListController instance;

    public PriceListController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }
    public static PriceListController getInstance(Context c){
        if(instance == null){
            instance = new PriceListController(c);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersPriceList);
        return reference;
    }

    public long insert(PriceList pl){
        ContentValues cv = new ContentValues();
        cv.put(CODE,pl.getCODE() );
        cv.put(CODEPRODUCT,pl.getCODEPRODUCT());
        cv.put(CODEUND, pl.getCODEUND());
        cv.put(PRICE,pl.getPRICE() );
        cv.put(DATE, Funciones.getFormatedDate((Date)pl.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate((Date)pl.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(PriceList pl, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,pl.getCODE() );
        cv.put(CODEPRODUCT,pl.getCODEPRODUCT());
        cv.put(CODEUND, pl.getCODEUND());
        cv.put(PRICE,pl.getPRICE() );
        cv.put(MDATE, Funciones.getFormatedDate((Date)pl.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> priceList = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersPriceList).get();
            priceList.addOnSuccessListener(onSuccessListener);
            priceList.addOnFailureListener(onFailureListener);
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
                            PriceList object = dc.getDocument().toObject(PriceList.class);
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

    public ArrayList<PriceList> getPriceLists(String where, String[]args, String orderBy){
        ArrayList<PriceList> result = new ArrayList<>();
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
            while(c.moveToNext()){
                result.add(new PriceList(c));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }


    public ArrayList<DocumentReference> getReferences(String field, String value){
        ArrayList<DocumentReference> references = new ArrayList<>();
        ArrayList<PriceList> objs = getPriceLists(field+" = ? ", new String[]{value}, null);
        if(objs != null){
            for(PriceList c: objs){
                references.add(getReferenceFireStore().document(c.getCODE()));
            }
        }
        return references;
    }


    public void searchChanges(boolean all, OnSuccessListener<QuerySnapshot> success,  OnFailureListener failure){

        Date mdate = all?null: DB.getLastMDateSaved(context, TABLE_NAME);
        if(mdate != null){
            getReferenceFireStore().
                    whereGreaterThan(MDATE, mdate).//mayor que, ya que las fechas (la que buscamos de la DB) tienen hora, minuto y segundos.
                    get().
                    addOnSuccessListener(success).
                    addOnFailureListener(failure);
        }else{//TODOS
            getReferenceFireStore().
                    get().
                    addOnSuccessListener(success).
                    addOnFailureListener(failure);
        }

    }

    public void consumeQuerySnapshot(boolean clear, QuerySnapshot querySnapshot){
        if(clear){
            delete(null, null);
        }
        if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
            for(DocumentSnapshot doc: querySnapshot){
                PriceList obj = doc.toObject(PriceList.class);
                if(update(obj, CODE+"=?", new String[]{obj.getCODE()}) <=0){
                    insert(obj);
                }
            }
        }

    }
}
