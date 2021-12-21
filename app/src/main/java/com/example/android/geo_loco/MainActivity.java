package com.example.android.geo_loco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    final int Access_location_request_code = 10001001;
    final int Access_Background_request_code = 10002;
    private static final String TAG = "AskPermission";
    FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;

    ArrayList<ClassRoom_Location>arrayList_of_geofenceLatlng;

    /*double Fence1_Latitude = 29.9262893;
    double Fence1_Longitude =  77.5179372;
    String ClassRoomNumber1 = "Operating Systems Lab";

    double Fence2_Latitude= 28.680847;
    double Fence2_Longitude =  77.0552608;
    String ClassRoomNumber2 = "Computer Organization & Architecture";

    double Fence3_Latitude= 19.1146788;
    double Fence3_Longitude =  72.8854589;
    String ClassRoomNumber3 = "Object Oriented Programming Using Java";*/




    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private TextView textViewLat;
    private TextView textViewLong;
    public  static Button updateButton;
    private String variableLatitude;
    private String variableLongitude;
    private UserLocation mUserLocation;

    private GeofenceHelper geofenceHelper;
    private GeofencingClient geofencingClient;
    private float GEOFENCE_RADIUS = 200;
    public static final String[] UserEnroll = new String[1];


    private void getUserDetail() {
        if (mUserLocation == null) {
            mUserLocation = new UserLocation();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("registeredUsers");
            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "User Data Retreived", Toast.LENGTH_SHORT).show();
                        DataSnapshot snapshot = task.getResult();

                        String textFullName = String.valueOf(snapshot.child("textFullName").getValue());
                        String enroll = String.valueOf(snapshot.child("textEnrollment").getValue());
                        String dob = String.valueOf(snapshot.child("textDob").getValue());
                        String gender = String.valueOf(snapshot.child("textGender").getValue());
                        String batch = String.valueOf(snapshot.child("textBatch").getValue());


                        ReadWriteUserDetail user = new ReadWriteUserDetail(textFullName, enroll, dob, gender, batch);
                        mUserLocation.setUserLatitude(variableLatitude.toString());
                        mUserLocation.setUserLongitude(variableLongitude.toString());
                        mUserLocation.setUserDetail(user);
                        saveUSerLocation();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to user Info!", Toast.LENGTH_LONG).show();
                    }

                }
            });
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserLocation");
            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userLatitude").setValue(variableLatitude);
            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userLongitude").setValue(variableLongitude);
            //saveUSerLocation();
        }
    }

    private void saveUSerLocation() {
        if (mUserLocation != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserLocation");
            ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "User Location Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to add", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            //super.onLocationResult(locationResult);
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                //Log.d(TAG, "onLocation Result" + location.toString());
                variableLatitude = Double.toString(location.getLatitude());
                variableLongitude = Double.toString(location.getLongitude());

                textViewLat.setText(Double.toString(location.getLatitude()));
                textViewLong.setText(Double.toString(location.getLongitude()));
            }

        }
    };
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.home:
                        //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        //overridePendingTransition(0,0 );
                        return true;
                    case R.id.schedule:
                        startActivity(new Intent(getApplicationContext(), Schedule.class));
                        overridePendingTransition(0,0 );

                        finish();
              return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        overridePendingTransition(0,0 );

                        finish();

                        return true;
                }
                return false;
            }
        });



        //
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("registeredUsers");
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();

                    String user_name = String.valueOf(dataSnapshot.child("textFullName").getValue());
                    UserEnroll[0] = String.valueOf(dataSnapshot.child("textEnrollment").getValue());
                    Objects.requireNonNull(getSupportActionBar()).setTitle(user_name);
                }
            }
        });


        textViewLat = findViewById(R.id.textView2);
        textViewLong = findViewById(R.id.textView3);
        updateButton = findViewById(R.id.buttonview);
        updateButton.setVisibility(View.GONE);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserDetail();



                Toast.makeText(MainActivity.this, "Attendance Marked!", Toast.LENGTH_SHORT).show();
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        locationRequest = LocationRequest.create();

        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Location");


        geofencingClient = LocationServices.getGeofencingClient(MainActivity.this);
        geofenceHelper = new GeofenceHelper(MainActivity.this);

        arrayList_of_geofenceLatlng=new ArrayList<>();
        arrayList_of_geofenceLatlng.add(new ClassRoom_Location(29.9262893,77.5179372,"Operating Systems Lab"));
        arrayList_of_geofenceLatlng.add(new ClassRoom_Location(28.680847,77.0552608,"Computer Organization & Architecture"));
        arrayList_of_geofenceLatlng.add(new ClassRoom_Location(19.1146788,72.8854589,"Object Oriented Programming Using Java"));
        arrayList_of_geofenceLatlng.add(new ClassRoom_Location(26.8531697,80.9136721,"Logical Reasoning & Inequality"));

        if (Build.VERSION.SDK_INT >= 29) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                for(ClassRoom_Location classRoom_location:arrayList_of_geofenceLatlng){
                    addGeofence(classRoom_location.ClassName,classRoom_location.Fence_Latitude,classRoom_location.Fence_Longitude);
                }
                /*addGeofence(ClassRoomNumber1,Fence1_Latitude,Fence1_Longitude);
                addGeofence(ClassRoomNumber2,Fence2_Latitude,Fence2_Longitude);
                addGeofence(ClassRoomNumber3,Fence3_Latitude,Fence3_Longitude);*/
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {

                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Access_Background_request_code);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Access_Background_request_code);
                }
            }

        } else {
            for(ClassRoom_Location classRoom_location:arrayList_of_geofenceLatlng){
                addGeofence(classRoom_location.ClassName,classRoom_location.Fence_Latitude,classRoom_location.Fence_Longitude);
            }
            /*addGeofence(ClassRoomNumber1,Fence1_Latitude,Fence1_Longitude);
            addGeofence(ClassRoomNumber2,Fence2_Latitude,Fence2_Longitude);
            addGeofence(ClassRoomNumber3,Fence3_Latitude,Fence3_Longitude);*/
        }



    }



    protected void onStart() {

        super.onStart();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //getLastLocation();
            checkSettingsAndStartLocationUpdate();
        } else {
            askLocationPermission();
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdate();
    }

    private void checkSettingsAndStartLocationUpdate() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        Task<LocationSettingsResponse> locationSettingsResponseTask = LocationServices.getSettingsClient(MainActivity.this).checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //settings are satisfied and we can start location update
                startLocationUpdates();
            }
        });

        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(MainActivity.this, 1001);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }
            }
        });
    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }


    private void stopLocationUpdate() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "Ask Permission, You should show an alert Dialog!");
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Access_location_request_code);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Access_location_request_code);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Access_location_request_code) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //request granted

                checkSettingsAndStartLocationUpdate();
            } else {

            }
        }
        if (requestCode == Access_Background_request_code) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //request granted
                Toast.makeText(this, "BACKGROUND PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                //show dialog box
                showSettingsDialog();
                Toast.makeText(this, "BACKGROUND LOCATION PERMISSION IS REQUIRED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void  showSettingsDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs Background permissions to use this feature. You can grant them app settings");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                openSettings();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void openSettings(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    private void addGeofence(String ClassroomNum, double Fence_Latitude, double Fence_Longitude) {
        Log.d(TAG, "addGeofence() called.."+ClassroomNum);
        LatLng latLng = new LatLng(Fence_Latitude, Fence_Longitude);
        //UUID.randomUUID().toString()
        Geofence geofence = geofenceHelper.getGeofence(ClassroomNum, latLng, GEOFENCE_RADIUS, Geofence.GEOFENCE_TRANSITION_DWELL);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        //geofenceHelper.sendClassroomVal(ClassroomNum);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_BACKGROUND_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Access_Background_request_code);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Access_Background_request_code);
            }
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: Geofence Added...");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                String error = geofenceHelper.getErrorString(e);

                Log.d(TAG, "OnFailure " + error);
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }


}