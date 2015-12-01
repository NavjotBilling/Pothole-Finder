package com.example.start.finalproject2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 100461439 on 11/30/2015.
 */
public class potholeAdapter extends ArrayAdapter<potHole> {
    public potholeAdapter(Context context, ArrayList<potHole> Contact){
        super(context,0,Contact);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        potHole pothole = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.pothole_inflator, parent, false);
        }
        TextView tvLocation = (TextView) convertView.findViewById(R.id.tvLocation);
        TextView tvnumSpotted = (TextView) convertView.findViewById(R.id.tvnumSpotted);

        tvLocation.setText(""+pothole.getLocationName());
        tvnumSpotted.setText(""+pothole.getNumSpotted());

        return convertView;
    }
}
