package far.com.eatit.Controllers;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import far.com.eatit.API.APIClient;
import far.com.eatit.API.APIInterface;
import far.com.eatit.API.models.Area;
import far.com.eatit.API.models.AreaDetail;
import far.com.eatit.API.models.Client;
import far.com.eatit.API.models.Company;
import far.com.eatit.API.models.Day;
import far.com.eatit.API.models.LoginResponse;
import far.com.eatit.API.models.MeasureUnit;
import far.com.eatit.API.models.Order;
import far.com.eatit.API.models.OrderDetail;
import far.com.eatit.API.models.Payment;
import far.com.eatit.API.models.Product;
import far.com.eatit.API.models.ProductMeasure;
import far.com.eatit.API.models.ProductSubType;
import far.com.eatit.API.models.ProductType;
import far.com.eatit.API.models.Receipt;
import far.com.eatit.API.models.Sale;
import far.com.eatit.API.models.SaleDetail;
import far.com.eatit.API.models.Table;
import far.com.eatit.API.models.UserRole;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Interfases.IActualizationListener;
import far.com.eatit.Utils.Funciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ActualizationController {
    Context context;
    APIInterface apiInterface;
    LoginResponse loginResponse;
    boolean initialLoad;
    IActualizationListener listener;

    private int CURRENT_PAGE =1;
    private final int OFFSET= 1;
    private  final String[] DownloadData = new String[]{UserRolesController.TABLE_NAME, CompanyController.TABLE_NAME,ClientController.TABLE_NAME,
            AreasController.TABLE_NAME,AreasDetailController.TABLE_NAME, MeasureUnitsController.TABLE_NAME, ProductsController.TABLE_NAME,ProductsMeasureController.TABLE_NAME, ProductsTypesController.TABLE_NAME, ProductsSubTypesController.TABLE_NAME,
            TableController.TABLE_NAME , OrderController.TABLE_NAME, OrderController.TABLE_NAME_DETAIL,
            SalesController.TABLE_NAME, SalesController.TABLE_NAME_DETAIL, ReceiptController.TABLE_NAME, PaymentController.TABLE_NAME, DayController.TABLE_NAME};
    private int CURRENT_INDEX=0;

    public ActualizationController(Context context, IActualizationListener listener){
        this.context = context;
        this.apiInterface = APIClient.getClient(context).create(APIInterface.class);
        this.loginResponse =  Funciones.getLoginResponseData(context);
        this.listener = listener;
    }

    public void initialLoad(){
        initialLoad = true;
        CURRENT_PAGE = 1;
        loadData();
    }
    public void lastChanges(){
        initialLoad = false;
        CURRENT_PAGE = 1;
        loadData();
    }

    public void resume(){
        loadData();
    }

    private String getDate(){
        String date;
        if(initialLoad){
            date= Funciones.getMinServerDate();
        }else{
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery("select ifnull(ifnull(DELETE_DATE, UPDATE_DATE), CREATE_DATE) as maxDate from "+DownloadData[CURRENT_INDEX]+" ORDER BY DELETE_DATE, UPDATE_DATE,CREATE_DATE  limit 1", null);
            if(c.moveToFirst()){
              date = c.getString(0);
          }else{
              date= Funciones.getMinServerDate();
          }
            c.close();
        }
        return  date;
    }

    private void loadData(){
        if(CURRENT_INDEX >= DownloadData.length){
           listener.onFinishLoad();
            return;
        }

        switch (DownloadData[CURRENT_INDEX]){
            case UserRolesController.TABLE_NAME:
                userRoles();
                break;
            case CompanyController.TABLE_NAME:
                company();
                break;
            case ClientController.TABLE_NAME:
                clients();
                break;
            case AreasController.TABLE_NAME:
                areas();
                break;
            case AreasDetailController.TABLE_NAME:
                areaDetails();
                break;
            case MeasureUnitsController.TABLE_NAME:
                measureUnit();
                break;
            case ProductsController.TABLE_NAME:
                products();
                break;
            case ProductsMeasureController.TABLE_NAME:
                productMeasure();
                break;
            case ProductsTypesController.TABLE_NAME:
                productTypes();
                break;
            case ProductsSubTypesController.TABLE_NAME:
                productSubTypes();
                break;
            case TableController.TABLE_NAME:
                tables();
                break;
            case OrderController.TABLE_NAME:
                orders();
                break;
            case OrderController.TABLE_NAME_DETAIL:
                orderDetails();
                break;
            case SalesController.TABLE_NAME:
                sales();
                break;
            case SalesController.TABLE_NAME_DETAIL:
                salesDetails();
                break;
            case ReceiptController.TABLE_NAME:
                receipts();
                break;
            case PaymentController.TABLE_NAME:
                payments();
                break;
            case DayController.TABLE_NAME:
                day();
                break;
            default:
                listener.onFinishLoad();
                break;


        }

    }

    private void updateProgress(){
        listener.onProgressChange(((CURRENT_INDEX + 1) * 100)/DownloadData.length, "Loading ".concat(DownloadData[CURRENT_INDEX]).concat(" Page ").concat(String.valueOf(CURRENT_PAGE)));
    }

    private void userRoles(){

    //para probar error cambia el formato de la fecha
        apiInterface.getUserRolesUpdates(/*loginResponse.getUserDevice().getId()*/2,getDate(),CURRENT_PAGE,OFFSET).enqueue(new Callback<List<UserRole>>() {
            @Override
            public void onResponse(Call<List<UserRole>> call, Response<List<UserRole>> response) {
                if(response.isSuccessful()){
                    updateProgress();

                    List<UserRole> dataList = response.body();
                    if(dataList.size() > 0){
                        for (UserRole obj: dataList) {
                        UserRolesController.getInstance(context).insertOrUpdate(obj);
                        }
                        CURRENT_PAGE++;
                        userRoles();
                    }else{
                        CURRENT_INDEX++;
                        CURRENT_PAGE = 1;
                        loadData();
                    }

                }else{

                    listener.onError("Error loading data. Do you want to retry?");
                }
            }

            @Override
            public void onFailure(Call<List<UserRole>> call, Throwable t) {

                listener.onError("Error loading data. Do you want to retry?");
            }
        });
    }



    private void company(){

        //para probar error cambia el formato de la fecha
        apiInterface.getCompanyUpdates(/*loginResponse.getUserDevice().getId()*/2,getDate(),CURRENT_PAGE,OFFSET).enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {
                if(response.isSuccessful()){
                    updateProgress();

                    List<Company> dataList = response.body();
                    if(dataList.size() > 0){
                        for (Company obj: dataList) {
                            CompanyController.getInstance(context).insertOrUpdate(obj);
                        }
                        CURRENT_PAGE++;
                        company();
                    }else{
                        CURRENT_INDEX++;
                        CURRENT_PAGE = 1;
                        loadData();
                    }

                }else{

                    listener.onError("Error loading data. Do you want to retry?");
                }
            }

            @Override
            public void onFailure(Call<List<Company>> call, Throwable t) {

                listener.onError("Error loading data. Do you want to retry?");
            }
        });
    }

    private void clients(){

        //para probar error cambia el formato de la fecha
        apiInterface.getClientUpdates(/*loginResponse.getUserDevice().getId()*/2,getDate(),CURRENT_PAGE,OFFSET).enqueue(new Callback<List<Client>>() {
            @Override
            public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {
                if(response.isSuccessful()){
                    updateProgress();

                    List<Client> dataList = response.body();
                    if(dataList.size() > 0){
                        for (Client obj: dataList) {
                            ClientController.getInstance(context).insertOrUpdate(obj);
                        }
                        CURRENT_PAGE++;
                        clients();
                    }else{
                        CURRENT_INDEX++;
                        CURRENT_PAGE = 1;
                        loadData();
                    }

                }else{

                    listener.onError("Error loading data. Do you want to retry?");
                }
            }

            @Override
            public void onFailure(Call<List<Client>> call, Throwable t) {

                listener.onError("Error loading data. Do you want to retry?");
            }
        });
    }

    private void areas(){

        //para probar error cambia el formato de la fecha
        apiInterface.getAreaUpdates(/*loginResponse.getUserDevice().getId()*/2,getDate(),CURRENT_PAGE,OFFSET).enqueue(new Callback<List<Area>>() {
            @Override
            public void onResponse(Call<List<Area>> call, Response<List<Area>> response) {
                if(response.isSuccessful()){
                    updateProgress();

                    List<Area> dataList = response.body();
                    if(dataList.size() > 0){
                        for (Area obj: dataList) {
                            AreasController.getInstance(context).insertOrUpdate(obj);
                        }
                        CURRENT_PAGE++;
                        areas();
                    }else{
                        CURRENT_INDEX++;
                        CURRENT_PAGE = 1;
                        loadData();
                    }

                }else{

                    listener.onError("Error loading data. Do you want to retry?");
                }
            }

            @Override
            public void onFailure(Call<List<Area>> call, Throwable t) {

                listener.onError("Error loading data. Do you want to retry?");
            }
        });
    }

    private void areaDetails(){

        //para probar error cambia el formato de la fecha
        apiInterface.getAreaDetailUpdates(/*loginResponse.getUserDevice().getId()*/2,getDate(),CURRENT_PAGE,OFFSET).enqueue(new Callback<List<AreaDetail>>() {
            @Override
            public void onResponse(Call<List<AreaDetail>> call, Response<List<AreaDetail>> response) {
                if(response.isSuccessful()){
                    updateProgress();

                    List<AreaDetail> dataList = response.body();
                    if(dataList.size() > 0){
                        for (AreaDetail obj: dataList) {
                            AreasDetailController.getInstance(context).insertOrUpdate(obj);
                        }
                        CURRENT_PAGE++;
                        areaDetails();
                    }else{
                        CURRENT_INDEX++;
                        CURRENT_PAGE = 1;
                        loadData();
                    }

                }else{

                    listener.onError("Error loading data. Do you want to retry?");
                }
            }

            @Override
            public void onFailure(Call<List<AreaDetail>> call, Throwable t) {

                listener.onError("Error loading data. Do you want to retry?");
            }
        });
    }



    private void measureUnit(){

        //para probar error cambia el formato de la fecha
        apiInterface.getMeasureUnitUpdates(/*loginResponse.getUserDevice().getId()*/2,getDate(),CURRENT_PAGE,OFFSET).enqueue(new Callback<List<MeasureUnit>>() {
            @Override
            public void onResponse(Call<List<MeasureUnit>> call, Response<List<MeasureUnit>> response) {
                if(response.isSuccessful()){
                    updateProgress();

                    List<MeasureUnit> dataList = response.body();
                    if(dataList.size() > 0){
                        for (MeasureUnit obj: dataList) {
                            MeasureUnitsController.getInstance(context).insertOrUpdate(obj);
                        }
                        CURRENT_PAGE++;
                        measureUnit();
                    }else{
                        CURRENT_INDEX++;
                        CURRENT_PAGE = 1;
                        loadData();
                    }

                }else{

                    listener.onError("Error loading data. Do you want to retry?");
                }
            }

            @Override
            public void onFailure(Call<List<MeasureUnit>> call, Throwable t) {

                listener.onError("Error loading data. Do you want to retry?");
            }
        });
    }

    private void products(){

        //para probar error cambia el formato de la fecha
        apiInterface.getProductUpdates(/*loginResponse.getUserDevice().getId()*/2,getDate(),CURRENT_PAGE,OFFSET).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    updateProgress();

                    List<Product> dataList = response.body();
                    if(dataList.size() > 0){
                        for (Product obj: dataList) {
                            ProductsController.getInstance(context).insertOrUpdate(obj);
                        }
                        CURRENT_PAGE++;
                        products();
                    }else{
                        CURRENT_INDEX++;
                        CURRENT_PAGE = 1;
                        loadData();
                    }

                }else{

                    listener.onError("Error loading data. Do you want to retry?");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

                listener.onError("Error loading data. Do you want to retry?");
            }
        });
    }


    private void productMeasure(){

        //para probar error cambia el formato de la fecha
        apiInterface.getProductMeasureUpdates(/*loginResponse.getUserDevice().getId()*/2,getDate(),CURRENT_PAGE,OFFSET).enqueue(new Callback<List<ProductMeasure>>() {
            @Override
            public void onResponse(Call<List<ProductMeasure>> call, Response<List<ProductMeasure>> response) {
                if(response.isSuccessful()){
                    updateProgress();

                    List<ProductMeasure> dataList = response.body();
                    if(dataList.size() > 0){
                        for (ProductMeasure obj: dataList) {
                            ProductsMeasureController.getInstance(context).insertOrUpdate(obj);
                        }
                        CURRENT_PAGE++;
                        productMeasure();
                    }else{
                        CURRENT_INDEX++;
                        CURRENT_PAGE = 1;
                        loadData();
                    }

                }else{

                    listener.onError("Error loading data. Do you want to retry?");
                }
            }

            @Override
            public void onFailure(Call<List<ProductMeasure>> call, Throwable t) {

                listener.onError("Error loading data. Do you want to retry?");
            }
        });
    }

    private void productTypes(){

        //para probar error cambia el formato de la fecha
        apiInterface.getProductTypeUpdates(/*loginResponse.getUserDevice().getId()*/2,getDate(),CURRENT_PAGE,OFFSET).enqueue(new Callback<List<ProductType>>() {
            @Override
            public void onResponse(Call<List<ProductType>> call, Response<List<ProductType>> response) {
                if(response.isSuccessful()){
                    updateProgress();

                    List<ProductType> dataList = response.body();
                    if(dataList.size() > 0){
                        for (ProductType obj: dataList) {
                            ProductsTypesController.getInstance(context).insertOrUpdate(obj);
                        }
                        CURRENT_PAGE++;
                        productTypes();
                    }else{
                        CURRENT_INDEX++;
                        CURRENT_PAGE = 1;
                        loadData();
                    }

                }else{
                    listener.onError("Error loading data. Do you want to retry?");
                }
            }

            @Override
            public void onFailure(Call<List<ProductType>> call, Throwable t) {

                listener.onError("Error loading data. Do you want to retry?");
            }
        });
    }


    private void productSubTypes(){

        //para probar error cambia el formato de la fecha
        apiInterface.getProductSubTypeUpdates(/*loginResponse.getUserDevice().getId()*/2,getDate(),CURRENT_PAGE,OFFSET).enqueue(new Callback<List<ProductSubType>>() {
            @Override
            public void onResponse(Call<List<ProductSubType>> call, Response<List<ProductSubType>> response) {
                if(response.isSuccessful()){
                    updateProgress();

                    List<ProductSubType> dataList = response.body();
                    if(dataList.size() > 0){
                        for (ProductSubType obj: dataList) {
                            ProductsSubTypesController.getInstance(context).insertOrUpdate(obj);
                        }
                        CURRENT_PAGE++;
                        productSubTypes();
                    }else{
                        CURRENT_INDEX++;
                        CURRENT_PAGE = 1;
                        loadData();
                    }

                }else{
                    listener.onError("Error loading data. Do you want to retry?");
                }
            }

            @Override
            public void onFailure(Call<List<ProductSubType>> call, Throwable t) {

                listener.onError("Error loading data. Do you want to retry?");
            }
        });
    }


    private void tables(){

        //para probar error cambia el formato de la fecha
        apiInterface.getTableUpdates(/*loginResponse.getUserDevice().getId()*/2,getDate(),CURRENT_PAGE,OFFSET).enqueue(new Callback<List<Table>>() {
            @Override
            public void onResponse(Call<List<Table>> call, Response<List<Table>> response) {
                if(response.isSuccessful()){
                    updateProgress();

                    List<Table> dataList = response.body();
                    if(dataList.size() > 0){
                        for (Table obj: dataList) {
                            TableController.getInstance(context).insertOrUpdate(obj);
                        }
                        CURRENT_PAGE++;
                        tables();
                    }else{
                        CURRENT_INDEX++;
                        CURRENT_PAGE = 1;
                        loadData();
                    }

                }else{
                    listener.onError("Error loading data. Do you want to retry?");
                }
            }

            @Override
            public void onFailure(Call<List<Table>> call, Throwable t) {

                listener.onError("Error loading data. Do you want to retry?");
            }
        });
    }


    private void orders(){

        //para probar error cambia el formato de la fecha
        apiInterface.getOrderUpdates(/*loginResponse.getUserDevice().getId()*/2,getDate(),CURRENT_PAGE,OFFSET).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if(response.isSuccessful()){
                    updateProgress();

                    List<Order> dataList = response.body();
                    if(dataList.size() > 0){
                        for (Order obj: dataList) {
                            OrderController.getInstance(context).insertOrUpdate(obj);
                        }
                        CURRENT_PAGE++;
                        orders();
                    }else{
                        CURRENT_INDEX++;
                        CURRENT_PAGE = 1;
                        loadData();
                    }

                }else{
                    listener.onError("Error loading data. Do you want to retry?");
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {

                listener.onError("Error loading data. Do you want to retry?");
            }
        });
    }

    private void orderDetails(){

        //para probar error cambia el formato de la fecha
        apiInterface.getOrderDetailUpdates(/*loginResponse.getUserDevice().getId()*/2,getDate(),CURRENT_PAGE,OFFSET).enqueue(new Callback<List<OrderDetail>>() {
            @Override
            public void onResponse(Call<List<OrderDetail>> call, Response<List<OrderDetail>> response) {
                if(response.isSuccessful()){
                    updateProgress();

                    List<OrderDetail> dataList = response.body();
                    if(dataList.size() > 0){
                        for (OrderDetail obj: dataList) {
                            OrderController.getInstance(context).insertOrUpdate(obj);
                        }
                        CURRENT_PAGE++;
                        orderDetails();
                    }else{
                        CURRENT_INDEX++;
                        CURRENT_PAGE = 1;
                        loadData();
                    }

                }else{
                    listener.onError("Error loading data. Do you want to retry?");
                }
            }

            @Override
            public void onFailure(Call<List<OrderDetail>> call, Throwable t) {

                listener.onError("Error loading data. Do you want to retry?");
            }
        });
    }

    private void sales(){

        //para probar error cambia el formato de la fecha
        apiInterface.getSaleUpdates(/*loginResponse.getUserDevice().getId()*/2,getDate(),CURRENT_PAGE,OFFSET).enqueue(new Callback<List<Sale>>() {
            @Override
            public void onResponse(Call<List<Sale>> call, Response<List<Sale>> response) {
                if(response.isSuccessful()){
                    updateProgress();

                    List<Sale> dataList = response.body();
                    if(dataList.size() > 0){
                        for (Sale obj: dataList) {
                            SalesController.getInstance(context).insertOrUpdate(obj);
                        }
                        CURRENT_PAGE++;
                        sales();
                    }else{
                        CURRENT_INDEX++;
                        CURRENT_PAGE = 1;
                        loadData();
                    }

                }else{
                    listener.onError("Error loading data. Do you want to retry?");
                }
            }

            @Override
            public void onFailure(Call<List<Sale>> call, Throwable t) {

                listener.onError("Error loading data. Do you want to retry?");
            }
        });
    }



    private void salesDetails(){

        //para probar error cambia el formato de la fecha
        apiInterface.getSaleDetailUpdates(/*loginResponse.getUserDevice().getId()*/2,getDate(),CURRENT_PAGE,OFFSET).enqueue(new Callback<List<SaleDetail>>() {
            @Override
            public void onResponse(Call<List<SaleDetail>> call, Response<List<SaleDetail>> response) {
                if(response.isSuccessful()){
                    updateProgress();

                    List<SaleDetail> dataList = response.body();
                    if(dataList.size() > 0){
                        for (SaleDetail obj: dataList) {
                            SalesController.getInstance(context).insertOrUpdate(obj);
                        }
                        CURRENT_PAGE++;
                        salesDetails();
                    }else{
                        CURRENT_INDEX++;
                        CURRENT_PAGE = 1;
                        loadData();
                    }

                }else{
                    listener.onError("Error loading data. Do you want to retry?");
                }
            }

            @Override
            public void onFailure(Call<List<SaleDetail>> call, Throwable t) {

                listener.onError("Error loading data. Do you want to retry?");
            }
        });
    }


    private void receipts(){

        //para probar error cambia el formato de la fecha
        apiInterface.getReceiptUpdates(/*loginResponse.getUserDevice().getId()*/2,getDate(),CURRENT_PAGE,OFFSET).enqueue(new Callback<List<Receipt>>() {
            @Override
            public void onResponse(Call<List<Receipt>> call, Response<List<Receipt>> response) {
                if(response.isSuccessful()){
                    updateProgress();

                    List<Receipt> dataList = response.body();
                    if(dataList.size() > 0){
                        for (Receipt obj: dataList) {
                            ReceiptController.getInstance(context).insertOrUpdate(obj);
                        }
                        CURRENT_PAGE++;
                        receipts();
                    }else{
                        CURRENT_INDEX++;
                        CURRENT_PAGE = 1;
                        loadData();
                    }

                }else{
                    listener.onError("Error loading data. Do you want to retry?");
                }
            }

            @Override
            public void onFailure(Call<List<Receipt>> call, Throwable t) {

                listener.onError("Error loading data. Do you want to retry?");
            }
        });
    }


    private void payments(){

        //para probar error cambia el formato de la fecha
        apiInterface.getPaymentUpdates(/*loginResponse.getUserDevice().getId()*/2,getDate(),CURRENT_PAGE,OFFSET).enqueue(new Callback<List<Payment>>() {
            @Override
            public void onResponse(Call<List<Payment>> call, Response<List<Payment>> response) {
                if(response.isSuccessful()){
                    updateProgress();

                    List<Payment> dataList = response.body();
                    if(dataList.size() > 0){
                        for (Payment obj: dataList) {
                            PaymentController.getInstance(context).insertOrUpdate(obj);
                        }
                        CURRENT_PAGE++;
                        payments();
                    }else{
                        CURRENT_INDEX++;
                        CURRENT_PAGE = 1;
                        loadData();
                    }

                }else{
                    listener.onError("Error loading data. Do you want to retry?");
                }
            }

            @Override
            public void onFailure(Call<List<Payment>> call, Throwable t) {

                listener.onError("Error loading data. Do you want to retry?");
            }
        });
    }

    private void day(){

        //para probar error cambia el formato de la fecha
        apiInterface.getDayUpdates(/*loginResponse.getUserDevice().getId()*/2,getDate(),CURRENT_PAGE,OFFSET).enqueue(new Callback<List<Day>>() {
            @Override
            public void onResponse(Call<List<Day>> call, Response<List<Day>> response) {
                if(response.isSuccessful()){
                    updateProgress();

                    List<Day> dataList = response.body();
                    if(dataList.size() > 0){
                        for (Day obj: dataList) {
                            DayController.getInstance(context).insertOrUpdate(obj);
                        }
                        CURRENT_PAGE++;
                        day();
                    }else{
                        CURRENT_INDEX++;
                        CURRENT_PAGE = 1;
                        loadData();
                    }

                }else{
                    listener.onError("Error loading data. Do you want to retry?");
                }
            }

            @Override
            public void onFailure(Call<List<Day>> call, Throwable t) {

                listener.onError("Error loading data. Do you want to retry?");
            }
        });
    }


}
