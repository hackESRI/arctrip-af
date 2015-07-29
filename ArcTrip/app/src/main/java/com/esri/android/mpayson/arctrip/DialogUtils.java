package com.esri.android.mpayson.arctrip;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by maxw8108 on 7/29/15.
 */
public class DialogUtils {
    private static ProgressDialog mProgressDialog;

    //Non-interactive progress dialog
    public static void showProgressDialog(String title, String message, Context activity){
        ProgressDialog dialog = getProgressDialog(activity);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.show();
    }

    //Get progress dialog, kinda self-explanatory
    public static ProgressDialog getProgressDialog(Context activity){

        if (mProgressDialog == null){
            mProgressDialog = new ProgressDialog(activity);
        }
        return mProgressDialog;
    }

    //Get rid of that damn dialog box
    public static void dismissProgress() {
        mProgressDialog.dismiss();
        mProgressDialog = null;
    }

    //Oh no something went wrong, #poundthealarm
    public static void showAlert(String title, String message, Context activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title).setMessage(message).setPositiveButton("Ok", null).show();
    }
}
