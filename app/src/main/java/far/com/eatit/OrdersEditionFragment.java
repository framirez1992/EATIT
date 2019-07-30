package far.com.eatit;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import far.com.eatit.Adapters.Models.OrderDetailModel;
import far.com.eatit.Adapters.OrderDetailAdapter;
import far.com.eatit.Adapters.OrdersBoardAdapter;
import far.com.eatit.CloudFireStoreObjects.AreasDetail;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.CloudFireStoreObjects.TableCode;
import far.com.eatit.CloudFireStoreObjects.UserInbox;
import far.com.eatit.Controllers.AreasController;
import far.com.eatit.Controllers.AreasDetailController;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Controllers.TableCodeController;
import far.com.eatit.Controllers.UserControlController;
import far.com.eatit.Controllers.UserInboxController;
import far.com.eatit.Dialogs.NotificationsDialog;
import far.com.eatit.Dialogs.WorkedOrdersDialog;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.Utils.Funciones;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersEditionFragment extends Fragment {


    DialogFragment parent;
    Sales sales = null;
    RecyclerView rvList;
    TextView tvOrderNumber,tvArea, tvMesa, tvTime, tvNotes;
    LinearLayout llEdition;
    Button btnCerrar, btnEntregar, btnEditar, btnAnular;
    ImageView imgBack;

    public OrdersEditionFragment() {
        // Required empty public constructor
    }

    public void setParent(DialogFragment p){
        this.parent = p;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders_edition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        showSales();
    }


    public void init(View v){
        rvList = v.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(parent.getContext()));
        tvOrderNumber = v.findViewById(R.id.tvOrderNumber);
        tvArea = v.findViewById(R.id.tvArea);
        tvMesa = v.findViewById(R.id.tvMesa);
        tvNotes = v.findViewById(R.id.tvNotes);
        tvTime = v.findViewById(R.id.tvTime);
        llEdition = v.findViewById(R.id.llEdition);
        btnCerrar = v.findViewById(R.id.btnCerrar);
        btnEntregar = v.findViewById(R.id.btnEntregar);
        btnEditar = v.findViewById(R.id.btnEditar);
        btnAnular = v.findViewById(R.id.btnAnular);
        imgBack = v.findViewById(R.id.imgBack);

        if(imgBack != null){
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(parent instanceof NotificationsDialog){
                        ((NotificationsDialog)parent).goToMessagesList();
                    }else if(parent instanceof WorkedOrdersDialog){
                        ((WorkedOrdersDialog)parent).goToMessagesList();
                    }

                }
            });
        }

        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sales != null) {
                    if (sales.getSTATUS() == CODES.CODE_ORDER_STATUS_DELIVERED) {
                        cerrarOrden();
                    } else {
                        Snackbar.make(parent.getView(), "No se puede CERRAR la orden", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnEntregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sales != null) {
                    if (sales.getSTATUS() == CODES.CODE_ORDER_STATUS_READY) {
                        entregarOrden();
                    } else {
                        Snackbar.make(parent.getView(), "No se puede ENTREGAR la orden", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnAnular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sales != null) {
                    if ((sales.getSTATUS() == CODES.CODE_ORDER_STATUS_OPEN || sales.getSTATUS() == CODES.CODE_ORDER_STATUS_READY || sales.getSTATUS() == CODES.CODE_ORDER_STATUS_DELIVERED)) {
                        showDialogReturnCause();
                    } else {
                        Snackbar.make(parent.getView(), "No se puede ANULAR la orden.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(parent instanceof NotificationsDialog){
                    ((NotificationsDialog)parent).editOrder(sales);
                     parent.dismiss();
                }else if(parent instanceof WorkedOrdersDialog){
                    ((WorkedOrdersDialog)parent).editOrder(sales);
                    parent.dismiss();
                }

            }
        });

    }

    public void setSales(Sales s){
        this.sales = s;
        showSales();
    }
    public void showSales(){
        if(sales != null && tvOrderNumber != null) {
            tvOrderNumber.setText("Orden: " + sales.getCODE());
            AreasDetail ad = AreasDetailController.getInstance(parent.getContext()).getAreasDetailByCode(sales.getCODEAREADETAIL());
            tvArea.setText(AreasController.getInstance(parent.getContext()).getAreaByCode(ad.getCODEAREA()).getDESCRIPTION());
            tvMesa.setText(ad.getDESCRIPTION());
            if(sales.getSTATUS() == CODES.CODE_ORDER_STATUS_OPEN && sales.getDATE().equals(sales.getMDATE())){
               tvTime.setVisibility(View.GONE);
            }else{
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText("Listo hace " + Funciones.calcularMinutos(new Date(), sales.getMDATE()) + " Mins");
            }

            tvNotes.setText("Notas: " + ((sales.getNOTES() != null) ? sales.getNOTES() : ""));

            OrderDetailAdapter adapter = new OrderDetailAdapter(parent.getActivity(),SalesController.getInstance(getActivity()).getOrderDetailModels(sales.getCODE()));
            rvList.setAdapter(adapter);
            rvList.invalidate();
        }

    }

    public void cerrarOrden(){
        if(sales != null){
            sales.setSTATUS(CODES.CODE_ORDER_STATUS_CLOSED);
            sales.setMDATE(null);//actualizar fecha de ultima actualizacion.


            ArrayList<Sales> s = new ArrayList<>();
            s.add(sales);
            ///////////////////////////////////////////////////////////////////
            ///////////   ENVIANDO AL HISTORICO     ///////////////////////////

            SalesController.getInstance(parent.getContext()).sendToHistory(s);
            ///////////////////////////////////////////////////////////////////

            ///////////////////////////////////////////////////////////////////
            //////      ELIMINANDO DE LA TABLA SALES Y SALES_DETAIL EN FIREBASE   ////////
            SalesController.getInstance(parent.getContext()).massiveDelete(s);
            //////////////////////////////////////////////////////////////////

            ///////////////////////////////////////////////////////////////////
            //////////  ELIMINANDOLA EN EL MOVIL   ///////////////////////////
            SalesController.getInstance(parent.getContext()).deleteHeadDetail(sales);//esto es porque la lista se actualizara antes de que el server retorne la actualizacion.
            //////////////////////////////////////////////////////////////////

            ((MainOrders)parent.getActivity()).refreshInterface();
            clear();

            imgBack.performClick();
            if(parent instanceof WorkedOrdersDialog){
                ((WorkedOrdersDialog)parent).refreshNotifications();
            }else if(parent instanceof NotificationsDialog){
                ((WorkedOrdersDialog)parent).refreshNotifications();
            }
        }
    }

    public void entregarOrden(){

        if(sales != null) {
            btnEntregar.setEnabled(false);
            sales.setSTATUS(CODES.CODE_ORDER_STATUS_DELIVERED);
            SalesController.getInstance(getActivity()).update(sales);//esto es porque la lista se actualizara antes de que el server retorne la actualizacion.

            sales.setMDATE(null);//actualizar fecha de ultima actualizacion.
            SalesController.getInstance(getActivity()).sendToFireBase(sales);

            //////////////////////////////////////////////////////////////////
            //////     ELIMINANDO ALERTAS DE ESTA ORDEN             /////////

            String where = UserInboxController.CODEMESSAGE + " = ? AND " + UserInboxController.STATUS + " = ?";
            String[] args = new String[]{sales.getCODE(), CODES.CODE_USERINBOX_STATUS_NO_READ + ""};
            ArrayList<UserInbox> inbox = UserInboxController.getInstance(parent.getContext()).getUserInbox(where, args, null);
            if (inbox != null && inbox.size() > 0) {
                UserInboxController.getInstance(parent.getContext()).massiveDelete(inbox);
            }
            /////////////////////////////////////////////////////////////////

            ((MainOrders)parent.getActivity()).refreshInterface();
            clear();
            btnEntregar.setEnabled(true);
            imgBack.performClick();
        }
    }

    public void anularOrden(KV motivo){
        if(sales!= null){
            boolean wasOpen = sales.getSTATUS() == CODES.CODE_ORDER_STATUS_OPEN;//INDICA SI LA ORDEN AUN ESTABA ABIERTA CUANDO LA ANULARON

            sales.setSTATUS(CODES.CODE_ORDER_STATUS_CANCELED);
            sales.setCODEREASON(motivo.getKey());
            sales.setREASONDESCRIPTION(motivo.getValue());

            /////////////////////////////////////////////////////////////////
            ////////   ELIMINANDO DE LA TABLA SALES           //////////////
            //SalesController.getInstance(getActivity()).sendToFireBase(sales);
            ArrayList<Sales> del = new ArrayList<>();
            del.add(sales);
            SalesController.getInstance(getActivity()).massiveDelete(del);
            ////////////////////////////////////////////////////////////////

            ///////////////////////////////////////////////////////////////////
            ///////////   ENVIANDO AL HISTORICO     ///////////////////////////
            ArrayList<Sales> s = new ArrayList<>();
            s.add(sales);
            SalesController.getInstance(parent.getContext()).sendToHistory(s);
            ///////////////////////////////////////////////////////////////////

            if(wasOpen) {
               /* String where = UserInboxController.CODEMESSAGE + " = ? AND " + UserInboxController.STATUS + " = ?";
                String[] args = new String[]{sales.getCODE(), CODES.CODE_USERINBOX_STATUS_NO_READ + ""};
                ArrayList<UserInbox> inbox = UserInboxController.getInstance(parent.getContext()).getUserInbox(where, args, null);
                if (inbox != null && inbox.size() > 0) {
                    for (UserInbox ui : inbox) {
                        UserInboxController.getInstance(parent.getContext()).update(ui);
                    }
                    UserInboxController.getInstance(parent.getContext()).sendToFireBase(inbox);
                }*/
            }


            ///////////////////////////////////////////////////////////////////
            //////////  ELIMINANDOLA EN EL MOVIL   ///////////////////////////
            SalesController.getInstance(parent.getContext()).deleteHeadDetail(sales);//esto es porque la lista se actualizara antes de que el server retorne la actualizacion.
            //////////////////////////////////////////////////////////////////


            ((MainOrders)parent.getActivity()).refreshInterface();
            clear();

            imgBack.performClick();
        }
    }

    public void clear(){
        sales = null;
        tvOrderNumber.setText("");
        tvArea.setText("");
        tvMesa.setText("");
        tvTime.setText("");
        tvNotes.setText("");
        rvList.setAdapter(null);
        rvList.invalidate();
    }


    public void setupEdition(){

       if(sales.getSTATUS() == CODES.CODE_ORDER_STATUS_READY || sales.getSTATUS() == CODES.CODE_ORDER_STATUS_OPEN || sales.getSTATUS() == CODES.CODE_ORDER_STATUS_DELIVERED) {
           btnEditar.setVisibility(View.GONE);
           btnAnular.setVisibility(View.GONE);

           if(UserControlController.getInstance(parent.getContext()).editOrders()){
               btnEditar.setVisibility(View.VISIBLE);
           }
           if(UserControlController.getInstance(parent.getContext()).cancelOrders()){
               btnAnular.setVisibility(View.VISIBLE);
           }

           btnEntregar.setVisibility((sales.getSTATUS() == CODES.CODE_ORDER_STATUS_READY)?View.VISIBLE:View.GONE);

       }else if(sales.getSTATUS() == CODES.CODE_ORDER_STATUS_CANCELED || sales.getSTATUS() == CODES.CODE_ORDER_STATUS_CLOSED ){
           btnEditar.setVisibility(View.GONE);
           btnAnular.setVisibility(View.GONE);
           btnEntregar.setVisibility(View.GONE);

       }else {//En caso de que este nul o algo asi
           btnEditar.setVisibility(View.GONE);
           btnAnular.setVisibility(View.GONE);
           btnEntregar.setVisibility(View.GONE);
       }
    }

    public void showDialogReturnCause(){
        btnAnular.setEnabled(false);
        final Dialog d = new Dialog(parent.getContext());
        d.setCancelable(false);
        d.setContentView(R.layout.msg_2_buttons);
        final Spinner spn = d.findViewById(R.id.spn);
        final Button btnAceptar = d.findViewById(R.id.btnPositive);
        Button btnCancelar = d.findViewById(R.id.btnNegative);
        TextView tvMsg = d.findViewById(R.id.tvMsg);
        spn.setVisibility(View.VISIBLE);

        tvMsg.setText("Seleccione un motivo");
        TableCodeController.getInstance(parent.getContext()).fillSpinnerByCode(spn,CODES.TABLA_MOTIVOS_ANULADO_CODE);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAnular.setEnabled(true);
                d.dismiss();
            }
        });
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spn != null && spn.getAdapter()!= null && spn.getAdapter().getCount() >0){
                    btnAceptar.setEnabled(false);
                    anularOrden(((KV)spn.getSelectedItem()));
                    d.dismiss();
                    btnAnular.setEnabled(true);
                    btnAceptar.setEnabled(true);
                }else{
                    Toast.makeText(parent.getContext(), "El motivo es obligatorio", Toast.LENGTH_LONG).show();
                }
            }
        });

        d.show();

    }
}
