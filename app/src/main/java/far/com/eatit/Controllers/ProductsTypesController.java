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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import far.com.eatit.API.models.Product;
import far.com.eatit.API.models.ProductType;
import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Adapters.Models.SimpleSeleccionRowModel;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.ProductsControl;
import far.com.eatit.CloudFireStoreObjects.ProductsSubTypes;
import far.com.eatit.CloudFireStoreObjects.ProductsTypes;
import far.com.eatit.CloudFireStoreObjects.Users;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class ProductsTypesController {
    public static final  String TABLE_NAME = "PRODUCTSTYPES";
    public static String IDPRODUCTTYPE="idProductType",IDLICENSE="idLicense", CODE = "code", DESCRIPTION = "description", POSITION = "position",
            CREATEDATE = "createDate", CREATEUSER = "createUser", UPDATEDATE="updateDate", UPDATEUSER="updateUser",DELETEDATE="deleteDate",DELETEUSER="deleteUser" ;
    public static String[]colums = new String[]{IDPRODUCTTYPE,IDLICENSE,CODE, DESCRIPTION, POSITION, CREATEDATE, CREATEUSER,UPDATEDATE, UPDATEUSER, DELETEDATE, DELETEUSER};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +IDPRODUCTTYPE+" INTEGER, "
            +IDLICENSE+" INTEGER, "
            +CODE+" TEXT, "
            +DESCRIPTION+" TEXT,"
            +POSITION+" INTEGER, "
            +CREATEDATE+" TEXT, "
            +CREATEUSER+" TEXT, "
            +UPDATEDATE+" TEXT, "
            +UPDATEUSER+" TEXT, "
            +DELETEDATE+" TEXT, "
            +DELETEUSER+" TEXT "
            +")";

    Context context;
    DB db;
    private static ProductsTypesController instance;
    private ProductsTypesController(Context c){
        this.context = c;
        this.db = DB.getInstance(c);
    }
    public static ProductsTypesController getInstance(Context context){
        if(instance == null){
            instance = new ProductsTypesController(context);
        }
        return instance;
    }

    public void insertOrUpdate(ProductType obj){
        String sql ="insert or replace into "+TABLE_NAME+" ("+IDPRODUCTTYPE+", "+IDLICENSE+", "+CODE+", "+DESCRIPTION+", "+POSITION+", "+CREATEDATE+", "+CREATEUSER+","+UPDATEDATE+", "+UPDATEUSER+", "+DELETEDATE+", "+DELETEUSER+") values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        db.getWritableDatabase().execSQL(sql,new String[]{obj.getId()+"", obj.getIdLicense()+"",obj.getCode(), obj.getDescription(),obj.getPosition()+"" ,obj.getCreateDate(), obj.getCreateUser(),  obj.getUpdateDate(),obj.getUpdateUser(),  obj.getDeleteDate(), obj.getDeleteUser() });
    }

    public long insert(ProductType obj){
        ContentValues cv = new ContentValues();
        cv.put(IDPRODUCTTYPE,obj.getId());
        cv.put(IDLICENSE,obj.getIdLicense());
        cv.put(CODE,obj.getCode());
        cv.put(DESCRIPTION,obj.getDescription());
        cv.put(POSITION, obj.getPosition());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }


    public long update(ProductType obj){
        ContentValues cv = new ContentValues();
        cv.put(IDPRODUCTTYPE,obj.getId());
        cv.put(IDLICENSE,obj.getIdLicense());
        cv.put(CODE,obj.getCode());
        cv.put(DESCRIPTION,obj.getDescription());
        cv.put(POSITION, obj.getPosition());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,IDPRODUCTTYPE.concat("= ?"), new String[]{obj.getId()+""});
        return result;
    }


    public long delete(ProductType obj){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,IDPRODUCTTYPE.concat("= ?"), new String[]{obj.getId()+""});
        return result;
    }

    public ArrayList<ProductType> getProductTypes(String[] camposFiltros,String[]argumentos, String campoOrderBy){

        ArrayList<ProductType> result = new ArrayList<>();
        if(campoOrderBy == null){
            campoOrderBy=DESCRIPTION;
        }
        try {
            Cursor c =  DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, colums, ((camposFiltros!=null)?DB.getWhereFormat(camposFiltros):null), argumentos, null, null, campoOrderBy);
            while (c.moveToNext()){
                result.add(new ProductType(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public int getCount(String destiny){
        int result = 0;
        ArrayList<ProductType> pts = getProductTypes(null, null, null);
        if(pts != null){
            result =  pts.size();
        }
        return result;
    }

    public ArrayList<SimpleRowModel> getAllProductTypesSRM(String where, String[] args){
        ArrayList<SimpleRowModel> result = new ArrayList<>();
       /* String orderBy = ORDER+" ASC, "+DESCRIPTION;
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, colums, where, args, null, null, orderBy);
            while(c.moveToNext()){
                result.add(new SimpleRowModel(c.getString(c.getColumnIndex(CODE)), c.getString(c.getColumnIndex(DESCRIPTION)), c.getString(c.getColumnIndex(MDATE)) != null));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        */

        return result;

    }

    /**
     * Simple seleccion row model
     * @param where
     * @param args
     * @param campoOrder
     * @return
     */
    public ArrayList<SimpleSeleccionRowModel> getProductTypesSSRM(String where, String[] args, String campoOrder){
        ArrayList<SimpleSeleccionRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = DESCRIPTION;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT "+CODE+" as CODE, "+DESCRIPTION+" as DESCRIPTION " +
                    "FROM "+TABLE_NAME+"  " +
                    where;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while(c.moveToNext()){
                String code = c.getString(c.getColumnIndex("CODE"));
                String name = c.getString(c.getColumnIndex("DESCRIPTION"));
                result.add(new SimpleSeleccionRowModel(code,name ,false));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }







    public void fillSpinner(Spinner spn, boolean addTodos){
        String orderBy = ProductsTypesController.POSITION+" ASC, "+ProductsTypesController.DESCRIPTION;
        ArrayList<ProductType> list = getProductTypes(null,null, orderBy);
        ArrayList<KV> data = new ArrayList<>();
        if(addTodos){
            KV obj = new KV("0", "TODOS");
            data.add(obj);
        }
        for(ProductType pt : list){
            data.add(new KV(pt.getCode(), pt.getDescription()));
        }

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }

    /**
     * Retorna los ProductTypes de los productos que estan bloqueados
     * @param spn
     * @param addTodos
     */
    public void fillSpinnerForLockedProducts(Spinner spn, boolean addTodos){
        ArrayList<KV> data = new ArrayList<>();
        if(addTodos){
            KV obj = new KV("0", "TODOS");
            data.add(obj);
        }
        try {
            String sql = "SELECT pt." + ProductsTypesController.CODE + ", pt." + ProductsTypesController.DESCRIPTION + " " +
                    "FROM " + ProductsTypesController.TABLE_NAME + " pt " +
                    "INNER JOIN " + ProductsController.TABLE_NAME + " p on p." + ProductsController.IDPRODUCTTYPE  + " = pt." + ProductsTypesController.CODE + " " +
                    "INNER JOIN " + ProductsControlController.TABLE_NAME + " pc on pc." + ProductsControlController.CODEPRODUCT + " = p." + ProductsController.CODE + " " +
                    "WHERE pc." + ProductsControlController.BLOQUED + " = ? " +
                    "GROUP BY pt." + ProductsTypesController.CODE + ", pt." + ProductsTypesController.DESCRIPTION + " " +
                    "ORDER BY pt." + ProductsTypesController.POSITION + " ASC, pt." + ProductsTypesController.DESCRIPTION;

            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{"1"});
            while (c.moveToNext()) {
                data.add(new KV(c.getString(0), c.getString(1)));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }





}
