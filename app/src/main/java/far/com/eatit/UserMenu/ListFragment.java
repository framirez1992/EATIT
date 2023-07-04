package far.com.eatit.UserMenu;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.R;
import far.com.eatit.UserMenu.Adapters.SimpleItemAdapter;
import far.com.eatit.UserMenu.Model.ItemModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {


    RecyclerView rvList;
    MainUserMenu parentActivity;
    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvList = view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(parentActivity));

        fillData();
    }

    public void setParentActivity(MainUserMenu mainUserMenu){
        this.parentActivity = mainUserMenu;
    }

    public void fillData(){
        ArrayList<ItemModel> list  = new ArrayList<>();
        ArrayList<String> urls = new ArrayList<>();
        list.add(ItemModel.initHeader("Entradas","#0288D1"));

        urls.add("http://2.bp.blogspot.com/_xZi4NccTdcI/SYSBkmdhOTI/AAAAAAAAACI/9uwMp210vxc/s400/el_seto.jpg");
        urls.add("http://3.bp.blogspot.com/-tEN6vGr9twM/UIej8MiFhhI/AAAAAAAADds/Jt35HP-6bKU/s1600/BOLSITAS+DE+PUERRO.JPG");
        list.add(ItemModel.initDetail("Bolsitas de queso con puerro y gambas", urls));


        urls = new ArrayList<>();
        urls.add("https://www.enriquetomas.com/blog/wp-content/uploads/2017/10/como-presentar-un-buen-plato-de-jamon.jpg");
        urls.add("https://jamonesesenciapura.com/wp-content/uploads/2018/10/jamon6.jpg");
        list.add(ItemModel.initDetail("Jamon iberico de bellota", urls));

        urls = new ArrayList<>();
        urls.add("https://s3.envato.com/files/236502494/2171022-013.jpg");
        urls.add("https://i.pinimg.com/originals/20/b4/e3/20b4e394037e6018128f1b5a145bdc32.jpg");
        list.add(ItemModel.initDetail("Anillos de calamar empanado", urls));

        list.add(ItemModel.initHeader("Guarniciones","#00695C"));

        urls = new ArrayList<>();
        urls.add("http://recetario.lavillita.com.mx/assets/images/recetas/Pastel_de_papas_gratinadas_con_jam%C3%B3n_800X559_132171638.jpg");
        urls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQgJkf0ncCXSV-rUy8yRjoNExbWNhv5MuaSUKUzroaeTuMzZWkP");
        list.add(ItemModel.initDetail("Papas a la crema", urls));

        urls = new ArrayList<>();
        urls.add("https://www.cook2eatwell.com/wp-content/uploads/2018/07/Tostones-9.jpg");
        urls.add("https://www.dominicancooking.com/wp-content/uploads/tostones-recipe-CG16736-800x1120.jpg");
        list.add(ItemModel.initDetail("Tostones", urls));

        urls = new ArrayList<>();
        urls.add("https://www.recetasderechupete.com/wp-content/uploads/2019/08/Arroz-blanco-525x360.jpg");
        urls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT51LsY-zp3IHCcAt9kvCRcdPKKqzl8miOTIPwVB9zZ-LfatLDXWg");
        list.add(ItemModel.initDetail("Arroz", urls));


        list.add(ItemModel.initHeader("Carnes","#C62828"));

        urls = new ArrayList<>();
        urls.add("https://okdiario.com/img/recetas/2016/05/26/lomo-cerdo-horno-01-1024x576.jpg");
        urls.add("https://pespdotcom.files.wordpress.com/2019/05/lomo-de-cerdo-al-horno.jpg?w=2000");
        list.add(ItemModel.initDetail("Lomillo de cerdo", urls));

        urls = new ArrayList<>();
        urls.add("https://pespdotcom.files.wordpress.com/2019/07/bistec-de-res-fc3a1cil-y-rc3a1pido.jpg?crop=0px%2C0px%2C4800px%2C2520px&resize=1200%2C630");
        urls.add("https://img.recetascomidas.com/recetas/640_480/bistec-con-patatas.jpg");
        list.add(ItemModel.initDetail("Bistec de res", urls));

        urls = new ArrayList<>();
        urls.add("https://okdiario.com/img/2018/07/17/receta-de-chorizo-casero-1-620x349.jpg");
        urls.add("https://www.raeucherwiki.de/wp-content/uploads/2019/03/chorizo-grill-bratpfanne-rezept-raeucherwiki.jpg");
        list.add(ItemModel.initDetail("Chorizo", urls));


        SimpleItemAdapter adapter = new SimpleItemAdapter(parentActivity,parentActivity,list);
        rvList.setAdapter(adapter);
        rvList.invalidate();

    }
}
