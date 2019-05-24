package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.SimpleSeleccionRowModel;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.MeasureUnits;
import far.com.eatit.CloudFireStoreObjects.ProductsMeasure;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class ProductsMeasureController {

    public static final String TABLE_NAME ="PRODUCTSMEASURE";
    public static  String CODE = "code", CODEPRODUCT = "codeproduct", CODEMEASURE = "codemeasure" ,
            DATE = "date", MDATE= "mdate";
    private static String[] columns = new String[]{CODE, CODEPRODUCT, CODEMEASURE, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT, "+CODEPRODUCT+" TEXT, "+CODEMEASURE+" TEXT,"+DATE+" TEXT," + MDATE+" TEXT)";
    Context context;
    FirebaseFirestore db;
    private static  ProductsMeasureController instance;

    private ProductsMeasureController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static ProductsMeasureController getInstance(Context c){
        if(instance == null){
            instance = new ProductsMeasureController(c);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
       return db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersProductsMeasure);
    }

    public ArrayList<ProductsMeasure>getProductsMeasureByCodeProduct(String codeProduct){
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
                    "FROM "+MeasureUnitsController.TABLE_NAME+" um " +
                    "INNER JOIN "+TABLE_NAME+" pm on pm."+CODEMEASURE+" = um."+MeasureUnitsController.CODE+" "+
                    "WHERE "+CODEPRODUCT+" = ?";
            String[] args = new String[]{codeProduct};

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
            String sql = "SELECT pm." + CODEMEASURE + " AS CODE, mu." + MeasureUnitsController.DESCRIPTION + " AS DESCRIPTION " +
                    "FROM " + TABLE_NAME + " pm " +
                    "INNER JOIN " + MeasureUnitsController.TABLE_NAME + " mu ON pm." + CODEMEASURE + " = mu." + MeasureUnitsController.CODE + " " +
                    "WHERE pm." + CODEPRODUCT + " = ? ";
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{codeProduct});
            while (c.moveToNext()) {
                result.add(new SimpleSeleccionRowModel(c.getString(c.getColumnIndex("CODE")),
                        c.getString(c.getColumnIndex("DESCRIPTION")), true));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }
    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> combos = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersProductsMeasure).get();
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

}
