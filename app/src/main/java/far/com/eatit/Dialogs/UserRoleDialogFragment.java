package far.com.eatit.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;

import far.com.eatit.API.APIClient;
import far.com.eatit.API.APIInterface;
import far.com.eatit.API.models.Device;
import far.com.eatit.API.models.License;
import far.com.eatit.API.models.ResponseBase;
import far.com.eatit.API.models.UserRole;
import far.com.eatit.CloudFireStoreObjects.UserTypes;
import far.com.eatit.Controllers.UserTypesController;
import far.com.eatit.Interfases.DialogCaller;
import far.com.eatit.Main;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRoleDialogFragment extends DialogFragment {

    Main mainActivity;
    License license;
    DialogCaller dialogCaller;
    UserRole tempObj;
    APIInterface apiInterface;

    LinearLayout llSave;
    TextInputEditText etName;

    UserRoleDialogFragmentResponse dialogResponse;
    Runnable exitRunnable = new Runnable() {
        @Override
        public void run() {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialogCaller.dialogClosed(dialogResponse);
                    UserRoleDialogFragment.this.dismiss();
                }
            });
        }
    };



    public  static UserRoleDialogFragment newInstance(Main mainActivity, License license, DialogCaller dialogCaller, UserRole userRole) {

        UserRoleDialogFragment f = new UserRoleDialogFragment();
        f.tempObj = userRole;
        f.mainActivity = mainActivity;
        f.license = license;
        f.dialogCaller = dialogCaller;

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(userRole != null) {
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

        apiInterface = APIClient.getClient(mainActivity).create(APIInterface.class);
        return inflater.inflate(R.layout.product_type_fragment_dialog, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiInterface = APIClient.getClient(mainActivity).create(APIInterface.class);
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
                    EditEntity();
                }
            }
        });

        if(tempObj != null){//EDIT
            setUpEdit();
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
            SaveEntity();
        }else{
            llSave.setEnabled(true);
        }
    }






    public void SaveEntity(){
        try {
            String code = UUID.randomUUID().toString();
            String description = etName.getText().toString();
            UserRole userRole = new UserRole(license.getId(),code, description);

            mainActivity.showWaitingDialog();
            apiInterface.saveUserRole(userRole).enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                    ResponseBase rb = response.body();
                    if(response.isSuccessful()){
                        UserRole o = (UserRole) rb.getData();
                        dialogResponse = new UserRoleDialogFragmentResponse(o);
                        mainActivity.showSuccessActionDialog("Saved",exitRunnable);
                    }else{
                        String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                        dialogResponse = new UserRoleDialogFragmentResponse("99",message);
                    }
                    mainActivity.dismissWaitingDialog();
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    dialogResponse = new UserRoleDialogFragmentResponse("99",t.getMessage());
                    mainActivity.showErrorDialogAutoClose(t.getMessage(), exitRunnable);
                    mainActivity.dismissWaitingDialog();
                }
            });


        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void EditEntity(){
        try {
            tempObj.setDescription(etName.getText().toString());
            mainActivity.showWaitingDialog();
            apiInterface.updateUserRole(tempObj).enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {

                    ResponseBase rb = response.body();
                    if(response.isSuccessful()){
                        tempObj = (UserRole) rb.getData();
                        dialogCaller.dialogClosed(new UserRoleDialogFragmentResponse(tempObj));
                        mainActivity.showSuccessActionDialog("Updated",exitRunnable);
                    }else{
                        String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                        dialogCaller.dialogClosed(new UserRoleDialogFragmentResponse("99",message));
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                    }
                    mainActivity.dismissWaitingDialog();
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    mainActivity.showErrorDialogAutoClose(t.getMessage(), exitRunnable);
                    dialogCaller.dialogClosed(new UserRoleDialogFragmentResponse("99",t.getMessage()));
                    mainActivity.dismissWaitingDialog();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }


    }



    public void setUpEdit(){
        etName.setText(tempObj.getDescription());

    }

    public  class UserRoleDialogFragmentResponse{
        private UserRole userRole;
        private String responseCode;
        private String responseMessage;

        public UserRoleDialogFragmentResponse(UserRole userRole) {
            this.userRole = userRole;
            this.responseCode = "00";
            this.responseMessage = "success";
        }

        public UserRoleDialogFragmentResponse(String responseCode, String responseMessage) {
            this.userRole = null;
            this.responseCode = responseCode;
            this.responseMessage = responseMessage;
        }

        public UserRole getUserRole() {
            return userRole;
        }

        public void setUserRole(UserRole userRole) {
            this.userRole = userRole;
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
