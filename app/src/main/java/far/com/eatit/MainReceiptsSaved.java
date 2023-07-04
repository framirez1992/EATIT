package far.com.eatit;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import far.com.eatit.CloudFireStoreObjects.Receipts;
import far.com.eatit.Controllers.ReceiptController;
import far.com.eatit.Interfases.ListableActivity;

public class MainReceiptsSaved extends AppCompatActivity implements ListableActivity {

    CollectionReference receipts;
    ReceiptListFragment receiptListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_receipts_saved);
        setContentView(R.layout.content_main);
        receiptListFragment = new ReceiptListFragment();
        receiptListFragment.setMainActivityReference(this);

        //receipts = ReceiptController.getInstance(MainReceiptsSaved.this).getReferenceFireStore();
        changeFragment(receiptListFragment, R.id.details);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    public void changeFragment(Fragment f, int id){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.commit();
    }

    @Override
    public void onClick(Object obj) {

    }
}
