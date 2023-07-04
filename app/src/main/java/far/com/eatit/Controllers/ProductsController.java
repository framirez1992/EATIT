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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import far.com.eatit.API.models.MeasureUnit;
import far.com.eatit.API.models.Product;
import far.com.eatit.Adapters.Models.NewOrderProductModel;
import far.com.eatit.Adapters.Models.ProductRowModel;
import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.Products;
import far.com.eatit.CloudFireStoreObjects.ProductsControl;
import far.com.eatit.CloudFireStoreObjects.ProductsMeasure;
import far.com.eatit.DataBase.CloudFireStoreDB;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Generic.Objects.KV2;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class ProductsController {

    public static final String TABLE_NAME ="PRODUCTS";
    public static  String IDPRODUCT ="idProduct", IDLICENSE="idLicense",IDPRODUCTTYPE="idProductType",IDPRODUCTSUBTYPE="idProductSubType",
            CODE = "code", DESCRIPTION = "description", COMBO = "combo",
            CREATEDATE = "createDate", CREATEUSER = "createUser", UPDATEDATE="updateDate", UPDATEUSER="updateUser",DELETEDATE="deleteDate",DELETEUSER="deleteUser" ;
    public static String[] columns = new String[]{IDPRODUCT,IDLICENSE,IDPRODUCTTYPE,IDPRODUCTSUBTYPE,CODE, DESCRIPTION, COMBO, CREATEDATE, CREATEUSER,UPDATEDATE, UPDATEUSER, DELETEDATE, DELETEUSER};

    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +IDPRODUCT+" INTEGER, "
            +IDLICENSE+" INTEGER, "
            +IDPRODUCTTYPE+" INTEGER, "
            +IDPRODUCTSUBTYPE+" INTEGER, "
            +CODE+" TEXT, "
            +DESCRIPTION+" TEXT, "
            +COMBO+" BOOLEAN, "
            +CREATEDATE+" TEXT, "
            +CREATEUSER+" TEXT, "
            +UPDATEDATE+" TEXT, "
            +UPDATEUSER+" TEXT, "
            +DELETEDATE+" TEXT, "
            +DELETEUSER+" TEXT "
            +")";
    Context context;
    DB db;
    static ProductsController instance;

    private ProductsController(Context c){
        this.context = c;
        this.db =DB.getInstance(c);
    }

    public static ProductsController getInstance(Context context){
        if(instance == null){
            instance = new ProductsController(context);
        }
        return instance;
    }

    public void insertOrUpdate(Product obj){
        String sql ="insert or replace into "+TABLE_NAME+" ("+IDPRODUCT+", "+IDLICENSE+", "+IDPRODUCTTYPE+", "+IDPRODUCTSUBTYPE+", "+CODE+", "+DESCRIPTION+", "+COMBO+", "+CREATEDATE+", "+CREATEUSER+","+UPDATEDATE+", "+UPDATEUSER+", "+DELETEDATE+", "+DELETEUSER+") values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        db.getWritableDatabase().execSQL(sql,new String[]{obj.getId()+"", obj.getIdLicense()+"", obj.getIdproductType()+"",obj.getIdproductSubType()+"",obj.getCode(), obj.getDescription(),obj.isCombo()?"1":"0" ,obj.getCreateDate(), obj.getCreateUser(),  obj.getUpdateDate(),obj.getUpdateUser(),  obj.getDeleteDate(), obj.getDeleteUser() });
    }


    public long insert(Product obj){
        ContentValues cv = new ContentValues();
        cv.put(IDPRODUCT,obj.getId() );
        cv.put(IDLICENSE,obj.getIdLicense());
        cv.put(IDPRODUCTTYPE,obj.getIdproductType());
        cv.put(IDPRODUCTSUBTYPE,obj.getIdproductSubType());
        cv.put(CODE,obj.getCode() );
        cv.put(DESCRIPTION,obj.getDescription());
        cv.put(COMBO,obj.isCombo());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Product obj){
        ContentValues cv = new ContentValues();
        cv.put(IDPRODUCT,obj.getId() );
        cv.put(IDLICENSE,obj.getIdLicense());
        cv.put(IDPRODUCTTYPE,obj.getIdproductType());
        cv.put(IDPRODUCTSUBTYPE,obj.getIdproductSubType());
        cv.put(CODE,obj.getCode() );
        cv.put(DESCRIPTION,obj.getDescription());
        cv.put(COMBO,obj.isCombo());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,IDPRODUCT.concat("= ?"), new String[]{obj.getId()+""});
        return result;
    }

    public long delete(Product obj){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,IDPRODUCT.concat("= ?"), new String[]{obj.getId()+""});
        return result;
    }

    public ArrayList<Product> getProducts(String where, String[]args, String orderBy){
        ArrayList<Product> result = new ArrayList<>();
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
            while(c.moveToNext()){
                result.add(new Product(c));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public Product getProductByCode(String code){
        String where = CODE+" = ?";
        ArrayList<Product> pts = getProducts(where, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }



    public ArrayList<ProductRowModel> getProductsPRM(String where, String[] args, String campoOrder){
        ArrayList<ProductRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = DESCRIPTION;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT p."+CODE+" as CODE, p."+DESCRIPTION+" AS DESCRIPTION, pt."+ProductsTypesController.CODE+" as PTCODE, pt."+ProductsTypesController.DESCRIPTION+" as PTDESCRIPTION, pst."+ProductsSubTypesController.CODE+" AS PSTCODE, pst."+ProductsSubTypesController.DESCRIPTION+" AS PSTDESCRIPTION, p."+UPDATEDATE +" AS MDATE " +
                    "FROM "+TABLE_NAME+" p " +
                    "LEFT JOIN "+ProductsTypesController.TABLE_NAME+" pt ON pt."+ProductsTypesController.CODE+" = p."+IDPRODUCTTYPE +" "+
                    "LEFT JOIN "+ProductsSubTypesController.TABLE_NAME+" pst ON pst."+ProductsSubTypesController.CODE+" = "+ IDPRODUCTSUBTYPE +" "+
                    where;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while(c.moveToNext()){
                result.add(new ProductRowModel(c.getString(c.getColumnIndex("CODE")), c.getString(c.getColumnIndex("DESCRIPTION")),c.getString(c.getColumnIndex("PTCODE")) ,c.getString(c.getColumnIndex("PTDESCRIPTION")),c.getString(c.getColumnIndex("PSTCODE")),c.getString(c.getColumnIndex("PSTDESCRIPTION")),c.getString(c.getColumnIndex("MDATE")) != null));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }

    public ArrayList<NewOrderProductModel> getNewProductRowModels(String where, String[] args, String campoOrder){
        ArrayList<NewOrderProductModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = DESCRIPTION;}
        where=((where != null)? "WHERE "+where:"");
        String data = "";
        try {

            String sql = "SELECT * FROM ("+
                    "SELECT toc."+TempOrdersController.DETAIL_CODE+" AS CODEORDERDETAIL, p."+CODE+" as CODE, p."+DESCRIPTION+" AS DESCRIPTION, ifnull(toc."+TempOrdersController.DETAIL_QUANTITY+", 0) AS QUANTITY, " +
                    //COLOCANDO UNA UNIDAD DE MEDIDA POR DEFECTO A CADA PRODUCTO QUE VENGA EN EL QUERY. SI NO ESTA GUARDADO EN LA TABLA TEMPORAL TOMARA UNA UNIDAD CUALQUIERA DE LAS QUE EL PRODUCTO YA TIENE REGISTRADA.
                    "ifnull(toc."+TempOrdersController.DETAIL_CODEUND+", pmc."+ProductsMeasureController.IDPRODUCTMEASURE +" ) as MEASURE," +
                    "ifnull(toc."+TempOrdersController.DETAIL_POSITION+", 0) as POSITION, pt."+ProductsTypesController.CODE+" as PTCODE, pt."+ProductsTypesController.DESCRIPTION+" as PTDESCRIPTION, " +
                    "pst."+ProductsSubTypesController.CODE+" AS PSTCODE, pst."+ProductsSubTypesController.DESCRIPTION+" AS PSTDESCRIPTION, p."+ UPDATEDATE +" AS MDATE, ifnull(pc."+ProductsControlController.BLOQUED+", 0) as BLOQUED " +
                    "FROM "+TABLE_NAME+" p " +
                    "INNER JOIN "+ProductsMeasureController.TABLE_NAME+" pmc on pmc."+ProductsMeasureController.IDPRODUCT +" = p."+ProductsController.CODE+" "+
                    "INNER JOIN "+ProductsTypesController.TABLE_NAME+" pt ON pt."+ProductsTypesController.CODE+" = p."+ IDPRODUCTTYPE +" "+
                    "INNER JOIN "+ProductsSubTypesController.TABLE_NAME+" pst ON pst."+ProductsSubTypesController.CODE+" = "+ IDPRODUCTSUBTYPE +" "+
                    "LEFT JOIN "+TempOrdersController.TABLE_NAME_DETAIL+" toc  on toc."+TempOrdersController.DETAIL_CODEPRODUCT+" = p."+CODE+" AND toc."+TempOrdersController.DETAIL_CODEUND+" = pmc."+ProductsMeasureController.IDPRODUCTMEASURE+" "+
                    "LEFT JOIN "+ProductsControlController.TABLE_NAME+" pc on pc."+ProductsControlController.CODEPRODUCT+" = p."+ProductsController.CODE+" "+
                     where+" " +
                    "ORDER BY toc."+TempOrdersController.DETAIL_POSITION+" ASC " +
                    ")"+
                    "GROUP BY  "+CODE+" "+
                    "ORDER BY  "+DESCRIPTION+" ASC";
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);

            while(c.moveToNext()){
                String codeOrderdetail = c.getString(c.getColumnIndex("CODEORDERDETAIL"));
                String code = c.getString(c.getColumnIndex("CODE"));
                String desc = c.getString(c.getColumnIndex("DESCRIPTION"));
                String qty = String.valueOf(c.getInt(c.getColumnIndex("QUANTITY")));
                String measure = c.getString(c.getColumnIndex("MEASURE"));
                String position =  c.getString(c.getColumnIndex("POSITION"));
                String blocked = c.getString(c.getColumnIndex("BLOQUED"));
                data+="DESC:"+desc+" MEASURE: "+measure+" ORDER:"+position+"\n";
                result.add(new NewOrderProductModel(codeOrderdetail,
                        code,
                        desc,
                        qty,
                        measure,
                        blocked,
                        ProductsMeasureController.getInstance(context).getProductsMeasureKVByCodeProduct(c.getInt(c.getColumnIndex("CODE")))));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }

}
