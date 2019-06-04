package far.com.eatit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import far.com.eatit.Adapters.Holders.UserControlRowHolder;
import far.com.eatit.Adapters.Models.UserControlRowModel;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.R;

public class UserControlRowEditionAdapter extends RecyclerView.Adapter<UserControlRowHolder> {

    Activity activity;
    ArrayList<UserControlRowModel> objects;
    ListableActivity listableActivity;
    public UserControlRowEditionAdapter(Activity act,ListableActivity la, ArrayList<UserControlRowModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public UserControlRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new UserControlRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.usercontrol_row_edition, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserControlRowHolder holder, final int position) {

        holder.fillData(objects.get(position));
        holder.getMenuImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.registerForContextMenu(v);
                v.showContextMenu();
                listableActivity.onClick(objects.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }
}
