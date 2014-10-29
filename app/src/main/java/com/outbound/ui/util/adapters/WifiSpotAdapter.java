package com.outbound.ui.util.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PUser;
import com.outbound.model.PWifiSpot;
import com.outbound.model.WifiSpot;
import com.outbound.ui.util.RoundedImageView;
import com.outbound.util.Constants;
import com.parse.ParseGeoPoint;

/**
 * Created by zeki on 27/10/2014.
 */
public class WifiSpotAdapter extends ArrayAdapter<WifiSpot> {

    private LayoutInflater inflater;
    private ParseGeoPoint referenceGeoPoint = null;
    private boolean isWifiTypeVisible;
    public WifiSpotAdapter(Context context, boolean isWifiTypeVisible) {
        super(context, 0);

        inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        this.isWifiTypeVisible = isWifiTypeVisible;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if(view == null){
            view = inflater.inflate(R.layout.wifi_spot_item, parent, false);
            holder = new ViewHolder();
            holder.wifiName = (TextView)view.findViewById(R.id.wifi_name);
            holder.wifiAddress = (TextView)view.findViewById(R.id.wifi_address);
            holder.wifiDistance = (TextView)view.findViewById(R.id.wifi_distance);
            holder.wifiType = (ImageView)view.findViewById(R.id.wifi_type);
            holder.wifiType.setVisibility(isWifiTypeVisible?View.VISIBLE:View.GONE);
            view.setTag(holder);
        }else
        {
            holder = (ViewHolder) view.getTag();
        }

        final WifiSpot spot = getItem(position);


        holder.wifiAddress.setText(spot.getWifiAddress());
        holder.wifiName.setText(spot.getWifiName());
        Double distance = spot.getWifiLocation().distanceInKilometersTo(referenceGeoPoint);
        String str = String.format("%.2fkm", distance);
        holder.wifiDistance.setText(str);

        String wifiType = spot.getWifiType();
        if(wifiType.equals("withPurchase")){
            holder.wifiType.setImageResource(R.drawable.wifi_with_purchase);
        }else if(wifiType.equals("free")){
            holder.wifiType.setImageResource(R.drawable.wifi_free);
        }else if(wifiType.equals("paid")){
            holder.wifiType.setImageResource(R.drawable.wifi_paid);
        }
        return view;
    }


    public void setReferenceGeoPoint(ParseGeoPoint ref){
        this.referenceGeoPoint = ref;
    }

    private static class ViewHolder {
        TextView wifiName;
        TextView wifiAddress;
        TextView wifiDistance;
        ImageView wifiType;
    }
}
