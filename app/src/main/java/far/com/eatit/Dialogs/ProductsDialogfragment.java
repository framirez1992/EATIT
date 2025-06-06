package far.com.eatit.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.UUID;

import far.com.eatit.Adapters.EditSelectionRowAdapter;
import far.com.eatit.Adapters.Models.EditSelectionRowModel;
import far.com.eatit.Adapters.Models.SimpleSeleccionRowModel;
import far.com.eatit.Adapters.SimpleSelectionRowAdapter;
import far.com.eatit.CloudFireStoreObjects.Products;
import far.com.eatit.CloudFireStoreObjects.ProductsMeasure;
import far.com.eatit.Controllers.MeasureUnitsController;
import far.com.eatit.Controllers.MeasureUnitsInvController;
import far.com.eatit.Controllers.ProductsController;
import far.com.eatit.Controllers.ProductsInvController;
import far.com.eatit.Controllers.ProductsMeasureController;
import far.com.eatit.Controllers.ProductsMeasureInvController;
import far.com.eatit.Controllers.ProductsSubTypesController;
import far.com.eatit.Controllers.ProductsSubTypesInvController;
import far.com.eatit.Controllers.ProductsTypesController;
import far.com.eatit.Controllers.ProductsTypesInvController;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;

public class ProductsDialogfragment extends DialogFragment implements OnFailureListener {

    private  Products tempObj;

    LinearLayout llSave;
    TextInputEditText etCode, etName;
    Spinner spnFamily, spnGroup;
    RecyclerView rvMeasures;
    LinearLayout llMeasureScreen, llMainScreen, llNext;

    ProductsController productsController;
    ProductsInvController productsInvController;
    ArrayList<EditSelectionRowModel> selected = new ArrayList<>() ;
    boolean firstTime = true;
    String type;

    Dialog loadingDialg;
    Dialog errorDialog;

    public  static ProductsDialogfragment newInstance(String type, Products pt) {


        ProductsDialogfragment f = new ProductsDialogfragment();
        f.type = type;
        f.tempObj = pt;

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
        productsController = ProductsController.getInstance(getActivity());
        productsInvController = ProductsInvController.getInstance(getActivity());

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.dialog_add_edit_product, container, true);
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
        llMeasureScreen = view.findViewById(R.id.llMeasureScreen);
        llNext = view.findViewById(R.id.llNext);
        llSave = view.findViewById(R.id.llSave);
        etCode = view.findViewById(R.id.etCode);
        etName = view.findViewById(R.id.etName);
        spnFamily = view.findViewById(R.id.spnFamilia);
        spnGroup = view.findViewById(R.id.spnGrupo);
        rvMeasures = view.findViewById(R.id.rvMeasures);
        rvMeasures.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            ProductsTypesController.getInstance(getActivity()).fillSpinner(spnFamily, false);
            ProductsSubTypesController.getInstance(getActivity()).fillSpinner(spnGroup, false);
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            ProductsTypesInvController.getInstance(getActivity()).fillSpinner(spnFamily, false);
            ProductsSubTypesInvController.getInstance(getActivity()).fillSpinner(spnGroup, false);
        }

        etCode.setEnabled(false);
        etCode.setText(Funciones.generateCode());

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                selected = ((EditSelectionRowAdapter)rvMeasures.getAdapter()).getSelectedObjects();
                if(tempObj == null){
                    Save();
                }else{
                    showLoadingDialog();
                    searchProduct(tempObj.getCODE());
                }
                //Funciones.getDateOnline(ProductTypeDialogFragment.this);
            }
        });

        llNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llMainScreen.setVisibility((llMainScreen.getVisibility() == View.VISIBLE)?View.GONE:View.VISIBLE);
                llMeasureScreen.setVisibility((llMeasureScreen.getVisibility() == View.GONE)?View.VISIBLE:View.GONE);
            }
        });

        if(tempObj != null){//EDIT
            setUpToEditUsers();
        }

        fillMeasures();


        spnFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV familia = (KV)spnFamily.getSelectedItem();
                if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                    ProductsSubTypesController.getInstance(getActivity()).fillSpinner(spnGroup, false, familia.getKey());
                }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                    ProductsSubTypesInvController.getInstance(getActivity()).fillSpinner(spnGroup, false, familia.getKey());
                }

                if(firstTime){//para que seleccione el subType del producto automaticamente la primera vez que abra el dialogo.
                    firstTime= false;
                    if(tempObj != null){
                        setSpinnerposition(spnGroup, tempObj.getSUBTYPE());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public boolean validate(){
        if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre.", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(spnFamily.getSelectedItem()== null){
            Snackbar.make(getView(), "Seleccione una familia.", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(spnGroup.getSelectedItem()== null){
            Snackbar.make(getView(), "Seleccione un grupo.", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(selected.size() == 0){
            Snackbar.make(getView(), "Seleccione por lo menos 1 unidad de medida", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    public void Save(){

        if(validate()) {
            SaveProduct();
        }else{
            llSave.setEnabled(true);
        }
    }

    public void SaveProduct(){
        try {
            String code = etCode.getText().toString();
            String description = etName.getText().toString();
            String productType = ((KV)spnFamily.getSelectedItem()).getKey();
            String productSubType = ((KV)spnGroup.getSelectedItem()).getKey();
            Products product = new Products(code, description, productType, productSubType, false);

            ArrayList<ProductsMeasure> list = new ArrayList<>();
            for(EditSelectionRowModel ssrm: selected){
                list.add(new ProductsMeasure(Funciones.generateCode(), code, ssrm.getCode(),Double.parseDouble(ssrm.getText()),true, null, null));
            }

            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                productsController.sendToFireBase(product, list);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                productsInvController.sendToFireBase(product, list);
            }


            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void EditProduct(){
        try {
            Products products = ((Products)tempObj);
            products.setDESCRIPTION(etName.getText().toString());
            products.setTYPE(((KV)spnFamily.getSelectedItem()).getKey());
            products.setSUBTYPE(((KV)spnGroup.getSelectedItem()).getKey());
            products.setMDATE(null);

            ArrayList<ProductsMeasure> list = new ArrayList<>();
            for(EditSelectionRowModel ssrm: selected){
                list.add(new ProductsMeasure(Funciones.generateCode(), products.getCODE(), ssrm.getCode(),Double.parseDouble(ssrm.getText()),true, null, null));
            }

            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                productsController.sendToFireBase(products, list);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                productsInvController.sendToFireBase(products, list);
            }

            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void setUpToEditUsers(){
        Products p = ((Products)tempObj);
        etCode.setText(p.getCODE());
        etCode.setEnabled(false);
        etName.setText(p.getDESCRIPTION());
        setSpinnerposition(spnFamily, p.getTYPE());
        setSpinnerposition(spnGroup, p.getSUBTYPE());


    }

    public void setSpinnerposition(Spinner spn, String key){
        for(int i = 0; i< spn.getAdapter().getCount(); i++){
            if(((KV)spn.getAdapter().getItem(i)).getKey().equals(key)){
                spn.setSelection(i);
                break;
            }
        }
    }




    @Override
    public void onFailure(@NonNull Exception e) {
        Funciones.showNetworkErrorWithText(getView(), e.getMessage());
        llSave.setEnabled(true);
    }

    public void fillMeasures(){

        if(tempObj != null) {
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                selected.addAll(ProductsMeasureController.getInstance(getActivity()).getSSRMByCodeProduct((tempObj).getCODE()));
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
               // selectedObjs.addAll(ProductsMeasureInvController.getInstance(getActivity()).getSSRMByCodeProduct(((Products) tempObj).getCODE()));
            }
        }
        ArrayList<EditSelectionRowModel> arr = null;
        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            arr =  MeasureUnitsController.getInstance(getActivity()).getUnitMeasuresSSRM(null, null, null);
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            //arr = MeasureUnitsInvController.getInstance(getActivity()).getUnitMeasuresSSRM(null, null, null);
        }

        rvMeasures.setAdapter(new EditSelectionRowAdapter(getActivity(),arr, selected));
        rvMeasures.getAdapter().notifyDataSetChanged();
        rvMeasures.invalidate();
    }

    OnFailureListener onFailureSerachProduct = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
        showErrorDialog(e.getMessage());
        llSave.setEnabled(true);
        closeLoadingDialog();
        }
    };

    OnSuccessListener<QuerySnapshot> onSuccessSeachProduct = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {

            if(querySnapshot != null && querySnapshot.getDocuments().size() >0){
            showErrorDialog("No puede editar productos que esten actualmente en ventas (Abiertas o Entregadas)");
            }else{
                EditProduct();
            }

            llSave.setEnabled(true);
            closeLoadingDialog();
        }
    };

    /**
     * validamos que el producto a editar no este en una venta (Abierta, entregada, anulada) es decir no historica
     * @param codeProduct
     */
    public void searchProduct(String codeProduct){
        SalesController.getInstance(getActivity()).searchProductInSalesDetail(codeProduct,onSuccessSeachProduct, onFailureSerachProduct);
    }

    public void showLoadingDialog(){
        loadingDialg = null;
        loadingDialg = Funciones.getLoadingDialog(getActivity(),"Loading...");
        loadingDialg.show();
    }

    public void closeLoadingDialog(){
        if(loadingDialg != null){
            loadingDialg.dismiss();
        }
    }

    public void showErrorDialog(String msg){
        errorDialog = null;
        errorDialog = Funciones.getCustomDialog(getActivity(), "Error", msg, R.drawable.ic_error_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDialog.dismiss();
                errorDialog = null;
            }
        });
        errorDialog.setCancelable(false);
        errorDialog.show();
    }
}
