package far.com.eatit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import far.com.eatit.Adapters.Holders.UserRowHolder;
import far.com.eatit.Adapters.Models.UserRowModel;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.R;

public class UserRowEditionAdapter extends RecyclerView.Adapter<UserRowHolder> {

    Activity activity;
    ArrayList<UserRowModel> objects;
    ListableActivity listableActivity;
    public UserRowEditionAdapter(Activity act,ListableActivity la, ArrayList<UserRowModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public UserRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new UserRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.user_row_edition, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserRowHolder holder, final int position) {

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
