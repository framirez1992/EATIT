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
import far.com.eatit.Adapters.Models.SimpleSeleccionRowModel;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.ProductsSubTypes;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class ProductsSubTypesInvController {
    public static String TABLE_NAME = "PRODUCTSSUBTYPESINV";
    public static String CODE = "code", CODETYPE = "codetype", DESCRIPTION = "description", ORDER = "orden",
            DATE = "date", MDATE="mdate";
    private String[]colums = new String[]{CODE, CODETYPE, DESCRIPTION, ORDER, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT,"+CODETYPE+" TEXT, "+DESCRIPTION+" TEXT, "+ORDER+" INTEGER, "+DATE+" TEXT, "+MDATE+" TEXT)";

    Context context;
    FirebaseFirestore db;
    private static  ProductsSubTypesInvController instance;
    private ProductsSubTypesInvController(Context c){
        this.context = c;
        this.db = FirebaseFirestore.getInstance();
    }
    public static ProductsSubTypesInvController getInstance(Context context){
        if(instance == null){
            instance = new ProductsSubTypesInvController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersProductsSubTypesInv);
        return reference;
    }
    public long insert(ProductsSubTypes pt){
        ContentValues cv = new ContentValues();
        cv.put(CODE,pt.getCODE());
        cv.put(CODETYPE, pt.getCODETYPE());
        cv.put(DESCRIPTION,pt.getDESCRIPTION());
        cv.put(ORDER, pt.getORDEN());
        cv.put(DATE, (pt.getDATE() != null)? Funciones.getFormatedDate(pt.getDATE()): null);
        cv.put(MDATE,  (pt.getDATE() != null)?Funciones.getFormatedDate(pt.getMDATE()):null);

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(ProductsSubTypes pt, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,pt.getCODE() );
        cv.put(CODETYPE, pt.getCODETYPE());
        cv.put(DESCRIPTION,pt.getDESCRIPTION());
        cv.put(ORDER, pt.getORDEN());
        cv.put(MDATE,  (pt.getDATE() != null)?Funciones.getFormatedDate(pt.getMDATE()):null);

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public int getNextOrden(){
        int result = 0;
        String sql = "SELECT CAST(MAX("+ORDER+") AS INTEGER) + 1 " +
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

    public ArrayList<ProductsSubTypes> getProductSubTypes(String[] camposFiltros, String[]argumentos, String campoOrderBy){

        ArrayList<ProductsSubTypes> result = new ArrayList<>();
        if(campoOrderBy == null){
            campoOrderBy=DESCRIPTION;
        }
        try {
            Cursor c =  DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, colums, ((camposFiltros!=null)?DB.getWhereFormat(camposFiltros):null), argumentos, null, null, campoOrderBy);
            while (c.moveToNext()){
                result.add(new ProductsSubTypes(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public ProductsSubTypes getProductTypeByCode(String code){
        ArrayList<ProductsSubTypes> pts = getProductSubTypes(new String[]{CODE}, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }


    public void sendToFireBase(ProductsSubTypes pst){
        try {
            WriteBatch lote = db.batch();
            lote.set(getReferenceFireStore().document(pst.getCODE()), pst.toMap());
            lote.commit();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void deleteFromFireBase(ProductsSubTypes pst){
        try {
            getReferenceFireStore().document(pst.getCODE()).delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> measureUnits = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersProductsSubTypesInv).get();
            measureUnits.addOnSuccessListener(onSuccessListener);
            measureUnits.addOnFailureListener(onFailureListener);
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
                            ProductsSubTypes object = dc.getDocument().toObject(ProductsSubTypes.class);
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

    public ArrayList<SimpleRowModel> getAllProductSubTypesSRM(String where, String[] args, String campoOrderBy){

        ArrayList<SimpleRowModel> result = new ArrayList<>();
        if(where != null)
            where = "WHERE "+where;
        else
            where = "";

        if(campoOrderBy == null)
            campoOrderBy=DESCRIPTION;

        try {

            String sql = "SELECT pst."+ CODE+" AS CODE, pst."+DESCRIPTION+" AS DESCRIPTION,  pst."+DATE+" AS DATE " +
                    "FROM "+TABLE_NAME+" pst " +
                    "INNER JOIN "+ProductsTypesInvController.TABLE_NAME+" pt on pst."+CODETYPE+" = pt."+ProductsTypesController.CODE+" " +
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
    }

    /**
     * Simple seleccion row model
     * @param where
     * @param args
     * @param campoOrder
     * @return
     */
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

    }

    public void fillSpinner(Spinner spn, boolean addTodos){
        fillSpinner(spn,addTodos, null);
    }
    public void fillSpinner(Spinner spn, boolean addTodos, String type){
        String orderBy = /*ProductsSubTypesController.ORDER+" ASC, "+*/ProductsSubTypesController.DESCRIPTION;
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
    }


}
