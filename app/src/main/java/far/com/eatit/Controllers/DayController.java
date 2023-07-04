package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;

import far.com.eatit.API.models.Day;
import far.com.eatit.API.models.Table;
import far.com.eatit.DataBase.DB;

public class DayController {

    public static final String TABLE_NAME ="Days";
    public static  String IDDAY = "idDay", IDUSER = "idUser", STATUS = "status" ,RETURNCOUNT="returnCount",RETURNAMOUNT="returnAmount",SALECOUNT="saleCount",SALEAMOUNT="saleAmount",TOTAL="total",
            CREATEDATE = "createDate", CREATEUSER = "createUser", UPDATEDATE="updateDate", UPDATEUSER="updateUser",DELETEDATE="deleteDate",DELETEUSER="deleteUser" ;

    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +IDDAY+" INTEGER, "
            +IDUSER+" INTEGER,"
            +STATUS+" INTEGER, "
            +RETURNCOUNT+" INTEGER, "
            +RETURNAMOUNT+" DECIMAL(11, 3), "
            +SALECOUNT+" INTEGER, "
            +SALEAMOUNT+" DECIMAL(11, 3), "
            +TOTAL+" DECIMAL(11, 3), "
            +CREATEDATE+" TEXT, "
            +CREATEUSER+" TEXT, "
            +UPDATEDATE+" TEXT, "
            +UPDATEUSER+" TEXT, "
            +DELETEDATE+" TEXT, "
            +DELETEUSER+" TEXT "
            +")";
    public static String[] columns = new String[]{IDDAY, IDUSER, STATUS,RETURNCOUNT,RETURNAMOUNT,SALECOUNT,SALEAMOUNT,TOTAL, CREATEDATE, CREATEUSER,UPDATEDATE, UPDATEUSER, DELETEDATE, DELETEUSER};
    Context context;
    private static DayController instance;
    DB db;
    private DayController(Context c){
        this.context = c;
        this.db = DB.getInstance(c);
    }
    public static DayController getInstance(Context c){
        if(instance == null){
            instance = new DayController(c);
        }
        return instance;
    }

    public void insertOrUpdate(Day obj){
        String sql ="insert or replace into "+TABLE_NAME+" ("+IDDAY+", "+IDUSER+", "+STATUS+", "+RETURNCOUNT+","+RETURNAMOUNT+", "+SALECOUNT+","+SALEAMOUNT+", "+TOTAL+", "+CREATEDATE+", "+CREATEUSER+","+UPDATEDATE+", "+UPDATEUSER+", "+DELETEDATE+", "+DELETEUSER+") values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?)";
        db.getWritableDatabase().execSQL(sql,new String[]{obj.getId()+"", obj.getIduser()+"", obj.getStatus()+"",obj.getReturnCount()+"",obj.getReturnAmount()+"",obj.getSalesCount()+"",obj.getSalesAmount()+"",obj.getTotal()+"", obj.getCreateDate(), obj.getCreateUser(),  obj.getUpdateDate(),obj.getUpdateUser(),  obj.getDeleteDate(), obj.getDeleteUser() });
    }

    public long insert(Day obj){
        ContentValues cv = new ContentValues();
        cv.put(IDDAY,obj.getId());
        cv.put(IDUSER,obj.getIduser());
        cv.put(STATUS,obj.getStatus());
        cv.put(RETURNCOUNT,obj.getReturnCount());
        cv.put(RETURNAMOUNT,obj.getReturnAmount());
        cv.put(SALECOUNT,obj.getSalesCount());
        cv.put(SALEAMOUNT,obj.getSalesAmount());
        cv.put(TOTAL,obj.getTotal());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Day obj){
        ContentValues cv = new ContentValues();
        cv.put(IDDAY,obj.getId());
        cv.put(IDUSER,obj.getIduser());
        cv.put(STATUS,obj.getStatus());
        cv.put(RETURNCOUNT,obj.getReturnCount());
        cv.put(RETURNAMOUNT,obj.getReturnAmount());
        cv.put(SALECOUNT,obj.getSalesCount());
        cv.put(SALEAMOUNT,obj.getSalesAmount());
        cv.put(TOTAL,obj.getTotal());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,IDDAY.concat(" = ?"), new String[]{obj.getId()+""});
        return result;
    }

    public long delete(Day obj){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,IDDAY.concat(" = ?"), new String[]{obj.getId()+""});
        return result;
    }
}
