package far.com.eatit.UserMenu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.io.InputStream;

import far.com.eatit.MainOrders;
import far.com.eatit.R;
import far.com.eatit.UserMenu.Model.ItemModel;

public class DetailFragment extends Fragment {


    MainUserMenu parentActivity;
    TextView tvTitle;
    CarouselView carouselView;
    String[] sampleImages = {"https://i.ndtvimg.com/i/2017-09/mango-620x350_620x350_71505731672.jpg",
           "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT_2BOMlsiQ-5K3dA3dSEvJNSWbfVBOZfj7NZAaBF2O7yqx0CQ8QQ",
            "https://i2.wp.com/media.globalnews.ca/videostatic/443/23/DIRTY_DOZEN_LIST_2019-5c924556dd173300c125ac1f_1_Mar_20_2019_19_51_30_poster.jpg?w=372&quality=70&strip=all"};
   ItemModel itemModel;

    public DetailFragment() {
        // Required empty public constructor
    }

    public void setParent(MainUserMenu parent){
        this.parentActivity = parent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.collapting_layout, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        carouselView = view.findViewById(R.id.carouselView);
        tvTitle = view.findViewById(R.id.tvTitle);

        if(itemModel != null){

            tvTitle.setText(itemModel.getTitle());
            try {
                carouselView.setImageListener(new ImageListener() {
                    @Override
                    public void setImageForPosition(int position, ImageView imageView) {
                        // imageView.setImageResource(sampleImages[position]);
                        Picasso.with(parentActivity).load(itemModel.getUrls().get(position)).into(imageView);

                    }
                });
                carouselView.setPageCount(itemModel.getUrls().size());
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        /**
         *  carouselView.setPageCount(sampleImages.length);
         *
         *             carouselView.setImageListener(new ImageListener() {
         *                 @Override
         *                 public void setImageForPosition(int position, ImageView imageView) {
         *                     // imageView.setImageResource(sampleImages[position]);
         *                     Picasso.with(parentActivity).load(sampleImages[position]).into(imageView);
         *
         *                 }
         *             });
         */
        //((CollapsingToolbarLayout)view.findViewById(R.id.collaptingToolBarLayout)).setTitle("Test");


       // Picasso.with(parentActivity).load("https://i.ndtvimg.com/i/2017-09/mango-620x350_620x350_71505731672.jpg").into((ImageView) view.findViewById(R.id.img));

    }

    public void setItemData(ItemModel item){

            this.itemModel = item;
    }



}
