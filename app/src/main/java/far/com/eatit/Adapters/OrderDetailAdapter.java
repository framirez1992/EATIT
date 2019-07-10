package far.com.eatit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import far.com.eatit.Adapters.Holders.OrderDetailHolder;
import far.com.eatit.Adapters.Models.OrderDetailModel;
import far.com.eatit.R;

public class OrderDetailAdapter  extends RecyclerView.Adapter<OrderDetailHolder> {

    Activity activity;
    ArrayList<OrderDetailModel> objects;
    public OrderDetailAdapter(Activity act, ArrayList<OrderDetailModel> objs){
        this.activity = act;
        this.objects = objs;
    }
    @NonNull
    @Override
    public OrderDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new OrderDetailHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.row_order_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailHolder holder, final int position) {

        holder.fillData(objects.get(position));

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }



}

