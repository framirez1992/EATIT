package far.com.eatit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import far.com.eatit.Adapters.OrdersReceipAdapter;
import far.com.eatit.Controllers.AreasController;
import far.com.eatit.Controllers.AreasDetailController;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Generic.Objects.KV;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReceipFragment extends Fragment {

    Spinner spnAreas, spnMesas;
    RecyclerView rvList;
    MainReceipt mainReceipt;
    KV area, mesa;

    public ReceipFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.maintenance_w2_spinner, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvList = view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(mainReceipt));
        ((TextView)view.findViewById(R.id.spnTitle)).setText("Area");
        ((TextView)view.findViewById(R.id.spnTitle2)).setText("Mesa");
        spnAreas = view.findViewById(R.id.spn);
        spnMesas = view.findViewById(R.id.spn2);

        AreasController.getInstance(mainReceipt).fillSpinner(spnAreas, false);
        spnAreas.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                area = (KV)parent.getSelectedItem();
                AreasDetailController.getInstance(mainReceipt).fillSpinner(spnMesas,false, area.getKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnMesas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               mesa = (KV)parent.getSelectedItem();
                refreshList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void setMainactivityReference(MainReceipt mainReceipt){
        this.mainReceipt = mainReceipt;
    }
    public void refreshList(){
        if(mesa == null){
            return;
        }
        OrdersReceipAdapter adapter = new OrdersReceipAdapter(mainReceipt, mainReceipt, SalesController.getInstance(getActivity()).getOrderReceipt(mesa.getKey()));
        rvList.setAdapter(adapter);
        rvList.getAdapter().notifyDataSetChanged();
        rvList.invalidate();

    }
}
