package com.example.trashcanapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import android.view.View;
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private static  final String TAG = "MAP_TEST";
    private boolean isPermissionGranted = false;
    private boolean isCameraPermissionGranted = false;

    private static final int pic_id = 1;
    Button camera_open_id;
    ImageView click_image_id;

    // Location Finder Objects
    FusedLocationProviderClient fusedClient;
    private LocationRequest mRequest;
    private LocationCallback mCallback;
    private Location currentLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentLoc = new Location("");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        camera_open_id = (Button)findViewById(R.id.camera_button);
        click_image_id = (ImageView)findViewById(R.id.click_image);

        // get current location
        locationWizardry();
        // if needed
        openLocationServices();
        // check if GPS permission has been granted
        requestGPSPermission();


        if(isPermissionGranted){
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

    }

    // This method will help to retrieve the image
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        // Match the request 'pic id with requestCode
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pic_id) {

            // BitMap is data structure of image file
            // which store the image in memory
            Bitmap photo = (Bitmap) data.getExtras()
                    .get("data");

            // Set the image in imageview for display
            click_image_id.setImageBitmap(photo);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[0])==PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[1])==PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[2])==PackageManager.PERMISSION_GRANTED){
            isCameraPermissionGranted = true;
            allowCamera();
        }else{
            ActivityCompat.requestPermissions(MapsActivity.this, permissions, pic_id);
        }
    }

    private void allowCamera() {
        if(isCameraPermissionGranted){
            camera_open_id.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v)
                {
                    // Create the camera_intent ACTION_IMAGE_CAPTURE
                    // it will open the camera for capture the image
                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    // Start the activity with camera_intent,
                    // and request pic id
                    startActivityForResult(camera_intent, pic_id);
                }
            });
        }
    }

    private void requestGPSPermission() {
        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

        }else {
            isPermissionGranted = true;
        }
    }

    private void openLocationServices() {
        LocationManager lm = (LocationManager)MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(MapsActivity.this)
                    .setMessage(R.string.gps_network_not_enabled)
                    .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            MapsActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(R.string.Cancel,null)
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i(TAG, String.valueOf(requestCode));
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        mapFragment.getMapAsync(this);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                isPermissionGranted = true;
                return;
            }
        }

    }


    @SuppressLint("MissingPermission")
    private void locationWizardry() {
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        //Initially, get last known location. We can refine this estimate later
        fusedClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.i(TAG, location.toString());
                    currentLoc = location;
                }
            }
        });


        //now for receiving constant location updates:
        createLocRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mRequest);

        //This checks whether the GPS mode (high accuracy,battery saving, device only) is set appropriately for "mRequest". If the current settings cannot fulfil
        //mRequest(the Google Fused Location Provider determines these automatically), then we listen for failutes and show a dialog box for the user to easily
        //change these settings.
        SettingsClient client = LocationServices.getSettingsClient(MapsActivity.this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this, 500);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

        //actually start listening for updates: See on Resume(). It's done there so that conveniently we can stop listening in onPause
    }

    private void createLocRequest() {
        mRequest = new LocationRequest();
        mRequest.setInterval(10000);//time in ms; every ~10 seconds
        mRequest.setFastestInterval(5000);
        mRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                LatLng latLng = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("You Are Here!"));
                return false;
            }
        });
    }
}