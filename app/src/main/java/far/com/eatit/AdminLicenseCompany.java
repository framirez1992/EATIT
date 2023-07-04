package far.com.eatit;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

import far.com.eatit.API.APIClient;
import far.com.eatit.API.APIInterface;
import far.com.eatit.API.models.Company;
import far.com.eatit.API.models.License;
import far.com.eatit.API.models.UserRole;
import far.com.eatit.Adapters.CompanyEditionAdapter;
import far.com.eatit.Adapters.Models.CompanyRowModel;
import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Adapters.SimpleRowEditionAdapter;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.Controllers.CompanyController;
import far.com.eatit.Dialogs.AdminCompanyDialogFragment;
import far.com.eatit.Dialogs.UserRoleDialogFragment;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Interfases.DialogCaller;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.Utils.Funciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminLicenseCompany extends Fragment implements ListableActivity, DialogCaller {

    Main mainActivity;
    License license;
    APIInterface apiInterface;
    ProgressBar pb;

    RecyclerView rvList;
    Company company;

    public AdminLicenseCompany() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AdminLicenseCompany newInstance(Main mainActivity, License license) {
        AdminLicenseCompany fragment = new AdminLicenseCompany();
        fragment.mainActivity = mainActivity;
        fragment.license = license;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        apiInterface = APIClient.getClient(mainActivity).create(APIInterface.class);
        return inflater.inflate(R.layout.maintenance_w_spinner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.llMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptions(true);
            }
        });

        view.findViewById(R.id.cvSpinner).setVisibility(View.GONE);

        rvList = view.findViewById(R.id.rvList);
        pb = view.findViewById(R.id.pb);

        LinearLayoutManager manager = new LinearLayoutManager(mainActivity);
        rvList.setLayoutManager(manager);

        searchEntities();
    }







    public void callAddDialog(boolean isNew){
        FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
        Fragment prev = mainActivity.getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment =  AdminCompanyDialogFragment.newInstance(mainActivity, (isNew)?null:company, license,this::dialogClosed);
        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(company != null){
            description = company.getName();
        }

        String msg = "Esta seguro que desea eliminar \'"+description+"\' permanentemente?";
        final Dialog d = Funciones.getCustomDialog2Btn(mainActivity,getResources().getColor(R.color.red_700),"Delete", msg,R.drawable.delete,null, null);
        CardView btnAceptar = d.findViewById(R.id.btnPositive);
        CardView btnCancelar = d.findViewById(R.id.btnNegative);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                company = null;
                d.dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();
        Window window = d.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);

    }



    public void searchEntities(){
        startLoading();
        apiInterface.getCompanies(license.getId()).enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {
                ArrayList<CompanyRowModel> lrm = new ArrayList<>();
                endLoading();

                if(response.isSuccessful()){
                    List<Company> list = response.body();
                    for(Company obj : list){
                        //String code, String name, String rnc, String address, String address2, String phone, String phone2, String logo, Company company
                        lrm.add(new CompanyRowModel(obj.getCode(),obj.getName(),obj.getRnc(),obj.getAddress(),obj.getAddress2(),obj.getPhone(),obj.getPhone2(),obj.getLogo(),obj));
                    }
                }
                refreshList(lrm);

            }

            @Override
            public void onFailure(Call<List<Company>> call, Throwable t) {
                Snackbar.make(getView(),t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                endLoading();
            }
        });

    }


    public void refreshList(ArrayList<CompanyRowModel> list){
        CompanyEditionAdapter la = new CompanyEditionAdapter(mainActivity, this,list);
        rvList.setAdapter(la);
        rvList.invalidate();
    }

    public void startLoading(){
        pb.setVisibility(View.VISIBLE);
    }
    public void endLoading(){
        pb.setVisibility(View.GONE);
    }





    @Override
    public void onClick(Object obj) {
        CompanyRowModel item = (CompanyRowModel)obj;
        company =  (Company) item.getCompany();
        showOptions(false);

    }

    @Override
    public void dialogClosed(Object o) {
        if(o instanceof AdminCompanyDialogFragment.AdminCompanyDialogFragmentResponse){
            searchEntities();
        }
    }


    private void showOptions(boolean fromMenu) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mainActivity);
        bottomSheetDialog.setContentView(R.layout.admin_licenses_menu);

        View settings = bottomSheetDialog.findViewById(R.id.trSettings);
        View edit = bottomSheetDialog.findViewById(R.id.trEdit);
        View add = bottomSheetDialog.findViewById(R.id.trAdd);

        settings.setVisibility(View.GONE);
        edit.setVisibility(fromMenu?View.GONE:View.VISIBLE);
        add.setVisibility(fromMenu?View.VISIBLE:View.GONE);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                mainActivity.setAdminLicenseSetupFragment(license);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                callAddDialog(false);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                callAddDialog(true);
            }
        });
        bottomSheetDialog.show();
    }

}
