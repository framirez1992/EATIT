package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Adapters.Models.SimpleSeleccionRowModel;
import far.com.eatit.Adapters.Models.UserRowModel;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.Users;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class UsersController {

    public static String TABLE_NAME = "USERS";
    public static String CODE = "code", COMPANY= "company", ENABLED = "enabled",ROLE = "role",USERNAME = "username",
            PASSWORD = "password",SYSTEMCODE = "systemcode",  DATE = "date", MDATE = "mdate";
    public static String[]columns = new String[]{CODE,SYSTEMCODE, COMPANY, ENABLED, ROLE, USERNAME, PASSWORD, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT, "+SYSTEMCODE+" TEXT, "+COMPANY+" TEXT,"+ROLE+" TEXT, "+USERNAME+" TEXT," +
             PASSWORD+" TEXT, "+ENABLED+" TEXT, "+DATE+" TEXT, "+MDATE+" TEXT)";

    FirebaseFirestore db;
    Context context;
    private static UsersController instance;
    private UsersController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static UsersController getInstance(Context context){
        if(instance == null){
            instance = new UsersController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersUsers);
        return reference;
    }

    public long insert(Users user){
        ContentValues cv = new ContentValues();
        cv.put(CODE, user.getCODE());
        cv.put(SYSTEMCODE, user.getSYSTEMCODE());
        cv.put(COMPANY, user.getCOMPANY());
        cv.put(ENABLED, user.isENABLED());
        cv.put(ROLE, user.getROLE());
        cv.put(USERNAME, user.getUSERNAME());
        cv.put(PASSWORD, user.getPASSWORD());
        cv.put(DATE, Funciones.getFormatedDate(user.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(user.getMDATE()));

        long result =DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Users user, String where, String[]whereArgs){
        ContentValues cv = new ContentValues();
        cv.put(CODE, user.getCODE());
        cv.put(SYSTEMCODE, user.getSYSTEMCODE());
        cv.put(COMPANY, user.getCOMPANY());
        cv.put(ENABLED, user.isENABLED());
        cv.put(ROLE, user.getROLE());
        cv.put(USERNAME, user.getUSERNAME());
        cv.put(PASSWORD, user.getPASSWORD());
        cv.put(MDATE, Funciones.getFormatedDate(user.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, whereArgs);
        return result;
    }

    public long delete(String where, String[]whereArgs){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, whereArgs);
        return result;
    }


    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                     OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> client = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersUsers).get();
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
                            Users object = dc.getDocument().toObject(Users.class);
                            String where = CODE+" = ?";
                            String[]args = new String[]{object.getCODE()};
                            delete(where, args);
                            insert(object);
                        }
                    }
                }
            }).addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sendToFireBase(Users user){
        try {
            WriteBatch lote = db.batch();
            lote.set(getReferenceFireStore().document(user.getCODE()), user.toMap());
            lote.commit();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void deleteFromFireBase(Users user){
        try {
            getReferenceFireStore().document(user.getCODE()).delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public ArrayList<Users> getUsers(String where, String[]args, String orderBy){
        ArrayList<Users> result = new ArrayList<>();
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
            while(c.moveToNext()){
                result.add(new Users(c));
            }c.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public Users getUserByCode(String code){
        String where = CODE+" = ?";
        ArrayList<Users> pts = getUsers(where, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }


    public ArrayList<UserRowModel> getUserSRM(String where, String[] args, String campoOrder){
        ArrayList<UserRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = USERNAME;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT u."+CODE+" as CODE,u."+SYSTEMCODE+" as SYSTEMCODE, u."+USERNAME+" AS USERNAME, u."+ENABLED+" ENABLED, ut."+UserTypesController.DESCRIPTION+" as ROLE, u."+MDATE+" AS MDATE " +
                    "FROM "+TABLE_NAME+" u " +
                    "INNER JOIN "+UserTypesController.TABLE_NAME+" ut on u."+ROLE+" = ut."+UserTypesController.CODE+" "+
                    where;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while(c.moveToNext()){
                result.add(new UserRowModel(c.getString(c.getColumnIndex("CODE")),
                        c.getString(c.getColumnIndex("SYSTEMCODE")),
                        c.getString(c.getColumnIndex("USERNAME")),
                        c.getString(c.getColumnIndex("ROLE")) ,
                        c.getString(c.getColumnIndex("ENABLED")).equals("1"),
                        c.getString(c.getColumnIndex("MDATE")) != null));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }

    /**
     * Simple seleccion row model
     * @param where
     * @param args
     * @param campoOrder
     * @return
     */
    public ArrayList<SimpleSeleccionRowModel> getUserSSRM(String where, String[] args, String campoOrder){
        ArrayList<SimpleSeleccionRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = USERNAME;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT u."+CODE+" as CODE, u."+USERNAME+" AS USERNAME, u."+ENABLED+" ENABLED, ut."+UserTypesController.DESCRIPTION+" as ROLE, u."+MDATE+" AS MDATE " +
                    "FROM "+TABLE_NAME+" u " +
                    "INNER JOIN "+UserTypesController.TABLE_NAME+" ut on u."+ROLE+" = ut."+UserTypesController.CODE+" "+
                     where
                    +" ORDER BY "+campoOrder;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while(c.moveToNext()){
                String code = c.getString(c.getColumnIndex("CODE"));
                String name = code+"\n"+c.getString(c.getColumnIndex("USERNAME"));
                result.add(new SimpleSeleccionRowModel(code,name ,false));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }

    public int validateUser(Users u){
        if(u == null){
            return CODES.CODE_USERS_INVALID;
        }
        if(!u.isENABLED()){
            return CODES.CODE_USERS_DISBLED;
        }

        return CODES.CODE_USERS_ENABLED;
    }

    public  Task<QuerySnapshot> getUserFromFireBase(String user, String pass){

      // Create a query against the collection.
        Query query = getReferenceFireStore().whereEqualTo("code", user).whereEqualTo("password", pass);
// retrieve  query results asynchronously using query.get()
        return query.get();
    }

}
