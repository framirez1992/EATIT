package far.com.eatit.Controllers;

import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import far.com.eatit.CloudFireStoreObjects.Licenses;
import far.com.eatit.CloudFireStoreObjects.Token;
import far.com.eatit.Globales.Tablas;

public class TokenController {
    public static String CODE = "code";

    FirebaseFirestore db;
    Context context;
    private static TokenController instance;
    private TokenController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static TokenController getInstance(Context context){
        if(instance == null){
            instance = new TokenController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersToken);
        return reference;
    }

    public void deleteFromFireBase(Token token){
        try {
            getReferenceFireStore().document(token.getCode()).delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getTokenByCode(String code, OnSuccessListener<QuerySnapshot> success, OnFailureListener failute){
        getReferenceFireStore().
                whereEqualTo(CODE, code).get().
                addOnSuccessListener(success).
                addOnFailureListener(failute);

    }

    public void getTokenByCode(String licenseCode, String code, OnSuccessListener<QuerySnapshot> success, OnFailureListener failute){
        db.collection(Tablas.generalUsers).document(licenseCode).collection(Tablas.generalUsersToken).
                whereEqualTo(CODE, code).get().
                addOnSuccessListener(success).
                addOnFailureListener(failute);

    }

    public void deleteToken(String licenseCode, String codeToken){

            db.collection(Tablas.generalUsers).document(licenseCode).collection(Tablas.generalUsersToken)
                    .document(codeToken).delete();

    }


}
