package far.com.eatit;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.PercentRowModel;
import far.com.eatit.Globales.CODES;

public class ReportsDetail extends AppCompatActivity {

    ReportsSimplePercentFragment simplePercentFragment;
    ChartFragment chartFragment;
    int idCaller;
    String totalOrders,totalAmount, dateIni ,dateEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_detail);
        idCaller = getIntent().getExtras().getInt(CODES.MAIN_REPORTS_EXTRA_IDCALLER);
        totalOrders = getIntent().getExtras().getString(CODES.MAIN_REPORTS_TOTALORDERS);
        totalAmount = getIntent().getExtras().getString(CODES.MAIN_REPORTS_TOTALORDERSAMOUNT);
        dateIni = getIntent().getExtras().getString(CODES.MAIN_REPORTS_EXTRA_LASTDATEINI);
        dateEnd = getIntent().getExtras().getString(CODES.MAIN_REPORTS_EXTRA_LASTDATEEND);

        simplePercentFragment = new ReportsSimplePercentFragment();
        simplePercentFragment.setParams(idCaller,totalOrders,totalAmount, dateIni, dateEnd);
        chartFragment = new ChartFragment();
        chartFragment.setParent(this);
        chartFragment.setParams(idCaller,totalOrders,totalAmount, dateIni, dateEnd);

       showDetail();
    }


    public void changeFragment(Fragment f, int id){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.commit();
    }

    public void showChart(String label, ArrayList<PercentRowModel> data){
        chartFragment.setData(label, data);
        changeFragment(chartFragment,  R.id.details);
    }

    public void showDetail(){
        changeFragment(simplePercentFragment, R.id.details);
    }


}
