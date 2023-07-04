package far.com.eatit.Dialogs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import far.com.eatit.API.APIClient;
import far.com.eatit.API.APIInterface;
import far.com.eatit.API.models.License;
//import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.API.models.ResponseBase;
import far.com.eatit.Controllers.LicenseController;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Interfases.DialogCaller;
import far.com.eatit.Main;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LicenseDialogFragment extends DialogFragment implements OnFailureListener {

    Main mainActivity;
    DialogCaller dialogCaller;
    APIInterface apiInterface;
    License tempObj;
    public String type;

    LicenseDialogFragmentResponse dialogResponse;

    LinearLayout llSave;
    TextInputEditText etCode, etClient;
    CheckBox cbEnabled;
    Runnable exitRunnable = new Runnable() {
        @Override
        public void run() {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialogCaller.dialogClosed(dialogResponse);
                    LicenseDialogFragment.this.dismiss();
                }
            });
        }
    };


    LicenseController licenseController;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static LicenseDialogFragment newInstance(Main mainActivity, DialogCaller dialogCaller, License l) {

        LicenseDialogFragment f = new LicenseDialogFragment();
        f.mainActivity = mainActivity;
        f.dialogCaller = dialogCaller;
        f.tempObj = l;

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(l != null) {
            f.setArguments(args);
        }

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
        licenseController = LicenseController.getInstance(getActivity());
        apiInterface = APIClient.getClient(mainActivity).create(APIInterface.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_edit_license, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etClient);
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
        etCode = view.findViewById(R.id.etCode);
        etClient = view.findViewById(R.id.etClient);
        cbEnabled = view.findViewById(R.id.cbEnabled);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                    EditLicense();
                }
            }
        });



        etCode.setText(Funciones.generateCode());

        if(tempObj != null){//EDIT
            setUpToEditProductType();
        }
    }

    public boolean validateProductType(){
        if(etClient.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void Save(){
        if(validateProductType()) {
            SaveLicense();
        }

        llSave.setEnabled(true);

    }

    public void SaveLicense(){
        try {
            String code = etCode.getText().toString();
            String client = etClient.getText().toString();
            License license = new License(code,client,cbEnabled.isChecked());

            mainActivity.showWaitingDialog();
            apiInterface.saveLicense(license).enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                    ResponseBase rb = response.body();
                    if(response.isSuccessful()){
                        License l = (License) rb.getData();
                        dialogResponse = new LicenseDialogFragmentResponse(l);
                        mainActivity.showSuccessActionDialog("Saved",exitRunnable);
                    }else{
                        String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                        dialogResponse = new LicenseDialogFragmentResponse("99",message);
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                    }
                    mainActivity.dismissWaitingDialog();
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    dialogResponse = new LicenseDialogFragmentResponse("99",t.getMessage());
                    mainActivity.showErrorDialogAutoClose(t.getMessage(), exitRunnable);
                    mainActivity.dismissWaitingDialog();
                }
            });


        }catch(Exception e){
            e.printStackTrace();
        }


    }



    public void EditLicense(){
        try {
            String code = etCode.getText().toString();
            String client = etClient.getText().toString();

            tempObj.setClientName(client);
            tempObj.setEnabled(cbEnabled.isChecked());

            mainActivity.showWaitingDialog();
            apiInterface.updateLicense(tempObj).enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {

                    ResponseBase rb = response.body();
                    if(response.isSuccessful()){
                        tempObj = (License) rb.getData();
                        dialogCaller.dialogClosed(new LicenseDialogFragmentResponse(tempObj));
                        mainActivity.showSuccessActionDialog("Updated",exitRunnable);
                    }else{
                        String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                        dialogCaller.dialogClosed(new LicenseDialogFragmentResponse("99",message));
                        mainActivity.showErrorDialog("Error",message);
                    }
                    mainActivity.dismissWaitingDialog();
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    mainActivity.showErrorDialogAutoClose(t.getMessage(), exitRunnable);
                    dialogCaller.dialogClosed(new LicenseDialogFragmentResponse("99",t.getMessage()));
                    mainActivity.dismissWaitingDialog();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }


    }



    public void setUpToEditProductType(){
        etCode.setText(tempObj.getCode());
        etClient.setText(tempObj.getClientName());
        cbEnabled.setChecked(tempObj.isEnabled());

    }



    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }


    public  class LicenseDialogFragmentResponse{
        private License license;
        private String responseCode;
        private String responseMessage;

        public LicenseDialogFragmentResponse(License license) {
            this.license = license;
            this.responseCode = "00";
            this.responseMessage = "success";
        }

        public LicenseDialogFragmentResponse(String responseCode, String responseMessage) {
            this.license = null;
            this.responseCode = responseCode;
            this.responseMessage = responseMessage;
        }

        public License getLicense() {
            return license;
        }

        public void setLicense(License license) {
            this.license = license;
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
