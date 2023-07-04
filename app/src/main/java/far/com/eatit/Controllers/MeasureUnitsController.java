package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
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

import far.com.eatit.API.models.Company;
import far.com.eatit.API.models.MeasureUnit;
import far.com.eatit.Adapters.Models.EditSelectionRowModel;
import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Adapters.Models.SimpleSeleccionRowModel;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.MeasureUnits;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class MeasureUnitsController {
    public static final  String TABLE_NAME = "MEASUREUNITS";
    public static String IDMEASUREUNIT="idMeasureUnit",IDLICENSE="idLicense",CODE = "code", DESCRIPTION = "description",
            CREATEDATE = "createDate", CREATEUSER = "createUser", UPDATEDATE="updateDate", UPDATEUSER="updateUser",DELETEDATE="deleteDate",DELETEUSER="deleteUser" ;
    public static String[]columns = new String[]{IDMEASUREUNIT,IDLICENSE,CODE, DESCRIPTION,CREATEDATE, CREATEUSER,UPDATEDATE, UPDATEUSER, DELETEDATE, DELETEUSER};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +IDMEASUREUNIT+" INTEGER, "
            +IDLICENSE+" INTEGER, "
            +CODE+" TEXT, "
            +DESCRIPTION+" TEXT, "
            +CREATEDATE+" TEXT, "
            +CREATEUSER+" TEXT, "
            +UPDATEDATE+" TEXT, "
            +UPDATEUSER+" TEXT, "
            +DELETEDATE+" TEXT, "
            +DELETEUSER+" TEXT "
            +")";

        Context context;
        DB db;
        private static MeasureUnitsController instance;
        private MeasureUnitsController(Context c){
            this.context = c;
            this.db = DB.getInstance(c);
        }

        public static MeasureUnitsController getInstance(Context context){
            if(instance == null){
                instance = new MeasureUnitsController(context);
            }
            return instance;
        }

    public void insertOrUpdate(MeasureUnit obj){
        String sql ="insert or replace into "+TABLE_NAME+" ("+IDMEASUREUNIT+", "+IDLICENSE+", "+CODE+", "+DESCRIPTION+", "+CREATEDATE+", "+CREATEUSER+","+UPDATEDATE+", "+UPDATEUSER+", "+DELETEDATE+", "+DELETEUSER+") values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        db.getWritableDatabase().execSQL(sql,new String[]{obj.getId()+"", obj.getIdLicense()+"", obj.getCode(), obj.getDescription(), obj.getCreateDate(), obj.getCreateUser(),  obj.getUpdateDate(),obj.getUpdateUser(),  obj.getDeleteDate(), obj.getDeleteUser() });
    }

    public long insert(MeasureUnit obj){
        ContentValues cv = new ContentValues();
        cv.put(IDMEASUREUNIT,obj.getId());
        cv.put(IDLICENSE,obj.getIdLicense());
        cv.put(CODE,obj.getCode());
        cv.put(DESCRIPTION,obj.getDescription());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(MeasureUnit obj){
        ContentValues cv = new ContentValues();
        cv.put(IDMEASUREUNIT,obj.getId());
        cv.put(IDLICENSE,obj.getIdLicense());
        cv.put(CODE,obj.getCode());
        cv.put(DESCRIPTION,obj.getDescription());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,IDMEASUREUNIT.concat("= ?"), new String[]{obj.getId()+""});
        return result;
    }

    public long delete(MeasureUnit obj){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,IDMEASUREUNIT.concat("= ?"), new String[]{obj.getId()+""});
        return result;
    }



    public ArrayList<MeasureUnit> getMeasureUnits(String where, String[]args, String orderBy){
            ArrayList<MeasureUnit> result = new ArrayList<>();
            try{
                Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
                while(c.moveToNext()){
                    result.add(new MeasureUnit(c));
                }
            }catch(Exception e){
              e.printStackTrace();
            }
            return result;
    }



    public ArrayList<SimpleRowModel> getMeasureUnitsSRM(String where, String[] args, String campoOrder){
            ArrayList<SimpleRowModel> result = new ArrayList<>();
          /*  if(campoOrder == null){campoOrder = DESCRIPTION;}
            try {
                Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, where, args, null, null, campoOrder);
                while(c.moveToNext()){
                    result.add(new SimpleRowModel(c.getString(c.getColumnIndex(CODE)), c.getString(c.getColumnIndex(DESCRIPTION)), c.getString(c.getColumnIndex(MDATE)) != null));
                }
            }catch(Exception e){
               e.printStackTrace();
            }*/

            return result;

    }

    public void fillSpinner(Spinner spn){
            try {
                ArrayList<KV> result = new ArrayList<>();
                Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, null, null, null, null, DESCRIPTION);
                while(c.moveToNext()){
                    result.add(new KV(c.getString(c.getColumnIndex(IDMEASUREUNIT)), c.getString(c.getColumnIndex(DESCRIPTION))));
                }
                c.close();

                spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,result));
            }catch(Exception e){
               e.printStackTrace();
            }
    }

    public ArrayList<KV> getMeasureUnitsKV(){
        ArrayList<KV> result = new ArrayList<>();
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, null, null, null, null, DESCRIPTION);
            while(c.moveToNext()){
                result.add(new KV(c.getString(c.getColumnIndex(IDMEASUREUNIT)), c.getString(c.getColumnIndex(DESCRIPTION))));
            }
            c.close();


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
    public ArrayList<EditSelectionRowModel> getUnitMeasuresSSRM(String where, String[] args, String campoOrder){
        ArrayList<EditSelectionRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = DESCRIPTION;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT u."+IDMEASUREUNIT+" as IDMEASUREUNIT, u."+DESCRIPTION+" AS DESCRIPTION,  u."+CREATEDATE+" AS CREATEDATE " +
                    "FROM "+TABLE_NAME+" u " +
                    where;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while(c.moveToNext()){
                String code = c.getString(c.getColumnIndex("IDMEASUREUNIT"));
                String name = c.getString(c.getColumnIndex("DESCRIPTION"));
                result.add(new EditSelectionRowModel(code,name ,"", false));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }

}
