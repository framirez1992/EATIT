package far.com.eatit;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Tables;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import far.com.eatit.Adapters.Models.OrderDetailModel;
import far.com.eatit.Adapters.Models.OrderModel;
import far.com.eatit.Adapters.OrdersBoardAdapter;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.CloudFireStoreObjects.SalesDetails;
import far.com.eatit.Controllers.LicenseController;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Controllers.TableFilterController;
import far.com.eatit.Controllers.UserControlController;
import far.com.eatit.Controllers.UsersController;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersBoardFragment extends Fragment {

    RecyclerView rvOrders;
    SalesController salesController;

    public OrderBoard parentActivity;

    public OrdersBoardFragment() {
        // Required empty public constructor
    }


    public void setParentActivity(OrderBoard parent){
        this.parentActivity = parent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.orders_board, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvOrders = view.findViewById(R.id.rvOrders);
        salesController = SalesController.getInstance(parentActivity);

        LinearLayoutManager manager = new LinearLayoutManager(parentActivity);
        manager.offsetChildrenHorizontal(10);
        rvOrders.setLayoutManager(manager);

        reloadList();
    }

    public void reloadList(){

        String where = " ("+SalesController.STATUS+" = "+ CODES.CODE_ORDER_STATUS_OPEN+" OR "+ SalesController.STATUS+" = "+ CODES.CODE_ORDER_STATUS_CANCELED+") ";
        //where += TableFilterController.getInstance(parentActivity).getConditionsByTableTask(Tablas.generalUsersSales, CODES.TABLE_FILTER_CODETASK_WORKORDER);

        ArrayList<OrderModel> orders = salesController.getOrderModels(where);
        OrdersBoardAdapter adapter = new OrdersBoardAdapter(parentActivity,parentActivity, orders);
        rvOrders.setAdapter(adapter);
        rvOrders.invalidate();

        boolean canceledOrders = false;
        for(OrderModel om : orders){
            if(om.getStatus().equals(CODES.CODE_ORDER_STATUS_CANCELED+"")){
            canceledOrders = true;
            break;
            }
        }
        if(canceledOrders) {
            parentActivity.imgDelete.setVisibility(View.VISIBLE);
        }else{
            parentActivity.imgDelete.setVisibility(View.GONE);
        }

    }

}
