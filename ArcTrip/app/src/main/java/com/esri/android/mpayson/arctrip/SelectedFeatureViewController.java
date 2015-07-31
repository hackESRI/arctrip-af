package com.esri.android.mpayson.arctrip;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.appframework.viewcontrollers.BaseViewController;

import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;
import com.esri.core.tasks.na.DirectionsLengthUnit;
import com.esri.core.tasks.na.NAFeaturesAsFeature;
import com.esri.core.tasks.na.RouteParameters;
import com.esri.core.tasks.na.RouteResult;
import com.esri.core.tasks.na.RouteTask;
import com.esri.core.tasks.na.StopGraphic;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by maxw8108 on 7/29/15.
 */
public class SelectedFeatureViewController extends BaseViewController {
    final static String TAG = "SelectedFeatureVC";
    private static final SpatialReference EGS = SpatialReference.create(4326);
    TextView mTitleText;
    ArrayList<ImageView> mStars;
    MapView mMapView;
    NearbyFeature mNearbyFeature;
    UUID mID;
    GraphicsLayer mRouteLayer;
    GraphicsLayer mMarkerLayer;
    RouteTask mRouteTask;
    Point mMidP;

    public SelectedFeatureViewController(UUID id){
        mID = id;
    }

    @Override
    public View createView(ViewGroup viewGroup, Bundle bundle) {
        View v = getDependencyContainer().getLayoutInflater()
                .inflate(R.layout.selected_feature_layout, viewGroup, false);

        String routeTaskURL = "http://sampleserver3.arcgisonline.com/ArcGIS/rest/services/Network/USA/NAServer/Route";
//        String routeTaskURL = "http://route.arcgis.com/arcgis/rest/services/World/Route/NAServer/Route_World";
        try {
            mRouteTask = RouteTask.createOnlineRouteTask(routeTaskURL, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView = (MapView) v.findViewById(R.id.map);

        //        mMidP = mNearbyFeature.getPoint();
        mMidP = new Point(-1.3046173349916304E7, 4036555.457708142);

        if(mMidP == null){
            mMapView.setVisibility(View.GONE);
            v.findViewById(R.id.select_notfound_textview).setVisibility(View.VISIBLE);
        }

        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            public void onStatusChanged(Object source, STATUS status) {
                if ((source == mMapView) && (status == STATUS.INITIALIZED)) {
                    mapLoaded();
                }
            }
        });

        mNearbyFeature = NearbyFeatureGallery.get(getContext()).getNearbyFeature(mID);

        ((TextView) v.findViewById(R.id.selected_feature_title)).setText(mNearbyFeature.getTitle());
        mStars = new ArrayList<ImageView>();
        mStars.add((ImageView) v.findViewById(R.id.select_star_5));
        mStars.add((ImageView) v.findViewById(R.id.select_star_4));
        mStars.add((ImageView) v.findViewById(R.id.select_star_3));
        mStars.add((ImageView) v.findViewById(R.id.select_star_2));
        mStars.add((ImageView) v.findViewById(R.id.select_star_1));

        for(int i = 0; i < mNearbyFeature.getStars(); i++){
            mStars.get(i).setAlpha((float)1.0);
        }

        return v;
    }

    private void mapLoaded(){
        if(mMidP != null) {
            mMarkerLayer = new GraphicsLayer();
            mMapView.addLayer(mMarkerLayer);
            mRouteLayer = new GraphicsLayer();
            mMapView.addLayer(mRouteLayer);

            Point startP = NearbyFeatureGallery.get(getContext()).getStartP();
            Point endP = NearbyFeatureGallery.get(getContext()).getEndP();

            drawDot(ArcTripMapViewController.PointType.START,
                    startP, "S");
            drawDot(ArcTripMapViewController.PointType.END,
                    endP, "F");
            drawDot(ArcTripMapViewController.PointType.STOP, mMidP, "P");
            mMapView.centerAt(mMidP, true);



        }



    }


    public void drawDot(ArcTripMapViewController.PointType type, Point p, String dispStr){

        SimpleMarkerSymbol simpleMarker = null;

        switch (type){
            case START:
                simpleMarker = new SimpleMarkerSymbol(Color.GREEN, 20, SimpleMarkerSymbol.STYLE.CIRCLE);
                break;
            case STOP:
                simpleMarker = new SimpleMarkerSymbol(Color.BLUE, 20, SimpleMarkerSymbol.STYLE.CIRCLE);
                break;
            case END:
                simpleMarker = new SimpleMarkerSymbol(Color.RED, 20, SimpleMarkerSymbol.STYLE.CIRCLE);
                break;
            case MILE:
                simpleMarker = new SimpleMarkerSymbol(Color.RED, 15, SimpleMarkerSymbol.STYLE.CIRCLE);
        }
        Graphic pointGraphic = new Graphic(p, simpleMarker);

        TextSymbol textSymbol = new TextSymbol(18, dispStr, Color.BLACK);
        textSymbol.setHorizontalAlignment(TextSymbol.HorizontalAlignment.CENTER);
        textSymbol.setVerticalAlignment(TextSymbol.VerticalAlignment.MIDDLE);
        Graphic textGraphic = new Graphic(p, textSymbol);

        mMarkerLayer.addGraphic(pointGraphic);
        mMarkerLayer.addGraphic(textGraphic);
    }
}
