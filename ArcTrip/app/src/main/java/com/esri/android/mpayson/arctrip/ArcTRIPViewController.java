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
import com.esri.appframework.viewcontrollers.navigation.NavigationViewController;
import com.esri.appframework.wrappers.AGSMap;
import com.esri.core.common.V2CallbackListener;
import com.esri.core.portal.WebMap;

/**
 * Created by maxw8108 on 7/27/15.
 */
public class ArcTRIPViewController extends NavigationViewController
        implements SignInListener{

    final static String TAG = "ArcTRIPViewController";

    ProgressDialog mProgressDialog;
    private Account mAccount;

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
        showProgressDialog("Initializing the app...", "Two seconds!");
        AccountManager.getInstance().signIn("http://ess.maps.arcgis.com", "MThornton_ess", "Kaleytoby13!", this);
    }

    private void loadMap(){
        ProgressDialog dialog = getProgressDialog();
        String message = "Just a jiffy...";
        if(dialog.isShowing()){
            dialog.setMessage(message);
        } else{
            showProgressDialog("Loading...", message);
        }


        getAccount().getPortal().fetchWebMap("1449dba9fbd74bd59077242b7dfefe5b",
                new V2CallbackListenerHelper<WebMap>(new V2CallbackListener<WebMap>() {
                    @Override
                    public void onCallbackCompleted(WebMap webMap, Throwable throwable) {
                        dismissProgress();

                        if (throwable != null) {
                            showAlert("Error", "Error downloading map");
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
        super.goTo(mapViewController);
    }


    //region PROGRESS DIALOG

    //Non-interactive progress dialog
    private void showProgressDialog(String title, String message){
        ProgressDialog dialog = getProgressDialog();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.show();
    }

    //Get progress dialog, kinda self-explanatory
    private ProgressDialog getProgressDialog(){

        if (mProgressDialog == null){
            mProgressDialog = new ProgressDialog(getDependencyContainer().getCurrentActivity());
        }
        return mProgressDialog;
    }

    //Get rid of that damn dialog box
    private void dismissProgress() {
        mProgressDialog.dismiss();
        mProgressDialog = null;
    }

    @Override
    public void onSignInFinished(@NonNull Account account) {
        mAccount = account;
        dismissProgress();
        AccountManager.getInstance().setActiveAccount(account);
        loadMap();
    }

    @Override
    public void onSignInError(@NonNull Throwable throwable) {
        dismissProgress();
        showAlert("Error", "Error signing in");
    }

    //Oh no something went wrong, #poundthealarm
    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getDependencyContainer().getCurrentActivity());
        builder.setTitle(title).setMessage(message).setPositiveButton("Ok", null).show();
    }

    //endregion

    private Account getAccount() {
        if (mAccount == null) {
            mAccount = AccountManager.getInstance().getActiveAccount();
        }

        return mAccount;
    }

}
