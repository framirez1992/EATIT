package far.com.eatit.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import far.com.eatit.API.models.MeasureUnit;
import far.com.eatit.API.models.ProductSubType;
import far.com.eatit.API.models.ProductType;
import far.com.eatit.API.models.ResponseBase;
import far.com.eatit.CloudFireStoreObjects.MeasureUnits;
import far.com.eatit.Controllers.MeasureUnitsController;
import far.com.eatit.Controllers.MeasureUnitsInvController;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Interfases.DialogCaller;
import far.com.eatit.Main;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeasureUnitDialogFragment extends DialogFragment {

    Main mainActivity;
    DialogCaller dialogCaller;
    APIInterface apiInterface;

    private MeasureUnit tempObj;
    LoginResponse loginResponse;

    LinearLayout llSave;
    TextInputEditText etName;

    //MeasureUnitsController measureUnitsController;
    //MeasureUnitsInvController measureUnitsInvController;
    String type;

    MeasureUnitDialogFragment.MeasureUnitDialogFragmentResponse dialogResponse;
    Runnable exitRunnable = new Runnable() {
        @Override
        public void run() {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialogCaller.dialogClosed(dialogResponse);
                    MeasureUnitDialogFragment.this.dismiss();
                }
            });
        }
    };

    public  static MeasureUnitDialogFragment newInstance(Main mainActivity,String type, MeasureUnit pt, DialogCaller dialogCaller) {

        MeasureUnitDialogFragment f = new MeasureUnitDialogFragment();
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
        return inflater.inflate(R.layout.product_type_fragment_dialog, container, true);
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
        llSave = view.findViewById(R.id.llSave);
        etName = view.findViewById(R.id.etName);
        ((EditText)view.findViewById(R.id.etOrden)).setVisibility(View.GONE);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                   EditMeasureUnit();
                }
            }
        });

        if(tempObj != null){//EDIT
                setUpToEditMeasureUnits();
        }
    }

    public boolean validate(){
        if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void Save(){

            if(validate()) {
                SaveMeasureUnit();
            }else{
                llSave.setEnabled(true);
            }
    }

    public void SaveMeasureUnit(){
        try {
            String code =Funciones.generateCode();
            String name = etName.getText().toString();
            MeasureUnit mu = new MeasureUnit(loginResponse.getLicense().getId(),code, name);
            /*
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                measureUnitsController.sendToFireBase(pt);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                measureUnitsInvController.sendToFireBase(pt);
            }*/

            mainActivity.showWaitingDialog();
            apiInterface.saveMeasureUnit(mu).enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                    ResponseBase rb = response.body();
                    if(response.isSuccessful()){
                        MeasureUnit pt = (MeasureUnit) rb.getData();
                        dialogResponse = new MeasureUnitDialogFragmentResponse(pt);
                        mainActivity.showSuccessActionDialog("Saved",exitRunnable);
                    }else{
                        String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                        dialogResponse = new MeasureUnitDialogFragmentResponse("99",message);
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                    }
                    mainActivity.dismissWaitingDialog();
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    dialogResponse = new MeasureUnitDialogFragmentResponse("99",t.getMessage());
                    mainActivity.showErrorDialogAutoClose(t.getMessage(), exitRunnable);
                    mainActivity.dismissWaitingDialog();
                }
            });


            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void EditMeasureUnit(){
        try {
            MeasureUnit mu = ((MeasureUnit)tempObj);
            mu.setDescription(etName.getText().toString());

            /*if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                measureUnitsController.sendToFireBase(mu);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                measureUnitsInvController.sendToFireBase(mu);
            }*/

            mainActivity.showWaitingDialog();
            apiInterface.updateMeasureUnit(mu).enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                    ResponseBase rb = response.body();
                    if(response.isSuccessful()){
                        MeasureUnit pt = (MeasureUnit) rb.getData();
                        dialogResponse = new MeasureUnitDialogFragmentResponse(pt);
                        mainActivity.showSuccessActionDialog("Saved",exitRunnable);
                    }else{
                        String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                        dialogResponse = new MeasureUnitDialogFragmentResponse("99",message);
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                    }
                    mainActivity.dismissWaitingDialog();
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    dialogResponse = new MeasureUnitDialogFragmentResponse("99",t.getMessage());
                    mainActivity.showErrorDialogAutoClose(t.getMessage(), exitRunnable);
                    mainActivity.dismissWaitingDialog();
                }
            });

            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void setUpToEditMeasureUnits(){
        etName.setText(((MeasureUnit)tempObj).getDescription());

    }


    public  class MeasureUnitDialogFragmentResponse{
        private MeasureUnit measureUnit;
        private String responseCode;
        private String responseMessage;

        public MeasureUnitDialogFragmentResponse(MeasureUnit measureUnit) {
            this.measureUnit = measureUnit;
            this.responseCode = "00";
            this.responseMessage = "success";
        }

        public MeasureUnitDialogFragmentResponse(String responseCode, String responseMessage) {
            this.measureUnit = null;
            this.responseCode = responseCode;
            this.responseMessage = responseMessage;
        }

        public MeasureUnit getMeasureUnit() {
            return measureUnit;
        }

        public void setMeasureUnit(MeasureUnit measureUnit) {
            this.measureUnit = measureUnit;
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
