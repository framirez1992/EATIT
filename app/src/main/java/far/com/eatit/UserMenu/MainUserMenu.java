package far.com.eatit.UserMenu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.R;
import far.com.eatit.UserMenu.Model.ItemModel;

public class MainUserMenu extends AppCompatActivity implements ListableActivity {

    DetailFragment detailFragment;
    ListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_user_menu);

        detailFragment = new DetailFragment();
        detailFragment.setParent(this);

        listFragment = new ListFragment();
        listFragment.setParentActivity(this);

        changeFragment(detailFragment,R.id.details);
        changeFragment(listFragment, R.id.menu);
    }

    public void changeFragment(Fragment f, int id){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.commit();
    }

    @Override
    public void onClick(Object obj) {
        detailFragment.setItemData((ItemModel)obj);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(detailFragment).attach(detailFragment).commit();
        //detailFragment.setItemData((ItemModel)obj);
    }
}
