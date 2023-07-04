package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import far.com.eatit.API.models.UserRole;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Utils.Funciones;

public class UserRolesController {

    public static final String TABLE_NAME ="UserRoles";
    public static  String ID = "id", IDLICENSE = "idLicense" ,CODE = "code" , DESCRIPTION = "description" ,
            CREATEDATE = "createDate", CREATEUSER = "createUser", UPDATEDATE="updateDate", UPDATEUSER="updateUser",DELETEDATE="deleteDate",DELETEUSER="deleteUser" ;

    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +ID+" INTEGER, "
            +IDLICENSE+" INTEGER,"
            +CODE+" TEXT, "
            +DESCRIPTION+" TEXT, "
            +CREATEDATE+" TEXT, "
            +CREATEUSER+" TEXT, "
            +UPDATEDATE+" TEXT, "
            +UPDATEUSER+" TEXT, "
            +DELETEDATE+" TEXT, "
            +DELETEUSER+" TEXT "
            +")";
    public static String[] columns = new String[]{ID, IDLICENSE,CODE, DESCRIPTION, CREATEDATE, CREATEUSER,UPDATEDATE, UPDATEUSER, DELETEDATE, DELETEUSER};
    Context context;
    private static UserRolesController instance;
    DB db;
    private UserRolesController(Context c){
        this.context = c;
        this.db = DB.getInstance(c);
    }
    public static UserRolesController getInstance(Context c){
        if(instance == null){
            instance = new UserRolesController(c);
        }
        return instance;
    }

    public void insertOrUpdate(UserRole obj){
        String sql ="insert or replace into "+TABLE_NAME+" ("+ID+", "+IDLICENSE+", "+CODE+", "+DESCRIPTION+", "+CREATEDATE+", "+CREATEUSER+","+UPDATEDATE+", "+UPDATEUSER+", "+DELETEDATE+", "+DELETEUSER+") values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
        db.getWritableDatabase().execSQL(sql,new String[]{obj.getId()+"", obj.getIdLicense()+"", obj.getCode(), obj.getDescription(),  obj.getCreateDate(), obj.getCreateUser(),  obj.getUpdateDate(),obj.getUpdateUser(),  obj.getDeleteDate(), obj.getDeleteUser() });
    }

    public long insert(UserRole obj){
        ContentValues cv = new ContentValues();

        cv.put(ID,obj.getId());
        cv.put(IDLICENSE,obj.getIdLicense());
        cv.put(CODE,obj.getCode());
        cv.put(DESCRIPTION,obj.getDescription());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(UserRole obj){
        ContentValues cv = new ContentValues();
        cv.put(ID,obj.getId());
        cv.put(IDLICENSE,obj.getIdLicense());
        cv.put(CODE,obj.getCode());
        cv.put(DESCRIPTION,obj.getDescription());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,ID.concat(" = ?"), new String[]{obj.getId()+""});
        return result;
    }

    public long delete(UserRole obj){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,ID.concat(" = ?"), new String[]{obj.getId()+""});
        return result;
    }


}
