package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

import far.com.eatit.CloudFireStoreObjects.Combos;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class CombosController {
    public static final String TABLE_NAME ="COMBOS";
    public static  String CODE = "code", CODEPRODUCT = "codeproduct" ,
            CODEPRODUCTCOMBO = "codeproductcombo",CODEUNDPRODUCT = "codeundproduct",
            DATE = "date", MDATE= "mdate";
   public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
           +CODE+" TEXT, "+CODEPRODUCT+" TEXT, "+CODEUNDPRODUCT+" TEXT, "+CODEPRODUCTCOMBO+" TEXT, "+DATE+" TEXT," +
           MDATE+" TEXT)";
    Context context;
    FirebaseFirestore db;
    public CombosController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersCombos);
        return reference;
    }

    public long insert(Combos c){
        ContentValues cv = new ContentValues();
        cv.put(CODE,c.getCODE() );
        cv.put(CODEPRODUCT,c.getCODEPRODUCT());
        cv.put(CODEUNDPRODUCT, c.getCODEUNDPRODUCT());
        cv.put(CODEPRODUCTCOMBO,c.getCODEPRODUCTCOMBO() );
        cv.put(DATE, Funciones.getFormatedDate((Date) c.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate((Date) c.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Combos c, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,c.getCODE() );
        cv.put(CODEPRODUCT,c.getCODEPRODUCT());
        cv.put(CODEUNDPRODUCT, c.getCODEUNDPRODUCT());
        cv.put(CODEPRODUCTCOMBO,c.getCODEPRODUCTCOMBO() );
        cv.put(MDATE, Funciones.getFormatedDate((Date) c.getMDATE()));

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
            Task<QuerySnapshot> combos = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersCombos).get();
            combos.addOnSuccessListener(onSuccessListener);
            combos.addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getAllDataFromFireBase(String key, OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> measureUnits = getReferenceFireStore().get();
            measureUnits.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            Combos object = dc.getDocument().toObject(Combos.class);
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
}
