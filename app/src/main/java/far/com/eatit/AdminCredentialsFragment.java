package far.com.eatit;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminCredentialsFragment extends Fragment {
    Main mainActivity;
    EditText etUser, etPass;
    TextView btnOk;

    public AdminCredentialsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AdminCredentialsFragment newInstance(Main mainActivity) {
        AdminCredentialsFragment fragment = new AdminCredentialsFragment();
        fragment.mainActivity = mainActivity;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_credentials, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }


    public void init(View v){
        btnOk = v.findViewById(R.id.btnOK);
        etUser = v.findViewById(R.id.etUser);
        etPass = v.findViewById(R.id.etPass);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autenticate();
            }
        });
    }

    public void autenticate(){
        String user = etUser.getText().toString();
        String pass = etPass.getText().toString();
        if(user.equals("admin") && pass.equals("admin")){
          mainActivity.setLicenseFragment();
        }
    }





}
