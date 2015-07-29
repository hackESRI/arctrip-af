package com.esri.android.mpayson.arctrip;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;


import com.esri.android.mpayson.arctrip.R;
import com.esri.appframework.EsriAppContextProvider;
import com.esri.appframework.common.recycler.BaseRecyclerItem;
import com.esri.appframework.common.recycler.RecyclerItem;
import com.esri.appframework.viewcontrollers.RecyclerViewController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.esri.appframework.viewcontrollers.BaseViewController;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteDirection;

/**
 * Created by maxw8108 on 7/29/15.
 */
public class SelectTypeDialogVC extends BaseViewController{
    public Route mRoute;

    public SelectTypeDialogVC(Route route){
        mRoute = route;
    }


    @Override
    public View createView(ViewGroup viewGroup, Bundle bundle) {
        return null;
    }

    public class RouteListViewController extends RecyclerViewController{

        private Context mContext;

        public RouteListViewController (Route route){
            super();
        }

        @Override
        public View createView(ViewGroup parentView, Bundle savedState) {
            View v = super.createView(parentView, savedState);

            mContext = EsriAppContextProvider.getContext();

            setRecyclerItems(createRecyclerItems());

            return v;
        }

        private List<RecyclerItem> createRecyclerItems(){
            ArrayList<RecyclerItem> recyclerItems = new ArrayList<>();
            List<RouteDirection> routeDirections = mRoute.getRoutingDirections();

            if(routeDirections != null){
                for (int i = 0; i < routeDirections.size(); i++){
                    recyclerItems.add(new DirectionsRecyclerItem(routeDirections));
                }
            }
            return recyclerItems;
        }

        private class DirectionsRecyclerItem extends BaseRecyclerItem{
            private RouteDirection mRouteDirection;

            private DirectionsRecyclerItem(RouteDirection direction){
                mRouteDirection = direction;
            }

            @Override
            public int getLayoutResource()  {
                return R.layout.list_item_directions;
            }

            @Override
            public RecyclerView.ViewHolder newViewHolder(View view){
                return new RecyclerView.ViewHolder(view){};
            }

            @Override
            public void bindViewHolder(final RecyclerView.ViewHolder viewHolder){
                super.bindViewHolder(viewHolder);
//      Context context = viewHolder.itemView.getContext();

                DirectionsListView v = (DirectionsListView) viewHolder.itemView;

                v.setDirectionText(mRouteDirection.getText());
                if(d.getDistance() > 0) {
                    v.setDistanceText(String.format("%.2f", mRouteDirection.getLength()) + " mi");
                }

            }

        }
    }
}
