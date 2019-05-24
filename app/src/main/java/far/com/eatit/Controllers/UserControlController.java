package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.UserControl;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class UserControlController {
    public static String TABLE_NAME = "USERCONTROL";
    public static String CODE = "code", CODEUSER= "codeuser", CODEUSERTYPE = "codeusertype",CONTROL = "control",VALUE = "value", DATE = "date", MDATE = "mdate";
    public static String[]columns = new String[]{CODE, CODEUSER, CODEUSERTYPE, CONTROL,VALUE, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT, "+CODEUSER+" TEXT,"+CODEUSERTYPE+" TEXT, "+CONTROL+" TEXT," +VALUE+" TEXT, "+
            DATE+" TEXT, "+MDATE+" TEXT)";

    FirebaseFirestore db;
    Context context;
    private static UserControlController instance;
    private UserControlController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static UserControlController getInstance(Context context){
        if(instance == null){
            instance = new UserControlController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersUserControl);
        return reference;
    }

    public long insert(UserControl uc){
        ContentValues cv = new ContentValues();
        cv.put(CODE, uc.getCODE());
        cv.put(CODEUSER, uc.getCODEUSER());
        cv.put(CODEUSERTYPE, uc.getCODEUSERTYPE());
        cv.put(CONTROL, uc.getCONTROL());
        cv.put(VALUE, uc.getVALUE());
        cv.put(DATE, Funciones.getFormatedDate(uc.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(uc.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(UserControl uc, String where, String[]whereArgs){
        ContentValues cv = new ContentValues();
        cv.put(CODE, uc.getCODE());
        cv.put(CODEUSER, uc.getCODEUSER());
        cv.put(CODEUSERTYPE, uc.getCODEUSERTYPE());
        cv.put(CONTROL, uc.getCONTROL());
        cv.put(VALUE, uc.getVALUE());
        cv.put(DATE, Funciones.getFormatedDate(uc.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(uc.getMDATE()));

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
            Task<QuerySnapshot> client = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersUserControl).get();
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
                            UserControl object = dc.getDocument().toObject(UserControl.class);
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



    public void sendToFireBase(UserControl uc){
        try {
            WriteBatch lote = db.batch();
            lote.set(getReferenceFireStore().document(uc.getCODE()), uc.toMap());
            lote.commit();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void deleteFromFireBase(UserControl uc){
        try {
            getReferenceFireStore().document(uc.getCODE()).delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public ArrayList<UserControl> getUserControls(String where, String[]args, String orderBy){
        ArrayList<UserControl> result = new ArrayList<>();
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
            while(c.moveToNext()){
                result.add(new UserControl(c));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public UserControl getUserControlByCode(String code){
        String where = CODE+" = ?";
        ArrayList<UserControl> pts = getUserControls(where, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }

    /**
     * Simple seleccion row model
     * @param where
     * @param args
     * @param campoOrder
     * @return
     */
        /*
        public ArrayList<SimpleRowModel> getTablaCodeSRM(String where, String[] args, String campoOrder){
            ArrayList<SimpleRowModel> result = new ArrayList<>();
            if(campoOrder == null){campoOrder = DESCRIPTION;}
            where=((where != null)? "WHERE "+where:"");
            try {

                String sql = "SELECT "+CODE+" as CODE, "+DESCRIPTION+" AS DESCRIPTION, "+MDATE+" AS MDATE " +
                        "FROM "+TABLE_NAME+"  " +
                        where+" " +
                        "ORDER BY "+campoOrder;
                Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
                while(c.moveToNext()){
                    String code = c.getString(c.getColumnIndex("CODE"));
                    String name = c.getString(c.getColumnIndex("DESCRIPTION"));
                    String mdate = c.getString(c.getColumnIndex("MDATE"));

                    result.add(new SimpleRowModel(code,name ,(mdate!= null)));
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            return result;

        }

*/

    /**
     * CONTROL: ORDERSPLIT
     * Indica si el Usuario o Tipo de usuario fracionara la orden segun la Familia o Grupo de productos creando ordenes diferentes para cada agrupacion.
     * Depende del parametro: ORDERSPLITTYPE
     * @return
     */
    public boolean orderSplit(){
            boolean result = false;
            String sql = "SELECT "+VALUE+" " +
                    "FROM "+TABLE_NAME+" " +
                    "WHERE "+CONTROL+" = ? AND "+VALUE+" = ? AND ("+CODEUSER+" = ? OR "+CODEUSERTYPE+" = ?)  ";
            String[]args = new String[]{CODES.USERCONTROL_ORDERSPLIT, "1", Funciones.getCodeuserLogged(context), Funciones.getPreferences(context, CODES.PREFERENCE_USERSKEY_USERTYPE)};
            try {
                Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
                if (c.moveToFirst()) {
                    result = true;
                }
                c.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            return (result && orderSplitType()!= null);

        }


        public String orderSplitType(){

            String value = null;
            String sql = "SELECT "+VALUE+" " +
                    "FROM "+TABLE_NAME+" " +
                    "WHERE "+CONTROL+" = ? AND  ("+CODEUSER+" = ? OR "+CODEUSERTYPE+" = ?)  ";
            String[]args = new String[]{CODES.USERCONTROL_ORDERSPLITTYPE, Funciones.getCodeuserLogged(context), Funciones.getPreferences(context, CODES.PREFERENCE_USERSKEY_USERTYPE)};

           Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
           if(c.moveToFirst()){
               value = c.getString(0);
           }c.close();

           return (!value.equals("") && !value.equals("0"))?value:null;
        }

}
