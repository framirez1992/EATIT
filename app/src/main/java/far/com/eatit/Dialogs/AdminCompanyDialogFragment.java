package far.com.eatit.Dialogs;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import far.com.eatit.API.APIClient;
import far.com.eatit.API.APIInterface;
import far.com.eatit.API.models.Company;
import far.com.eatit.API.models.License;
import far.com.eatit.API.models.ResponseBase;
import far.com.eatit.API.models.UserRole;
import far.com.eatit.AdminLicenseCompany;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Interfases.DialogCaller;
import far.com.eatit.Main;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class AdminCompanyDialogFragment  extends DialogFragment {

    Main mainActivity;
    License license;
    DialogCaller dialogCaller;
    Company company;
    APIInterface apiInterface;

    AdminCompanyDialogFragment.AdminCompanyDialogFragmentResponse dialogResponse;
    Runnable exitRunnable = new Runnable() {
        @Override
        public void run() {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialogCaller.dialogClosed(dialogResponse);
                    AdminCompanyDialogFragment.this.dismiss();
                }
            });
        }
    };


    LinearLayout llSave;
    TextInputEditText etCode, etRnc, etName, etPhone, etPhone2, etAddress, etAddress2;
    ImageView imgLogo;
    ProgressBar pb;
    TextView tvMessage;
    //private StorageReference mStorageRef;
    int SEARCH_REQUEST=777;
    Uri filePath;

    public  static AdminCompanyDialogFragment newInstance(Main mainActivity, Company company,License license, DialogCaller dialogCaller) {
        AdminCompanyDialogFragment f = new AdminCompanyDialogFragment();
        f.company = company;
        f.mainActivity = mainActivity;
        f.license = license;
        f.dialogCaller = dialogCaller;


        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(company != null) {
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
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etRnc);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        apiInterface = APIClient.getClient(mainActivity).create(APIInterface.class);
        //mStorageRef = FirebaseStorage.getInstance().getReference();
        return inflater.inflate(R.layout.dialog_add_edit_company, container, true);
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
        etCode = view.findViewById(R.id.etCode);
        etName = view.findViewById(R.id.etName);
        etRnc= view.findViewById(R.id.etRnc);
        etPhone = view.findViewById(R.id.etPhone);
        etPhone2= view.findViewById(R.id.etPhone2);
        etAddress= view.findViewById(R.id.etAddress);
        etAddress2 = view.findViewById(R.id.etAddress2);
        imgLogo = view.findViewById(R.id.imgLogo);
        pb = view.findViewById(R.id.pb);
        tvMessage = view.findViewById(R.id.tvMessage);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                Save();
            }
        });

        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchImage();
            }
        });

        etCode.setText(Funciones.generateCode());

        if(company != null){//EDIT
            setUpToEditCompany();
        }
    }

    public boolean validate(){
        if(etCode.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "El codigo de empresa es obligatorio", Snackbar.LENGTH_SHORT).show();
            return false;
        }if(etRnc.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "El RNC es obligatorio", Snackbar.LENGTH_SHORT).show();
            return false;
        }if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void Save(){

        if(validate()) {
            if(company!= null /*&& keeptLogo()*/){
                EditCompany(company.getLogo());
                //dismiss();
                //endLoading();
            }else{
                //uploadImage();
                SaveCompany("");
            }
        }else{
            llSave.setEnabled(true);
        }
    }

    public void SaveCompany(String logo){
            String code = etCode.getText().toString();
            String name = etName.getText().toString();
            String rnc = etRnc.getText().toString();
            String address = etAddress.getText().toString();
            String address2 = etAddress2.getText().toString();
            String phone = etPhone.getText().toString();
            String phone2 = etPhone2.getText().toString();
            //int idLicense, String code, String rnc, String name, String phone, String phone2, String phone3, String address, String address2, String address3, String logo
            Company company = new Company(license.id,code, rnc,name, phone, phone2,"", address, address2,"",logo);

        mainActivity.showWaitingDialog();
        apiInterface.saveCompany(company).enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                ResponseBase rb = response.body();
                if(response.isSuccessful()){
                    Company o = (Company) rb.getData();
                    dialogResponse = new AdminCompanyDialogFragmentResponse(o);
                    mainActivity.showSuccessActionDialog("Saved",exitRunnable);
                }else{
                    String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                    dialogResponse = new AdminCompanyDialogFragmentResponse("99",message);
                    mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                }
                mainActivity.dismissWaitingDialog();
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                dialogResponse = new AdminCompanyDialogFragmentResponse("99",t.getMessage());
                mainActivity.showErrorDialogAutoClose(t.getMessage(), exitRunnable);
                mainActivity.dismissWaitingDialog();
            }
        });

    }

    public void EditCompany(String logo){
        try {
            company.setName(etName.getText().toString());
            company.setRnc(etRnc.getText().toString());
            company.setAddress(etAddress.getText().toString());
            company.setAddress2(etAddress2.getText().toString());
            company.setPhone(etPhone.getText().toString());
            company.setPhone2(etPhone2.getText().toString());
            company.setLogo(logo);

            mainActivity.showWaitingDialog();
            apiInterface.updateCompany(company).enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {

                    ResponseBase rb = response.body();
                    if(response.isSuccessful()){
                        company = (Company) rb.getData();
                        dialogCaller.dialogClosed(new AdminCompanyDialogFragmentResponse(company));
                        mainActivity.showSuccessActionDialog("Updated",exitRunnable);
                    }else{
                        String message = rb == null?response.errorBody().toString():rb.getResposeMessage();
                        dialogCaller.dialogClosed(new AdminCompanyDialogFragmentResponse("99",message));
                        mainActivity.showErrorDialogAutoClose(message, exitRunnable);
                    }
                    mainActivity.dismissWaitingDialog();
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    mainActivity.showErrorDialogAutoClose(t.getMessage(), exitRunnable);
                    dialogCaller.dialogClosed(new AdminCompanyDialogFragmentResponse("99",t.getMessage()));
                    mainActivity.dismissWaitingDialog();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void setUpToEditCompany(){

        etCode.setText(company.getCode());
        etName.setText(company.getName());
        etRnc.setText(company.getRnc());
        etPhone.setText(company.getPhone());
        etPhone2.setText(company.getPhone2());
        etAddress.setText(company.getAddress());
        etAddress2.setText(company.getAddress3());
        if(!company.getLogo().isEmpty()){
            //Picasso.with(adminLicenseCompany).load(tempObj.getLOGO()).into(imgLogo);
        }


    }


    public void searchImage(){
        Intent i = new Intent();
        i.setType("Image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Selecciona una imagen"), SEARCH_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SEARCH_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            //Picasso.with(adminLicenseCompany).load(filePath).into(imgLogo);
        }else{
            Snackbar.make(getView(), "No selecciono ningun archivo", Snackbar.LENGTH_LONG ).show();
        }
    }




    public void uploadImage(){
        startLoading();
        Uri file = filePath;
        String fileExtension="png";

        if(file == null){
            Resources resources = getActivity().getResources();
            Uri uri = new Uri.Builder()
                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(resources.getResourcePackageName(R.mipmap.ic_launcher))
                    .appendPath(resources.getResourceTypeName(R.mipmap.ic_launcher))
                    .appendPath(resources.getResourceEntryName(R.mipmap.ic_launcher))
                    .build();
             file =uri;
        }else{
            fileExtension=Funciones.getFileExtension(mainActivity, filePath);
        }
        /*
       // if(file != null){
            StorageReference riversRef = mStorageRef.child((tempObj!=null?tempObj.getCODE():etCode.getText().toString())+"/"+ "logo" +"."+fileExtension);
            riversRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if(tempObj == null){
                                        SaveCompany(uri.toString());
                                    }else{
                                        EditCompany(uri.toString());
                                    }

                                    dismiss();
                                    endLoading();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    llSave.setEnabled(true);
                                    endLoading();
                                    setMessageUpload(e.getLocalizedMessage(), R.color.red_700);
                                    Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            llSave.setEnabled(true);
                            endLoading();
                            setMessageUpload(exception.getLocalizedMessage(), R.color.red_700);
                            Snackbar.make(getView(), exception.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    setMessageUpload(progress+"% Uploaded...");

                }
            });
       /* }else{
            llSave.setEnabled(true);
            endLoading();
            setMessageUpload("Archivo invalido. Seleccione una imagen");
            Snackbar.make(getView(), "Seleccione un logo", Snackbar.LENGTH_LONG).show();
        }*/


    }

    public void startLoading(){
        pb.setVisibility(View.VISIBLE);
        setMessageUpload("Uploading...");
    }

    public void endLoading(){
        pb.setVisibility(View.INVISIBLE);
    }
    public void setMessageUpload(String msg){
        setMessageUpload(msg,android.R.color.black);
    }

    public void setMessageUpload(String msg, int color){
        tvMessage.setText(msg);
        tvMessage.setTextColor(mainActivity.getResources().getColor(color));
    }

/*
    public boolean keeptLogo(){
        return filePath== null && company!= null && !company.getLOGO().isEmpty();
    }
*/

    public  class AdminCompanyDialogFragmentResponse{
        private Company company;
        private String responseCode;
        private String responseMessage;

        public AdminCompanyDialogFragmentResponse(Company company) {
            this.company = company;
            this.responseCode = "00";
            this.responseMessage = "success";
        }

        public AdminCompanyDialogFragmentResponse(String responseCode, String responseMessage) {
            this.company = null;
            this.responseCode = responseCode;
            this.responseMessage = responseMessage;
        }

        public Company getCompany() {
            return company;
        }

        public void setCompany(Company company) {
            this.company = company;
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
