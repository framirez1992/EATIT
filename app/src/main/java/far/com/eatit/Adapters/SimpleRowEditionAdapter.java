package far.com.eatit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import far.com.eatit.Adapters.Holders.SimpleRowHolder;
import far.com.eatit.Adapters.Models.SimpleRowModel;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.R;

public class SimpleRowEditionAdapter extends RecyclerView.Adapter<SimpleRowHolder> {

    Activity activity;
    ArrayList<SimpleRowModel> objects;
    ListableActivity listableActivity;
    public SimpleRowEditionAdapter(Activity act,ListableActivity la, ArrayList<SimpleRowModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public SimpleRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new SimpleRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.simple_row_edition, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleRowHolder holder, final int position) {

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
