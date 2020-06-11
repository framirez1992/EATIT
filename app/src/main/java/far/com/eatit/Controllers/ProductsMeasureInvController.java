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

import far.com.eatit.Adapters.Models.SimpleSeleccionRowModel;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.ProductsMeasure;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class ProductsMeasureInvController {
    public static final String TABLE_NAME ="PRODUCTSMEASUREINV";
    public static  String CODE = "code", CODEPRODUCT = "codeproduct", CODEMEASURE = "codemeasure" ,
            ENABLED = "enabled", DATE = "date", MDATE= "mdate";
    private static String[] columns = new String[]{CODE, CODEPRODUCT, CODEMEASURE, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT, "+CODEPRODUCT+" TEXT, "+CODEMEASURE+" TEXT,"+ENABLED+" TEXT, "+DATE+" TEXT," + MDATE+" TEXT)";
    Context context;
    FirebaseFirestore db;
    private static  ProductsMeasureInvController instance;

    private ProductsMeasureInvController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static ProductsMeasureInvController getInstance(Context c){
        if(instance == null){
            instance = new ProductsMeasureInvController(c);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        return db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersProductsMeasureInv);
    }

    public ArrayList<ProductsMeasure> getProductsMeasureByCodeProduct(String codeProduct){
        String where = CODEPRODUCT+" = ? ";
        String[] args = new String[]{codeProduct};
        return  getProductsMeasure(where, args);
    }
    public ArrayList<ProductsMeasure>getProductsMeasure(String where, String[] args){
        ArrayList<ProductsMeasure> result = new ArrayList<>();
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, where, args, null, null, CODEMEASURE);
            while(c.moveToNext()){
                result.add(new ProductsMeasure(
                        c.getString(c.getColumnIndex(CODE)),
                        c.getString(c.getColumnIndex(CODEPRODUCT)),
                        c.getString(c.getColumnIndex(CODEMEASURE)),
                        0.0,
                        c.getString(c.getColumnIndex(ENABLED)).equals("1"),
                        c.getString(c.getColumnIndex(DATE)),
                        c.getString(c.getColumnIndex(MDATE))));

            }c.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public ArrayList<KV>getProductsMeasureKVByCodeProduct(String codeProduct){
        ArrayList<KV> result = new ArrayList<>();
        try {
            String sql = "select um."+MeasureUnitsController.CODE+" as CODE, um."+MeasureUnitsController.DESCRIPTION+" as DESCRIPTION " +
                    "FROM "+MeasureUnitsInvController.TABLE_NAME+" um " +
                    "INNER JOIN "+TABLE_NAME+" pm on pm."+CODEMEASURE+" = um."+MeasureUnitsController.CODE+" "+
                    "WHERE "+CODEPRODUCT+" = ? AND "+ENABLED+" = ?";
            String[] args = new String[]{codeProduct, "1"};

            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql,args );
            while(c.moveToNext()){
                result.add(new KV(c.getString(c.getColumnIndex("CODE")),
                        c.getString(c.getColumnIndex("DESCRIPTION"))));

            }c.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }
    public long insert(ProductsMeasure p){
        ContentValues cv = new ContentValues();
        cv.put(CODE, p.getCODE());
        cv.put(CODEPRODUCT,p.getCODEPRODUCT() );
        cv.put(CODEMEASURE,p.getCODEMEASURE());
        cv.put(ENABLED, p.getENABLED());
        cv.put(DATE, Funciones.getFormatedDate(p.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(p.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(ProductsMeasure p, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE, p.getCODE());
        cv.put(CODEPRODUCT,p.getCODEPRODUCT() );
        cv.put(CODEMEASURE,p.getCODEMEASURE());
        cv.put(ENABLED, p.getENABLED());
        cv.put(DATE, Funciones.getFormatedDate(p.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(p.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public ArrayList<SimpleSeleccionRowModel> getSSRMByCodeProduct(String codeProduct){
        ArrayList<SimpleSeleccionRowModel> result = new ArrayList<>();
        try {
            String sql = "SELECT pm." + CODEMEASURE + " AS CODE, mu." + MeasureUnitsController.DESCRIPTION + " AS DESCRIPTION, pm."+ENABLED+" AS ENABLED " +
                    "FROM " + TABLE_NAME + " pm " +
                    "INNER JOIN " + MeasureUnitsInvController.TABLE_NAME + " mu ON pm." + CODEMEASURE + " = mu." + MeasureUnitsController.CODE + " " +
                    "WHERE pm." + CODEPRODUCT + " = ? ";
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{codeProduct});
            while (c.moveToNext()) {
                result.add(new SimpleSeleccionRowModel(c.getString(c.getColumnIndex("CODE")),
                        c.getString(c.getColumnIndex("DESCRIPTION")), c.getString(c.getColumnIndex("ENABLED")).equals("1")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }
    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> combos = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersProductsMeasureInv).get();
            combos.addOnSuccessListener(onSuccessListener);
            combos.addOnFailureListener(onFailureListener);
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
                            ProductsMeasure object = dc.getDocument().toObject(ProductsMeasure.class);
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

    public void sendToFireBase(ArrayList<ProductsMeasure> pm){
        try {
            WriteBatch lote = db.batch();

            if (pm != null){
                for (ProductsMeasure obj : pm) {//Insertando o actualizando el nuevo detalle
                    if (obj.getMDATE() == null) {
                        lote.set(getReferenceFireStore().document(obj.getCODE()), obj.toMap());
                    } else {
                        lote.update(getReferenceFireStore().document(obj.getCODE()), obj.toMap());
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


    public ArrayList<ProductsMeasure> getdifference(ArrayList<ProductsMeasure> sdOriginal, ArrayList<ProductsMeasure> newsalesDetails){
        ArrayList<ProductsMeasure>toDelete = new ArrayList<>();
        for(ProductsMeasure del: sdOriginal){
            boolean delete = true;
            for(ProductsMeasure update: newsalesDetails){
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

    public ArrayList<DocumentReference> getReferences(String field, String value){
        ArrayList<DocumentReference> references = new ArrayList<>();
        ArrayList<ProductsMeasure> objs = getProductsMeasure(field+" = ? ", new String[]{value});
        if(objs != null){
            for(ProductsMeasure c: objs){
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
                ProductsMeasure obj = doc.toObject(ProductsMeasure.class);
                if(update(obj, CODE+"=?", new String[]{obj.getCODE()}) <=0){
                    insert(obj);
                }
            }
        }

    }
}
