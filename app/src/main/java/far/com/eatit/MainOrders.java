package far.com.eatit;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

import far.com.eatit.API.models.Product;
import far.com.eatit.API.models.Sale;
import far.com.eatit.Adapters.Models.NotificationRowModel;
import far.com.eatit.Adapters.Models.OrderDetailModel;
import far.com.eatit.Adapters.Models.OrderReceiptModel;
import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Adapters.Models.WorkedOrdersRowModel;
import far.com.eatit.CloudFireStoreObjects.Products;
import far.com.eatit.CloudFireStoreObjects.ProductsMeasure;
import far.com.eatit.CloudFireStoreObjects.Receipts;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.CloudFireStoreObjects.SalesDetails;
import far.com.eatit.CloudFireStoreObjects.UserInbox;
import far.com.eatit.Controllers.ProductsController;
import far.com.eatit.Controllers.ProductsMeasureController;
import far.com.eatit.Controllers.ReceiptController;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Controllers.TempOrdersController;
import far.com.eatit.Controllers.UserControlController;
import far.com.eatit.Controllers.UserInboxController;
import far.com.eatit.Dialogs.MessageSendDialog;
import far.com.eatit.Dialogs.NotificationsDialog;
import far.com.eatit.Dialogs.WorkedOrdersDialog;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.Interfases.ReceiptableActivity;
import far.com.eatit.Utils.Funciones;

public class MainOrders extends AppCompatActivity implements ListableActivity, ReceiptableActivity, NavigationView.OnNavigationItemSelectedListener {

    SalesController salesController;
    UserInboxController userInboxController;

    ProductsMeasureController productsMeasureController;

    //CollectionReference sales;
    //CollectionReference salesDetails;
    CollectionReference userInbox;
    //CollectionReference productsMeasure;

    NewOrderFragment newOrderFragment;
    ResumenOrderFragment resumenOrderFragment;
    ReceipFragment receipFragment;
    ReceiptResumeFragment receiptResumeFragment;
    Fragment lastFragment;

    TempOrdersController tempOrdersController;
    String orderCode = null;
    RelativeLayout rlNotifications;
    CardView cvNotificacions;
    TextView tvNotificationsNumber;
    ImageView imgMenu, imgSeach, imgHideSearch, imgBell;
    EditText etSearch;
    LinearLayout llSearch, llMenu;


    DrawerLayout drawer;
    NavigationView nav;

    NotificationsDialog notificationsDialog;
    WorkedOrdersDialog workedOrdersDialog;
    OrderDetailModel objectToEditFromResume = null;

    public static final String KEY_ORDERCODE = "KEYORDERCODE";
    boolean editingOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_orders);
        tempOrdersController = TempOrdersController.getInstance(MainOrders.this);
        salesController = SalesController.getInstance(MainOrders.this);
        userInboxController = UserInboxController.getInstance(MainOrders.this);
        productsMeasureController = ProductsMeasureController.getInstance(MainOrders.this);

        //sales = salesController.getReferenceFireStore();
        //salesDetails = salesController.getReferenceDetailFireStore();
        userInbox = userInboxController.getReferenceFireStore();
        //productsMeasure = productsMeasureController.getReferenceFireStore();

        rlNotifications = findViewById(R.id.rlNotifications);
        cvNotificacions = findViewById(R.id.cvNotifications);
        tvNotificationsNumber = findViewById(R.id.tvNotificationNumber);
        imgBell = findViewById(R.id.imgBell);
        imgMenu = findViewById(R.id.imgMenu);
        imgHideSearch = findViewById(R.id.imgHideSearch);
        imgSeach = findViewById(R.id.imgSearch);
        llSearch = findViewById(R.id.llSearch);
        etSearch = findViewById(R.id.etSearch);
        llMenu = findViewById(R.id.llMenu);

        imgMenu.setVisibility(View.VISIBLE);
        imgSeach.setVisibility(View.VISIBLE);

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if(etSearch.getText().toString().trim().equals("")){
                            return false;
                        }
                        newOrderFragment.setLastSearch(etSearch.getText().toString());
                        imgHideSearch.performClick();

                        newOrderFragment.search();

                        return true;

                }
                return false;
            }
        });
        imgSeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llMenu.setVisibility(View.GONE);
                rlNotifications.setVisibility(View.GONE);
                imgSeach.setVisibility(View.GONE);
                llSearch.setVisibility(View.VISIBLE);
                etSearch.requestFocus();
                etSearch.setText("");
                Funciones.showKeyBoard(etSearch, MainOrders.this);
            }
        });

        imgHideSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Funciones.hideKeyBoard(etSearch, MainOrders.this);

                llMenu.setVisibility(View.VISIBLE);
                rlNotifications.setVisibility(View.VISIBLE);
                imgSeach.setVisibility(View.VISIBLE);
                llSearch.setVisibility(View.GONE);
            }
        });

        rlNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditingOrder()){
                    return;
                }

                callNotificationsDialog();
            }
        });


        newOrderFragment = new NewOrderFragment();
        newOrderFragment.setParent(this);
        resumenOrderFragment = new ResumenOrderFragment();
        resumenOrderFragment.setParent(this);
        receipFragment = new ReceipFragment();
        receipFragment.setMainActivityReference(this);
        receiptResumeFragment = new ReceiptResumeFragment();
        receiptResumeFragment.setMainActivityReference(this);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nav = (NavigationView)findViewById(R.id.nav_view);

        imgMenu.setOnClickListener(new View.OnClickListener() {
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


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainOrders.this);

        setupUserControls();

        // SI SE ENTRA POR PRIMERA VEZ, SI SE GIRA LA PANTALLA NO CORRER OTRA VEZ.
        if(savedInstanceState == null){
            prepareNewOrder();
        }
        goToNewOrder();

    }

    @Override
    protected void onStart() {
        super.onStart();


        userInbox.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                if(querySnapshot == null ){
                    return;
                }

                userInboxController.delete(null, null);
                for(DocumentSnapshot dc: querySnapshot){
                    UserInbox ui = dc.toObject(UserInbox.class);
                    if(ui.getCODEUSER().equals(Funciones.getCodeuserLogged(MainOrders.this))) {
                        userInboxController.insert(ui);
                    }
                }

                userInboxController.deleteOldReadedMessages();
                refreshInterface();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_delete, menu);
        menu.findItem(R.id.actionEdit).setVisible(false);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionEdit:
                callAddDialog(objectToEditFromResume);
                return true;
            case R.id.actionDelete:
                deleteOrderLine(objectToEditFromResume);
                return  true;

            default:return super.onContextItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if(!isEditingOrder()) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.goMenu) {
                goToMenu();
            } else if (id == R.id.openOrders) {
                callWorkedOrdersDialog();
            } else if (id == R.id.goReceip) {
                showReceiptFragment();
            }
        }
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState .putString(KEY_ORDERCODE, orderCode);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        orderCode = savedInstanceState.getString(KEY_ORDERCODE);
        resumenOrderFragment.refreshList();
    }

    public void goToNewOrder(){
        changeFragment(newOrderFragment, R.id.details);
        changeFragment(resumenOrderFragment, R.id.result);
    }


    public void goToMenu(){
        changeFragment(newOrderFragment, R.id.details);
    }
    public void changeFragment(Fragment f, int id){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.commit();

        if(id == R.id.details) {
            lastFragment = f;
        }
    }

    @Override
    public void onClick(Object obj) {
        if(obj instanceof SimpleRowModel) {
            Product p = ProductsController.getInstance(MainOrders.this).getProductByCode(((SimpleRowModel) obj).getId());
            callAddDialog(p);
        }else if(obj instanceof NotificationRowModel){
            UserInboxController.getInstance(MainOrders.this).setMessageReaded(((NotificationRowModel) obj).getCode());
            notificationsDialog.showDetail((NotificationRowModel)obj);
        }else if(obj instanceof OrderDetailModel){
            objectToEditFromResume = (OrderDetailModel) obj;
        }else if(obj instanceof WorkedOrdersRowModel){
            workedOrdersDialog.showDetail((WorkedOrdersRowModel) obj);
        }else if(obj instanceof OrderReceiptModel){
            OrderReceiptModel orderReceiptModel = (OrderReceiptModel)obj;
            receiptResumeFragment.setCodeAreaDetail(orderReceiptModel.getMesaID());
            showReceiptResumeFragment();
        }
    }

    public void showReceiptResumeFragment(){
        changeFragment(receiptResumeFragment, R.id.details);
    }

    public void callAddDialog(Object object){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment = null;

            //newFragment = AddProductDialog.newInstance(object, null);

        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callMsgDialog(UserInbox userInbox){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment =  MessageSendDialog.newInstance(MainOrders.this, userInbox);

        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }


    public void callNotificationsDialog(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialogNotification");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        notificationsDialog =  NotificationsDialog.newInstance();
        // Create and show the dialog.
        notificationsDialog.show(ft, "dialogNotification");
    }

    public void callWorkedOrdersDialog(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialogWorkedOrders");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        workedOrdersDialog =  WorkedOrdersDialog.newInstance();
        // Create and show the dialog.
        workedOrdersDialog.show(ft, "dialogWorkedOrders");
    }

    public void prepareNewOrder(){
        setEditing(false);
        tempOrdersController.delete(null, null);
        tempOrdersController.delete_Detail(null, null);
        orderCode  = Funciones.generateCode();
        double totalDiscount = 0.0;
        double total = 0.0;
        String codeUser = Funciones.getCodeuserLogged(MainOrders.this);

        Sales s = new Sales(orderCode,codeUser,null, totalDiscount, total, CODES.CODE_ORDER_STATUS_OPEN,"", null, null, null, null, orderCode, null);
        tempOrdersController.insert(s);//
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void refreshResume(){
     resumenOrderFragment.refreshList();
    }

    public void prepareResumeForEdition(){
        setThemeEditing();
        resumenOrderFragment.prepareResumeForEdition();
    }
    public void refreshProductsSearch(int goToPosition){
        newOrderFragment.search();
        newOrderFragment.setSelection(goToPosition);
    }

    public void setResumeSelection(int pos){
        resumenOrderFragment.setSelection(pos);
    }

    public void notityOrdersReady(){
        String where = UserInboxController.STATUS+" = ? ";
        String[] args = new String[]{CODES.CODE_USERINBOX_STATUS_NO_READ+""};
        String orderBy = UserInboxController.MDATE+" DESC, "+UserInboxController.CODEMESSAGE;
        ArrayList<UserInbox> msgs =  UserInboxController.getInstance(MainOrders.this).getUserInbox(where, args, orderBy);

        if((/*sales.size() +*/ msgs.size())>0){
            cvNotificacions.setVisibility(View.VISIBLE);
            tvNotificationsNumber.setText((/*sales.size() +*/ msgs.size())+"");
        }else{
            cvNotificacions.setVisibility(View.GONE);
            tvNotificationsNumber.setText("0");
        }
    }


    public void refreshInterface(){
        notityOrdersReady();

        if(lastFragment instanceof ReceipFragment){
            receipFragment.refreshList();
        }else if(lastFragment instanceof ReceiptResumeFragment){
            receiptResumeFragment.refreshList();
        }

        if(notificationsDialog != null && notificationsDialog.isVisible()){
            notificationsDialog.refreshNotifications();
        }else if(workedOrdersDialog != null && workedOrdersDialog.isVisible()){
            workedOrdersDialog.refreshNotifications();
        }

    }

    public void editOrder(Sale s){
        orderCode = s.getId()+"";
        tempOrdersController.delete(null, null);
        tempOrdersController.delete_Detail(null, null);

        //tempOrdersController.insert(s);

        for(SalesDetails sd: salesController.getSalesDetailsByCodeSales(s.getId())){
            tempOrdersController.insert_Detail(sd);
        }

        refreshResume();
        prepareResumeForEdition();
        /////////////////////////////////////////
        ///// SI ES UNA ORDEN SPLIT  ////////////
        setUpEditSplitedOrder(s);
        ////////////////////////////////////////


        FrameLayout menuFrameLayout = findViewById(R.id.details);
        FrameLayout resumenFrameLayout = findViewById(R.id.result);

        //Si la visivilidad esta en modo "telefono" mostrar el layout del resumen si actualmente se esta visualizando el "menu"
        if(menuFrameLayout.getVisibility()== View.VISIBLE && resumenFrameLayout.getVisibility() == View.GONE){
            menuFrameLayout.setVisibility(View.GONE);
            resumenFrameLayout.setVisibility(View.VISIBLE);
        }

        setEditing(true);
    }

    public void deleteOrderLine(OrderDetailModel d){
        String where = TempOrdersController.DETAIL_CODE +" = ? AND "+TempOrdersController.DETAIL_CODESALES+" = ?";
        tempOrdersController.delete_Detail(where, new String[]{d.getCode(), d.getCode_sales()});
        refreshResume();
        objectToEditFromResume = null;
    }

    public void showDetail(){
        changeFragment(resumenOrderFragment, R.id.result);
        ((ViewGroup)findViewById(R.id.details)).setVisibility(View.GONE);
        ((ViewGroup)findViewById(R.id.result)).setVisibility(View.VISIBLE);

    }

    public void showMenu(){
        changeFragment(newOrderFragment, R.id.details);
        ((ViewGroup)findViewById(R.id.details)).setVisibility(View.VISIBLE);
        ((ViewGroup)findViewById(R.id.result)).setVisibility(View.GONE);
    }


    public void refresh(){
        prepareNewOrder();
        resumenOrderFragment.refreshList();
        resumenOrderFragment.llCancel.setVisibility(View.GONE);
        resumenOrderFragment.etNotas.setText("");
        resumenOrderFragment.llMore.setVisibility(View.GONE);
        resumenOrderFragment.imgMore.setImageResource(R.drawable.ic_arrow_drop_down);
        resumenOrderFragment.setUpSpinnersAreas();

        newOrderFragment.setUpSpinners();
    }

    public void setThemeEditing(){
        setTheme(R.style.ThemeEditing);
        ((LinearLayout)findViewById(R.id.llParent)).setBackgroundColor(getResources().getColor(R.color.yellow_700));
        ImageViewCompat.setImageTintList(imgMenu, ColorStateList.valueOf(getResources().getColor(android.R.color.black)));
        ImageViewCompat.setImageTintList(imgSeach, ColorStateList.valueOf(getResources().getColor(android.R.color.black)));
        ImageViewCompat.setImageTintList(imgHideSearch, ColorStateList.valueOf(getResources().getColor(android.R.color.black)));
        ImageViewCompat.setImageTintList(imgBell, ColorStateList.valueOf(getResources().getColor(android.R.color.black)));


    }
    public void setThemeNormal(){
        setTheme(R.style.AppTheme);
        ((LinearLayout)findViewById(R.id.llParent)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ImageViewCompat.setImageTintList(imgMenu, ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
        ImageViewCompat.setImageTintList(imgSeach, ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
        ImageViewCompat.setImageTintList(imgHideSearch, ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
        ImageViewCompat.setImageTintList(imgBell, ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
    }

    public void updateTempSalesDetail(){
        TempOrdersController.getInstance(MainOrders.this).updatePrices();
    }

    @Override
    public void closeOrders(Receipts receipt, ArrayList<Sales> sales) {

        if(sales != null && sales.size() > 0) {
            //////////////////////////////////////////////////////////////////
            ////////// CERRANDO ORDENES            ///////////////////////////
            SalesController.getInstance(MainOrders.this).closeOrders(receipt,sales);
            //////////////////////////////////////////////////////////////////
            //////////  GUARDANDO RECIBO          ////////////////////////////
            //ReceiptController.getInstance(MainOrders.this).sendToFireBase(receipt);
            /////////////////////////////////////////////////////////////////
        }
    }

    public void setupUserControls(){
        MenuItem printOrders = ((NavigationView)findViewById(R.id.nav_view)).getMenu().findItem(R.id.goReceip);
        printOrders.setVisible(false);
        if(UserControlController.getInstance(MainOrders.this).printOrders()){
            printOrders.setVisible(true);
        }

    }

    @Override
    public void showReceiptFragment() {
        changeFragment(receipFragment, R.id.details);
        ((ViewGroup)findViewById(R.id.details)).setVisibility(View.VISIBLE);
        ((ViewGroup)findViewById(R.id.result)).setVisibility(View.GONE);
    }

    public void setUpEditSplitedOrder(Sale s){
        /*if(s.getCODEPRODUCTTYPE() != null || s.getCODEPRODUCTSUBTYPE() != null){
                newOrderFragment.setUpEditSplitedOrder(s);
        }*/
    }

    public void setEditing(boolean b){
        this.editingOrder = b;
    }
    public boolean isEditingOrder(){
        return this.editingOrder;
    }
}
