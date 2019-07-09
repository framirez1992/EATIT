package far.com.eatit;


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

import java.util.ArrayList;

import far.com.eatit.Adapters.ReceiptResumeAdapter;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.Controllers.SalesController;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReceiptResumeFragment extends Fragment {

    RecyclerView rvList;
    MainReceipt mainReceipt;
    String codeAreaDetail;
    Button btnCollect;

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
        btnCollect = view.findViewById(R.id.btnCollect);
        rvList = view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(mainReceipt));

        btnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Sales> sales = SalesController.getInstance(mainReceipt).getDeliveredOrdersByCodeAreadetail(codeAreaDetail);
                if(sales.size() == 0){
                    return;
                }
                mainReceipt.closeOrders(sales);
                codeAreaDetail = null;
                mainReceipt.showReceiptFragment();
            }
        });

        refreshList();
    }

    public void setMainactivityReference(MainReceipt mainReceipt){
        this.mainReceipt = mainReceipt;
    }
    public void setCodeAreaDetail(String codeAreaDetail){
        this.codeAreaDetail = codeAreaDetail;
    }
    public void refreshList(){
        ReceiptResumeAdapter adapter = new ReceiptResumeAdapter(mainReceipt, mainReceipt, SalesController.getInstance(mainReceipt).getOrderReceiptResume(codeAreaDetail));
        rvList.setAdapter(adapter);
        rvList.getAdapter().notifyDataSetChanged();
        rvList.invalidate();

    }
}
