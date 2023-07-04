package far.com.eatit.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;

import far.com.eatit.API.models.Company;
import far.com.eatit.API.models.UserRole;
import far.com.eatit.Adapters.Models.CompanyRowModel;
import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.DataBase.DB;
import far.com.eatit.Generic.Objects.KV;


public class CompanyController {
    public static final String TABLE_NAME ="COMPANY";
    public static  String IDCOMPANY="idCompany",IDLICENSE="idLicense", CODE = "code", NAME = "name" ,
            RNC = "rnc",PHONE = "phone", PHONE2="phone2",PHONE3="phone3",LOGO = "logo", ADDRESS="address", ADDRESS2="address2",ADDRESS3="address3",
            CREATEDATE = "createDate", CREATEUSER = "createUser", UPDATEDATE="updateDate", UPDATEUSER="updateUser",DELETEDATE="deleteDate",DELETEUSER="deleteUser" ;
    String[] columns = new String[]{IDCOMPANY,IDLICENSE,CODE, NAME, RNC, PHONE, PHONE2,PHONE3, ADDRESS, ADDRESS2,ADDRESS3,LOGO, CREATEDATE, CREATEUSER,UPDATEDATE, UPDATEUSER, DELETEDATE, DELETEUSER};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +IDCOMPANY+" INTEGER, "
            +IDLICENSE+" INTEGER, "
            +CODE+" TEXT, "
            +NAME+" TEXT, "
            +RNC+" TEXT, "
            +PHONE+" TEXT, "
            +PHONE2+" TEXT, "
            +PHONE3+" TEXT, "
            +ADDRESS+" TEXT,"
            +ADDRESS2+" TEXT,"
            +ADDRESS3+" TEXT,"
            +LOGO+" TEXT,  "
            +CREATEDATE+" TEXT, "
            +CREATEUSER+" TEXT, "
            +UPDATEDATE+" TEXT, "
            +UPDATEUSER+" TEXT, "
            +DELETEDATE+" TEXT, "
            +DELETEUSER+" TEXT "
            +")";
    Context context;
    DB db;
    private static CompanyController instance;
    private CompanyController(Context c){
        this.context = c;
        this.db = DB.getInstance(c);
    }

    public static CompanyController getInstance(Context context){
        if(instance == null){
            instance = new CompanyController(context);
        }
        return instance;
    }

    public void insertOrUpdate(Company obj){
        String sql ="insert or replace into "+TABLE_NAME+" ("+IDCOMPANY+", "+IDLICENSE+", "+CODE+", "+NAME+","+RNC+","+PHONE+","+PHONE2+","+PHONE3+","+ADDRESS+","+ADDRESS2+","+ADDRESS3+","+LOGO+", "+CREATEDATE+", "+CREATEUSER+","+UPDATEDATE+", "+UPDATEUSER+", "+DELETEDATE+", "+DELETEUSER+") values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        db.getWritableDatabase().execSQL(sql,new String[]{obj.getId()+"", obj.getIdLicense()+"", obj.getCode(), obj.getName(),obj.getRnc(),obj.getPhone(),obj.getPhone2(),obj.getPhone3(),obj.getAddress(),obj.getAddress2(),obj.getAddress3(),obj.getLogo(), obj.getCreateDate(), obj.getCreateUser(),  obj.getUpdateDate(),obj.getUpdateUser(),  obj.getDeleteDate(), obj.getDeleteUser() });
    }

    public long insert(Company obj){
        ContentValues cv = new ContentValues();
        cv.put(IDCOMPANY,obj.getId() );
        cv.put(IDLICENSE,obj.getIdLicense() );
        cv.put(CODE,obj.getCode() );
        cv.put(NAME,obj.getName());
        cv.put(RNC, obj.getRnc());
        cv.put(PHONE,obj.getPhone() );
        cv.put(PHONE2,obj.getPhone2() );
        cv.put(PHONE3,obj.getPhone3() );
        cv.put(ADDRESS,obj.getAddress() );
        cv.put(ADDRESS2,obj.getAddress2() );
        cv.put(ADDRESS3,obj.getAddress3() );
        cv.put(LOGO, obj.getLogo());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Company obj){
        ContentValues cv = new ContentValues();
        cv.put(IDCOMPANY,obj.getId() );
        cv.put(IDLICENSE,obj.getIdLicense() );
        cv.put(CODE,obj.getCode() );
        cv.put(NAME,obj.getName());
        cv.put(RNC, obj.getRnc());
        cv.put(PHONE,obj.getPhone() );
        cv.put(PHONE2,obj.getPhone2() );
        cv.put(PHONE3,obj.getPhone3() );
        cv.put(ADDRESS,obj.getAddress() );
        cv.put(ADDRESS2,obj.getAddress2() );
        cv.put(ADDRESS3,obj.getAddress3() );
        cv.put(LOGO, obj.getLogo());
        cv.put(CREATEDATE, obj.getCreateDate());
        cv.put(CREATEUSER, obj.getCreateUser());
        cv.put(UPDATEDATE, obj.getCreateDate());
        cv.put(UPDATEUSER, obj.getUpdateUser());
        cv.put(DELETEDATE, obj.getCreateDate());
        cv.put(DELETEUSER, obj.getDeleteUser());

        long result = db.getWritableDatabase().update(TABLE_NAME,cv,IDCOMPANY.concat("= ?"),new String[]{ obj.getId()+""});
        return result;
    }

    public long delete(Company obj){
        long result = db.getWritableDatabase().delete(TABLE_NAME,IDCOMPANY.concat("= ?"),new String[]{ obj.getId()+""});
        return result;
    }



    public ArrayList<Company> getCompanys(String where, String[]args, String orderBy){
        ArrayList<Company> result = new ArrayList<>();
        try{
            Cursor c = db.getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
            while(c.moveToNext()){
                result.add(new Company(c));
            }c.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public Company getCompany(){
        Company result=null;
        try{
            Cursor c = db.getReadableDatabase().query(TABLE_NAME,columns,null,null,null,null,null);
            if(c.moveToFirst()){
                result=new Company(c);
            }c.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<CompanyRowModel> getCompanyRM(String where, String[] args, String campoOrder){
        ArrayList<CompanyRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = NAME;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT "+CODE+" as CODE,"+RNC+" as RNC, "+NAME+" AS NAME, "+PHONE+" AS PHONE, "+PHONE2+" as PHONE2, " +
                    ""+ADDRESS+" as ADDRESS, "+ADDRESS2+" as ADDRESS2,"+LOGO+" as LOGO, "+CREATEDATE +" AS CREATEDATE " +
                    "FROM "+TABLE_NAME+" u " +
                    where;
            Cursor c = db.getReadableDatabase().rawQuery(sql, args);
            while(c.moveToNext()){
                result.add(new CompanyRowModel(c.getString(c.getColumnIndex("CODE")),
                        c.getString(c.getColumnIndex("NAME")),
                        c.getString(c.getColumnIndex("RNC")),
                        c.getString(c.getColumnIndex("ADDRESS")) ,
                        c.getString(c.getColumnIndex("ADDRESS2")) ,
                        c.getString(c.getColumnIndex("PHONE")) ,
                        c.getString(c.getColumnIndex("PHONE2")) ,
                        c.getString(c.getColumnIndex("LOGO")) ,
                        c.getString(c.getColumnIndex("CREATEDATE")) != null));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }









   /* public void addCompanyToPrint(Print p){
        ArrayList<Company> companys = getCompanys(null, null, null);
        String name="NONE";
        String direction ="NONE";
        String phone = "NONE";
        if(companys.size() > 0){
            name = companys.get(0).getNAME();
            direction = companys.get(0).getADDRESS();
            phone = Funciones.formatPhone(companys.get(0).getPHONE());
        }
        p.addAlign(Print.PRINTER_ALIGN.ALIGN_CENTER);
        //p.addImage(R.drawable.ic_action_monetization_on);
        if(!name.equals("NONE")){
            p.drawText(name);
        }
        if(!direction.equals("NONE")){
            p.drawText(direction);
        }
        if(!phone.equals("NONE")){
            p.drawText(phone);
        }



        p.drawText("");
        p.addAlign(Print.PRINTER_ALIGN.ALIGN_LEFT);
    }*/




    public void fillSpnCompany(Spinner spn){
        ArrayList<Company> result = getCompanys(null, null, null);
        ArrayList<KV> spnList = new ArrayList<>();
        for(Company ut : result){
            spnList.add(new KV(ut.getCode(), ut.getName()+" ["+ut.getRnc()+"]"));
        }
        spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,spnList));
    }

}
