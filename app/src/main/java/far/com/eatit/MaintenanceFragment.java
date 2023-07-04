package far.com.eatit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import far.com.eatit.API.models.License;
import far.com.eatit.API.models.LoginResponse;
import far.com.eatit.Controllers.UserControlController;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Utils.Funciones;


public class MaintenanceFragment extends Fragment {


    Main mainActivity;
    ImageView btnFamily, btnGroup, btnMeasures, btnProducts,btnFamilyInv, btnGroupInv, btnMeasuresInv, btnProductsInv, btnUsers, btnUserRol, btnAreas, btnMesas,btnControls, btnTableCode,
            btnTableFilter/*, btnActualizationCenter*/, btnUserTable, btnUsersControl, btnRolesControl, btnOrderSplit, btnOrderSplitDestiny, btnOrderMove;
    LinearLayout llMainScreen,llMaintenanceControls, llMaintenanceAreas, llMaintenanceUsers, llMaintenanceProducts,llMaintenanceInventory;

    public MaintenanceFragment() {
        // Required empty public constructor
    }

    public static MaintenanceFragment newInstance(Main mainActivity) {
        MaintenanceFragment fragment = new MaintenanceFragment();
        fragment.mainActivity = mainActivity;
        return fragment;
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
        btnControls = view.findViewById(R.id.btnControls);
        btnTableCode = view.findViewById(R.id.btnTableCode);
        btnTableFilter = view.findViewById(R.id.btnTableFilter);
        //btnActualizationCenter = view.findViewById(R.id.btnActualizationCenter);
        btnUserTable = view.findViewById(R.id.btnUserTable);
        btnUsersControl = view.findViewById(R.id.btnUsersControl);
        btnRolesControl = view.findViewById(R.id.btnRolesControl);
        btnOrderSplit = view.findViewById(R.id.btnOrderSplit);
        btnOrderSplitDestiny = view.findViewById(R.id.btnOrderSplitDestiny);
        btnOrderMove = view.findViewById(R.id.btnOrderMove);

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
        btnUserTable.setOnClickListener(imageClick);

        btnAreas.setOnClickListener(imageClick);
        btnMesas.setOnClickListener(imageClick);

        btnControls.setOnClickListener(imageClick);
        btnUsersControl.setOnClickListener(imageClick);
        btnRolesControl.setOnClickListener(imageClick);
        btnTableCode.setOnClickListener(imageClick);
        btnTableFilter.setOnClickListener(imageClick);
        btnOrderSplit.setOnClickListener(imageClick);
        btnOrderSplitDestiny.setOnClickListener(imageClick);
        btnOrderMove.setOnClickListener(imageClick);


       //btnActualizationCenter.setOnClickListener(imageClick);

    }

    public View.OnClickListener imageClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = null;
            switch (v.getId()){
                case R.id.btnFamily:
                case R.id.btnFamilyInv:
                    mainActivity.setMaintenanceProductTypes((v.getId() == R.id.btnFamilyInv)? CODES.ENTITY_TYPE_EXTRA_INVENTORY:CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE );
                    //i = new Intent(getActivity(), MaintenanceProductTypes.class);
                    //i.putExtra(CODES.EXTRA_TYPE_FAMILY, (v.getId() == R.id.btnFamilyInv)? CODES.ENTITY_TYPE_EXTRA_INVENTORY:CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE );
                    return;
                case R.id.btnGroups:
                case R.id.btnGroupsInv:
                    mainActivity.setMaintenanceProductSubTypes((v.getId() == R.id.btnGroupsInv)? CODES.ENTITY_TYPE_EXTRA_INVENTORY:CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE );
                    return;
                    /*i = new Intent(getActivity(), MaintenanceProductSubTypes.class);
                    i.putExtra(CODES.EXTRA_TYPE_FAMILY, (v.getId() == R.id.btnGroupsInv)? CODES.ENTITY_TYPE_EXTRA_INVENTORY:CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE );
                    break;*/
                case R.id.btnMeasures:
                case R.id.btnMeasuresInv:
                    mainActivity.setMaintenanceUnitMeasure((v.getId() == R.id.btnMeasuresInv)? CODES.ENTITY_TYPE_EXTRA_INVENTORY:CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE);
                    /*i = new Intent(getActivity(), MaintenanceUnitMeasure.class);
                    i.putExtra(CODES.EXTRA_TYPE_FAMILY, (v.getId() == R.id.btnMeasuresInv)? CODES.ENTITY_TYPE_EXTRA_INVENTORY:CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE );
                    break;*/
                    return;
                case R.id.btnUsers:
                    i = new Intent(getActivity(), MaintenanceUsers.class);
                    break;
                case R.id.btnUserRol:
                    i = new Intent(getActivity(), MaintenanceUserTypes.class);
                    break;
                case R.id.btnUserTable:
                    i =new Intent(getActivity(), MainAssignation.class);
                    i.putExtra(CODES.EXTRA_MAINASSIGNATION_TABLE, UserControlController.TABLE_NAME);
                    i.putExtra(CODES.EXTRA_MAINASSIGNATION_TARGET, CODES.USERCONTROL_TABLEASSIGN);
                    break;
                case R.id.btnProducts:
                case R.id.btnProductsInv:
                    /*
                    i = new Intent(getActivity(), MaintenanceProducts.class);
                    i.putExtra(CODES.EXTRA_TYPE_FAMILY, (v.getId() == R.id.btnProductsInv)? CODES.ENTITY_TYPE_EXTRA_INVENTORY:CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE );
                    break;
                     */
                    mainActivity.setMaintenanceProducts((v.getId() == R.id.btnProductsInv)? CODES.ENTITY_TYPE_EXTRA_INVENTORY:CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE);
                    return;
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
               /* case R.id.btnActualizationCenter:
                    i = new Intent(getActivity(), MainActualizationCenter.class);
                    break;*/
                case R.id.btnControls:
                    i = new Intent(getActivity(), MaintenanceUsersControl.class);
                    break;
                case R.id.btnRolesControl:
                    i =new Intent(getActivity(), MainAssignation.class);
                    i.putExtra(CODES.EXTRA_MAINASSIGNATION_TABLE, UserControlController.TABLE_NAME);
                    i.putExtra(CODES.EXTRA_MAINASSIGNATION_TARGET, CODES.EXTRA_MAINASSIGNATION_TARGET_ROLESCONTROL);
                    break;
                case R.id.btnUsersControl:
                    i =new Intent(getActivity(), MainAssignation.class);
                    i.putExtra(CODES.EXTRA_MAINASSIGNATION_TABLE, UserControlController.TABLE_NAME);
                    i.putExtra(CODES.EXTRA_MAINASSIGNATION_TARGET, CODES.EXTRA_MAINASSIGNATION_TARGET_USERSCONTROL);
                    break;
                case R.id.btnOrderSplit:
                    i =new Intent(getActivity(), MainAssignation.class);
                    i.putExtra(CODES.EXTRA_MAINASSIGNATION_TABLE, UserControlController.TABLE_NAME);
                    i.putExtra(CODES.EXTRA_MAINASSIGNATION_TARGET, CODES.USERCONTROL_ORDERSPLIT);
                    break;
                case R.id.btnOrderSplitDestiny:
                    i =new Intent(getActivity(), MainAssignation.class);
                    i.putExtra(CODES.EXTRA_MAINASSIGNATION_TABLE, UserControlController.TABLE_NAME);
                    i.putExtra(CODES.EXTRA_MAINASSIGNATION_TARGET, CODES.USERCONTROL_ORDERSPLITDESTINY);
                    break;
                case R.id.btnOrderMove:
                    i =new Intent(getActivity(), MainOrderReasignation.class);
                    break;


            }

            startActivity(i);
        }


    };
}
