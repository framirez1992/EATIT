package far.com.eatit;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import far.com.eatit.Adapters.ReceiptSavedAdapter;
import far.com.eatit.Controllers.AreasController;
import far.com.eatit.Controllers.AreasDetailController;
import far.com.eatit.Controllers.ReceiptController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Interfases.ListableActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReceiptListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ReceiptListFragment extends Fragment implements ListableActivity {
    Activity activity;
    RecyclerView rvList;
    Spinner spnAreas, spnMesas;
    KV area, mesa;
    public ReceiptListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.maintenance_w2_spinner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvList = view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(activity));
        ((TextView)view.findViewById(R.id.spnTitle)).setText("Area");
        ((TextView)view.findViewById(R.id.spnTitle2)).setText("Mesa");
        spnAreas = view.findViewById(R.id.spn);
        spnMesas = view.findViewById(R.id.spn2);

        AreasController.getInstance(activity).fillSpinner(spnAreas, false);
        spnAreas.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                area = (KV)parent.getSelectedItem();
                AreasDetailController.getInstance(activity).fillSpinner(spnMesas,false, area.getKey());
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

    @Override
    public void onClick(Object obj) {

    }

    public void refreshList(){
        if(mesa == null){
            return;
        }
        ReceiptSavedAdapter adapter = new ReceiptSavedAdapter(activity, (ListableActivity) activity, ReceiptController.getInstance(activity).getReceiptsSM(mesa.getKey()));
        rvList.setAdapter(adapter);
        rvList.getAdapter().notifyDataSetChanged();
        rvList.invalidate();

    }

    public void setMainActivityReference(Activity activity){
        this.activity = activity;
    }
}
