package far.com.eatit.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import far.com.eatit.API.APIClient;
import far.com.eatit.API.APIInterface;
import far.com.eatit.API.models.LoginResponse;
import far.com.eatit.API.models.MeasureUnit;
import far.com.eatit.API.models.Product;
import far.com.eatit.API.models.ProductMeasure;
import far.com.eatit.API.models.ProductSubType;
import far.com.eatit.API.models.ProductType;
import far.com.eatit.API.models.ResponseBase;
import far.com.eatit.Adapters.EditSelectionRowAdapter;
import far.com.eatit.Adapters.Models.EditSelectionRowModel;
import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Adapters.Models.SimpleSeleccionRowModel;
import far.com.eatit.Adapters.SimpleSelectionRowAdapter;
import far.com.eatit.CloudFireStoreObjects.Products;
import far.com.eatit.CloudFireStoreObjects.ProductsMeasure;
import far.com.eatit.Controllers.MeasureUnitsController;
import far.com.eatit.Controllers.MeasureUnitsInvController;
import far.com.eatit.Controllers.ProductsController;
import far.com.eatit.Controllers.ProductsInvController;
import far.com.eatit.Controllers.ProductsMeasureController;
import far.com.eatit.Controllers.ProductsMeasureInvController;
import far.com.eatit.Controllers.ProductsSubTypesController;
import far.com.eatit.Controllers.ProductsSubTypesInvController;
import far.com.eatit.Controllers.ProductsTypesController;
import far.com.eatit.Controllers.ProductsTypesInvController;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Interfases.DialogCaller;
import far.com.eatit.Main;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsDialogfragment extends DialogFragment {

    Main mainActivity;
    APIInterface apiInterface;
    LoginResponse loginResponse;
    DialogCaller dialogCaller;
    ProductsDialogfragmentResponse dialogResponse;

    private Product tempObj;

    LinearLayout llSave;
    TextInputEditText etCode, etName;
    Spinner spnFamily, spnGroup;
    RecyclerView rvMeasures;
    LinearLayout llMeasureScreen, llMainScreen, llNext, llBack;

    List<ProductMeasure> productMeasures;
    List<MeasureUnit> measureUnits;
    ArrayList<EditSelectionRowModel> selected = new ArrayList<>() ;
    //boolean firstTime = true;
    boolean firstLoadProductType = true;
    boolean firstLoadProductSubType = true;
    String type;

    Dialog loadingDialg;
    Dialog errorDialog;

    Runnable exitRunnable = new Runnable() {
        @Override
        public void run() {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialogCaller.dialogClosed(dialogResponse);
                    ProductsDialogfragment.this.dismiss();
                }
            });
        }
    };


    public  static ProductsDialogfragment newInstance(Main mainActivity,String type,Product pt, DialogCaller dialogCaller) {
        ProductsDialogfragment f = new ProductsDialogfragment();
        f.mainActivity = mainActivity;
        f.dialogCaller = dialogCaller;
        f.type = type;
        f.tempObj = pt;

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(pt != null) {
            f.setArguments(args);
        }

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        loginResponse = Funciones.getLoginResponseData(mainActivity);
        apiInterface = APIClient.getClient(mainActivity).create(APIInterface.class);
        return inflater.inflate(R.layout.dialog_add_edit_product, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }


    public void init(View view){
        llMainScreen = view.findViewById(R.id.llMainScreen);
        llMeasureScreen = view.findViewById(R.id.llMeasureScreen);
        llNext = view.findViewById(R.id.llNext);
        llBack = view.findViewById(R.id.llBack);
        llSave = view.findViewById(R.id.llSave);
        etCode = view.findViewById(R.id.etCode);
        etName = view.findViewById(R.id.etName);
        spnFamily = view.findViewById(R.id.spnFamilia);
        spnGroup = view.findViewById(R.id.spnGrupo);
        rvMeasures = view.findViewById(R.id.rvMeasures);
        rvMeasures.setLayoutManager(new LinearLayoutManager(getActivity()));

        /*
        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            ProductsTypesController.getInstance(getActivity()).fillSpinner(spnFamily, false);
            ProductsSubTypesController.getInstance(getActivity()).fillSpinner(spnGroup, false);
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            ProductsTypesInvController.getInstance(getActivity()).fillSpinner(spnFamily, false);
            ProductsSubTypesInvController.getInstance(getActivity()).fillSpinner(spnGroup, false);
        }*/

        etCode.setEnabled(false);
        etCode.setText(Funciones.generateCode());

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                selected = ((EditSelectionRowAdapter)rvMeasures.getAdapter()).getSelectedObjects();
                if(tempObj == null){
                    Save();
                }else{
                    //showLoadingDialog();
                    //searchProduct(String.valueOf(tempObj.getId()));
                    EditProduct();
                }
                //Funciones.getDateOnline(ProductTypeDialogFragment.this);
            }
        });

        llNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llMainScreen.setVisibility((llMainScreen.getVisibility() == View.VISIBLE)?View.GONE:View.VISIBLE);
                llMeasureScreen.setVisibility((llMeasureScreen.getVisibility() == View.GONE)?View.VISIBLE:View.GONE);
            }
        });

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llMainScreen.setVisibility((llMainScreen.getVisibility() == View.GONE)?View.VISIBLE:View.GONE);
                llMeasureScreen.setVisibility((llMeasureScreen.getVisibility() == View.VISIBLE)?View.GONE:View.VISIBLE);
            }
        });

        if(tempObj != null){//EDIT
            setUpToEditUsers();
        }



        spnFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV familia = (KV)spnFamily.getSelectedItem();
                /*if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                    ProductsSubTypesController.getInstance(getActivity()).fillSpinner(spnGroup, false, familia.getKey());
                }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                    ProductsSubTypesInvController.getInstance(getActivity()).fillSpinner(spnGroup, false, familia.getKey());
                }*/

                if(firstLoadProductType){//para que seleccione el subType del producto automaticamente la primera vez que abra el dialogo.
                    firstLoadProductType= false;
                    if(tempObj != null){
                        setSpinnerposition(spnFamily, String.valueOf(tempObj.getIdproductType()));
                    }
                }

                fillProductSubTypes(Integer.parseInt(familia.getKey()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV group = (KV)spnGroup.getSelectedItem();
                /*if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                    ProductsSubTypesController.getInstance(getActivity()).fillSpinner(spnGroup, false, familia.getKey());
                }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                    ProductsSubTypesInvController.getInstance(getActivity()).fillSpinner(spnGroup, false, familia.getKey());
                }*/

                if(firstLoadProductSubType){//para que seleccione el subType del producto automaticamente la primera vez que abra el dialogo.
                    firstLoadProductSubType= false;
                    if(tempObj != null){
                        setSpinnerposition(spnGroup, String.valueOf(tempObj.getIdproductSubType()));
                    }

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        fillProductTypes();
        fillUnitMeasure();
    }

    public boolean validate(){
        if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre.", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(spnFamily.getSelectedItem()== null){
            Snackbar.make(getView(), "Seleccione una familia.", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(spnGroup.getSelectedItem()== null){
            Snackbar.make(getView(), "Seleccione un grupo.", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(selected.size() == 0){
            Snackbar.make(getView(), "Seleccione por lo menos 1 unidad de medida", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    public void Save(){

        if(validate()) {
            SaveProduct();
        }else{
            llSave.setEnabled(true);
        }
    }

    public void SaveProduct(){
        try {
            String code = etCode.getText().toString();
            String description = etName.getText().toString();
            String productType = ((KV)spnFamily.getSelectedItem()).getKey();
            String productSubType = ((KV)spnGroup.getSelectedItem()).getKey();
            //int idLicense, int idproductType, int idproductSubType, String code, String description, boolean combo
            Product product = new Product(loginResponse.getLicense().getId(),Integer.parseInt(productType), Integer.parseInt(productSubType),code,description, false);

            ArrayList<ProductMeasure> list = new ArrayList<>();
            for(EditSelectionRowModel ssrm: selected){
                MeasureUnit mu = (MeasureUnit)ssrm.getEntity();
                //int idproduct, int idmeasureUnit, double price, double maxPrice, double minPrice, boolean enabled, boolean range
                list.add(new ProductMeasure(0, mu.getId(), Double.parseDouble(ssrm.getText()),0,0, ssrm.isChecked(), false));
            }
            product.setProductMeasures(list);

            /*
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                productsController.sendToFireBase(product, list);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                productsInvController.sendToFireBase(product, list);
            }*/


            apiInterface.saveProduct(product).enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                    ResponseBase rb = response.body();
                    if(response.isSuccessful()){
                        Product pt = (Product) rb.getData();
                        dialogResponse = new ProductsDialogfragmentResponse(pt);
                        mainActivity.showSuccessActionDialog("Saved",exitRunnable);
                    }else{
                        String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                        dialogResponse = new ProductsDialogfragmentResponse("99",message);
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                    }
                    mainActivity.dismissWaitingDialog();
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    dialogResponse = new ProductsDialogfragmentResponse("99",t.getMessage());
                    mainActivity.showErrorDialogAutoClose(t.getMessage(), exitRunnable);
                    mainActivity.dismissWaitingDialog();
                }
            });


            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void EditProduct(){
        try {
            Product product = ((Product)tempObj);
            product.setDescription(etName.getText().toString());
            product.setIdproductType(Integer.parseInt(((KV)spnFamily.getSelectedItem()).getKey()));
            product.setIdproductSubType(Integer.parseInt(((KV)spnGroup.getSelectedItem()).getKey()));
            //products.setMDATE(null);

            ArrayList<ProductMeasure> list = new ArrayList<>();
            list.addAll(productMeasures);//agregamos todos los productMeasure existentes

            for(EditSelectionRowModel ssrm: selected){ //iterar sobra las unidades de medidas selecionadas
                MeasureUnit mu = (MeasureUnit)ssrm.getEntity();
                ProductMeasure pm = null;

                for(ProductMeasure obj : list){//itera sobre los productMeasureExistentes
                    if(mu.getId() == obj.getIdmeasureUnit()){//modificar al product measure
                        pm = obj;
                        break;
                    }
                }
                if(pm == null){// si el pm == null significa que 1- no existe en los producMeasure  hay que agregarlo
                    list.add(new ProductMeasure(tempObj.getId(), mu.getId(), Double.parseDouble(ssrm.getText()),0,0, ssrm.isChecked(), false));
                }else{//si existe de modifica el precio y si esta checked
                    pm.setPrice(Double.parseDouble(ssrm.getText()));
                    pm.setEnabled(ssrm.isChecked());
                }

            }


            //Escenario se esta editando un producto con una unidad de media, se inactiva la unidad de medida actual y se activa una nueva
            for(ProductMeasure pm : list){//encontrar los inactivos y cambiar su status
                boolean deselect = true;
                for(EditSelectionRowModel ssrm: selected){
                    MeasureUnit mu = (MeasureUnit)ssrm.getEntity();
                    if(pm.getIdmeasureUnit() == mu.getId()){//existe pero lo deseleccionaron
                       deselect = false;
                        break;
                    }
                }

                if(deselect){
                    pm.setEnabled(false);
                }
            }
            product.setProductMeasures(list);


            apiInterface.updateProduct(product).enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                    ResponseBase rb = response.body();
                    if(response.isSuccessful()){
                        Product pt = (Product) rb.getData();
                        dialogResponse = new ProductsDialogfragmentResponse(pt);
                        mainActivity.showSuccessActionDialog("Saved",exitRunnable);
                    }else{
                        String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                        dialogResponse = new ProductsDialogfragmentResponse("99",message);
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                    }
                    mainActivity.dismissWaitingDialog();
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    dialogResponse = new ProductsDialogfragmentResponse("99",t.getMessage());
                    mainActivity.showErrorDialogAutoClose(t.getMessage(), exitRunnable);
                    mainActivity.dismissWaitingDialog();
                }
            });
            /*
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                productsController.sendToFireBase(products, list);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                productsInvController.sendToFireBase(products, list);
            }*/

            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void setUpToEditUsers(){
        Product p = ((Product)tempObj);
        etCode.setText(p.getCode());
        etCode.setEnabled(false);
        etName.setText(p.getDescription());

    }

    public void setSpinnerposition(Spinner spn, String key){
        for(int i = 0; i< spn.getAdapter().getCount(); i++){
            if(((KV)spn.getAdapter().getItem(i)).getKey().equals(key)){
                spn.setSelection(i);
                break;
            }
        }
    }





    OnFailureListener onFailureSerachProduct = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
        showErrorDialog(e.getMessage());
        llSave.setEnabled(true);
        closeLoadingDialog();
        }
    };

    OnSuccessListener<QuerySnapshot> onSuccessSeachProduct = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {

            if(querySnapshot != null && querySnapshot.getDocuments().size() >0){
            showErrorDialog("No puede editar productos que esten actualmente en ventas (Abiertas o Entregadas)");
            }else{
                EditProduct();
            }

            llSave.setEnabled(true);
            closeLoadingDialog();
        }
    };

    /**
     * validamos que el producto a editar no este en una venta (Abierta, entregada, anulada) es decir no historica
     * @param codeProduct
     */
    public void searchProduct(String codeProduct){
        //SalesController.getInstance(getActivity()).searchProductInSalesDetail(codeProduct,onSuccessSeachProduct, onFailureSerachProduct);
    }

    public void showLoadingDialog(){
        loadingDialg = null;
        loadingDialg = Funciones.getLoadingDialog(getActivity(),"Loading...");
        loadingDialg.show();
    }

    public void closeLoadingDialog(){
        if(loadingDialg != null){
            loadingDialg.dismiss();
        }
    }

    public void showErrorDialog(String msg){
        errorDialog = null;
        errorDialog = Funciones.getCustomDialog(getActivity(), "Error", msg, R.drawable.ic_error_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDialog.dismiss();
                errorDialog = null;
            }
        });
        errorDialog.setCancelable(false);
        errorDialog.show();
    }

    private void fillProductTypes(){
        apiInterface.getProductTypes(loginResponse.getLicense().getId()).enqueue(new Callback<List<ProductType>>() {
            @Override
            public void onResponse(Call<List<ProductType>> call, Response<List<ProductType>> response) {
                ArrayList<KV> lrm = new ArrayList<>();

                if(response.isSuccessful()){
                    List<ProductType> list = response.body();
                    for(ProductType obj : list){
                        //String id, String text, Object entity
                        lrm.add(new KV(String.valueOf(obj.getId()),obj.getDescription()));
                    }
                }else{
                    Snackbar.make(getView(),response.errorBody().byteStream().toString(), BaseTransientBottomBar.LENGTH_LONG).show();
                }

                ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(mainActivity, android.R.layout.simple_list_item_1,lrm);
                spnFamily.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<ProductType>> call, Throwable t) {
                Snackbar.make(getView(),t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

    }

    private void fillProductSubTypes(int idProductType){
        apiInterface.getProductSubTypes(loginResponse.getLicense().getId(), idProductType).enqueue(new Callback<List<ProductSubType>>() {
            @Override
            public void onResponse(Call<List<ProductSubType>> call, Response<List<ProductSubType>> response) {
                ArrayList<KV> lrm = new ArrayList<>();

                if(response.isSuccessful()){
                    List<ProductSubType> list = response.body();
                    for(ProductSubType obj : list){
                        //String id, String text, Object entity
                        lrm.add(new KV(String.valueOf(obj.getId()),obj.getDescription()));
                    }
                }else{
                    Snackbar.make(getView(),response.errorBody().byteStream().toString(), BaseTransientBottomBar.LENGTH_LONG).show();
                }

                ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(mainActivity, android.R.layout.simple_list_item_1,lrm);
                spnGroup.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<ProductSubType>> call, Throwable t) {
                Snackbar.make(getView(),t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

    }

    public void fillUnitMeasure(){
        apiInterface.getMeasureUnits(loginResponse.getLicense().getId()).enqueue(new Callback<List<MeasureUnit>>() {
            @Override
            public void onResponse(Call<List<MeasureUnit>> call, Response<List<MeasureUnit>> response) {
                ArrayList<EditSelectionRowModel> arr = new ArrayList<>();

                if(response.isSuccessful()){
                    measureUnits = response.body();
                    for(MeasureUnit obj : measureUnits){
                        //String code, String description, String text, boolean checked,
                        arr.add(new EditSelectionRowModel(String.valueOf(obj.getId()),obj.getDescription(),"0",false,obj));
                    }
                }else{
                    Snackbar.make(getView(),response.errorBody().byteStream().toString(), BaseTransientBottomBar.LENGTH_LONG).show();
                }


                if(tempObj != null && productMeasures == null  ) {
                    searchProductMeasures();
                }else{
                    fillUnitMeasureAdapter(arr);
                }

            }

            @Override
            public void onFailure(Call<List<MeasureUnit>> call, Throwable t) {
                Snackbar.make(getView(),t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

    }


    private void fillUnitMeasureAdapter(ArrayList<EditSelectionRowModel> arr){
        rvMeasures.setAdapter(new EditSelectionRowAdapter(getActivity(),arr, selected));
        rvMeasures.getAdapter().notifyDataSetChanged();
        rvMeasures.invalidate();
    }

    public void searchProductMeasures(){
        apiInterface.getProductMeasures(tempObj.getId()).enqueue(new Callback<List<ProductMeasure>>() {
            @Override
            public void onResponse(Call<List<ProductMeasure>> call, Response<List<ProductMeasure>> response) {
                mainActivity.dismissWaitingDialog();

                if(response.isSuccessful()){
                    productMeasures = response.body();
                    loadSelectedMeasures();
                }else{
                    Snackbar.make(getView(),response.errorBody().byteStream().toString(), BaseTransientBottomBar.LENGTH_LONG).show();
                }


            }

            @Override
            public void onFailure(Call<List<ProductMeasure>> call, Throwable t) {
                Snackbar.make(getView(),t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                mainActivity.dismissWaitingDialog();
            }
        });

    }

    private void loadSelectedMeasures(){
        ArrayList<EditSelectionRowModel> adapterData = new ArrayList<>();

        for(MeasureUnit obj : measureUnits){

            ProductMeasure p = null;
            for(ProductMeasure pm : productMeasures){
                if(pm.getIdmeasureUnit() == obj.getId()){
                    p = pm;
                    break;
                }
            }

            EditSelectionRowModel esm = new EditSelectionRowModel(String.valueOf(obj.getId()),obj.getDescription(),p==null? "0":String.valueOf(p.getPrice()),(p!=null && p.isEnabled()),obj);
            if(p != null && p.isEnabled()){
                selected.add(esm);
            }
            adapterData.add(esm);
        }

        fillUnitMeasureAdapter(adapterData);
    }



    public  class ProductsDialogfragmentResponse{
        private Product product;
        private String responseCode;
        private String responseMessage;

        public ProductsDialogfragmentResponse(Product product) {
            this.product = product;
            this.responseCode = "00";
            this.responseMessage = "success";
        }

        public ProductsDialogfragmentResponse(String responseCode, String responseMessage) {
            this.product = null;
            this.responseCode = responseCode;
            this.responseMessage = responseMessage;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public String getResponseCode() {
            return responseCode;
        }

        public void setResponseCode(String responseCode) {
            this.responseCode = responseCode;
        }

        public String getResponseMessage() {
            return responseMessage;
        }

        public void setResponseMessage(String responseMessage) {
            this.responseMessage = responseMessage;
        }
    }
}
