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
import far.com.eatit.API.models.Client;
import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.CloudFireStoreObjects.Areas;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.Users;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class AreasController {
    public static final String TABLE_NAME = "AREAS";
    public static String IDAREA="idArea", IDCOMPANY="idCompany",CODE = "code", DESCRIPTION = "description", POSITION = "position", DATA="data", DATA2="data2",DATA3="data3",
            CREATEDATE = "createDate", CREATEUSER = "createUser", UPDATEDATE="updateDate", UPDATEUSER="updateUser",DELETEDATE="deleteDate",DELETEUSER="deleteUser" ;
    public static String[]colums = new String[]{IDAREA,IDCOMPANY,CODE, DESCRIPTION, POSITION, DATA, DATA2, DATA3, CREATEDATE, CREATEUSER,UPDATEDATE, UPDATEUSER, DELETEDATE, DELETEUSER};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +IDAREA+" INTEGER, "
            +IDCOMPANY+" INTEGER, "
            +CODE+" TEXT, "
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
    private static AreasController instance;
    private AreasController(Context c){
        this.context = c;
        this.db = DB.getInstance(c);
    }
    public static AreasController getInstance(Context context){
        if(instance == null){
            instance = new AreasController(context);
        }
        return instance;
    }

    public void insertOrUpdate(Area obj){
        String sql ="insert or replace into "+TABLE_NAME+" ("+IDAREA+", "+IDCOMPANY+", "+CODE+", "+DESCRIPTION+","+POSITION+","+DATA+","+DATA2+","+DATA3+", "+CREATEDATE+", "+CREATEUSER+","+UPDATEDATE+", "+UPDATEUSER+", "+DELETEDATE+", "+DELETEUSER+") values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        db.getWritableDatabase().execSQL(sql,new String[]{obj.getIdarea()+"", obj.getIdcompany()+"", obj.getCode(), obj.getDescription(),obj.getPosition()+"",obj.getData(),obj.getData2(),obj.getData3(), obj.getCreateDate(), obj.getCreateUser(),  obj.getUpdateDate(),obj.getUpdateUser(),  obj.getDeleteDate(), obj.getDeleteUser() });
    }


    public long insert(Area obj){
        ContentValues cv = new ContentValues();
        cv.put(IDAREA,obj.getIdarea());
        cv.put(IDCOMPANY,obj.getIdcompany());
        cv.put(CODE,obj.getCode());
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

    public long update(Area obj){
        ContentValues cv = new ContentValues();
        cv.put(IDAREA,obj.getIdarea());
        cv.put(IDCOMPANY,obj.getIdcompany());
        cv.put(CODE,obj.getCode());
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

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,IDAREA.concat(" = ?"), new String[]{obj.getIdarea()+""});
        return result;
    }



    public long delete(Area obj){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,IDAREA.concat(" = ?"), new String[]{obj.getIdarea()+""});
        return result;
    }

    public ArrayList<Area> getAreas(String[] camposFiltros, String[]argumentos, String campoOrderBy){

        ArrayList<Area> result = new ArrayList<>();
        if(campoOrderBy == null){
            campoOrderBy=DESCRIPTION;
        }
        try {
            Cursor c =  DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, colums, ((camposFiltros!=null)?DB.getWhereFormat(camposFiltros):null), argumentos, null, null, campoOrderBy);
            while (c.moveToNext()){
                result.add(new Area(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public int getCount(){
        int result = 0;
        ArrayList<Area> pts = getAreas(null, null, null);
        if(pts != null){
            result =  pts.size();
        }
        return result;
    }
    public Area getAreaByCode(String code){
        ArrayList<Area> pts = getAreas(new String[]{CODE}, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }

    public int getNextOrden(){
        int result = 0;
        String sql = "SELECT MAX("+POSITION+" + 1) " +
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
        String orderBy = POSITION+" ASC, "+DESCRIPTION;
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, colums, where, args, null, null, orderBy);
            while(c.moveToNext()){
                result.add(new SimpleRowModel(c.getString(c.getColumnIndex(CODE)), c.getString(c.getColumnIndex(DESCRIPTION)), c.getString(c.getColumnIndex(CREATEDATE)) != null));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }


    public void fillSpinner(Spinner spn, boolean addTodos){
        String orderBy = ProductsTypesController.POSITION+" ASC, "+ProductsTypesController.DESCRIPTION;
        ArrayList<Area> list = getAreas(null, null, orderBy);
        ArrayList<KV> data = new ArrayList<>();
        if(addTodos){
            KV obj = new KV("-1", "TODOS");
            data.add(obj);
        }
        for(Area a : list){
            data.add(new KV(a.getCode(), a.getDescription()));
        }

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }

    public void fillSpinnerAreasForAssignedTables(Spinner spn, boolean addTodos){
        String orderBy = " ac."+AreasController.POSITION+" ASC, ac."+AreasController.DESCRIPTION;
        ArrayList<KV> data = new ArrayList<>();
        if(addTodos){
            KV obj = new KV("-1", "TODOS");
            data.add(obj);
        }
        try {
            /*KV lowTarger = UserControlController.getInstance(context).getLowTargetLevel(CODES.USERCONTROL_TABLEASSIGN);
            String target = lowTarger.getKey();
            String targetCode = lowTarger.getValue();*/
            Users u = UsersController.getInstance(context).getUserByCode(Funciones.getCodeuserLogged(context));

            String sql = "SELECT ac." + AreasController.CODE + ", ac." + AreasController.DESCRIPTION + " " +
                    "FROM " + AreasDetailController.TABLE_NAME + " ad " +
                    "INNER JOIN " + AreasController.TABLE_NAME + " ac on ac." + AreasController.IDAREA + " = ad." + AreasDetailController.IDAREA + " " +
                    "INNER JOIN " + UserControlController.TABLE_NAME + " uc on uc."+UserControlController.CONTROL+" = '"+CODES.USERCONTROL_TABLEASSIGN+"' AND uc."+UserControlController.ACTIVE+" = '1' " +
                    "AND (   (uc."+UserControlController.TARGET+" = '"+CODES.USERSCONTROL_TARGET_USER+"' AND uc."+UserControlController.TARGETCODE+" = '"+u.getCODE()+"') " +
                    "      OR(uc."+UserControlController.TARGET+" = '"+CODES.USERSCONTROL_TARGET_USER_ROL+"' AND uc."+UserControlController.TARGETCODE+" = '"+u.getROLE()+"') " +
                    "      OR(uc."+UserControlController.TARGET+" = '"+CODES.USERSCONTROL_TARGET_COMPANY+"' AND uc."+UserControlController.TARGETCODE+" = '"+u.getCOMPANY()+"') " +
                    ")" +
                    "AND  ad." + AreasDetailController.IDAREADETAIL + " = uc." + UserControlController.VALUE + "  "+
                    "GROUP BY ac." + AreasController.CODE + ", ac." + AreasController.DESCRIPTION + " " +
                    "ORDER BY " + orderBy;

            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                data.add(new KV(c.getString(0), c.getString(1)));
            }
            c.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }





}
