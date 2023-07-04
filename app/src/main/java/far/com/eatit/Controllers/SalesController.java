package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import java.util.ArrayList;
import java.util.Date;

import far.com.eatit.API.models.Company;
import far.com.eatit.API.models.Sale;
import far.com.eatit.API.models.SaleDetail;
import far.com.eatit.Adapters.Models.OrderDetailModel;
import far.com.eatit.Adapters.Models.OrderModel;
import far.com.eatit.Adapters.Models.OrderReceiptModel;
import far.com.eatit.Adapters.Models.ReceiptResumeModel;
import far.com.eatit.Adapters.Models.SelectableOrderRowModel;
import far.com.eatit.Adapters.Models.WorkedOrdersRowModel;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.Receipts;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.CloudFireStoreObjects.SalesDetails;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class SalesController {
    public static final String TABLE_NAME = Tablas.generalUsersSales;
    public static String IDSALE="idSale", IDUSER="idUser", IDDAY="idDay",IDTABLE="idTable",SUBTOTAL="subTotal",
            DISCOUNT="discount",TAXES="taxes",TOTAL="total", STATUS = "status",NOTES = "notes",
            CANCELREASON = "cancelReason", SALEINFOJSON="saleInfoJson",
            CREATEDATE = "createDate", CREATEUSER = "createUser", UPDATEDATE="updateDate", UPDATEUSER="updateUser",DELETEDATE="deleteDate",DELETEUSER="deleteUser" ;
    String[]columns = new String[]{IDSALE,IDUSER, IDDAY,IDTABLE, SUBTOTAL, DISCOUNT, TAXES, TOTAL, STATUS,NOTES, CANCELREASON, SALEINFOJSON, CREATEDATE, CREATEUSER,UPDATEDATE, UPDATEUSER, DELETEDATE, DELETEUSER};

    public static final String TABLE_NAME_DETAIL = Tablas.generalUsersSalesDetails;
    public static String IDSALEDETAIL = "idSaleDetail",DETAIL_IDSALE = "idSale", DETAIL_IDPRODUCT = "idProduct",DETAIL_IDPRODUCTMEASURE="idProductMeasure",
            DETAIL_QUANTITY = "quantity",DETAIL_PRICE = "price",DETAIL_DISCOUNT = "discount",DETAIL_TAX="tax", DETAIL_MANUALPRICE="manualPrice",DETAIL_POSITION = "position",
            DETAIL_CREATEDATE = "createDate", DETAIL_CREATEUSER = "createUser", DETAIL_UPDATEDATE="updateDate", DETAIL_UPDATEUSER="updateUser",DETAIL_DELETEDATE="deleteDate",DETAIL_DELETEUSER="deleteUser" ;
    String[]columnsDetails = new String[]{IDSALEDETAIL,DETAIL_IDSALE, DETAIL_IDPRODUCT,DETAIL_IDPRODUCTMEASURE,DETAIL_QUANTITY,DETAIL_PRICE,DETAIL_DISCOUNT,DETAIL_TAX,DETAIL_MANUALPRICE,DETAIL_POSITION,DETAIL_CREATEDATE, DETAIL_CREATEUSER, DETAIL_UPDATEDATE, DETAIL_UPDATEUSER,DETAIL_DELETEDATE,DETAIL_DELETEUSER};


    Context context;
    DB db;
    private static SalesController instance;

    private SalesController(Context c){
        this.context = c;
        this.db = DB.getInstance(c);
    }
    public static SalesController getInstance(Context c){
        if(instance == null){
           instance = new SalesController(c);
        }
        return instance;
    }

    public static String getQueryCreateHead(){
              String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
                      +IDSALE+" INTEGER,"
                      +IDUSER+" INTEGER,"
                      +IDDAY+" INTEGER,"
                      +IDTABLE+" TEXT,"
                      +SUBTOTAL+" DECIMAL(11, 3),"
                      +DISCOUNT+" DECIMAL(11, 3),"
                      +TAXES+" DECIMAL(11, 3), "
                      +TOTAL+" DECIMAL(11, 3),"
                      +STATUS+" TEXT, "
                      +NOTES+" TEXT, "
                      +CANCELREASON+" TEXT, "
                      +SALEINFOJSON+" TEXT,"
                      +CREATEDATE+" TEXT, "
                      +CREATEUSER+" TEXT, "
                      +UPDATEDATE+" TEXT, "
                      +UPDATEUSER+" TEXT, "
                      +DELETEDATE+" TEXT, "
                      +DELETEUSER+" TEXT "
                      +")";
              return QUERY_CREATE;
    }

    public static String getQueryCreateDetail(){
              String QUERY_CREATE_DETAIL = "CREATE TABLE "+TABLE_NAME_DETAIL+" ("
                      +IDSALEDETAIL+" INTEGER, "
                      +DETAIL_IDSALE+" INTEGER, "
                      +DETAIL_IDPRODUCT+" INTEGER, "
                      +DETAIL_IDPRODUCTMEASURE+" INTEGER, "
                      +DETAIL_QUANTITY+" DOUBLE, "
                      +DETAIL_PRICE+" DECIMAL(11, 3), "
                      +DETAIL_DISCOUNT+" DECIMAL(11,3), "
                      +DETAIL_TAX+" DECIMAL(11,3), "
                      +DETAIL_MANUALPRICE+" DECIMAL(11,3), "
                      +DETAIL_POSITION+" INTEGER, "
                      +DETAIL_CREATEDATE+" TEXT, "
                      +DETAIL_CREATEUSER+" TEXT, "
                      +DETAIL_UPDATEDATE+" TEXT, "
                      +DETAIL_UPDATEUSER+" TEXT, "
                      +DETAIL_DELETEDATE+" TEXT, "
                      +DETAIL_DELETEUSER+" TEXT "
                      +")";
              return QUERY_CREATE_DETAIL;

    }


    public void insertOrUpdate(Sale obj){
        String sql ="insert or replace into "+TABLE_NAME+" ("+IDSALE+", "+IDUSER+", "+IDDAY+", "+IDTABLE+","+SUBTOTAL+","+DISCOUNT+","+TAXES+","+TOTAL+","+STATUS+","+NOTES+","+CANCELREASON+","+SALEINFOJSON +", "+CREATEDATE+", "+CREATEUSER+","+UPDATEDATE+", "+UPDATEUSER+", "+DELETEDATE+", "+DELETEUSER+") values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        db.getWritableDatabase().execSQL(sql,new String[]{obj.getId()+"", obj.getIduser()+"", obj.getIdday()+"", obj.getIdTable(),obj.getSubTotal()+"",obj.getDiscount()+"",obj.getTaxes()+"",obj.getTotal()+"",obj.getStatus()+"",obj.getNotes(),obj.getCancelReason(),obj.getSaleInfoJson(), obj.getCreateDate(), obj.getCreateUser(),  obj.getUpdateDate(),obj.getUpdateUser(),  obj.getDeleteDate(), obj.getDeleteUser() });
    }

    public void insertOrUpdate(SaleDetail obj){
        String sql ="insert or replace into "+TABLE_NAME_DETAIL+" ("+IDSALEDETAIL+", "+DETAIL_IDSALE+", "+DETAIL_IDPRODUCT+", "+DETAIL_IDPRODUCTMEASURE+","+DETAIL_QUANTITY+","+DETAIL_PRICE+","+DETAIL_DISCOUNT+","+DETAIL_TAX+","+DETAIL_MANUALPRICE+","+DETAIL_POSITION+", "+DETAIL_CREATEDATE+", "+DETAIL_CREATEUSER+","+DETAIL_UPDATEDATE+", "+DETAIL_UPDATEUSER+", "+DETAIL_DELETEDATE+", "+DETAIL_DELETEUSER+") values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        db.getWritableDatabase().execSQL(sql,new String[]{obj.getId()+"", obj.getIdsale()+"", obj.getIdproduct()+"",obj.getIdproductMeasure()+"",obj.getQuantity()+"",obj.getPrice()+"",obj.getDiscount()+"",obj.getTax()+"",obj.getManualPrice()+"",obj.getPosition()+"", obj.getCreateDate(), obj.getCreateUser(),  obj.getUpdateDate(),obj.getUpdateUser(),  obj.getDeleteDate(), obj.getDeleteUser() });
    }

    public long insert(Sale obj){
        String table = TABLE_NAME;

        ContentValues cv = new ContentValues();
        cv.put(IDSALE,obj.getId());
        cv.put(IDUSER, obj.getIduser());
        cv.put(IDDAY, obj.getIdday());
        cv.put(SUBTOTAL, obj.getSubTotal());
        cv.put(DISCOUNT,obj.getDiscount());
        cv.put(TAXES,obj.getTaxes());
        cv.put(TOTAL,obj.getTotal());
        cv.put(STATUS, obj.getStatus());
        cv.put(NOTES, obj.getNotes());
        cv.put(CANCELREASON, obj.getCancelReason());
        cv.put(SALEINFOJSON, obj.getSaleInfoJson());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().insert(table,null,cv);
        return result;
    }


    public long update(Sale obj){
        ContentValues cv = new ContentValues();
        cv.put(IDSALE,obj.getId());
        cv.put(IDUSER, obj.getIduser());
        cv.put(IDDAY, obj.getIdday());
        cv.put(SUBTOTAL, obj.getSubTotal());
        cv.put(DISCOUNT,obj.getDiscount());
        cv.put(TAXES,obj.getTaxes());
        cv.put(TOTAL,obj.getTotal());
        cv.put(STATUS, obj.getStatus());
        cv.put(NOTES, obj.getNotes());
        cv.put(CANCELREASON, obj.getCancelReason());
        cv.put(SALEINFOJSON, obj.getSaleInfoJson());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        String table = TABLE_NAME;
        long result = DB.getInstance(context).getWritableDatabase().update(table,cv,IDSALE.concat(" = ? "), new String[]{obj.getId()+""} );
        return result;
    }

    public long delete(Sale obj){
        String table =TABLE_NAME;
        long result = DB.getInstance(context).getWritableDatabase().delete(table,IDSALE.concat(" = ? "), new String[]{obj.getId()+""});
        return result;
    }

    public void deleteHeadDetail(ArrayList<Sale> sales){
        for(Sale sale: sales){
            deleteHeadDetail(sale);
        }
    }
    public long deleteHeadDetail(Sale s){
        String where = IDSALE+" = ? ";
        String[]args = new String[]{s.getId()+""};
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        if(result >0){
           where = DETAIL_IDSALE+" = ? ";
           result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME_DETAIL,where, args);
        }
        return result;
    }


    public long insert_Detail(SaleDetail obj){
        ContentValues cv = new ContentValues();
        cv.put(IDSALEDETAIL,obj.getId());
        cv.put(DETAIL_IDSALE, obj.getIdsale());
        cv.put(DETAIL_IDPRODUCT,obj.getIdproduct());
        cv.put(DETAIL_IDPRODUCTMEASURE,obj.getIdproductMeasure());
        cv.put(DETAIL_QUANTITY,obj.getQuantity());
        cv.put(DETAIL_PRICE,obj.getPrice());
        cv.put(DETAIL_DISCOUNT,obj.getDiscount());
        cv.put(DETAIL_TAX,obj.getTax());
        cv.put(DETAIL_MANUALPRICE,obj.getManualPrice());
        cv.put(DETAIL_POSITION,obj.getPosition());
        cv.put(DETAIL_CREATEDATE, obj.getCreateDate());
        cv.put(DETAIL_CREATEUSER, obj.getCreateUser());
        cv.put(DETAIL_UPDATEDATE, obj.getCreateDate());
        cv.put(DETAIL_UPDATEUSER, obj.getUpdateUser());
        cv.put(DETAIL_DELETEDATE, obj.getCreateDate());
        cv.put(DETAIL_DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME_DETAIL,null,cv);
        return result;
    }



    public long update_Detail(SaleDetail obj){
        ContentValues cv = new ContentValues();
        cv.put(IDSALEDETAIL,obj.getId());
        cv.put(DETAIL_IDSALE, obj.getIdsale());
        cv.put(DETAIL_IDPRODUCT,obj.getIdproduct());
        cv.put(DETAIL_IDPRODUCTMEASURE,obj.getIdproductMeasure());
        cv.put(DETAIL_QUANTITY,obj.getQuantity());
        cv.put(DETAIL_PRICE,obj.getPrice());
        cv.put(DETAIL_DISCOUNT,obj.getDiscount());
        cv.put(DETAIL_TAX,obj.getTax());
        cv.put(DETAIL_MANUALPRICE,obj.getManualPrice());
        cv.put(DETAIL_POSITION,obj.getPosition());
        cv.put(DETAIL_CREATEDATE, obj.getCreateDate());
        cv.put(DETAIL_CREATEUSER, obj.getCreateUser());
        cv.put(DETAIL_UPDATEDATE, obj.getCreateDate());
        cv.put(DETAIL_UPDATEUSER, obj.getUpdateUser());
        cv.put(DETAIL_DELETEDATE, obj.getCreateDate());
        cv.put(DETAIL_DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME_DETAIL,cv,IDSALEDETAIL.concat("= ?"), new String[] {obj.getId()+""});
        return result;
    }


    public ArrayList<Sale> getSales(String where, String[] args){
        String tabla = TABLE_NAME;
       ArrayList<Sale> result = new ArrayList<>();

        Cursor c = DB.getInstance(context).getReadableDatabase().query(tabla,columns,where, args,null,null,null);
        while(c.moveToNext()){
            result.add(new Sale(c));
        }
        c.close();
        return result;
    }

    public ArrayList<SalesDetails> getSalesDetailsByCodeSales(int idSale){
        String where = DETAIL_IDSALE+" = ?";
        return getSalesDetails(where, new String[]{idSale+""});
    }
    public ArrayList<SalesDetails> getSalesDetails(String where, String[] args){
        String tabla = TABLE_NAME_DETAIL;
        ArrayList<SalesDetails> result = new ArrayList<>();
        Cursor c = DB.getInstance(context).getReadableDatabase().query(tabla,columnsDetails,where, args,null,null,null);
        while(c.moveToNext()){
            result.add(new SalesDetails(c));
        }
        c.close();
        return result;
    }

    public Sale getSaleById(int id){
        String table = TABLE_NAME;
        Sale s = null;
        String where = IDSALE+" = ?";
        String[]args = new String[]{id+""};
        Cursor c = DB.getInstance(context).getReadableDatabase().query(table,columns,where, args,null,null,null);
        if(c.moveToFirst()){
            s = new Sale(c);
        }
        c.close();
        return s;
    }


    public long delete_Detail(String where, String[] args){
        String table = TABLE_NAME_DETAIL;
        long result = DB.getInstance(context).getWritableDatabase().delete(table,where, args);
        return result;
    }




    public ArrayList<OrderModel> getOrderModels(String where){
        ArrayList<OrderModel> objects = new ArrayList<>();
        String sql = "Select DISTINCT "+IDSALE+","+STATUS+", "+NOTES+", "+DISCOUNT+", "+TOTAL+", " +CREATEDATE+", "+UPDATEDATE+" "+
                "FROM "+TABLE_NAME+" " +
                ((where == null)?"":"WHERE "+where+" ")
                +"ORDER BY "+UPDATEDATE+" DESC";
        Cursor c = db.getReadableDatabase().rawQuery(sql, null);
        while(c.moveToNext()){

            int idSale = c.getInt(c.getColumnIndex(IDSALE));
            String status = c.getString(c.getColumnIndex(STATUS));
            String notes = c.getString(c.getColumnIndex(NOTES));
            String lastUpdate = c.getString(c.getColumnIndex(UPDATEDATE));
            String date = c.getString(c.getColumnIndex(CREATEDATE));
            String minutos = (lastUpdate == null)?"NONE":String.valueOf(Funciones.calcularMinutos(new Date(), Funciones.parseStringToDate(date)))+" Mins";
            boolean edited = (lastUpdate != null && !(date.equals(lastUpdate)));
            OrderModel om = new OrderModel(idSale+"",status, notes,minutos,edited, getOrderDetailModels(idSale));
            objects.add(om);
        }
        return objects;
    }


    public ArrayList<WorkedOrdersRowModel> getWorkedOrderModels(String where){
        ArrayList<WorkedOrdersRowModel> objects = new ArrayList<>();
        try {
            String sql = "Select DISTINCT s." + IDSALE + " as CODE, ifnull(ifnull(s." + UPDATEDATE + ", s." + CREATEDATE + "), 'NONE') as FECHA,  " +
                    "ad." + TableController.IDTABLE + " as ADCODE, ad." + TableController.DESCRIPTION + " as ADDESCRIPTION, s." + STATUS + " as STATUS, " +
                    "s."+TOTAL+" as TOTAL,u."+UsersController.USERNAME+" as USERNAME  " +
                    "FROM " + TABLE_NAME + " s  " +
                    "INNER JOIN " + TableController.TABLE_NAME + " ad on ad." + TableController.IDTABLE + " = s." + IDTABLE + " " +
                    ((where == null) ? "" : "WHERE " + where + " ")
                    + "ORDER BY s." + UPDATEDATE + " DESC";
            Cursor c = db.getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {

                String code = c.getString(c.getColumnIndex("CODE"));
                String total = c.getString(c.getColumnIndex("TOTAL"));
                String userName = "";//c.getString(c.getColumnIndex("USERNAME"));
                String fecha = c.getString(c.getColumnIndex("FECHA"));
                String areaCode = "";//c.getString(c.getColumnIndex("ACODE"));
                String areaDescription = "";//c.getString(c.getColumnIndex("ADESCRIPTION"));
                String areaDetailCode = c.getString(c.getColumnIndex("ADCODE"));
                String areaDetailDescription = c.getString(c.getColumnIndex("ADDESCRIPTION"));
                int s = c.getInt(c.getColumnIndex("STATUS"));
                String status = "NONE";
                switch (s) {
                    case CODES.CODE_ORDER_STATUS_OPEN:
                        status = "Abierta";
                        break;
                    case CODES.CODE_ORDER_STATUS_CLOSED:
                        status = "Cerrada";
                        break;
                    case CODES.CODE_ORDER_STATUS_CANCELED:
                        status = "Cancelada";
                        break;
                    case CODES.CODE_ORDER_STATUS_READY:
                        status = "Lista";
                        break;
                    case CODES.CODE_ORDER_STATUS_DELIVERED:
                        status = "Entregada";
                        break;

                }

                WorkedOrdersRowModel om = new WorkedOrdersRowModel(code, fecha, areaCode, areaDescription, areaDetailCode, areaDetailDescription, status,userName,total);
                objects.add(om);
            }c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return objects;
    }
    public ArrayList<OrderDetailModel> getOrderDetailModels(int idSale){

        ArrayList<OrderDetailModel> objects = new ArrayList<>();
        try {
            String where = "sd."+DETAIL_IDSALE + " = '" + idSale +"'";

            String sql = "Select sd."+IDSALEDETAIL+" as CODE,sd."+DETAIL_IDSALE+" as CODESALES,p."+ProductsController.CODE+" AS CODEPRODUCT, " +
                    "p." + ProductsController.DESCRIPTION + " as PRODUCTO, sd." + DETAIL_QUANTITY + " as CANTIDAD, sd." + DETAIL_IDPRODUCTMEASURE + " as UNIDAD," +
                    "m."+MeasureUnitsController.CODE+" AS CODEMEDIDA,  m." + MeasureUnitsController.DESCRIPTION + " AS MEDIDA,  ifnull(pc."+ProductsControlController.BLOQUED+", '0') as BLOQUED, " +
                    "sd."+ DETAIL_CREATEDATE +" as DATE, sd."+ DETAIL_UPDATEDATE +" as MDATE "+
                    "FROM " + TABLE_NAME_DETAIL + " sd " +
                    "LEFT JOIN " + ProductsController.TABLE_NAME + " p on sd." + DETAIL_IDPRODUCT + " = p." + ProductsController.CODE + " " +
                    "LEFT JOIN " + MeasureUnitsController.TABLE_NAME + " m on m." + MeasureUnitsController.IDMEASUREUNIT + " = sd." + DETAIL_IDPRODUCTMEASURE + " " +//OJO ARREGLAR (NO VA ASI)
                    "LEFT JOIN "+ProductsControlController.TABLE_NAME+" pc on pc."+ProductsControlController.CODEPRODUCT+ " = p." + ProductsController.CODE + " " +
                    "WHERE " + where+" " +
                    "GROUP BY p."+ProductsController.CODE+", m."+MeasureUnitsController.CODE ;
            Cursor c = db.getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {

                OrderDetailModel om = new OrderDetailModel(
                        c.getString(c.getColumnIndex("CODEPRODUCT")),
                        c.getString(c.getColumnIndex("CODE")),
                        c.getString(c.getColumnIndex("CODESALES")),
                        c.getString(c.getColumnIndex("PRODUCTO")),
                        c.getString(c.getColumnIndex("CANTIDAD")),
                        c.getString(c.getColumnIndex("CODEMEDIDA")),
                        c.getString(c.getColumnIndex("MEDIDA")),
                        c.getString(c.getColumnIndex("BLOQUED")),
                        ProductsMeasureController.getInstance(context).getProductsMeasureKVByCodeProduct(c.getInt(c.getColumnIndex("CODEPRODUCT"))));
                objects.add(om);

            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return objects;
    }


    public ArrayList<SelectableOrderRowModel> getSelectableOrderModelsByAreaDetail(int tableId){
        ArrayList<SelectableOrderRowModel> data = new ArrayList<>();
        String sql = "Select DISTINCT s." + IDSALE + " as CODE, ifnull(ifnull(s." + UPDATEDATE  + ", s." + CREATEDATE + "), 'NONE') as FECHA,  " +
                "ad." + TableController.IDTABLE + " as ADCODE, ad." + TableController.DESCRIPTION + " as ADDESCRIPTION, s." + STATUS + " as STATUS," +
                "s."+TOTAL+" as TOTAL,u."+UsersController.CODE+" as CODEUSER,  u."+UsersController.USERNAME+" as USERNAME " +
                "FROM " + TABLE_NAME + " s  " +
                "INNER JOIN " + TableController.TABLE_NAME + " ad on ad." + TableController.IDTABLE + " = s." + IDTABLE + " " +
                "INNER JOIN "+UsersController.TABLE_NAME+" u on u."+UsersController.CODE+" = s."+IDUSER+" "+
                "WHERE ad."+TableController.IDTABLE+" = ? AND s."+STATUS+" IN(?, ?, ?) "+
                "ORDER BY ad." + TableController.DESCRIPTION;
        Cursor c = db.getReadableDatabase().rawQuery(sql, new String[]{tableId+"",CODES.CODE_ORDER_STATUS_OPEN+"", CODES.CODE_ORDER_STATUS_READY+"", CODES.CODE_ORDER_STATUS_DELIVERED+""});
        while (c.moveToNext()) {

            String code = c.getString(c.getColumnIndex("CODE"));
            String fecha = c.getString(c.getColumnIndex("FECHA"));
            String areaCode = "";//c.getString(c.getColumnIndex("ACODE"));
            String areaDescription = "";//c.getString(c.getColumnIndex("ADESCRIPTION"));
            String areaDetailCode = c.getString(c.getColumnIndex("ADCODE"));
            String areaDetailDescription = c.getString(c.getColumnIndex("ADDESCRIPTION"));
            String total = c.getString(c.getColumnIndex("TOTAL"));
            String codeUser = c.getString(c.getColumnIndex("CODEUSER"));
            String userName = c.getString(c.getColumnIndex("USERNAME"));
            int s = c.getInt(c.getColumnIndex("STATUS"));
            String status = "NONE";
            switch (s) {
                case CODES.CODE_ORDER_STATUS_OPEN:
                    status = "Abierta";
                    break;
                case CODES.CODE_ORDER_STATUS_CLOSED:
                    status = "Cerrada";
                    break;
                case CODES.CODE_ORDER_STATUS_CANCELED:
                    status = "Cancelada";
                    break;
                case CODES.CODE_ORDER_STATUS_READY:
                    status = "Lista";
                    break;
                case CODES.CODE_ORDER_STATUS_DELIVERED:
                    status = "Entregada";
                    break;

            }

            SelectableOrderRowModel om = new SelectableOrderRowModel(code, fecha, codeUser,userName,areaCode, areaDescription, areaDetailCode, areaDetailDescription, status,total,false);
            data.add(om);
        }c.close();
        return data;
    }








    public void fillSpinnerOrderStatus(Spinner spn, boolean addTodos){
        ArrayList<KV> data = new ArrayList<>();
        if(addTodos){
            KV obj = new KV("-1", "TODOS");
            data.add(obj);
        }
        data.add(new KV(CODES.CODE_ORDER_STATUS_OPEN+"","Abiertas") );
        data.add(new KV(CODES.CODE_ORDER_STATUS_CANCELED+"","Canceladas") );
        data.add(new KV(CODES.CODE_ORDER_STATUS_CLOSED+"","Cerradas") );
        data.add(new KV(CODES.CODE_ORDER_STATUS_DELIVERED+"","Entregadas") );
        data.add(new KV(CODES.CODE_ORDER_STATUS_READY+"","Lista") );


        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }




    public void fillSpnVista(Spinner spn){
        ArrayList<KV> spnList = new ArrayList<>();
        spnList.add(new KV("0", "Detalle")); spnList.add(new KV("1", "General"));
        spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,spnList));
    }

    public ArrayList<OrderReceiptModel> getOrderReceipt(int idTable){

        String status = CODES.CODE_ORDER_STATUS_DELIVERED+"";
        ArrayList<OrderReceiptModel> result = new ArrayList<>();
        String sql = "SELECT  ad."+TableController.IDTABLE+" AS CODEMESA, ad."+TableController.DESCRIPTION+" AS DESCRIPTIONMESA, " +
                "sum(s."+SalesController.TOTAL+") AS SALESTOTAL " +
                "FROM "+SalesController.TABLE_NAME+" s " +
                "INNER JOIN "+TableController.TABLE_NAME+" ad ON s."+IDTABLE+" = ad."+TableController.IDTABLE+" " +
                "WHERE s."+SalesController.STATUS+" = ? AND s."+SalesController.IDTABLE+" = ? " +
                "GROUP BY a."+AreasController.CODE+", ad."+TableController.IDTABLE;

        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{status, idTable+""});
        while (c.moveToNext()){
            result.add(new OrderReceiptModel(Funciones.generateCode(),
                    "",//c.getString(c.getColumnIndex("CODEAREA")),
                    "",//c.getString(c.getColumnIndex("DESCRIPTIONAREA")),
                    c.getString(c.getColumnIndex("CODEMESA")),
                    c.getString(c.getColumnIndex("DESCRIPTIONMESA")),
                    c.getDouble(c.getColumnIndex("SALESTOTAL"))));
        }c.close();

        return result;

    }

    public ArrayList<ReceiptResumeModel> getOrderReceiptResume(String codeAreaDetail){
        String status = CODES.CODE_ORDER_STATUS_DELIVERED+"";
        ArrayList<ReceiptResumeModel> result = new ArrayList<>();
        String sql = "SELECT sd."+SalesController.DETAIL_IDPRODUCT+" AS CODEPRODUCT, p."+ProductsController.DESCRIPTION+" AS  PRODUCTDESCRIPTION, " +
                "sd."+SalesController.DETAIL_IDPRODUCTMEASURE +" AS CODEMEASURE, mu."+MeasureUnitsController.DESCRIPTION+" AS MEASUREDESCRIPTION, "+
                "SUM(sd."+SalesController.DETAIL_QUANTITY+") AS QUANTITY, SUM(sd."+SalesController.DETAIL_PRICE+" * "+SalesController.DETAIL_QUANTITY+" ) AS SALESTOTAL "+
                "FROM "+SalesController.TABLE_NAME+" s " +
                "INNER JOIN "+SalesController.TABLE_NAME_DETAIL+" sd on s."+SalesController.IDSALE +" = sd."+SalesController.DETAIL_IDSALE+" "+
                "INNER JOIN "+ProductsController.TABLE_NAME+" p on p."+ProductsController.CODE+" = sd."+SalesController.DETAIL_IDPRODUCT+" "+
                "LEFT JOIN "+MeasureUnitsController.TABLE_NAME+" mu on mu."+MeasureUnitsController.CODE+" = sd."+SalesController.DETAIL_IDPRODUCTMEASURE+" "+
                "INNER JOIN "+TableController.TABLE_NAME+" ad ON s."+IDTABLE+" = ad."+TableController.IDTABLE+" " +
                "WHERE s."+SalesController.STATUS+" = ? AND s."+SalesController.IDTABLE+" = ? " +
                "GROUP BY sd."+SalesController.DETAIL_IDPRODUCT+",sd."+SalesController.DETAIL_IDPRODUCTMEASURE+" " +
                "ORDER BY p."+ProductsController.DESCRIPTION+",  mu."+MeasureUnitsController.DESCRIPTION;

        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{status, codeAreaDetail});
        while (c.moveToNext()){
            result.add(new ReceiptResumeModel( codeAreaDetail,
                    c.getString(c.getColumnIndex("CODEPRODUCT")),
                    c.getString(c.getColumnIndex("PRODUCTDESCRIPTION")),
                    c.getString(c.getColumnIndex("CODEMEASURE")),
                    c.getString(c.getColumnIndex("MEASUREDESCRIPTION")),
                    c.getString(c.getColumnIndex("QUANTITY")),
                    c.getString(c.getColumnIndex("SALESTOTAL"))));
        }c.close();

        return result;
    }

    public ArrayList<Sales> getDeliveredOrdersByCodeAreadetail(int idTable){
        ArrayList<Sales> deliveredOrders = new ArrayList<>();
        String where = SalesController.STATUS+" = ? AND "+SalesController.IDTABLE+" = ? ";
        Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns, where,new String[]{CODES.CODE_ORDER_STATUS_DELIVERED+"", idTable+""},null,null,null);
        while(c.moveToNext()){
            deliveredOrders.add(new Sales(c));
        }
        return deliveredOrders;
    }


    public Receipts getReceiptByCodeAreadetail(int idTable){
        Receipts receipt = null;
        String where = "s."+SalesController.STATUS+" = ? AND s."+SalesController.IDTABLE+" = ? ";
        String sql = "SELECT SUM(sd."+SalesController.DETAIL_PRICE+" * sd."+SalesController.DETAIL_QUANTITY+" ) AS TOTAL " +
                "FROM "+SalesController.TABLE_NAME+" s " +
                "INNER JOIN "+SalesController.TABLE_NAME_DETAIL+" sd on s."+SalesController.IDSALE+" = sd."+SalesController.DETAIL_IDSALE+" "+
                " WHERE "+where;
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql,new String[]{CODES.CODE_ORDER_STATUS_DELIVERED+"", idTable+""});
        if(c.moveToFirst()){
            double total = c.getDouble(0);
            receipt = new Receipts(Funciones.generateCode(),Funciones.getCodeuserLogged(context),idTable+"",null,null,total,0.0,0.0,total);
        }c.close();
        return receipt;
    }

    public void closeOrders(Receipts receipt, ArrayList<Sales> deliveredSales){
       /* if(deliveredSales != null && deliveredSales.size() > 0) {

            for(Sales sales : deliveredSales){
                sales.setSTATUS(CODES.CODE_ORDER_STATUS_CLOSED);
                sales.setCODERECEIPT(receipt.getCode());
                sales.setMDATE(null);//actualizar fecha de ultima actualizacion.

            }


            ///////////////////////////////////////////////////////////////////
            ///////////   ENVIANDO AL HISTORICO     ///////////////////////////

            SalesHistoryController.getInstance(context).sendToHistory(deliveredSales);
            ///////////////////////////////////////////////////////////////////

            ///////////////////////////////////////////////////////////////////
            //////      ELIMINANDO DE LA TABLA SALES Y SALES_DETAIL EN FIREBASE   ////////
            //massiveDelete(deliveredSales);
            //////////////////////////////////////////////////////////////////

            ///////////////////////////////////////////////////////////////////
            //////////  ELIMINANDOLA EN EL MOVIL   ///////////////////////////
            deleteHeadDetail(deliveredSales);//esto es porque la lista se actualizara antes de que el server retorne la actualizacion.
            //////////////////////////////////////////////////////////////////



        }*/
    }
/*
    public static String printReceipt(Context c, String codeAreaDetail, ArrayList<ReceiptResumeModel> list){
        Print p = new Print(c,Print.PULGADAS.PULGADAS_2);
        p.addAlign(Print.PRINTER_ALIGN.ALIGN_CENTER);
        p.addImage(R.drawable.gordi);
        p.drawText("Restaurant Los Gorditos");
        p.drawText("C: La gloria #14 El almirante");
        p.drawText("Tel:809-236-1503");
        p.drawText("");
        p.drawLine();
        p.drawText("Detalle");
        p.drawLine();

        p.addAlign(Print.PRINTER_ALIGN.ALIGN_LEFT);

        double total = 0.0;
        for(ReceiptResumeModel r :list){
            total+=Double.parseDouble(r.getTotal());
            p.drawText(r.getProductDescription());
            p.drawText(Funciones.reservarCaracteres("Cant:"+r.getQuantity(),9)+Funciones.reservarCaracteres(r.getMeasureDescription(),10)+Funciones.reservarCaracteresAlinearDerecha(" $"+Funciones.formatDecimal(r.getTotal()),13));
        }
        p.drawLine();

        p.addAlign(Print.PRINTER_ALIGN.ALIGN_RIGHT);
        p.drawText("Total:"+Funciones.formatDecimal(total), Print.TEXT_ALIGN.RIGHT);


        p.printText("02:3D:D3:DB:D5:06");
        return null;
    }*/



  public boolean orderContainsBlockedProduct(Sale s){
      boolean result;
      String sql = "SELECT sd."+IDSALE+" " +
              "FROM "+TABLE_NAME_DETAIL +" sd "+
              "INNER JOIN "+ProductsControlController.TABLE_NAME+" pc on pc."+ProductsControlController.CODEPRODUCT+" = sd."+DETAIL_IDPRODUCT+" " +
              "AND pc."+ProductsControlController.BLOQUED+" = '1' " +
              "WHERE sd."+DETAIL_IDSALE+" = ? ";
      Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{s.getId()+""});
      result = c.moveToFirst();
      c.close();
      return result;
  }

    public ArrayList<KV> getBloquedProductsInOrder(Sale s){
       ArrayList<KV> result = new ArrayList<>();
        String sql = "SELECT p."+ProductsController.CODE+", p."+ProductsController.DESCRIPTION+" " +
                "FROM "+TABLE_NAME_DETAIL +" sd " +
                "INNER JOIN "+ProductsControlController.TABLE_NAME+" pc on pc."+ProductsControlController.CODEPRODUCT+" = sd."+DETAIL_IDPRODUCT+" " +
                "AND pc."+ProductsControlController.BLOQUED+" = '1' " +
                "INNER JOIN " +ProductsController.TABLE_NAME+" p on p."+ProductsController.CODE+" = pc."+ProductsControlController.CODEPRODUCT+" "+
                "WHERE sd."+DETAIL_IDSALE+" = ? " +
                "GROUP BY p."+ProductsController.CODE+", p."+ProductsController.DESCRIPTION+" " +
                "ORDER BY p."+ProductsController.DESCRIPTION;

        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{s.getId()+""});
        while(c.moveToNext()){
            result.add(new KV(c.getString(0), c.getString(1)));
        }c.close();

        return result;
    }

}
