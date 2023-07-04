package far.com.eatit.UserMenu.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import far.com.eatit.Interfases.ListableActivity;
import far.com.eatit.R;
import far.com.eatit.UserMenu.Model.ItemModel;

public class SimpleItemAdapter extends RecyclerView.Adapter<SimpleItemAdapter.ItemHolder> {

    ArrayList<ItemModel> objects;
    Context context;
    ListableActivity listActivity;
    public SimpleItemAdapter(Context context,ListableActivity act, ArrayList<ItemModel> objects){
        this.context = context;
        this.listActivity = act;
        this.objects = objects;
    }
    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new ItemHolder(inflater.inflate(R.layout.simple_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, final int position) {

        holder.fillData(objects.get(position));
        if(!objects.get(position).isHeader()){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listActivity.onClick(objects.get(position));
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }



    public class ItemHolder extends RecyclerView.ViewHolder {
        TextView text;
        CardView cvParent;
        public ItemHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.tvText);
            cvParent = itemView.findViewById(R.id.cvParent);
        }

        public void fillData(ItemModel im){
            text.setText(im.getTitle());
            cvParent.setCardBackgroundColor(Color.parseColor(im.getHexBackground()));
            if(im.isHeader()){
                text.setTextSize(20);
                text.setTextColor(Color.WHITE);
                text.setTypeface(text.getTypeface(), Typeface.BOLD);
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) cvParent.getLayoutParams();
                layoutParams.setMargins(0, 10,0, 0);
                cvParent.requestLayout();
            }else{
                text.setTextSize(16);
                text.setTextColor(Color.BLACK);
                text.setTypeface(text.getTypeface(), Typeface.NORMAL);
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) cvParent.getLayoutParams();
                layoutParams.setMargins(0, 0,0, 0);
                cvParent.requestLayout();
            }

        }
    }
}
