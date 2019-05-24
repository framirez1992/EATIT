package far.com.eatit.Interfases;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

public interface FireStoreReader extends OnSuccessListener {
    public void readQuerySnapshot(QuerySnapshot querySnapshot, int senderID);
}
