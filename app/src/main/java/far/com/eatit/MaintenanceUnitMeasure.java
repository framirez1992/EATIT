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
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import far.com.eatit.API.APIClient;
import far.com.eatit.API.APIInterface;
import far.com.eatit.API.models.LoginResponse;
import far.com.eatit.API.models.MeasureUnit;
import far.com.eatit.API.models.ProductSubType;
import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Adapters.SimpleRowEditionAdapter;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.MeasureUnits;
import far.com.eatit.Controllers.LicenseController;
import far.com.eatit.Controllers.MeasureUnitsController;
import far.com.eatit.Controllers.MeasureUnitsInvController;
import far.com.eatit.Dialogs.MeasureUnitDialogFragment;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Interfases.DialogCaller;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.Utils.Funciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaintenanceUnitMeasure extends Fragment implements ListableActivity, DialogCaller {

    Main mainActivity;
    String type;
    APIInterface apiInterface;
    LoginResponse loginResponse;


    RecyclerView rvList;
    ArrayList<SimpleRowModel> objects;
    SimpleRowEditionAdapter adapter;
    //MeasureUnitsController measureUnitsController;
    //MeasureUnitsInvController measureUnitsInvController;
    MeasureUnit measureUnit;
    //Licenses licence;
    String lastSearch = null;


    public MaintenanceUnitMeasure(){

    }

    public static MaintenanceUnitMeasure newInstance(Main mainActivity, String type){
        MaintenanceUnitMeasure fragment = new MaintenanceUnitMeasure();
        fragment.mainActivity = mainActivity;
        fragment.type = type;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loginResponse = Funciones.getLoginResponseData(mainActivity);
        apiInterface = APIClient.getClient(mainActivity).create(APIInterface.class);
        return inflater.inflate(R.layout.activity_maintenance_screen, container, false);
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

        rvList = view.findViewById(R.id.rvList);
        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(mainActivity);
        rvList.setLayoutManager(manager);
        adapter = new SimpleRowEditionAdapter(mainActivity,this, objects);
        rvList.setAdapter(adapter);

       searchEntities();
    }





    public void callAddDialog(boolean isNew){
        FragmentTransaction ft =  mainActivity.getSupportFragmentManager().beginTransaction();
        Fragment prev = mainActivity.getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment =  MeasureUnitDialogFragment.newInstance(mainActivity,type, (isNew)?null:measureUnit,this::dialogClosed);
        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(measureUnit != null){
            description = measureUnit.getDescription();
        }

        final Dialog d = new Dialog(mainActivity);
        d.setTitle("Delete");
        d.setContentView(R.layout.msg_2_buttons);
        TextView tvMsg = d.findViewById(R.id.tvMsg);
        Button btnAceptar = d.findViewById(R.id.btnPositive);
        Button btnCancelar = d.findViewById(R.id.btnNegative);

        tvMsg.setText("Esta seguro que desea eliminar \'"+description+"\' permanentemente?");
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(measureUnit != null){
                    /*
                    if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                        measureUnitsController.deleteFromFireBase(measureUnit);
                    }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                        measureUnitsInvController.deleteFromFireBase(measureUnit);
                    }*/

                }
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

    }

    /*
    public void refreshList(String data){
        objects.clear();
        String where = (data!= null)?MeasureUnitsController.DESCRIPTION+" like  ? ":null;
        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            objects.addAll(measureUnitsController.getMeasureUnitsSRM(where, (data != null)?new String[]{data+"%"}:null, null));
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            objects.addAll(measureUnitsInvController.getMeasureUnitsSRM(where, (data != null)?new String[]{data+"%"}:null, null));
        }

        adapter.notifyDataSetChanged();
    }*/


    @Override
    public void onClick(Object obj) {
        SimpleRowModel sr = (SimpleRowModel)obj;
        measureUnit = (MeasureUnit)sr.getEntity();

        /*
        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            measureUnit = measureUnitsController.getMeasureUnitByCode(sr.getId());
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            measureUnit = measureUnitsInvController.getMeasureUnitByCode(sr.getId());
        }

         */
        showOptions(false);

    }


    public SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if(!query.equals("")) {
                lastSearch = query;
                //refreshList(lastSearch);
                return true;
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(newText.equals("")){
                lastSearch = null;
                //refreshList(lastSearch);
                return true;
            }
            return false;
        }
    };

    @Override
    public void dialogClosed(Object o) {
        searchEntities();
    }

    public void searchEntities(){
        startLoading();
        apiInterface.getMeasureUnits(loginResponse.getLicense().getId()).enqueue(new Callback<List<MeasureUnit>>() {
            @Override
            public void onResponse(Call<List<MeasureUnit>> call, Response<List<MeasureUnit>> response) {
                ArrayList<SimpleRowModel> lrm = new ArrayList<>();
                endLoading();

                if(response.isSuccessful()){
                    List<MeasureUnit> list = response.body();
                    for(MeasureUnit obj : list){
                        //String id, String text, Object entity
                        lrm.add(new SimpleRowModel(String.valueOf(obj.getId()),obj.getDescription(),obj));
                    }
                }else{
                    Snackbar.make(getView(),response.errorBody().byteStream().toString(), BaseTransientBottomBar.LENGTH_LONG).show();
                }
                refreshList(lrm);

            }

            @Override
            public void onFailure(Call<List<MeasureUnit>> call, Throwable t) {
                Snackbar.make(getView(),t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                endLoading();
            }
        });

    }


    public void refreshList(ArrayList<SimpleRowModel> list){
        SimpleRowEditionAdapter la = new SimpleRowEditionAdapter(mainActivity, this,list);
        rvList.setAdapter(la);
        rvList.invalidate();
    }

    private void startLoading(){
        mainActivity.showWaitingDialog();
    }

    private void endLoading(){
        mainActivity.dismissWaitingDialog();
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
