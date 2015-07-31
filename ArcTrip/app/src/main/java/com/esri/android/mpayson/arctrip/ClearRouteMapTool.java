package com.esri.android.mpayson.arctrip;

import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.esri.appframework.viewcontrollers.map.tools.MapTool;
import com.esri.appframework.wrappers.AGSMap;

/**
 * Created by maxw8108 on 7/31/15.
 */
public class ClearRouteMapTool extends MapTool implements MenuItem.OnMenuItemClickListener {
    ClearRouteToolListener mListener;


    public interface ClearRouteToolListener{
        void onClearClicked();
        void onContributeClicked();
    }

    public ClearRouteMapTool(@NonNull ClearRouteToolListener listener){
        super(R.menu.arctrip_map_menu, new int[]{R.id.arctrip_map_menu_clear, R.id.arctrip_map_menu_contribute});
        mListener = listener;
    }

    @Override
    public void updateAvailability(AGSMap agsMap) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item){
        switch(item.getItemId()){
            case R.id.arctrip_map_menu_clear:
                mListener.onClearClicked();
                return true;
            case R.id.arctrip_map_menu_contribute:
                mListener.onContributeClicked();
                return true;
            default:
                return false;
        }
    }

}
