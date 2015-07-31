package com.esri.android.mpayson.arctrip;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.esri.appframework.BaseViewControllerActivity;
import com.esri.appframework.CurrentActivityProvider;
import com.esri.appframework.viewcontrollers.DependencyContainer;
import com.esri.appframework.viewcontrollers.ViewController;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends BaseViewControllerActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "Main Activity";
    ArcTRIPViewController mNavViewController;
    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    boolean isConnected = false;

    @Override
    protected ViewController createRootViewController() {
        DependencyContainer dependencyContainer = new DependencyContainer.Builder()
                .setCurrentActivityProvider((CurrentActivityProvider) getApplication())
                .create();
        mNavViewController = new ArcTRIPViewController();
        mNavViewController.setDependencyContainer(dependencyContainer);
        return mNavViewController;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        isConnected = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "CONNECTION SUSPENDED");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection Failed");
    }

}
