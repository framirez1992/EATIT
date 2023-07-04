package far.com.eatit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import far.com.eatit.Adapters.Holders.ProductRowHolder;
import far.com.eatit.Adapters.Models.ProductRowModel;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.R;

public class ProductRowEditionAdapter extends RecyclerView.Adapter<ProductRowHolder> {

    Activity activity;
    ArrayList<ProductRowModel> objects;
    ListableActivity listableActivity;
    public ProductRowEditionAdapter(Activity act,ListableActivity la, ArrayList<ProductRowModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public ProductRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ProductRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.product_row_edition, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductRowHolder holder, final int position) {

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

