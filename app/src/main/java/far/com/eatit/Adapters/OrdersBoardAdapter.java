package far.com.eatit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.zip.Inflater;

import far.com.eatit.Adapters.Holders.OrderHolder;
import far.com.eatit.Adapters.Models.OrderModel;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.R;

public class OrdersBoardAdapter extends RecyclerView.Adapter<OrderHolder> {

    Activity activity;
    ArrayList<OrderModel> objects;
    LayoutInflater inflater;
    public int lastIndex=-1;
    ListableActivity listableActivity;

    public OrdersBoardAdapter(Activity act,ListableActivity la, ArrayList<OrderModel> obj){
        this.activity = act;
        this.listableActivity = la;
        this.objects = obj;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
    }
    @Override
    public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderHolder(inflater.inflate(R.layout.order_card, parent,false));
    }

    @Override
    public void onBindViewHolder(OrderHolder holder, final int position) {


        holder.fillData(inflater, objects.get(position));
        holder.getImgMenu().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.registerForContextMenu(v);
                v.showContextMenu();
                listableActivity.onClick(objects.get(position));
            }
        });

        lastIndex = position;
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }
    public int getLastIndex(){
        return lastIndex;
    }
    public void setLastIndex(int i){
        lastIndex = i;
    }
}
