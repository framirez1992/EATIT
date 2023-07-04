package far.com.eatit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import far.com.eatit.Adapters.Models.OptionModel;
import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.R;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.OptionHolder> {

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

    public class OptionHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView text;
        public OptionHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            text = itemView.findViewById(R.id.text);
        }

        public void fillData(OptionModel om){
            img.setImageResource(om.getImgResource());
            text.setText(om.getText());
        }
    }
}
