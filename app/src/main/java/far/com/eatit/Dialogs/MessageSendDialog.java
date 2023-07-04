package far.com.eatit.Dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import far.com.eatit.API.models.Sale;
import far.com.eatit.Adapters.Models.SimpleSeleccionRowModel;
import far.com.eatit.Adapters.SimpleSelectionRowAdapter;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.CloudFireStoreObjects.UserInbox;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Controllers.UserInboxController;
import far.com.eatit.Controllers.UserTypesController;
import far.com.eatit.Controllers.UsersController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.Globales.CODES;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;

public class MessageSendDialog extends DialogFragment {


    UserInbox userInbox;
    Sale sales;

    UserInboxController userInboxController;
    Spinner spnTarget, spnDestiny;
    TextInputEditText etMessage;
    RecyclerView rvList;
    LinearLayout llSend;
    Activity activity;

    ArrayList<SimpleSeleccionRowModel> selected = new ArrayList<>();

    public  static MessageSendDialog newInstance(Activity act){
        MessageSendDialog f = new MessageSendDialog();
        f.activity = act;
        return f;
    }
    public  static MessageSendDialog newInstance(Activity act, UserInbox userInbox) {
        MessageSendDialog f = new MessageSendDialog();
        f.activity = act;
        f.userInbox = userInbox;

        // Supply num input as an argument.
        Bundle args = new Bundle();

        return f;
    }
    public  static MessageSendDialog newInstance(Activity act, Sale sales) {
        MessageSendDialog f = new MessageSendDialog();
        f.activity = act;
        f.sales = sales;
        // Supply num input as an argument.
        Bundle args = new Bundle();

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userInboxController = UserInboxController.getInstance(getActivity());
        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etMessage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.send_message_dialog, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);


    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }


    public void init(View v){
        etMessage = v.findViewById(R.id.etMessage);
        spnTarget = v.findViewById(R.id.spnTarget);
        spnDestiny = v.findViewById(R.id.spnDestiny);
        rvList = v.findViewById(R.id.rvList);
        llSend = v.findViewById(R.id.llSend);

        if(userInbox != null || sales != null){
            rvList.setVisibility(View.GONE);
            ((LinearLayout)v.findViewById(R.id.llTargetDestiny)).setVisibility(View.GONE);
        }else {
            rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
            spnTarget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int code = Integer.parseInt(((KV) parent.getAdapter().getItem(position)).getKey());
                    TargetSelected(code);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spnDestiny.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    KV selection = ((KV) parent.getAdapter().getItem(position));
                    String where = null;
                    String[] args = null;
                    String orderBy = UsersController.ROLE + "," + UsersController.USERNAME;

                    where = " " + UsersController.ROLE + " = ? ";
                    args = new String[]{selection.getKey()};

                    refreshSelected();

                    rvList.setAdapter(new SimpleSelectionRowAdapter(getActivity(), UsersController.getInstance(getActivity()).getUserSSRM(where, args, orderBy), selected));
                    rvList.getAdapter().notifyDataSetChanged();
                    rvList.invalidate();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            userInboxController.fillTargetSpinner(spnTarget);
        }

        llSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userInbox != null || sales != null) {
                    Reply();
                }else{
                    Send();
                }
                dismiss();
            }
        });

    }


    public void TargetSelected(int code){
       refreshSelected();

        switch (code){
            case CODES.CODE_MESSAGE_TARGET_ALL:
                spnDestiny.setAdapter(new ArrayAdapter<KV>(getActivity(), android.R.layout.simple_list_item_1));
                ((ViewGroup)getView().findViewById(R.id.llDestiny)).setVisibility(View.INVISIBLE);
                ArrayList<SimpleSeleccionRowModel> objects = UsersController.getInstance(getActivity()).getUserSSRM(null, null, UsersController.USERNAME);
                rvList.setAdapter(new SimpleSelectionRowAdapter(getActivity(),objects, selected));
                rvList.invalidate();
                break;
            case CODES.CODE_MESSAGE_TARGET_GRUPOS:
                UserTypesController.getInstance(getActivity()).fillSpnUserTypes(spnDestiny,false);
                ((ViewGroup)getView().findViewById(R.id.llDestiny)).setVisibility(View.VISIBLE);
                break;
            case CODES.CODE_MESSAGE_TARGET_USERS:
                break;
        }
    }


    public void Send(){
        refreshSelected();
        ArrayList<UserInbox> mails = new ArrayList<>();
        String msgID = Funciones.generateCode();
        UserInbox ui = new UserInbox(Funciones.generateCode(),Funciones.getCodeuserLogged(getActivity()),Funciones.getCodeuserLogged(getActivity()),msgID,"Subject",etMessage.getText().toString(),CODES.CODE_TYPE_OPERATION_MESSAGE+"",CODES.CODE_ICON_MESSAGE_NEW,CODES.CODE_USERINBOX_STATUS_READ);
        mails.add(ui);//mensaje para mi mismo en status leido.
        for(SimpleSeleccionRowModel s: selected){
            if(s.isChecked()){
             mails.add(new UserInbox(Funciones.generateCode(),Funciones.getCodeuserLogged(getActivity()),s.getCode(),msgID,"Subject",etMessage.getText().toString(),CODES.CODE_TYPE_OPERATION_MESSAGE+"",CODES.CODE_ICON_MESSAGE_NEW,CODES.CODE_USERINBOX_STATUS_NO_READ));
            }
        }

        userInboxController.sendToFireBase(mails);


    }

    public void Reply(){
        ArrayList<UserInbox> mails = new ArrayList<>();
        String msgID = "";
        if(userInbox != null){
            msgID = userInbox.getCODEMESSAGE();
            UserInbox ui = new UserInbox(Funciones.generateCode(),userInbox.getCODEUSER(),userInbox.getCODEUSER(),msgID,userInbox.getSUBJECT(),etMessage.getText().toString(),CODES.CODE_TYPE_OPERATION_MESSAGE+"",CODES.CODE_ICON_MESSAGE_NEW,CODES.CODE_USERINBOX_STATUS_READ);


            mails.add(ui);//mensaje para mi mismo en status leido.
            mails.add(new UserInbox(Funciones.generateCode(),userInbox.getCODEUSER(),userInbox.getCODESENDER(),msgID,userInbox.getSUBJECT(),etMessage.getText().toString(),CODES.CODE_TYPE_OPERATION_MESSAGE+"",CODES.CODE_ICON_MESSAGE_NEW,CODES.CODE_USERINBOX_STATUS_NO_READ));

        }else if(sales != null){
            msgID = sales.getId()+"";
            String msg = etMessage.getText().toString();
            String subject = "Orden: "+sales.getId();
            UserInbox ui = new UserInbox(Funciones.generateCode(), Funciones.getCodeuserLogged(activity),Funciones.getCodeuserLogged(getActivity()),msgID,subject,msg,CODES.CODE_TYPE_OPERATION_SALES+"",CODES.CODE_ICON_MESSAGE_ALERT,CODES.CODE_USERINBOX_STATUS_READ);


            mails.add(ui);//mensaje para mi mismo en status leido.
            mails.add(new UserInbox(Funciones.generateCode(),Funciones.getCodeuserLogged(activity),sales.getId()+"",msgID,subject,msg,CODES.CODE_TYPE_OPERATION_SALES+"",CODES.CODE_ICON_MESSAGE_ALERT,CODES.CODE_USERINBOX_STATUS_NO_READ));

        }

        String where = UserInboxController.CODEMESSAGE+" = ?  AND "+UserInboxController.STATUS+" = ? ";
        for(UserInbox savedMail: userInboxController.getUserInbox(where, new String[]{msgID, CODES.CODE_USERINBOX_STATUS_NO_READ+""}, null)){
            savedMail.setSTATUS(CODES.CODE_USERINBOX_STATUS_READ);//toma todos los viejos NO LEIDOS y LOS ACTUALIZA a LEIDO
            mails.add(savedMail);
        }

        userInboxController.sendToFireBase(mails);
    }

    public void refreshSelected(){
        if(rvList.getAdapter() != null){
            selected = ((SimpleSelectionRowAdapter)rvList.getAdapter()).getSelectedObjects();
        }
    }
}
