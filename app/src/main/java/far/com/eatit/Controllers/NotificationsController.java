package far.com.eatit.Controllers;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.NotificationRowModel;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Globales.CODES;
import far.com.eatit.R;

public class NotificationsController {
    Context context;
    private static  NotificationsController instance;
    private  NotificationsController(Context context){
        this.context = context;
    }

    public static NotificationsController getInstance(Context c) {
        if(instance == null){
            instance = new NotificationsController(c);
        }
        return instance;
    }

    public ArrayList<NotificationRowModel> getNotifications(String where, String[]args){
        ArrayList<NotificationRowModel> result = new ArrayList<>();

        try {
            String sql = /*"SELECT " + SalesController.CODE + " AS CODE," + SalesController.CODE + " AS CODEMESSAGE, " + CODES.CODE_TYPE_OPERATION_SALES + " AS TYPE, " +
            "'Orden Lista' AS TITLE, 'Orden: '||"+ SalesController.CODE+" as SENDER, 'La orden  ya esta lista. Pase a retirarla' AS DESCRIPTION," +
            "" + R.drawable.ic_check + " as R, "+SalesController.MDATE+" as MDATE " +
            "FROM " + SalesController.TABLE_NAME + " " +
            "WHERE " + SalesController.STATUS + " = " + CODES.CODE_ORDER_STATUS_READY + " " +
            "UNION " +*/
            "SELECT " + UserInboxController.getCODE() + " AS CODE," + UserInboxController.getCODEMESSAGE() + " AS CODEMESSAGE,  " + UserInboxController.getTYPE() + " AS TYPE," +
            " "+UserInboxController.getSUBJECT()+" as TITLE, u."+ UsersController.USERNAME+" as SENDER, " + UserInboxController.getDESCRIPTION() + " AS DESCRIPTION, " +
            " case when("+UserInboxController.getCODEICON()+" = "+CODES.CODE_ICON_MESSAGE_ALERT+") then " + R.drawable.ic_alert+ " WHEN("+UserInboxController.getCODEICON()+" = "+CODES.CODE_ICON_MESSAGE_CHECK+") THEN "+R.drawable.ic_check+" else "+ R.drawable.ic_msg+" end as R , "+UserInboxController.getMDATE()+" as MDATE " +
            "FROM " + UserInboxController.TABLE_NAME + " " +
            "INNER JOIN "+UsersController.TABLE_NAME+" u on u."+UsersController.CODE+" = "+UserInboxController.getCODESENDER()+" "+
            "WHERE "+where+" "+
            "GROUP BY CODEMESSAGE "+
            "ORDER BY " + SalesController.UPDATEDATE + " DESC";

    Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
    while (c.moveToNext()) {
        String code = c.getString(c.getColumnIndex("CODE"));
        String codeMessage = c.getString(c.getColumnIndex("CODEMESSAGE"));
        String type = c.getString(c.getColumnIndex("TYPE"));
        String title = c.getString(c.getColumnIndex("TITLE"));
        String sender = c.getString(c.getColumnIndex("SENDER"));
        String description = c.getString(c.getColumnIndex("DESCRIPTION"));
        int resource = c.getInt(c.getColumnIndex("R"));

        result.add(new NotificationRowModel(code,codeMessage, type, title, sender,description, resource));

    }
    c.close();
}catch(Exception e){
    e.printStackTrace();
}
        return result;

    }

}
