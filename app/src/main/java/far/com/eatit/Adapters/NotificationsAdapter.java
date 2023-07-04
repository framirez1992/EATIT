package far.com.eatit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import far.com.eatit.Adapters.Holders.NotificationRowHolder;
import far.com.eatit.Adapters.Models.NotificationRowModel;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.R;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationRowHolder> {

    Activity activity;
    ArrayList<NotificationRowModel> objects;
    ListableActivity listableActivity;

    public NotificationsAdapter(Activity act, ListableActivity la, ArrayList<NotificationRowModel> objs) {
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }

    @NonNull
    @Override
    public NotificationRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new NotificationRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.notification_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationRowHolder holder, final int position) {

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