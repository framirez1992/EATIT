package far.com.eatit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.OptionModel;
import far.com.eatit.Adapters.OptionsAdapter;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Interfases.ListableActivity;


public class MaintenanceFragment extends Fragment {


    ImageView btnFamily, btnGroup, btnMeasures, btnProducts,btnFamilyInv, btnGroupInv, btnMeasuresInv, btnProductsInv, btnUsers, btnUserRol, btnAreas, btnMesas, btnTableCode,
            btnTableFilter, btnActualizationCenter;
    LinearLayout llMainScreen,llMaintenanceControls, llMaintenanceAreas, llMaintenanceUsers, llMaintenanceProducts,llMaintenanceInventory;
    public MaintenanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maintenance, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        llMainScreen = view.findViewById(R.id.llMainScreen);
        llMaintenanceControls = view.findViewById(R.id.llMaintenanceControls);
        llMaintenanceAreas = view.findViewById(R.id.llMaintenanceAreas);
        llMaintenanceUsers = view.findViewById(R.id.llMaintenanceUsers);
        llMaintenanceProducts = view.findViewById(R.id.llMaintenanceProducts);
        llMaintenanceInventory = view.findViewById(R.id.llMaintenanceInventory);

        btnFamily = view.findViewById(R.id.btnFamily);
        btnGroup = view.findViewById(R.id.btnGroups);
        btnMeasures = view.findViewById(R.id.btnMeasures);
        btnProducts = view.findViewById(R.id.btnProducts);
        btnFamilyInv = view.findViewById(R.id.btnFamilyInv);
        btnGroupInv = view.findViewById(R.id.btnGroupsInv);
        btnMeasuresInv = view.findViewById(R.id.btnMeasuresInv);
        btnProductsInv = view.findViewById(R.id.btnProductsInv);
        btnUsers = view.findViewById(R.id.btnUsers);
        btnUserRol = view.findViewById(R.id.btnUserRol);
        btnAreas = view.findViewById(R.id.btnAreas);
        btnMesas = view.findViewById(R.id.btnMesas);
        btnTableCode = view.findViewById(R.id.btnTableCode);
        btnTableFilter = view.findViewById(R.id.btnTableFilter);
        btnActualizationCenter = view.findViewById(R.id.btnActualizationCenter);

        btnFamily.setOnClickListener(imageClick);
        btnGroup.setOnClickListener(imageClick);
        btnMeasures.setOnClickListener(imageClick);
        btnProducts.setOnClickListener(imageClick);

        btnFamilyInv.setOnClickListener(imageClick);
        btnGroupInv.setOnClickListener(imageClick);
        btnMeasuresInv.setOnClickListener(imageClick);
        btnProductsInv.setOnClickListener(imageClick);

        btnUsers.setOnClickListener(imageClick);
        btnUserRol.setOnClickListener(imageClick);
        btnAreas.setOnClickListener(imageClick);
        btnMesas.setOnClickListener(imageClick);
        btnTableCode.setOnClickListener(imageClick);
        btnTableFilter.setOnClickListener(imageClick);
        btnActualizationCenter.setOnClickListener(imageClick);


    }

    public View.OnClickListener imageClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = null;
            switch (v.getId()){
                case R.id.btnFamily:
                case R.id.btnFamilyInv:
                    i = new Intent(getActivity(), MaintenanceProductTypes.class);
                    i.putExtra(CODES.EXTRA_TYPE_FAMILY, (v.getId() == R.id.btnFamilyInv)? CODES.ENTITY_TYPE_EXTRA_INVENTORY:CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE );
                    break;
                case R.id.btnMeasures:
                case R.id.btnMeasuresInv:
                    i = new Intent(getActivity(), MaintenanceUnitMeasure.class);
                    i.putExtra(CODES.EXTRA_TYPE_FAMILY, (v.getId() == R.id.btnMeasuresInv)? CODES.ENTITY_TYPE_EXTRA_INVENTORY:CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE );
                    break;
                case R.id.btnUsers:
                    i = new Intent(getActivity(), MaintenanceUsers.class);
                    break;
                case R.id.btnUserRol:
                    i = new Intent(getActivity(), MaintenanceUserTypes.class);
                    break;
                case R.id.btnGroups:
                case R.id.btnGroupsInv:
                    i = new Intent(getActivity(), MaintenanceProductSubTypes.class);
                    i.putExtra(CODES.EXTRA_TYPE_FAMILY, (v.getId() == R.id.btnGroupsInv)? CODES.ENTITY_TYPE_EXTRA_INVENTORY:CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE );
                    break;
                case R.id.btnProducts:
                case R.id.btnProductsInv:
                    i = new Intent(getActivity(), MaintenanceProducts.class);
                    i.putExtra(CODES.EXTRA_TYPE_FAMILY, (v.getId() == R.id.btnProductsInv)? CODES.ENTITY_TYPE_EXTRA_INVENTORY:CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE );
                    break;
                case R.id.btnAreas:
                    i = new Intent(getActivity(), MaintenanceAreas.class);
                    break;
                case R.id.btnMesas:
                    i = new Intent(getActivity(), MaintenanceAreasDetail.class);
                    break;
                case R.id.btnTableCode:
                    i = new Intent(getActivity(), MaintenanceTableCodes.class);
                    break;
                case R.id.btnTableFilter:
                    i = new Intent(getActivity(), MaintenanceTableFilter.class);
                    break;
                case R.id.btnActualizationCenter:
                    i = new Intent(getActivity(), MainActualizationCenter.class);
                    break;

            }

            startActivity(i);
        }


    };
}
