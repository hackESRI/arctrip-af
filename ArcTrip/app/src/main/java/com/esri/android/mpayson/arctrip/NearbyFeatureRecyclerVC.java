package com.esri.android.mpayson.arctrip;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.esri.appframework.common.recycler.BaseRecyclerItem;
import com.esri.appframework.common.recycler.RecyclerItem;
import com.esri.appframework.viewcontrollers.RecyclerViewController;
import com.esri.appframework.viewcontrollers.ViewController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxw8108 on 7/29/15.
 */
public class NearbyFeatureRecyclerVC extends RecyclerViewController{

    @Override
    public View createView(ViewGroup parentView, Bundle savedState){
        View v = super.createView(parentView, savedState);
        setRecyclerItems(createRecyclerItems());

        return v;
    }

    private List<RecyclerItem> createRecyclerItems(){
        ArrayList<RecyclerItem> recyclerItems = new ArrayList<>();
        ArrayList<NearbyFeature> nearbyFeatures = NearbyFeatureGallery.get(getContext()).getFeatures();

        if(nearbyFeatures != null) {
            for (int i = 0; i < nearbyFeatures.size(); i++) {
                recyclerItems.add(new NearbyFeatureRecyclerItem(nearbyFeatures.get(i)));
            }
        }
        return recyclerItems;
    }

    private class NearbyFeatureRecyclerItem extends BaseRecyclerItem{
        private NearbyFeature mNearbyFeature;

        public NearbyFeatureRecyclerItem(NearbyFeature feature){
            mNearbyFeature = feature;
        }

        @Override
        public int getLayoutResource(){return R.layout.nearby_feature_list_item;}

        @Override
        public RecyclerView.ViewHolder newViewHolder(View view){
            return new RecyclerView.ViewHolder(view){};
        }

        @Override
        public void bindViewHolder(final RecyclerView.ViewHolder viewHolder){
            super.bindViewHolder(viewHolder);

            NearbyFeatureListView v = (NearbyFeatureListView) viewHolder.itemView;

            v.setStars(mNearbyFeature.getStars());
            v.setTitleText(mNearbyFeature.getTitle());

        }
    }

}
