package far.com.eatit.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import far.com.eatit.Adapters.Holders.OptionHolder;
import far.com.eatit.Adapters.Models.OptionModel;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.R;

public class OptionsAdapter extends RecyclerView.Adapter<OptionHolder> {

    ArrayList<OptionModel> objects;
    Context context;
    ListableActivity listActivity;
    public OptionsAdapter(Context context,ListableActivity act, ArrayList<OptionModel> objects){
        this.context = context;
        this.listActivity = act;
        this.objects = objects;
    }
    @NonNull
    @Override
    public OptionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new OptionHolder(inflater.inflate(R.layout.model_option_icon, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OptionHolder holder, final int position) {

        holder.fillData(objects.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listActivity.onClick(objects.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }
}
