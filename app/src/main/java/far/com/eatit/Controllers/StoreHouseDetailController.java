package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

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
import far.com.eatit.CloudFireStoreObjects.StoreHouseDetail;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class StoreHouseDetailController {

    public static String TABLE_NAME = "STOREHOUSEDETAIL";
    public static String CODE = "code", CODESTOREHOUSE = "codestorehouse",CODEPRODUCT = "codeproduct",QUANTITY = "quantity",CODEMEASURE = "codemeasure",
            DATE = "date", MDATE="mdate";
    private String[]colums = new String[]{CODE, CODESTOREHOUSE, CODEPRODUCT, QUANTITY,CODEMEASURE, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT,"+CODESTOREHOUSE+" TEXT, "+CODEPRODUCT+" TEXT, "+QUANTITY+" NUMERIC,"+CODEMEASURE+" TEXT, "+DATE+" TEXT, "+MDATE+" TEXT)";

    Context context;
    FirebaseFirestore db;
    private static  StoreHouseDetailController instance;
    private StoreHouseDetailController(Context c){
        this.context = c;
        this.db = FirebaseFirestore.getInstance();
    }
    public static StoreHouseDetailController getInstance(Context context){
        if(instance == null){
            instance = new StoreHouseDetailController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersStoreHouseDetail);
        return reference;
    }
    public long insert(StoreHouseDetail pt){
        ContentValues cv = new ContentValues();
        cv.put(CODE,pt.getCODE());
        cv.put(CODESTOREHOUSE, pt.getCODESTOREHOUSE());
        cv.put(CODEPRODUCT,pt.getCODEPRODUCT());
        cv.put(CODEMEASURE, pt.getCODEMEASURE());
        cv.put(QUANTITY, pt.getQUANTITY());
        cv.put(DATE, (pt.getDATE() != null)? Funciones.getFormatedDate(pt.getDATE()): null);
        cv.put(MDATE,  (pt.getDATE() != null)?Funciones.getFormatedDate(pt.getMDATE()):null);

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(StoreHouseDetail pt, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,pt.getCODE());
        cv.put(CODESTOREHOUSE, pt.getCODESTOREHOUSE());
        cv.put(CODEPRODUCT,pt.getCODEPRODUCT());
        cv.put(CODEMEASURE, pt.getCODEMEASURE());
        cv.put(QUANTITY, pt.getQUANTITY());
        cv.put(MDATE,  (pt.getDATE() != null)?Funciones.getFormatedDate(pt.getMDATE()):null);

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }


    public ArrayList<StoreHouseDetail> getStoreHouseDetail(String[] camposFiltros, String[]argumentos, String campoOrderBy){

        ArrayList<StoreHouseDetail> result = new ArrayList<>();
        if(campoOrderBy == null){
            campoOrderBy=CODEPRODUCT;
        }
        try {
            Cursor c =  DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, colums, ((camposFiltros!=null)?DB.getWhereFormat(camposFiltros):null), argumentos, null, null, campoOrderBy);
            while (c.moveToNext()){
                result.add(new StoreHouseDetail(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public StoreHouseDetail getStoreHouseDetailByCode(String code){
        ArrayList<StoreHouseDetail> pts = getStoreHouseDetail(new String[]{CODE}, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }


    public void sendToFireBase(StoreHouseDetail pst){
        try {
            WriteBatch lote = db.batch();
            lote.set(getReferenceFireStore().document(pst.getCODE()), pst.toMap());
            lote.commit();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void deleteFromFireBase(StoreHouseDetail pst){
        try {
            getReferenceFireStore().document(pst.getCODE()).delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> storeHouseDetail = getReferenceFireStore().get();
            storeHouseDetail.addOnSuccessListener(onSuccessListener);
            storeHouseDetail.addOnFailureListener(onFailureListener);
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
                            StoreHouseDetail object = dc.getDocument().toObject(StoreHouseDetail.class);
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
/*
    public ArrayList<SimpleRowModel> getAllStoreHouseDetailSRM(String where, String[] args, String campoOrderBy){

        ArrayList<SimpleRowModel> result = new ArrayList<>();
        if(where != null)
            where = "WHERE "+where;
        else
            where = "";

        if(campoOrderBy == null)
            campoOrderBy=CODEPRODUCT;

        try {

            String sql = "SELECT pst."+ CODE+" AS CODE, pst."+DESCRIPTION+" AS DESCRIPTION,  pst."+DATE+" AS DATE " +
                    "FROM "+TABLE_NAME+" pst " +
                    "INNER JOIN "+ProductsTypesController.TABLE_NAME+" pt on pst."+CODETYPE+" = pt."+ProductsTypesController.CODE+" " +
                    where+" "+
                    "ORDER BY "+campoOrderBy;
            Cursor c =  DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while (c.moveToNext()){

                result.add(new SimpleRowModel(c.getString(c.getColumnIndex("CODE")), c.getString(c.getColumnIndex("DESCRIPTION")),c.getString(c.getColumnIndex("DATE")) != null) );
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }*/

    /**
     * Simple seleccion row model
     * @param where
     * @param args
     * @param campoOrder
     * @return
     */
    /*
    public ArrayList<SimpleSeleccionRowModel> getProductSubTypesSSRM(String where, String[] args, String campoOrder){
        ArrayList<SimpleSeleccionRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = DESCRIPTION;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT "+CODE+" as CODE, "+DESCRIPTION+" as DESCRIPTION " +
                    "FROM "+TABLE_NAME+"  " +
                    where;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while(c.moveToNext()){
                String code = c.getString(c.getColumnIndex("CODE"));
                String name = c.getString(c.getColumnIndex("DESCRIPTION"));
                result.add(new SimpleSeleccionRowModel(code,name ,false));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }*/
    /*
    public void fillSpinner(Spinner spn, boolean addTodos, String type){
        String orderBy =ProductsSubTypesController.DESCRIPTION;
        String[] camposFiltros = null;
        String[]args = null;
        if(type != null){
            camposFiltros = new String[]{CODETYPE};
            args = new String[]{type};
        }
        ArrayList<ProductsSubTypes> list = getProductSubTypes(camposFiltros, args, orderBy);
        ArrayList<KV> data = new ArrayList<>();
        if(addTodos){
            KV obj = new KV("0", "TODOS");
            data.add(obj);
        }
        for(ProductsSubTypes pt : list){
            data.add(new KV(pt.getCODE(), pt.getDESCRIPTION()));
        }

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }*/
}
