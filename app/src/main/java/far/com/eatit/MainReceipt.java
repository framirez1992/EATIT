package far.com.eatit;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.OrderReceiptModel;
import far.com.eatit.Adapters.Models.WorkedOrdersRowModel;
import far.com.eatit.CloudFireStoreObjects.Receipts;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.CloudFireStoreObjects.SalesDetails;
import far.com.eatit.Controllers.ReceiptController;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Dialogs.PaymentDialog;
import far.com.eatit.Dialogs.WorkedOrdersDialog;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.Interfases.ReceiptableActivity;

public class MainReceipt extends AppCompatActivity implements ListableActivity, ReceiptableActivity {

    SalesController salesController;
    //CollectionReference sales;
    //CollectionReference salesDetails;

    ReceipFragment receipFragment;
    ReceiptResumeFragment receiptResumeFragment;
    Fragment lastFragment;

    WorkedOrdersDialog workedOrdersDialog;
    PaymentDialog paymentDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_receipt);
        setContentView(R.layout.content_main);
        salesController = SalesController.getInstance(MainReceipt.this);
        //sales = salesController.getReferenceFireStore();
        //salesDetails = salesController.getReferenceDetailFireStore();

        receipFragment = new ReceipFragment();
        receiptResumeFragment = new ReceiptResumeFragment();

        receipFragment.setMainActivityReference(this);
        receiptResumeFragment.setMainActivityReference(this);


        showReceiptFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void showReceiptFragment(){
        changeFragment(receipFragment, R.id.details);
    }

    public void showReceiptResumeFragment(){
        changeFragment(receiptResumeFragment, R.id.details);
    }
    public void changeFragment(Fragment f, int id){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.commit();
        lastFragment = f;
    }

    @Override
    public void onClick(Object obj) {
        if(obj instanceof  OrderReceiptModel){
            OrderReceiptModel orderReceiptModel = (OrderReceiptModel)obj;
            receiptResumeFragment.setCodeAreaDetail(orderReceiptModel.getMesaID());
            showReceiptResumeFragment();
        }else if(obj instanceof WorkedOrdersRowModel){
            workedOrdersDialog.showDetail((WorkedOrdersRowModel) obj);
        }

    }

    @Override
    public void closeOrders(Receipts receipt, ArrayList<Sales> deliveredSales){

        if(deliveredSales != null && deliveredSales.size() > 0) {
            //////////////////////////////////////////////////////////////////
            ////////// CERRANDO ORDENES            ///////////////////////////
            SalesController.getInstance(MainReceipt.this).closeOrders(receipt,deliveredSales);
            //////////////////////////////////////////////////////////////////
            //////////  GUARDANDO RECIBO          ////////////////////////////
            //ReceiptController.getInstance(MainReceipt.this).sendToFireBase(receipt);
            /////////////////////////////////////////////////////////////////
        }
    }

    public void refresh(){
        if(lastFragment instanceof ReceipFragment){
            receipFragment.refreshList();
        }else if(lastFragment instanceof ReceiptResumeFragment){
            receiptResumeFragment.refreshList();
        }
    }

    public void callWorkedOrdersDialog(String codeAreaDetail){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialogWorkedOrders");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        workedOrdersDialog =  WorkedOrdersDialog.newInstance();
        workedOrdersDialog.setFromReceipts(codeAreaDetail);
        // Create and show the dialog.
        workedOrdersDialog.show(ft, "dialogWorkedOrders");
    }

    public void callPaymentDialog(String codeAreaDetail){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialogPaymentDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        paymentDialog =  PaymentDialog.newInstance(MainReceipt.this, codeAreaDetail);
        // Create and show the dialog.
        paymentDialog.show(ft, "dialogPaymentDialog");
    }
}
