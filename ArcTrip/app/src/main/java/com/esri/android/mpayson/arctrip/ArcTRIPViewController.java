package com.esri.android.mpayson.arctrip;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.esri.appframework.account.Account;
import com.esri.appframework.account.AccountManager;
import com.esri.appframework.account.SignInListener;
import com.esri.appframework.infrastructure.V2CallbackListenerHelper;
import com.esri.appframework.viewcontrollers.DependencyContainer;
import com.esri.appframework.viewcontrollers.ViewControllerDialog;
import com.esri.appframework.viewcontrollers.navigation.NavigationViewController;
import com.esri.appframework.wrappers.AGSMap;
import com.esri.core.common.V2CallbackListener;
import com.esri.core.portal.WebMap;
import com.esri.core.tasks.na.Route;

/**
 * Created by maxw8108 on 7/27/15.
 */
public class ArcTRIPViewController extends NavigationViewController
        implements SignInListener, ArcTripMapViewController.ArcTripMapVCListener,
        SelectTypeDialogVC.Listener{

    final static String TAG = "ArcTRIPViewController";

    ProgressDialog mProgressDialog;
    private Account mAccount;
    public Route mRoute;

    private ViewControllerDialog mSelectTypeDialog;

    @Override
    public View createView(ViewGroup parentView, Bundle savedState){
        View v = super.createView(parentView, savedState);

        if(AccountManager.getInstance().getActiveAccount() == null){
            signIn();
        } else {
            loadMap();
        }

        return v;
    }

    private void signIn(){
        DialogUtils.showProgressDialog("Initializing the app...", "Two seconds!",
                getDependencyContainer().getCurrentActivity());
        AccountManager.getInstance().signIn("http://ess.maps.arcgis.com", "MThornton_ess", "Kaleytoby13!", this);
    }

    private void loadMap(){
        ProgressDialog dialog = DialogUtils.getProgressDialog(getDependencyContainer().getCurrentActivity());
        String message = "Just a jiffy...";
        if(dialog.isShowing()){
            dialog.setMessage(message);
        } else{
            DialogUtils.showProgressDialog("Loading...", message, getDependencyContainer().getCurrentActivity());
        }


        getAccount().getPortal().fetchWebMap("1449dba9fbd74bd59077242b7dfefe5b",
                new V2CallbackListenerHelper<WebMap>(new V2CallbackListener<WebMap>() {
                    @Override
                    public void onCallbackCompleted(WebMap webMap, Throwable throwable) {
                        DialogUtils.dismissProgress();

                        if (throwable != null) {
                            DialogUtils.showAlert("Error", "Error downloading map", getDependencyContainer().getCurrentActivity());
                            return;
                        }

                        Log.d(TAG, "Download map complete.");

                        initMapViewController(webMap);
                    }
                }));
    }

    private void initMapViewController(WebMap webMap){
//        DependencyContainer dependencyContainer =
//                new DependencyContainer.Builder(getDependencyContainer()).create();
        ArcTripMapViewController mapViewController= new ArcTripMapViewController(new AGSMap(webMap));
        mapViewController.setListener(this);
        super.goTo(mapViewController);
    }


    //region PROGRESS DIALOG

    @Override
    public void onSignInFinished(@NonNull Account account) {
        mAccount = account;
        DialogUtils.dismissProgress();
        AccountManager.getInstance().setActiveAccount(account);
        loadMap();
    }

    @Override
    public void onSignInError(@NonNull Throwable throwable) {
        DialogUtils.dismissProgress();
        DialogUtils.showAlert("Error", "Error signing in", getDependencyContainer().getCurrentActivity());
    }

    //endregion

    private Account getAccount() {
        if (mAccount == null) {
            mAccount = AccountManager.getInstance().getActiveAccount();
        }

        return mAccount;
    }

    @Override
    public void onFABClicked(Route route) {
        mRoute = route;
        SelectTypeDialogVC selectTypeDialogVC = new SelectTypeDialogVC(route);
        selectTypeDialogVC.setDependencyContainer(getDependencyContainer());
        selectTypeDialogVC.setListener(this);
        ViewControllerDialog.Builder builder = new ViewControllerDialog.Builder(selectTypeDialogVC);
        mSelectTypeDialog = builder.setFullScreen(true).setHasCloseButton(true).show();
    }

    @Override
    public void onButtonClicked(int type) {
        //TODO query nearby, for now show recyclerview
        mSelectTypeDialog.dismiss();
        NearbyFeatureRecyclerVC nearbyFeatureRecyclerVC = new NearbyFeatureRecyclerVC();
        nearbyFeatureRecyclerVC.setDependencyContainer(getDependencyContainer());
        goTo(nearbyFeatureRecyclerVC);
    }
}
