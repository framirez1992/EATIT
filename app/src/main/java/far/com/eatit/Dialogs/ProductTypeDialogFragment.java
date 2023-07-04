package far.com.eatit.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;


import far.com.eatit.API.APIClient;
import far.com.eatit.API.APIInterface;
import far.com.eatit.API.models.LoginResponse;
import far.com.eatit.API.models.ProductType;
import far.com.eatit.API.models.ResponseBase;
import far.com.eatit.Interfases.DialogCaller;
import far.com.eatit.Main;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public  class ProductTypeDialogFragment extends DialogFragment  {

    Main mainActivity;
    APIInterface apiInterface;
    DialogCaller dialogCaller;

    ProductTypeDialogFragment.ProductTypeDialogFragmentResponse dialogResponse;

    public ProductType tempObj;
    public String type;
    LoginResponse loginResponse;

    LinearLayout llSave;
    TextInputEditText etName;
    TextInputEditText etOrden;

    Runnable exitRunnable = new Runnable() {
        @Override
        public void run() {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialogCaller.dialogClosed(dialogResponse);
                    ProductTypeDialogFragment.this.dismiss();
                }
            });
        }
    };

    //ProductsTypesController productsTypesController;
    //ProductsTypesInvController productsTypesInvController;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static ProductTypeDialogFragment newInstance(Main mainActivity, String type, ProductType pt, DialogCaller dialogCaller) {

        ProductTypeDialogFragment f = new ProductTypeDialogFragment();
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
        //productsTypesController = ProductsTypesController.getInstance(getActivity());
        //productsTypesInvController = ProductsTypesInvController.getInstance(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loginResponse = Funciones.getLoginResponseData(mainActivity);
        apiInterface = APIClient.getClient(mainActivity).create(APIInterface.class);
        return inflater.inflate(R.layout.product_type_fragment_dialog, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etName);
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
        llSave = view.findViewById(R.id.llSave);
        etName = view.findViewById(R.id.etName);
        etOrden = view.findViewById(R.id.etOrden);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                    EditProductType();
                }
            }
        });

        if(tempObj != null){//EDIT
                setUpToEditProductType();
        }
    }

    public boolean validateProductType(){
        if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void Save(){
        if(validateProductType()) {
            SaveProductType();
        }

    }

    public void SaveProductType(){
        try {
            String code = Funciones.generateCode();
            String name = etName.getText().toString();
            int orden = etOrden.getText().toString().trim().equals("")?9999:Integer.parseInt(etOrden.getText().toString());
            ProductType pt = new ProductType(loginResponse.getLicense().id,code, name, orden);

            /*if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
                productsTypesController.sendToFireBase(pt);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                productsTypesInvController.sendToFireBase(pt);
            }*/

            mainActivity.showWaitingDialog();
            apiInterface.saveProductType(pt).enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                    ResponseBase rb = response.body();
                    if(response.isSuccessful()){
                        ProductType pt = (ProductType) rb.getData();
                        dialogResponse = new ProductTypeDialogFragment.ProductTypeDialogFragmentResponse(pt);
                        mainActivity.showSuccessActionDialog("Saved",exitRunnable);
                    }else{
                        String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                        dialogResponse = new ProductTypeDialogFragment.ProductTypeDialogFragmentResponse("99",message);
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                    }
                    mainActivity.dismissWaitingDialog();
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    dialogResponse = new ProductTypeDialogFragment.ProductTypeDialogFragmentResponse("99",t.getMessage());
                    mainActivity.showErrorDialogAutoClose(t.getMessage(), exitRunnable);
                    mainActivity.dismissWaitingDialog();
                }
            });

            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void EditProductType(){
        try {
            int orden = etOrden.getText().toString().trim().equals("")?9999:Integer.parseInt(etOrden.getText().toString());
            tempObj.setDescription(etName.getText().toString());
            tempObj.setPosition(orden);
            /*
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
                productsTypesController.sendToFireBase(tempObj);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                productsTypesInvController.sendToFireBase(tempObj);
            }*/

            mainActivity.showWaitingDialog();
            apiInterface.updateProductType(tempObj).enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                    ResponseBase rb = response.body();
                    if(response.isSuccessful()){
                        ProductType pt = (ProductType) rb.getData();
                        dialogResponse = new ProductTypeDialogFragment.ProductTypeDialogFragmentResponse(pt);
                        mainActivity.showSuccessActionDialog("Saved",exitRunnable);
                    }else{
                        String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                        dialogResponse = new ProductTypeDialogFragment.ProductTypeDialogFragmentResponse("99",message);
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                    }
                    mainActivity.dismissWaitingDialog();
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    dialogResponse = new ProductTypeDialogFragment.ProductTypeDialogFragmentResponse("99",t.getMessage());
                    mainActivity.showErrorDialogAutoClose(t.getMessage(), exitRunnable);
                    mainActivity.dismissWaitingDialog();
                }
            });
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }



    public void setUpToEditProductType(){
        etName.setText(tempObj.getDescription());
        etOrden.setText(tempObj.getPosition()+"");
    }


    public  class ProductTypeDialogFragmentResponse{
        private ProductType productType;
        private String responseCode;
        private String responseMessage;

        public ProductTypeDialogFragmentResponse(ProductType productType) {
            this.productType = productType;
            this.responseCode = "00";
            this.responseMessage = "success";
        }

        public ProductTypeDialogFragmentResponse(String responseCode, String responseMessage) {
            this.productType = null;
            this.responseCode = responseCode;
            this.responseMessage = responseMessage;
        }

        public ProductType getProductType() {
            return productType;
        }

        public void setProductType(ProductType productType) {
            this.productType = productType;
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
