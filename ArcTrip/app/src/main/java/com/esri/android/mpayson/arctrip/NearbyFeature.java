package com.esri.android.mpayson.arctrip;

import android.graphics.Point;

/**
 * Created by maxw8108 on 7/29/15.
 */
public class NearbyFeature {

    public NearbyFeature(String title, float stars, Point point){
        mPoint = point;
        mStars = Math.round(stars);
        mTitle = title;
    }

    public Point getPoint() {
        return mPoint;
    }

    public void setPoint(Point point) {
        mPoint = point;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getStars() {
        return mStars;
    }

    public void setStars(float stars) {
        mStars = Math.round(stars);
    }

    Point mPoint;
    String mTitle;
    int mStars;
}
