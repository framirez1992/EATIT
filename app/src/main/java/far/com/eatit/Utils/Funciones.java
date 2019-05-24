package far.com.eatit.Utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import far.com.eatit.DataBase.DB;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Interfases.AsyncExecutor;
import far.com.eatit.R;

import static android.content.Context.MODE_PRIVATE;

public class Funciones {
    public static String getPhoneID(Context context){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static String getFormatedDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }
    public static String getFormatedDate(Date d){
        if(d == null){
            return null;
        }
        return getSimpleDateFormat().format(d);
    }

    public static String getFormatedDateNoTime(Date d){
        if(d == null){
            return null;
        }
        return  new SimpleDateFormat("yyyyMMdd").format(d);
    }

    public static String getFormatedDateRepDom(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }
    public static String sumaDiasFecha(String formatedDated, int dias){
        Calendar c = GregorianCalendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, dias);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(c.getTimeInMillis());
    }

    public static SimpleDateFormat getSimpleDateFormat(){
     return new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    }
    public static SimpleDateFormat getSimpleTimeFormat(){
        return new SimpleDateFormat("HHmmss");
    }
    public static Date parseStringToDate(String date){
        if(date == null){
            return null;
        }
        Date d = new Date();
        try {
            d = getSimpleDateFormat().parse(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return d;
    }

    public static Date sumaDiasFecha(int dias){
        Calendar c = GregorianCalendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, dias);
        return c.getTime();
    }

    public static String gerErrorMessage(int code){
        String message = "UNKNOWN";
        switch (code){
            case 0001:message = "Clave de producto invalida";
                break;
            case 0002:message = "La licencia expiro";
                break;
            case 0003:message = "La licencia fue desabilitada";
                break;
            case 0004:message = "Alcanzo el limite maximo de dispositivos permitidos de la licencia";
                break;
            case 0005:message = "Debe realizar una carga inicial";
                break;
        }
        return message;
    }

    public static boolean isNetDisponible(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isAvailable() && actNetInfo.isConnected() );
    }

    public static  Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    public static String getDateOnline(final AsyncExecutor ae){
        AsyncTask<Void, Void, String> x = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String url = "https://time.is/Unix_time_now";
                    Document doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
                    String[] tags = new String[]{
                            "div[id=time_section]",
                            "div[id=clock0_bg]"
                    };
                    Elements elements = doc.select(tags[0]);
                    for (int i = 0; i < tags.length; i++) {
                        elements = elements.select(tags[i]);
                    }
                    long x = Long.parseLong(elements.text() + "000");
                    String result = Funciones.getSimpleDateFormat().format(new Date(x));
                    return result;
                } catch (Exception e) {
                    return CODES.CODE_ERROR_GET_INTERNET_DATE;
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
             ae.setMessage(s);
            }
        };
        x.execute();

        return "";
    }

    public static void showLoading(View v){
        LinearLayout llLoading = v.findViewById(R.id.llProgress);
        LinearLayout llNetwork = v.findViewById(R.id.llNetwork);
        llLoading.setVisibility(View.VISIBLE);
        llNetwork.setVisibility(View.GONE);
    }

    public static void showNetworkError(View v){
        LinearLayout llLoading = v.findViewById(R.id.llProgress);
        LinearLayout llNetwork = v.findViewById(R.id.llNetwork);
        llLoading.setVisibility(View.GONE);
        llNetwork.setVisibility(View.VISIBLE);
    }

    public static void showNetworkErrorWithText(View v, String msg){
        LinearLayout llLoading = v.findViewById(R.id.llProgress);
        LinearLayout llNetwork = v.findViewById(R.id.llNetwork);
        TextView tvMsgNetwork = v.findViewById(R.id.tvMsgNetwork);
        llLoading.setVisibility(View.GONE);
        llNetwork.setVisibility(View.VISIBLE);
        tvMsgNetwork.setText(msg);
    }

    public static void hideLoadingNetwork(View v){
        LinearLayout llLoading = v.findViewById(R.id.llProgress);
        LinearLayout llNetwork = v.findViewById(R.id.llNetwork);
        llLoading.setVisibility(View.INVISIBLE);
        llNetwork.setVisibility(View.GONE);
    }

    public static boolean fechaMayorQue(String fechaProtagonista, String fecha){
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            Calendar c1 = Calendar.getInstance();c1.setTime(sdf.parse(fechaProtagonista));
            Calendar c2 = Calendar.getInstance();c2.setTime(sdf.parse(fecha));
            return c1.after(c2);

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean fechaMayorQue(Date fechaProtagonista, Date fecha){
        try {
            Calendar c1 = Calendar.getInstance();c1.setTime(fechaProtagonista);
            Calendar c2 = Calendar.getInstance();c2.setTime(fecha);
            return c1.after(c2);

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public static boolean fechaMenorQue(Date fechaProtagonista, Date fecha){
        try {
            Calendar c1 = Calendar.getInstance();c1.setTime(fechaProtagonista);
            Calendar c2 = Calendar.getInstance();c2.setTime(fecha);
            return c1.before(c2);

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public static int calcularDias(String fechaProtagonista, String fecha){
        try {
            SimpleDateFormat sdf =Funciones.getSimpleDateFormat();

            Calendar c1 = Calendar.getInstance();c1.setTime(sdf.parse(fechaProtagonista));
            Calendar c2 = Calendar.getInstance();c2.setTime(sdf.parse(fecha));

            double d = c1.getTimeInMillis() - c2.getTimeInMillis();

            long dias = Math.round(d / ( 24 * 60 * 60 * 1000));
            return ((int) dias);

        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public static int calcularMinutos(Date fechaProtagonista, Date fecha){
        try {
            if(fechaProtagonista == null){
                return 0;
            }


            Calendar c1 = Calendar.getInstance();
            c1.setTime(fechaProtagonista);
            Calendar c2 = Calendar.getInstance();
            c2.setTime(fecha);
            long minutes = c1.getTime().getTime()/(1000 * 60) - c2.getTime().getTime()/(1000 * 60);

            return  (int)minutes;

        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public static void setAlarm(Context c, String date){
        try {
            AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent();
            intent.setAction("far.com.eatit.ALARM");
            PendingIntent alarmIntent = PendingIntent.getBroadcast(c, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat("yyyyMMdd").parse(date));
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
/*no funciona*/
    public static String getCode(String field, String entity, Context cont){
            String code = "0000";
            String sql = "Select CAST(MAX("+field+") AS INTEGER) + 1 from "+entity;
            Cursor c = DB.getInstance(cont).getReadableDatabase().rawQuery(sql, null);
            if(c.moveToNext() && c.getString(0) != null){
                code = c.getString(0);
            }
            return code;
    }

    public String getNextCodeWhithPrefix(String prefix, String field, String entity, Context context){
        String code = prefix+"0000";
        String sql = "Select CAST(SUBSTR(MAX("+field+"), "+(prefix.length() + 1)+", LEN("+field+")) AS INTEGER) + 1 from "+entity
                +" WHERE "+field+" LIKE '"+prefix+"%'";
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        if(c.moveToNext()){
            code = c.getString(0);
        }
        return code;
    }

    public static void savePreferences(Context context, String key, String value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getCodeuserLogged(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(CODES.PREFERENCE_USERSKEY_CODE, "");
    }
    public static String getPreferences(Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
       return preferences.getString(key, "");
    }

    public static void clearPreference(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.clear();
        edit.commit();
    }

    public static void showKeyBoard(EditText et, Context c){
        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void showKeyBoard(final EditText et){
        et.post(new Runnable() {
            @Override
            public void run() {
                final InputMethodManager imm = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                et.requestFocus(); // needed if you have more then one input
            }
        });
    }

    public static void hideKeyBoard(EditText et, Context c){
        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }


    public static void sendSMS(String number, String msg){

        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(number,null,msg,null, null);
    }
}
