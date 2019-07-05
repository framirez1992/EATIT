package far.com.eatit.Dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;

import java.util.ArrayList;
import java.util.UUID;

import far.com.eatit.Adapters.Models.SimpleSeleccionRowModel;
import far.com.eatit.Adapters.SimpleSelectionRowAdapter;
import far.com.eatit.CloudFireStoreObjects.TableFilter;
import far.com.eatit.Controllers.ProductsSubTypesController;
import far.com.eatit.Controllers.ProductsTypesController;
import far.com.eatit.Controllers.TableFilterController;
import far.com.eatit.Controllers.UserTypesController;
import far.com.eatit.Controllers.UsersController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;

public class TableFilterDialogFragment extends DialogFragment implements OnFailureListener {

    private static  Object tempObj;

    LinearLayout llMainScreen,llOrigins,llDestiny, llSave, llNext, llNextOrigin, llBackOrigin, llBackDestiny;
    Spinner spnTable, spnTask, spnOriginType, spnDestinyType;
    CheckBox cbEnabled;

    TableFilterController tableFilterController;
    KV table, task, originType, destinyType;
    RecyclerView rvListOrigins,rvListDestiny ;
    ArrayList<SimpleSeleccionRowModel> selectedOrigins = new ArrayList<>();
    ArrayList<SimpleSeleccionRowModel> selectedDestinys = new ArrayList<>();


    public  static TableFilterDialogFragment newInstance(Object pt) {

        tempObj = pt;
        TableFilterDialogFragment f = new TableFilterDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(pt != null) {
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
        tableFilterController = TableFilterController.getInstance(getActivity());

    }

    @Override
    public void onStart() {
        super.onStart();
       // Funciones.showKeyBoard(etPassword);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.table_filter_dialog_fragment, container, true);
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
        llMainScreen = view.findViewById(R.id.llMainScreen);
        llOrigins = view.findViewById(R.id.llOrigins);
        llDestiny = view.findViewById(R.id.llDestiny);
        llSave = view.findViewById(R.id.llSave);
        llNext = view.findViewById(R.id.llNext);
        llNextOrigin = view.findViewById(R.id.llNextOrigin);
        llBackOrigin = view.findViewById(R.id.llBackOrigin);
        llBackDestiny = view.findViewById(R.id.llBackDestiny);
        spnTable = view.findViewById(R.id.spnTable);
        spnTask = view.findViewById(R.id.spnTask);
        spnOriginType = view.findViewById(R.id.spnOriginType);
        spnDestinyType = view.findViewById(R.id.spnDestinyType);
        cbEnabled = view.findViewById(R.id.cbEnabled);
        rvListOrigins = view.findViewById(R.id.rvListOrigins);
        rvListOrigins.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvListDestiny = view.findViewById(R.id.rvListDestiny);
        rvListDestiny.setLayoutManager(new LinearLayoutManager(getActivity()));

        tableFilterController.fillSpnTables(spnTable,false);
        spnTable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                table = (KV)parent.getSelectedItem();
                tableFilterController.fillSpnTaskByTable(spnTask,table.getKey());

                if(tempObj != null){//Cuando se abra el dialogo y se entre en modo edicion.
                    setSpnPosition(spnTask, ((TableFilter)tempObj).getTask());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnTask.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                task = (KV)parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tableFilterController.fillSpnOriginType(spnOriginType);
        tableFilterController.fillSpnDestinyType(spnDestinyType);

        spnOriginType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                originType = (KV)parent.getSelectedItem();

                if(tempObj == null) {
                    selectedOrigins.clear();
                }

                refreshOriginList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnDestinyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                destinyType = (KV)parent.getSelectedItem();

                if(tempObj == null) {
                    selectedDestinys.clear();//Solo reiniciar los destinos cuando se este creando nuevo
                }

                refreshDestinyList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        View.OnClickListener navigateListenener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigate(v.getId());
            }
        };

        llNext.setOnClickListener(navigateListenener);
        llNextOrigin.setOnClickListener(navigateListenener);
        llBackOrigin.setOnClickListener(navigateListenener);
        llBackDestiny.setOnClickListener(navigateListenener);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                    EditTableFilter();
                }
            }
        });

        if(tempObj != null){//EDIT
            setUpToEditTableFilter();
        }
    }


    public boolean validate(){
        boolean result = true;
        if(spnTable.getSelectedItem() == null){
            Snackbar.make(getView(),"Seleccione la tabla", Snackbar.LENGTH_LONG).show();
            result = false;
        }else if(spnTask.getSelectedItem() == null){
            Snackbar.make(getView(),"Seleccione la tarea", Snackbar.LENGTH_LONG).show();
            result = false;
        }else if(spnOriginType.getSelectedItem() == null){
            Snackbar.make(getView(),"Seleccione el tipo de origen", Snackbar.LENGTH_LONG).show();
            result = false;
        }else if(spnDestinyType.getSelectedItem() == null){
            Snackbar.make(getView(),"Seleccione el tipo de destino", Snackbar.LENGTH_LONG).show();
            result = false;
        }else if(selectedOrigins.size() == 0){
            Snackbar.make(getView(),"Debe seleccionar al menos un origen", Snackbar.LENGTH_LONG).show();
            result = false;
        }else if(selectedDestinys.size() == 0){
            Snackbar.make(getView(),"Debe seleccionar al menos un destino", Snackbar.LENGTH_LONG).show();
            result = false;
        }

        return result;
    }

    public void Save(){

    refreshSelectedOrigins();
    refreshSelectedDestinys();

        if(validate()) {
            SaveTableFilter();
        }else{
            llSave.setEnabled(true);
        }
    }

    public void SaveTableFilter(){
        try {
            ArrayList<TableFilter> objects = new ArrayList<>();
            String TABLES = table.getKey();
            String TASK = task.getKey();
            for(SimpleSeleccionRowModel o: selectedOrigins){
                String PRODUCTTYPE = null;
                String PRODUCTSUBTYPE = null;
                if(originType.getKey().equals(CODES.TABLE_FILTER_ORIGIN_PRODUCTTYPE)){
                    PRODUCTTYPE = o.getCode();
                }else if(originType.getKey().equals(CODES.TABLE_FILTER_ORIGIN_PRODUCTSUBTYPE)){
                    PRODUCTSUBTYPE = o.getCode();
                }

                for(SimpleSeleccionRowModel d: selectedDestinys){
                    String USER = null;
                    String USERTYPE = null;
                    if(destinyType.getKey().equals(CODES.TABLE_FILTER_DESTINY_USER)){
                        USER = d.getCode();
                    }else if(destinyType.getKey().equals(CODES.TABLE_FILTER_DESTINY_USERTYPE)){
                        USERTYPE = d.getCode();
                    }
                    TableFilter tf = TableFilterController.getInstance(getActivity()).findTableFilterData(TABLES,TASK, USER, USERTYPE, PRODUCTTYPE, PRODUCTSUBTYPE);
                    if(tf == null){
                        tf = new TableFilter(Funciones.generateCode(), TABLES,USER, USERTYPE,PRODUCTTYPE, PRODUCTSUBTYPE,TASK, "", ((cbEnabled.isChecked())?"1":"0"));
                    }
                    objects.add(tf);
                }

            }

            tableFilterController.sendToFireBase(objects);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void EditTableFilter(){
        try {
            ArrayList<TableFilter> list = new ArrayList<>();
            TableFilter tf = ((TableFilter)tempObj);
            tf.setEnabled((cbEnabled.isChecked())?"1":"0");
            tf.setMdate(null);
            /* user.setUSERNAME(etName.getText().toString());
            user.setPASSWORD(etPassword.getText().toString().trim());
            user.setCOMPANY("01");
            user.setENABLED(cbEnabled.isChecked());
            user.setROLE(((KV)spnRol.getSelectedItem()).getKey());*/
            list.add(tf);
            tableFilterController.sendToFireBase(list);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void setUpToEditTableFilter(){
        TableFilter tf = ((TableFilter)tempObj);
        setSpnPosition(spnTable, tf.getTables());
        setSpnPosition(spnOriginType, tableFilterController.getCodeOriginTypeByTableFilter(tf));
        setSpnPosition(spnDestinyType, tableFilterController.getCodeDestinyTypeByTableFilter(tf));
        cbEnabled.setChecked(tf.getEnabled().equals("1"));

        spnTable.setEnabled(false);
        spnTask.setEnabled(false);
        spnOriginType.setEnabled(false);
        spnDestinyType.setEnabled(false);

        boolean pt = tableFilterController.getCodeOriginTypeByTableFilter(tf).equals(CODES.TABLE_FILTER_ORIGIN_PRODUCTTYPE);
        ArrayList<SimpleSeleccionRowModel> origins = new ArrayList<>();
        if (pt) {
            origins = ProductsTypesController.getInstance(getActivity()).getProductTypesSSRM(null, null, null);
        } else {
            origins = ProductsSubTypesController.getInstance(getActivity()).getProductSubTypesSSRM(null, null, null);
        }

        for(SimpleSeleccionRowModel ssrm: origins){
            if((pt && ssrm.getCode().equals(tf.getProducttype())) || (!pt && ssrm.getCode().equals(tf.getProductsubtype()))){
              selectedOrigins.add(ssrm);

                break; }
            }

        boolean u = tableFilterController.getCodeDestinyTypeByTableFilter(tf).equals(CODES.TABLE_FILTER_DESTINY_USER);
        ArrayList<SimpleSeleccionRowModel> destinys = new ArrayList<>();
        if (u) {
            destinys = UsersController.getInstance(getActivity()).getUserSSRM(null, null, null);
        } else {
            destinys = UserTypesController.getInstance(getActivity()).getUserTypesSSRM(null, null, null);
        }

        for(SimpleSeleccionRowModel ssrm: destinys){
            if((u && ssrm.getCode().equals(tf.getUser())) || (!u && ssrm.getCode().equals(tf.getUsertype()))){
                selectedDestinys.add(ssrm);
                break; }
        }


    }

    public void setSpnPosition(Spinner spn, String key){
        for(int i = 0; i< spn.getAdapter().getCount(); i++){
            if(((KV)spn.getAdapter().getItem(i)).getKey().equals(key)){
                spn.setSelection(i);
                break;
            }
        }
    }




    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }

    public void navigate(int id){
        switch (id){
            case R.id.llNext :
            case R.id.llBackDestiny:
                llMainScreen.setVisibility(View.GONE);
                llDestiny.setVisibility(View.GONE);
                llOrigins.setVisibility(View.VISIBLE);
                return;
            case R.id.llNextOrigin:
                llMainScreen.setVisibility(View.GONE);
                llDestiny.setVisibility(View.VISIBLE);
                llOrigins.setVisibility(View.GONE);
                return;
            case R.id.llBackOrigin:
                llMainScreen.setVisibility(View.VISIBLE);
                llDestiny.setVisibility(View.GONE);
                llOrigins.setVisibility(View.GONE);
                return;
        }
    }

    public void refreshOriginList(){
        try {
            String where = null;
            String[] args = null;

            ArrayList<SimpleSeleccionRowModel> origins = new ArrayList<>();
            if (originType.getKey().equals(CODES.TABLE_FILTER_ORIGIN_PRODUCTTYPE)) {
                if(tempObj != null){
                    where = ProductsTypesController.CODE+" = ?";
                    args = new String[]{((TableFilter)tempObj).getProducttype()};
                }
                origins = ProductsTypesController.getInstance(getActivity()).getProductTypesSSRM(where, args, null);
            } else if (originType.getKey().equals(CODES.TABLE_FILTER_ORIGIN_PRODUCTSUBTYPE)) {
                if(tempObj != null){
                    where = ProductsSubTypesController.CODE+" = ?";
                    args = new String[]{((TableFilter)tempObj).getProductsubtype()};
                }
                origins = ProductsSubTypesController.getInstance(getActivity()).getProductSubTypesSSRM(where, args, null);
            }
            rvListOrigins.setAdapter(new SimpleSelectionRowAdapter(getActivity(), origins, selectedOrigins, (tempObj != null)));
            rvListOrigins.getAdapter().notifyDataSetChanged();
            rvListOrigins.invalidate();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void refreshSelectedOrigins(){
        if(rvListOrigins.getAdapter() != null){
            selectedOrigins = ((SimpleSelectionRowAdapter)rvListOrigins.getAdapter()).getSelectedObjects();
        }
    }

    public void refreshDestinyList(){
        try {
            String where = null;
            String[] args = null;

            ArrayList<SimpleSeleccionRowModel> destinys = new ArrayList<>();
            if (destinyType.getKey().equals(CODES.TABLE_FILTER_DESTINY_USER)) {
                if(tempObj != null){
                    where = UsersController.CODE+" = ?";
                    args = new String[]{((TableFilter)tempObj).getUser()};
                }
                destinys = UsersController.getInstance(getActivity()).getUserSSRM(where, args, null);
            } else if (destinyType.getKey().equals(CODES.TABLE_FILTER_DESTINY_USERTYPE)) {
                if(tempObj != null){
                    where = UserTypesController.CODE+" = ?";
                    args = new String[]{((TableFilter)tempObj).getUsertype()};
                }
                destinys = UserTypesController.getInstance(getActivity()).getUserTypesSSRM(where, args, null);
            }
            rvListDestiny.setAdapter(new SimpleSelectionRowAdapter(getActivity(), destinys, selectedDestinys, (tempObj != null)));
            rvListDestiny.getAdapter().notifyDataSetChanged();
            rvListDestiny.invalidate();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void refreshSelectedDestinys(){
        if(rvListDestiny.getAdapter() != null){
            selectedDestinys = ((SimpleSelectionRowAdapter)rvListDestiny.getAdapter()).getSelectedObjects();
        }
    }
}
