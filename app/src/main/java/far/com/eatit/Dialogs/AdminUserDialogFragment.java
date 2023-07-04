package far.com.eatit.Dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import far.com.eatit.API.APIClient;
import far.com.eatit.API.APIInterface;
import far.com.eatit.API.models.Device;
import far.com.eatit.API.models.License;
import far.com.eatit.API.models.ResponseBase;
import far.com.eatit.API.models.User;
import far.com.eatit.API.models.UserRole;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Interfases.DialogCaller;
import far.com.eatit.Main;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserDialogFragment extends DialogFragment  {


    Main mainActivity;
    License license;
    DialogCaller dialogCaller;
    APIInterface apiInterface;
    public User tempObj;
    boolean companyLoadedFirstTime;
    boolean userRoleLoadedFirstTime;

    LinearLayout llSave;
    TextInputEditText etName, etPassword, etCode;
    Spinner spnUserType, spnLevel, spnCompany;
    CheckBox cbEnabled;

    UserDialogFragmentResponse dialogResponse;
    Runnable exitRunnable = new Runnable() {
        @Override
        public void run() {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialogCaller.dialogClosed(dialogResponse);
                    AdminUserDialogFragment.this.dismiss();
                }
            });
        }
    };

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static AdminUserDialogFragment newInstance(Main mainActivity, License license,DialogCaller dialogCaller, User obj) {

        AdminUserDialogFragment f = new AdminUserDialogFragment();
        f.mainActivity = mainActivity;
        f.license = license;
        f.dialogCaller = dialogCaller;
        f.tempObj = obj;
        f.companyLoadedFirstTime = true;
        f.userRoleLoadedFirstTime = true;

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(obj != null) {
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        apiInterface = APIClient.getClient(mainActivity).create(APIInterface.class);
        return inflater.inflate(R.layout.admin_user_dialog_fragment, container, true);
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
        etName = view.findViewById(R.id.etName);
        etPassword = view.findViewById(R.id.etPassword);
        spnLevel = view.findViewById(R.id.spnLevel);
        spnCompany = view.findViewById(R.id.spnCompany);
        spnUserType = view.findViewById(R.id.spnUserType);
        cbEnabled = view.findViewById(R.id.cbEnabled);


        //fillSpnCompany();
        searchCompanies();
        //fillSpnUserTypes();
        searchUserRoles();

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
            setUpToEditUser();
        }
    }

    public boolean validate(){
        if(spnCompany.getSelectedItem()== null){
            Snackbar.make(getView(), "La empresa es obligatoria", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(etCode.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "El codigo de usuario es obligatorio", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(etPassword.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Debe escribir una contraseña", Snackbar.LENGTH_SHORT).show();
            return false;
        }if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void Save(){
        if(validate()) {
            SaveEntity();
        }

        llSave.setEnabled(true);

    }





    public void SaveEntity(){
        try {
            String userRole = ((KV)spnUserType.getSelectedItem()).getKey();
            String code = etCode.getText().toString();
            //String systemCode = ((KV)spnLevel.getSelectedItem()).getKey();
            String userName = etName.getText().toString();
            String password = etPassword.getText().toString().trim();
            String company = ((KV)spnCompany.getSelectedItem()).getKey();
            boolean enabled = cbEnabled.isChecked();

            //int idLicense, int idcompany, int iduserRole, String code, String password, String name, boolean enabled
            User u = new User(license.getId(), Integer.parseInt(company), Integer.parseInt(userRole),code, password, userName, enabled);

            mainActivity.showWaitingDialog();
            apiInterface.saveUser(u).enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                    ResponseBase rb = response.body();
                    if(response.isSuccessful()){
                        User o = (User) rb.getData();
                        dialogResponse = new UserDialogFragmentResponse(o);
                        mainActivity.showSuccessActionDialog("Saved",exitRunnable);
                    }else{
                        String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                        dialogResponse = new UserDialogFragmentResponse("99",message);
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                    }
                    mainActivity.dismissWaitingDialog();
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    dialogResponse = new UserDialogFragmentResponse("99",t.getMessage());
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
            tempObj.setIduserRole(Integer.parseInt(((KV)spnUserType.getSelectedItem()).getKey()));
            tempObj.setName(etName.getText().toString());
            tempObj.setPassword(etPassword.getText().toString().trim());
            tempObj.setEnabled(cbEnabled.isChecked());
            tempObj.setIdcompany(Integer.parseInt(((KV)spnCompany.getSelectedItem()).getKey()));
            tempObj.setIdLicense(license.getId());

            mainActivity.showWaitingDialog();
            apiInterface.updateUser(tempObj).enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {

                    ResponseBase rb = response.body();
                    if(response.isSuccessful()){
                        tempObj = (User) rb.getData();
                        dialogResponse = new UserDialogFragmentResponse(tempObj);
                        mainActivity.showSuccessActionDialog("Updated",exitRunnable);
                    }else{
                        String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                        dialogResponse = new UserDialogFragmentResponse("99",message);
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                    }
                    mainActivity.dismissWaitingDialog();
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    mainActivity.showErrorDialogAutoClose(t.getMessage(), exitRunnable);
                    dialogCaller.dialogClosed(new UserDialogFragmentResponse("99",t.getMessage()));
                    mainActivity.dismissWaitingDialog();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }


    }



    public void setUpToEditUser(){

        etCode.setText(tempObj.getCode());
        etCode.setEnabled(false);
        etName.setText(tempObj.getName());
        etPassword.setText(tempObj.getPassword());
        cbEnabled.setChecked(tempObj.isEnabled());

    }

    public void fillSpnCompany(ArrayList<KV> spnList){
        spnCompany.setAdapter(new ArrayAdapter<KV>(getActivity(), android.R.layout.simple_list_item_1,spnList));
    }

    public void fillSpnUserTypes(ArrayList<KV> spnList){
        spnUserType.setAdapter(new ArrayAdapter<KV>(getActivity(), android.R.layout.simple_list_item_1,spnList));
    }


    public void setLevelPosition(String key){
        for(int i = 0; i< spnLevel.getAdapter().getCount(); i++){
            if(((KV)spnLevel.getAdapter().getItem(i)).getKey().equals(key)){
                spnLevel.setSelection(i);
                break;
            }
        }
    }

    public void setCompanyPosition(int id){
        for(int i = 0; i< spnCompany.getAdapter().getCount(); i++){
            if(((KV)spnCompany.getAdapter().getItem(i)).getKey().equals(id+"")){
                spnCompany.setSelection(i);
                break;
            }
        }
    }

    public void setUserTypePosition(int id){
        for(int i = 0; i< spnUserType.getAdapter().getCount(); i++){
            if(((KV)spnUserType.getAdapter().getItem(i)).getKey().equals(id+"")){
                spnUserType.setSelection(i);
                break;
            }
        }
    }


    public void searchCompanies(){
        //startLoading();
        apiInterface.getCompanies(license.getId()).enqueue(new Callback<List<far.com.eatit.API.models.Company>>() {
            @Override
            public void onResponse(Call<List<far.com.eatit.API.models.Company>> call, Response<List<far.com.eatit.API.models.Company>> response) {
                ArrayList<KV> lrm = new ArrayList<>();
                //endLoading();

                if(response.isSuccessful()){
                    List<far.com.eatit.API.models.Company> list = response.body();
                    for(far.com.eatit.API.models.Company obj : list){
                        lrm.add(new KV(obj.getId()+"",obj.getName()));
                    }
                }
                fillSpnCompany(lrm);
                if(tempObj!= null && companyLoadedFirstTime){
                    companyLoadedFirstTime = false;
                    setCompanyPosition(tempObj.getIdcompany());
                }

            }

            @Override
            public void onFailure(Call<List<far.com.eatit.API.models.Company>> call, Throwable t) {
                Snackbar.make(getView(),t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                //endLoading();
            }
        });

    }

    public void searchUserRoles(){
        //startLoading();
        apiInterface.getUserRoles(license.getId()).enqueue(new Callback<List<UserRole>>() {
            @Override
            public void onResponse(Call<List<UserRole>> call, Response<List<UserRole>> response) {
                ArrayList<KV> lrm = new ArrayList<>();
                //endLoading();

                if(response.isSuccessful()){
                    List<UserRole> list = response.body();
                    for(UserRole obj : list){
                        lrm.add(new KV(obj.getId()+"",obj.getDescription()));
                    }
                }
                fillSpnUserTypes(lrm);
                if(tempObj!= null && userRoleLoadedFirstTime){
                    userRoleLoadedFirstTime = false;
                    setUserTypePosition(tempObj.getIduserRole());
                }

            }

            @Override
            public void onFailure(Call<List<UserRole>> call, Throwable t) {
                Snackbar.make(getView(),t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                //endLoading();
            }
        });

    }

    public  class UserDialogFragmentResponse{
        private User user;
        private String responseCode;
        private String responseMessage;

        public UserDialogFragmentResponse(User user) {
            this.user = user;
            this.responseCode = "00";
            this.responseMessage = "success";
        }

        public UserDialogFragmentResponse(String responseCode, String responseMessage) {
            this.user = null;
            this.responseCode = responseCode;
            this.responseMessage = responseMessage;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
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
