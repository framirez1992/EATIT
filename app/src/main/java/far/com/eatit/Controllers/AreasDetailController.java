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

import far.com.eatit.API.models.Area;
import far.com.eatit.API.models.AreaDetail;
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
    public static String IDAREADETAIL="idAreaDetail",IDAREA = "idArea", DESCRIPTION = "description", POSITION = "position", DATA="data", DATA2="data2",DATA3="data3",
            CREATEDATE = "createDate", CREATEUSER = "createUser", UPDATEDATE="updateDate", UPDATEUSER="updateUser",DELETEDATE="deleteDate",DELETEUSER="deleteUser" ;
    private String[]colums = new String[]{IDAREADETAIL, IDAREA, DESCRIPTION, POSITION,  DATA, DATA2, DATA3, CREATEDATE, CREATEUSER,UPDATEDATE, UPDATEUSER, DELETEDATE, DELETEUSER};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +IDAREADETAIL+" INTEGER,"
            +IDAREA+" INTEGER,"
            +DESCRIPTION+" TEXT, "
            +POSITION+" INTEGER, "
            +DATA+" TEXT,  "
            +DATA2+" TEXT,  "
            +DATA3+" TEXT,  "
            +CREATEDATE+" TEXT, "
            +CREATEUSER+" TEXT, "
            +UPDATEDATE+" TEXT, "
            +UPDATEUSER+" TEXT, "
            +DELETEDATE+" TEXT, "
            +DELETEUSER+" TEXT "
            +")";

    Context context;
    DB db;
    private static  AreasDetailController instance;
    private AreasDetailController(Context c){
        this.context = c;
        this.db = DB.getInstance(c);
    }
    public static AreasDetailController getInstance(Context context){
        if(instance == null){
            instance = new AreasDetailController(context);
        }
        return instance;
    }

    public void insertOrUpdate(AreaDetail obj){
        String sql ="insert or replace into "+TABLE_NAME+" ("+IDAREADETAIL+", "+IDAREA+", "+DESCRIPTION+","+POSITION+","+DATA+","+DATA2+","+DATA3+", "+CREATEDATE+", "+CREATEUSER+","+UPDATEDATE+", "+UPDATEUSER+", "+DELETEDATE+", "+DELETEUSER+") values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        db.getWritableDatabase().execSQL(sql,new String[]{obj.getIdarea()+"", obj.getIdarea()+"", obj.getDescription(),obj.getPosition()+"",obj.getData(),obj.getData2(),obj.getData3(), obj.getCreateDate(), obj.getCreateUser(),  obj.getUpdateDate(),obj.getUpdateUser(),  obj.getDeleteDate(), obj.getDeleteUser() });
    }

    public long insert(AreaDetail obj){
        ContentValues cv = new ContentValues();
        cv.put(IDAREADETAIL,obj.getIdareaDetail());
        cv.put(IDAREA, obj.getIdarea());
        cv.put(DESCRIPTION,obj.getDescription());
        cv.put(POSITION, obj.getPosition());
        cv.put(DATA, obj.getData());
        cv.put(DATA2, obj.getData2());
        cv.put(DATA3, obj.getData3());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(AreaDetail obj){
        ContentValues cv = new ContentValues();
        cv.put(IDAREADETAIL,obj.getIdareaDetail());
        cv.put(IDAREA, obj.getIdarea());
        cv.put(DESCRIPTION,obj.getDescription());
        cv.put(POSITION, obj.getPosition());
        cv.put(DATA, obj.getData());
        cv.put(DATA2, obj.getData2());
        cv.put(DATA3, obj.getData3());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,IDAREADETAIL.concat("= ?"), new String[]{obj.getIdareaDetail()+""});
        return result;
    }

    public long delete(AreaDetail obj){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,IDAREADETAIL.concat("= ?"), new String[]{obj.getIdareaDetail()+""});
        return result;
    }

    public int getNextOrden(){
        int result = 0;
        String sql = "SELECT CAST(MAX("+POSITION+") AS INTEGER) + 1 " +
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

    public ArrayList<AreaDetail> getAreasDetails(String[] camposFiltros, String[]argumentos, String campoOrderBy){

        ArrayList<AreaDetail> result = new ArrayList<>();
        if(campoOrderBy == null){
            campoOrderBy=DESCRIPTION;
        }
        try {
            Cursor c =  DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, colums, ((camposFiltros!=null)?DB.getWhereFormat(camposFiltros):null), argumentos, null, null, campoOrderBy);
            while (c.moveToNext()){
                result.add(new AreaDetail(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public AreaDetail getAreasDetail(String idAreaDetail){
        ArrayList<AreaDetail> pts = getAreasDetails(new String[]{IDAREADETAIL}, new String[]{idAreaDetail}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }




    public ArrayList<SimpleRowModel> getAllAreasDetailSRM(String where,String[] args, String campoOrderBy){

        ArrayList<SimpleRowModel> result = new ArrayList<>();
        if(where != null)
            where = "WHERE "+where;
        else
            where = "";

        if(campoOrderBy == null)
            campoOrderBy="pst."+POSITION+" ASC, pst."+DESCRIPTION;

        try {

            String sql = "SELECT pst."+ IDAREADETAIL+" AS CODE, pst."+DESCRIPTION+" AS DESCRIPTION,  pst."+CREATEDATE+" AS DATE " +
                    "FROM "+TABLE_NAME+" pst " +
                    "INNER JOIN "+AreasController.TABLE_NAME+" pt on pst."+IDAREA+" = pt."+AreasController.IDAREA +" " +
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
        String orderBy = AreasDetailController.POSITION+" ASC, "+AreasDetailController.DESCRIPTION;
        String[] camposFiltros = null;
        String[]args = null;
        if(area != null){
            camposFiltros = new String[]{IDAREA};
            args = new String[]{area};
        }
        ArrayList<AreaDetail> list = getAreasDetails(camposFiltros, args, orderBy);
        ArrayList<KV> data = new ArrayList<>();
        if(addTodos){
            KV obj = new KV("-1", "TODOS");
            data.add(obj);
        }
        for(AreaDetail pt : list){
            data.add(new KV(pt.getIdareaDetail()+"", pt.getDescription()));
        }

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }

    /**
     * Llena un spinner con las mesas que corespondan a Pedidos con los Status (Abierta, Lista, Entregada)
     */
    public void fillSpinnerPendingOrders(Spinner spn){
        ArrayList<KV> data = new ArrayList<>();
        String sql = "SELECT ad."+AreasDetailController.IDAREADETAIL +", ad."+AreasDetailController.DESCRIPTION+" " +
                "FROM "+AreasDetailController.TABLE_NAME+" ad " +
                "INNER JOIN "+SalesController.TABLE_NAME+" s on s."+SalesController.IDTABLE+" = ad."+TableController.IDTABLE+" " +
                "AND s."+SalesController.STATUS+" in ("+CODES.CODE_ORDER_STATUS_OPEN+", "+CODES.CODE_ORDER_STATUS_READY+", "+CODES.CODE_ORDER_STATUS_DELIVERED+") " +
                "GROUP BY ad."+AreasDetailController.IDAREADETAIL +", ad."+AreasDetailController.DESCRIPTION+" "+
                "ORDER BY ad."+AreasDetailController.POSITION +" ASC, ad."+AreasDetailController.DESCRIPTION+" ";
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        while(c.moveToNext()){
            data.add(new KV(c.getString(0), c.getString(1)));
        }c.close();

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }

    public void fillSpinnerWithAssignedTables(Spinner spn, String area){
        ArrayList<KV> data = new ArrayList<>();
        String orderBy = AreasDetailController.POSITION +" ASC, "+AreasDetailController.DESCRIPTION;

        /*KV lowTarger = UserControlController.getInstance(context).getLowTargetLevel(CODES.USERCONTROL_TABLEASSIGN);
        String target = lowTarger.getKey();
        String targetCode = lowTarger.getValue();*/
        Users u = UsersController.getInstance(context).getUserByCode(Funciones.getCodeuserLogged(context));

    String sql = "SELECT ad." + AreasDetailController.IDAREADETAIL + ", ad." + AreasDetailController.DESCRIPTION + " " +
            "FROM " + AreasDetailController.TABLE_NAME + " ad " +
            "INNER JOIN " + UserControlController.TABLE_NAME + " uc on uc." + UserControlController.CONTROL + " = '" + CODES.USERCONTROL_TABLEASSIGN + "' AND uc." + UserControlController.ACTIVE + " = '1' " +
            "AND (   (uc." + UserControlController.TARGET + " = '" + CODES.USERSCONTROL_TARGET_USER + "' AND uc." + UserControlController.TARGETCODE + " = '" + u.getCODE() + "') " +
            "      OR(uc." + UserControlController.TARGET + " = '" + CODES.USERSCONTROL_TARGET_USER_ROL + "' AND uc." + UserControlController.TARGETCODE + " = '" + u.getROLE() + "') " +
            "      OR(uc." + UserControlController.TARGET + " = '" + CODES.USERSCONTROL_TARGET_COMPANY + "' AND uc." + UserControlController.TARGETCODE + " = '" + u.getCOMPANY() + "') " +
            ")" +
            "AND  ad." + AreasDetailController.IDAREADETAIL + " = uc." + UserControlController.VALUE + "  " +
            "WHERE ad." + AreasDetailController.IDAREA + " = ? " +
            "GROUP BY ad." + AreasDetailController.IDAREADETAIL + ", ad." + AreasDetailController.DESCRIPTION + " " +
            "ORDER BY " + orderBy;
    Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{area});
    while (c.moveToNext()) {
        data.add(new KV(c.getString(0), c.getString(1)));
    }

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }

}
