package far.com.eatit.Interfases;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

public interface FireStoreReader extends OnSuccessListener {
    void readQuerySnapshot(QuerySnapshot querySnapshot, int senderID);
}
