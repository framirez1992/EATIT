package far.com.eatit.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import far.com.eatit.API.APIClient;
import far.com.eatit.API.APIInterface;
import far.com.eatit.API.models.Device;
import far.com.eatit.API.models.License;
import far.com.eatit.API.models.ResponseBase;
import far.com.eatit.AdminLicenseDevices;
import far.com.eatit.CloudFireStoreObjects.Devices;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Interfases.DialogCaller;
import far.com.eatit.Main;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceDialogFragment extends DialogFragment {

    Main mainActivity;
    License license;
    DialogCaller dialogCaller;
    APIInterface apiInterface;
    public Device tempObj;

    LinearLayout llSave;
    TextInputEditText etCode;
    CheckBox cbEnabled;

    DeviceDialogFragment.DeviceDialogFragmentResponse dialogResponse;
    Runnable exitRunnable = new Runnable() {
        @Override
        public void run() {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialogCaller.dialogClosed(dialogResponse);
                    DeviceDialogFragment.this.dismiss();
                }
            });
        }
    };


    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static DeviceDialogFragment newInstance(Main mainActivity, License license,DialogCaller dialogCaller, Device obj) {

        DeviceDialogFragment f = new DeviceDialogFragment();
        f.mainActivity = mainActivity;
        f.license = license;
        f.dialogCaller = dialogCaller;
        f.tempObj = obj;

        // Supply num input as an argument.
        Bundle args = new Bundle();

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        apiInterface = APIClient.getClient(mainActivity).create(APIInterface.class);
        return inflater.inflate(R.layout.dialog_add_edit_device, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etCode);
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
        cbEnabled = view.findViewById(R.id.cbEnabled);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                    EditDevice();
                }
            }
        });

        if(tempObj != null){//EDIT
            setUpToEditProductType();
        }
    }

    public boolean validateDevice(){
        if(etCode.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un codigo", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void Save(){
        if(validateDevice()) {
            SaveEntity();
        }

        llSave.setEnabled(true);

    }

    public void SaveEntity(){
        try {
            String code = etCode.getText().toString();
            Device device = new Device(license.id,code,cbEnabled.isChecked());

            mainActivity.showWaitingDialog();
            apiInterface.saveDevice(device).enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                    ResponseBase rb = response.body();
                    if(response.isSuccessful()){
                        Device o = (Device) rb.getData();
                        dialogResponse = new DeviceDialogFragment.DeviceDialogFragmentResponse(o);
                        mainActivity.showSuccessActionDialog("Saved",exitRunnable);
                    }else{
                        String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                        dialogResponse = new DeviceDialogFragment.DeviceDialogFragmentResponse("99",message);
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                    }
                    mainActivity.dismissWaitingDialog();
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    dialogResponse = new DeviceDialogFragment.DeviceDialogFragmentResponse("99",t.getMessage());
                    mainActivity.showErrorDialogAutoClose(t.getMessage(), exitRunnable);
                    mainActivity.dismissWaitingDialog();
                }
            });


        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void EditDevice(){
        try {
            tempObj.setEnabled(cbEnabled.isChecked());

            mainActivity.showWaitingDialog();
            apiInterface.updateDevice(tempObj).enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {

                    ResponseBase rb = response.body();
                    if(response.isSuccessful()){
                        tempObj = (Device) rb.getData();
                        dialogCaller.dialogClosed(new DeviceDialogFragment.DeviceDialogFragmentResponse(tempObj));
                        mainActivity.showSuccessActionDialog("Updated",exitRunnable);
                    }else{
                        String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                        dialogCaller.dialogClosed(new DeviceDialogFragment.DeviceDialogFragmentResponse("99",message));
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                    }
                    mainActivity.dismissWaitingDialog();
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    mainActivity.showErrorDialogAutoClose(t.getMessage(), exitRunnable);
                    dialogCaller.dialogClosed(new DeviceDialogFragment.DeviceDialogFragmentResponse("99",t.getMessage()));
                    mainActivity.dismissWaitingDialog();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }


    }



    public void setUpToEditProductType(){
        etCode.setText(tempObj.getCode());
        etCode.setEnabled(false);
        cbEnabled.setChecked(tempObj.isEnabled());

    }





    public  class DeviceDialogFragmentResponse{
        private Device device;
        private String responseCode;
        private String responseMessage;

        public DeviceDialogFragmentResponse(Device device) {
            this.device = device;
            this.responseCode = "00";
            this.responseMessage = "success";
        }

        public DeviceDialogFragmentResponse(String responseCode, String responseMessage) {
            this.device = null;
            this.responseCode = responseCode;
            this.responseMessage = responseMessage;
        }

        public Device getDevice() {
            return device;
        }

        public void setDevice(Device device) {
            this.device = device;
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
