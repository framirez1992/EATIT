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
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;

import java.util.ArrayList;
import java.util.UUID;

import far.com.eatit.Adapters.Models.SimpleSeleccionRowModel;
import far.com.eatit.Adapters.SimpleSelectionRowAdapter;
import far.com.eatit.CloudFireStoreObjects.ProductsControl;
import far.com.eatit.CloudFireStoreObjects.UserInbox;
import far.com.eatit.Controllers.ProductsControlController;
import far.com.eatit.Controllers.ProductsSubTypesController;
import far.com.eatit.Controllers.ProductsTypesController;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Controllers.UserControlController;
import far.com.eatit.Controllers.UserInboxController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;

public class ProductBlockSelectionDialog extends DialogFragment implements OnFailureListener {


    ArrayList<SimpleSeleccionRowModel> selectedObjs = new ArrayList<>() ;
    LinearLayout llSave;
    Spinner spnStatus, spnFamily, spnGroup;
    RecyclerView rvList;
    KV status, family, group;
    boolean unlock;

    public  static ProductBlockSelectionDialog newInstance() {
        ProductBlockSelectionDialog f = new ProductBlockSelectionDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();

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
        //Funciones.showKeyBoard(etName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.product_block_dialog, container, true);
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


    public void setUnlock(boolean b){
        unlock = b;
    }
    public void init(View view){
        llSave = view.findViewById(R.id.llSave);
        spnStatus = view.findViewById(R.id.spnStatus);
        spnFamily = view.findViewById(R.id.spnFamilia);
        spnGroup = view.findViewById(R.id.spnGrupo);
        rvList = view.findViewById(R.id.rvList);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvList.setLayoutManager(manager);

        ProductsControlController.getInstance(getContext()).fillSpinnerStatus(spnStatus, unlock);
        if(unlock){
            ProductsTypesController.getInstance(getActivity()).fillSpinnerForLockedProducts(spnFamily, true);
        }else{
            ProductsTypesController.getInstance(getActivity()).fillSpinner(spnFamily, true);
        }



        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                selectedObjs = ((SimpleSelectionRowAdapter)rvList.getAdapter()).getSelectedObjects();
                Save();
            }
        });


        spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View view, int position, long id) {
                status = (KV)p.getSelectedItem();
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View view, int position, long id) {
                family = (KV)p.getSelectedItem();
                if(unlock){
                    ProductsSubTypesController.getInstance(getActivity()).fillSpinnerForLockedProducts(spnGroup, false, family.getKey());
                }else{
                    ProductsSubTypesController.getInstance(getActivity()).fillSpinner(spnGroup, false, family.getKey());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View view, int position, long id) {
                group = (KV)p.getSelectedItem();
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnStatus.setVisibility(View.GONE);
        status = (KV)spnStatus.getSelectedItem();
        refresh();
    }

    public boolean validate(){
        if(selectedObjs.size() == 0){
            Snackbar.make(getView(), "Seleccione por lo menos 1 producto", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    public void Save(){

        if(validate()) {
            SaveProductControl();
        }else{
            llSave.setEnabled(true);
        }
    }

    public void SaveProductControl(){
        try {

            ArrayList<ProductsControl> list = new ArrayList<>();
            if(unlock){
                for (SimpleSeleccionRowModel ssrm : selectedObjs) {
                    ProductsControl pc = ProductsControlController.getInstance(getActivity()).getProductsControlByCodeProduct(ssrm.getCode()).get(0);
                    pc.setBLOQUED("0");
                    list.add(pc);

                    String where = ProductsControlController.CODE+" = ?";
                    String[]args = new String[]{pc.getCODE()};
                    ProductsControlController.getInstance(getActivity()).update(pc, where, args);
                }
            }else {
                for (SimpleSeleccionRowModel ssrm : selectedObjs) {
                    ArrayList<ProductsControl> apc = ProductsControlController.getInstance(getActivity()).getProductsControlByCodeProduct(ssrm.getCode());

                    ProductsControl pc = (apc != null && apc.size() >0)
                            ?apc.get(0)
                            :new ProductsControl(ssrm.getCode(), ssrm.getCode(), "1");
                    pc.setBLOQUED("1");
                    list.add(pc);
                    ProductsControlController.getInstance(getActivity()).insert(pc);
                }
            }

            String productsIn="";
            for (SimpleSeleccionRowModel ssrm : selectedObjs) {
                productsIn+=((!productsIn.equals(""))?",":"")+"'"+ssrm.getCode()+"'";
            }

            ProductsControlController.getInstance(getActivity()).sendToFireBase(list);

            if(!unlock) {
                ArrayList<UserInbox> uis = UserInboxController.getInstance(getActivity()).getUsersInboxForBloquedProduct(Funciones.getCodeuserLogged(getActivity()),productsIn);
                if(uis.size() > 0) {
                    UserInboxController.getInstance(getActivity()).sendToFireBase(uis);
                }

            }


            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }


    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }

    public void refresh(){

        String where = " 1 = 1";
        String[] args = null;
        ArrayList<String> values = new ArrayList<>();
        if(status!= null){
            where+=" AND ifnull(pc."+ProductsControlController.BLOQUED+", '0') = ?";
            values.add(status.getKey());
        }
        if(family != null && !family.getKey().equals("0")){
            where+=" AND pt."+ProductsTypesController.CODE+" = ?";
            values.add(family.getKey());
        }

        if(group != null && !group.getKey().equals("-1")){
            where+=" AND ps."+ProductsSubTypesController.CODE+" = ?";
            values.add(group.getKey());
        }

        if(values.size() >0)
            args = values.toArray(new String[values.size()]);

        rvList.setAdapter(new SimpleSelectionRowAdapter(getActivity(), ProductsControlController.getInstance(getActivity()).getSSRMProducts(where, args), selectedObjs));
        rvList.getAdapter().notifyDataSetChanged();
        rvList.invalidate();
    }
}
