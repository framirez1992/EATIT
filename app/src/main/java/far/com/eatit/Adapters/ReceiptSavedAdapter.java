package far.com.eatit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import far.com.eatit.Adapters.Holders.ReceiptSavedHolder;
import far.com.eatit.Adapters.Models.ReceiptSavedModel;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.R;

public class ReceiptSavedAdapter extends RecyclerView.Adapter<ReceiptSavedHolder>{

    Activity activity;
    ArrayList<ReceiptSavedModel> objects;
    ListableActivity listableActivity;
    public ReceiptSavedAdapter(Activity act, ListableActivity la, ArrayList<ReceiptSavedModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public ReceiptSavedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ReceiptSavedHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.row_receipt_saved, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptSavedHolder holder, final int position) {

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
