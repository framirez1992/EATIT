package far.com.eatit;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.OrderReceiptModel;
import far.com.eatit.CloudFireStoreObjects.Receipts;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.CloudFireStoreObjects.SalesDetails;
import far.com.eatit.Controllers.ReceiptController;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.Interfases.ReceiptableActivity;

public class MainReceipt extends AppCompatActivity implements ListableActivity, ReceiptableActivity {

    SalesController salesController;
    CollectionReference sales;
    CollectionReference salesDetails;

    ReceipFragment receipFragment;
    ReceiptResumeFragment receiptResumeFragment;
    Fragment lastFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_receipt);
        setContentView(R.layout.content_main);
        salesController = SalesController.getInstance(MainReceipt.this);
        sales = salesController.getReferenceFireStore();
        salesDetails = salesController.getReferenceDetailFireStore();

        receipFragment = new ReceipFragment();
        receiptResumeFragment = new ReceiptResumeFragment();

        receipFragment.setMainActivityReference(this);
        receiptResumeFragment.setMainActivityReference(this);


        showReceiptFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sales.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                if(querySnapshot == null || querySnapshot.isEmpty()){
                    return;
                }
                salesController.delete(null, null);
                for(DocumentSnapshot dc: querySnapshot){
                    Sales s = dc.toObject(Sales.class);
                    salesController.insert(s);
                }

                refresh();

            }
        });

        salesDetails.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                if(querySnapshot == null || querySnapshot.isEmpty()){
                    return;
                }

                salesController.delete_Detail(null, null);
                for(DocumentSnapshot dc: querySnapshot){
                    SalesDetails sd = dc.toObject(SalesDetails.class);
                    salesController.insert_Detail(sd);
                }

                refresh();
            }
        });
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
        OrderReceiptModel orderReceiptModel = (OrderReceiptModel)obj;
        receiptResumeFragment.setCodeAreaDetail(orderReceiptModel.getMesaID());
        showReceiptResumeFragment();
    }

    @Override
    public void closeOrders(Receipts receipt, ArrayList<Sales> deliveredSales){

        if(deliveredSales != null && deliveredSales.size() > 0) {
            //////////////////////////////////////////////////////////////////
            ////////// CERRANDO ORDENES            ///////////////////////////
            SalesController.getInstance(MainReceipt.this).closeOrders(receipt,deliveredSales);
            //////////////////////////////////////////////////////////////////
            //////////  GUARDANDO RECIBO          ////////////////////////////
            ReceiptController.getInstance(MainReceipt.this).sendToFireBase(receipt);
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
}
