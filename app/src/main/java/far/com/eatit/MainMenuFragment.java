package far.com.eatit;

import android.graphics.Path;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import far.com.eatit.API.models.LoginResponse;
import far.com.eatit.Adapters.Models.OptionModel;
import far.com.eatit.Adapters.OptionsAdapter;
import far.com.eatit.Globales.Constants;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.Utils.Funciones;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainMenuFragment extends Fragment implements ListableActivity {

    Main mainActivity;
    RecyclerView rv;

    public MainMenuFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static MainMenuFragment newInstance(Main mainActivity) {
        MainMenuFragment fragment = new MainMenuFragment();
        fragment.mainActivity = mainActivity;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv = view.findViewById(R.id.rv);
        GridLayoutManager manager = new GridLayoutManager(mainActivity,2);
        rv.setLayoutManager(manager);

        fillData();
    }

    @Override
    public void onClick(Object obj) {
        OptionModel optionModel = (OptionModel)obj;
        if(optionModel.getObject() == Constants.MODULES.MAINTENANCE){
            mainActivity.setMaintenenceFragment();
        }
    }

    private void fillData(){
        OptionsAdapter adapter = new OptionsAdapter(mainActivity,MainMenuFragment.this,getModules());
        rv.setAdapter(adapter);

    }

    private ArrayList<OptionModel> getModules(){
        ArrayList<OptionModel> data = new ArrayList<>();
        LoginResponse lr = Funciones.getLoginResponseData(mainActivity);
        for(String m :  lr.getModules()){
            if(m.toUpperCase().equals(Constants.MODULES.MAINTENANCE.name())){
                data.add(new OptionModel(Constants.MODULES.MAINTENANCE,"Maintenance",R.drawable.ic_menu_manage));
            }
        }
        return data;
    }


}