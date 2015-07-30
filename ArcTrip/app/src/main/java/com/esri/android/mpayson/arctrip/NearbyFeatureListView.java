package com.esri.android.mpayson.arctrip;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by maxw8108 on 7/29/15.
 */
public class NearbyFeatureListView extends android.support.v7.widget.CardView{
    private TextView mTitleText;
    ArrayList<ImageView> mStars;

    public NearbyFeatureListView(Context context) {
        super(context);
    }

    public NearbyFeatureListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NearbyFeatureListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();

        mStars = new ArrayList<ImageView>();

        mTitleText=(TextView) findViewById(R.id.nearby_feature_title);
        mStars.add((ImageView) findViewById(R.id.star_5));
        mStars.add((ImageView) findViewById(R.id.star_4));
        mStars.add((ImageView) findViewById(R.id.star_3));
        mStars.add((ImageView) findViewById(R.id.star_2));
        mStars.add((ImageView) findViewById(R.id.star_1));
    }

    public void setTitleText(String text){
        mTitleText.setText(text);
    }

    public void setStars(int stars){
        for(int i = 0; i < stars; i++){
            mStars.get(i).setAlpha((float)1.0);
        }
    }
}
