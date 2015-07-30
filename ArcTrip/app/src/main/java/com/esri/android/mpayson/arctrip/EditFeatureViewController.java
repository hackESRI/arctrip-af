package com.esri.android.mpayson.arctrip;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;

import com.esri.appframework.viewcontrollers.BaseViewController;

/**
 * Created by maxw8108 on 7/30/15.
 */
public class EditFeatureViewController extends BaseViewController {
    Utils.STOP mSTOP;

    public EditFeatureViewController(Utils.STOP stopType){
        mSTOP = stopType;
    }

    @Override
    public View createView(ViewGroup viewGroup, Bundle bundle) {
        View v = getDependencyContainer().getLayoutInflater()
                .inflate(R.layout.edit_feature_layout, viewGroup, false);

        if (mSTOP == Utils.STOP.PREV){
            v.findViewById(R.id.edit_feature_edit_text).setVisibility(View.GONE);
            //TODO SET TEXT
        }

        v.findViewById(R.id.edit_feature_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float stars = ((RatingBar)v.findViewById(R.id.edit_feature_stars)).getRating();
                if (mSTOP != Utils.STOP.PREV){
                    String title = ((EditText)v.findViewById(R.id.edit_feature_edit_text)).getText().toString();
                    //TODO CREATE NEW FEATURE
                }
                else {
                    //TODO APPEND FEATURE INFORMATION
                }
            }
        }
        );

        return v;
    }
}
