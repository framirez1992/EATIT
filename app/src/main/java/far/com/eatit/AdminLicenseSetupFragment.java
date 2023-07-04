package far.com.eatit;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import far.com.eatit.API.models.License;
import far.com.eatit.Globales.CODES;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminLicenseSetupFragment extends Fragment {


    Main mainActivity;
    License license;
    ImageView btnDevices, btnTokens, btnUserDevices, btnUsers, btnUserTypes, btnCompany, btnControls;

    public AdminLicenseSetupFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AdminLicenseSetupFragment newInstance(Main mainActivity, License l) {
        AdminLicenseSetupFragment fragment = new AdminLicenseSetupFragment();
        fragment.mainActivity = mainActivity;
        fragment.license = l;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_license_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnDevices = view.findViewById(R.id.btnDevices);
        btnTokens = view.findViewById(R.id.btnTokens);
        btnCompany = view.findViewById(R.id.btnCompany);
        btnUserDevices = view.findViewById(R.id.btnUserDevices);
        btnUsers = view.findViewById(R.id.btnUsers);
        btnUserTypes = view.findViewById(R.id.btnUserTypes);
        btnControls = view.findViewById(R.id.btnControls);

        btnTokens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(getView(),"NO REQUIRED. FULLY ONLINE APP", BaseTransientBottomBar.LENGTH_LONG).show();
                return;
                //mainActivity.setAdminLicenseTokens(license);
            }
        });
        btnDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.setAdminLicenseDevices(license);
            }
        });

        btnUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mainActivity.setAdminLicenseUsers(license);
            }
        });

        btnUserDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.setAdminLicenseUserDevice(license);
            }
        });

        btnCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.setAdminLicenseCompany(license);
            }
        });


        btnUserTypes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.setAdminLicenseUserRole(license);
            }
        });
        btnControls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AdminLicenseControls.class);
                i.putExtra(CODES.EXTRA_ADMIN_LICENSE, license);
                startActivity(i);
            }
        });

        if(license != null){
            ((TextView)view.findViewById(R.id.tvLicenceDescription)).setText(license.getCode()+" - "+license.getClientName());
        }
    }




}
