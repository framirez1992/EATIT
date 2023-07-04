package far.com.eatit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import far.com.eatit.Adapters.Holders.SimplePercentRowHolder;
import far.com.eatit.Adapters.Models.PercentRowModel;
import far.com.eatit.Generic.Objects.KV2;
import far.com.eatit.R;

public class SimplePercentRowAdapter extends RecyclerView.Adapter<SimplePercentRowHolder> {

    Activity activity;
    ArrayList<PercentRowModel> objects;
    public SimplePercentRowAdapter(Activity act, ArrayList<PercentRowModel> objs){
        this.activity = act;
        this.objects = objs;
    }
    @NonNull
    @Override
    public SimplePercentRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new SimplePercentRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.reports_simple_percent_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SimplePercentRowHolder holder, final int position) {

        holder.fillData(objects.get(position));

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }
}
