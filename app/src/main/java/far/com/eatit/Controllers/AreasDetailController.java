package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;

import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.CloudFireStoreObjects.AreasDetail;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.Users;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class AreasDetailController {
    public static final String TABLE_NAME = "AREASDETAIL";
    public static String CODE = "code", CODEAREA = "codearea", DESCRIPTION = "description", ORDER = "orden",
            DATE = "date", MDATE="mdate";
    private String[]colums = new String[]{CODE, CODEAREA, DESCRIPTION, ORDER, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT,"+CODEAREA+" TEXT, "+DESCRIPTION+" TEXT, "+ORDER+" INTEGER, "+DATE+" TEXT, "+MDATE+" TEXT)";

    Context context;
    FirebaseFirestore db;
    private static  AreasDetailController instance;
    private AreasDetailController(Context c){
        this.context = c;
        this.db = FirebaseFirestore.getInstance();
    }
    public static AreasDetailController getInstance(Context context){
        if(instance == null){
            instance = new AreasDetailController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersAreasDetail);
        return reference;
    }
    public long insert(AreasDetail pt){
        ContentValues cv = new ContentValues();
        cv.put(CODE,pt.getCODE());
        cv.put(CODEAREA, pt.getCODEAREA());
        cv.put(DESCRIPTION,pt.getDESCRIPTION());
        cv.put(ORDER, pt.getORDEN());
        cv.put(DATE, (pt.getDATE() != null)?Funciones.getFormatedDate(pt.getDATE()): null);
        cv.put(MDATE,  (pt.getDATE() != null)?Funciones.getFormatedDate(pt.getMDATE()):null);

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(AreasDetail pt, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,pt.getCODE() );
        cv.put(CODEAREA, pt.getCODEAREA());
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

    public ArrayList<AreasDetail> getAreasDetails(String[] camposFiltros, String[]argumentos, String campoOrderBy){

        ArrayList<AreasDetail> result = new ArrayList<>();
        if(campoOrderBy == null){
            campoOrderBy=DESCRIPTION;
        }
        try {
            Cursor c =  DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, colums, ((camposFiltros!=null)?DB.getWhereFormat(camposFiltros):null), argumentos, null, null, campoOrderBy);
            while (c.moveToNext()){
                result.add(new AreasDetail(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public AreasDetail getAreasDetailByCode(String code){
        ArrayList<AreasDetail> pts = getAreasDetails(new String[]{CODE}, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }


    public void sendToFireBase(AreasDetail pst){
        try {
            WriteBatch lote = db.batch();
            lote.set(getReferenceFireStore().document(pst.getCODE()), pst.toMap());
            lote.commit();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void deleteFromFireBase(AreasDetail pst){
        try {
            getReferenceFireStore().document(pst.getCODE()).delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> measureUnits = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersAreasDetail).get();
            measureUnits.addOnSuccessListener(onSuccessListener);
            measureUnits.addOnFailureListener(onFailureListener);
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
                            AreasDetail object = dc.getDocument().toObject(AreasDetail.class);
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


    public ArrayList<SimpleRowModel> getAllAreasDetailSRM(String where,String[] args, String campoOrderBy){

        ArrayList<SimpleRowModel> result = new ArrayList<>();
        if(where != null)
            where = "WHERE "+where;
        else
            where = "";

        if(campoOrderBy == null)
            campoOrderBy="pst."+ORDER+" ASC, pst."+DESCRIPTION;

        try {

            String sql = "SELECT pst."+ CODE+" AS CODE, pst."+DESCRIPTION+" AS DESCRIPTION,  pst."+DATE+" AS DATE " +
                    "FROM "+TABLE_NAME+" pst " +
                    "INNER JOIN "+AreasController.TABLE_NAME+" pt on pst."+CODEAREA+" = pt."+AreasController.CODE+" " +
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


    public void fillSpinner(Spinner spn, boolean addTodos){
        fillSpinner(spn,addTodos, null);
    }
    public void fillSpinner(Spinner spn, boolean addTodos, String area){
        String orderBy = AreasDetailController.ORDER+" ASC, "+AreasDetailController.DESCRIPTION;
        String[] camposFiltros = null;
        String[]args = null;
        if(area != null){
            camposFiltros = new String[]{CODEAREA};
            args = new String[]{area};
        }
        ArrayList<AreasDetail> list = getAreasDetails(camposFiltros, args, orderBy);
        ArrayList<KV> data = new ArrayList<>();
        if(addTodos){
            KV obj = new KV("-1", "TODOS");
            data.add(obj);
        }
        for(AreasDetail pt : list){
            data.add(new KV(pt.getCODE(), pt.getDESCRIPTION()));
        }

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }

    /**
     * Llena un spinner con las mesas que corespondan a Pedidos con los Status (Abierta, Lista, Entregada)
     */
    public void fillSpinnerPendingOrders(Spinner spn){
        ArrayList<KV> data = new ArrayList<>();
        String sql = "SELECT ad."+AreasDetailController.CODE+", ad."+AreasDetailController.DESCRIPTION+" " +
                "FROM "+AreasDetailController.TABLE_NAME+" ad " +
                "INNER JOIN "+SalesController.TABLE_NAME+" s on s."+SalesController.CODEAREADETAIL+" = ad."+AreasDetailController.CODE+" " +
                "AND s."+SalesController.STATUS+" in ("+CODES.CODE_ORDER_STATUS_OPEN+", "+CODES.CODE_ORDER_STATUS_READY+", "+CODES.CODE_ORDER_STATUS_DELIVERED+") " +
                "GROUP BY ad."+AreasDetailController.CODE+", ad."+AreasDetailController.DESCRIPTION+" "+
                "ORDER BY ad."+AreasDetailController.ORDER+" ASC, ad."+AreasDetailController.DESCRIPTION+" ";
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        while(c.moveToNext()){
            data.add(new KV(c.getString(0), c.getString(1)));
        }c.close();

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }

    public void fillSpinnerWithAssignedTables(Spinner spn, String area){
        ArrayList<KV> data = new ArrayList<>();
        String orderBy = AreasDetailController.ORDER+" ASC, "+AreasDetailController.DESCRIPTION;

        /*KV lowTarger = UserControlController.getInstance(context).getLowTargetLevel(CODES.USERCONTROL_TABLEASSIGN);
        String target = lowTarger.getKey();
        String targetCode = lowTarger.getValue();*/
        Users u = UsersController.getInstance(context).getUserByCode(Funciones.getCodeuserLogged(context));

    String sql = "SELECT ad." + AreasDetailController.CODE + ", ad." + AreasDetailController.DESCRIPTION + " " +
            "FROM " + AreasDetailController.TABLE_NAME + " ad " +
            "INNER JOIN " + UserControlController.TABLE_NAME + " uc on uc." + UserControlController.CONTROL + " = '" + CODES.USERCONTROL_TABLEASSIGN + "' AND uc." + UserControlController.ACTIVE + " = '1' " +
            "AND (   (uc." + UserControlController.TARGET + " = '" + CODES.USERSCONTROL_TARGET_USER + "' AND uc." + UserControlController.TARGETCODE + " = '" + u.getCODE() + "') " +
            "      OR(uc." + UserControlController.TARGET + " = '" + CODES.USERSCONTROL_TARGET_USER_ROL + "' AND uc." + UserControlController.TARGETCODE + " = '" + u.getROLE() + "') " +
            "      OR(uc." + UserControlController.TARGET + " = '" + CODES.USERSCONTROL_TARGET_COMPANY + "' AND uc." + UserControlController.TARGETCODE + " = '" + u.getCOMPANY() + "') " +
            ")" +
            "AND  ad." + AreasDetailController.CODE + " = uc." + UserControlController.VALUE + "  " +
            "WHERE ad." + AreasDetailController.CODEAREA + " = ? " +
            "GROUP BY ad." + AreasDetailController.CODE + ", ad." + AreasDetailController.DESCRIPTION + " " +
            "ORDER BY " + orderBy;
    Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{area});
    while (c.moveToNext()) {
        data.add(new KV(c.getString(0), c.getString(1)));
    }

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }



    public void searchDetailChanges(boolean all, OnSuccessListener<QuerySnapshot> success,  OnFailureListener failure){

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

    public void consumeDetailQuerySnapshot(boolean clear, QuerySnapshot querySnapshot){
        if(clear){
            delete(null, null);
        }
        if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
            for(DocumentSnapshot doc: querySnapshot){
                AreasDetail obj = doc.toObject(AreasDetail.class);
                if(update(obj, CODE+"=?", new String[]{obj.getCODE()}) <=0){
                    insert(obj);
                }
            }
        }

    }
}
