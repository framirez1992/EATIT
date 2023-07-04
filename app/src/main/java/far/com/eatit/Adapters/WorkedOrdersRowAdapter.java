package far.com.eatit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import far.com.eatit.Adapters.Holders.WorkedOrdersRowHolder;
import far.com.eatit.Adapters.Models.WorkedOrdersRowModel;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.R;

public class WorkedOrdersRowAdapter extends RecyclerView.Adapter<WorkedOrdersRowHolder> {

    Activity activity;
    ArrayList<WorkedOrdersRowModel> objects;
    ListableActivity listableActivity;
    public WorkedOrdersRowAdapter(Activity act,ListableActivity la, ArrayList<WorkedOrdersRowModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public WorkedOrdersRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new WorkedOrdersRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.row_worked_orders, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkedOrdersRowHolder holder, final int position) {

        holder.fillData(objects.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listableActivity.onClick(objects.get(position));
            }
        });
        /*holder.getMenuImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.registerForContextMenu(v);
                v.showContextMenu();
                listableActivity.onClick(objects.get(position));
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }
}
