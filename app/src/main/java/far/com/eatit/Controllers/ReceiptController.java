package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

import far.com.eatit.API.models.Receipt;
import far.com.eatit.API.models.Table;
import far.com.eatit.Adapters.Models.ReceiptResumeModel;
import far.com.eatit.Adapters.Models.ReceiptSavedModel;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.Receipts;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;

public class ReceiptController {
    public static final String TABLE_NAME ="RECEIPTS";
    //public static final String TABLE_NAME_HISTORY ="RECEIPTS_HISTORY";
    public static  String IDRECEIPT = "idReceipt",IDSALE = "idSale",STATUS = "status",  NCF = "ncf" , TOTAL = "total",
            CREATEDATE = "createDate", CREATEUSER = "createUser", UPDATEDATE="updateDate", UPDATEUSER="updateUser",DELETEDATE="deleteDate",DELETEUSER="deleteUser" ;;
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +IDRECEIPT+" INTEGER,"
            +IDSALE+" INTEGER,"
            +STATUS+" INTEGER, "
            +NCF+" TEXT,"
            +TOTAL+" DECIMAL(11, 3), "
            +CREATEDATE+" TEXT, "
            +CREATEUSER+" TEXT, "
            +UPDATEDATE+" TEXT, "
            +UPDATEUSER+" TEXT, "
            +DELETEDATE+" TEXT, "
            +DELETEUSER+" TEXT "
            +")";
    public static String[] columns = new String[]{IDRECEIPT,IDSALE,STATUS, NCF,TOTAL,CREATEDATE, CREATEUSER,UPDATEDATE, UPDATEUSER, DELETEDATE, DELETEUSER};
    Context context;
    DB db;
    private static ReceiptController instance;
    private ReceiptController(Context c){
        this.context = c;
        this.db = DB.getInstance(c);
    }
    public static ReceiptController getInstance(Context c){
        if(instance == null){
            instance = new ReceiptController(c);
        }
        return instance;
    }

    public void insertOrUpdate(Receipt obj){
        String sql ="insert or replace into "+TABLE_NAME+" ("+IDRECEIPT+", "+IDSALE+", "+STATUS+", "+NCF+", "+TOTAL+", "+CREATEDATE+", "+CREATEUSER+","+UPDATEDATE+", "+UPDATEUSER+", "+DELETEDATE+", "+DELETEUSER+") values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        db.getWritableDatabase().execSQL(sql,new String[]{obj.getId()+"", obj.getIdsale()+"", obj.getStatus()+"",obj.getNcf() ,obj.getTotal()+"", obj.getCreateDate(), obj.getCreateUser(),  obj.getUpdateDate(),obj.getUpdateUser(),  obj.getDeleteDate(), obj.getDeleteUser() });
    }


    public long insert(Receipt obj){
        ContentValues cv = new ContentValues();
        cv.put(IDRECEIPT,obj.getId() );
        cv.put(IDSALE, obj.getIdsale());
        cv.put(STATUS, obj.getStatus());
        cv.put(NCF,obj.getNcf());
        cv.put(TOTAL, obj.getTotal());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long delete(Receipt obj){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,IDRECEIPT.concat("= ?"), new String[]{obj.getId()+""});
        return result;
    }










    public ArrayList<ReceiptSavedModel> getReceiptsSM(String codeAreaDetail){
        ArrayList<ReceiptSavedModel> result = new ArrayList<>();
        try {
            String sql = "SELECT r." + IDRECEIPT + " as CODE,r."+STATUS+" as STATUS, '' as CODEUSER, '' as USERNAME, r." + NCF + " as NCF, " +
                    "ad." + TableController.IDTABLE + " as CODEAREA, a." + TableController.DESCRIPTION + " as AREADESCRIPTION, '' as CODEAREADETAIL, '' as AREADETAILDESCRIPTION, " +
                    "r." + TOTAL+", 0 as TAXES, 0 as DISCOUNT, r." + TOTAL + " as TOTAL, r." + CREATEDATE + " as DATE, r." + UPDATEDATE +" "+
                    "FROM " + TABLE_NAME + " r " +
                    "INNER JOIN " + TableController.TABLE_NAME + " ad on r." + IDSALE + " = ad." + TableController.IDTABLE + " " +//ARREGLAR ESTO
                    "INNER JOIN " + AreasController.TABLE_NAME + " a on ad." + AreasDetailController.IDAREA  + " = a." + AreasController.IDAREA + " " +
                    "ORDER BY r." + CREATEDATE + " DESC";

            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                result.add(new ReceiptSavedModel(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }







}
