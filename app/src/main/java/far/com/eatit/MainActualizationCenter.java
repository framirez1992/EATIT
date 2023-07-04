package far.com.eatit;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;

import far.com.eatit.CloudFireStoreObjects.Token;
import far.com.eatit.Controllers.AreasController;
import far.com.eatit.Controllers.AreasDetailController;
import far.com.eatit.Controllers.CombosController;
import far.com.eatit.Controllers.CompanyController;
import far.com.eatit.Controllers.MeasureUnitsController;
import far.com.eatit.Controllers.MeasureUnitsInvController;
import far.com.eatit.Controllers.PriceListController;
import far.com.eatit.Controllers.ProductsControlController;
import far.com.eatit.Controllers.ProductsController;
import far.com.eatit.Controllers.ProductsInvController;
import far.com.eatit.Controllers.ProductsMeasureController;
import far.com.eatit.Controllers.ProductsMeasureInvController;
import far.com.eatit.Controllers.ProductsSubTypesController;
import far.com.eatit.Controllers.ProductsSubTypesInvController;
import far.com.eatit.Controllers.ProductsTypesController;
import far.com.eatit.Controllers.ProductsTypesInvController;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Controllers.TableCodeController;
import far.com.eatit.Controllers.TableFilterController;
import far.com.eatit.Controllers.TokenController;
import far.com.eatit.Controllers.UserControlController;
import far.com.eatit.Controllers.UserInboxController;
import far.com.eatit.Controllers.UserTypesController;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Globales.Tablas;

public class MainActualizationCenter extends AppCompatActivity implements OnSuccessListener<QuerySnapshot>, OnFailureListener, OnCompleteListener, OnCanceledListener {

    ProgressBar pb;
    TextView tvMessage;
    Button btnExit;
    Token token;
    JSONArray tableList = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_actualization_center);
        if(getIntent().getExtras()== null || !getIntent().getExtras().containsKey(CODES.EXTRA_TOKEN) ){
            finish();
            return;
        }
        token = (Token) getIntent().getSerializableExtra(CODES.EXTRA_TOKEN);
        try{
            tableList = new JSONArray(token.getExtradata());
        }catch(Exception e){
            e.printStackTrace();
        }

        pb = findViewById(R.id.pb);
        tvMessage = findViewById(R.id.tvMessage);
        btnExit = findViewById(R.id.btnExit);

        tvMessage.setText("Por favor espere mietras se actualizan los datos...");
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pb.setVisibility(View.VISIBLE);
        loadData();


    }

    @Override
    public void onBackPressed() {

    }


    public void loadData(){
        try{
            if(tableList.length()>0){
                if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersAreas)){
                    //AreasController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersAreasDetail)){
                    //AreasDetailController.getInstance(MainActualizationCenter.this).searchDetailChanges(true, this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersCombos)){
                    CombosController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersCompany)){
                    //CompanyController.getInstance(MainActualizationCenter.this).searchChanges(true,  this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersMeasureUnits)){
                    //MeasureUnitsController.getInstance(MainActualizationCenter.this).searchChanges(true,  this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersMeasureUnitsInv)){
                    MeasureUnitsInvController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersPriceList)){
                    PriceListController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersProducts)){
                    //ProductsController.getInstance(MainActualizationCenter.this).searchChanges(true,  this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersProductsInv)){
                    ProductsInvController.getInstance(MainActualizationCenter.this).searchChanges(true,  this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersProductsControl)){
                    ProductsControlController.getInstance(MainActualizationCenter.this).searchChanges(true,  this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersProductsMeasure)){
                    //ProductsMeasureController.getInstance(MainActualizationCenter.this).searchChanges(true,  this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersProductsMeasureInv)){
                    ProductsMeasureInvController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersProductsTypes)){
                    //ProductsTypesController.getInstance(MainActualizationCenter.this).searchChanges(true,  this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersProductsTypesInv)){
                    ProductsTypesInvController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersProductsSubTypes)){
                   // ProductsSubTypesController.getInstance(MainActualizationCenter.this).searchChanges(true,  this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersProductsSubTypesInv)){
                    ProductsSubTypesInvController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this);
                }/*else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersSales)){
                    SalesController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersSalesDetails)){
                    SalesController.getInstance(MainActualizationCenter.this).searchDetailChanges(true, this, this);
                }*/else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersTableCode)){
                    TableCodeController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersTableFilter)){
                    TableFilterController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersUserControl)){
                    UserControlController.getInstance(MainActualizationCenter.this).searchChanges( true, this, this);
                }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersUserTypes)){
                    UserTypesController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this);
                }/*else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersUserInbox)){
                    UserInboxController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this);
                }*/else{
                    tableList.remove(0);
                    loadData();
                }
            }else{
                clearToken();
                tvMessage.setText("Finalizado Correctamente");
                pb.setVisibility(View.INVISIBLE);
                btnExit.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }



    @Override
    public void onFailure(@NonNull Exception e) {
        tvMessage.setText(e.getMessage()+" - "+e.getLocalizedMessage());
        pb.setVisibility(View.INVISIBLE);
        btnExit.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCanceled() {
        tvMessage.setText("Canceled");
        pb.setVisibility(View.INVISIBLE);
        btnExit.setVisibility(View.VISIBLE);
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if(task.getException() != null){
            tvMessage.setText(task.getException().toString());
            pb.setVisibility(View.INVISIBLE);
            btnExit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSuccess(QuerySnapshot querySnapshot) {

        try{
            if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersAreas)){
                //AreasController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersAreasDetail)){
                //AreasDetailController.getInstance(MainActualizationCenter.this).consumeDetailQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersCombos)){
                CombosController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersCompany)){
                //CompanyController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersMeasureUnits)){
                //MeasureUnitsController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersMeasureUnitsInv)){
                MeasureUnitsInvController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersPriceList)){
                PriceListController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersProducts)){
                //ProductsController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersProductsInv)){
                ProductsInvController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersProductsControl)){
                ProductsControlController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersProductsMeasure)){
                //ProductsMeasureController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersProductsMeasureInv)){
                ProductsMeasureInvController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersProductsTypes)){
                //ProductsTypesController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersProductsTypesInv)){
                ProductsTypesInvController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersProductsSubTypes)){
               // ProductsSubTypesController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersProductsSubTypesInv)){
                ProductsSubTypesInvController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersSales)){
               // SalesController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersSalesDetails)){
              //  SalesController.getInstance(MainActualizationCenter.this).consumeDetailQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersTableCode)){
                TableCodeController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersTableFilter)){
                TableFilterController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersUserControl)){
                UserControlController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersUserTypes)){
                UserTypesController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }else if(tableList.get(0).toString().equalsIgnoreCase(Tablas.generalUsersUserInbox)){
                UserInboxController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot);
            }

            tableList.remove(0);
            loadData();
        }catch (Exception e){
            onFailure(new Exception(e));
        }


    }

    public void clearToken(){
        if(token != null){
            if(token.isAutodelete()){
                TokenController.getInstance(MainActualizationCenter.this).deleteFromFireBase(token);
            }
        }
    }
}

