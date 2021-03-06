package com.example.kevin.watsoninnovation;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;

import com.facebook.login.LoginManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.premnirmal.Magnet.Magnet;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.Map;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MapsActivity extends FragmentActivity implements ServiceConnection, GoogleMap.OnMarkerClickListener,OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation = new Location("");
    LatLng mSelectedDestination;
    View mapView;
    Button goToCurrentChallengeButton;
    Button start_quest_btn;
    ImageButton openDrawerButton;
    DrawerLayout mDrawerLayout;
    Button start_navigation_btn;
    Toolbar sliderToolbar;
    SlidingUpPanelLayout sliding_layout;
    Toolbar toolbar_navigation;
    //Firebase objects
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    final int REQUEST_CODE = 2300;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("/project/quests/");
    TextView text_view_description_content;
    TextView text_view_places_content;
    TextView text_view_teaser_content;
    TextView navigation_drawer_logout;
    FloatingActionButton chatbutton;

    Map<String,Object> questsHashMap;
    Map<String,Marker> gMapsMarkerMap;

    MyService.IconService iconService;
    boolean isBound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mLastLocation.setLatitude(52.370216);//your coords of course
        mLastLocation.setLongitude(4.895168);




        myRef = database.getReference("/project/quests/");

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        mapView = mapFrag.getView();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        goToCurrentChallengeButton = findViewById(R.id.goToCurrentChallengeButton);
        openDrawerButton = findViewById(R.id.menuDrawerButton);
        mDrawerLayout =  findViewById(R.id.drawer_layout);
        sliderToolbar = findViewById(R.id.my_toolbar);
        sliding_layout = findViewById(R.id.sliding_layout);
        toolbar_navigation =  findViewById(R.id.toolbar_navigation);
        start_navigation_btn = findViewById(R.id.start_navigation_btn);
        start_quest_btn = findViewById(R.id.start_quest_btn);
        text_view_description_content = findViewById(R.id.text_view_description_content);
        text_view_places_content = findViewById(R.id.text_view_places_content);
        text_view_teaser_content = findViewById(R.id.text_view_teaser_content);
        navigation_drawer_logout = findViewById(R.id.navigation_drawer_logout);
        chatbutton = findViewById(R.id.chatbutton);


        openDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        goToCurrentChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((MyApplication) getApplication()).isQuestRunning()){
                    DBQuest questToStart = ((MyApplication) getApplication()).getDbQuestMap().get(((MyApplication) getApplication()).getCurrentQuestKey());
                    String type = questToStart.getType();
                    if(type.equals("photo")){
                        Intent intent = new Intent(getApplicationContext(), PhotoChallengeActivity.class);
                        String message = "";
                        intent.putExtra(EXTRA_MESSAGE, message);
                        startActivity(intent);

                    }


                }

            }
        });

        start_navigation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSelectedDestination!=null) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+mSelectedDestination.latitude+","+mSelectedDestination.longitude+"&mode=w");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            }
        });
        start_quest_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    DBQuest questToStart = ((MyApplication) getApplication()).getDbQuestMap().get(((MyApplication) getApplication()).getCurrentQuestKey());
                    String type = questToStart.getType();
                    if(type.equals("photo")){
                        ((MyApplication) getApplication()).setRunningQuest(((MyApplication) getApplication()).getCurrentQuestKey());
                        Bundle params = new Bundle();
                        params.putString("quest_name", questToStart.getTitle());
                        mFirebaseAnalytics.logEvent("quest_started", params);

                        goToCurrentChallengeButton.setText("Current Challenge: "+questToStart.getTitle());
                        Intent intent = new Intent(getApplicationContext(), PhotoChallengeActivity.class);
                        String message = "";
                        intent.putExtra(EXTRA_MESSAGE, message);
                        startActivity(intent);
                        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        ((MyApplication) getApplication()).setQuestRunning(true);
                    } else {
                        ((MyApplication) getApplication()).setQuestRunning(false);
                    }




            }
        });
        toolbar_navigation.setTitle("Settings");
        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        myRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        Log.w("TAG","AUFGERUFEN");
                        questsHashMap  = ((Map<String,Object>) dataSnapshot.getValue());
                        if(mGoogleMap!=null){
                            clearMap();
                            addMarker(questsHashMap);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
        navigation_drawer_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), R.string.signed_out, Toast.LENGTH_LONG).show();
                // Check if user is signed in (non-null) and update UI accordingly.
                FirebaseUser currentUser = mAuth.getCurrentUser();
                updateUI(currentUser);
            }
        });
        chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(intent);
            }
        });


        gMapsMarkerMap = new HashMap<String,Marker>();




    }
    @Override
    public void onBackPressed() {
        // INTENT FOR YOUR HOME ACTIVITY
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);

            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
        FusedLocationProviderClient mFusedLocationClient;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
                        }
                    }
                });
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }
        googleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mSelectedDestination = null;
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                sliderToolbar.setTitle("");
            }
        });
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));


        //move map camera
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        double dest_lat = 0;
        double des_lon = 0;
        //checkAndLaunch();
        for (Map.Entry<String, Marker> tmpMarkerEntry : gMapsMarkerMap.entrySet()){
            if(marker.equals(tmpMarkerEntry.getValue())){
                Marker tmpMarker = tmpMarkerEntry.getValue();
                LatLng position = tmpMarker.getPosition();
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                mSelectedDestination = new LatLng(position.latitude, position.longitude);
                sliderToolbar.setTitle(tmpMarker.getTitle());
                dest_lat = mSelectedDestination.latitude;
                des_lon = mSelectedDestination.longitude;

                String key = tmpMarkerEntry.getKey();
                DBQuest tempDBQuest = ((MyApplication) getApplication()).getDbQuestMap().get(key);
                text_view_description_content.setText(tempDBQuest.getDescription());
                text_view_places_content.setText(tempDBQuest.getPlaces());
                text_view_teaser_content.setText(tempDBQuest.getTeaser());
                ((MyApplication) getApplication()).setCurrentQuestKey(key);
            }
        }
        if(((MyApplication) getApplication()).isQuestRunning()){
            start_quest_btn.setEnabled(false);
            start_quest_btn.setClickable(false);
            start_navigation_btn.setEnabled(false);
            start_navigation_btn.setClickable(false);
            return false;
        }

        if(distance(dest_lat, des_lon, mLastLocation.getLatitude(),mLastLocation.getLongitude())>1){
            start_navigation_btn.setEnabled(true);
            start_navigation_btn.setClickable(true);
            start_quest_btn.setEnabled(false);
            start_quest_btn.setClickable(false);
        }
        else{
            start_navigation_btn.setEnabled(false);
            start_quest_btn.setEnabled(true);
            start_quest_btn.setClickable(true);
            start_navigation_btn.setClickable(false);
        }
        return false;
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public void clearMap(){
        mGoogleMap.clear();
    }

    public void addMarker(Map<String, Object> tempMap){
        Marker tempMarker;
        mGoogleMap.clear();
        for (Map.Entry<String, Object> entry : tempMap.entrySet()){
            String key = entry.getKey();
            Map singleChallenge = (Map) entry.getValue();
            String description = singleChallenge.get("description").toString();
            String places = singleChallenge.get("places").toString();
            String teaser = singleChallenge.get("teaser").toString();
            String title = singleChallenge.get("title").toString();
            long time = (long)singleChallenge.get("time");
            double lat = (double)singleChallenge.get("lat");
            double lon = (double)singleChallenge.get("lon");
            String type = singleChallenge.get("type").toString();
            String neededClass = "";
            if(type=="photo"){
                neededClass = singleChallenge.get("needed_class").toString();
            }

            DBQuest tempDBQuest = new DBQuest(description,places,teaser,title,time,lat,lon,type, neededClass);
            Map<String,DBQuest> tmpMap = ((MyApplication) getApplication()).getDbQuestMap();
            if(tmpMap==null)
                tmpMap = new HashMap<String, DBQuest>();
            tmpMap.put(key,tempDBQuest);
            ((MyApplication) getApplication()).setDbQuestMap(tmpMap);
            tempMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat,lon))
                    .title(title));
            gMapsMarkerMap.put(key,tempMarker);
            Log.w("addMarker",key+ ": "+lat);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mGoogleMap!=null) {
            clearMap();
            addMarker(questsHashMap);
        }
        if(!((MyApplication) getApplication()).isQuestRunning()){
            goToCurrentChallengeButton.setVisibility(View.INVISIBLE);
        }else {
            goToCurrentChallengeButton.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    public void updateUI(FirebaseUser user){
        if(user==null) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            LoginManager.getInstance().logOut();
            finish();
        }
    }
    void checkAndLaunch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                launchService();
            }
        } else {
            launchService();
        }
    }

    void launchService() {
        final Intent intent = new Intent(this, MyService.class);
        startService(intent);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
        isBound = true;

        iconService.startMagnet();
                /*
        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                iconService.startMagnet();
            }
        });
        findViewById(R.id.stop_button).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                iconService.stopMagnet();
            }
        });*/
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    launchService();
                } else {
                    new AlertDialog.Builder(this).setTitle("Permission")
                            .setMessage("Need Permission")
                            .setCancelable(false)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    finish();
                                }
                            })
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialogInterface, int i) {
                                    checkAndLaunch();
                                }
                            })
                            .show();
                }
            }
        }
    }
    @Override public void onServiceConnected(ComponentName className, IBinder binder) {
        iconService = (MyService.IconService) binder;
    }

    @Override public void onServiceDisconnected(ComponentName className) {
        iconService = null;
    }
}
