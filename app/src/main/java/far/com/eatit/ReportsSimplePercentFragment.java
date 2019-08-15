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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.PercentRowModel;
import far.com.eatit.Adapters.SimplePercentRowAdapter;
import far.com.eatit.Controllers.ProductsSubTypesController;
import far.com.eatit.Controllers.ProductsTypesController;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Controllers.SalesHistoryController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Generic.Objects.KV2;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Utils.Funciones;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsSimplePercentFragment extends Fragment {


    RecyclerView rvList;
    Spinner spnVista, spnFamilia, spnGrupo;
    LinearLayout llGrupo;
    TextView tvTotalOrders,tvTotalMonto, tvDateIni, tvDateEnd;
    Button btnSearch;
    Activity parent;
    KV lastVista, lastFamilia, lastGrupo;
    int idCaller;
    String totalOrders,totalAmount, dateIni, dateEnd;
    public ReportsSimplePercentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parent = getActivity();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reports_simple_percent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        fillList();
        fillSpinners();

    }
    public void init(View v){
        rvList = v.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(parent));

        btnSearch = v.findViewById(R.id.btnSearch);
        tvTotalOrders = v.findViewById(R.id.tvTotalOrders);
        tvTotalMonto = v.findViewById(R.id.tvTotalMonto);
        tvDateIni = v.findViewById(R.id.tvFechaDesde);
        tvDateEnd = v.findViewById(R.id.tvFechaHasta);
        spnVista = v.findViewById(R.id.spnTipoVista);
        spnFamilia = v.findViewById(R.id.spnFamilia);
        spnGrupo = v.findViewById(R.id.spnGrupo);
        llGrupo = v.findViewById(R.id.llGrupo);

        tvTotalOrders.setText(totalOrders);
        tvTotalMonto.setText(totalAmount);
        tvDateIni.setText(Funciones.getFormatedDateRepDom(Funciones.parseStringToDate(dateIni.replace("-", ""))));
        tvDateEnd.setText(Funciones.getFormatedDateRepDom(Funciones.parseStringToDate(dateEnd.replace("-", ""))));

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idCaller = v.getId();
                fillList();
            }
        });
        spnVista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View view, int position, long id) {
                lastVista = (KV)p.getSelectedItem();
                if(lastVista.getKey().equals("1")){
                  llGrupo.setVisibility(View.GONE);
                  lastGrupo = null;
                }else if(lastVista.getKey().equals("0")){
                    llGrupo.setVisibility(View.VISIBLE);
                    if(spnGrupo.getSelectedItem()!= null)
                    lastGrupo = (KV)spnGrupo.getSelectedItem();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnFamilia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View view, int position, long id) {
                lastFamilia = (KV)p.getSelectedItem();
                if(!lastFamilia.getKey().equals("0")){
                    ProductsSubTypesController.getInstance(parent).fillSpinner(spnGrupo, true, lastFamilia.getKey());
                    spnGrupo.setEnabled(true);
                }else{
                    lastGrupo = null;
                    spnGrupo.setAdapter(null);
                    spnGrupo.setEnabled(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnGrupo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View view, int position, long id) {
                lastGrupo = (KV)p.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       setUpVisuals(v);

    }
    public void setParams(int id, String totalOrders,String totalAmount, String dateIni, String dateEnd){
        this.idCaller = id; this.totalOrders = totalOrders;this.totalAmount = totalAmount; this.dateIni = dateIni; this.dateEnd = dateEnd;
    }

    public void fillList(){
        ArrayList<PercentRowModel> list = new ArrayList<>();
        if(idCaller == R.id.btnProducts){
            list = SalesHistoryController.getInstance(parent).getTopSalesProducts(null, null, dateIni, dateEnd);
        }else if(idCaller == R.id.btnMotivos){
            list = SalesHistoryController.getInstance(parent).getTopDevolucionesGeneraldata(dateIni, dateEnd);
        }else if(idCaller == R.id.btnVendedorMasVendido){
            list = SalesHistoryController.getInstance(parent).getVentasReportSellers(CODES.CODE_ORDER_STATUS_CLOSED+"", dateIni, dateEnd);
        }else if(idCaller == R.id.btnVendedorMasDevoluciones){
            list = SalesHistoryController.getInstance(parent).getVentasReportSellers(CODES.CODE_ORDER_STATUS_CANCELED+"", dateIni, dateEnd);
        }else if(idCaller == R.id.btnSearch){
            String keyFamilia = (lastFamilia!= null)?lastFamilia.getKey():null;
            String keyGrupo = (lastGrupo!= null)?lastGrupo.getKey():null;

            if(lastVista.getKey().equals("0")){
                list = SalesHistoryController.getInstance(parent).getTopSalesProducts(keyFamilia, keyGrupo, dateIni, dateEnd);
            }else if(lastVista.getKey().equals("1")){
                list = SalesHistoryController.getInstance(parent).getTopSalesGeneraldata(keyFamilia, dateIni, dateEnd);
            }
        }
        SimplePercentRowAdapter adapter = new SimplePercentRowAdapter(parent,list);
        rvList.setAdapter(adapter);
        rvList.getAdapter().notifyDataSetChanged();
        rvList.invalidate();
    }

    public void fillSpinners(){
        if(idCaller == R.id.btnMotivos){
            return;
        }
        SalesController.getInstance(parent).fillSpnVista(spnVista);
        ProductsTypesController.getInstance(parent).fillSpinner(spnFamilia, true);

    }

    public void setUpVisuals(View v){
        if(idCaller == R.id.btnMotivos){
            ((LinearLayout)v.findViewById(R.id.llFiltros)).setVisibility(View.GONE);
        }else if(idCaller == R.id.btnVendedorMasVendido || idCaller == R.id.btnVendedorMasDevoluciones){
            ((LinearLayout)v.findViewById(R.id.llFiltros)).setVisibility(View.GONE);
        }
    }
}
