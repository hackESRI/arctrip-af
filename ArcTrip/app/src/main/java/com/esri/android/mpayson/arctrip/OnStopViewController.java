package com.esri.android.mpayson.arctrip;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.esri.appframework.viewcontrollers.BaseViewController;

/**
 * Created by maxw8108 on 7/30/15.
 */
public class OnStopViewController extends BaseViewController {
    String mPrevStopTitle;
    Listener mListener;

    public void setListener(Listener listener){
        mListener=listener;
    }

    public interface Listener{
        void onStopTypeSelect(Utils.STOP stopType);
    }

    public OnStopViewController(String prevStopTitle){
        mPrevStopTitle = prevStopTitle;
    }

    @Override
    public View createView(ViewGroup viewGroup, Bundle bundle) {
        View v = getDependencyContainer().getLayoutInflater()
                .inflate(R.layout.on_stop_layout, viewGroup, false);

        if(mPrevStopTitle != null){
            Button prevButton = (Button) v.findViewById(R.id.on_stop_previous_button);
            prevButton.setVisibility(View.VISIBLE);
            prevButton.setText(mPrevStopTitle);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onStopTypeSelect(Utils.STOP.PREV);
                }
            });
        }

        v.findViewById(R.id.on_stop_bathroom_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onStopTypeSelect(Utils.STOP.BATH);
            }
        });

        v.findViewById(R.id.on_stop_gas_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onStopTypeSelect(Utils.STOP.GAS);
            }
        });

        v.findViewById(R.id.on_stop_none_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onStopTypeSelect(Utils.STOP.OTHER);
            }
        });

        return v;
    }
}
