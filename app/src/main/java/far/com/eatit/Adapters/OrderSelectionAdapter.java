package far.com.eatit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;

import far.com.eatit.Adapters.Holders.SelectableOrderRowHolder;
import far.com.eatit.Adapters.Models.SelectableOrderRowModel;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.R;

public class OrderSelectionAdapter extends RecyclerView.Adapter<SelectableOrderRowHolder> {

    Activity activity;
    ArrayList<SelectableOrderRowModel> objects;
    ArrayList<SelectableOrderRowModel> selectedObjects;
    ListableActivity listableActivity;
    public OrderSelectionAdapter(Activity act,ListableActivity la, ArrayList<SelectableOrderRowModel> objs, ArrayList<SelectableOrderRowModel> selectedObjs){
        this.activity = act;
        this.objects = objs;
        this.selectedObjects = selectedObjs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public SelectableOrderRowHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        return new SelectableOrderRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.row_selectable_order, parent, false));
    }

    @Override
    public void onBindViewHolder( SelectableOrderRowHolder holder,  final int position) {

        objects.get(position).setChecked(isSelected(objects.get(position)));

        holder.getCbCheck().setOnCheckedChangeListener(null);
        holder.fillData(objects.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listableActivity.onClick(objects.get(position));
            }
        });

        holder.getCbCheck().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                objects.get(position).setChecked(isChecked);
                if (isChecked && findPositionInSeleted(objects.get(position)) == -1) {//SI NO ESTA AGREGALO
                    selectedObjects.add(objects.get(position));
                } else {
                    selectedObjects.remove(findPositionInSeleted(objects.get(position)));
                }

                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }


    public boolean isSelected(SelectableOrderRowModel srm){
        for(SelectableOrderRowModel selected: selectedObjects){
            if(srm.getCode().equals(selected.getCode())){
                return true;
            }
        }
        return false;
    }

    public int findPositionInSeleted(SelectableOrderRowModel srm){
        for(int i = 0; i < selectedObjects.size(); i++){
            if(selectedObjects.get(i).getCode().equals(srm.getCode())){
                return i;
            }
        }
        return  -1;
    }
}
