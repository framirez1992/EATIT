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
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;

import java.util.UUID;

import far.com.eatit.CloudFireStoreObjects.Areas;
import far.com.eatit.Controllers.AreasController;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;

/**
 * A simple {@link Fragment} subclass.
 */
public class AreasDialogFragment  extends DialogFragment implements OnFailureListener {

    private static  Object tempObj;

    LinearLayout llSave;
    TextInputEditText etName, etOrden;

    AreasController areasController;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static AreasDialogFragment newInstance(Object pt) {

        tempObj = pt;
        AreasDialogFragment f = new AreasDialogFragment();

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
        areasController = AreasController.getInstance(getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.product_type_fragment_dialog, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etName);
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
        llSave = view.findViewById(R.id.llSave);
        etName = view.findViewById(R.id.etName);
        etOrden = view.findViewById(R.id.etOrden);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                    EditProductType();
                }
            }
        });

        if(tempObj != null){//EDIT
            setUpToEditProductType();
        }
    }

    public boolean validateProductType(){
        if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void Save(){
        if(validateProductType()) {
            SaveProductType();
        }

    }

    public void SaveProductType(){
        try {
            String code = UUID.randomUUID().toString();
            String name = etName.getText().toString();
            String valueOrden = etOrden.getText().toString().trim().equals("")?areasController.getNextOrden()+"":etOrden.getText().toString().trim();
            int orden = Integer.parseInt(valueOrden);
            Areas pt = new Areas(code, name, orden);
            areasController.sendToFireBase(pt);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void EditProductType(){
        try {
            Areas pt = ((Areas)tempObj);
            pt.setDESCRIPTION(etName.getText().toString());
            String valueOrden = etOrden.getText().toString().trim().equals("")?areasController.getNextOrden()+"":etOrden.getText().toString().trim();
            pt.setORDEN(Integer.parseInt(valueOrden));
            pt.setMDATE(null);
            areasController.sendToFireBase(pt);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }



    public void setUpToEditProductType(){
        etName.setText(((Areas)tempObj).getDESCRIPTION());
        etOrden.setText(((Areas)tempObj).getORDEN()+"");

    }



    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }
}
