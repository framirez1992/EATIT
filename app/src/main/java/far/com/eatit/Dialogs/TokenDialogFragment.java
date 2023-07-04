package far.com.eatit.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.SimpleSeleccionRowModel;
import far.com.eatit.Adapters.SimpleSelectionRowAdapter;
import far.com.eatit.AdminLicenseTokens;
import far.com.eatit.CloudFireStoreObjects.Token;
import far.com.eatit.CloudFireStoreObjects.UsersDevices;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;

public class TokenDialogFragment extends DialogFragment implements ListableActivity, OnFailureListener {

    public Token tempObj;
    public String codeLicense;

    LinearLayout llMain, llTables, llSave, llSave2, llBack, llNext;
    CheckBox cbAutoDelete, cbAllTables;
    Spinner spnTokenType;

    RecyclerView rvDevices, rvTables;
    ArrayList<SimpleSeleccionRowModel> userDevicesList, selectedUserDevicesList, tablesList, selectedTablesList;
    SimpleSelectionRowAdapter adapter, adapterTables;
    FirebaseFirestore fs;
    ArrayList<UsersDevices> userDevices;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static TokenDialogFragment newInstance(Token token, String codeLicense) {

        TokenDialogFragment f = new TokenDialogFragment();
        f.tempObj = token;
        f.codeLicense = codeLicense;
        f.fs = FirebaseFirestore.getInstance();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(token != null) {
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
        return inflater.inflate(R.layout.dialog_add_edit_token, container, true);
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
        llMain = view.findViewById(R.id.llMain);
        llSave = view.findViewById(R.id.llSave);
        llNext = view.findViewById(R.id.llNext);
        cbAutoDelete = view.findViewById(R.id.cbAutoDelete);
        spnTokenType = view.findViewById(R.id.spn);
        rvDevices = view.findViewById(R.id.rvDevices);

        llTables = view.findViewById(R.id.llTables);
        llSave2 = view.findViewById(R.id.llSave2);
        llBack = view.findViewById(R.id.llBack);
        cbAllTables = view.findViewById(R.id.cbAllTables);
        rvTables = view.findViewById(R.id.rvTables);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             saveEvent();
            }
        });

        llSave2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
            }
        });

        llNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llMain.setVisibility(View.GONE);
                llTables.setVisibility(View.VISIBLE);
            }
        });

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llMain.setVisibility(View.VISIBLE);
                llTables.setVisibility(View.GONE);
            }
        });

        cbAllTables.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapterTables.setSelectAll(isChecked);
            }
        });

        ArrayList<KV> tokentype = new ArrayList<>();
        tokentype.add(new KV(CODES.TOKEN_TYPE_LOGIN, "Desbloqueo"));
        tokentype.add(new KV(CODES.TOKEN_TYPE_INITIAL_LOAD, "Carga inicial"));
        tokentype.add(new KV(CODES.TOKEN_TYPE_ACTUALIZATION, "Actualizacion"));
        spnTokenType.setAdapter(new ArrayAdapter<KV>(getContext(),android.R.layout.simple_list_item_1, tokentype));
        spnTokenType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    llSave.setVisibility(((KV)parent.getAdapter().getItem(position)).getKey().equals(CODES.TOKEN_TYPE_ACTUALIZATION)?View.GONE:View.VISIBLE);
                    llNext.setVisibility(((KV)parent.getAdapter().getItem(position)).getKey().equals(CODES.TOKEN_TYPE_ACTUALIZATION)?View.VISIBLE:View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        userDevicesList = new ArrayList<>();
        selectedUserDevicesList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        rvDevices.setLayoutManager(manager);
        adapter = new SimpleSelectionRowAdapter(getActivity(), userDevicesList,selectedUserDevicesList);
        rvDevices.setAdapter(adapter);



        tablesList = new ArrayList<>();
        for(String i :Tablas.tablesFireBase){
            tablesList.add(new SimpleSeleccionRowModel(i,i,false));
        }

        selectedTablesList = new ArrayList<>();
        LinearLayoutManager manager2 = new LinearLayoutManager(getActivity());
        rvTables.setLayoutManager(manager2);
        adapterTables = new SimpleSelectionRowAdapter(getActivity(), tablesList,selectedTablesList);
        rvTables.setAdapter(adapterTables);


        if(tempObj != null){//EDIT
            setUpToEditProductType();
        }
    }

    public void saveEvent(){
        llSave.setEnabled(false);
        if(tempObj == null){
            Save();
        }else{
            EditLicense();
        }
    }

    public boolean validateToken(){
        if(selectedUserDevicesList.size() == 0){
            Snackbar.make(getView(), "Debe seleccionar al menos 1 device", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(((KV)spnTokenType.getSelectedItem()).getKey().equals(CODES.TOKEN_TYPE_ACTUALIZATION) && selectedTablesList.size() == 0){
            Snackbar.make(getView(), "Debe seleccionar las tablas a para la actualizacion", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void Save(){
        if(validateToken()) {
            SaveToken();
        }

        llSave.setEnabled(true);

    }

    public void SaveToken(){
        /*
        try {
            WriteBatch lote = adminLicenseTokens.getFs().batch();

            Token t = new Token();
            t.setAutodelete(cbAutoDelete.isChecked());
            KV tokenType =((KV)spnTokenType.getSelectedItem());
            if(tokenType.getKey().equals(CODES.TOKEN_TYPE_ACTUALIZATION)){
                ArrayList<String>  data = new ArrayList<>();
                for(SimpleSeleccionRowModel tables: selectedTablesList){
                data.add(tables.getCode());
                }
                t.setExtradata(Funciones.toJson(data));
            }
            t.setType(tokenType.getKey());
            for(SimpleSeleccionRowModel dev: selectedUserDevicesList){
              t.setCode(dev.getCode());
              lote.set(adminLicenseTokens.getFs().collection(Tablas.generalUsers).document(codeLicense).collection(Tablas.generalUsersToken).document(t.getCode()), t.toMap());
            }

            lote.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dismiss();
                        }
                    }).addOnFailureListener(this);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }*/


    }


    public void EditLicense(){
        /*
        try {
            String code = "";//etCode.getText().toString();

            tempObj.setCode(code);
            tempObj.setAutodelete(cbAutoDelete.isChecked());

            adminLicenseTokens.getFs().collection(Tablas.generalUsers).document(codeLicense).collection(Tablas.generalUsersToken)
                    .document(code).update(tempObj.toMap())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dismiss();
                }
            }).addOnFailureListener(this);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }*/


    }



    public void setUpToEditProductType(){
        //etCode.setText(tempObj.getCode());
        //etCode.setEnabled(false);
        cbAutoDelete.setChecked(tempObj.isAutodelete());
        for(int i=0; i<spnTokenType.getAdapter().getCount(); i++){
            if(((KV)spnTokenType.getAdapter().getItem(i)).getKey().equals(tempObj.getType())){
                spnTokenType.setSelection(i);
                break;
            }
        }

    }


    public void refreshDevicesList(){
        userDevicesList.clear();
        for(UsersDevices t: userDevices){
            userDevicesList.add(new SimpleSeleccionRowModel(t.getCODE(), "Device: "+t.getCODEDEVICE()+"  - User: "+t.getCODEUSER(), false) );
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }


    @Override
    public void onClick(Object obj) {

    }
}
