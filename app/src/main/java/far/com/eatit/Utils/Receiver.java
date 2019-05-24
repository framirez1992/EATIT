package far.com.eatit.Utils;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import far.com.eatit.Controllers.LicenseController;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Interfases.AsyncExecutor;

public class Receiver extends BroadcastReceiver implements AsyncExecutor{
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {//el dispositivo encendio.
                Funciones.getDateOnline(Receiver.this);
        }else if(intent.getAction().equals("far.com.eatit.ALARM")){

            LicenseController.getInstance(context).updateLicenciaDiaria(intent.getStringExtra("fecha"));
        }
    }

    @Override
    public void setMessage(String msg) {
      /*  Calendar cal = Calendar.getInstance();
        try {
            if (msg != CODES.CODE_ERROR_GET_INTERNET_DATE) {
                cal.setTime(new SimpleDateFormat("yyyyMMdd-HHmmss").parse(msg));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        licenseController.setAlarm(Funciones.getFormatedDate(cal.getTime()),7,30);*/
    }
}
