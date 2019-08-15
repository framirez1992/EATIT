package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import far.com.eatit.Adapters.Models.PercentRowModel;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.MeasureUnits;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.CloudFireStoreObjects.SalesDetails;
import far.com.eatit.CloudFireStoreObjects.SalesDetailsHistory;
import far.com.eatit.CloudFireStoreObjects.SalesHistory;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class SalesHistoryController {
    Context context;
    FirebaseFirestore db;
    DB sqlite;
    private static SalesHistoryController instance;

    public static String TABLE_NAME = Tablas.generalUsersSalesHistory;
    public static String CODE = "code",STATUS = "status",NOTES = "notes", DATE = "date",MDATE = "mdate", TOTAL="total",TOTALDISCOUNT = "totaldiscount",
            CODEUSER = "codeuser",USERNAME = "username",CODEAREADETAIL = "codeareadetail",AREADETAILDESCRIPTION = "areadetaildescription", CODEREASON = "codereason" , REASONDESCRIPTION = "reasondescription",
            CODEPRODUCTTYPE = "codeproducttype",PRODUCTTYPEDESCRIPTION = "producttypedescription", CODEPRODUCTSUBTYPE="codeproductsubtype",PRODUCTSUBTYPEDESCRIPTION = "productsubtypedescription",
            CODESALESORIGEN = "codesalesorigen", CODERECEIPT = "codereceipt" ;
    String[]columns = new String[]{CODE,STATUS, NOTES, DATE, MDATE, TOTAL, TOTALDISCOUNT, CODEUSER,USERNAME,CODEAREADETAIL,AREADETAILDESCRIPTION, CODEREASON, REASONDESCRIPTION, CODEPRODUCTTYPE,PRODUCTTYPEDESCRIPTION, CODEPRODUCTSUBTYPE,PRODUCTSUBTYPEDESCRIPTION, CODESALESORIGEN, CODERECEIPT};

    public static String TABLE_NAME_DETAIL = Tablas.generalUsersSalesDetailsHistory;
    public static String DETAIL_CODE = "code",DETAIL_CODESALES = "codesales", DETAIL_CODEPRODUCT = "codeproduct",DETAIL_PRODUCTDESCRIPTION="productdescription",
            DETAIL_DISCOUNT = "discount", DETAIL_POSITION = "position", DETAIL_PRICE = "price",
            DETAIL_QUANTITY = "quantity", DETAIL_UNIT = "unit", DETAIL_CODEUND = "codeund",DETAIL_UNDDESCRIPTION = "unddescription", DETAIL_DATE="date", DETAIL_MDATE="mdate";
    String[]columnsDetails = new String[]{DETAIL_CODE,DETAIL_CODESALES, DETAIL_CODEPRODUCT,DETAIL_PRODUCTDESCRIPTION,DETAIL_CODEUND,DETAIL_UNDDESCRIPTION,DETAIL_DISCOUNT,DETAIL_POSITION,DETAIL_QUANTITY,DETAIL_UNIT,DETAIL_PRICE, DATE, MDATE};

    public static String getQueryCreateHead(){
        String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
                +CODE+" TEXT,"+STATUS+" TEXT,"+NOTES+" TEXT, "+DATE+" TEXT,"+MDATE+" TEXT, "+TOTAL+" DECIMAL(11, 3), "+TOTALDISCOUNT+" DECIMAL(11, 3), " +
                CODEUSER+" TEXT,"+USERNAME+" TEXT, "+CODEAREADETAIL+" TEXT,"+AREADETAILDESCRIPTION+" TEXT, "+CODEREASON+" TEXT, "+REASONDESCRIPTION+" TEXT, "+CODEPRODUCTTYPE+" TEXT,"+PRODUCTTYPEDESCRIPTION+" TEXT, "+CODEPRODUCTSUBTYPE+" TEXT, "+PRODUCTSUBTYPEDESCRIPTION+" TEXT, " +
                CODESALESORIGEN+" TEXT,"+CODERECEIPT+" TEXT )";
        return QUERY_CREATE;
    }

    public static String getQueryCreateDetail(){
        String QUERY_CREATE_DETAIL = "CREATE TABLE "+TABLE_NAME_DETAIL+" ("
                +DETAIL_CODE+" TEXT, "+DETAIL_CODESALES+" TEXT, "+DETAIL_CODEPRODUCT+" TEXT,"+DETAIL_PRODUCTDESCRIPTION+" TEXT, "+DETAIL_DISCOUNT+" DECIMAL(11,3), "+DETAIL_POSITION+" INTEGER, "
                +DETAIL_PRICE+" DECIMAL(11, 3), "+DETAIL_QUANTITY+" DOUBLE, "+DETAIL_UNIT+" DOUBLE, "+DETAIL_CODEUND+" TEXT,"+DETAIL_UNDDESCRIPTION+" TEXT, " +
                DETAIL_DATE+" TEXT, "+DETAIL_MDATE+" TEXT)";
        return QUERY_CREATE_DETAIL;

    }

    private SalesHistoryController(Context c){
        this.context = c;
        this.db = FirebaseFirestore.getInstance();
        sqlite = DB.getInstance(c);
    }
    public static SalesHistoryController getInstance(Context c){
        if(instance == null){
            instance = new SalesHistoryController(c);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }

        CollectionReference reference = db.collection(Tablas.generalUsers)
                .document(l.getCODE()).collection(Tablas.generalUsersSalesHistory);
        return reference;
    }

    public long insert(SalesHistory s){
        String table = TABLE_NAME;

        ContentValues cv = new ContentValues();
        cv.put(CODE,s.getCODE());
        cv.put(STATUS, s.getSTATUS());
        cv.put(NOTES, s.getNOTES());
        cv.put(DATE, Funciones.getFormatedDate(s.getDATE()));
        cv.put(MDATE,Funciones.getFormatedDate(s.getMDATE()));
        cv.put(TOTAL,s.getTOTAL());
        cv.put(TOTALDISCOUNT,s.getTOTALDISCOUNT());
        cv.put(CODEUSER, s.getCODEUSER());
        cv.put(USERNAME, s.getUSERNAME());
        cv.put(CODEAREADETAIL, s.getCODEAREADETAIL());
        cv.put(AREADETAILDESCRIPTION, s.getAREADETAILDESCRIPTION());
        cv.put(CODEREASON, s.getCODEREASON());
        cv.put(REASONDESCRIPTION, s.getREASONDESCRIPTION());
        cv.put(CODEPRODUCTTYPE, s.getCODEPRODUCTTYPE());
        cv.put(PRODUCTTYPEDESCRIPTION, s.getPRODUCTTYPEDESCRIPTION());
        cv.put(CODEPRODUCTSUBTYPE, s.getCODEPRODUCTSUBTYPE());
        cv.put(PRODUCTSUBTYPEDESCRIPTION, s.getPRODUCTSUBTYPEDESCRIPTION());
        cv.put(CODESALESORIGEN, s.getCODEPRODUCTSUBTYPE());
        cv.put(CODERECEIPT, s.getCODERECEIPT());

        long result = DB.getInstance(context).getWritableDatabase().insert(table,null,cv);
        return result;
    }

    public long update(SalesHistory s){
        ContentValues cv = new ContentValues();
        cv.put(CODE,s.getCODE());
        cv.put(STATUS, s.getSTATUS());
        cv.put(NOTES, s.getNOTES());
        cv.put(DATE, Funciones.getFormatedDate(s.getDATE()));
        cv.put(MDATE,Funciones.getFormatedDate(s.getMDATE()));
        cv.put(TOTAL,s.getTOTAL());
        cv.put(TOTALDISCOUNT,s.getTOTALDISCOUNT());
        cv.put(CODEUSER, s.getCODEUSER());
        cv.put(USERNAME, s.getUSERNAME());
        cv.put(CODEAREADETAIL, s.getCODEAREADETAIL());
        cv.put(AREADETAILDESCRIPTION, s.getAREADETAILDESCRIPTION());
        cv.put(CODEREASON, s.getCODEREASON());
        cv.put(REASONDESCRIPTION, s.getREASONDESCRIPTION());
        cv.put(CODEPRODUCTTYPE, s.getCODEPRODUCTTYPE());
        cv.put(PRODUCTTYPEDESCRIPTION, s.getPRODUCTTYPEDESCRIPTION());
        cv.put(CODEPRODUCTSUBTYPE, s.getCODEPRODUCTSUBTYPE());
        cv.put(PRODUCTSUBTYPEDESCRIPTION, s.getPRODUCTSUBTYPEDESCRIPTION());
        cv.put(CODESALESORIGEN, s.getCODEPRODUCTSUBTYPE());
        cv.put(CODERECEIPT, s.getCODERECEIPT());

        String where = CODE+" = ? ";
        String table = TABLE_NAME;
        long result = DB.getInstance(context).getWritableDatabase().update(table,cv,where, new String[]{s.getCODE()} );
        return result;
    }

    public long insert_Detail(SalesDetailsHistory sd){
        String tabla = TABLE_NAME_DETAIL;
        ContentValues cv = new ContentValues();
        cv.put(DETAIL_CODE,sd.getCODE());
        cv.put(DETAIL_CODESALES, sd.getCODESALES());
        cv.put(DETAIL_CODEPRODUCT,sd.getCODEPRODUCT());
        cv.put(DETAIL_PRODUCTDESCRIPTION, sd.getPRODUCTDESCRIPTION());
        cv.put(DETAIL_DISCOUNT,sd.getDISCOUNT());
        cv.put(DETAIL_POSITION,sd.getPOSITION());
        cv.put(DETAIL_PRICE,sd.getPRICE());
        cv.put(DETAIL_QUANTITY,sd.getQUANTITY());
        cv.put(DETAIL_UNIT,sd.getUNIT());
        cv.put(DETAIL_CODEUND, sd.getCODEUND());
        cv.put(DETAIL_UNDDESCRIPTION, sd.getUNDDESCRIPTION());
        cv.put(DETAIL_DATE, Funciones.getFormatedDate(sd.getDATE()));
        cv.put(DETAIL_MDATE, Funciones.getFormatedDate(sd.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(tabla,null,cv);
        return result;
    }

    public void sendToHistory(ArrayList<Sales> sales){
        try {
            WriteBatch lote = db.batch();
            for(Sales s: sales){
                SalesHistory sh = castSaleToSaleHistory(s);
                sh.setDetails(getSalesDetailsByCodeSales(s.getCODE()));
                HashMap<String, Object> map =  sh.toMap();
                lote.set(getReferenceFireStore().document(s.getCODE()),map);
            }
            lote.commit().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getVentasReport(String dateIni, String dateEnd){
        HashMap<String, String> map = new HashMap<>();
        map.put("TO", "0");//TOTAL ORDERS
        map.put("TP", "0");//TOTAL GANADO
        map.put("MS", "");//MOST SALE (mas vendido)
        map.put("MSS", "");//MOST SALE SELLER
        try {
            String sql = "SELECT count(s." + CODE + "), sum(s." + TOTAL + ") " +
                    "FROM " + TABLE_NAME + " s "+
                    "WHERE s."+STATUS+" = '"+ CODES.CODE_ORDER_STATUS_CLOSED+"' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') ";
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                String totalOrdenes = c.getString(0);
                String totalMonto = c.getString(1);
                map.put("TO", totalOrdenes);
                map.put("TP", totalMonto);
            }
            c.close();

            sql = "SELECT sum(sd." + DETAIL_QUANTITY + "), p."+ProductsController.DESCRIPTION+" " +
                    "FROM " + TABLE_NAME_DETAIL + " sd " +
                    "INNER JOIN " + TABLE_NAME + " s on s." + CODE + " = sd." + DETAIL_CODESALES + "  AND s."+STATUS+" = '"+CODES.CODE_ORDER_STATUS_CLOSED+"' "+
                    "INNER JOIN "+ ProductsController.TABLE_NAME+" p on p."+ProductsController.CODE+" = sd."+DETAIL_CODEPRODUCT+" "+
                    "WHERE s."+STATUS+" = '"+CODES.CODE_ORDER_STATUS_CLOSED+"'  AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') "+
                    "GROUP BY sd." + DETAIL_CODEPRODUCT + ", p."+ProductsController.DESCRIPTION+" " +
                    "ORDER BY 1 DESC " +
                    "LIMIT 1 ";
            Cursor c2 = DB.getInstance(context).getReadableDatabase().rawQuery(sql,null);
            if (c2.moveToFirst()) {
                map.put("MS", c2.getString(1));
            }
            c2.close();

            sql = "SELECT count(s."+CODE+"), s."+CODEUSER+",u."+UsersController.USERNAME+" " +
                    "FROM "+TABLE_NAME+" s " +
                    "INNER JOIN "+UsersController.TABLE_NAME+" u on u."+UsersController.CODE+" = s."+CODEUSER+" " +
                    "WHERE s."+STATUS+" = '"+CODES.CODE_ORDER_STATUS_CLOSED+"' "+
                    "GROUP BY s."+CODEUSER+", u."+UsersController.USERNAME+" " +
                    "ORDER BY 1 DESC " +
                    "LIMIT 1";

            Cursor c3 = DB.getInstance(context).getReadableDatabase().rawQuery(sql,null);
            if (c3.moveToFirst()) {
                map.put("MSS", c3.getString(2));
            }
            c3.close();

       /* sql = "SELECT ";
        Cursor c3 =*/
        }catch(Exception e){
            e.printStackTrace();
        }
        return map;
    }

    public ArrayList<PercentRowModel> getVentasReportSellers(String status, String dateIni, String dateEnd){

        ArrayList<PercentRowModel> list = new ArrayList<>();
        int total = 0;
        try {
            String sql = "SELECT count(s." + CODE + ") " +
                    "FROM " + TABLE_NAME + " s "+
                    "WHERE s."+STATUS+" = '"+status+"' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') ";
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            if (c.moveToFirst()) {
                total = c.getInt(0);
            }c.close();


            sql = "SELECT count(s." + CODE + ") as cantidad, sum(s."+TOTAL+") as monto, s."+CODEUSER+",u."+UsersController.USERNAME+" as description, ut."+UserTypesController.DESCRIPTION+" as rol " +
                    ",( CAST(count(s." + CODE + ") AS REAL) * 100.0 /(" + total + ")) as percent   " +
                    "FROM " + TABLE_NAME + " s  " +
                    "INNER JOIN "+UsersController.TABLE_NAME+" u on u."+UsersController.CODE+" = s."+CODEUSER+" "+
                    "INNER JOIN "+UserTypesController.TABLE_NAME+" ut on ut."+UserTypesController.CODE+" = u."+UsersController.ROLE+" "+
                    "WHERE s."+STATUS+" = '"+status+"'  AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') "+
                    "GROUP BY s." + CODEUSER + ", u."+UsersController.USERNAME+", ut."+UserTypesController.DESCRIPTION+" " +
                    "ORDER BY 6 DESC, 5 DESC ";
            Cursor c2 = DB.getInstance(context).getReadableDatabase().rawQuery(sql,null);
            while (c2.moveToNext()) {
                list.add(new PercentRowModel(Math.round(c2.getDouble(c2.getColumnIndex("percent"))) + "%",
                        c2.getString(c2.getColumnIndex("description")),
                        c2.getString(c2.getColumnIndex("cantidad")),
                        c2.getString(c2.getColumnIndex("monto"))));
            }c2.close();

       /* sql = "SELECT ";
        Cursor c3 =*/
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public HashMap<String, String> getDevolucionesReport(String dateIni, String dateEnd){
        HashMap<String, String> map = new HashMap<>();
        map.put("TD", "0");//TOTAL DEVUELTO
        map.put("TP", "0");//TOTAL PERDIDA
        map.put("MF", "");//MOTIVO FRECUENTE
        map.put("MRS", "");//MOST RETURN SELLER
        try {
            String sql = "SELECT count(s." + CODE + "), sum(s." + TOTAL + ") " +
                    "FROM " + TABLE_NAME + " s "+
                    "WHERE s."+STATUS+" = '"+CODES.CODE_ORDER_STATUS_CANCELED+"' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') ";
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                String totalOrdenes = c.getString(0);
                String totalMonto = c.getString(1);
                map.put("TD", totalOrdenes);
                map.put("TP", totalMonto);
            }
            c.close();

            sql = "SELECT count(s." + CODE + "), s."+REASONDESCRIPTION+" " +
                    "FROM " + TABLE_NAME + " s  "+
                    "WHERE s."+STATUS+" = '"+CODES.CODE_ORDER_STATUS_CANCELED+"'  AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') "+
                    "GROUP BY s." + REASONDESCRIPTION +" " +
                    "ORDER BY 1 DESC " +
                    "LIMIT 1 ";
            Cursor c2 = DB.getInstance(context).getReadableDatabase().rawQuery(sql,null);
            if (c2.moveToFirst()) {
                map.put("MF", c2.getString(1));
            }
            c2.close();

            sql = "SELECT count(s."+CODE+"), s."+CODEUSER+",u."+UsersController.USERNAME+" " +
                    "FROM "+TABLE_NAME+" s " +
                    "INNER JOIN "+UsersController.TABLE_NAME+" u on u."+UsersController.CODE+" = s."+CODEUSER+" " +
                    "WHERE s."+STATUS+" = '"+CODES.CODE_ORDER_STATUS_CANCELED+"' "+
                    "GROUP BY s."+CODEUSER+", u."+UsersController.USERNAME+" " +
                    "ORDER BY 1 DESC " +
                    "LIMIT 1";

            Cursor c3 = DB.getInstance(context).getReadableDatabase().rawQuery(sql,null);
            if (c3.moveToFirst()) {
                map.put("MRS", c3.getString(2));
            }
            c3.close();

       /* sql = "SELECT ";
        Cursor c3 =*/
        }catch(Exception e){
            e.printStackTrace();
        }
        return map;
    }


    public ArrayList<PercentRowModel> getTopSalesProducts(String familia, String grupo, String dateIni, String dateEnd){
        ArrayList<PercentRowModel> list = new ArrayList<>();
        try {
            String wFamilia = (familia != null && !familia.equals("0"))?" AND p."+ ProductsController.TYPE+" = '"+familia+"' ":" ";
            String wGrupo = (grupo != null && !grupo.equals("0"))?" AND p."+ ProductsController.SUBTYPE+" = '"+grupo+"' ":" ";
            String where = " WHERE "+STATUS + " = '" + CODES.CODE_ORDER_STATUS_CLOSED + "' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  "+
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') ";//+
            //        wFamilia+
            //        wGrupo;
            String queryCount = "SELECT sum(sd." + DETAIL_QUANTITY + ") " +
                    "FROM " + TABLE_NAME_DETAIL + " sd " +
                    "INNER JOIN " + TABLE_NAME + " s on s." + CODE + " = sd." + DETAIL_CODESALES + "  " +
                    "INNER JOIN " + ProductsController.TABLE_NAME + " p on p." + ProductsController.CODE + " = sd." + DETAIL_CODEPRODUCT + " " +
                    where;

            Cursor cu = DB.getInstance(context).getReadableDatabase().rawQuery(queryCount, null);
            double totalCloseSales = 0.0;
            if(cu.moveToFirst()){
                totalCloseSales = cu.getDouble(0);
            }cu.close();

            String sql = "SELECT sum(sd." + DETAIL_QUANTITY + ") as cantidad, sum(ifnull(sd."+DETAIL_PRICE+", 0.0) * ifnull(sd."+DETAIL_QUANTITY+", 0.0)) as monto, p." + ProductsController.DESCRIPTION + " as description, ( CAST(sum(sd." + DETAIL_QUANTITY + ") AS REAL) * 100.0 /(" + totalCloseSales + ")) as percent," +
                    "p."+ProductsController.TYPE+" as "+ProductsController.TYPE+", p."+ProductsController.SUBTYPE+" as "+ProductsController.SUBTYPE+"  " +
                    "FROM " + TABLE_NAME_DETAIL + " sd " +
                    "INNER JOIN " + TABLE_NAME + " s on s." + CODE + " = sd." + DETAIL_CODESALES + "  AND s." + STATUS + " = '" + CODES.CODE_ORDER_STATUS_CLOSED + "' " +
                    "INNER JOIN " + ProductsController.TABLE_NAME + " p on p." + ProductsController.CODE + " = sd." + DETAIL_CODEPRODUCT + " " +
                    "INNER JOIN "+ ProductsTypesController.TABLE_NAME+" pt on pt."+ProductsTypesController.CODE+" = p."+ProductsController.TYPE+" "+
                    "WHERE s." + STATUS + " = '" + CODES.CODE_ORDER_STATUS_CLOSED + "' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') "+
                    wFamilia+
                    wGrupo+
                    "GROUP BY pt." + ProductsTypesController.DESCRIPTION + ", sd." + DETAIL_CODEPRODUCT + ", p." + ProductsController.DESCRIPTION + " " +
                    "HAVING  ( CAST(count(sd." + DETAIL_CODEPRODUCT + ") AS REAL) * 100.0 /(" + totalCloseSales + ")) > 0 ";

            if((familia != null && !familia.equals("0"))){
                sql = "SELECT * FROM ("+sql+") as tabla WHERE "+ProductsController.TYPE+" = '"+familia+"' ";
                if(grupo != null && !grupo.equals("0")){
                    sql+= " AND "+ProductsController.SUBTYPE+" = '"+grupo+"' ";
                }
            }
            sql+= "ORDER BY 4 DESC, 3 DESC ";//porcentaje, descripcion;

            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                list.add(new PercentRowModel(Math.round(c.getDouble(c.getColumnIndex("percent"))) + "%",
                        c.getString(c.getColumnIndex("description")),
                        c.getString(c.getColumnIndex("cantidad")),
                        c.getString(c.getColumnIndex("monto"))));
            }c.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

    public ArrayList<PercentRowModel> getTopSalesGeneraldata(String familia,String dateIni, String dateEnd){
        ArrayList<PercentRowModel> list = new ArrayList<>();
        try {
            String wFamilia = (familia.equals("1"))?" AND p."+ ProductsController.TYPE+" = '"+familia+"' ":" ";
            String where = " WHERE "+STATUS + " = '" + CODES.CODE_ORDER_STATUS_CLOSED + "' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  "+
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') "+
                    wFamilia;

            String queryCount = "SELECT sum(sd." + DETAIL_QUANTITY + ") " +
                    "FROM " + TABLE_NAME_DETAIL + " sd " +
                    "INNER JOIN " + TABLE_NAME + " s on s." + CODE + " = sd." + DETAIL_CODESALES + "  " +
                    "INNER JOIN " + ProductsController.TABLE_NAME + " p on p." + ProductsController.CODE + " = sd." + DETAIL_CODEPRODUCT + " " +
                    where;

            Cursor cu = DB.getInstance(context).getReadableDatabase().rawQuery(queryCount, null);
            double totalCloseSales = 0.0;
            if(cu.moveToFirst()){
                totalCloseSales = cu.getDouble(0);
            }cu.close();


            String sql = "SELECT sum(sd."+DETAIL_QUANTITY+") AS cantidad, sum(ifnull(sd."+DETAIL_PRICE+", 0.0)) as monto,  pt."+ProductsTypesController.DESCRIPTION+" as description, ( CAST(sum(sd."+DETAIL_QUANTITY+") AS REAL) * 100.0 /(" + totalCloseSales + ")) as percent," +
                    "p."+ProductsController.TYPE+" as "+ProductsController.TYPE+", p."+ProductsController.SUBTYPE+" as "+ProductsController.SUBTYPE+" " +
                    "FROM " + TABLE_NAME_DETAIL + " sd " +
                    "INNER JOIN " + TABLE_NAME + " s on s." + CODE + " = sd." + DETAIL_CODESALES + "  " +
                    "INNER JOIN " + ProductsController.TABLE_NAME + " p on p." + ProductsController.CODE + " = sd." + DETAIL_CODEPRODUCT + " " +
                    "INNER JOIN "+ ProductsTypesController.TABLE_NAME+" pt on pt."+ProductsTypesController.CODE+" = p."+ProductsController.TYPE+" "+
                    "WHERE s." + STATUS + " = '" + CODES.CODE_ORDER_STATUS_CLOSED + "' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') "+
                    wFamilia+
                    "GROUP BY p." + ProductsController.TYPE + " " ;
            //"HAVING  ( CAST(count(sd." + DETAIL_CODEPRODUCT + ") AS REAL) * 100.0 /(" + totalCloseSales + ")) > 0 "+


            if(!familia.equals("0")){
                sql = "SELECT * FROM ("+sql+") as tabla where "+ProductsController.TYPE+" = '"+familia+"' ";
            }

            sql +=  "ORDER BY 4 DESC, 3 DESC ";// descripcion;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                list.add(new PercentRowModel(Math.round(c.getDouble(c.getColumnIndex("percent"))) + "%",
                        c.getString(c.getColumnIndex("description")),
                        c.getString(c.getColumnIndex("cantidad")),
                        c.getString(c.getColumnIndex("monto"))));
            }c.close();

        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }


    public ArrayList<PercentRowModel> getTopDevolucionesGeneraldata(String dateIni, String dateEnd){
        ArrayList<PercentRowModel> list = new ArrayList<>();
        try {
            String where = " WHERE s."+STATUS + " = '" + CODES.CODE_ORDER_STATUS_CANCELED + "' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  "+
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') ";

            String queryCount = "SELECT count(s." + CODE + ") " +
                    "FROM " + TABLE_NAME + " s " +
                    where;

            Cursor cu = DB.getInstance(context).getReadableDatabase().rawQuery(queryCount, null);
            double totalSales = 0.0;
            if(cu.moveToFirst()){
                totalSales = cu.getDouble(0);
            }cu.close();

            String sql = "SELECT count(s."+CODE+") AS cantidad, sum(ifnull(sd."+DETAIL_PRICE+", 0.0)) as monto,s."+CODEREASON+",  s."+REASONDESCRIPTION+" as description, ( CAST(count(s."+CODE+") AS REAL) * 100.0 /(" + totalSales + ")) as percent " +
                    "FROM " + TABLE_NAME_DETAIL + " sd " +
                    "INNER JOIN " + TABLE_NAME + " s on s." + CODE + " = sd." + DETAIL_CODESALES + "  " +
                    "WHERE s." + STATUS + " = '" + CODES.CODE_ORDER_STATUS_CANCELED + "' " +
                    "AND julianday(substr(s."+DATE+",1,4)||'-'||substr(s."+DATE+",5,2)||'-'||substr(s."+DATE+",7,2)||' '||substr(s."+DATE+",10,length(s."+DATE+")))  " +
                    "BETWEEN julianday('"+dateIni+"') AND julianday('"+dateEnd+"') "+
                    "GROUP BY s." + CODEREASON + " " ;


            sql +=  "ORDER BY 5 DESC, 4 DESC ";// descripcion;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                list.add(new PercentRowModel(Math.round(c.getDouble(c.getColumnIndex("percent"))) + "%",
                        c.getString(c.getColumnIndex("description")),
                        c.getString(c.getColumnIndex("cantidad")),
                        c.getString(c.getColumnIndex("monto"))));
            }c.close();

        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }



    public void getHistoricDataToSearch(int status, Date dateIni, Date dateEnd, OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failute){
        if(status == -1){
            getReferenceFireStore().
                    whereGreaterThan(MDATE, dateIni).//mayor que, ya que las fechas (la que buscamos de la DB) tienen hora, minuto y segundos.
                    whereLessThanOrEqualTo(MDATE, dateEnd).get().
                    addOnSuccessListener(success).addOnCompleteListener(complete).
                    addOnFailureListener(failute);
        }else {
            getReferenceFireStore().whereEqualTo(STATUS, status).
                    whereGreaterThan(MDATE, dateIni).//mayor que, ya que las fechas (la que buscamos de la DB) tienen hora, minuto y segundos.
                    whereLessThanOrEqualTo(MDATE, dateEnd).get().
                    addOnSuccessListener(success).addOnCompleteListener(complete).
                    addOnFailureListener(failute);
        }

    }


    public void proccessQuerySnapshotHistoricData(QuerySnapshot querySnapshot){
        try {
            if (!querySnapshot.isEmpty()) {

                for (DocumentSnapshot doc : querySnapshot) {
                    SalesHistory s = doc.toObject(SalesHistory.class);
                    if (update(s) <= 0){
                        insert(s);
                        List<Map<String, Object>> x = (List<Map<String, Object>>) doc.getData().get("salesdetails");
                        for (Map<String, Object> m : x) {
                            insert_Detail(new SalesDetailsHistory(m));
                        }
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Date getLastDateSavedHistory(int status){
        Date date = null;
        String sql = "SELECT "+DATE+" as DATE " +
                "FROM "+TABLE_NAME+" " +
                "WHERE "+STATUS+" = '"+status+"' "+
                "ORDER BY "+DATE+" DESC " +
                "LIMIT 1 ";
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        if(c.moveToFirst()){
            date = Funciones.parseStringToDate(c.getString(c.getColumnIndex("DATE")));
        }c.close();
        return date;
    }

    /**
     * obtiene la fecha mas baja guardada en la base de datos local en la tabla de historico.
     * @param status
     * @return
     */
    public Date getLastInitialDateSavedHistory(int status){
        Date date = null;
        String sql = "SELECT "+DATE+" as DATE " +
                "FROM "+TABLE_NAME+" " +
                "WHERE "+STATUS+" = '"+status+"' "+
                "ORDER BY "+DATE+" ASC " +
                "LIMIT 1 ";
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        if(c.moveToFirst()){
            date = Funciones.parseStringToDate(c.getString(c.getColumnIndex("DATE")));
        }c.close();
        return date;
    }

    public double getCountHistory(String where){
        double result = 0.0;
        String sql = "SELECT COUNT(*) " +
                "FROM "+TABLE_NAME;
        sql+=(where != null)?" WHERE "+where: "";
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        if(c.moveToFirst()){
            result = c.getDouble(0);
        }c.close();
        return result;
    }

    public SalesHistory castSaleToSaleHistory(Sales s){

        SalesHistory sh = new SalesHistory();
        sh.setCODE(s.getCODE());
        sh.setNOTES(s.getNOTES());
        sh.setCODEUSER(s.getCODEUSER());
        sh.setCODEAREADETAIL(s.getCODEAREADETAIL());
        sh.setCODEREASON(s.getCODEREASON());
        sh.setREASONDESCRIPTION(s.getREASONDESCRIPTION());
        sh.setCODEPRODUCTTYPE(s.getCODEPRODUCTTYPE());
        sh.setCODEPRODUCTSUBTYPE(s.getCODEPRODUCTSUBTYPE());
        sh.setCODESALESORIGEN(s.getCODESALESORIGEN());
        sh.setCODERECEIPT(s.getCODERECEIPT());
        sh.setSTATUS(s.getSTATUS());
        sh.setTOTALDISCOUNT(s.getTOTALDISCOUNT());
        sh.setTOTAL(s.getTOTAL());
        sh.setDATE(s.getDATE());
        sh.setMDATE(s.getMDATE());

        String sql = "SELECT u."+UsersController.USERNAME+" as USERNAME, ad."+AreasDetailController.DESCRIPTION+" as AREADESCRIPTION, " +
                "pt."+ProductsTypesController.DESCRIPTION+" as PTDESCRIPTION, pst."+ProductsSubTypesController.DESCRIPTION+" as PSTDESCRIPTION " +
                "FROM "+SalesController.TABLE_NAME+" s " +
                "INNER JOIN "+UsersController.TABLE_NAME+" u on u."+UsersController.CODE+" = s."+SalesController.CODEUSER+" " +
                "INNER JOIN "+AreasDetailController.TABLE_NAME+" ad on ad."+AreasDetailController.CODE+" = s."+SalesController.CODEAREADETAIL+" " +
                "LEFT JOIN "+ProductsTypesController.TABLE_NAME+" pt on pt."+ProductsTypesController.CODE+" = s."+SalesController.CODEPRODUCTTYPE+" " +
                "LEFT JOIN "+ProductsSubTypesController.TABLE_NAME+" pst on pst."+ProductsSubTypesController.CODE+" = s."+SalesController.CODEPRODUCTSUBTYPE+" " +
                "WHERE s."+SalesController.CODE+" = ? ";
        Cursor c = sqlite.getReadableDatabase().rawQuery(sql, new String[]{s.getCODE()});
        if(c.moveToFirst()){
            sh.setUSERNAME(c.getString(c.getColumnIndex("USERNAME")));
            sh.setAREADETAILDESCRIPTION(c.getString(c.getColumnIndex("AREADESCRIPTION")));
            sh.setPRODUCTTYPEDESCRIPTION(c.getString(c.getColumnIndex("PTDESCRIPTION")));
            sh.setPRODUCTSUBTYPEDESCRIPTION(c.getString(c.getColumnIndex("PSTDESCRIPTION")));
        }c.close();

        return sh;
    }


    public ArrayList<SalesDetailsHistory> getSalesDetailsByCodeSales(String codeSale){
        ArrayList<SalesDetailsHistory> result = new ArrayList<>();
        String sql ="SELECT sd."+DETAIL_CODE+" as "+DETAIL_CODE+", sd."+DETAIL_CODESALES+" as "+DETAIL_CODESALES+", sd."+DETAIL_CODEPRODUCT+" as "+DETAIL_CODEPRODUCT+", p."+ProductsController.DESCRIPTION+" as "+DETAIL_PRODUCTDESCRIPTION+", " +
                "sd."+DETAIL_CODEUND+" as "+DETAIL_CODEUND+", mu."+MeasureUnitsController.DESCRIPTION+" as "+DETAIL_UNDDESCRIPTION+", " +
                "sd."+DETAIL_DISCOUNT+" as "+DETAIL_DISCOUNT+",sd."+DETAIL_POSITION+" as "+DETAIL_POSITION+", sd."+DETAIL_QUANTITY+" as "+DETAIL_QUANTITY+", " +
                "sd."+DETAIL_UNIT+" as "+DETAIL_UNIT+", sd."+DETAIL_PRICE+" as "+DETAIL_PRICE+", sd."+DATE+" as "+DATE+", sd."+MDATE+" as "+MDATE+" " +
                "FROM "+SalesController.TABLE_NAME_DETAIL+" sd " +
                "INNER JOIN "+ProductsController.TABLE_NAME+" p on p."+ProductsController.CODE+" = sd."+DETAIL_CODEPRODUCT+" " +
                "INNER JOIN "+ MeasureUnitsController.TABLE_NAME+" mu on mu."+MeasureUnitsController.CODE+" = sd."+DETAIL_CODEUND+" " +
                "WHERE sd."+DETAIL_CODESALES+" = ?";
        Cursor c = sqlite.getReadableDatabase().rawQuery(sql, new String[]{codeSale});
        while(c.moveToNext()){
        result.add(new SalesDetailsHistory(c));
        }
        return result;
    }

}
