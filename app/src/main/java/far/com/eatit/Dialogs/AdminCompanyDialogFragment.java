package far.com.eatit.Dialogs;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import far.com.eatit.AdminLicenseCompany;
import far.com.eatit.CloudFireStoreObjects.Company;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;

import static android.app.Activity.RESULT_OK;

public class AdminCompanyDialogFragment  extends DialogFragment implements OnFailureListener {

    AdminLicenseCompany adminLicenseCompany;
    Company tempObj;
    String codeLicense;

    LinearLayout llSave;
    TextInputEditText etCode, etRnc, etName, etPhone, etPhone2, etAddress, etAddress2;
    ImageView imgLogo;
    ProgressBar pb;
    TextView tvMessage;
    private StorageReference mStorageRef;
    int SEARCH_REQUEST=777;
    Uri filePath;

    public  static AdminCompanyDialogFragment newInstance(AdminLicenseCompany adminLicenseCompany, Company company, String codeLicense) {
        AdminCompanyDialogFragment f = new AdminCompanyDialogFragment();
        f.tempObj = company;
        f.adminLicenseCompany = adminLicenseCompany;
        f.codeLicense = codeLicense;


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

        mStorageRef = FirebaseStorage.getInstance().getReference();
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

        if(tempObj != null){//EDIT
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
            if(tempObj!= null && keeptLogo()){
                EditCompany(tempObj.getLOGO());
                dismiss();
                endLoading();
            }else{
                uploadImage();
            }
        }else{
            llSave.setEnabled(true);
        }
    }

    public void SaveCompany(String logo){
        try {
            String code = etCode.getText().toString();
            String name = etName.getText().toString();
            String rnc = etRnc.getText().toString();
            String address = etAddress.getText().toString();
            String address2 = etAddress2.getText().toString();
            String phone = etPhone.getText().toString();
            String phone2 = etPhone2.getText().toString();
            Company company = new Company(code,name, rnc, address, address2, phone, phone2,logo,  null, null);

            adminLicenseCompany.getFs().collection(Tablas.generalUsers).document(codeLicense).collection(Tablas.generalUsersCompany).document(company.getCODE()).set(company.toMap())
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

    public void EditCompany(String logo){
        try {
            tempObj.setNAME(etName.getText().toString());
            tempObj.setRNC(etRnc.getText().toString());
            tempObj.setADDRESS(etAddress.getText().toString());
            tempObj.setADDRESS2(etAddress2.getText().toString());
            tempObj.setPHONE(etPhone.getText().toString());
            tempObj.setPHONE2(etPhone2.getText().toString());
            tempObj.setLOGO(logo);
            tempObj.setMDATE(null);

            adminLicenseCompany.getFs().collection(Tablas.generalUsers).document(codeLicense).collection(Tablas.generalUsersCompany)
                    .document(tempObj.getCODE()).update(tempObj.toMap())
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


    public void setUpToEditCompany(){

        etCode.setText(tempObj.getCODE());
        etName.setText(tempObj.getNAME());
        etRnc.setText(tempObj.getRNC());
        etPhone.setText(tempObj.getPHONE());
        etPhone2.setText(tempObj.getPHONE2());
        etAddress.setText(tempObj.getADDRESS());
        etAddress2.setText(tempObj.getADDRESS2());
        if(!tempObj.getLOGO().isEmpty()){
            Picasso.with(adminLicenseCompany).load(tempObj.getLOGO()).into(imgLogo);
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
            Picasso.with(adminLicenseCompany).load(filePath).into(imgLogo);
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
            fileExtension=Funciones.getFileExtension(adminLicenseCompany, filePath);
        }
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
        tvMessage.setTextColor(adminLicenseCompany.getResources().getColor(color));
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }

    public boolean keeptLogo(){
        return filePath== null && tempObj!= null && !tempObj.getLOGO().isEmpty();
    }
}
