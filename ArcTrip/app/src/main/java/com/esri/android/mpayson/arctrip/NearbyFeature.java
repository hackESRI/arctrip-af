package com.esri.android.mpayson.arctrip;

import com.esri.core.geometry.Point;

import java.util.UUID;

/**
 * Created by maxw8108 on 7/29/15.
 */
public class NearbyFeature {
    private Point mPoint;
    private String mTitle;
    private int mStars;
    private UUID mID;

    public NearbyFeature(String title, float stars, Point point){
        mPoint = point;
        mStars = Math.round(stars);
        mTitle = title;
        mID = UUID.randomUUID();

    }

    public UUID getID() {
        return mID;
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

}
