package far.com.eatit.Dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import far.com.eatit.CloudFireStoreObjects.Receipts;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Interfases.ReceiptableActivity;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;

public class PaymentDialog extends DialogFragment {

    Activity parentActivity;
    TextView tvAmount;
    Spinner spnPaymentMethod;
    EditText etAmountCash, etAmountCredit;
    Button btnPay;
    String codeAreaDetail;
    TextInputLayout llAmount1, llAmount2;

    public  static PaymentDialog newInstance(Activity parentActivity, String codeAreaDetail) {
        PaymentDialog p = new PaymentDialog();
        p.codeAreaDetail = codeAreaDetail;
        p.parentActivity = parentActivity;
        return p;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.payment_dialog, container, true);
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

    public void init(View v){
        llAmount1 = v.findViewById(R.id.llAmount1);
        llAmount2 = v.findViewById(R.id.llAmount2);
        tvAmount = v.findViewById(R.id.tvAmount);
        spnPaymentMethod = v.findViewById(R.id.spnPaymentMethod);
        etAmountCash = v.findViewById(R.id.etAmount1);
        etAmountCredit = v.findViewById(R.id.etAmount2);
        btnPay = v.findViewById(R.id.btnPay);

        fillSpinner(spnPaymentMethod);
        spnPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshAmount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    ((ReceiptableActivity)parentActivity).closeOrders(getReceipt(), getSales());
                    codeAreaDetail = null;
                    ((ReceiptableActivity)parentActivity).showReceiptFragment();
                    dismiss();
                }


            }
        });

        refreshAmount();
    }


    public void fillSpinner(Spinner spn){
        ArrayList<KV> data = new ArrayList<>();
        data.add(new KV("1", "Cash"));
        data.add(new KV("2", "Credit"));
        data.add(new KV("3", "Mixed"));

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(getContext(),android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }

    public void prepareAmounts(double amount){
        KV payment = (KV)spnPaymentMethod.getSelectedItem();

        llAmount1.setVisibility(((isPaymentCash() || isPaymentMixed())?View.VISIBLE:View.GONE));
        llAmount2.setVisibility(((isPaymentCredit() || isPaymentMixed())?View.VISIBLE:View.GONE));

        if(isPaymentCash()){
            etAmountCash.setText(Funciones.formatDecimal(amount));
            etAmountCredit.setText("");
        }else if(isPaymentCredit()){
            etAmountCredit.setText(Funciones.formatDecimal(amount));
            etAmountCash.setText("");
        }else{
            etAmountCash.setText("");
            etAmountCredit.setText("");
        }
    }

    public void refreshAmount(){
        double amount = SalesController.getInstance(parentActivity).getReceiptByCodeAreadetail(/*codeAreaDetail*/0).getTotal();
        tvAmount.setText("$"+Funciones.formatDecimal(amount));
        prepareAmounts(amount);
    }

    public ArrayList<Sales> getSales(){
        return SalesController.getInstance(parentActivity).getDeliveredOrdersByCodeAreadetail(/*codeAreaDetail*/0);
    }

    public Receipts getReceipt(){
        return SalesController.getInstance(parentActivity).getReceiptByCodeAreadetail(/*codeAreaDetail*/0);
    }
    public boolean validate(){
        if(getSales().size() == 0){
            Snackbar.make(getView(), "No existen ordenes para esta mesa", Snackbar.LENGTH_LONG).show();
            return false;
        }
        Receipts r = getReceipt();
        if( r == null){
            Snackbar.make(getView(), "No se puede crear el recibo", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(isPaymentCash() && getAmountCash() < r.getTotal()){
            Snackbar.make(getView(), "El monto a pagar debe ser igual a la deuda", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if(isPaymentCash() && getAmountCash() > r.getTotal()){
            Snackbar.make(getView(), "El monto a pagar debe ser igual a la deuda", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(isPaymentCredit() && getAmountCredit() < r.getTotal()){
            Snackbar.make(getView(), "El monto a pagar debe ser igual a la deuda", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if(isPaymentCredit() && getAmountCredit() > r.getTotal()){
            Snackbar.make(getView(), "El monto a pagar debe ser igual a la deuda", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(isPaymentMixed() && getAmountMixed() < r.getTotal()){
            Snackbar.make(getView(), "El monto a pagar debe ser igual a la deuda", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if(isPaymentMixed() && getAmountMixed() > r.getTotal()){
            Snackbar.make(getView(), "El monto a pagar debe ser igual a la deuda", Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public boolean isPaymentCash(){
        return ((KV)spnPaymentMethod.getSelectedItem()).getKey().equals("1");
    }

    public boolean isPaymentCredit(){
        return ((KV)spnPaymentMethod.getSelectedItem()).getKey().equals("2");
    }
    public boolean isPaymentMixed(){
        return ((KV)spnPaymentMethod.getSelectedItem()).getKey().equals("3");
    }

    public double getAmountCash(){
        double amount = 0.0;
        try {
            amount = Double.parseDouble(etAmountCash.getText().toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return amount;
    }

    public double getAmountCredit(){
        double amount = 0.0;
        try {
            amount = Double.parseDouble(etAmountCredit.getText().toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return amount;
    }

    public double getAmountMixed(){
        return getAmountCash() + getAmountCredit();
    }
}
