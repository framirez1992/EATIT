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
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.TableCode;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class TableCodeController {

    public static String TABLE_NAME = "TABLECODE";
    public static String CODE = "code", CODETYPE= "codetype", CODECONTROL = "codecontrol",DESCRIPTION = "description", DATE = "date", MDATE = "mdate";
    public static String[]columns = new String[]{CODE, CODETYPE, CODECONTROL, DESCRIPTION, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT, "+CODETYPE+" TEXT,"+CODECONTROL+" TEXT, "+DESCRIPTION+" TEXT," +
             DATE+" TEXT, "+MDATE+" TEXT)";

    FirebaseFirestore db;
    Context context;
    private static TableCodeController instance;
    private TableCodeController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static TableCodeController getInstance(Context context){
        if(instance == null){
            instance = new TableCodeController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersTableCode);
        return reference;
    }

    public long insert(TableCode tc){
        ContentValues cv = new ContentValues();
        cv.put(CODE, tc.getCODE());
        cv.put(CODETYPE, tc.getCODETYPE());
        cv.put(CODECONTROL, tc.getCODECONTROL());
        cv.put(DESCRIPTION, tc.getDESCRIPTION());
        cv.put(DATE, Funciones.getFormatedDate(tc.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(tc.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(TableCode tc, String where, String[]whereArgs){
        ContentValues cv = new ContentValues();
        cv.put(CODE, tc.getCODE());
        cv.put(CODETYPE, tc.getCODETYPE());
        cv.put(CODECONTROL, tc.getCODECONTROL());
        cv.put(DESCRIPTION, tc.getDESCRIPTION());
        cv.put(DATE, Funciones.getFormatedDate(tc.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(tc.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, whereArgs);
        return result;
    }

    public long delete(String where, String[]whereArgs){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, whereArgs);
        return result;
    }


    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> client = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersTableCode).get();
            client.addOnSuccessListener(onSuccessListener);
            client.addOnFailureListener(onFailureListener);
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
                            TableCode object = dc.getDocument().toObject(TableCode.class);
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


    public void sendToFireBase(TableCode tc){
        try {
            WriteBatch lote = db.batch();
            lote.set(getReferenceFireStore().document(tc.getCODE()), tc.toMap());
            lote.commit();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void deleteFromFireBase(TableCode tc){
        try {
            getReferenceFireStore().document(tc.getCODE()).delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public ArrayList<TableCode> getTableCodes(String where, String[]args, String orderBy){
        ArrayList<TableCode> result = new ArrayList<>();
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
            while(c.moveToNext()){
                result.add(new TableCode(c));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public TableCode getTableCodeByCode(String code){
        String where = CODE+" = ?";
        ArrayList<TableCode> pts = getTableCodes(where, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }

    /**
     * Simple seleccion row model
     * @param where
     * @param args
     * @param campoOrder
     * @return
     */
    public ArrayList<SimpleRowModel> getTablaCodeSRM(String where, String[] args, String campoOrder){
        ArrayList<SimpleRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = DESCRIPTION;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT "+CODE+" as CODE, "+DESCRIPTION+" AS DESCRIPTION, "+MDATE+" AS MDATE " +
                    "FROM "+TABLE_NAME+"  " +
                    where+" " +
                    "ORDER BY "+campoOrder;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while(c.moveToNext()){
                String code = c.getString(c.getColumnIndex("CODE"));
                String name = c.getString(c.getColumnIndex("DESCRIPTION"));
                String mdate = c.getString(c.getColumnIndex("MDATE"));

                result.add(new SimpleRowModel(code,name ,(mdate!= null)));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }



    public void fillSpinner(Spinner spn, boolean addTodos){

        ArrayList<KV> data = new ArrayList<>();
        if(addTodos){
            KV obj = new KV("0", "TODOS");
            data.add(obj);
        }

        data.add(new KV(CODES.TABLA_MOTIVOS_ANULADO_CODE, CODES.TABLA_MOTIVOS_ANULADO));
        data.add(new KV(CODES.TABLA_TABLEFILTER_TASK_CODE, CODES.TABLA_TABLEFILTER_TASK));

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }


    public void fillSpinnerByCode(Spinner spn,String code){

        ArrayList<KV> data = new ArrayList<>();
        String where = CODETYPE+" = ? ";
        String[]args = new String[]{code};
        String sql = "SELECT "+CODE+" as CODE, "+DESCRIPTION+" AS DESCRIPTION " +
                "FROM "+TABLE_NAME+"  " +
                "WHERE "+where+" " +
                "ORDER BY "+DESCRIPTION;
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
        while(c.moveToNext()){
            data.add(new KV(c.getString(c.getColumnIndex("CODE")),
                    c.getString(c.getColumnIndex("DESCRIPTION"))));

        }


        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }

}
