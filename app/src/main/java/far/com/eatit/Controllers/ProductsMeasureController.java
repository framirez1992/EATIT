package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;

import far.com.eatit.API.models.Product;
import far.com.eatit.API.models.ProductMeasure;
import far.com.eatit.Adapters.Models.EditSelectionRowModel;
import far.com.eatit.Adapters.Models.SimpleSeleccionRowModel;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.MeasureUnits;
import far.com.eatit.CloudFireStoreObjects.ProductsMeasure;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class ProductsMeasureController {

    public static final String TABLE_NAME ="PRODUCTSMEASURE";
    public static  String IDPRODUCTMEASURE="idProductMeasure",IDPRODUCT="idProduct",IDMEASUREUNIT="idMeasureUnit",
            PRICE="price",MAXPRICE="maxPrice",MINPRICE="minPrice",ENABLED = "enabled", RANGE = "range" ,
            CREATEDATE = "createDate", CREATEUSER = "createUser", UPDATEDATE="updateDate", UPDATEUSER="updateUser",DELETEDATE="deleteDate",DELETEUSER="deleteUser" ;
    private static String[] columns = new String[]{IDPRODUCTMEASURE, IDPRODUCT, IDMEASUREUNIT,PRICE,MAXPRICE,MINPRICE,ENABLED,RANGE,CREATEDATE, CREATEUSER,UPDATEDATE, UPDATEUSER, DELETEDATE, DELETEUSER};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +IDPRODUCTMEASURE+" INTEGER, "
            +IDPRODUCT+" INTEGER, "
            +IDMEASUREUNIT+" INTEGER,"
            +PRICE+" DECIMAL,"
            +MAXPRICE+" DECIMAL,"
            +MINPRICE+" DECIMAL,"
            +ENABLED+" TEXT, "
            +RANGE+" TEXT, "
            +CREATEDATE+" TEXT, "
            +CREATEUSER+" TEXT, "
            +UPDATEDATE+" TEXT, "
            +UPDATEUSER+" TEXT, "
            +DELETEDATE+" TEXT, "
            +DELETEUSER+" TEXT "
            +")";
    Context context;
    DB db;
    private static  ProductsMeasureController instance;

    private ProductsMeasureController(Context c){
        this.context = c;
        this.db = DB.getInstance(c);
    }

    public static ProductsMeasureController getInstance(Context c){
        if(instance == null){
            instance = new ProductsMeasureController(c);
        }
        return instance;
    }


    public void insertOrUpdate(ProductMeasure obj){
        String sql ="insert or replace into "+TABLE_NAME+" ("+IDPRODUCTMEASURE+", "+IDPRODUCT+", "+IDMEASUREUNIT+", "+PRICE+", "+MAXPRICE+", "+MINPRICE+", "+ENABLED+", "+RANGE +", "+CREATEDATE+", "+CREATEUSER+","+UPDATEDATE+", "+UPDATEUSER+", "+DELETEDATE+", "+DELETEUSER+") values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        db.getWritableDatabase().execSQL(sql,new String[]{obj.getId()+"", obj.getIdproduct()+"", obj.getIdmeasureUnit()+"",obj.getPrice()+"",obj.getMaxPrice()+"", obj.getMinPrice()+"",obj.isEnabled()?"1":"0",obj.isRange()?"1":"0" ,obj.getCreateDate(), obj.getCreateUser(),  obj.getUpdateDate(),obj.getUpdateUser(),  obj.getDeleteDate(), obj.getDeleteUser() });
    }

    public ArrayList<ProductMeasure>getProductsMeasure(String where, String[] args){
       ArrayList<ProductMeasure> result = new ArrayList<>();
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, where, args, null, null, IDPRODUCTMEASURE);
            while(c.moveToNext()){
               result.add(new ProductMeasure(c));

            }c.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public ArrayList<KV>getProductsMeasureKVByCodeProduct(int idProduct){
        ArrayList<KV> result = new ArrayList<>();
        try {
            String sql = "select um."+MeasureUnitsController.CODE+" as CODE, um."+MeasureUnitsController.DESCRIPTION+" as DESCRIPTION " +
                    "FROM "+MeasureUnitsController.TABLE_NAME+" um " +
                    "INNER JOIN "+TABLE_NAME+" pm on pm."+IDMEASUREUNIT+" = um."+MeasureUnitsController.IDMEASUREUNIT +" "+
                    "WHERE "+IDPRODUCT +" = ? AND "+ENABLED+" = ?";
            String[] args = new String[]{idProduct+"", "1"};

            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql,args );
            while(c.moveToNext()){
                result.add(new KV(c.getString(c.getColumnIndex("CODE")),
                        c.getString(c.getColumnIndex("DESCRIPTION"))));

            }c.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }
    public long insert(ProductMeasure obj){

        ContentValues cv = new ContentValues();
        cv.put(IDPRODUCTMEASURE, obj.getId());
        cv.put(IDPRODUCT,obj.getIdproduct() );
        cv.put(IDMEASUREUNIT,obj.getIdmeasureUnit());
        cv.put(PRICE,obj.getPrice());
        cv.put(MINPRICE,obj.getMinPrice());
        cv.put(MAXPRICE,obj.getMaxPrice());
        cv.put(ENABLED, obj.isEnabled());
        cv.put(RANGE, obj.isRange());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(ProductMeasure obj){
        ContentValues cv = new ContentValues();
        cv.put(IDPRODUCTMEASURE, obj.getId());
        cv.put(IDPRODUCT,obj.getIdproduct() );
        cv.put(IDMEASUREUNIT,obj.getIdmeasureUnit());
        cv.put(PRICE,obj.getPrice());
        cv.put(MINPRICE,obj.getMinPrice());
        cv.put(MAXPRICE,obj.getMaxPrice());
        cv.put(ENABLED, obj.isEnabled());
        cv.put(RANGE, obj.isRange());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,IDPRODUCTMEASURE.concat("= ?"), new String[]{obj.getId()+""});
        return result;
    }

    public long delete(ProductMeasure obj){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,IDPRODUCTMEASURE.concat("= ?"), new String[]{obj.getId()+""});
        return result;
    }

    /*
    public ArrayList<EditSelectionRowModel> getSSRMByCodeProduct(String codeProduct){
        ArrayList<EditSelectionRowModel> result = new ArrayList<>();
        try {
            String sql = "SELECT pm." + CODEMEASURE + " AS CODE, mu." + MeasureUnitsController.DESCRIPTION + " AS DESCRIPTION, ifnull(pm."+PRICE+", 0.0) as PRICE, pm."+ENABLED+" AS ENABLED " +
                    "FROM " + TABLE_NAME + " pm " +
                    "INNER JOIN " + MeasureUnitsController.TABLE_NAME + " mu ON pm." + CODEMEASURE + " = mu." + MeasureUnitsController.CODE + " " +
                    "WHERE pm." + CODEPRODUCT + " = ? AND pm.ENABLED = ?";
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{codeProduct, "1"});
            while (c.moveToNext()) {
                result.add(new EditSelectionRowModel(c.getString(c.getColumnIndex("CODE")),
                        c.getString(c.getColumnIndex("DESCRIPTION")),
                        c.getString(c.getColumnIndex("PRICE")),
                        c.getString(c.getColumnIndex("ENABLED")).equals("1")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }*/



}
