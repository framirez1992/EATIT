package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Tables;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.TableFilterRowModel;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.ProductsTypes;
import far.com.eatit.CloudFireStoreObjects.TableFilter;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;
import far.com.eatit.Utils.Funciones;

public class TableFilterController {
        public static String TABLE_NAME = "TABLEFILTER";
        public static String CODE = "code", TABLES= "tables", USER = "user",USERTYPE = "usertype", PRODUCTTYPE = "producttype", PRODUCTSUBTYPE = "productsubtype", TASK = "task", FILTER = "filter",ENABLED = "enabled",  DATE = "date", MDATE = "mdate";
        public static String[]columns = new String[]{CODE, TABLES, USER, USERTYPE,PRODUCTTYPE, PRODUCTSUBTYPE,TASK,FILTER,ENABLED, DATE, MDATE};
        public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
                +CODE+" TEXT, "+TABLES+" TEXT,"+USER+" TEXT, "+USERTYPE+" TEXT,"+PRODUCTTYPE+" TEXT, "+PRODUCTSUBTYPE+" TEXT," +TASK+" TEXT, "+FILTER+" TEXT, "+ENABLED+" TEXT, "+
                DATE+" TEXT, "+MDATE+" TEXT)";

        FirebaseFirestore db;
        Context context;
        private static TableFilterController instance;
        private TableFilterController(Context c){
            this.context = c;
            db = FirebaseFirestore.getInstance();
        }

        public static TableFilterController getInstance(Context context){
            if(instance == null){
                instance = new TableFilterController(context);
            }
            return instance;
        }

        public CollectionReference getReferenceFireStore(){
            Licenses l = LicenseController.getInstance(context).getLicense();
            if(l == null){
                return null;
            }
            CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersTableFilter);
            return reference;
        }

        public long insert(TableFilter tc){
            ContentValues cv = new ContentValues();
            cv.put(CODE, tc.getCode());
            cv.put(TABLES, tc.getTable());
            cv.put(USER, tc.getUser());
            cv.put(USERTYPE, tc.getUsertype());
            cv.put(PRODUCTTYPE, tc.getProducttype());
            cv.put(PRODUCTSUBTYPE, tc.getProductsubtype());
            cv.put(TASK, tc.getTask());
            cv.put(FILTER, tc.getFilter());
            cv.put(ENABLED, tc.getEnabled());
            cv.put(DATE, Funciones.getFormatedDate(tc.getDate()));
            cv.put(MDATE, Funciones.getFormatedDate(tc.getDate()));

            long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
            return result;
        }

        public long update(TableFilter tc, String where, String[]whereArgs){
            ContentValues cv = new ContentValues();
            cv.put(CODE, tc.getCode());
            cv.put(TABLES, tc.getTable());
            cv.put(USER, tc.getUser());
            cv.put(USERTYPE, tc.getUsertype());
            cv.put(PRODUCTTYPE, tc.getProducttype());
            cv.put(PRODUCTSUBTYPE, tc.getProductsubtype());
            cv.put(TASK, tc.getTask());
            cv.put(FILTER, tc.getFilter());
            cv.put(ENABLED, tc.getEnabled());
            cv.put(DATE, Funciones.getFormatedDate(tc.getDate()));
            cv.put(MDATE, Funciones.getFormatedDate(tc.getDate()));

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
                Task<QuerySnapshot> client = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersTableFilter).get();
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
                            TableFilter object = dc.getDocument().toObject(TableFilter.class);
                            String where = CODE+" = ?";
                            String[]args = new String[]{object.getCode()};
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

        public void sendToFireBase(ArrayList<TableFilter> tableFilters){
            try {
                WriteBatch lote = db.batch();
                for(TableFilter tc: tableFilters) {
                    if(tc.getDate() == null) {
                        lote.set(getReferenceFireStore().document(tc.getCode()), tc.toMap());
                    }else{
                        lote.update(getReferenceFireStore().document(tc.getCode()), tc.toMap());
                    }

                }
                lote.commit();
            }catch(Exception e){
                e.printStackTrace();
            }

        }

        public void deleteFromFireBase(TableFilter tc){
            try {
                getReferenceFireStore().document(tc.getCode()).delete();
            }catch(Exception e){
                e.printStackTrace();
            }
        }


        public ArrayList<TableFilter> getTableFilters(String where, String[]args, String orderBy){
            ArrayList<TableFilter> result = new ArrayList<>();
            try{
                Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
                while(c.moveToNext()){
                    result.add(new TableFilter(c));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return result;
        }
        public TableFilter getTableFilterByCode(String code){
            String where = CODE+" = ?";
            ArrayList<TableFilter> pts = getTableFilters(where, new String[]{code}, null);
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

        public ArrayList<TableFilterRowModel> getTableFilterRM(String where, String[] args, String campoOrder){
            ArrayList<TableFilterRowModel> result = new ArrayList<>();
            if(campoOrder == null){campoOrder = TASK;}
            where=((where != null)? "WHERE "+where:"");
            try {

                String sql = "SELECT tf."+CODE+" as CODE, tf."+TABLES+" AS TABLES, tf."+USER+" as USER, u."+UsersController.USERNAME+" as USERNAME, tf."+USERTYPE+" as USERTYPE, " +
                        "ut."+UserTypesController.DESCRIPTION+" as USERTYPEDESC, tf."+PRODUCTTYPE+" as PRODUCTTYPE, pt."+ProductsTypesController.DESCRIPTION+" as PRODUCTTYPEDESC, " +
                        "tf."+PRODUCTSUBTYPE+" as PRODUCTSUBTYPE, pst."+ProductsSubTypesController.DESCRIPTION+" as PRODUCTSUBTYPEDESC, tf."+TASK+" as TASK, tf."+FILTER+" as FILTER, "+
                        "tf."+ENABLED+" as ENABLED,tf."+MDATE+" AS MDATE " +
                        "FROM "+TABLE_NAME+" tf  " +
                        "LEFT JOIN "+UsersController.TABLE_NAME+" u on u.code = tf."+USER+" " +
                        "LEFT JOIN "+UserTypesController.TABLE_NAME+" ut on ut."+UserTypesController.CODE+" = tf."+USERTYPE+" " +
                        "LEFT JOIN "+ ProductsTypesController.TABLE_NAME+" pt on pt."+ProductsTypesController.CODE+" = tf."+PRODUCTTYPE+" " +
                        "LEFT JOIN "+ ProductsSubTypesController.TABLE_NAME+" pst on pst."+ProductsSubTypesController.CODE+" = tf."+PRODUCTSUBTYPE+" "+
                         where+" " +
                        "ORDER BY "+campoOrder;
                Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
                while(c.moveToNext()){

                    String code = c.getString(c.getColumnIndex("CODE"));
                    String table = c.getString(c.getColumnIndex("TABLES"));
                    String user = c.getString(c.getColumnIndex("USER"));
                    String userDescription = c.getString(c.getColumnIndex("USERNAME"));
                    String userType = c.getString(c.getColumnIndex("USERTYPE"));
                    String userTypeDescription = c.getString(c.getColumnIndex("USERTYPEDESC"));
                    String productType = c.getString(c.getColumnIndex("PRODUCTTYPE"));
                    String productTypeDescription = c.getString(c.getColumnIndex("PRODUCTTYPEDESC"));
                    String productSubType = c.getString(c.getColumnIndex("PRODUCTSUBTYPE"));
                    String productSubTypeDescription = c.getString(c.getColumnIndex("PRODUCTSUBTYPEDESC"));
                    String task =  c.getString(c.getColumnIndex("TASK"));
                    String filter = c.getString(c.getColumnIndex("FILTER"));
                    String enabled =  c.getString(c.getColumnIndex("ENABLED"));
                    String mdate = c.getString(c.getColumnIndex("MDATE"));
                    boolean inServer = (mdate!= null);

                    result.add(new TableFilterRowModel(code,table ,user,userDescription,userType, userTypeDescription,productType,productTypeDescription,productSubType, productSubTypeDescription, task, filter,enabled,inServer));
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            return result;

        }




        public String getConditionsByTableTask(String table, String task){
            String where = "";
            String condition = ENABLED+" = ? AND "+TABLES+" = ? AND "+TASK+" = ? AND ("+USER+" = ? OR "+USERTYPE+" = ?) ";
            String[] args = new String[]{"1", table, task, Funciones.getPreferences(context, CODES.PREFERENCE_USERSKEY_CODE),Funciones.getPreferences(context, CODES.PREFERENCE_USERSKEY_USERTYPE) };
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns,condition,args, null, null, null );

            if(c.moveToFirst()){
                String aliasTabla = c.getString(c.getColumnIndex(TABLES));
                where+=((where.equals(""))?" AND (":" OR ");
                do{
                   if(c.getString(c.getColumnIndex(TASK)).equals(CODES.TABLE_FILTER_CODETASK_WORKORDER)){
                       if(c.getString(c.getColumnIndex(PRODUCTTYPE)) != null && !c.getString(c.getColumnIndex(PRODUCTTYPE)).equals("")){
                         where+=aliasTabla+"."+SalesController.CODEPRODUCTTYPE+" = '"+c.getString(c.getColumnIndex(PRODUCTTYPE))+"' ";
                       }else if(c.getString(c.getColumnIndex(PRODUCTSUBTYPE)) != null && !c.getString(c.getColumnIndex(PRODUCTSUBTYPE)).equals("")){
                           where+=aliasTabla+"."+SalesController.CODEPRODUCTSUBTYPE+" = '"+c.getString(c.getColumnIndex(PRODUCTSUBTYPE))+"' ";
                       }else{
                           where = "";
                           break;
                       }
                   }

                }while(c.moveToNext()); c.close();

                where+=(!where.equals(""))?") ":"";
            }

            return where;

        }


    public void fillSpnTables(Spinner spn, boolean addTodos){
        ArrayList<KV> spnList = new ArrayList<>();
        if(addTodos){
            spnList.add(new KV("0", "TODOS"));
        }
        for(String tbl: Tablas.tablesFireBase){
            spnList.add(new KV(tbl, tbl));
        }

        spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,spnList));
    }

    public void fillSpnTaskByTable(Spinner spn, String table){
        String where =TABLES+" = ? ";
        String[]args = new String[]{table};

        //ArrayList<TableFilter> result = getTableFilters(where, args, null);
        ArrayList<KV> spnList = new ArrayList<>();
        //for(TableFilter tf : result){
          //  spnList.add(new KV(tf.getTask(), tf.getTask()));
        //}
        if(table.equals(Tablas.generalUsersSales)){
            spnList.add(new KV(CODES.TABLE_FILTER_CODETASK_WORKORDER, CODES.TABLE_FILTER_CODETASK_WORKORDER));
        }
        spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,spnList));
    }


    public void fillSpnOriginType(Spinner spn){
        ArrayList<KV> spnList = new ArrayList<>();
        spnList.add(new KV(CODES.TABLE_FILTER_ORIGIN_PRODUCTTYPE, "FAMILIA DE PRODUCTO"));
        spnList.add(new KV(CODES.TABLE_FILTER_ORIGIN_PRODUCTSUBTYPE, "GRUPO DE PRODUCTO"));
        spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,spnList));
    }

    public void fillSpnDestinyType(Spinner spn){
        ArrayList<KV> spnList = new ArrayList<>();
        spnList.add(new KV(CODES.TABLE_FILTER_DESTINY_USER, "USUARIO"));
        spnList.add(new KV(CODES.TABLE_FILTER_DESTINY_USERTYPE, "ROL"));
        spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,spnList));
    }

    public TableFilter findTableFilterData(String tables,String task, String user, String userType,String productType, String productSubType){
            TableFilter tf = null;
            String where = TABLES+" = ? AND "+TASK+" = ? ";
            ArrayList<String> v = new ArrayList();
            v.add(tables);v.add(task);
       ;
            if(user != null){
                where+= " AND "+USER+" = ? ";
                v.add(user);
            }
            if(userType != null){
                where+= " AND "+USERTYPE+" = ? ";
                v.add(userType);
            }
            if(productType != null){
                where+= "AND "+PRODUCTTYPE+" = ? ";
                v.add(productType);
            }
            if(productSubType != null){
                where+= " AND "+PRODUCTSUBTYPE+" = ? ";
                v.add(productSubType);
            }
            String[]args =  v.toArray(new String[v.size()]);
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where, args, null, null, null);

            if(c.moveToFirst()){
                tf = new TableFilter(c);
            }c.close();

            return tf;

    }

    public String getCodeOriginTypeByTableFilter(TableFilter tf){
            if(tf.getProducttype() != null && !tf.getProducttype().equals("")){
                return CODES.TABLE_FILTER_ORIGIN_PRODUCTTYPE;
            }else{
                return CODES.TABLE_FILTER_ORIGIN_PRODUCTSUBTYPE;
            }
    }


    public String getCodeDestinyTypeByTableFilter(TableFilter tf){
        if(tf.getUser() != null && !tf.getUser().equals("")){
            return CODES.TABLE_FILTER_DESTINY_USER;
        }else{
            return CODES.TABLE_FILTER_DESTINY_USERTYPE;
        }
    }
}
