package far.com.eatit.Dialogs;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;

import java.util.UUID;

import far.com.eatit.CloudFireStoreObjects.TableCode;
import far.com.eatit.Controllers.TableCodeController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;

/**
 * A simple {@link Fragment} subclass.
 */
public class TableCodesDialogFragment extends DialogFragment implements OnFailureListener {

    private static TableCode tempObj;

    LinearLayout llTabla;
    Spinner spnTabla;
    LinearLayout llSave;
    TextInputEditText etName;

    TableCodeController tableCodeController;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static TableCodesDialogFragment newInstance(TableCode pt) {

        tempObj = pt;

        TableCodesDialogFragment f = new TableCodesDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(pt != null) {
            f.setArguments(args);
        }

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);
        tableCodeController = TableCodeController.getInstance(getActivity());

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.dialog_spn_save, container, true);
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


    public void init(View view){
        llTabla = view.findViewById(R.id.llFamilia);
        spnTabla = view.findViewById(R.id.spnFamilia);
        llSave = view.findViewById(R.id.llSave);
        etName = view.findViewById(R.id.etName);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                    EditTableCode();
                }
            }
        });

        TableCodeController.getInstance(getActivity()).fillSpinner(spnTabla, false);

        if(tempObj != null) {//EDIT
            prepareForTableCode();
        }
    }


    public boolean validateTableCode(){
        if(spnTabla.getSelectedItem()== null){
            Snackbar.make(getView(), "Seleccione una Tabla", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void Save(){
        if(validateTableCode()){
            SaveTableCode();
        }else{
            llSave.setEnabled(true);
        }

    }

    public void SaveTableCode(){
        try {
            String code = UUID.randomUUID().toString();
            String codeType = ((KV)spnTabla.getSelectedItem()).getKey();
            String codeControl = code;
            String name = etName.getText().toString();
            
            TableCode pst = new TableCode(code,codeType,codeControl,name);
            tableCodeController.sendToFireBase(pst);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void EditTableCode(){
        try {
            TableCode tc = tempObj;
            tc.setDESCRIPTION(etName.getText().toString());
            tc.setCODETYPE(((KV)spnTabla.getSelectedItem()).getKey());
            tc.setMDATE(null);
            tableCodeController.sendToFireBase(tc);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void prepareForTableCode(){
        setFamilia();
        etName.setText(tempObj.getDESCRIPTION());
    }
    public void setFamilia(){
        for(int i = 0; i< spnTabla.getAdapter().getCount(); i++){
            if(((KV)spnTabla.getAdapter().getItem(i)).getKey().equals(tempObj.getCODETYPE())){
                spnTabla.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }
}
