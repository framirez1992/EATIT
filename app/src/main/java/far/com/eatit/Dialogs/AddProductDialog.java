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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.UUID;

import far.com.eatit.Adapters.Models.OrderDetailModel;
import far.com.eatit.CloudFireStoreObjects.Products;
import far.com.eatit.CloudFireStoreObjects.SalesDetails;
import far.com.eatit.Controllers.MeasureUnitsController;
import far.com.eatit.Controllers.TempOrdersController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.MainOrders;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;

public class AddProductDialog  extends DialogFragment {

    private static  Products tempObj;
    private static OrderDetailModel tempOrderModel;
    LinearLayout llSave;
    TextView tvName;
    Spinner spnUnidad;
    TextInputEditText etCantidad;
    TempOrdersController tempOrdersController;
    MeasureUnitsController measureUnitsController;

    public  static AddProductDialog newInstance(Object pt) {

        if(pt instanceof Products){
            tempObj = (Products)pt;
            tempOrderModel = null;
        }else if(pt instanceof OrderDetailModel){
            tempOrderModel = (OrderDetailModel)pt;
            tempObj = null;
        }
        AddProductDialog f = new AddProductDialog();

        // Supply num input as an argument.
      //  Bundle args = new Bundle();
        //if(pt != null) {
          //  f.setArguments(args);
        //}

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tempOrdersController = TempOrdersController.getInstance(getActivity());
        measureUnitsController = MeasureUnitsController.getInstance(getActivity());
        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etCantidad);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.add_product_dialog, container, true);
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
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }


    public void init(View view){
        llSave = view.findViewById(R.id.llSave);
        tvName = view.findViewById(R.id.tvName);
        spnUnidad = view.findViewById(R.id.spnUnidad);
        etCantidad = view.findViewById(R.id.etCantidad);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                Save();

            }
        });

        measureUnitsController.fillSpinner(spnUnidad);

        if(tempObj != null){
            inicializeNewProduct();
        }else if(tempOrderModel != null){
            inicializeEditOrderLine();
        }
    }


    public boolean validate(){
        if(etCantidad.getText().toString().trim().equals("") || etCantidad.getText().toString().trim().equals(".") || etCantidad.getText().toString().trim().equals("0")){
            Snackbar.make(getView(), "La cantidad debe ser mayor a 0", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(spnUnidad.getSelectedItem() == null){
            Snackbar.make(getView(), "Debe seleccionar la unidad", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public void Save(){

        if(validate()){
            if(tempOrderModel != null){
                editOrderLine();
            }else {
                saveOrderLine();
            }
        }else{
            llSave.setEnabled(true);
        }

    }

    public void saveOrderLine(){
        String code = Funciones.generateCode();
        String codeSale = ((MainOrders)getActivity()).getOrderCode();
        String codeProduct = tempObj.getCODE();
        String codeUnd =((KV)spnUnidad.getSelectedItem()).getKey();
        int position = 0;
        double quantity = Double.parseDouble(etCantidad.getText().toString());
        double unit = 0;
        double price =0;
        double discount = 0;
        SalesDetails sd = new SalesDetails(code,codeSale, codeProduct, codeUnd, position, quantity, unit, price, discount);
        tempOrdersController.insert_Detail(sd);

        ((MainOrders)getActivity()).refreshResume();
        dismiss();
    }

    public void editOrderLine(){
        SalesDetails sd = tempOrdersController.getTempSaleDetailByCode(tempOrderModel.getCode());
        sd.setCODEUND(((KV)spnUnidad.getSelectedItem()).getKey());
        sd.setQUANTITY(Double.parseDouble(etCantidad.getText().toString()));
        tempOrdersController.update_Detail(sd);

        ((MainOrders)getActivity()).refreshResume();
        dismiss();
    }


    public void inicializeNewProduct(){
        tvName.setText(tempObj.getDESCRIPTION());
    }
    public void inicializeEditOrderLine(){
        tvName.setText(tempOrderModel.getProduct_name());
        etCantidad.setText(tempOrderModel.getQuantity());
        for(int i = 0; i<spnUnidad.getAdapter().getCount(); i++){
            if(((KV)spnUnidad.getAdapter().getItem(i)).getKey().equals(tempOrderModel.getCodeMeasure())){
                spnUnidad.setSelection(i);
                break;
            }
        }
    }
}
