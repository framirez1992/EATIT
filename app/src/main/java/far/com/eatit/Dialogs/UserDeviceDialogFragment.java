package far.com.eatit.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import far.com.eatit.API.APIClient;
import far.com.eatit.API.APIInterface;
import far.com.eatit.API.models.Company;
import far.com.eatit.API.models.Device;
import far.com.eatit.API.models.License;
import far.com.eatit.API.models.ResponseBase;
import far.com.eatit.API.models.User;
import far.com.eatit.API.models.UserDevice;
import far.com.eatit.AdminLicenseUserDevice;
import far.com.eatit.CloudFireStoreObjects.Devices;
import far.com.eatit.CloudFireStoreObjects.Users;
import far.com.eatit.CloudFireStoreObjects.UsersDevices;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Interfases.DialogCaller;
import far.com.eatit.Main;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDeviceDialogFragment extends DialogFragment {

    Main mainActivity;
    License license;
    DialogCaller dialogCaller;
    APIInterface apiInterface;
    public UserDevice tempObj;

    UserDeviceDialogFragment.UserDeviceDialogFragmentResponse dialogResponse;
    Runnable exitRunnable = new Runnable() {
        @Override
        public void run() {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialogCaller.dialogClosed(dialogResponse);
                    UserDeviceDialogFragment.this.dismiss();
                }
            });
        }
    };

    LinearLayout llSave;
    Spinner spnUser, spnDevice;


    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static UserDeviceDialogFragment newInstance(Main mainActivity, License license, DialogCaller dialogCaller, UserDevice obj) {

        UserDeviceDialogFragment f = new UserDeviceDialogFragment();
        f.mainActivity = mainActivity;
        f.license = license;
        f.dialogCaller = dialogCaller;
        f.tempObj = obj;

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
        return inflater.inflate(R.layout.dialog_add_user_device, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

    }

    @Override
    public void onStart() {
        super.onStart();
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
        spnUser = view.findViewById(R.id.spnUser);
        spnDevice = view.findViewById(R.id.spnDevice);
        searchUsers();
        searchDevices();

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                    Save();
            }
        });

    }

    public boolean validateProductType(){
        if(spnUser.getAdapter()== null || spnUser.getAdapter().getCount()<1){
            Snackbar.make(getView(), "Especifique un usuario", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(spnDevice.getAdapter()== null || spnDevice.getAdapter().getCount()<1){
            Snackbar.make(getView(), "Especifique un dispositivo", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void Save(){
        if(validateProductType()) {
            SaveUserDevice();
        }

        llSave.setEnabled(true);

    }




    public void SaveUserDevice(){
        try {
            String idUser = ((KV)spnUser.getSelectedItem()).getKey();
            String idDevice = ((KV)spnDevice.getSelectedItem()).getKey();
            UserDevice t = new UserDevice(Integer.parseInt(idUser), Integer.parseInt(idDevice));

            mainActivity.showWaitingDialog();
            apiInterface.saveUserDevice(t).enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                    ResponseBase rb = response.body();
                    if(response.isSuccessful()){
                        UserDevice o = (UserDevice) rb.getData();
                        dialogResponse = new UserDeviceDialogFragmentResponse(o);
                        mainActivity.showSuccessActionDialog("Saved",exitRunnable);
                    }else{
                        String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                        dialogResponse = new UserDeviceDialogFragmentResponse("99",message);
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                    }
                    mainActivity.dismissWaitingDialog();
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    dialogResponse = new UserDeviceDialogFragmentResponse("99",t.getMessage());
                    mainActivity.showErrorDialogAutoClose(t.getMessage(), exitRunnable);
                    mainActivity.dismissWaitingDialog();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }


    }




    public void searchUsers(){
        //startLoading();
        apiInterface.getUnnasignedUsersToDevice(license.getId()).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                ArrayList<KV> lrm = new ArrayList<>();
                //endLoading();

                if(response.isSuccessful()){
                    List<User> list = response.body();
                    for(User obj : list){
                        lrm.add(new KV(obj.getId()+"",obj.getCode().concat("-").concat(obj.getName())));
                    }
                }
                fillSpnUsers(lrm);
                /*if(tempObj!= null && companyLoadedFirstTime){
                    companyLoadedFirstTime = false;
                    setCompanyPosition(tempObj.getIdcompany());
                }*/

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Snackbar.make(getView(),t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                //endLoading();
            }
        });

    }

    public void searchDevices(){
        //startLoading();
        apiInterface.getUnnasignedDevicesToUser(license.getId()).enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                ArrayList<KV> lrm = new ArrayList<>();
                //endLoading();

                if(response.isSuccessful()){
                    List<Device> list = response.body();
                    for(Device obj : list){
                        lrm.add(new KV(obj.getId()+"",obj.getCode()));
                    }
                }
                fillSpnDevices(lrm);
                /*if(tempObj!= null && companyLoadedFirstTime){
                    companyLoadedFirstTime = false;
                    setCompanyPosition(tempObj.getIdcompany());
                }*/

            }

            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {
                Snackbar.make(getView(),t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                //endLoading();
            }
        });

    }

    public void fillSpnUsers(ArrayList<KV> spnList){
        spnUser.setAdapter(new ArrayAdapter<KV>(getActivity(), android.R.layout.simple_list_item_1,spnList));
    }

    public void fillSpnDevices(ArrayList<KV> spnList){
        spnDevice.setAdapter(new ArrayAdapter<KV>(getActivity(), android.R.layout.simple_list_item_1,spnList));
    }


    public  class UserDeviceDialogFragmentResponse{
        private UserDevice userDevice;
        private String responseCode;
        private String responseMessage;

        public UserDeviceDialogFragmentResponse(UserDevice user) {
            this.userDevice = user;
            this.responseCode = "00";
            this.responseMessage = "success";
        }

        public UserDeviceDialogFragmentResponse(String responseCode, String responseMessage) {
            this.userDevice = null;
            this.responseCode = responseCode;
            this.responseMessage = responseMessage;
        }

        public UserDevice getUserDevice() {
            return userDevice;
        }

        public void setUserDevice(UserDevice userDevice) {
            this.userDevice = userDevice;
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
