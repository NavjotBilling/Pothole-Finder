package com.example.start.finalproject2;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements LocationListener, DirectionListener {
    /***********
     * Shake sensor
     **********/
    private SensorManager mSensorManager;
    public float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    /***********
     * GoogleMap declarations
      ***********/
    private GoogleMap map;
    private double latitude, longitude;
    private String locationName;
    private String Destination;
    private databaseHelper dbHelper = new databaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.d("user", "creating the new page");
        requestLocationPermissions();

        //this is for gps activation
        LocationManager locManager;
        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String gpsProvider = LocationManager.GPS_PROVIDER;
        if (!locManager.isProviderEnabled(gpsProvider)) {
            //ask the user for permission to turn on gps
            String config = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
            Intent enableGPS = new Intent(config);
            startActivity(enableGPS);
        } else {
            //there exists a GPSActivity so rest of program can run
            modifyLocation();
        }
        setUpMap();

        /** Gravity is default to Earth's Gravity */
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }


    private final SensorEventListener mSensorListener = new SensorEventListener() {



        /** Function to change the sensitivity of shake using accelerometer*/
        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;

            /** Low-cut Filter */


            mAccel = mAccel * 0.9f + delta * 0.1f;

            if (mAccel > 2)
            {
                Toast toast = Toast.makeText(getApplicationContext(), "PotHole Found!", Toast.LENGTH_LONG);
                toast.show();

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try{
                    List<Address> address = geocoder.getFromLocation(latitude, longitude, 1);
                    dbHelper.createPotHole(latitude,longitude, address.get(0).getAddressLine(0));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        /**********
         * Helps with accuracy but gravity deprecated this
         * @param sensor
         * @param accuracy
         */
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

    };


    public void historyClicked(View V){
        Intent intent = new Intent(this, potHoleDisplay.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    /**************
     * Reset the map when we resume the application
     **************/
    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        setUpMap();
    }

    /**************
     * change the current location
     **************/
    private void modifyLocation(){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("user", "modifing location");
            LocationManager locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
            //choose the best provider
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(false);
            String recommended = locManager.getBestProvider(criteria, true);

            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);

            Location location = locManager.getLastKnownLocation(recommended);
            if(location != null){
                String address = showLocationName(location);
                locationName=address;
                setLocation(address);
            }
        } else {
            Log.d("error", "location Provider permission was denied");
        }
    }

    /***************
     * The 4 functions below are listeners for location changes
     * @param provider
     */
    public void onProviderEnabled(String provider) {
        Log.d("user", "onProviderEnabled(" + provider + ")");
    }

    public void onProviderDisabled(String provider) {
        Log.d("User", "onProviderDisabled(" + provider + ")");
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("User", "onStatusChanged(" + provider + ", " + status + ", extras)");
    }

    public void onLocationChanged(Location location) {
        Log.d("User", "onLocationChanged(" + location + ")");
        String address = showLocationName(location);
        locationName=address;
        setLocation(address);
        if(Destination != null) {
            updateDirections();
        }
    }



    //this is the request code returned from the permission manager upon success
    final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 410020;
    /*****************
     * Check if we have the correct permission for gps
     *****************/
    private void requestLocationPermissions() {
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d("user", "We require positional information to allow the gps to function correctly");
            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_LOCATION);

            return;
        }
    }

    @Override
    /***************
     * Run upon recieving permission result
     * runs the update function if permission was succesful
     ***************/
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    modifyLocation();
                } else {
                    // tell the user that the feature will not work
                    Log.d("user", "The program could not recieve access to your location");
                }
                return;
            }
        }
    }

    /***********8
     * fill the google map with the location data
     ************/
    private void setUpMap(){
        if(map == null){
            map =  ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (map != null) {
                Log.d("error", "map value was " + map.toString());
                showMapLocation();
            }
        }
    }

    /************
     * Create an async call to gather directions from google API
     * @param V
     */
    private String baseURL = "https://maps.googleapis.com/maps/api/directions/xml?origin=";
    public void directionClick(View V){
        EditText destination = (EditText) findViewById(R.id.destination);
        Destination = destination.getText().toString();
        if(Destination != null) {
            updateDirections();
        }
    }

    private void updateDirections(){
        String url = baseURL +locationName+"&destination="+Destination+"&key="+"AIzaSyDp7e1rUEp_AKCByBEVBfLlC55Z_Vy5Rzo";//R.string.google_maps_key;
        url = url.replaceAll(" ", "%20");
        Log.d("user",url);
        QueryDirections query = new QueryDirections(this);
        query.execute(url);
    }

    /*************
     * This will recieve the directions from the async task
     * @param directions
     */
    public void showDirections(String directions){
        String[] pairs = directions.split(";");
        Log.d("error", ""+pairs);
        List<LatLng> latlngs = new ArrayList<LatLng>();
        for(int i=0; i< pairs.length;i++){
            String[] temp = pairs[i].split(",");
            LatLng latlng = new LatLng(Double.parseDouble(temp[0]),Double.parseDouble(temp[1]));
            Log.d("error",""+latlng);
            latlngs.add(latlng);
        }
        Polyline line = map.addPolyline(new PolylineOptions()
                .addAll(latlngs)
                .width(5)
                .color(Color.RED));
    }

    /************
     * creates the map and our location
     ************/
    private void showMapLocation() {
        Log.d("user", "Finished setting up the map");
        LatLng position = new LatLng(latitude, longitude);
        map.clear();
        map.addMarker(new MarkerOptions().position(position).title("Search Results"));
        map.animateCamera(CameraUpdateFactory.newLatLng(position));

        map.setTrafficEnabled(true);
        map.setBuildingsEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
    }


    /************
     * The backward geocoder, used to convert from latlng to a name
     * @param location the location as a lat, lng pair
     */
    private String showLocationName(Location location) {
        Log.d("User", "showLocationName(" + location + ")");
        // perform a reverse geocode to get the address
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            try {
                // reverse geocode from current GPS position
                List<Address> results = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if (results.size() > 0) {
                    Log.d("user","I got a result");
                    Address match = results.get(0);
                    String address = match.getAddressLine(0);
                    return address;
                } else {
                    Log.d("User", "No results found while reverse geocoding GPS location");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d("User", "No geocoder present");
        }
        return "Calgary";
    }

    /*************
     * uses the geocoder to find lat and lng given some address
     * @param address The name of a geographical location
     * @return a Address object with the geographical lat and lng
     *************/
    private Address geocodeLookup(String address) {
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            try {
                // forward geocode from the provided address
                List<Address> results = geocoder.getFromLocationName(address, 1);

                if (results.size() > 0) {
                    return results.get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /***********
     * Geocode and send to the map our location
     * @param location our current location
     ***********/
    private void setLocation(String location) {
        Log.d("User", "setLocation("+location+")");
        Address address = geocodeLookup(location);
        if(address != null) {
            latitude = address.getLatitude();
            longitude = address.getLongitude();
        }
        if(map != null) {
            showMapLocation();
        }
    }
}

