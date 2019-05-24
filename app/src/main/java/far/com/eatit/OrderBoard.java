package far.com.eatit;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.OrderModel;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.CloudFireStoreObjects.SalesDetails;
import far.com.eatit.CloudFireStoreObjects.UserInbox;
import far.com.eatit.Controllers.AreasDetailController;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Controllers.UserInboxController;
import far.com.eatit.Dialogs.MessageSendDialog;
import far.com.eatit.Dialogs.ProductBlockSelectionDialog;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.Utils.Funciones;

public class OrderBoard extends AppCompatActivity implements ListableActivity, NavigationView.OnNavigationItemSelectedListener {

    OrdersBoardFragment ordersBoardFragment;
    SalesController salesController;
    CollectionReference salesReference;
    CollectionReference salesDetailReference;
    OrderModel currentOrder = null;
    ContextMenu contextMenu;
    ImageView imgDelete;
    LinearLayout llMenu;

    DrawerLayout drawer;
    NavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_order_board);

            imgDelete = findViewById(R.id.imgDelete);
            llMenu = findViewById(R.id.llMenu);

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCanceledOrders();
                }
            });

            salesController = SalesController.getInstance(OrderBoard.this);
            salesReference = salesController.getReferenceFireStore();
            salesDetailReference = salesController.getReferenceDetailFireStore();

            ordersBoardFragment = new OrdersBoardFragment();
            ordersBoardFragment.setParentActivity(this);



            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            nav = (NavigationView)findViewById(R.id.nav_view);
            nav.setNavigationItemSelectedListener(OrderBoard.this);

            llMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (nav.isShown()) {
                            drawer.closeDrawer(nav);
                        } else {
                            drawer.openDrawer(nav);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });

            goToOrdersBoard();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        contextMenu = menu;
        inflater.inflate(R.menu.menu_order_board, contextMenu);
        super.onCreateContextMenu(contextMenu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionReady:
                setOrderReady();
                return true;
            case R.id.actionMessage:
                callMsgDialog();
                return  true;
            case R.id.actionDelete:
                deleteCurrentOrder();
                return true;

            default:return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.actionBlock) {
           callDialogProductBlocked(false);
        }else if(id == R.id.actionUnblock){
            callDialogProductBlocked(true);
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void goToOrdersBoard(){
            changeFragment(ordersBoardFragment, R.id.board);
        }

        public void changeFragment(Fragment f, int id){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(id, f);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

            ft.commit();
        }

    @Override
    protected void onStart() {
        super.onStart();
        salesReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                salesController.delete(null, null);
                for(DocumentSnapshot ds : querySnapshot){
                    Sales s = ds.toObject(Sales.class);
                    salesController.insert(s);
                }
                reloadOrdersList();
            }
        });

        salesDetailReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                salesController.delete_Detail(null, null);
                for(DocumentSnapshot ds : querySnapshot){
                    SalesDetails sd = ds.toObject(SalesDetails.class);
                    salesController.insert_Detail(sd);
                }

                reloadOrdersList();
            }
        });

    }


    public void reloadOrdersList(){
        ordersBoardFragment.reloadList();
    }

    @Override
    public void onClick(Object obj) {

        currentOrder = (OrderModel) obj;
        if(currentOrder.getStatus().equals(CODES.CODE_ORDER_STATUS_CANCELED+"")){
             contextMenu.findItem(R.id.actionReady).setVisible(false);
             contextMenu.findItem(R.id.actionMessage).setVisible(false);
             contextMenu.findItem(R.id.actionDelete).setVisible(true);

        }else{
            contextMenu.findItem(R.id.actionReady).setVisible(true);
            contextMenu.findItem(R.id.actionMessage).setVisible(true);
            contextMenu.findItem(R.id.actionDelete).setVisible(false);
        }
    }

    public void setOrderReady(){
        if(currentOrder != null){
            Sales s = salesController.getSaleByCode(currentOrder.getOrderNum());
            s.setSTATUS(CODES.CODE_ORDER_STATUS_READY);

            if(s != null) {
                salesController.sendToFireBase(s, new ArrayList<SalesDetails>());//AQUI DEBEN ACTUALIZARSE A "LISTO" EL DETALLE DE LAS ORDENES
                //Se le coloca el codigo de la orden en caso de que la orden pase a "LISTA" varias veces sobreescriba el mensaje en el server con Status "NO LEIDO"
                String subject =  "Orden Lista: "+AreasDetailController.getInstance(OrderBoard.this).getAreasDetailByCode(s.getCODEAREADETAIL()).getDESCRIPTION();
                String message = "La orden esta lista. Pase a recogerla";
                UserInbox ui = new UserInbox(s.getCODE(), Funciones.getCodeuserLogged(OrderBoard.this), s.getCODEUSER(),s.getCODE(), subject,message,CODES.CODE_TYPE_OPERATION_SALES+"",CODES.CODE_ICON_MESSAGE_CHECK, CODES.CODE_USERINBOX_STATUS_NO_READ );
                ArrayList<UserInbox> uis = new ArrayList<>(); uis.add(ui);
                UserInboxController.getInstance(OrderBoard.this).sendToFireBase(uis);
            }else{
                Toast.makeText(OrderBoard.this, "Not in DATABASE", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void deleteCanceledOrders(){

            String where = SalesController.STATUS+" = ?";
            String[]args = new String[]{+CODES.CODE_ORDER_STATUS_CANCELED+""};
            ArrayList<Sales> sales = salesController.getSales(where, args);
            SalesController.getInstance(OrderBoard.this).massiveDelete(sales);
            ordersBoardFragment.reloadList();
    }

    public void deleteCurrentOrder(){

        String where = SalesController.STATUS+" = ? AND "+SalesController.CODE+" = ?";
        String[]args = new String[]{+CODES.CODE_ORDER_STATUS_CANCELED+"", currentOrder.getOrderNum()};
        ArrayList<Sales> sales = salesController.getSales(where, args);
        SalesController.getInstance(OrderBoard.this).massiveDelete(sales);
        ordersBoardFragment.reloadList();
    }

    public void callMsgDialog(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        //ft.addToBackStack(null);
        DialogFragment newFragment =  MessageSendDialog.newInstance(OrderBoard.this, salesController.getSaleByCode(currentOrder.getOrderNum()));

        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDialogProductBlocked(boolean unlock){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment = null;

        newFragment = ProductBlockSelectionDialog.newInstance();
        ((ProductBlockSelectionDialog)newFragment).setUnlock(unlock);

        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }



}
