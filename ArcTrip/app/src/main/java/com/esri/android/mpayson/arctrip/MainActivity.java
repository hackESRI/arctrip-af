package com.esri.android.mpayson.arctrip;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.esri.appframework.BaseViewControllerActivity;
import com.esri.appframework.CurrentActivityProvider;
import com.esri.appframework.viewcontrollers.DependencyContainer;
import com.esri.appframework.viewcontrollers.ViewController;


public class MainActivity extends BaseViewControllerActivity {
    ArcTRIPViewController mNavViewController;

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
//        setContentView(R.layout.activity_main);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
