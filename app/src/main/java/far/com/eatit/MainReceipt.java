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
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.CloudFireStoreObjects.SalesDetails;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Interfases.ListableActivity;

public class MainReceipt extends AppCompatActivity implements ListableActivity {

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

        receipFragment.setMainactivityReference(this);
        receiptResumeFragment.setMainactivityReference(this);


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

    public void closeOrders(ArrayList<Sales> deliveredSales){
        if(deliveredSales != null && deliveredSales.size() > 0) {

            for(Sales sales : deliveredSales){
            sales.setSTATUS(CODES.CODE_ORDER_STATUS_CLOSED);
            sales.setMDATE(null);//actualizar fecha de ultima actualizacion.


            ArrayList<Sales> s = new ArrayList<>();
            s.add(sales);
            ///////////////////////////////////////////////////////////////////
            ///////////   ENVIANDO AL HISTORICO     ///////////////////////////

            SalesController.getInstance(MainReceipt.this).sendToHistory(s);
            ///////////////////////////////////////////////////////////////////

            ///////////////////////////////////////////////////////////////////
            //////      ELIMINANDO DE LA TABLA SALES Y SALES_DETAIL EN FIREBASE   ////////
            SalesController.getInstance(MainReceipt.this).massiveDelete(s);
            //////////////////////////////////////////////////////////////////

            ///////////////////////////////////////////////////////////////////
            //////////  ELIMINANDOLA EN EL MOVIL   ///////////////////////////
            SalesController.getInstance(MainReceipt.this).deleteHeadDetail(sales);//esto es porque la lista se actualizara antes de que el server retorne la actualizacion.
            //////////////////////////////////////////////////////////////////

        }
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
