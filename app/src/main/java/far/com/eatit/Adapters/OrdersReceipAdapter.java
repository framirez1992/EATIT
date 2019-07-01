package far.com.eatit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import far.com.eatit.Adapters.Holders.OrderReceiptHolder;
import far.com.eatit.Adapters.Models.OrderReceiptModel;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.R;

public class OrdersReceipAdapter extends RecyclerView.Adapter<OrderReceiptHolder> {

    Activity activity;
    ArrayList<OrderReceiptModel> objects;
    ListableActivity listableActivity;
    public OrdersReceipAdapter(Activity act,ListableActivity la, ArrayList<OrderReceiptModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public OrderReceiptHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new OrderReceiptHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.selectable_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderReceiptHolder holder, final int position) {

        holder.fillData(objects.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listableActivity.onClick(objects.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }



}

