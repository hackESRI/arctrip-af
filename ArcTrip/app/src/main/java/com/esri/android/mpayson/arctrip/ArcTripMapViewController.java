package com.esri.android.mpayson.arctrip;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.esri.android.map.GraphicsLayer;
import com.esri.appframework.common.ViewAccessory;
import com.esri.appframework.viewcontrollers.BaseViewController;
import com.esri.appframework.viewcontrollers.map.MapViewController;
import com.esri.appframework.viewcontrollers.map.TouchHandler;
import com.esri.appframework.viewcontrollers.map.tools.GPSMapTool;
import com.esri.appframework.viewcontrollers.map.tools.search.SearchMapTool;
import com.esri.appframework.wrappers.AGSMap;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.SimpleRenderer;
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
import com.esri.android.mpayson.arctrip.R;
import com.squareup.otto.Subscribe;

/**
 * Created by maxw8108 on 7/27/15.
 */
public class ArcTripMapViewController extends MapViewController
        implements MapViewController.Listener{

    public enum PointType{START, STOP, END, MILE}

    private static final SpatialReference EGS = SpatialReference.create(4326);

    private Point mStartP;
    private Point mEndP;

    SearchMapTool mSearchMapTool;

    private GraphicsLayer mMarkerLayer;
    private GraphicsLayer mRouteLayer;

    private AsyncTask mRouteAsyncTask;
    private RouteTask mRouteTask;
    private RouteResult mRouteResults;
    private RouteResultViewController mRouteResultViewController;

    public static final String TAG = "ArcTripMapVC";

    public ArcTripMapViewController(AGSMap map) {
        super(map);
    }

    @Override
    public View createView(ViewGroup parentView, Bundle savedState){
        View v = super.createView(parentView, savedState);

        setListener(this);

        return v;
    }

    @Subscribe
    @Override
    public void onMapInitialization(AGSMap.AGSMapInitializationEvent event) {
        // Even though we only call the base class method here, we still need to implement this, otherwise, the event bus
        // doesn't appear to call the base class method (perhaps because we're a subclass?).
        //

        String routeTaskURL = "http://sampleserver3.arcgisonline.com/ArcGIS/rest/services/Network/USA/NAServer/Route";
//        String routeTaskURL = "http://route.arcgis.com/arcgis/rest/services/World/Route/NAServer/Route_World";
        try {
            mRouteTask = RouteTask.createOnlineRouteTask(routeTaskURL, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "onMapInitialization called");
        super.onMapInitialization(event);
    }

    @Override
    public void onMapViewLoaded() {
        // Create/add a graphics layer to display search results in
        //

        mMarkerLayer = new GraphicsLayer();
        addMapLayer(mMarkerLayer);
        mRouteLayer = new GraphicsLayer();
        addMapLayer(mRouteLayer);

        GraphicsLayer graphicsLayer = new GraphicsLayer();
        addMapLayer(graphicsLayer);

        // Define which tools we want to include in the map view controller
        //
        addMapTool(new GPSMapTool());

        mSearchMapTool = new SearchMapTool(getDependencyContainer(), this, graphicsLayer);
//        searchMapTool.setResultSummaryViewAccessory(createRouteToResultViewAccessory());
        addMapTool(mSearchMapTool);

        getMapViewControllerTouchListener().addOnTapListener(new TouchHandler() {
            @Override
            public boolean canHandlePoint(Point screenPoint, Point mapPoint) {

                return true;
            }

            @Override
            public void handlePoint(Point screenPoint, Point mapPoint) {
                closeBottomPanel();
                getMap().getMapView().centerAt(mapPoint, true);
                if(mStartP == null) {
                    drawDot(PointType.START, mapPoint, "S");
                    mStartP = mapPoint;
                } else if (mEndP == null) {
                    drawDot(PointType.END, mapPoint, "S");
                    mEndP = mapPoint;
                    mRouteAsyncTask = new generateAutoRoute(mStartP, mEndP).execute();
                }
            }

            @Override
            public void tearDownOnRemove() {

            }
        });

    }

    private class generateAutoRoute extends AsyncTask<Void, Void, RouteResult> {
        Point startP, endP;

        public generateAutoRoute(Point prevP, Point currP){
            startP = (Point) GeometryEngine.project(prevP, getMapSpatialReference(), EGS);
            endP = (Point) GeometryEngine.project(currP, getMapSpatialReference(), EGS);
        }

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected RouteResult doInBackground(Void... params){
            try {

                RouteParameters rp = mRouteTask
                        .retrieveDefaultRouteTaskParameters();
                rp.setDirectionsLengthUnit(DirectionsLengthUnit.MILES);
                rp.setImpedanceAttributeName("Time");
                rp.setOutSpatialReference(getMapSpatialReference());


                NAFeaturesAsFeature rfaf = new NAFeaturesAsFeature();

                StopGraphic point1 = new StopGraphic(startP);
                StopGraphic point2 = new StopGraphic(endP);
                rfaf.setFeatures(new Graphic[]{point1, point2});
                rp.setStops(rfaf);

                return mRouteTask.solve(rp);


            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(RouteResult results){
            if(results != null) {
                Log.d(TAG, "k");
                mRouteResults = results;
                drawRouteResult(0);
                mRouteResultViewController = new RouteResultViewController(0, results.getRoutes().size());
                mRouteResultViewController.setDependencyContainer(getDependencyContainer());
//                ViewAccessory routeResultViewAccessory = new ViewAccessory();
//                routeResultViewAccessory.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Log.d(TAG, "clicked_");
//                    }
//                });
//                showViewControllerInBottomPanel(mRouteResultViewController, routeResultViewAccessory);
                showViewControllerInBottomPanel(mRouteResultViewController);
            }
        }

    }
    protected void drawRouteResult(int index){
        mRouteLayer.clearSelection();
        mRouteLayer.addGraphic(mRouteResults.getRoutes().get(index).getRouteGraphic());
    }

    public void drawDot(PointType type, Point p, String dispStr){
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

    class RouteResultViewController extends BaseViewController{
        int mRouteResultSize;
        int mCurrRoute;
        TextView mTextView;

        public RouteResultViewController(int routeIndex, int size){
            mCurrRoute = routeIndex;
            mRouteResultSize = size;
        }

        public int getCurrRoute(){
            return mCurrRoute;
        }

        @Override
        public View createView(ViewGroup viewGroup, Bundle bundle) {
            View v = getDependencyContainer().getLayoutInflater()
                    .inflate(R.layout.route_select_layout, viewGroup);
            mTextView = (TextView) v.findViewById(R.id.route_select_textview);

            v.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
                public void onSwipeTop() {

                }
                public void onSwipeRight() {
                    if(mCurrRoute<mRouteResultSize){
                        mCurrRoute++;
                        changeTextView(mCurrRoute);
                        drawRouteResult(mCurrRoute);
                    }
                }
                public void onSwipeLeft() {
                    if(mCurrRoute>= 0){
                        mCurrRoute--;
                        changeTextView(mCurrRoute);
                        drawRouteResult(mCurrRoute);
                    }
                }
                public void onSwipeBottom() {

                }

                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });


            return v;
        }

        private void changeTextView(int route){
            int dispRoute = route + 1;
            mTextView.setText("Route" + dispRoute);

        }



    }
}
