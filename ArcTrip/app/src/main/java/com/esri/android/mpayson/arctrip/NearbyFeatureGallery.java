package com.esri.android.mpayson.arctrip;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by maxw8108 on 7/29/15.
 */
public class NearbyFeatureGallery {
    private Context mAppContext;
    private static NearbyFeatureGallery sNearbyFeatureGallery;

    private ArrayList<NearbyFeature> mFeatures;

    private NearbyFeatureGallery(Context appContext){
        mAppContext = appContext;
        mFeatures = new ArrayList<>();
        for(int i = 0; i<10; i++){
            String title = String.format("Title: " + i);
            float star = (float) Math.random()*5;
            mFeatures.add(new NearbyFeature(title, star, null));
        }
    }

    public static NearbyFeatureGallery get(Context c){
        if(sNearbyFeatureGallery == null){
            sNearbyFeatureGallery = new NearbyFeatureGallery(c.getApplicationContext());
        }
        return sNearbyFeatureGallery;
    }

    public ArrayList<NearbyFeature> getFeatures() {
        return mFeatures;
    }

    public void addNearbyFeature(NearbyFeature nearbyFeature){
        mFeatures.add(nearbyFeature);
    }
}
