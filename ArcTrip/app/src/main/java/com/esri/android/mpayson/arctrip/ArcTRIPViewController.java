package com.esri.android.mpayson.arctrip;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.esri.appframework.account.Account;
import com.esri.appframework.account.AccountManager;
import com.esri.appframework.account.SignInListener;
import com.esri.appframework.infrastructure.V2CallbackListenerHelper;
import com.esri.appframework.viewcontrollers.ViewControllerDialog;
import com.esri.appframework.viewcontrollers.navigation.NavigationViewController;
import com.esri.appframework.wrappers.AGSMap;
import com.esri.core.common.V2CallbackListener;
import com.esri.core.portal.WebMap;
import com.esri.core.tasks.na.Route;

import java.util.UUID;

/**
 * Created by maxw8108 on 7/27/15.
 */
public class ArcTRIPViewController extends NavigationViewController
        implements ArcTripMapViewController.ArcTripMapVCListener,
        SelectTypeDialogVC.Listener, NearbyFeatureRecyclerVC.Listener{

    final static String TAG = "ArcTRIPViewController";

    ProgressDialog mProgressDialog;
    private Account mAccount;
    public Route mRoute;

    private ViewControllerDialog mVCDialog;

    @Override
    public View createView(ViewGroup parentView, Bundle savedState){
        View v = super.createView(parentView, savedState);

        startRouting();
//        startCrowdSourcing();
        return v;
    }

    public void startRouting(){
        if(AccountManager.getInstance().getActiveAccount() == null){
            signInRoute();
        } else {
            loadMap();
        }
    }

    private void signInRoute(){

        Utils.showProgressDialog("Initializing the app...", "Two seconds!",
                getDependencyContainer().getCurrentActivity());
//        AccountManager.getInstance().signIn("http://ess.maps.arcgis.com", "MThornton_ess", "Kaleytoby13!",
        AccountManager.getInstance().signIn("https://Nitro.maps.arcgis.com", "MPayson_Nitro", "AcheTeVitu0811",
                new SignInListener() {
                    @Override
                    public void onSignInFinished(@NonNull Account account) {
                        mAccount = account;
                        ProgressDialog dialog = Utils.getProgressDialog(getDependencyContainer().getCurrentActivity());
                        Utils.dismissProgress();
                        AccountManager.getInstance().setActiveAccount(account);
                        loadMap();
                    }

                    @Override
                    public void onSignInError(@NonNull Throwable throwable) {
                        Utils.dismissProgress();
                        Utils.showAlert("Error", "Error signing in", getDependencyContainer().getCurrentActivity());
                    }
                });
    }

    private void loadMap(){

        ProgressDialog dialog = Utils.getProgressDialog(getDependencyContainer().getCurrentActivity());
        String message = "Just a jiffy...";
        if (dialog.isShowing()) {
            dialog.setMessage(message);
        } else {
            Utils.showProgressDialog("Loading...", message, getDependencyContainer().getCurrentActivity());
        }

        getAccount().getPortal().fetchWebMap("1449dba9fbd74bd59077242b7dfefe5b",
                new V2CallbackListenerHelper<WebMap>(new V2CallbackListener<WebMap>() {
                    @Override
                    public void onCallbackCompleted(WebMap webMap, Throwable throwable) {
                        Utils.dismissProgress();

                        if (throwable != null) {
                            Utils.showAlert("Error", "Error downloading map", getDependencyContainer().getCurrentActivity());
                            return;
                        }

                        Log.d(TAG, "Download map complete.");

                        initMapViewController(webMap);
                    }
                }));
    }

    private void initMapViewController(WebMap webMap){
        ArcTripMapViewController mapViewController= new ArcTripMapViewController(new AGSMap(webMap));
        mapViewController.setListener(this);
        super.goTo(mapViewController);
    }

    @Override
    public void onFABClicked(Route route) {
        mRoute = route;
        SelectTypeDialogVC selectTypeDialogVC = new SelectTypeDialogVC(route);
        selectTypeDialogVC.setDependencyContainer(getDependencyContainer());
        selectTypeDialogVC.setListener(this);
        ViewControllerDialog.Builder builder = new ViewControllerDialog.Builder(selectTypeDialogVC);
        mVCDialog = builder.setFullScreen(true).setHasCloseButton(true).show();
    }

    @Override
    public void onButtonClicked(int type) {
        //TODO query nearby, for now show recyclerview
        mVCDialog.dismiss();
        NearbyFeatureRecyclerVC nearbyFeatureRecyclerVC = new NearbyFeatureRecyclerVC();
        nearbyFeatureRecyclerVC.setDependencyContainer(getDependencyContainer());
        nearbyFeatureRecyclerVC.setListener(this);
        goTo(nearbyFeatureRecyclerVC);
    }

    @Override
    public void onNearbyFeatureSelected(UUID id) {
        SelectedFeatureViewController selectedFeatureViewController = new SelectedFeatureViewController(id);
        selectedFeatureViewController.setDependencyContainer(getDependencyContainer());
        goTo(selectedFeatureViewController);
    }

    public void startCrowdSourcing(){
        if(AccountManager.getInstance().getActiveAccount() == null){
//            signInCrowd();
        }
        displayOnStoppedView();
    }

    //TODO OPEN MAP (STARTROUTING()) WHEN DONE OR DISMISSED
    public void displayOnStoppedView(){
        //TODO get reference to last stop and pass in title
        OnStopViewController vc = new OnStopViewController(null);
        vc.setDependencyContainer(getDependencyContainer());
        vc.setListener(new OnStopViewController.Listener() {
            @Override
            public void onStopTypeSelect(Utils.STOP stopType) {
                mVCDialog.dismiss();
                if (stopType != Utils.STOP.OTHER) {
                    displayEditFeatureView(stopType);
                } else {
                    //TODO ADD DIALOG
                }
            }
        });
        ViewControllerDialog.Builder builder = new ViewControllerDialog.Builder(vc);
        mVCDialog = builder.setFullScreen(true).setHasCloseButton(true).show();
    }

    public void displayEditFeatureView(Utils.STOP stopType){
        EditFeatureViewController vc = new EditFeatureViewController(stopType);
        vc.setDependencyContainer(getDependencyContainer());
        ViewControllerDialog.Builder builder = new ViewControllerDialog.Builder(vc);
        mVCDialog = builder.setFullScreen(true).setHasCloseButton(true)
                .show();
    }

    private void signInCrowd(){
        AccountManager.getInstance().signIn("http://ess.maps.arcgis.com", "MThornton_ess", "Kaleytoby13!",
                new SignInListener() {
                    @Override
                    public void onSignInFinished(@NonNull Account account) {
                        mAccount = account;
                        AccountManager.getInstance().setActiveAccount(account);
                    }

                    @Override
                    public void onSignInError(@NonNull Throwable throwable) {
                        Utils.showAlert("Error", "Error signing in", getDependencyContainer().getCurrentActivity());
                    }
                });
    }

    private Account getAccount() {
        if (mAccount == null) {
            mAccount = AccountManager.getInstance().getActiveAccount();
        }

        return mAccount;
    }
}
