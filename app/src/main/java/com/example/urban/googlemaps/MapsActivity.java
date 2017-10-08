package com.example.urban.googlemaps;

/**
 * Created by urban on 7. 10. 2017.
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
         GoogleApiClient.OnConnectionFailedListener, LocationListener, SensorEventListener {

    private GoogleMap mMap;
    private final int REQUEST_LOCATION = 1;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private TextView mLatitudeText;
    private TextView mLongitudeText, mAccX, mAccY, mAccZ;
    private TextView mAddressText;
    private Marker mMarker;
    private Button mBtnRecord;
    private Button mBtnData;
    public boolean mFile = false;
    private File file;

//    private AddressResultReceiver mResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // get TextViews
        mLatitudeText = (TextView) findViewById(R.id.txtLatitude);
        mLongitudeText = (TextView) findViewById(R.id.txtLongitude);
        mAccX = (TextView) findViewById(R.id.txtAccX);
        mAccY = (TextView) findViewById(R.id.txtAccY);
        mAccZ = (TextView) findViewById(R.id.txtAccZ);

        mBtnRecord = (Button) findViewById(R.id.btnRecord);
        mBtnData = (Button) findViewById(R.id.btnShowData);
        mBtnRecord.setTag(1);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        //accelerometer sensor
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // sensor listener
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // build Google Play Services Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        MarkerOptions a = new MarkerOptions()
//                .position(new LatLng(50,6));
//        Marker m = mMap.addMarker(a);
//        m.setPosition(new LatLng(50,5));
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // show location in TextViews
        mLatitudeText.setText("Latitude: " + location.getLatitude());
        mLongitudeText.setText("Longitude: " + location.getLongitude());
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        if (mMarker == null) {
            mMarker = mMap.addMarker(new MarkerOptions().position(position).title("Your actual position"));
        } else {
            mMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        if (Constants.RECORDING == 0) {
            if (!mFile) {
                File folder = new File(Environment.getExternalStorageDirectory()
                        + "/gpsApp");

                boolean var = false;
                if (!folder.exists())
                    var = folder.mkdir();

//                Toast.makeText(getApplicationContext(), String.valueOf(folder), Toast.LENGTH_SHORT).show();

                long timeStamp = System.currentTimeMillis();
                final String filename = folder.toString() + "/" + String.valueOf(timeStamp) + ".csv";

                file = new File(filename);
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (file.exists()) {
                    mFile = true;
                }
            }
            try {
//                Toast.makeText(getApplicationContext(), "file already exist", Toast.LENGTH_SHORT).show();
                FileOutputStream fileOutputStream = new FileOutputStream(file,true);
                OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
                writer.append(location.getLatitude()+","+location.getLongitude()+","+location.getAccuracy()+","+
                        location.getSpeed()+","+mAccX.getText()+","+mAccY.getText()+","+mAccZ.getText()
                        +","+System.currentTimeMillis()+"\n");
                writer.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*protected void startIntentService(Location location) {
        mAddressText.setText("Fetching address...");
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }*/

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();
//        mResultReceiver = new AddressResultReceiver(new Handler());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void checkPermissions() {
        // check permission
        int hasLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        // permission is not granted yet
        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
            // ask it -> a dialog will be opened
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            // permission is already granted, start get location information
            startGettingLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // accepted, start getting location information
                    startGettingLocation();
                } else {
                    // denied
                    Toast.makeText(this, "Location access denied by the user!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startGettingLocation() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000); // update period

        // now permission is granted, but we need to check it
        int hasLocationPermission = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // start requesting location changes
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }
/*
    // Receiver for data sent from FetchAddressIntentService
    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        // receives data sent from FetchAddressIntentService
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            mAddressText.setText("Address:"+resultData.getString(Constants.RESULT_DATA_KEY));
        }
    }*/

    /** Called when the user taps the Send button */
    public void ShowData(View view) {
        Intent intent = new Intent(this, PreviousRoutesActivity.class);
//        EditText editText = (EditText) findViewById(R.id.editText);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void StartRecording(View view){
        final int status =(Integer) view.getTag();
        if(status == 1) {
            Constants.RECORDING = 0;
            mBtnRecord.setText("Stop Recording");
            mBtnData.setClickable(false);
            view.setTag(0);
        } else {
            Constants.RECORDING = 1;
            mBtnData.setClickable(true);
            mBtnRecord.setText("Start Recording");
            view.setTag(1);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        mAccX.setText(""+sensorEvent.values[0]);
        mAccY.setText(""+sensorEvent.values[1]);
        mAccZ.setText(""+sensorEvent.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
