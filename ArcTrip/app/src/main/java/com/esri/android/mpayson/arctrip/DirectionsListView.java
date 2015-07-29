package com.esri.android.mpayson.arctrip;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esri.android.mpayson.arctrip.R;

/**
 * Created by maxw8108 on 6/24/15.
 */
public class DirectionsListView extends LinearLayout {
    private TextView mDirectionText;
    private TextView mDistanceText;


    //region CONSTRUCTORS
    public DirectionsListView(Context context) {
        super(context);
    }

    public DirectionsListView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public DirectionsListView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DirectionsListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    //endregion

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();

        mDirectionText = (TextView) findViewById(R.id.direction_list_item_text);
        mDistanceText = (TextView) findViewById(R.id.direction_list_item_distance);
    }

    public void setDirectionText(String directionText){
        mDirectionText.setText(directionText);
    }
    public void setDistanceText(String distanceText){
        mDistanceText.setText(distanceText);
    }
}
