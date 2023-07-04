package far.com.eatit.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import far.com.eatit.Controllers.ActualizationController;
import far.com.eatit.Interfases.IActualizationListener;
import far.com.eatit.Main;
import far.com.eatit.R;

public class ActualizationDialog extends DialogFragment implements IActualizationListener {


    Main mainActivity;
    ProgressBar pb;
    LinearLayout llProgress, llError;
    TextView tvProgressLabel, tvError;
    CardView btnCancel, btnRetry;
    ActualizationController actualizationController;


    public static ActualizationDialog newInstance(Main mainActivity) {
        ActualizationDialog f = new ActualizationDialog();
        f.mainActivity = mainActivity;
        f.actualizationController = new ActualizationController(mainActivity, f);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.actualization_dialog, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @Override
    public void onError(String msg) {
        tvError.setText(msg);
        showError();
    }

    @Override
    public void onProgressChange(int currentProgress, String msg) {

        pb.setProgress(currentProgress);
        tvProgressLabel.setText(msg);
        showProgress();
    }

    @Override
    public void onFinishLoad() {
        dismiss();
    }

    public void init(View view) {
        pb = view.findViewById(R.id.pb);
        llProgress = view.findViewById(R.id.llProgress);
        llError = view.findViewById(R.id.llError);
        tvProgressLabel= view.findViewById(R.id.tvProgressLabel);
        tvError = view.findViewById(R.id.tvError);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnRetry= view.findViewById(R.id.btnRetry);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress();
                actualizationController.resume();
            }
        });

        showProgress();
        actualizationController.initialLoad();
    }


    private void showError(){
        llProgress.setVisibility(View.GONE);
        llError.setVisibility(View.VISIBLE);
    }

    private void showProgress(){
        llProgress.setVisibility(View.VISIBLE);
        llError.setVisibility(View.GONE);
    }

}