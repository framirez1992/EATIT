package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;

import far.com.eatit.API.models.Client;
import far.com.eatit.API.models.Company;
import far.com.eatit.DataBase.DB;

public class ClientController {
    public static final String TABLE_NAME ="CLIENTS";
    public static  String IDCLIENT="idClient",IDCOMPANY="idCompany", CODE = "code", NAME = "name" ,
            DOCUMENT = "document",PHONE = "phone", PHONE2="phone2",PHONE3="phone3", ADDRESS="address", ADDRESS2="address2",ADDRESS3="address3",DATA="data", DATA2="data2",DATA3="data3",
            CREATEDATE = "createDate", CREATEUSER = "createUser", UPDATEDATE="updateDate", UPDATEUSER="updateUser",DELETEDATE="deleteDate",DELETEUSER="deleteUser" ;
    String[] columns = new String[]{IDCLIENT,IDCOMPANY,CODE, NAME, DOCUMENT, PHONE, PHONE2,PHONE3, ADDRESS, ADDRESS2,ADDRESS3,DATA, DATA2, DATA3, CREATEDATE, CREATEUSER,UPDATEDATE, UPDATEUSER, DELETEDATE, DELETEUSER};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +IDCLIENT+" INTEGER, "
            +IDCOMPANY+" INTEGER, "
            +CODE+" TEXT, "
            +NAME+" TEXT, "
            +DOCUMENT+" TEXT, "
            +PHONE+" TEXT, "
            +PHONE2+" TEXT, "
            +PHONE3+" TEXT, "
            +ADDRESS+" TEXT,"
            +ADDRESS2+" TEXT,"
            +ADDRESS3+" TEXT,"
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
    private static ClientController instance;
    private ClientController(Context c){
        this.context = c;
        this.db = DB.getInstance(c);
    }

    public static ClientController getInstance(Context context){
        if(instance == null){
            instance = new ClientController(context);
        }
        return instance;
    }

    public void insertOrUpdate(Client obj){
        String sql ="insert or replace into "+TABLE_NAME+" ("+IDCLIENT+", "+IDCOMPANY+", "+CODE+", "+NAME+","+DOCUMENT+","+PHONE+","+PHONE2+","+PHONE3+","+ADDRESS+","+ADDRESS2+","+ADDRESS3+","+DATA+","+DATA2+","+DATA3+", "+CREATEDATE+", "+CREATEUSER+","+UPDATEDATE+", "+UPDATEUSER+", "+DELETEDATE+", "+DELETEUSER+") values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        db.getWritableDatabase().execSQL(sql,new String[]{obj.getId()+"", obj.getIdcompany()+"", obj.getCode(), obj.getName(),obj.getDocument(),obj.getPhone(),obj.getPhone2(),obj.getPhone3(),obj.getAddress(),obj.getAddress2(),obj.getAddress3(),obj.getData(),obj.getData2(),obj.getData3(), obj.getCreateDate(), obj.getCreateUser(),  obj.getUpdateDate(),obj.getUpdateUser(),  obj.getDeleteDate(), obj.getDeleteUser() });
    }

    public long insert(Client obj){
        ContentValues cv = new ContentValues();
        cv.put(IDCOMPANY,obj.getId() );
        cv.put(IDCOMPANY,obj.getIdcompany() );
        cv.put(CODE,obj.getCode() );
        cv.put(NAME,obj.getName());
        cv.put(DOCUMENT, obj.getDocument());
        cv.put(PHONE,obj.getPhone() );
        cv.put(PHONE2,obj.getPhone2() );
        cv.put(PHONE3,obj.getPhone3() );
        cv.put(ADDRESS,obj.getAddress() );
        cv.put(ADDRESS2,obj.getAddress2() );
        cv.put(ADDRESS3,obj.getAddress3() );
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

    public long update(Client obj){
        ContentValues cv = new ContentValues();
        cv.put(IDCOMPANY,obj.getId() );
        cv.put(IDCOMPANY,obj.getIdcompany() );
        cv.put(CODE,obj.getCode() );
        cv.put(NAME,obj.getName());
        cv.put(DOCUMENT, obj.getDocument());
        cv.put(PHONE,obj.getPhone() );
        cv.put(PHONE2,obj.getPhone2() );
        cv.put(PHONE3,obj.getPhone3() );
        cv.put(ADDRESS,obj.getAddress() );
        cv.put(ADDRESS2,obj.getAddress2() );
        cv.put(ADDRESS3,obj.getAddress3() );
        cv.put(DATA, obj.getData());
        cv.put(DATA2, obj.getData2());
        cv.put(DATA3, obj.getData3());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = db.getWritableDatabase().update(TABLE_NAME,cv,IDCLIENT.concat("= ?"),new String[]{ obj.getId()+""});
        return result;
    }

    public long delete(Company obj){
        long result = db.getWritableDatabase().delete(TABLE_NAME,IDCLIENT.concat("= ?"),new String[]{ obj.getId()+""});
        return result;
    }

}
