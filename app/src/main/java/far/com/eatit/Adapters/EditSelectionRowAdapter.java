package far.com.eatit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import far.com.eatit.Adapters.Holders.EditSelectionRowHolder;
import far.com.eatit.Adapters.Models.EditSelectionRowModel;
import far.com.eatit.R;

public class EditSelectionRowAdapter  extends RecyclerView.Adapter<EditSelectionRowHolder> {

    Activity activity;
    ArrayList<EditSelectionRowModel> objects;
    ArrayList<EditSelectionRowModel> selectedObjects;
    boolean noEditable;

    public EditSelectionRowAdapter(Activity act, ArrayList<EditSelectionRowModel> objs, ArrayList<EditSelectionRowModel> selectedObjs) {
        this.activity = act;
        this.objects = objs;
        this.selectedObjects = selectedObjs;
    }

    public EditSelectionRowAdapter(Activity act, ArrayList<EditSelectionRowModel> objs, ArrayList<EditSelectionRowModel> selectedObjs, boolean noEditable) {
        this.activity = act;
        this.objects = objs;
        this.selectedObjects = selectedObjs;
        this.noEditable = noEditable;
    }
    @NonNull
    @Override
    public EditSelectionRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new EditSelectionRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.edit_selection_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EditSelectionRowHolder holder, final int position) {

        if(isSelected(objects.get(position))){
            objects.get(position).setChecked(true);
            objects.get(position).setText(selectedObjects.get(findPositionInSeleted(objects.get(position))).getText());
        }
        holder.fillData(objects.get(position));

        CheckBox cb =  holder.getCbCheck();
        final EditText etText = holder.getEtEditable();
        //if(noEditable) {
          //  cb.setEnabled(false);
           // cb.setFocusable(false);

       // }else {
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    objects.get(position).setChecked(isChecked);
                    if (isChecked && findPositionInSeleted(objects.get(position)) == -1) {//SI NO ESTA AGREGALO
                        selectedObjects.add(objects.get(position));
                    } else {
                        selectedObjects.remove(findPositionInSeleted(objects.get(position)));
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cbCheck = v.findViewById(R.id.cbCheck);
                    cbCheck.setChecked(!cbCheck.isChecked());
                }
            });
       // }

        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //if(objects.get(position).isChecked()){
                objects.get(position).setText(s.toString());
                if(findPositionInSeleted(objects.get(position)) != -1){
                    selectedObjects.get(findPositionInSeleted(objects.get(position))).setText(s.toString());
                }
                //}
            }
        });


    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public ArrayList<EditSelectionRowModel> getObjects() {
        return objects;
    }

    public boolean isSelected(EditSelectionRowModel srm){
        for(EditSelectionRowModel selected: selectedObjects){
            if(srm.getCode().equals(selected.getCode())){
                return true;
            }
        }
        return false;
    }

    public void setObjects(ArrayList<EditSelectionRowModel> objs){
        this.objects.clear();
        this.objects.addAll(objs);
        notifyDataSetChanged();
    }
    public int findPositionInSeleted(EditSelectionRowModel srm){
        for(int i = 0; i < selectedObjects.size(); i++){
            if(selectedObjects.get(i).getCode().equals(srm.getCode())){
                return i;
            }
        }
        return  -1;
    }

    public ArrayList<EditSelectionRowModel> getSelectedObjects() {
        return selectedObjects;
    }
}