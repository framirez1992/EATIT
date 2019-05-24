package far.com.eatit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import far.com.eatit.Adapters.Holders.TableFilterRowHolder;
import far.com.eatit.Adapters.Models.TableFilterRowModel;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.R;

public class TableFilterRowEditionAdapter extends RecyclerView.Adapter<TableFilterRowHolder> {

    Activity activity;
    ArrayList<TableFilterRowModel> objects;
    ListableActivity listableActivity;
    public TableFilterRowEditionAdapter(Activity act,ListableActivity la, ArrayList<TableFilterRowModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public TableFilterRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new TableFilterRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.tablefilter_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TableFilterRowHolder holder, final int position) {

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
