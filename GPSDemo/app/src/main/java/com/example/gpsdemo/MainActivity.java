package com.example.gpsdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

import static com.example.gpsdemo.LocationHelper.PERMISSION_GPS_LOCATION;

public class MainActivity extends AppCompatActivity implements LocationHelper.OnLocationHelperListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String TAG = "MainActivity";
    private LocationHelper locationHelper;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleApiClient mGoogleApiClient;
    private LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            //
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            //
        }
    };

    private TextView tvLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocation = findViewById(R.id.tvLocation);
        locationHelper = LocationHelper.getInstance(this)
                .setOnLocationHelperListener(this);

        if(!locationHelper.hasPermission()){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_GPS_LOCATION);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();

//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(createLocationRequest());
//
//        SettingsClient client = LocationServices.getSettingsClient(this);
//        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
//
//        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
//            @Override
//            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                Log.i(TAG, "onSuccess: LocationSettingsResponse");
//            }
//        });
//
//        task.addOnFailureListener(this, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                if (e instanceof ResolvableApiException) {
//                    // Location settings are not satisfied, but this can be fixed
//                    // by showing the user a dialog.
////                    try {
////                        // Show the dialog by calling startResolutionForResult(),
////                        // and check the result in onActivityResult().
////                        ResolvableApiException resolvable = (ResolvableApiException) e;
////                        resolvable.startResolutionForResult(MainActivity.this,
////                                REQUEST_CHECK_SETTINGS);
////                    } catch (IntentSender.SendIntentException sendEx) {
////                        // Ignore the error.
////                    }
//                }
//            }
//        });
    }

    @SuppressLint("MissingPermission")
    private void startLocationListener(){
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Log.i(TAG, "onSuccess: " + location.getAltitude());
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //getLastLocation();
        if (locationHelper.hasPermission()) {
//            //locationHelper.stratLocationTracking();
//            //locationHelper.requestPermission();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && PERMISSION_GPS_LOCATION == requestCode
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //locationHelper.stratLocationTracking();
            startLocationListener();
        }
    }

    @Override
    public void onSuccess(double latitude, double longitude) {
        tvLocation.setText("latitude : " + latitude + ", longitude : " + longitude);
    }

//    private LocationRequest createLocationRequest() {
//        LocationRequest mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(10000);
//        mLocationRequest.setFastestInterval(5000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        return mLocationRequest;
//    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location mLastLocation = task.getResult();

//                            mLatitudeText.setText(String.format(Locale.ENGLISH, "%s: %f",
//                                    mLatitudeLabel,
//                                    mLastLocation.getLatitude()));
//                            mLongitudeText.setText(String.format(Locale.ENGLISH, "%s: %f",
//                                    mLongitudeLabel,
//                                    mLastLocation.getLongitude()));
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected: ");
        createLocationRequest();
        startLocationListener();
        //Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended: ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed: ");
    }

    @SuppressLint("MissingPermission")
    private LocationRequest createLocationRequest() {

        //remove location updates so that it resets
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        return mLocationRequest;
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
