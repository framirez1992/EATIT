package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import far.com.eatit.Adapters.Models.OrderDetailModel;
import far.com.eatit.Adapters.Models.OrderModel;
import far.com.eatit.CloudFireStoreObjects.ProductsControl;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.CloudFireStoreObjects.SalesDetails;
import far.com.eatit.CloudFireStoreObjects.UserControl;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class TempOrdersController{
        public static String TABLE_NAME = Tablas.tempOrders;
    String[]columns = new String[]{CODE,STATUS, NOTES, DATE, MDATE, TOTAL, TOTALDISCOUNT, CODEUSER,CODEAREADETAIL, CODEREASON, REASONDESCRIPTION, CODEPRODUCTTYPE, CODEPRODUCTSUBTYPE, CODESALESORIGEN, CODERECEIPT};

    public static String CODE = "code",STATUS = "status",NOTES = "notes", DATE = "date",MDATE = "mdate", TOTAL="total",TOTALDISCOUNT = "totaldiscount",
            CODEUSER = "codeuser",CODEAREADETAIL = "codeareadetail", CODEREASON = "codereason" , REASONDESCRIPTION = "reasondescription",
            CODEPRODUCTTYPE = "codeproducttype", CODEPRODUCTSUBTYPE="codeproductsubtype", CODESALESORIGEN = "codesalesorigen", CODERECEIPT = "codereceipt" ;
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT,"+STATUS+" TEXT,"+NOTES+" TEXT, "+DATE+" TEXT,"+MDATE+" TEXT, "+TOTAL+" DECIMAL(11, 3), "+TOTALDISCOUNT+" DECIMAL(11, 3), " +
            CODEUSER+" TEXT,"+CODEAREADETAIL+" TEXT, "+CODEREASON+" TEXT, "+REASONDESCRIPTION+" TEXT, "+CODEPRODUCTTYPE+" TEXT, "+CODEPRODUCTSUBTYPE+" TEXT, " +
            CODESALESORIGEN+" TEXT, "+CODERECEIPT+" TEXT)";

    public static String TABLE_NAME_DETAIL = Tablas.tempOrdersDetails;
    String[]columnsDetails = new String[]{DETAIL_CODE,DETAIL_CODESALES, DETAIL_CODEPRODUCT,DETAIL_CODEUND,DETAIL_DISCOUNT,DETAIL_POSITION,DETAIL_QUANTITY,DETAIL_UNIT,DETAIL_PRICE, DATE, MDATE};

    public static String DETAIL_CODE = "code",DETAIL_CODESALES = "codesales", DETAIL_CODEPRODUCT = "codeproduct",
                DETAIL_DISCOUNT = "discount", DETAIL_POSITION = "position", DETAIL_PRICE = "price",
                DETAIL_QUANTITY = "quantity", DETAIL_UNIT = "unit", DETAIL_CODEUND = "codeund", DETAIL_DATE="date", DETAIL_MDATE="mdate";
        public static String QUERY_CREATE_DETAIL = "CREATE TABLE "+TABLE_NAME_DETAIL+" ("
                +DETAIL_CODE+" TEXT,"+DETAIL_CODESALES+" TEXT, "+DETAIL_CODEPRODUCT+" TEXT, "+DETAIL_DISCOUNT+" DECIMAL(11,3), "+DETAIL_POSITION+" INTEGER, "
                +DETAIL_PRICE+" DECIMAL(11, 3), "+DETAIL_QUANTITY+" DOUBLE, "+DETAIL_UNIT+" DOUBLE, "+DETAIL_CODEUND+" TEXT, " +
                DETAIL_DATE+" TEXT, "+DETAIL_MDATE+" TEXT)";

        Context context;
        DB sqlite;
        private static TempOrdersController instance;
        private TempOrdersController(Context c){
            this.context = c;
            sqlite = DB.getInstance(c);
        }
        public static TempOrdersController getInstance(Context c){
            if(instance == null){
                instance = new TempOrdersController(c);
            }
            return instance;
        }

        public long insert(Sales s){
            ContentValues cv = new ContentValues();
            cv.put(CODE,s.getCODE());
            cv.put(STATUS, s.getSTATUS());
            cv.put(NOTES, s.getNOTES());
            cv.put(DATE, Funciones.getFormatedDate(s.getDATE()));
            cv.put(MDATE,Funciones.getFormatedDate(s.getMDATE()));
            cv.put(TOTAL,s.getTOTAL());
            cv.put(TOTALDISCOUNT,s.getTOTALDISCOUNT());
            cv.put(CODEUSER, s.getCODEUSER());
            cv.put(CODEAREADETAIL, s.getCODEAREADETAIL());
            cv.put(CODEREASON, s.getCODEREASON());
            cv.put(REASONDESCRIPTION, s.getREASONDESCRIPTION());
            cv.put(CODEPRODUCTTYPE, s.getCODEPRODUCTTYPE());
            cv.put(CODEPRODUCTSUBTYPE, s.getCODEPRODUCTSUBTYPE());
            cv.put(CODESALESORIGEN, s.getCODEPRODUCTSUBTYPE());
            cv.put(CODERECEIPT, s.getCODERECEIPT());

            long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
            return result;
        }

        public long update(Sales s, String where, String[]args){
            ContentValues cv = new ContentValues();
            cv.put(CODE,s.getCODE());
            cv.put(STATUS, s.getSTATUS());
            cv.put(NOTES, s.getNOTES());
            cv.put(DATE, Funciones.getFormatedDate(s.getDATE()));
            cv.put(MDATE,Funciones.getFormatedDate(s.getMDATE()));
            cv.put(TOTAL,s.getTOTAL());
            cv.put(TOTALDISCOUNT,s.getTOTALDISCOUNT());
            cv.put(CODEUSER, s.getCODEUSER());
            cv.put(CODEAREADETAIL, s.getCODEAREADETAIL());
            cv.put(CODEREASON, s.getCODEREASON());
            cv.put(REASONDESCRIPTION, s.getREASONDESCRIPTION());
            cv.put(CODEPRODUCTTYPE, s.getCODEPRODUCTTYPE());
            cv.put(CODEPRODUCTSUBTYPE, s.getCODEPRODUCTSUBTYPE());
            cv.put(CODESALESORIGEN, s.getCODESALESORIGEN());
            cv.put(CODERECEIPT, s.getCODERECEIPT());

            long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args );
            return result;
        }

        public long delete(String where, String[] args){
            long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
            return result;
        }


        public long insert_Detail(SalesDetails sd){
            ContentValues cv = new ContentValues();
            cv.put(DETAIL_CODE,sd.getCODE());
            cv.put(DETAIL_CODESALES, sd.getCODESALES());
            cv.put(DETAIL_CODEPRODUCT,sd.getCODEPRODUCT());
            cv.put(DETAIL_DISCOUNT,sd.getDISCOUNT());
            cv.put(DETAIL_POSITION,sd.getPOSITION());
            cv.put(DETAIL_PRICE,sd.getPRICE());
            cv.put(DETAIL_QUANTITY,sd.getQUANTITY());
            cv.put(DETAIL_UNIT,sd.getUNIT());
            cv.put(DETAIL_CODEUND, sd.getCODEUND());
            cv.put(DETAIL_DATE, Funciones.getFormatedDate(sd.getDATE()));
            cv.put(DETAIL_MDATE, Funciones.getFormatedDate(sd.getMDATE()));

            long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME_DETAIL,null,cv);
            return result;
        }

        public long update_Detail(SalesDetails sd){
            ContentValues cv = new ContentValues();
            cv.put(DETAIL_CODE,sd.getCODE());
            cv.put(DETAIL_CODESALES, sd.getCODESALES());
            cv.put(DETAIL_CODEPRODUCT,sd.getCODEPRODUCT());
            cv.put(DETAIL_DISCOUNT,sd.getDISCOUNT());
            cv.put(DETAIL_POSITION,sd.getPOSITION());
            cv.put(DETAIL_PRICE,sd.getPRICE());
            cv.put(DETAIL_QUANTITY,sd.getQUANTITY());
            cv.put(DETAIL_UNIT,sd.getUNIT());
            cv.put(DETAIL_CODEUND, sd.getCODEUND());
            cv.put(DETAIL_MDATE, Funciones.getFormatedDate(sd.getMDATE()));

            String where = DETAIL_CODE+"= ? AND "+DETAIL_CODEPRODUCT+"= ? ";

            long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME_DETAIL,cv,where, new String[] {sd.getCODE(), sd.getCODEPRODUCT()});
            return result;
        }

        public long delete_Detail(String where, String[] args){
            long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME_DETAIL,where, args);
            return result;
        }


      /*  public ArrayList<OrderModel> getOrderModels(String where){
            ArrayList<OrderModel> objects = new ArrayList<>();
            String sql = "Select "+CODE+", "+TOTALDISCOUNT+", "+TOTAL+", " +DATE+", "+MDATE+" "+
                    "FROM "+TABLE_NAME+" " +
                    ((where == null)?"":"WHERE "+where);
            Cursor c = sqlite.getReadableDatabase().rawQuery(sql, null);
            while(c.moveToNext()){

                String code = c.getString(c.getColumnIndex(CODE));
                String fecha = c.getString(c.getColumnIndex(MDATE));
                OrderModel om = new OrderModel(code,String.valueOf(Funciones.calcularMinutos(fecha, Funciones.getFormatedDate())), getOrderDetailModels(code));
                objects.add(om);
            }
            return objects;
        }*/

        public ArrayList<OrderDetailModel> getOrderDetailModels(String code){

            ArrayList<OrderDetailModel> objects = new ArrayList<>();
            try {
                String where = "sd."+DETAIL_CODESALES + " = '" + code+"'";

                String sql = "Select sd."+DETAIL_CODE+" as CODE,sd."+DETAIL_CODESALES+" as CODESALES ,p."+ProductsController.CODE+" AS CODEPRODUCT" +
                        ", p." + ProductsController.DESCRIPTION + " as PRODUCTO, sd." + DETAIL_QUANTITY + " as CANTIDAD, sd." + DETAIL_UNIT + " as UNIDAD," +
                        " m."+MeasureUnitsController.CODE+" AS CODEMEDIDA, m." + MeasureUnitsController.DESCRIPTION + " AS MEDIDA, ifnull(pc."+ProductsControlController.BLOQUED+", '0') as BLOQUED, " +
                        "sd."+DETAIL_DATE+" as DATE, sd."+DETAIL_MDATE+" as MDATE "+
                        "FROM " + TABLE_NAME_DETAIL + " sd " +
                        "LEFT JOIN " + ProductsController.TABLE_NAME + " p on sd." + DETAIL_CODEPRODUCT + " = p." + ProductsController.CODE + " " +
                        "LEFT JOIN " + MeasureUnitsController.TABLE_NAME + " m on m." + MeasureUnitsController.CODE + " = sd." + DETAIL_CODEUND + " " +
                        "LEFT JOIN "+ProductsControlController.TABLE_NAME+" pc on pc."+ProductsControlController.CODEPRODUCT+ " = p." + ProductsController.CODE + " " +
                        "WHERE " + where+" " +
                        "GROUP BY p."+ProductsController.CODE+", sd."+DETAIL_CODEUND;
                Cursor c = sqlite.getReadableDatabase().rawQuery(sql, null);
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


        public Sales getTempSale(){
           Sales s = null;

            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, null, null, null, null, null);
            if(c.moveToFirst()){
                s = new Sales(c);
            }
            c.close();
            return s;
        }
/*
    public ArrayList<ArrayList> getSplitedTempSale(String notes, String codeAreaDetail){


        ArrayList<ArrayList> result = new ArrayList<>();

        try {
            Sales originalSale = null;
            Cursor cos = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, null, null, null, null, null);
            if (cos.moveToFirst()) {
                originalSale = new Sales(cos);
                originalSale.setNOTES(notes);
                originalSale.setCODEAREADETAIL(codeAreaDetail);
            }
            cos.close();

            ArrayList<Sales> sales = new ArrayList<>();
            ArrayList<SalesDetails> salesDetails = new ArrayList<>();
            ArrayList<String> ArraySplit = new ArrayList<>();

            if (originalSale != null) {

                String splitBy = UserControlController.getInstance(context).orderSplitType();
                String fieldOrder = "p." + ProductsController.TYPE;//DEFAULT
                if (splitBy.equals(CODES.VAL_USERCONTROL_ORDERSPLITTYPE_FAMILY)) {
                    fieldOrder = "p." + ProductsController.TYPE;
                    ArraySplit = getDistinctProductTypesInOrderDetail();
                } else if (splitBy.equals(CODES.VAL_USERCONTROL_ORDERSPLITTYPE_GROUP)) {
                    fieldOrder = "p." + ProductsController.SUBTYPE;
                    ArraySplit = getDistinctProductSubTypesInOrderDetail();
                }
                String sql = "SELECT od." + DETAIL_CODE + " as " + DETAIL_CODE + " , od." + DETAIL_CODESALES + " as " + DETAIL_CODESALES + " , od." + DETAIL_CODEPRODUCT + " as " + DETAIL_CODEPRODUCT + ",od." +
                        DETAIL_DISCOUNT + " as " + DETAIL_DISCOUNT + ",od." + DETAIL_POSITION + " as " + DETAIL_POSITION + ",od." + DETAIL_PRICE + " as " + DETAIL_PRICE + ",od." +
                        DETAIL_QUANTITY + " as " + DETAIL_QUANTITY + ",od." + DETAIL_UNIT + " as " + DETAIL_UNIT + ",od." + DETAIL_CODEUND + " as " + DETAIL_CODEUND + ", od." + DETAIL_DATE + " as "+DETAIL_DATE+", od." + DETAIL_MDATE + " as " + DETAIL_MDATE
                        + ", " + fieldOrder + " as FO " +
                        "FROM " + TABLE_NAME_DETAIL + " od " +
                        "INNER JOIN " + ProductsController.TABLE_NAME + " p on p." + ProductsController.CODE + " = od." + DETAIL_CODEPRODUCT + " " +
                        "ORDER BY " + fieldOrder;

                Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
                int lastIndex = -1;
                for (String spl : ArraySplit) {
                    String codeSalesOrigen =originalSale.getCODE();
                    String code = Funciones.generateCode();
                    String codeuser = originalSale.getCODEUSER();
                    String codeAreaDet = originalSale.getCODEAREADETAIL();
                    double totalDiscount = originalSale.getTOTALDISCOUNT();
                    double total = originalSale.getTOTAL();
                    int status = originalSale.getSTATUS();
                    String note = originalSale.getNOTES();
                    String codeReason = originalSale.getCODEREASON();
                    String reasonDescription = originalSale.getCODEREASON();
                    String codeProductType= (splitBy.equals(CODES.VAL_USERCONTROL_ORDERSPLITTYPE_FAMILY))?spl: null;
                    String codeProductSubType = (splitBy.equals(CODES.VAL_USERCONTROL_ORDERSPLITTYPE_GROUP))?spl: null;


                    Sales s = new Sales(code, codeuser, codeAreaDet, totalDiscount, total, status, note, codeReason, reasonDescription, codeProductType, codeProductSubType,codeSalesOrigen, null );
                    sales.add(s);
                    c.moveToPosition(lastIndex);
                    while (c.moveToNext()) {
                        lastIndex++;
                        if (!c.getString(c.getColumnIndex("FO")).equals(spl)) {
                            lastIndex-=1;
                            break;
                        }
                        SalesDetails sd = new SalesDetails(c);
                        sd.setCODESALES(code);//Codigo de la orden
                        salesDetails.add(sd);

                    }
                }
                c.close();

                result.add(sales);//SALES position 0
                result.add(salesDetails);//SALES_DETAILS  position 1

            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }*/





    public ArrayList<String> getDistinctProductTypesInOrderDetail(){
            ArrayList<String> familys = new ArrayList<>();
            String sql = "SELECT  p."+ProductsController.IDPRODUCTTYPE+" " +
                    "FROM "+ProductsController.TABLE_NAME+" p " +
                    "INNER JOIN "+ TABLE_NAME_DETAIL+" od on od."+DETAIL_CODEPRODUCT+" = p."+ProductsController.CODE+" " +
                    "GROUP BY p."+ProductsController.IDPRODUCTTYPE;
            try {
                Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
                while(c.moveToNext()){
                    familys.add(c.getString(0));
                }c.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            return familys;
    }
    public ArrayList<String> getDistinctProductSubTypesInOrderDetail(){
        ArrayList<String> groups = new ArrayList<>();
        String sql = "SELECT  p."+ProductsController.IDPRODUCTSUBTYPE+" " +
                "FROM "+ProductsController.TABLE_NAME+" p " +
                "INNER JOIN "+ TABLE_NAME_DETAIL+" od on od."+DETAIL_CODEPRODUCT+" = p."+ProductsController.CODE+" " +
                "GROUP BY p."+ProductsController.IDPRODUCTSUBTYPE;
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while(c.moveToNext()){
                groups.add(c.getString(0));
            }c.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return groups;
    }


    public ArrayList<ArrayList> getSplittedOrder(String notes, String codeAreaDetail){
            ArrayList<ArrayList> result = new ArrayList<>();
            ArrayList<Sales> heads = new ArrayList<>();
            ArrayList<SalesDetails>details = new ArrayList<>();

        Sales originalSale = null;
        Cursor cos = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, null, null, null, null, null);
        if (cos.moveToFirst()) {
            originalSale = new Sales(cos);
            originalSale.setNOTES(notes);
            originalSale.setCODEAREADETAIL(codeAreaDetail);
        }
        cos.close();


        String sql = "SELECT od." + DETAIL_CODE + " as " + DETAIL_CODE + " , od." + DETAIL_CODESALES + " as " + DETAIL_CODESALES + " , od." + DETAIL_CODEPRODUCT + " as " + DETAIL_CODEPRODUCT + ",od." +
                DETAIL_DISCOUNT + " as " + DETAIL_DISCOUNT + ",od." + DETAIL_POSITION + " as " + DETAIL_POSITION + ",od." + DETAIL_PRICE + " as " + DETAIL_PRICE + ",od." +
                DETAIL_QUANTITY + " as " + DETAIL_QUANTITY + ",od." + DETAIL_UNIT + " as " + DETAIL_UNIT + ",od." + DETAIL_CODEUND + " as " + DETAIL_CODEUND + ", od." + DETAIL_DATE + " as "+DETAIL_DATE+", od." + DETAIL_MDATE + " as " + DETAIL_MDATE+"," +
                "ifnull(uc."+UserControlController.VALUE+", -1) as CODETYPE, ifnull(uc2."+UserControlController.VALUE+", -1) as CODESUBTYPE "+
                "FROM " + TABLE_NAME_DETAIL + " od " +
                "INNER JOIN " + ProductsController.TABLE_NAME + " p on p." + ProductsController.CODE + " = od." + DETAIL_CODEPRODUCT + " " +
                "LEFT JOIN "+UserControlController.TABLE_NAME+" uc on uc."+UserControlController.CONTROL+" = '"+CODES.USERCONTROL_ORDERSPLIT+"' AND uc."+UserControlController.ACTIVE+" = '1' AND uc."+ UserControlController.VALUE+" = p."+ProductsController.IDPRODUCTTYPE+" " +
                "LEFT JOIN "+UserControlController.TABLE_NAME+" uc2 on uc2."+UserControlController.CONTROL+" = '"+CODES.USERCONTROL_ORDERSPLIT+"' AND uc2."+UserControlController.ACTIVE+" = '1' AND uc2."+ UserControlController.VALUE+" = p."+ProductsController.IDPRODUCTSUBTYPE+" " +
                "ORDER BY uc."+UserControlController.VALUE+", uc2."+UserControlController.VALUE;
        Cursor c = sqlite.getReadableDatabase().rawQuery(sql, null);
        String lastHead = "";
        String splitType;
        Sales s = null;
        while(c.moveToNext()){
            String currentHead;
            if(!c.getString(c.getColumnIndex("CODETYPE")).equals("-1")){
                currentHead = c.getString(c.getColumnIndex("CODETYPE"));
                splitType = CODES.VAL_USERCONTROL_ORDERSPLITTYPE_FAMILY;
            }else if(!c.getString(c.getColumnIndex("CODESUBTYPE")).equals("-1")){
                currentHead = c.getString(c.getColumnIndex("CODESUBTYPE"));
                splitType = CODES.VAL_USERCONTROL_ORDERSPLITTYPE_GROUP;
            }else{
                currentHead= "NONE";
                splitType="NONE";
            }

            if(!lastHead.equals(currentHead)){
                lastHead = currentHead;
                s = new Sales(Funciones.generateCode(),originalSale.getCODEUSER(),codeAreaDetail,0.0,0.0,CODES.CODE_ORDER_STATUS_OPEN,notes,null,null,null,null,originalSale.getCODE(),null);
                if(splitType.equals(CODES.VAL_USERCONTROL_ORDERSPLITTYPE_FAMILY)){
                    s.setCODEPRODUCTTYPE(lastHead);
                }else if(splitType.equals(CODES.VAL_USERCONTROL_ORDERSPLITTYPE_GROUP)){
                    s.setCODEPRODUCTSUBTYPE(lastHead);
                }

                heads.add(s);
            }

            SalesDetails sd = new SalesDetails(c);
            sd.setCODESALES(s.getCODE());
            details.add(sd);

            /////////////////////////////////////////////////////////////////////
            /////////     ACTUALIZAR EL TOTAL         ///////////////////////////
            double total = sd.getPRICE() * sd.getQUANTITY();
            int saleIndex = heads.indexOf(s);
            heads.get(saleIndex).setTOTAL(heads.get(saleIndex).getTOTAL() + total);
            //////////////////////////////////////////////////////////////////////


        }c.close();
        result.add(heads);
        result.add(details);

        return result;
    }

        public ArrayList<SalesDetails> getTempSalesDetails( Sales s){
           ArrayList<SalesDetails> result = new ArrayList<>();

            String selection = DETAIL_CODESALES +" = ?";
            String[] args = new String[]{s.getCODE()};
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME_DETAIL, columnsDetails, selection, args, null, null, null);

            while (c.moveToNext()){
                result.add(new SalesDetails(c));
            }
            c.close();
            return result;
        }

    public SalesDetails getTempSaleDetailByCode( String code_sale_detail){
       SalesDetails result = null;

        String selection = DETAIL_CODE +" = ?";
        String[] args = new String[]{code_sale_detail};
        Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME_DETAIL, columnsDetails, selection, args, null, null, null);

        if (c.moveToFirst()){
           result = new SalesDetails(c);
        }
        c.close();
        return result;
    }

    public ArrayList<SalesDetails> getTempSaleDetailByCodeProduct( String codeProduct){
        ArrayList<SalesDetails> result = new ArrayList<>();

        String selection = DETAIL_CODEPRODUCT +" = ?";
        String[] args = new String[]{codeProduct};
        Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME_DETAIL, columnsDetails, selection, args, null, null, null);

        if (c.moveToFirst()){
            result.add( new SalesDetails(c));
        }
        c.close();
        return result;
    }

    public SalesDetails getTempSaleDetailByCodeProductAndCodeMeasure( String code_product, String code_measure){
        SalesDetails result = null;

        String selection = DETAIL_CODEPRODUCT +" = ? AND "+DETAIL_CODEUND+" = ? ";
        String[] args = new String[]{code_product, code_measure};
        Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME_DETAIL, columnsDetails, selection, args, null, null, null);

        if (c.moveToFirst()){
            result = new SalesDetails(c);
        }
        c.close();
        return result;
    }

    public void deleteTempSaleDetailByCodeProductAndCodeMeasure( String code_product, String code_measure){

        try {
            String selection = DETAIL_CODEPRODUCT + " = ? AND " + DETAIL_CODEUND + " = ? ";
            String[] args = new String[]{code_product, code_measure};
            DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME_DETAIL, selection, args);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public double getSumPrice(){
            double result = 0.0;
             String sql = "SELECT  SUM("+DETAIL_PRICE+" * "+DETAIL_QUANTITY+") AS TOTAL " +
                "FROM "+TABLE_NAME_DETAIL+" ";
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            if(c.moveToFirst()){
                result =c.getDouble(0);
            }c.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public void updatePrices(){
            String sql = "SELECT sd."+DETAIL_CODEPRODUCT+" as CODEPRODUCT, sd."+DETAIL_CODEUND+" as CODEUND, pm."+ProductsMeasureController.PRICE+" as PRICE " +
                    "FROM "+TABLE_NAME_DETAIL+" sd " +
                    "INNER JOIN "+ProductsMeasureController.TABLE_NAME+" pm on sd."+DETAIL_CODEPRODUCT+" = pm."+ProductsMeasureController.IDPRODUCT+" " +
                    "AND sd."+DETAIL_CODEUND+" = pm."+ProductsMeasureController.IDPRODUCTMEASURE+" ";
            Cursor c = sqlite.getReadableDatabase().rawQuery(sql,null);
            while(c.moveToNext()){
                ContentValues cv = new ContentValues();
                cv.put(DETAIL_PRICE, c.getDouble(c.getColumnIndex("PRICE")));
                        String where = DETAIL_CODEPRODUCT+" = ? AND "+DETAIL_CODEUND+" = ?";
                sqlite.getWritableDatabase().update(TABLE_NAME_DETAIL,cv,where,new String[]{c.getString(c.getColumnIndex("CODEPRODUCT")), c.getString(c.getColumnIndex("CODEUND"))});
            }c.close();
    }

}
