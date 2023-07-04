package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;

import far.com.eatit.API.models.Table;
import far.com.eatit.DataBase.DB;

public class TableController {
    public static final String TABLE_NAME ="Tables";
    public static  String IDTABLE = "idTable", IDCOMPANY = "idCompany", DESCRIPTION = "description" ,ENABLED="enabled",
            CREATEDATE = "createDate", CREATEUSER = "createUser", UPDATEDATE="updateDate", UPDATEUSER="updateUser",DELETEDATE="deleteDate",DELETEUSER="deleteUser" ;

    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +IDTABLE+" INTEGER, "
            +IDCOMPANY+" INTEGER,"
            +DESCRIPTION+" TEXT, "
            +ENABLED+" TEXT, "
            +CREATEDATE+" TEXT, "
            +CREATEUSER+" TEXT, "
            +UPDATEDATE+" TEXT, "
            +UPDATEUSER+" TEXT, "
            +DELETEDATE+" TEXT, "
            +DELETEUSER+" TEXT "
            +")";
    public static String[] columns = new String[]{IDTABLE, IDCOMPANY, DESCRIPTION,ENABLED, CREATEDATE, CREATEUSER,UPDATEDATE, UPDATEUSER, DELETEDATE, DELETEUSER};
    Context context;
    private static TableController instance;
    DB db;
    private TableController(Context c){
        this.context = c;
        this.db = DB.getInstance(c);
    }
    public static TableController getInstance(Context c){
        if(instance == null){
            instance = new TableController(c);
        }
        return instance;
    }

    public void insertOrUpdate(Table obj){
        String sql ="insert or replace into "+TABLE_NAME+" ("+IDTABLE+", "+IDCOMPANY+", "+DESCRIPTION+", "+ENABLED+", "+CREATEDATE+", "+CREATEUSER+","+UPDATEDATE+", "+UPDATEUSER+", "+DELETEDATE+", "+DELETEUSER+") values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
        db.getWritableDatabase().execSQL(sql,new String[]{obj.getIdtable()+"", obj.getIdcompany()+"", obj.getDescription(),obj.isEnabled()?"1":"0" , obj.getCreateDate(), obj.getCreateUser(),  obj.getUpdateDate(),obj.getUpdateUser(),  obj.getDeleteDate(), obj.getDeleteUser() });
    }

    public long insert(Table obj){
        ContentValues cv = new ContentValues();

        cv.put(IDTABLE,obj.getIdtable());
        cv.put(IDCOMPANY,obj.getIdcompany());
        cv.put(DESCRIPTION,obj.getDescription());
        cv.put(ENABLED,obj.isEnabled()?"1":"0");
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Table obj){
        ContentValues cv = new ContentValues();
        cv.put(IDTABLE,obj.getIdtable());
        cv.put(IDCOMPANY,obj.getIdcompany());
        cv.put(DESCRIPTION,obj.getDescription());
        cv.put(ENABLED,obj.isEnabled()?"1":"0");
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,IDTABLE.concat(" = ?"), new String[]{obj.getIdtable()+""});
        return result;
    }

    public long delete(Table obj){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,IDTABLE.concat(" = ?"), new String[]{obj.getIdtable()+""});
        return result;
    }
}
