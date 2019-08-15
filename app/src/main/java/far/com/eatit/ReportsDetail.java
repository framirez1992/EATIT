package far.com.eatit;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import far.com.eatit.Globales.CODES;

public class ReportsDetail extends AppCompatActivity {

    ReportsSimplePercentFragment simplePercentFragment;
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

        changeFragment(simplePercentFragment, R.id.details);
    }


    public void changeFragment(Fragment f, int id){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.commit();
    }
}
