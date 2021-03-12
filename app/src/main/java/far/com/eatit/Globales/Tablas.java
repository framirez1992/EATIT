package far.com.eatit.Globales;

import far.com.eatit.Controllers.AreasController;
import far.com.eatit.Controllers.AreasDetailController;
import far.com.eatit.Controllers.CombosController;
import far.com.eatit.Controllers.CompanyController;
import far.com.eatit.Controllers.DevicesController;
import far.com.eatit.Controllers.LicenseController;
import far.com.eatit.Controllers.MeasureUnitsController;
import far.com.eatit.Controllers.MeasureUnitsInvController;
import far.com.eatit.Controllers.NotificationsController;
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
import far.com.eatit.Controllers.SalesHistoryController;
import far.com.eatit.Controllers.StoreHouseController;
import far.com.eatit.Controllers.StoreHouseDetailController;
import far.com.eatit.Controllers.TableCodeController;
import far.com.eatit.Controllers.TableFilterController;
import far.com.eatit.Controllers.TokenController;
import far.com.eatit.Controllers.UserControlController;
import far.com.eatit.Controllers.UserInboxController;
import far.com.eatit.Controllers.UserTypesController;
import far.com.eatit.Controllers.UsersController;
import far.com.eatit.Controllers.UsersDevicesController;
import far.com.eatit.Controllers.WorkDayController;

public class Tablas {

    ////////////////////////////////////////////////////////////////////////
    /////////////           CLOUD FIRESTORE         ///////////////////////
    //clientes de la app
    public static final String generalUsers = "GENERAL_USERS";
    public static final String generalLicencias = "GENERAL_LICENSES";
    public static final String generalRoles = "GENERAL_ROLES";

    //GENERAL_USERS childs
    public static final String generalUsersAreas = "Areas";
    public static final String generalUsersAreasDetail = "AreasDetail";
    public static final String generalUsersCombos = "Combos";
    public static final String generalUsersCompany = "Company";
    public static final String generalUsersMeasureUnits = "MeasureUnits";
    public static final String generalUsersMeasureUnitsInv = "MeasureUnitsInv";
    public static final String generalUsersPriceList = "PriceList";
    public static final String generalUsersProducts = "Products";
    public static final String generalUsersProductsInv = "ProductsInv";
    public static final String generalUsersProductsControl = "ProductsControl";
    public static final String generalUsersProductsMeasure = "ProductsMeasure";
    public static final String generalUsersProductsMeasureInv = "ProductsMeasureInv";
    public static final String generalUsersProductsTypes = "ProductsTypes";
    public static final String generalUsersProductsTypesInv = "ProductsTypesInv";
    public static final String generalUsersProductsSubTypes = "ProductsSubTypes";
    public static final String generalUsersProductsSubTypesInv = "ProductsSubTypesInv";
    public static final String generalUsersReceipts = "Receipts";
    public static final String generalUsersReceiptsHistory = "ReceiptsHistory";
    public static final String generalUsersSales = "Sales";
    public static final String generalUsersSalesDetails = "SalesDetails";
    public static final String generalUsersSalesHistory = "SalesHistory";
    public static final String generalUsersSalesDetailsHistory = "SalesDetailsHistory";
    public static final String generalUsersStoreHouse = "StoreHouse";
    public static final String generalUsersStoreHouseDetail = "StoreHouseDetail";
    public static final String generalUsersTableCode = "TableCode";
    public static final String generalUsersTableFilter = "TableFilter";
    public static final String generalUsersToken = "Token";
    public static final String generalUsersUserControl = "UserControl";
    public static final String generalUsersUsers = "Users";
    public static final String generalUsersUsersDevices = "UsersDevices";
    public static final String generalUsersUserTypes = "UserTypes";
    public static final String generalUsersUserInbox = "UserInbox";
    //GENERAL_LICENSES childs
    public static final String generalLicenciasDevices = "Devices";

    public static String[]tablesFireBase = new String[]{generalUsersAreas, generalUsersAreasDetail, generalUsersCombos,generalUsersCompany,
            generalLicenciasDevices, generalUsersMeasureUnits,generalUsersMeasureUnitsInv, generalUsersPriceList, generalUsersProducts,generalUsersProductsInv,generalUsersProductsControl,generalUsersProductsMeasure,
            generalUsersProductsMeasureInv,generalUsersProductsTypes,generalUsersProductsTypesInv, generalUsersProductsSubTypes,generalUsersProductsSubTypesInv ,generalUsersSales, generalUsersSalesDetails, generalUsersSalesHistory,
            generalUsersSalesDetailsHistory, generalUsersTableCode,generalUsersTableFilter, generalUsersUserControl, generalUsersUsers,
            generalUsersUserTypes, generalUsersUserInbox};
    public static String[] tablesLocal = new String[]{AreasController.TABLE_NAME, AreasDetailController.TABLE_NAME, CombosController.TABLE_NAME, CompanyController.TABLE_NAME, DevicesController.TABLE_NAME,
            LicenseController.TABLE_NAME, MeasureUnitsController.TABLE_NAME, MeasureUnitsInvController.TABLE_NAME, PriceListController.TABLE_NAME, ProductsControlController.TABLE_NAME, ProductsController.TABLE_NAME,
            ProductsInvController.TABLE_NAME, ProductsMeasureController.TABLE_NAME, ProductsMeasureInvController.TABLE_NAME, ProductsSubTypesController.TABLE_NAME, ProductsSubTypesInvController.TABLE_NAME,
            ProductsTypesController.TABLE_NAME, ProductsTypesInvController.TABLE_NAME, ReceiptController.TABLE_NAME, RolesController.TABLE_NAME, SalesController.TABLE_NAME, SalesController.TABLE_NAME_DETAIL,
            TableCodeController.TABLE_NAME, TableFilterController.TABLE_NAME, UserControlController.TABLE_NAME, UserInboxController.TABLE_NAME, UsersController.TABLE_NAME, UserTypesController.TABLE_NAME,
            StoreHouseController.TABLE_NAME, StoreHouseDetailController.TABLE_NAME, SalesHistoryController.TABLE_NAME, SalesHistoryController.TABLE_NAME_DETAIL};



   ///////////////////////////////////////////////////////////////////////
    ///////////////////        SQLITE         ///////////////////////////
   public static final String DB_NAME = "EATIT.db";
   public static final String tempOrders = "TempOrders";
    public static final String tempOrdersDetails = "TempOrdersDetails";



}
