package far.com.eatit.Dialogs;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;

import far.com.eatit.API.models.AreaDetail;
import far.com.eatit.CloudFireStoreObjects.AreasDetail;
import far.com.eatit.Controllers.AreasController;
import far.com.eatit.Controllers.AreasDetailController;
import far.com.eatit.Generic.Objects.KV;
import far.com.eatit.R;
import far.com.eatit.Utils.Funciones;

/**
 * A simple {@link Fragment} subclass.
 */
public class AreasDetailDialogFragment extends DialogFragment implements OnFailureListener {

    private static AreaDetail tempObj;

    LinearLayout llArea;
    Spinner spnArea;
    LinearLayout llSave;
    TextInputEditText etName, etOrden;

    AreasDetailController areasDetailController;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static AreasDetailDialogFragment newInstance(AreaDetail pt) {

        tempObj = pt;

        AreasDetailDialogFragment f = new AreasDetailDialogFragment();

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
        areasDetailController = AreasDetailController.getInstance(getActivity());

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
        llArea = view.findViewById(R.id.llFamilia);
        spnArea = view.findViewById(R.id.spnFamilia);
        ((TextView)view.findViewById(R.id.tvFamilia)).setText("Area");
        llSave = view.findViewById(R.id.llSave);
        etName = view.findViewById(R.id.etName);
        etOrden = view.findViewById(R.id.etOrden);
        view.findViewById(R.id.tilOrden).setVisibility(View.VISIBLE);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                    EditProductSubType();
                }
            }
        });

        AreasController.getInstance(getActivity()).fillSpinner(spnArea, false);

        if(tempObj != null) {//EDIT
            prepareForProductSubType();
        }
    }


    public boolean validateProductSubType(){
        if(spnArea.getSelectedItem()== null){
            Snackbar.make(getView(), "Seleccione una Area", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void Save(){
        if(validateProductSubType()){
            SaveProductSubType();
        }else{
            llSave.setEnabled(true);
        }

    }

    public void SaveProductSubType(){
        try {
            String code =Funciones.generateCode();
            String name = etName.getText().toString();
            int orden = etOrden.getText().toString().trim().equals("")?9999:Integer.parseInt(etOrden.getText().toString());
            String codeProductType = ((KV)spnArea.getSelectedItem()).getKey();
            AreasDetail pst = new AreasDetail(code,codeProductType,name, orden);
            //areasDetailController.sendToFireBase(pst);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void EditProductSubType(){
        try {
            AreaDetail pst = tempObj;
            int orden = etOrden.getText().toString().trim().equals("")?9999:Integer.parseInt(etOrden.getText().toString());
            pst.setPosition(orden);
            pst.setDescription(etName.getText().toString());
            pst.setIdarea(Integer.parseInt(((KV)spnArea.getSelectedItem()).getKey()));

            //areasDetailController.sendToFireBase(pst);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void prepareForProductSubType(){
        setFamilia();
        etName.setText(tempObj.getDescription());
        etOrden.setText(tempObj.getPosition()+"");
    }
    public void setFamilia(){
        for(int i = 0; i< spnArea.getAdapter().getCount(); i++){
            if(((KV)spnArea.getAdapter().getItem(i)).getKey().equals(tempObj.getIdarea()+"")){
                spnArea.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }
}
