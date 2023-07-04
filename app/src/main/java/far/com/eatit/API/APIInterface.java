package far.com.eatit.API;

import java.util.Date;
import java.util.List;

import far.com.eatit.API.models.Area;
import far.com.eatit.API.models.AreaDetail;
import far.com.eatit.API.models.Client;
import far.com.eatit.API.models.Company;
import far.com.eatit.API.models.Day;
import far.com.eatit.API.models.Device;
import far.com.eatit.API.models.License;
import far.com.eatit.API.models.LoginRequest;
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
import far.com.eatit.API.models.ResponseBase;
import far.com.eatit.API.models.Sale;
import far.com.eatit.API.models.SaleDetail;
import far.com.eatit.API.models.Table;
import far.com.eatit.API.models.User;
import far.com.eatit.API.models.UserDevice;
import far.com.eatit.API.models.UserRole;
import far.com.eatit.CloudFireStoreObjects.Devices;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface APIInterface {



    @GET("api/License")
    Call<License> getLicense(@Query("id") int id);
    @GET("api/License")
    Call<List<License>> getLicenses();
    @GET("api/License/GetUpdates")
    Call<List<License>> getLicenseUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);
    @POST("api/License")
    Call<ResponseBase> saveLicense(@Body License license);
    @PUT("api/License")
    Call<ResponseBase> updateLicense(@Body License license);



    @GET("api/Device")
    Call<Device> getDevice(@Query("id") int id);
    @GET("api/Device/Devices")
    Call<List<Device>> getDevices(@Query("idLicense") int idLicense);
    @GET("api/Device/UnnasignedDevicesToUser")
    Call<List<Device>> getUnnasignedDevicesToUser(@Query("idLicense") int idLicense);
    @GET("api/Device/GetUpdates")
    Call<List<Device>> getDeviceUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);
    @POST("api/Device")
    Call<ResponseBase> saveDevice(@Body Device device);
    @PUT("api/Device")
    Call<ResponseBase> updateDevice(@Body Device device);


    @GET("api/UserRole")
    Call<UserRole> getUserRole(@Query("id") int id);
    @GET("api/UserRole/UserRoles")
    Call<List<UserRole>> getUserRoles(@Query("idLicense") int idLicense);
    @GET("api/UserRole/GetUpdates")
    Call<List<UserRole>> getUserRolesUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom") String dateFrom, @Query("page") int page, @Query("offset") int offset);
    @POST("api/UserRole")
    Call<ResponseBase> saveUserRole(@Body UserRole userRole);
    @PUT("api/UserRole")
    Call<ResponseBase> updateUserRole(@Body UserRole userRole);


    @GET("api/User")
    Call<User> getUser(@Query("id") int id);
    @GET("api/User/Users")
    Call<List<User>> getUsers(@Query("idLicense") int idLicense);
    @GET("api/User/UnnasignedUsersToDevice")
    Call<List<User>> getUnnasignedUsersToDevice(@Query("idLicense") int idLicense);
    @GET("api/User/GetUpdates")
    Call<List<User>> getUserUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);
    @POST("api/User")
    Call<ResponseBase> saveUser(@Body User user);
    @POST("api/User/Login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
    @PUT("api/User")
    Call<ResponseBase> updateUser(@Body User user);



    @GET("api/Company")
    Call<User> getCompany(@Query("id") int id);
    @GET("api/Company/Companies")
    Call<List<Company>> getCompanies(@Query("idLicense") int idLicense);
    @GET("api/Company/GetUpdates")
    Call<List<Company>> getCompanyUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);
    @POST("api/Company")
    Call<ResponseBase> saveCompany(@Body Company company);
    @PUT("api/Company")
    Call<ResponseBase> updateCompany(@Body Company company);


    @GET("api/UserDevice")
    Call<UserDevice> getUserDevice(@Query("id") int id);
    @GET("api/UserDevice/UserDevices")
    Call<List<UserDevice>> getUserDevices(@Query("idLicense") int idLicense);
    @GET("api/UserDevice/GetUpdates")
    Call<List<UserDevice>> getUserDeviceUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);
    @POST("api/UserDevice")
    Call<ResponseBase> saveUserDevice(@Body UserDevice userDevice);
    @PUT("api/UserDevice")
    Call<ResponseBase> updateUserDevice(@Body UserDevice userDevice);




    @GET("api/ProductType")
    Call<ProductType> getProductType(@Query("id") int id);
    @GET("api/ProductType/ProductTypes")
    Call<List<ProductType>> getProductTypes(@Query("idLicense") int idLicense);
    @GET("api/ProductType/GetUpdates")
    Call<List<ProductType>> getProductTypeUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);
    @POST("api/ProductType")
    Call<ResponseBase> saveProductType(@Body ProductType productType);
    @PUT("api/ProductType")
    Call<ResponseBase> updateProductType(@Body ProductType productType);

    @GET("api/ProductSubType")
    Call<ProductSubType> getProductSubType(@Query("id") int id);
    @GET("api/ProductSubType/ProductSubTypes")
    Call<List<ProductSubType>> getProductSubTypes(@Query("idLicense") int idLicense, @Query("idProductType") int idProductType);
    @GET("api/ProductSubType/GetUpdates")
    Call<List<ProductSubType>> getProductSubTypeUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);
    @POST("api/ProductSubType")
    Call<ResponseBase> saveProductSubType(@Body ProductSubType productSubType);
    @PUT("api/ProductSubType")
    Call<ResponseBase> updateProductSubType(@Body ProductSubType productSubType);



    @GET("api/Table/GetUpdates")
    Call<List<Table>> getTableUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);



    @GET("api/MeasureUnit")
    Call<MeasureUnit> getMeasureUnit(@Query("id") int id);
    @GET("api/MeasureUnit/MeasureUnits")
    Call<List<MeasureUnit>> getMeasureUnits(@Query("idLicense") int idLicense);
    @GET("api/MeasureUnit/GetUpdates")
    Call<List<MeasureUnit>> getMeasureUnitUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String  dateFrom, @Query("page") int page, @Query("offset") int offset);
    @POST("api/MeasureUnit")
    Call<ResponseBase> saveMeasureUnit(@Body MeasureUnit measureUnit);
    @PUT("api/MeasureUnit")
    Call<ResponseBase> updateMeasureUnit(@Body MeasureUnit measureUnit);

    @GET("api/Product")
    Call<Product> getProduct(@Query("id") int id);
    @GET("api/Product/Products")
    Call<List<Product>> getProducts(@Query("idLicense") int idLicense, @Query("idProductType") int idProductType, @Query("idProductSubType") int idProductSubType);
    @GET("api/Product/GetUpdates")
    Call<List<Product>> getProductUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);
    @POST("api/Product")
    Call<ResponseBase> saveProduct(@Body Product product);
    @PUT("api/Product")
    Call<ResponseBase> updateProduct(@Body Product product);

    @GET("api/ProductMeasure")
    Call<ProductMeasure> getProductMeasure(@Query("id") int id);
    @GET("api/ProductMeasure/ProductMeasures")
    Call<List<ProductMeasure>> getProductMeasures(@Query("idProduct") int idProduct);
    @GET("api/ProductMeasure/GetUpdates")
    Call<List<ProductMeasure>> getProductMeasureUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);
    @POST("api/ProductMeasure")
    Call<ResponseBase> saveProductMeasure(@Body ProductMeasure productMeasure);
    @PUT("api/ProductMeasure")
    Call<ResponseBase> updateProductMeasure(@Body ProductMeasure productMeasure);


    @GET("api/Client/GetUpdates")
    Call<List<Client>> getClientUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);


    @GET("api/Area/GetUpdates")
    Call<List<Area>> getAreaUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);


    @GET("api/AreaDetail/GetUpdates")
    Call<List<AreaDetail>> getAreaDetailUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);


    @GET("api/Order/GetUpdates")
    Call<List<Order>> getOrderUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);


    @GET("api/OrderDetail/GetUpdates")
    Call<List<OrderDetail>> getOrderDetailUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);



    @GET("api/Sale/GetUpdates")
    Call<List<Sale>> getSaleUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);


    @GET("api/SaleDetail/GetUpdates")
    Call<List<SaleDetail>> getSaleDetailUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);


    @GET("api/Receipt/GetUpdates")
    Call<List<Receipt>> getReceiptUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);



    @GET("api/Payment/GetUpdates")
    Call<List<Payment>> getPaymentUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);


    @GET("api/Day/GetUpdates")
    Call<List<Day>> getDayUpdates(@Query("idUserDevice") int idUserDevice, @Query("dateFrom")String dateFrom, @Query("page") int page, @Query("offset") int offset);
}
