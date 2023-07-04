package far.com.eatit.Globales;

import android.content.Context;
import android.content.SharedPreferences;

public class Functions {

    public static String getDeviceSerialNumber(){
        return android.os.Build.SERIAL;
    }

    public static void savePreference(Context context, String key, String value){
        SharedPreferences sharedPref = context.getSharedPreferences(
                "MY_PREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static   String getPreference(Context context,String key){
        SharedPreferences sharedPref = context.getSharedPreferences(
                "MY_PREFERENCES", Context.MODE_PRIVATE);
        String value = sharedPref.getString(key,"");
        return value;
    }
}
