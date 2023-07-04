package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;

import far.com.eatit.API.models.Payment;
import far.com.eatit.API.models.Table;
import far.com.eatit.DataBase.DB;

public class PaymentController {
    public static final String TABLE_NAME ="Payments";
    public static  String IDPAYMENT = "idPayment", IDRECEIPT = "idReceipt", TYPE = "type" ,TOTAL="total",REFERENCE="reference",REFERECE2="reference2",REFERENCE3="reference3",
            CREATEDATE = "createDate", CREATEUSER = "createUser", UPDATEDATE="updateDate", UPDATEUSER="updateUser",DELETEDATE="deleteDate",DELETEUSER="deleteUser" ;

    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +IDPAYMENT+" INTEGER, "
            +IDRECEIPT+" INTEGER,"
            +TYPE+" INTEGER, "
            +TOTAL+" DECIMAL(11, 3), "
            +REFERENCE+" TEXT, "
            +REFERECE2+" TEXT, "
            +REFERENCE3+" TEXT, "
            +CREATEDATE+" TEXT, "
            +CREATEUSER+" TEXT, "
            +UPDATEDATE+" TEXT, "
            +UPDATEUSER+" TEXT, "
            +DELETEDATE+" TEXT, "
            +DELETEUSER+" TEXT "
            +")";
    public static String[] columns = new String[]{IDPAYMENT, IDRECEIPT, TYPE,TOTAL, REFERENCE,REFERECE2,REFERENCE3,CREATEDATE, CREATEUSER,UPDATEDATE, UPDATEUSER, DELETEDATE, DELETEUSER};
    Context context;
    private static PaymentController instance;
    DB db;
    private PaymentController(Context c){
        this.context = c;
        this.db = DB.getInstance(c);
    }
    public static PaymentController getInstance(Context c){
        if(instance == null){
            instance = new PaymentController(c);
        }
        return instance;
    }

    public void insertOrUpdate(Payment obj){
        String sql ="insert or replace into "+TABLE_NAME+" ("+IDPAYMENT+", "+IDRECEIPT+", "+TYPE+", "+TOTAL+", "+REFERENCE+","+REFERECE2+","+REFERENCE3+", "+CREATEDATE+", "+CREATEUSER+","+UPDATEDATE+", "+UPDATEUSER+", "+DELETEDATE+", "+DELETEUSER+") values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        db.getWritableDatabase().execSQL(sql,new String[]{obj.getId()+"", obj.getIdreceipt()+"", obj.getType()+"",obj.getTotal()+"" , obj.getReference(),obj.getReference2(),obj.getReference3(),obj.getCreateDate(), obj.getCreateUser(),  obj.getUpdateDate(),obj.getUpdateUser(),  obj.getDeleteDate(), obj.getDeleteUser() });
    }

    public long insert(Payment obj){
        ContentValues cv = new ContentValues();
        cv.put(IDPAYMENT,obj.getId());
        cv.put(IDRECEIPT,obj.getIdreceipt());
        cv.put(TYPE,obj.getType());
        cv.put(TOTAL,obj.getTotal());
        cv.put(REFERENCE,obj.getReference());
        cv.put(REFERECE2,obj.getReference2());
        cv.put(REFERENCE3,obj.getReference3());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Payment obj){
        ContentValues cv = new ContentValues();
        cv.put(IDPAYMENT,obj.getId());
        cv.put(IDRECEIPT,obj.getIdreceipt());
        cv.put(TYPE,obj.getType());
        cv.put(TOTAL,obj.getTotal());
        cv.put(REFERENCE,obj.getReference());
        cv.put(REFERECE2,obj.getReference2());
        cv.put(REFERENCE3,obj.getReference3());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,IDPAYMENT.concat(" = ?"), new String[]{obj.getId()+""});
        return result;
    }

    public long delete(Payment obj){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,IDPAYMENT.concat(" = ?"), new String[]{obj.getId()+""});
        return result;
    }
}
