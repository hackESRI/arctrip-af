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
import com.esri.appframework.common.ViewAccessory;
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
import com.esri.core.tasks.na.Route;
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
    private static final SpatialReference EGS = SpatialReference.create(4326);
    TextView mTitleText;
    ArrayList<ImageView> mStars;
    MapView mMapView;
    NearbyFeature mNearbyFeature;
    UUID mID;
    GraphicsLayer mRouteLayer;
    GraphicsLayer mMarkerLayer;
    RouteTask mRouteTask;

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

        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            public void onStatusChanged(Object source, STATUS status) {
                if ((source == mMapView) && (status == STATUS.INITIALIZED)) {
                    mapLoaded();
                }
            }
        });

        mNearbyFeature = NearbyFeatureGallery.get(getContext()).getNearbyFeature(mID);

        ((TextView) v.findViewById(R.id.selected_feature_title)).setText(mNearbyFeature.getTitle());
//        mStars.add((ImageView) v.findViewById(R.id.select_star_5));
//        mStars.add((ImageView) v.findViewById(R.id.select_star_4));
//        mStars.add((ImageView) v.findViewById(R.id.select_star_3));
//        mStars.add((ImageView) v.findViewById(R.id.select_star_2));
//        mStars.add((ImageView) v.findViewById(R.id.select_star_1));

        for(int i = 0; i < mNearbyFeature.getStars(); i++){
            mStars.get(i).setAlpha((float)1.0);
        }

        return v;
    }

    private void mapLoaded(){
        mMarkerLayer= new GraphicsLayer();
        mMapView.addLayer(mMarkerLayer);
        mRouteLayer = new GraphicsLayer();
        mMapView.addLayer(mRouteLayer);

        Point startP = NearbyFeatureGallery.get(getContext()).getStartP();
        Point endP = NearbyFeatureGallery.get(getContext()).getEndP();

//        Point midP = mNearbyFeature.getPoint();
        Point midP = new Point(-1.3046173349916304E7, 4036555.457708142);

        mMapView.centerAt(midP, true);


        drawDot(ArcTripMapViewController.PointType.START,
                startP, "S");
        drawDot(ArcTripMapViewController.PointType.END,
                endP, "F");

        if(midP != null) {
            drawDot(ArcTripMapViewController.PointType.STOP, midP, "P");
            new generateAutoRoute(startP, endP, midP).execute();
        } else {
            new generateAutoRoute(startP, endP, null).execute();
        }



    }

    private class generateAutoRoute extends AsyncTask<Void, Void, RouteResult> {
        Point startP, endP, midP;

        public generateAutoRoute(Point prevP, Point currP, Point mP){
            startP = (Point) GeometryEngine.project(prevP, mMapView.getSpatialReference(), EGS);
            endP = (Point) GeometryEngine.project(currP, mMapView.getSpatialReference(), EGS);
            if(midP != null) {
                midP = (Point) GeometryEngine.project(mP, mMapView.getSpatialReference(), EGS);
            }
        }

        @Override
        protected void onPreExecute(){
            DialogUtils.showProgressDialog("Generating Routes", "We'll get you there...",
                    getDependencyContainer().getCurrentActivity());
        }

        @Override
        protected RouteResult doInBackground(Void... params){
            try {

                RouteParameters rp = mRouteTask
                        .retrieveDefaultRouteTaskParameters();
                rp.setDirectionsLengthUnit(DirectionsLengthUnit.MILES);
                rp.setImpedanceAttributeName("Time");
                rp.setOutSpatialReference(mMapView.getSpatialReference());


                NAFeaturesAsFeature rfaf = new NAFeaturesAsFeature();

                StopGraphic point1 = new StopGraphic(startP);
                StopGraphic point2 = new StopGraphic(endP);
                if(midP != null) {
                    StopGraphic point3 = new StopGraphic(midP);
                    rfaf.setFeatures(new Graphic[]{point1, point3, point2});
                } else {
                    rfaf.setFeatures(new Graphic[]{point1, point2});
                }
                rp.setStops(rfaf);

                return mRouteTask.solve(rp);


            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        //TODO GET INDEX?!
        @Override
        protected void onPostExecute(RouteResult results){
            DialogUtils.dismissProgress();
            if(results != null) {
                Polyline routeGeom = (Polyline) results.getRoutes().get(0).getRouteGraphic().getGeometry();
                mRouteLayer.addGraphic(new Graphic(routeGeom, new SimpleLineSymbol(Color.BLUE,3)));
            }
        }

    }

    public void drawDot(ArcTripMapViewController.PointType type, Point p, String dispStr){
        SimpleMarkerSymbol simpleMarker = null;

        switch (type){
            case START:
                simpleMarker = new SimpleMarkerSymbol(Color.GREEN, 20, SimpleMarkerSymbol.STYLE.CIRCLE);
                break;
            case STOP:
                simpleMarker = new SimpleMarkerSymbol(Color.BLUE, 10, SimpleMarkerSymbol.STYLE.CIRCLE);
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
