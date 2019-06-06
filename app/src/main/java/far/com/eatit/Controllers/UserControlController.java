package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Adapters.Models.UserControlRowModel;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.UserControl;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class UserControlController {
    public static final String TABLE_NAME = "USERCONTROL";
    public static String CODE = "code",TARGET ="target", TARGETCODE = "targetcode",CONTROL = "control",VALUE = "value",ACTIVE = "active", DATE = "date", MDATE = "mdate";
    public static String[]columns = new String[]{CODE, TARGET, TARGETCODE, CONTROL,VALUE,ACTIVE, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT, "+TARGET+" TEXT,"+TARGETCODE+" TEXT, "+CONTROL+" TEXT," +VALUE+" TEXT, "+ACTIVE+" BOOLEAN, "+
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
        cv.put(TARGET, uc.getTARGET());
        cv.put(TARGETCODE, uc.getTARGETCODE());
        cv.put(CONTROL, uc.getCONTROL());
        cv.put(VALUE, uc.getVALUE());
        cv.put(ACTIVE, uc.getACTIVE());
        cv.put(DATE, Funciones.getFormatedDate(uc.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(uc.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(UserControl uc, String where, String[]whereArgs){
        ContentValues cv = new ContentValues();
        cv.put(CODE, uc.getCODE());
        cv.put(TARGET, uc.getTARGET());
        cv.put(TARGETCODE, uc.getTARGETCODE());
        cv.put(CONTROL, uc.getCONTROL());
        cv.put(VALUE, uc.getVALUE());
        cv.put(ACTIVE, uc.getACTIVE());
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

        public ArrayList<UserControlRowModel> getTablaCodeSRM(String where, String[] args, String campoOrder){
            ArrayList<UserControlRowModel> result = new ArrayList<>();
            if(campoOrder == null){campoOrder = TARGET+" DESC,"+TARGETCODE+", "+CONTROL;}
            where=((where != null)? "WHERE "+where:"");
            try {

                String sql = "SELECT UC."+CODE+" as CODE, UC."+CONTROL+" AS CONTROL,UC."+TARGET+" as TARGET, UC."+TARGETCODE+" as TARGETCODE, IFNULL(C."+CompanyController.NAME+", IFNULL(UT."+UserTypesController.DESCRIPTION+", IFNULL(U."+UsersController.USERNAME+", ''))) as TARGETCODEDESCRIPTION, UC."+VALUE+" as VALUE, UC."+ACTIVE+" as ACTIVE,  UC."+MDATE+" AS MDATE " +
                        "FROM "+TABLE_NAME+" UC  " +
                        "LEFT JOIN "+CompanyController.TABLE_NAME+" C on UC."+TARGETCODE+" = C."+CompanyController.CODE+" AND UC."+TARGET+" = '"+CODES.USERSCONTROL_TARGET_COMPANY+"' "+
                        "LEFT JOIN "+UserTypesController.TABLE_NAME+" UT on UC."+TARGETCODE+" = UT."+UserTypesController.CODE+" AND UC."+TARGET+" = '"+CODES.USERSCONTROL_TARGET_USER_ROL+"' "+
                        "LEFT JOIN "+UsersController.TABLE_NAME+" U on UC."+TARGETCODE+" = U."+UsersController.CODE+" AND UC."+TARGET+" = '"+CODES.USERSCONTROL_TARGET_USER+"' "+
                         where+" " +
                        "ORDER BY "+campoOrder;
                Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
                while(c.moveToNext()){
                    String code = c.getString(c.getColumnIndex("CODE"));
                    String value = c.getString(c.getColumnIndex("VALUE"));
                    String description = c.getString(c.getColumnIndex("CONTROL"))+" ["+value+"]";
                    String target = c.getString(c.getColumnIndex("TARGET"));
                    String targetCode = c.getString(c.getColumnIndex("TARGETCODE"));
                    String active = c.getString(c.getColumnIndex("ACTIVE"));
                    String mdate = c.getString(c.getColumnIndex("MDATE"));
                    String targetName ="";
                    if(target.equals(CODES.USERSCONTROL_TARGET_COMPANY)){
                        targetName = "Compañia";
                    }else if(target.equals(CODES.USERSCONTROL_TARGET_USER_ROL)){
                        targetName="Cargo";
                    }else if(target.equals(CODES.USERSCONTROL_TARGET_USER)){
                        targetName="Usuario";
                    }else{ targetName="UNKNOWN";}

                    String targetDesc =  c.getString(c.getColumnIndex("TARGETCODEDESCRIPTION"));

                    result.add(new UserControlRowModel(code,description, target, targetName,targetCode ,targetDesc,active.equals("1"),(mdate!= null)));
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            return result;

        }



    /**
     * CONTROL: ORDERSPLIT
     * Indica si el Usuario o Tipo de usuario fracionara la orden segun la Familia o Grupo de productos creando ordenes diferentes para cada agrupacion.
     * Depende del parametro: ORDERSPLITTYPE
     * @return
     */
    public boolean orderSplit(){
            String result = searchControl(CODES.USERCONTROL_ORDERSPLIT);
            return (result !=null && orderSplitType()!= null);
        }

        public String orderSplitType(){
           return searchControl(CODES.USERCONTROL_ORDERSPLITTYPE);
        }

    public String searchControl(String control){
        String result = null;
        String sql = "SELECT "+VALUE+" " +
                "FROM "+TABLE_NAME+" " +
                "WHERE "+CONTROL+" = ? AND "+ACTIVE+" = ?  AND ( ("+TARGET+" = ? AND "+TARGETCODE+" = ? ) OR ("+TARGET+" = ? AND "+TARGETCODE+" = ? )  OR ("+TARGET+" = ? AND "+TARGETCODE+" = ? )    )  ";
        String[]args = new String[]{control, "1",
                CODES.USERSCONTROL_TARGET_USER,Funciones.getCodeuserLogged(context),
                CODES.USERSCONTROL_TARGET_USER_ROL,Funciones.getRoleUserLogged(context),
                CODES.USERSCONTROL_TARGET_COMPANY,UsersController.getInstance(context).getUserByCode(Funciones.getRoleUserLogged(context)).getCOMPANY() };
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            if (c.moveToFirst()) {
                result = c.getString(0);
            }
            c.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public void fillSpinnerControlLevels(Spinner spn, boolean addTodos){
        ArrayList<KV> list = new ArrayList<>();
        if(addTodos)
        list.add(new KV("-1", "TODOS"));

        list.add(new KV(CODES.USERSCONTROL_TARGET_USER_ROL, "Cargo"));
        list.add(new KV(CODES.USERSCONTROL_TARGET_COMPANY, "Empresa"));
        list.add(new KV(CODES.USERSCONTROL_TARGET_USER, "Usuario"));
        spn.setAdapter(new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1,list));
    }

}
