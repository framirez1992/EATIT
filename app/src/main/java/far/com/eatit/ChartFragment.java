package far.com.eatit;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.PercentRowModel;
import far.com.eatit.Utils.Funciones;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChartFragment extends Fragment {

    Activity parent;
    PieChart pieChart;
    LinearLayout llGoBack;
    TextView tvTitle, tvQuantity, tvAmount;
    String label;
    ArrayList<PercentRowModel> list;
    int idCaller;
    String totalOrders, totalAmount , dateIni, dateEnd;
    public static final int[] MATERIAL_COLORS = {
            rgb("#F44336"),rgb("#1E88E5"), rgb("#43A047"),  rgb("#795548"),rgb("#E91E63"),
            rgb("#0097A7"), rgb("#E65100"),rgb("#B71C1C"),rgb("#212121"),rgb("#607D8B"),
            rgb("#0091EA"), rgb("#827717"), rgb("#F9A825"), rgb("#FFC107"),rgb("#009688"),
            rgb("#7986CB"),rgb("#EC407A"), rgb("#A1887F"),  rgb("#757575"), rgb("#D500F9")

    };

    public ChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pieChart = view.findViewById(R.id.pieChart);
        tvTitle = view.findViewById(R.id.tvTitle);
        llGoBack = view.findViewById(R.id.llGoBack);
        tvQuantity = view.findViewById(R.id.tvQuantity);
        tvAmount = view.findViewById(R.id.tvAmount);

        llGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReportsDetail)parent).showDetail();
            }
        });

        generateData();

    }

    public void setParent(Activity act){
        this.parent = act;
    }
    public void setParams(int id, String totalOrders,String totalAmount, String dateIni, String dateEnd){
        this.idCaller = id; this.totalOrders = totalOrders;this.totalAmount = totalAmount; this.dateIni = dateIni; this.dateEnd = dateEnd;
    }
    public void setData(String label, ArrayList<PercentRowModel> data){
        this.list = data;
        this.label = label;
    }
    public void generateData(){
        tvTitle.setText(label+"\n("+ Funciones.getFormatedDateRepDom(Funciones.parseStringToDate(dateIni.replace("-", "")))+" - "+
                Funciones.getFormatedDateRepDom(Funciones.parseStringToDate(dateEnd.replace("-", "")))+")");
        tvQuantity.setText(totalOrders);
        tvAmount.setText("$"+Funciones.formatDecimal(Double.parseDouble(totalAmount)));

        pieChart.clear();
        pieChart.setUsePercentValues(true);//agrega [%] a los porcentajes PieEntry
        //pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(10,15,10,15);
        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        //pieChart.setHoleColor(getResources().getColor(R.color.gray_900));
        //pieChart.setTransparentCircleRadius(61f);
        //pieChart.setDrawRoundedSlices(true);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
        l.setTextSize(15f);

        if(list == null || list.size() == 0){
            return;
        }
        ArrayList<PieEntry> entries = new ArrayList<>();
        float percent = 100.0f;
        for(PercentRowModel prm : list){
            percent-= Float.parseFloat(prm.getPercent().replace("%", ""));
            PieEntry entry = new PieEntry(Float.parseFloat(prm.getPercent().replace("%", "")),prm.getDescription());
            entries.add(entry);
        }

        if(percent >1){
            PieEntry entry = new PieEntry(percent,"Others");
            entries.add(entry);
        }

        PieDataSet dataSet = new PieDataSet(entries,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(MATERIAL_COLORS);

        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);//numero porcentual
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);//texto del PieEntry



        PieData pdata = new PieData(dataSet);
        pdata.setValueTextSize(15f);
        pdata.setValueTextColor(Color.WHITE);

        pieChart.getDescription().setText("");//label debajo del grafico
        //pieChart.getDescription().setTextSize(18f);
        pieChart.setData(pdata);
    }


}
