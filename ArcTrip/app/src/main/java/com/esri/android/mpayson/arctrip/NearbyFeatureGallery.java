package com.esri.android.mpayson.arctrip;

import android.content.Context;

import com.esri.core.geometry.Point;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by maxw8108 on 7/29/15.
 */
public class NearbyFeatureGallery {
    private Context mAppContext;
    private static NearbyFeatureGallery sNearbyFeatureGallery;
    private Point mStartP;
    private Point mEndP;

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

    public NearbyFeature getNearbyFeature(UUID id){
        for(NearbyFeature f : mFeatures){
            if(id == f.getID()){
                return f;
            }
        }
        return null;
    }

    public Point getStartP(){
        return mStartP;
    }
    public Point getEndP(){
        return mEndP;
    }

    public void setStartP(Point p){
        mStartP = p;
    }
    public void setEndP(Point p){
        mEndP = p;
    }
}
