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

import far.com.eatit.API.models.ProductSubType;
import far.com.eatit.API.models.ProductType;
import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Adapters.Models.SimpleSeleccionRowModel;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.ProductsSubTypes;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class ProductsSubTypesController {
    public static final  String TABLE_NAME = "PRODUCTSSUBTYPES";
    public static String IDPRODUCTSUBTYPE="idProductSubType",IDPRODUCTTYPE="idProductType",CODE = "code", DESCRIPTION = "description", POSITION = "position",
            CREATEDATE = "createDate", CREATEUSER = "createUser", UPDATEDATE="updateDate", UPDATEUSER="updateUser",DELETEDATE="deleteDate",DELETEUSER="deleteUser" ;
    private String[]colums = new String[]{IDPRODUCTSUBTYPE,IDPRODUCTTYPE,CODE,  DESCRIPTION, POSITION, CREATEDATE, CREATEUSER,UPDATEDATE, UPDATEUSER, DELETEDATE, DELETEUSER};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +IDPRODUCTSUBTYPE+" TEXT,"
            +IDPRODUCTTYPE+" TEXT,"
            +CODE+" TEXT,"
            +DESCRIPTION+" TEXT, "
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
    private static  ProductsSubTypesController instance;
    private ProductsSubTypesController(Context c){
        this.context = c;
        this.db = DB.getInstance(c);
    }
    public static ProductsSubTypesController getInstance(Context context){
        if(instance == null){
            instance = new ProductsSubTypesController(context);
        }
        return instance;
    }

    public void insertOrUpdate(ProductSubType obj){
        String sql ="insert or replace into "+TABLE_NAME+" ("+IDPRODUCTSUBTYPE+", "+IDPRODUCTTYPE+", "+CODE+", "+DESCRIPTION+", "+POSITION+", "+CREATEDATE+", "+CREATEUSER+","+UPDATEDATE+", "+UPDATEUSER+", "+DELETEDATE+", "+DELETEUSER+") values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        db.getWritableDatabase().execSQL(sql,new String[]{obj.getId()+"", obj.getIdproductType()+"",obj.getCode(), obj.getDescription(),obj.getPosition()+"" ,obj.getCreateDate(), obj.getCreateUser(),  obj.getUpdateDate(),obj.getUpdateUser(),  obj.getDeleteDate(), obj.getDeleteUser() });
    }

    public long insert(ProductSubType obj){
        ContentValues cv = new ContentValues();
        cv.put(IDPRODUCTSUBTYPE,obj.getId());
        cv.put(IDPRODUCTTYPE,obj.getIdproductType());
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

    public long update(ProductSubType obj){
        ContentValues cv = new ContentValues();
        cv.put(IDPRODUCTSUBTYPE,obj.getId());
        cv.put(IDPRODUCTTYPE,obj.getIdproductType());
        cv.put(CODE,obj.getCode());
        cv.put(DESCRIPTION,obj.getDescription());
        cv.put(POSITION, obj.getPosition());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,IDPRODUCTSUBTYPE.concat("= ?"), new String[]{obj.getId()+""});
        return result;
    }

    public long delete(ProductSubType obj){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,IDPRODUCTSUBTYPE.concat(" = ?"), new String[]{obj.getId()+""});
        return result;
    }



    public ArrayList<ProductSubType> getProductSubTypes(String[] camposFiltros, String[]argumentos, String campoOrderBy){

        ArrayList<ProductSubType> result = new ArrayList<>();
        if(campoOrderBy == null){
            campoOrderBy=DESCRIPTION;
        }
        try {
            Cursor c =  DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, colums, ((camposFiltros!=null)?DB.getWhereFormat(camposFiltros):null), argumentos, null, null, campoOrderBy);
            while (c.moveToNext()){
                result.add(new ProductSubType(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public ProductSubType getProductTypeByCode(String code){
        ArrayList<ProductSubType> pts = getProductSubTypes(new String[]{CODE}, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }
/*
    public ArrayList<SimpleRowModel> getAllProductSubTypesSRM(String where,String[] args, String campoOrderBy){

        ArrayList<SimpleRowModel> result = new ArrayList<>();
        if(where != null)
            where = "WHERE "+where;
        else
            where = "";

        if(campoOrderBy == null)
            campoOrderBy="pst."+ORDER+" ASC, pst."+DESCRIPTION;

        try {

            String sql = "SELECT pst."+ CODE+" AS CODE, pst."+DESCRIPTION+" AS DESCRIPTION,  pst."+DATE+" AS DATE " +
                    "FROM "+TABLE_NAME+" pst " +
                    "INNER JOIN "+ProductsTypesController.TABLE_NAME+" pt on pst."+CODETYPE+" = pt."+ProductsTypesController.CODE+" " +
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
    }*/

    /**
     * Simple seleccion row model
     * @param where
     * @param args
     * @param campoOrder
     * @return
     */
    public ArrayList<SimpleSeleccionRowModel> getProductSubTypesSSRM(String where, String[] args, String campoOrder){
        ArrayList<SimpleSeleccionRowModel> result = new ArrayList<>();
        if(campoOrder == null){ campoOrder=POSITION+" ASC, "+DESCRIPTION;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT "+CODE+" as CODE, "+DESCRIPTION+" as DESCRIPTION " +
                    "FROM "+TABLE_NAME+"  " +
                    where+
                    " ORDER BY "+campoOrder;
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
         fillSpinner(spn,addTodos, null);
    }
    public void fillSpinner(Spinner spn, boolean addTodos, String type){
        String orderBy = ProductsSubTypesController.POSITION+" ASC, "+ProductsSubTypesController.DESCRIPTION;
        String[] camposFiltros = null;
        String[]args = null;
        if(type != null){
            camposFiltros = new String[]{IDPRODUCTTYPE};
            args = new String[]{type};
        }
        ArrayList<ProductSubType> list = getProductSubTypes(camposFiltros, args, orderBy);
        ArrayList<KV> data = new ArrayList<>();
        if(addTodos){
            KV obj = new KV("0", "TODOS");
            data.add(obj);
        }
        for(ProductSubType pt : list){
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
    public void fillSpinnerForLockedProducts(Spinner spn, boolean addTodos, String type){
        ArrayList<KV> data = new ArrayList<>();
        if(addTodos){
            KV obj = new KV("0", "TODOS");
            data.add(obj);
        }
        try {
            String sql = "SELECT pst." + ProductsSubTypesController.CODE + ", pst." + ProductsSubTypesController.DESCRIPTION + " " +
                    "FROM " + ProductsSubTypesController.TABLE_NAME + " pst " +
                    "INNER JOIN " + ProductsController.TABLE_NAME + " p on p." + ProductsController.IDPRODUCTSUBTYPE + " = pst." + ProductsSubTypesController.CODE + " " +
                    "INNER JOIN " + ProductsControlController.TABLE_NAME + " pc on pc." + ProductsControlController.CODEPRODUCT + " = p." + ProductsController.CODE + " " +
                    "WHERE pc." + ProductsControlController.BLOQUED + " = ? AND p."+ProductsController.IDPRODUCTTYPE  +" = ? " +
                    "GROUP BY pst." + ProductsSubTypesController.CODE + ", pst." + ProductsSubTypesController.DESCRIPTION + " " +
                    "ORDER BY pst." + ProductsSubTypesController.POSITION + " ASC, pst." + ProductsSubTypesController.DESCRIPTION;

            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{"1", type});
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
