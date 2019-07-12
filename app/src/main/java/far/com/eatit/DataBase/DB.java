package far.com.eatit.DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import far.com.eatit.CloudFireStoreObjects.ProductsMeasure;
import far.com.eatit.CloudFireStoreObjects.UserInbox;
import far.com.eatit.Controllers.AreasController;
import far.com.eatit.Controllers.AreasDetailController;
import far.com.eatit.Controllers.CombosController;
import far.com.eatit.Controllers.CompanyController;
import far.com.eatit.Controllers.DevicesController;
import far.com.eatit.Controllers.LicenseController;
import far.com.eatit.Controllers.MeasureUnitsController;
import far.com.eatit.Controllers.MeasureUnitsInvController;
import far.com.eatit.Controllers.PriceListController;
import far.com.eatit.Controllers.ProductsControlController;
import far.com.eatit.Controllers.ProductsController;
import far.com.eatit.Controllers.ProductsInvController;
import far.com.eatit.Controllers.ProductsMeasureController;
import far.com.eatit.Controllers.ProductsMeasureInvController;
import far.com.eatit.Controllers.ProductsSubTypesController;
import far.com.eatit.Controllers.ProductsSubTypesInvController;
import far.com.eatit.Controllers.ProductsTypesController;
import far.com.eatit.Controllers.ProductsTypesInvController;
import far.com.eatit.Controllers.ReceiptController;
import far.com.eatit.Controllers.RolesController;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Controllers.StoreHouseController;
import far.com.eatit.Controllers.StoreHouseDetailController;
import far.com.eatit.Controllers.TableCodeController;
import far.com.eatit.Controllers.TableFilterController;
import far.com.eatit.Controllers.TempOrdersController;
import far.com.eatit.Controllers.UserControlController;
import far.com.eatit.Controllers.UserInboxController;
import far.com.eatit.Controllers.UserTypesController;
import far.com.eatit.Controllers.UsersController;
import far.com.eatit.Globales.Tablas;

public class DB extends SQLiteOpenHelper {
    private static DB instance;
    public static DB getInstance(Context c){
    if(instance == null){
        instance = new DB(c, Tablas.DB_NAME,null,1);
    }
    return instance;
    }

    private DB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.beginTransaction();

            db.execSQL(AreasController.QUERY_CREATE);
            db.execSQL(AreasDetailController.QUERY_CREATE);
            db.execSQL(LicenseController.QUERY_CREATE);
            db.execSQL(UsersController.QUERY_CREATE);
            db.execSQL(UserTypesController.QUERY_CREATE);
            db.execSQL(CombosController.QUERY_CREATE);
            db.execSQL(CompanyController.QUERY_CREATE);
            db.execSQL(DevicesController.QUERY_CREATE);
            db.execSQL(MeasureUnitsController.QUERY_CREATE);
            db.execSQL(MeasureUnitsInvController.QUERY_CREATE);
            db.execSQL(PriceListController.QUERY_CREATE);
            db.execSQL(ProductsController.QUERY_CREATE);
            db.execSQL(ProductsInvController.QUERY_CREATE);
            db.execSQL(ProductsTypesController.QUERY_CREATE);
            db.execSQL(ProductsTypesInvController.QUERY_CREATE);
            db.execSQL(ProductsSubTypesController.QUERY_CREATE);
            db.execSQL(ProductsSubTypesInvController.QUERY_CREATE);
            db.execSQL(ReceiptController.QUERY_CREATE);
            db.execSQL(SalesController.getQueryCreateSales());
            db.execSQL(SalesController.getQueryCreateSalesDetail());
            db.execSQL(SalesController.getQueryCreateSalesHistory());
            db.execSQL(SalesController.getQueryCreateSalesDetailHistory());
            db.execSQL(StoreHouseController.QUERY_CREATE);
            db.execSQL(StoreHouseDetailController.QUERY_CREATE);
            db.execSQL(UserInboxController.QUERY_CREATE);
            db.execSQL(ProductsMeasureController.QUERY_CREATE);
            db.execSQL(ProductsMeasureInvController.QUERY_CREATE);
            db.execSQL(ProductsControlController.QUERY_CREATE);
            db.execSQL(TableCodeController.QUERY_CREATE);
            db.execSQL(TableFilterController.QUERY_CREATE);
            db.execSQL(UserControlController.QUERY_CREATE);
            db.execSQL(RolesController.QUERY_CREATE);

            db.execSQL(TempOrdersController.QUERY_CREATE);
            db.execSQL(TempOrdersController.QUERY_CREATE_DETAIL);


            db.setTransactionSuccessful();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createStructure(){

    }

    public static String getWhereFormat(String[] campos){
        String result ="";
        for(int i = 0; i< campos.length; i++){
            result+=(i == 0)?campos[i]+" = ? ":","+campos[i]+" = ?";
        }
        return result;
    }
    public boolean hasDependencies(String table, String field, String code){
        boolean resutl= false;
        String sql ="SELECT "+field+" from "+table+" WHERE "+field+" = ? ";
        Cursor c = getReadableDatabase().rawQuery(sql, new String[]{code});
        if(c.moveToFirst()){
            resutl = true;
        }c.close();
        return resutl;
    }
}
