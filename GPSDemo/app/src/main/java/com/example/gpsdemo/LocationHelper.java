package com.example.gpsdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import kotlin.jvm.Synchronized;

import static android.content.Context.LOCATION_SERVICE;

public class LocationHelper implements LocationListener{

    private final String TAG = "LocationHelper";
    private final int MIN_UPDATE_TIME = 2000;
    private final int MIN_DISTANCE_UPDATE_TIME = 2000;
    public static final int PERMISSION_GPS_LOCATION = 0x001;

    private Activity mActivity;
    private OnLocationHelperListener mOnLocationHelperListener;
    private static LocationHelper sInstance;

    public interface OnLocationHelperListener {
        void onSuccess(double latitude, double longitude);
    }

    @Synchronized
    public static LocationHelper getInstance(Activity activity){
        if(sInstance == null){
            sInstance = new LocationHelper(activity);
        }

        return sInstance;
    }

    private LocationHelper(Activity activity) {
        this.mActivity = activity;
    }

    public LocationHelper setOnLocationHelperListener(OnLocationHelperListener listener) {
        mOnLocationHelperListener = listener;
        return this;
    }

    public boolean hasPermission(){
        return ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
         && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(){
        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_GPS_LOCATION);
    }

    @SuppressLint("MissingPermission")
    public LocationManager stratLocationTracking() {

        Location location = null;
        String origin = null;
        LocationManager locationMgr = null;

        if (hasPermission()){
            locationMgr = (LocationManager) mActivity.getSystemService(LOCATION_SERVICE);

            if (locationMgr != null) {

                // Getting GPS status
                boolean isGPSEnabled = locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
                // Getting network status
                boolean isNetworkEnabled = locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (isNetworkEnabled || isGPSEnabled) {
                    Log.i(TAG, "GPS Enabled.");
                    if (isNetworkEnabled) {
                        Log.i(TAG, "Found Network GPS Provider.");
                        locationMgr.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_UPDATE_TIME,
                                MIN_DISTANCE_UPDATE_TIME, this);

                        location = locationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            //origin = location.getLatitude() + "," + location.getLongitude();
                        }
                    }

                    if (isGPSEnabled) {
                        Log.i(TAG, "Found GPS Provider.");
                        locationMgr.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_UPDATE_TIME,
                                MIN_DISTANCE_UPDATE_TIME, this);

                        location = locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            //origin = location.getLatitude() + "," + location.getLongitude();
                        }
                    }

                    if (mOnLocationHelperListener != null && location != null) {
                        mOnLocationHelperListener.onSuccess(location.getLatitude(), location.getLongitude());
                    }
                } else {
                    Log.i(TAG, "Not found GPS.");
                }

            } else {
                Log.i(TAG, "No GPS Permission.");
            }
        }else{
            requestPermission();
        }

        return locationMgr;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
