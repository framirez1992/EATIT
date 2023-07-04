package far.com.eatit;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import far.com.eatit.API.models.Sale;
import far.com.eatit.CloudFireStoreObjects.Sales;
import far.com.eatit.CloudFireStoreObjects.UserInbox;
import far.com.eatit.Controllers.SalesController;
import far.com.eatit.Controllers.UserInboxController;
import far.com.eatit.Controllers.UsersController;
import far.com.eatit.Dialogs.NotificationsDialog;
import far.com.eatit.Globales.CODES;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageDetailFragment extends Fragment {


    NotificationsDialog parent;
    UserInboxController userInboxController;
    UserInbox userInbox;
    TextView tvFrom, tvTitle;
    ListView rvList;
    Button btnResponder, btnConfirmar, btnEditar;


    public MessageDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userInboxController = UserInboxController.getInstance(parent.getActivity());
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        fillMessage();
    }

    public void setParent(NotificationsDialog nd){
       this.parent = nd;
    }
    public void init(View v){
        this.tvTitle = v.findViewById(R.id.tvTitle);
        this.tvFrom = v.findViewById(R.id.tvFrom);
        this.rvList = v.findViewById(R.id.rvList);
        btnResponder = v.findViewById(R.id.btnResponder);
        btnConfirmar = v.findViewById(R.id.btnConfirmar);
        btnEditar = v.findViewById(R.id.btnEditar);

        btnResponder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userInbox!= null) {
                    parent.callMsgDialog();
                }
            }
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(userInbox != null) {
                    userInbox.setSTATUS(CODES.CODE_USERINBOX_STATUS_READ);
                    userInboxController.update(userInbox);
                    userInbox.setMDATE(null);

                    ArrayList<UserInbox> uiAray = new ArrayList<>();
                    uiAray.add(userInbox);
                    userInboxController.sendToFireBase(uiAray);

                    userInbox = null;
                }

            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userInbox != null) {
                    Sale s = SalesController.getInstance(parent.getActivity()).getSaleById(/*userInbox.getCODEMESSAGE()*/0);
                    if(s != null && s.getStatus() == CODES.CODE_ORDER_STATUS_OPEN){
                        parent.editOrder(s);
                    }else{
                        Snackbar.make(parent.getView(), "Esta orden ya esta trabajada", Snackbar.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    public void setUserInbox(UserInbox ui){
        this.userInbox = ui;
        fillMessage();
    }
    public void fillMessage(){
        if(userInbox != null && tvFrom != null) {
            tvTitle.setText(userInbox.getSUBJECT());
            tvFrom.setText(UsersController.getInstance(parent.getActivity()).getUserByCode(userInbox.getCODESENDER()).getUSERNAME());
            if(userInbox.getTYPE().equals(String.valueOf(CODES.CODE_TYPE_OPERATION_SALES))){
                btnEditar.setVisibility(View.VISIBLE);
            }

            ArrayList<String> result = new ArrayList<>();
            String where = UserInboxController.CODEMESSAGE + " = ? ";
            String[] args = new String[]{userInbox.getCODEMESSAGE()};
            for (UserInbox u : UserInboxController.getInstance(parent.getActivity()).getUserInbox(where, args, UserInboxController.MDATE)) {
                result.add(UsersController.getInstance(parent.getActivity()).getUserByCode(u.getCODESENDER()).getUSERNAME()+"\n"+u.getDESCRIPTION());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent.getActivity(), android.R.layout.simple_list_item_1, result);
            rvList.setAdapter(adapter);
            rvList.invalidate();
        }
    }

    public UserInbox getUserInbox(){
        return userInbox;
    }
}
