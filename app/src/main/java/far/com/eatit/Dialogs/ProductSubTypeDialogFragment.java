package far.com.eatit.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;

import far.com.eatit.API.APIClient;
import far.com.eatit.API.APIInterface;
import far.com.eatit.API.models.LoginResponse;
import far.com.eatit.API.models.ProductSubType;
import far.com.eatit.API.models.ProductType;
import far.com.eatit.API.models.ResponseBase;
import far.com.eatit.CloudFireStoreObjects.ProductsSubTypes;
import far.com.eatit.Controllers.ProductsTypesController;
import far.com.eatit.Controllers.ProductsTypesInvController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Interfases.DialogCaller;
import far.com.eatit.Main;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductSubTypeDialogFragment extends DialogFragment implements OnFailureListener {

    Main mainActivity;
    APIInterface apiInterface;
    DialogCaller dialogCaller;
    LoginResponse loginResponse;

    SpinnerAdapter spnFamilyAdapter;
    ProductSubType tempObj;
    LinearLayout llFamilia;
    Spinner spnFamilia;
    LinearLayout llSave;
    TextInputEditText etName, etOrden;
    String type;

    ProductSubTypeDialogFragmentResponse dialogResponse;
    Runnable exitRunnable = new Runnable() {
        @Override
        public void run() {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialogCaller.dialogClosed(dialogResponse);
                    ProductSubTypeDialogFragment.this.dismiss();
                }
            });
        }
    };

    //ProductsSubTypesController productsSubTypesController;
    //ProductsSubTypesInvController productsSubTypesInvController;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static ProductSubTypeDialogFragment newInstance(Main mainActivity, SpinnerAdapter spnFamilyAdapter, String type, ProductSubType pt, DialogCaller dialogCaller) {

        ProductSubTypeDialogFragment f = new ProductSubTypeDialogFragment();
        f.mainActivity = mainActivity;
        f.spnFamilyAdapter = spnFamilyAdapter;
        f.dialogCaller = dialogCaller;
        f.tempObj = pt;
        f.type = type;

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
        //productsSubTypesController = ProductsSubTypesController.getInstance(getActivity());
        //productsSubTypesInvController = ProductsSubTypesInvController.getInstance(getActivity());

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
        return inflater.inflate(R.layout.dialog_spn_save, container, true);
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
        llFamilia = view.findViewById(R.id.llFamilia);
        spnFamilia = view.findViewById(R.id.spnFamilia);
        llSave = view.findViewById(R.id.llSave);
        etName = view.findViewById(R.id.etName);
        etOrden = view.findViewById(R.id.etOrden);
        view.findViewById(R.id.tilOrden).setVisibility(View.VISIBLE);


        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            ProductsTypesController.getInstance(getActivity()).fillSpinner(spnFamilia, false);
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            ProductsTypesInvController.getInstance(getActivity()).fillSpinner(spnFamilia, false);
        }

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                    EditProductSubType();
                }
            }
        });


        spnFamilia.setAdapter(spnFamilyAdapter);

        if(tempObj != null) {//EDIT
            prepareForProductSubType();
        }
    }


    public boolean validateProductSubType(){
        if(spnFamilia.getSelectedItem()== null){
            Snackbar.make(getView(), "Seleccione una familia", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void Save(){
            if(validateProductSubType()){
                SaveProductSubType();
            }else{
                llSave.setEnabled(true);
            }

    }

    public void SaveProductSubType(){
        try {
            String code = Funciones.generateCode();
            String name = etName.getText().toString();
            String idProductType = ((KV)spnFamilia.getSelectedItem()).getKey();
            int orden = (etOrden.getText().toString().trim().equals(""))?9999:Integer.parseInt(etOrden.getText().toString());
            ProductSubType pst = new ProductSubType(Integer.parseInt(idProductType),code,name, orden);

            /*if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                productsSubTypesController.sendToFireBase(pst);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                productsSubTypesInvController.sendToFireBase(pst);
            }*/

            mainActivity.showWaitingDialog();
            apiInterface.saveProductSubType(pst).enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                    ResponseBase rb = response.body();
                    if(response.isSuccessful()){
                        ProductSubType pt = (ProductSubType) rb.getData();
                        dialogResponse = new ProductSubTypeDialogFragment.ProductSubTypeDialogFragmentResponse(pt);
                        mainActivity.showSuccessActionDialog("Saved",exitRunnable);
                    }else{
                        String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                        dialogResponse = new ProductSubTypeDialogFragment.ProductSubTypeDialogFragmentResponse("99",message);
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                    }
                    mainActivity.dismissWaitingDialog();
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    dialogResponse = new ProductSubTypeDialogFragment.ProductSubTypeDialogFragmentResponse("99",t.getMessage());
                    mainActivity.showErrorDialogAutoClose(t.getMessage(), exitRunnable);
                    mainActivity.dismissWaitingDialog();
                }
            });

            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void EditProductSubType(){
        try {
            ProductSubType pst = tempObj;
            int orden = (etOrden.getText().toString().trim().equals(""))?9999:Integer.parseInt(etOrden.getText().toString());
            pst.setDescription(etName.getText().toString());
            pst.setIdproductType(Integer.parseInt(((KV)spnFamilia.getSelectedItem()).getKey()));
            pst.setPosition(orden);

            /*if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            productsSubTypesController.sendToFireBase(pst);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
             productsSubTypesInvController.sendToFireBase(pst);
            }*/


            mainActivity.showWaitingDialog();
            apiInterface.updateProductSubType(tempObj).enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                    ResponseBase rb = response.body();
                    if(response.isSuccessful()){
                        ProductSubType pt = (ProductSubType) rb.getData();
                        dialogResponse = new ProductSubTypeDialogFragment.ProductSubTypeDialogFragmentResponse(pt);
                        mainActivity.showSuccessActionDialog("Saved",exitRunnable);
                    }else{
                        String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                        dialogResponse = new ProductSubTypeDialogFragment.ProductSubTypeDialogFragmentResponse("99",message);
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                    }
                    mainActivity.dismissWaitingDialog();
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    dialogResponse = new ProductSubTypeDialogFragment.ProductSubTypeDialogFragmentResponse("99",t.getMessage());
                    mainActivity.showErrorDialogAutoClose(t.getMessage(), exitRunnable);
                    mainActivity.dismissWaitingDialog();
                }
            });
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void prepareForProductSubType(){
        setFamilia();
        etName.setText(tempObj.getDescription());
        etOrden.setText(tempObj.getPosition()+"");
    }
    public void setFamilia(){
        for(int i = 0; i< spnFamilia.getAdapter().getCount(); i++){
            if(((KV)spnFamilia.getAdapter().getItem(i)).getKey().equals(String.valueOf(tempObj.getIdproductType()))){
                spnFamilia.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }


    public  class ProductSubTypeDialogFragmentResponse{
        private ProductSubType productSubType;
        private String responseCode;
        private String responseMessage;

        public ProductSubTypeDialogFragmentResponse(ProductSubType productSubType) {
            this.productSubType = productSubType;
            this.responseCode = "00";
            this.responseMessage = "success";
        }

        public ProductSubTypeDialogFragmentResponse(String responseCode, String responseMessage) {
            this.productSubType = null;
            this.responseCode = responseCode;
            this.responseMessage = responseMessage;
        }

        public ProductSubType getProductSubType() {
            return productSubType;
        }

        public void setProductSubType(ProductSubType productSubType) {
            this.productSubType = productSubType;
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
