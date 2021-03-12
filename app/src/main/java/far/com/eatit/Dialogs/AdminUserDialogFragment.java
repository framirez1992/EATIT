package far.com.eatit.Dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import far.com.eatit.AdminLicenseUsers;
import far.com.eatit.CloudFireStoreObjects.Company;
import far.com.eatit.CloudFireStoreObjects.UserTypes;
import far.com.eatit.CloudFireStoreObjects.Users;
import far.com.eatit.Controllers.RolesController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;

public class AdminUserDialogFragment extends DialogFragment implements OnFailureListener {

    AdminLicenseUsers adminLicenseUsers;
    public Users tempObj;
    ArrayList<Company> companies;
    ArrayList<UserTypes> userTypes;
    public String codeLicense;

    LinearLayout llSave;
    TextInputEditText etName, etPassword, etCode;
    Spinner spnUserType, spnLevel, spnCompany;
    CheckBox cbEnabled;


    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static AdminUserDialogFragment newInstance(AdminLicenseUsers adminLicenseUsers, Users users,ArrayList<Company> companies,ArrayList<UserTypes> userTypes, String codeLicense) {

        AdminUserDialogFragment f = new AdminUserDialogFragment();
        f.adminLicenseUsers = adminLicenseUsers;
        f.tempObj = users;
        f.codeLicense = codeLicense;
        f.companies = companies;
        f.userTypes = userTypes;

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(users != null) {
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

        RolesController.getInstance(getActivity()).fillGeneralRolesLocal(spnLevel);
        fillSpnCompany();
        fillSpnUserTypes();

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                    EditUser();
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
        }else if(tempObj == null && adminLicenseUsers.getUserByCode(etCode.getText().toString()) != null){
            Snackbar.make(getView(), "Ya existe el codigo de usuario", Snackbar.LENGTH_SHORT).show();
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
            SaveUser();
        }

        llSave.setEnabled(true);

    }

    public void SaveUser(){
        try {
            String userType = ((KV)spnUserType.getSelectedItem()).getKey();
            String code = etCode.getText().toString();
            String systemCode = ((KV)spnLevel.getSelectedItem()).getKey();
            String userName = etName.getText().toString();
            String password = etPassword.getText().toString().trim();
            String company = ((KV)spnCompany.getSelectedItem()).getKey();
            boolean enabled = cbEnabled.isChecked();

            Users u = new Users(code,systemCode, password, userName,userType,company, enabled);


            adminLicenseUsers.getFs().collection(Tablas.generalUsers).document(codeLicense).collection(Tablas.generalUsersUsers).document(u.getCODE()).set(u.toMap())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dismiss();
                        }
                    }).addOnFailureListener(this);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void EditUser(){
        try {
            Users u = tempObj;
            u.setROLE(((KV)spnUserType.getSelectedItem()).getKey());
            u.setUSERNAME(etName.getText().toString());
            u.setPASSWORD(etPassword.getText().toString().trim());
            u.setENABLED(cbEnabled.isChecked());
            u.setSYSTEMCODE(((KV)spnLevel.getSelectedItem()).getKey());
            u.setCOMPANY(((KV)spnCompany.getSelectedItem()).getKey());
            u.setMDATE(null);

            adminLicenseUsers.getFs().collection(Tablas.generalUsers).document(codeLicense).collection(Tablas.generalUsersUsers)
                    .document(u.getCODE()).update(tempObj.toMap())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dismiss();
                        }
                    }).addOnFailureListener(this);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }



    public void setUpToEditUser(){

        Users u = ((Users)tempObj);
        etCode.setText(u.getCODE());
        etCode.setEnabled(false);
        etName.setText(u.getUSERNAME());
        etPassword.setText(u.getPASSWORD());
        setLevelPosition(u.getSYSTEMCODE());
        setCompanyPosition(u.getCOMPANY());
        setUserTypePosition(u.getROLE());
        cbEnabled.setChecked(u.isENABLED());

    }

    public void fillSpnCompany(){
        ArrayList<KV> spnList = new ArrayList<>();
        for(Company ut : companies){
            spnList.add(new KV(ut.getCODE(), ut.getNAME()+" ["+ut.getRNC()+"]"));
        }
        spnCompany.setAdapter(new ArrayAdapter<KV>(getActivity(), android.R.layout.simple_list_item_1,spnList));
    }

    public void fillSpnUserTypes(){
        ArrayList<KV> spnList = new ArrayList<>();
        for(UserTypes ut : userTypes){
            spnList.add(new KV(ut.getCODE(), ut.getDESCRIPTION()));
        }
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

    public void setCompanyPosition(String key){
        for(int i = 0; i< spnCompany.getAdapter().getCount(); i++){
            if(((KV)spnCompany.getAdapter().getItem(i)).getKey().equals(key)){
                spnCompany.setSelection(i);
                break;
            }
        }
    }

    public void setUserTypePosition(String key){
        for(int i = 0; i< spnUserType.getAdapter().getCount(); i++){
            if(((KV)spnUserType.getAdapter().getItem(i)).getKey().equals(key)){
                spnUserType.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }



}
