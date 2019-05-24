package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.CloudFireStoreObjects.Areas;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class AreasController {
    public static String TABLE_NAME = "AREAS";
    public static String CODE = "code", DESCRIPTION = "description", ORDER = "orden", DATE="date", MDATE="mdate";
    public static String[]colums = new String[]{CODE, DESCRIPTION, ORDER, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT, "+DESCRIPTION+" TEXT, "+ORDER+" INTEGER, "+DATE+" TEXT, "+MDATE+" TEXT)";

    Context context;
    FirebaseFirestore db;
    private static AreasController instance;
    private AreasController(Context c){
        this.context = c;
        this.db = FirebaseFirestore.getInstance();
    }
    public static AreasController getInstance(Context context){
        if(instance == null){
            instance = new AreasController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersAreas);
        return reference;
    }
    public long insert(Areas a){
        ContentValues cv = new ContentValues();
        cv.put(CODE,a.getCODE());
        cv.put(DESCRIPTION,a.getDESCRIPTION());
        cv.put(ORDER, a.getORDEN());
        cv.put(DATE, Funciones.getFormatedDate(a.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(a.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Areas a){
        String where = CODE+" = ?";
        return update(a, where, new String[]{a.getCODE()});
    }
    public long update(Areas a, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,a.getCODE() );
        cv.put(DESCRIPTION,a.getDESCRIPTION());
        cv.put(ORDER, a.getORDEN());
        cv.put(DATE, Funciones.getFormatedDate(a.getMDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(a.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(Areas a){
        return delete(CODE+" = ?", new String[]{a.getCODE()});
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public ArrayList<Areas> getAreas(String[] camposFiltros, String[]argumentos, String campoOrderBy){

        ArrayList<Areas> result = new ArrayList<>();
        if(campoOrderBy == null){
            campoOrderBy=DESCRIPTION;
        }
        try {
            Cursor c =  DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, colums, ((camposFiltros!=null)?DB.getWhereFormat(camposFiltros):null), argumentos, null, null, campoOrderBy);
            while (c.moveToNext()){
                result.add(new Areas(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public int getCount(){
        int result = 0;
        ArrayList<Areas> pts = getAreas(null, null, null);
        if(pts != null){
            result =  pts.size();
        }
        return result;
    }
    public Areas getAreaByCode(String code){
        ArrayList<Areas> pts = getAreas(new String[]{CODE}, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }

    public int getNextOrden(){
        int result = 0;
        String sql = "SELECT MAX("+ORDER+" + 1) " +
                "FROM "+TABLE_NAME;
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            if(c.moveToFirst()){
                result = c.getInt(0);
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    public ArrayList<SimpleRowModel> getAllAreasSRM(String where, String[] args){
        ArrayList<SimpleRowModel> result = new ArrayList<>();
        String orderBy = ORDER+" ASC, "+DESCRIPTION;
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, colums, where, args, null, null, orderBy);
            while(c.moveToNext()){
                result.add(new SimpleRowModel(c.getString(c.getColumnIndex(CODE)), c.getString(c.getColumnIndex(DESCRIPTION)), c.getString(c.getColumnIndex(MDATE)) != null));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }


    public void sendToFireBase(Areas a){
        try {
            WriteBatch lote = db.batch();
            lote.set(getReferenceFireStore().document(a.getCODE()), a.toMap());
            lote.commit();
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    public void deleteFromFireBase(Areas a){
        try {
            getReferenceFireStore().document(a.getCODE()).delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){

            Task<QuerySnapshot> areas = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersAreas).get();
            areas.addOnSuccessListener(onSuccessListener);
            areas.addOnFailureListener(onFailureListener);

    }

    public void getAllDataFromFireBase(String key, OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> measureUnits = getReferenceFireStore().get();
            measureUnits.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            Areas area = dc.getDocument().toObject(Areas.class);
                            delete(area);
                            insert(area);
                        }
                    }
                }
            }).addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void fillSpinner(Spinner spn, boolean addTodos){
        String orderBy = ProductsTypesController.ORDER+" ASC, "+ProductsTypesController.DESCRIPTION;
        ArrayList<Areas> list = getAreas(null, null, orderBy);
        ArrayList<KV> data = new ArrayList<>();
        if(addTodos){
            KV obj = new KV("-1", "TODOS");
            data.add(obj);
        }
        for(Areas a : list){
            data.add(new KV(a.getCODE(), a.getDESCRIPTION()));
        }

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }

}
