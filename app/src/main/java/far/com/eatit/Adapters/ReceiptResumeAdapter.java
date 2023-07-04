package far.com.eatit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import far.com.eatit.Adapters.Holders.ReceiptResumeHolder;
import far.com.eatit.Adapters.Models.ReceiptResumeModel;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.R;

public class ReceiptResumeAdapter extends RecyclerView.Adapter<ReceiptResumeHolder>{

    Activity activity;
    ArrayList<ReceiptResumeModel> objects;
    ListableActivity listableActivity;
    public ReceiptResumeAdapter(Activity act,ListableActivity la, ArrayList<ReceiptResumeModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public ReceiptResumeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ReceiptResumeHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.row_receipt_resume, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptResumeHolder holder, final int position) {

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
