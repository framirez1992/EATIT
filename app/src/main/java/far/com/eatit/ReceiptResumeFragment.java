package far.com.eatit;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import far.com.eatit.Adapters.ReceiptResumeAdapter;
import far.com.eatit.CloudFireStoreObjects.Receipts;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Controllers.UserControlController;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.Interfases.ReceiptableActivity;
import far.com.eatit.Utils.Funciones;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReceiptResumeFragment extends Fragment {

    RecyclerView rvList;
    Activity activity;
    String codeAreaDetail;
    Button btnCollect, btnPrint;
    TextView tvTotal, tvSubTotal, tvItbis;
    LinearLayout llBack, llDetails;

    public ReceiptResumeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_receipt_resume, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTotal = view.findViewById(R.id.tvTotal);
        tvSubTotal = view.findViewById(R.id.tvSubTotal);
        tvItbis = view.findViewById(R.id.tvItbis);
        llBack = view.findViewById(R.id.llBack);
        llDetails = view.findViewById(R.id.llDetails);

        btnCollect = view.findViewById(R.id.btnCollect);
        btnPrint = view.findViewById(R.id.btnPrint);
        rvList = view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(activity));

        btnPrint.setVisibility(View.GONE);
        btnCollect.setVisibility(View.GONE);
        llDetails.setVisibility(View.GONE);

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReceiptableActivity)activity).showReceiptFragment();
            }
        });

        llDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainReceipt)activity).callWorkedOrdersDialog(codeAreaDetail);
            }
        });

        btnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Sales> sales = SalesController.getInstance(activity).getDeliveredOrdersByCodeAreadetail(codeAreaDetail);
                if(sales.size() == 0){
                    return;
                }
                Receipts receipt = SalesController.getInstance(activity).getReceiptByCodeAreadetail(codeAreaDetail);
                if(receipt == null){
                    return;
                }

                llBack.setEnabled(false);
                ((ReceiptableActivity)activity).closeOrders(receipt, sales);
                codeAreaDetail = null;
                ((ReceiptableActivity)activity).showReceiptFragment();
                llBack.setEnabled(true);
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              // SalesController.printReceipt(mainReceipt,codeAreaDetail, SalesController.getInstance(mainReceipt).getOrderReceiptResume(codeAreaDetail));
            }
        });

        if(activity instanceof MainOrders && UserControlController.getInstance(activity).printOrders()){
            btnPrint.setVisibility(View.VISIBLE);
        }
        if(activity instanceof MainReceipt){
            llDetails.setVisibility(View.VISIBLE);
            if(UserControlController.getInstance(activity).chargeOrders()){
                btnCollect.setVisibility(View.VISIBLE);
            }
        }

        refreshList();
    }

    public void setMainActivityReference(Activity activity){
        this.activity = activity;
    }
    public void setCodeAreaDetail(String codeAreaDetail){
        this.codeAreaDetail = codeAreaDetail;
    }
    public void refreshList(){
        ReceiptResumeAdapter adapter = new ReceiptResumeAdapter(activity, (ListableActivity) activity, SalesController.getInstance(activity).getOrderReceiptResume(codeAreaDetail));
        rvList.setAdapter(adapter);
        rvList.getAdapter().notifyDataSetChanged();
        rvList.invalidate();

        Receipts receipt = SalesController.getInstance(activity).getReceiptByCodeAreadetail(codeAreaDetail);
        if(receipt != null){
            tvSubTotal.setText("$"+receipt.getSubTotal());
            tvItbis.setText("$"+receipt.getTaxes());
            tvTotal.setText("$"+receipt.getTotal());
        }

    }


}
