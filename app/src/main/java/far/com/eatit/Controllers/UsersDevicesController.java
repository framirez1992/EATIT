package far.com.eatit.Controllers;

import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.Globales.Tablas;

public class UsersDevicesController {
    public static String CODE = "code", CODEUSER= "codeuser", CODEDEVICE = "codedevice",  DATE = "date", MDATE = "mdate";

    FirebaseFirestore db;
    Context context;
    private static UsersDevicesController instance;
    private UsersDevicesController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static UsersDevicesController getInstance(Context context){
        if(instance == null){
            instance = new UsersDevicesController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(Licenses l){
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersUsersDevices);
        return reference;
    }



    public void getQueryusersDevices(Licenses l, String codeUser,String deviceID, OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failute){
        getReferenceFireStore(l).
                whereEqualTo(CODEUSER, codeUser).
                whereEqualTo(CODEDEVICE, deviceID).get().
                addOnSuccessListener(success).
                addOnCompleteListener(complete).
                addOnFailureListener(failute);

    }
}
