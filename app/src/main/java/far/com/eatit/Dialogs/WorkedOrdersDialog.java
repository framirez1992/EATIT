package far.com.eatit.Dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import far.com.eatit.API.models.Sale;
import far.com.eatit.Adapters.Models.NotificationRowModel;
import far.com.eatit.Adapters.Models.WorkedOrdersRowModel;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.MainOrders;
import far.com.eatit.NotificationFragment;
import far.com.eatit.OrdersEditionFragment;
import far.com.eatit.R;

public class WorkedOrdersDialog extends DialogFragment {


    OrdersEditionFragment ordersEditionFragment;
    NotificationFragment notificationFragment;
    boolean fromReceipts;
    String codeAreaDetail;

    boolean showOnlyDetail;
    Sale sales;

    public  static WorkedOrdersDialog newInstance() {

        WorkedOrdersDialog f = new WorkedOrdersDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notificationFragment = new NotificationFragment();
        ordersEditionFragment = new OrdersEditionFragment();

        notificationFragment.setWorkedOrders(true);
        if(fromReceipts) {
            notificationFragment.setFromReceipt(codeAreaDetail);
            ordersEditionFragment.setFromReceipt();
        }

        if(showOnlyDetail){
            ordersEditionFragment.setSales(sales);
            ordersEditionFragment.setShowOnlyDetail();

        }

        notificationFragment.setParent(this.getActivity());
        ordersEditionFragment.setParent(this);


        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);

        setRetainInstance(true);//al girar la pantalla el dialogo se mantenia visible pero su instancia era null,
        //con esto, si se gira la pantalla la instancia es nula, asi que lo cierra automaticamente

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.notification_dialog, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        initializeFragments();

    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    public void initializeFragments(){

        if(showOnlyDetail){
            changeFragment(ordersEditionFragment, R.id.notifications);
            return;
        }
        changeFragment(notificationFragment, R.id.notifications);
        changeFragment(ordersEditionFragment, R.id.notificationsDetails);

    }

    public void changeFragment(Fragment f, int id){

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(id, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.commit();
    }

    public void init(View view){

    }



    public void saveOrderLine(){
        dismiss();
    }


    public void refreshNotifications(){
        notificationFragment.refreshList();
    }


    public void goToMessageDetail(){
        FrameLayout notificationFL = getView().findViewById(R.id.notifications);
        FrameLayout notificationDetailsFL = getView().findViewById(R.id.notificationsDetails);

        if(notificationFL.getVisibility() == View.VISIBLE && notificationDetailsFL.getVisibility() == View.GONE){
            notificationDetailsFL.setVisibility(View.VISIBLE);
            notificationFL.setVisibility(View.GONE);
        }
    }


    public void showDetail(WorkedOrdersRowModel n){

        goToMessageDetail();
        changeFragment(ordersEditionFragment, R.id.notificationsDetails);
        ordersEditionFragment.setSales(SalesController.getInstance(getActivity()).getSaleById(/*n.getCode()*/0));
        ordersEditionFragment.setupEdition();

    }

    public void goToMessagesList(){
        FrameLayout notificationFL = getView().findViewById(R.id.notifications);
        FrameLayout notificationDetailsFL = getView().findViewById(R.id.notificationsDetails);

        if(notificationDetailsFL.getVisibility() == View.VISIBLE && notificationFL.getVisibility() == View.GONE){
            notificationDetailsFL.setVisibility(View.GONE);
            notificationFL.setVisibility(View.VISIBLE);
        }
    }

    public void editOrder(Sale s){
        ((MainOrders)getActivity()).editOrder(s);
        dismiss();
    }


    public void setFromReceipts(String codeAreaDetail){
        fromReceipts = true;
        this.codeAreaDetail = codeAreaDetail;

    }

    public void setShowOnlyDetail(boolean b, Sale s){
        this.showOnlyDetail = b;
        this.sales = s;
    }
}
