package far.com.eatit;


import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import far.com.eatit.API.APIClient;
import far.com.eatit.API.APIInterface;
import far.com.eatit.API.models.License;
import far.com.eatit.Adapters.LicenseAdapter;
import far.com.eatit.Adapters.Models.LicenseRowModel;
//import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.Controllers.LicenseController;
import far.com.eatit.Dialogs.LicenseDialogFragment;
import far.com.eatit.Interfases.DialogCaller;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.Utils.Funciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminLicensesFragment extends Fragment implements ListableActivity, DialogCaller {


    Main mainActivity;
    APIInterface apiInterface;
    RecyclerView rvList;
    ProgressBar pb;
    License license;
    public AdminLicensesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AdminLicensesFragment newInstance(Main mainActivity) {
        AdminLicensesFragment fragment = new AdminLicensesFragment();
        fragment.mainActivity = mainActivity;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        apiInterface = APIClient.getClient(mainActivity).create(APIInterface.class);
        return inflater.inflate(R.layout.fragment_admin_licenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.llMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLicenseOptions(true);
            }
        });
        rvList = view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(mainActivity));
        pb = view.findViewById(R.id.pb);

        searchLicenses();
    }

    @Override
    public void dialogClosed(Object o) {

        if(o instanceof LicenseDialogFragment.LicenseDialogFragmentResponse){
            searchLicenses();
        }


    }


    public void searchLicenses(){
        startLoading();
        apiInterface.getLicenses().enqueue(new Callback<List<License>>() {
            @Override
            public void onResponse(Call<List<License>> call, Response<List<License>> response) {
                ArrayList<LicenseRowModel> lrm = new ArrayList<>();
                endLoading();

                if(response.isSuccessful()){
                    List<License> licenseList = response.body();
                    for(License l : licenseList){
                        lrm.add(new LicenseRowModel(l));
                    }

                }
                refreshList(lrm);

            }

            @Override
            public void onFailure(Call<List<License>> call, Throwable t) {
                Snackbar.make(getView(),t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                endLoading();
            }
        });

    }



    public void refreshList(ArrayList<LicenseRowModel> list){
        LicenseAdapter la = new LicenseAdapter(mainActivity, this,list);
        rvList.setAdapter(la);
        rvList.invalidate();
    }

    public void startLoading(){
        pb.setVisibility(View.VISIBLE);
    }
    public void endLoading(){
        pb.setVisibility(View.GONE);
    }



     public void callAddDialog(boolean isNew){
        FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
        Fragment prev =  mainActivity.getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        LicenseDialogFragment newFragment = null;
        if(isNew){
            newFragment = LicenseDialogFragment.newInstance(mainActivity,this::dialogClosed, null);
        }else {
            newFragment = LicenseDialogFragment.newInstance(mainActivity,this::dialogClosed, license);
        }

        // Create and show the dialog.
        newFragment.show(ft,"");
    }



    public void showLicenseSetup(){
        //adminLicenseSetupFragment.setLicense(licenses);
        //setFragment(adminLicenseSetupFragment);
    }
    public void callDeleteConfirmation(){

        String msg = "Esta seguro que desea eliminar la licencia \'"+license.getCode()+" - "+license.getClientName()+"\' permanentemente?";
        final Dialog d = Funciones.getCustomDialog2Btn(mainActivity,getResources().getColor(R.color.red_700),"Delete", msg,R.drawable.delete,null, null);
        CardView btnAceptar = d.findViewById(R.id.btnPositive);
        CardView btnCancelar = d.findViewById(R.id.btnNegative);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                license = null;
                searchLicenses();
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
      @Override
    public void onClick(Object obj) {
        //Usar BottomSheetDialog
          //registering popup with OnMenuItemClickListener
        LicenseRowModel licenseRowModel = (LicenseRowModel)obj;
        license = licenseRowModel.getLicense();
        showLicenseOptions(false);
    }

    private void showLicenseOptions(boolean fromMenu) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mainActivity);
        bottomSheetDialog.setContentView(R.layout.admin_licenses_menu);

        View settings = bottomSheetDialog.findViewById(R.id.trSettings);
        View edit = bottomSheetDialog.findViewById(R.id.trEdit);
        View add = bottomSheetDialog.findViewById(R.id.trAdd);

        settings.setVisibility(fromMenu?View.GONE:View.VISIBLE);
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
