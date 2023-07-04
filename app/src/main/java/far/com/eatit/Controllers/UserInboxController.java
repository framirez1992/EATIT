package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.CloudFireStoreObjects.TableCode;
import far.com.eatit.CloudFireStoreObjects.UserInbox;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class UserInboxController{

        public static String TABLE_NAME = "USERINBOX";
        public static String CODE = "code",TYPE = "type", CODESENDER = "codesender", CODEUSER= "codeuser",SUBJECT = "subject",  CODEMESSAGE = "codemessage",DESCRIPTION = "description",
                STATUS = "status",CODEICON = "codeicon", DATE = "date", MDATE = "mdate";
        public static String[]columns = new String[]{CODE,TYPE,CODESENDER, CODEUSER, SUBJECT, CODEMESSAGE, DESCRIPTION, STATUS,CODEICON, DATE, MDATE};
        public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
                +CODE+" TEXT, "+TYPE+" TEXT, "+CODESENDER+" TEXT, "+CODEUSER+" TEXT,"+CODEMESSAGE+" TEXT,"+SUBJECT+" TEXT, "+DESCRIPTION+" TEXT," +
                STATUS+" TEXT,"+CODEICON+" TEXT, "+DATE+" TEXT, "+MDATE+" TEXT)";

        FirebaseFirestore db;
        Context context;
        private static UserInboxController instance;

        private UserInboxController(Context c){
            this.context = c;
            db = FirebaseFirestore.getInstance();
        }

        public static String getCODE(){
            return TABLE_NAME+"."+CODE;
        }
        public static String getTYPE(){
            return TABLE_NAME+"."+TYPE;
        }
        public static String getCODESENDER(){
            return TABLE_NAME+"."+CODESENDER;
        }
        public static String getCODEUSER(){
            return TABLE_NAME+"."+CODEUSER;
        }
        public static String getSUBJECT(){
            return TABLE_NAME+"."+SUBJECT;
        }
        public static String getCODEMESSAGE(){
            return TABLE_NAME+"."+CODEMESSAGE;
        }
        public static String getDESCRIPTION(){
            return TABLE_NAME+"."+DESCRIPTION;
        }
        public static String getSTATUS(){
            return TABLE_NAME+"."+STATUS;
        }
        public static String getCODEICON(){
        return TABLE_NAME+"."+CODEICON;
    }
        public static String getDATE(){
            return TABLE_NAME+"."+DATE;
        }
        public static String getMDATE(){
            return TABLE_NAME+"."+MDATE;
        }

        public static UserInboxController getInstance(Context context){
            if(instance == null){
                instance = new UserInboxController(context);
            }
            return instance;
        }

        public CollectionReference getReferenceFireStore(){
            Licenses l = LicenseController.getInstance(context).getLicense();
            if(l == null){
                return null;
            }
            CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersUserInbox);
            return reference;
        }

        public long insert(UserInbox ui){
            ContentValues cv = new ContentValues();
            cv.put(CODE, ui.getCODE());
            cv.put(TYPE, ui.getTYPE());
            cv.put(CODEMESSAGE, ui.getCODEMESSAGE());
            cv.put(CODESENDER, ui.getCODESENDER());
            cv.put(CODEUSER, ui.getCODEUSER());
            cv.put(CODEMESSAGE, ui.getCODEMESSAGE());
            cv.put(SUBJECT, ui.getSUBJECT());
            cv.put(DESCRIPTION, ui.getDESCRIPTION());
            cv.put(STATUS, ui.getSTATUS());
            cv.put(CODEICON, ui.getCODEICON());
            cv.put(DATE, Funciones.getFormatedDate(ui.getDATE()));
            cv.put(MDATE, Funciones.getFormatedDate(ui.getMDATE()));

            long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
            return result;
        }


        public long update(UserInbox ui){

            String where = CODE +" = ?";
            String[]args = new String[]{ui.getCODE()};
            return update(ui, where, args);
        }

        public long update(UserInbox ui, String where, String[]args){

        ContentValues cv = new ContentValues();
        cv.put(CODE, ui.getCODE());
        cv.put(TYPE, ui.getTYPE());
        cv.put(CODEMESSAGE, ui.getCODEMESSAGE());
        cv.put(CODESENDER, ui.getCODESENDER());
        cv.put(CODEUSER, ui.getCODEUSER());
        cv.put(SUBJECT, ui.getSUBJECT());
        cv.put(CODEMESSAGE, ui.getCODEMESSAGE());
        cv.put(DESCRIPTION, ui.getDESCRIPTION());
        cv.put(STATUS, ui.getSTATUS());
        cv.put(CODEICON, ui.getCODEICON());
        cv.put(MDATE, Funciones.getFormatedDate(ui.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }


    public long delete(String where, String[]whereArgs){
            long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, whereArgs);
            return result;
        }


        public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                        OnFailureListener onFailureListener){
            try {
                Task<QuerySnapshot> client = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersUserInbox).get();
                client.addOnSuccessListener(onSuccessListener);
                client.addOnFailureListener(onFailureListener);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    public void getAllDataFromFireBase(String key, OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> reference = getReferenceFireStore().get();
            reference.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            UserInbox object = dc.getDocument().toObject(UserInbox.class);
                            if(object.getCODEUSER().equals(Funciones.getCodeuserLogged(context))) {
                                String where = CODE + " = ?";
                                String[] args = new String[]{object.getCODE()};
                                delete(where, args);
                                insert(object);
                            }
                        }
                    }
                }
            }).addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

        public void sendToFireBase(ArrayList<UserInbox> uiArray){
            try {
                WriteBatch lote = db.batch();
                for(UserInbox ui: uiArray){
                    if(ui.getMDATE() == null){
                        lote.set(getReferenceFireStore().document(ui.getCODE()),ui.toMap());
                    }else{
                        lote.update(getReferenceFireStore().document(ui.getCODE()),ui.toMap());
                    }

                }
                lote.commit().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            }catch(Exception e){
                e.printStackTrace();
            }

        }

    public void massiveDelete(ArrayList<UserInbox> uiArray){
        try {
            WriteBatch lote = db.batch();
            for(UserInbox ui: uiArray){
                    lote.delete(getReferenceFireStore().document(ui.getCODE()));
            }
            lote.commit().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

    }

        public void deleteFromFireBase(UserInbox ui){
            try {
                getReferenceFireStore().document(ui.getCODE()).delete();
            }catch(Exception e){
                e.printStackTrace();
            }
        }


        public ArrayList<UserInbox> getUserInbox(String where, String[]args, String orderBy){
            ArrayList<UserInbox> result = new ArrayList<>();
            try{
                Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
                while(c.moveToNext()){
                    result.add(new UserInbox(c));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return result;
        }
        public UserInbox getUserInboxByCode(String code){
            String where = CODE+" = ?";
            ArrayList<UserInbox> pts = getUserInbox(where, new String[]{code}, null);
            if(pts.size()>0){
                return  pts.get(0);
            }
            return null;
        }

        public void fillTargetSpinner(Spinner spn){

            ArrayList<KV> list = new ArrayList<>();
            list.add(new KV(CODES.CODE_MESSAGE_TARGET_ALL+"",  "TODOS"));
            list.add(new KV(CODES.CODE_MESSAGE_TARGET_GRUPOS+"",  "Grupos"));
            list.add(new KV(CODES.CODE_MESSAGE_TARGET_USERS+"",  "Usuarios"));

            ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, list);
            spn.setAdapter(adapter);

        }


    /*    public ArrayList<UserRowModel> getUserSRM(String where, String[] args, String campoOrder){
            ArrayList<UserRowModel> result = new ArrayList<>();
            if(campoOrder == null){campoOrder = USERNAME;}
            where=((where != null)? "WHERE "+where:"");
            try {

                String sql = "SELECT u."+CODE+" as CODE, u."+USERNAME+" AS USERNAME, u."+ENABLED+" ENABLED, ut."+UserTypesController.DESCRIPTION+" as ROLE, u."+MDATE+" AS MDATE " +
                        "FROM "+TABLE_NAME+" u " +
                        "INNER JOIN "+UserTypesController.TABLE_NAME+" ut on u."+ROLE+" = ut."+UserTypesController.CODE+" "+
                        where;
                Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
                while(c.moveToNext()){
                    result.add(new UserRowModel(c.getString(c.getColumnIndex("CODE")), c.getString(c.getColumnIndex("USERNAME")),c.getString(c.getColumnIndex("ROLE")) ,c.getString(c.getColumnIndex("ENABLED")).equals("1"),c.getString(c.getColumnIndex(MDATE)) != null));
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            return result;

        }*/


    public  ArrayList<UserInbox> getUsersInboxForBloquedProduct(String sender, String productsIn){
        ArrayList<UserInbox> userInboxes = new ArrayList<>();
        try {
            String sql = "SELECT s." + SalesController.IDSALE + " AS CODE, s." + SalesController.IDUSER + " AS CODEUSER, p." + ProductsController.DESCRIPTION + " as PRODUCT " +
                    "FROM " + SalesController.TABLE_NAME + " s " +
                    "INNER JOIN " + SalesController.TABLE_NAME_DETAIL + " sd on sd." + SalesController.DETAIL_IDSALE + " = s." + SalesController.IDSALE + " " +
                    "INNER JOIN " + ProductsController.TABLE_NAME + " p on p." + ProductsController.CODE + " = sd." + SalesController.DETAIL_IDPRODUCT + " " +
                    "LEFT JOIN " + ProductsControlController.TABLE_NAME + " pc on pc." + ProductsControlController.CODEPRODUCT + " = sd." + SalesController.DETAIL_IDPRODUCT + " " +
                    "WHERE s." + SalesController.STATUS + " = ? AND sd."+SalesController.DETAIL_IDPRODUCT+" in ("+productsIn+") " +
                    //"ifnull(pc." + ProductsControlController.BLOQUED + ", '0') = ? " +
                    "GROUP BY s." + SalesController.IDSALE + ", s." + SalesController.IDUSER;

            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{CODES.CODE_ORDER_STATUS_OPEN + "",  /*CODES.CODE_PRODUCTS_CONTROL_BLOQUED +"", productsIn*/});
            while (c.moveToNext()) {
                String msgID = c.getString(c.getColumnIndex("CODE"));
                String destiny = c.getString(c.getColumnIndex("CODEUSER"));
                String subject = c.getString(c.getColumnIndex("PRODUCT")) + " No disponible";
                String msg = "El producto " + c.getString(c.getColumnIndex("PRODUCT")) + " se encuentra temporalmente desabilitado. Edite la orden";
                String codeIcon = CODES.CODE_ICON_MESSAGE_ALERT;
                userInboxes.add(new UserInbox(Funciones.generateCode(),
                        sender,
                        destiny,
                        msgID,
                        subject,
                        msg,
                        CODES.CODE_TYPE_OPERATION_SALES + "",
                        codeIcon,
                        CODES.CODE_USERINBOX_STATUS_NO_READ));

            }
            c.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        return userInboxes;

    }



    public void fillSpinnerMessageStatus(Spinner spn, boolean addTodos){
        ArrayList<KV> data = new ArrayList<>();

        data.add(new KV(CODES.CODE_USERINBOX_STATUS_NO_READ+"","NO LEIDOS"));
        if(addTodos){
            KV obj = new KV("-1", "TODOS");
            data.add(obj);
        }
        data.add(new KV(CODES.CODE_USERINBOX_STATUS_READ+"","LEIDOS"));


        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }

    public void setMessageReaded(String code){
        UserInbox ui = getUserInboxByCode(code);
        ui.setSTATUS(CODES.CODE_USERINBOX_STATUS_READ);
        update(ui);
        ArrayList<UserInbox> uiArray = new ArrayList<>();
        uiArray.add(ui);
        sendToFireBase(uiArray);
    }

    public void deleteOldReadedMessages(){
        ArrayList<UserInbox> list = new ArrayList<>();
        String lastDate ="";
        String sql = "Select "+DATE+" " +
                "FROM "+TABLE_NAME+" " +
                "ORDER BY "+DATE+" DESC " +
                "LIMIT 1";
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        if(c.moveToFirst()){
            lastDate = c.getString(0);
        }c.close();
        if(!lastDate.isEmpty()){
            lastDate = lastDate.substring(0,4)+"-"+lastDate.substring(4,6)+"-"+lastDate.substring(6);
            String date = "substr("+DATE+", 0,5)||'-'||substr("+DATE+", 5,2)||'-'||substr("+DATE+", 7,length("+DATE+"))";
            String where = "Cast ((JulianDay('"+lastDate+"') - JulianDay("+date+")) * 24 As Integer) > ? AND "+STATUS+" = ? " +
                    "AND ("+CODESENDER+" = ? OR "+CODEUSER+" = ? )";
            c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where, new String[]{"1", CODES.CODE_USERINBOX_STATUS_READ+"", Funciones.getCodeuserLogged(context), Funciones.getCodeuserLogged(context)},null,null,DATE);
            while(c.moveToNext()){
                list.add(new UserInbox(c));
            }c.close();

        }

       massiveDelete(list);

    }

    public ArrayList<UserInbox> getUserInbox(String where, String[]args){
        ArrayList<UserInbox> result = new ArrayList<>();
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, where, args, null, null, null);
            while(c.moveToNext()){
                result.add(new UserInbox(c));
            }c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * elimina todos los mensajes relacionados a una orden. este tipo de mensajes tienen el msgID = Sales.Code. con el tipo CODES.CODE_TYPE_OPERATION_SALES
     */
    public ArrayList<UserInbox> getRelatedSalesMesage(Sales s){
        String codeUser = Funciones.getCodeuserLogged(context);
        String where = CODEMESSAGE+" = ? AND "+TYPE+" = ? AND ("+CODEUSER+" = ? OR "+CODESENDER+" = ?) ";
        String[]args = new String[]{s.getCODE(),CODES.CODE_TYPE_OPERATION_SALES+"", codeUser, codeUser};
        return getUserInbox(where, args);
    }

    public void searchChanges(boolean all, OnSuccessListener<QuerySnapshot> success, OnFailureListener failure){

        Date mdate = all?null: DB.getLastMDateSaved(context, TABLE_NAME);
        if(mdate != null){
            getReferenceFireStore().
                    whereGreaterThan(MDATE, mdate).//mayor que, ya que las fechas (la que buscamos de la DB) tienen hora, minuto y segundos.
                    get().
                    addOnSuccessListener(success).
                    addOnFailureListener(failure);
        }else{//TODOS
            getReferenceFireStore().
                    get().
                    addOnSuccessListener(success).
                    addOnFailureListener(failure);
        }

    }


    public void consumeQuerySnapshot(boolean clear, QuerySnapshot querySnapshot){
        if(clear){
            delete(null, null);
        }
        if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
            for(DocumentSnapshot doc: querySnapshot){
                UserInbox obj = doc.toObject(UserInbox.class);
                if(update(obj, CODE+"=?", new String[]{obj.getCODE()}) <=0){
                    insert(obj);
                }
            }
        }

    }

}