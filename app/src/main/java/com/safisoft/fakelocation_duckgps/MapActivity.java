package com.safisoft.fakelocation_duckgps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;


import static android.content.ContentValues.TAG;
import static com.safisoft.fakelocation_duckgps.Gps_Startup_Service.shouldContinue_Startup;
import static com.safisoft.fakelocation_duckgps.Gps_Startup_ForegroundService.shouldContinue_Startup_foreground;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationClient;
    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 12345;
    private boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    View map;
    double lat = 0;
    double lng = 0;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    LinearLayout lay_click_info;
    TextView txt_lay_info;
    ImageView imgv_info ;


    DbConnction myDbHelper;
    Cursor c = null;
    ImageButton start_but, stop_but, satview_but, mapview_but, zoomin_but, zoomout_but, settings_but, zoomallout_but, streetview_but, darkmode_but , save_favorite_but, view_favorite_but , info_but ;
    ImageButton btn_search ;
    ImageView arrow_down, arrow_up;
    AdView chooseadview;
    ScrollView scrool_option;
    CameraPosition cameraPosition;
    SaveLocationCustomDialogClass cdd;
    ViewLocationCustomDialogClass viewdialog;
    SettingsDialog settingsDialog;
    User_Guide user_guide ;
    InfoCustomDialogClass infoCustomDialogClass ;
    int step = 0;
    String lat_String;
    String lng_String;
    String location_name;
    double map_fake_long;
    double map_fake_lat;
    double database_lat;
    double database_long;
    double lat_move_double;
    double lon_move_double;
    boolean exist ;
    Vibrator vibe;
    EditText ed_tx_search ;
    TextView txtv_lat , txtv_lng ;

    private AppUpdateManager appUpdateManager;
    private static final int IMMEDIATE_APP_UPDATE_REQ_CODE = 124 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        map = findViewById(R.id.map);


        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        checkUpdate();



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);








        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLocationPermission();


        myDbHelper = new DbConnction(MapActivity.this);
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }


        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }


        c = myDbHelper.query_user_data("fake_location", null, null, null, null, null, null);
        c.moveToPosition(0);



        String fake_lat = c.getString(1);
        String fake_long = c.getString(2);

        database_lat = Double.parseDouble(fake_lat);
        database_long = Double.parseDouble(fake_long);


        start_but = findViewById(R.id.start_but);
        stop_but= findViewById(R.id.stop_but);
        satview_but= findViewById(R.id.satview_but);
        mapview_but= findViewById(R.id.mapview_but);
        zoomin_but= findViewById(R.id.zoomin_but);
        zoomout_but= findViewById(R.id.zoomout_but);
        zoomallout_but= findViewById(R.id.zoomallout_but);
        settings_but= findViewById(R.id.settings_but);
        streetview_but= findViewById(R.id.streetview_but);
        save_favorite_but= findViewById(R.id.save_favorite_but);
        view_favorite_but= findViewById(R.id.view_favorite_but);
        scrool_option = findViewById(R.id.scrool_option) ;
        arrow_up = findViewById(R.id.arrow_up);
        arrow_down = findViewById(R.id.arrow_down);
        btn_search = findViewById(R.id.btn_search);
        txtv_lat =findViewById(R.id.txtv_lat);
        txtv_lng = findViewById(R.id.txtv_lng);
        txt_lay_info = findViewById(R.id.txt_lay_info);
        lay_click_info = findViewById(R.id.lay_click_info);
        imgv_info = findViewById(R.id.imgv_info);
        darkmode_but = findViewById(R.id.darkmode_but);
        info_but = findViewById(R.id.info_but);



        settings_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsDialog=new SettingsDialog(MapActivity.this);
                settingsDialog.show();
                settingsDialog.setCanceledOnTouchOutside(false);
                settingsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                settingsDialog.btn_go_settings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    }
                });

                settingsDialog.btn_go_dev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));

                    }
                });
            }
        });

        satview_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        streetview_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPosition = new CameraPosition.Builder().target(new LatLng(database_lat, database_long))
                        .zoom(15).bearing(180).tilt(90).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        view_favorite_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewdialog=new ViewLocationCustomDialogClass(MapActivity.this);
                viewdialog.show();
                viewdialog.setCanceledOnTouchOutside(false);
                viewdialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                viewdialog.simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        TextView tv_locationName = view.findViewById(R.id.textv_locationName);

                        String lat ;
                        String lon ;

                        location_name = tv_locationName.getText().toString();

                        if(tv_locationName != null) {
                            myDbHelper.row_query("saved_location","location_name",location_name);
                            viewdialog.cursor.moveToPosition(i);
                            lat= viewdialog.cursor.getString(2);
                            lon= viewdialog.cursor.getString(3);

                            lat_move_double = Double.parseDouble(lat);
                            lon_move_double = Double.parseDouble(lon);

                            String latlon = "lat "+lat+"  "+"lon "+lon;
                            viewdialog.textv_showselctedName.setText(location_name);
                            viewdialog.textv_showselctedLatLong.setText(latlon);

                            viewdialog.liner_selected_data.setVisibility(View.VISIBLE);
                        }
                    }
                });

                viewdialog.go.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(location_name == null || location_name == "")
                        {
                            Toast.makeText(MapActivity.this, "Select Location to GO", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            cameraPosition = new CameraPosition.Builder().target(new LatLng(lat_move_double, lon_move_double))
                                    .zoom(15).bearing(360).tilt(1).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            location_name = "";
                            viewdialog.dismiss();
                            int min = 2*1000;
                            new CountDownTimer(min, 1000) {
                                public void onTick(long millisUntilFinished) { }
                                public void onFinish() {
                                    StartInAppReview();
                                }
                            }.start();
                        }
                    }
                });

                viewdialog.del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(location_name == null || location_name == "")
                        {
                            Toast.makeText(MapActivity.this, "Select Location to Delete", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            myDbHelper.deleteRecordAlternate("saved_location", "location_name", location_name);
                            viewdialog.review();
                            location_name = "";
                        }
                    }
                });

                viewdialog.ex.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        location_name = "";
                        viewdialog.dismiss();
                        int min = 2*1000;
                        new CountDownTimer(min, 1000) {
                            public void onTick(long millisUntilFinished) { }
                            public void onFinish() {
                                StartInAppReview();
                            }
                        }.start();
                    }
                });
            }
        });

        save_favorite_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cdd=new SaveLocationCustomDialogClass(MapActivity.this);
                cdd.show();
                cdd.setCanceledOnTouchOutside(false);
                cdd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                lat_String = Double.toString(map_fake_lat);
                lng_String = Double.toString(map_fake_long);
                cdd.Longitude_txv.setText(lng_String);
                cdd.Latitude_txv.setText(lat_String);

                cdd.yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(step == 0){
                            cdd.layout_locname.setVisibility(View.VISIBLE);
                            step = 1 ;
                        }
                        else if(step == 1) {
                            String locname = cdd.location_name_edtx.getText().toString();
                            if(locname.equals("")){
                                Toast.makeText(MapActivity.this, "Insert Name For This Location", Toast.LENGTH_SHORT).show();
                            }
                            if(!locname.equals("")) {

                                myDbHelper.row_query("saved_location","location_name",locname);
                                int count = cdd.cursor.getCount();
                                for (int i = 0 ; i < count ; i++) {
                                    cdd.cursor.moveToPosition(i);
                                    String get_poss_name = cdd.cursor.getString(1);
                                    if (locname.equals(get_poss_name))
                                    {
                                        Toast.makeText(getApplicationContext(),"Location Name is Already in Use Try Another One",Toast.LENGTH_SHORT).show();
                                        exist = true;
                                        break;
                                    }
                                    else {
                                        exist = false ;
                                    }

                                    cdd.cursor.moveToNext();
                                }
                            }
                            if (!exist && !locname.equals("") ) {
                                myDbHelper.insertRecord(locname, lat_String, lng_String);
                                step = 0;
                                cdd.dismiss();
                                show_info_lay("Location saved",R.drawable.ic_btn_save_location);
                                int min = 2*1000;
                                new CountDownTimer(min, 1000) {
                                    public void onTick(long millisUntilFinished) { }
                                    public void onFinish() {
                                        StartInAppReview();
                                    }
                                }.start();
                            }
                        }
                    }
                });
                cdd.no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        step = 0 ;
                        cdd.dismiss();
                        int min = 2*1000;
                        new CountDownTimer(min, 1000) {
                            public void onTick(long millisUntilFinished) { }
                            public void onFinish() {
                                StartInAppReview();
                            }
                        }.start();
                    }
                });
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLocation();

            }
        });


        mapview_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapActivity.this, R.raw.map_standerd_mood_style));

            }
        });

        darkmode_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                 mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapActivity.this, R.raw.map_night_mood_style)); // map night mood
            }
        });

        zoomin_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        zoomout_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });

        zoomallout_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cameraPosition = new CameraPosition.Builder().target(new LatLng(database_lat,database_long))
                        .zoom(1).bearing(360).tilt(40).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        start_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMockLocationEnabledForAndroidHiLivel()) {

                    int min = 2*1000;
                    new CountDownTimer(min, 1000) {
                        public void onTick(long millisUntilFinished) { }
                        public void onFinish() {
                            StartInAppReview();
                        }
                    }.start();

                  if (mInterstitialAd != null) {
                //      mInterstitialAd.show(MapActivity.this);
                  }

                  if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                      Notificaton_Start();
                  }

                  show_info_lay("Fake Location START",R.drawable.ic_btn_start);
                  String s_map_fake_lat = Double.toString(map_fake_lat);
                  String s_map_fake_long = Double.toString(map_fake_long);

                  myDbHelper.updateRecord("fake_location", "fake_lat", "1", s_map_fake_lat);
                  myDbHelper.updateRecord("fake_location", "fake_long", "1", s_map_fake_long);
                  shouldContinue_Startup = true;
                  shouldContinue_Startup_foreground = true;

                  if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                      Intent serviceIntent = new Intent(MapActivity.this, Gps_Startup_ForegroundService.class);
                      serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
                      ContextCompat.startForegroundService(MapActivity.this, serviceIntent);
                  }else {
                      startService(new Intent(MapActivity.this, Gps_Startup_Service.class));
                  }

                } else {

                    show_info_lay("Please Enable Mock Locations First",R.drawable.ic_btn_info);
                    user_guide = new User_Guide(MapActivity.this);
                    user_guide.show();
                    user_guide.setCanceledOnTouchOutside(false);
                    user_guide.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    user_guide.btn_go_settings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                            user_guide.dismiss();
                        }
                    });

                    user_guide.btn_go_dev.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try{
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                                user_guide.dismiss();
                            }catch (Exception e){
                               // Toast.makeText(getApplicationContext(),"please enable developer mode manually",Toast.LENGTH_LONG).show();
                                show_info_lay("Please Enable Developer Mode Manually",R.drawable.ic_btn_info);
                            }

                        }
                    });

                }

            }
        });

        stop_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // InterstitialAd.load(this,"ca-app-pub-5637187199850424/3733806316", adRequest,          // real code
                // InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,          // test code
                InterstitialAd.load(MapActivity.this,"ca-app-pub-5637187199850424/3733806316", adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                mInterstitialAd = interstitialAd;
                            }
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                mInterstitialAd = null;
                            }
                            });
                StartInAppReview();
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MapActivity.this);
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Intent serviceIntent = new Intent(MapActivity.this, Gps_Startup_ForegroundService.class);
                    stopService(serviceIntent);
                    shouldContinue_Startup_foreground = false;
                }

                stopService(new Intent(MapActivity.this, Gps_Startup_Service.class));
                shouldContinue_Startup = false;
                show_info_lay("Fake Location STOP",R.drawable.ic_btn_stop);
                NotificationManager notificationManager = (NotificationManager)MapActivity.this.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(1);
                notificationManager.cancelAll();
            }
        });

        info_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                infoCustomDialogClass = new InfoCustomDialogClass(MapActivity.this);
                infoCustomDialogClass.show();
                infoCustomDialogClass.setCanceledOnTouchOutside(false);
                infoCustomDialogClass.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            }
        });

    }





    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        try {
            final ViewGroup parent = (ViewGroup) map.findViewWithTag("GoogleMapMyLocationButton").getParent();
            parent.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Resources r = getResources();
                        //convert our dp margin into pixels
                        int marginPixels = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11, r.getDisplayMetrics());
                        // Get the map compass view
                        View mapCompass = parent.getChildAt(4);

                        // create layoutParams, giving it our wanted width and height(important, by default the width is "match parent")
                        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(mapCompass.getHeight(),mapCompass.getHeight());
                        // position on top right
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        //give compass margin
                        rlp.setMargins(marginPixels, marginPixels, marginPixels, marginPixels);
                        mapCompass.setLayoutParams(rlp);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }




        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                map.getParent().requestDisallowInterceptTouchEvent(false);
                map_fake_lat = googleMap.getCameraPosition().target.latitude;
                map_fake_long = googleMap.getCameraPosition().target.longitude;

               txtv_lat.setText(String.valueOf(map_fake_lat));
               txtv_lng.setText(String.valueOf(map_fake_long));
            }
        });

        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                map.getParent().requestDisallowInterceptTouchEvent(true);
            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if(location == null){
                            cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(database_lat,database_long))
                                    .zoom(13)
                                    .bearing(360)
                                    .tilt(1)
                                    .build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            show_info_lay("Fake Location Duck Can't Found Your Real Location",R.drawable.ic_btn_info);

                            new CountDownTimer(5000, 1000) {
                                public void onTick(long millisUntilFinished) {}
                                public void onFinish() {show_info_lay("This is The Last Known Location",R.drawable.ic_btn_info);}}.start();
                        }

                        if (location != null) {
                            cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(),location.getLongitude()))
                                    .zoom(13)
                                    .bearing(360)
                                    .tilt(1)
                                    .build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            show_info_lay("Fake Location Duck Found Your Real Location",R.drawable.ic_btn_info);

                        }
                    }
                });



    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void getLocationPermission(){
        String [] permissions={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted=true;
            } else {
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else {
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    public boolean isMockLocationEnabledForAndroidHiLivel() {
        boolean isMockLocation = false;
        try {
            //if marshmallow
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AppOpsManager opsManager = (AppOpsManager) getApplicationContext().getSystemService(Context.APP_OPS_SERVICE);

                isMockLocation = (opsManager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), BuildConfig.APPLICATION_ID) == AppOpsManager.MODE_ALLOWED);
            } else {
                // in marshmallow this will always return true
                isMockLocation = !android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), "mock_location").equals("0");
            }
        } catch (Exception e) {
            return isMockLocation;
        }
        return isMockLocation;
    }


    public void Notificaton_Start() {
        Intent intent = getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.safisoft.fakelocation_duckgps");
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //    Uri uri = Uri.parse("android.resource://" +getApplicationContext().getPackageName() + "/" + R.raw.quack);
            NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel("1", "Start Notification",NotificationManager.IMPORTANCE_HIGH);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
                // notificationChannel.setSound(uri, null);
                builder = builder.setContentIntent(pendingIntent)
                        // .setSound(uri)
                        .setOngoing(true)
                        .setSmallIcon(R.drawable.ic_notifacation)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .setContentTitle("Duck Fake Location is Running")
                        .setContentText("Touch to open")
                        .setChannelId("1");
                notificationManager = getSystemService(NotificationManager.class);
                notificationManager.notify(1, builder.build());
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder builder_repet = new NotificationCompat.Builder(getApplicationContext())
                    .setContentIntent(pendingIntent)
                    //   .setSound(uri)
                    .setSmallIcon(R.drawable.ic_notifacation)
                    .setOngoing(true)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentTitle("Duck Fake Location is Running")
                    .setContentText("Touch to open")
                    .setPriority(Notification.PRIORITY_MAX);
            notificationManager.notify(0, builder_repet.build());
        }
    }


    public void searchLocation() {
            Geocoder geocoder = new Geocoder(this);
            EditText edtxt_locationSearch = findViewById(R.id.edtxt_locationSearch);
            List<Address> addressList;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if(isConnected()) {
            if (Geocoder.isPresent()) {
                String location = edtxt_locationSearch.getText().toString();
                try {
                    addressList = geocoder.getFromLocationName(location, 1);
                    if (addressList.size() > 0) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        // mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(address.getLatitude(), address.getLongitude()))      // Sets the center of the map to location user
                                .zoom(13)                   // Sets the zoom
                                .bearing(360)                // Sets the orientation of the camera to wast
                                .tilt(1)                   // Sets the tilt of the camera to 30 degrees
                                .build();                   // Creates a CameraPosition from the builder
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    } else {
                        //   Toast.makeText(getApplicationContext(), "can't find this Address", Toast.LENGTH_LONG).show();
                        show_info_lay("Can't Find This Address", R.drawable.ic_btn_info);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                show_info_lay("Not Supported on Your Device", R.drawable.ic_btn_info);
            }
        }
        else {
            show_info_lay("Search for Location Needs Internet Connection", R.drawable.ic_btn_info);
        }
    }


    public void show_info_lay (String Show_Text , int Image_info){

        lay_click_info.setVisibility(View.VISIBLE);
        txt_lay_info.setText(Show_Text);
        imgv_info.setBackgroundResource(Image_info);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(3000);
        fadeOut.setDuration(1000);
        lay_click_info.startAnimation(fadeOut);
        lay_click_info.setVisibility(View.INVISIBLE);

    }


    public boolean isConnected() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    public void StartInAppReview(){

        ReviewManager manager = ReviewManagerFactory.create(MapActivity.this);

        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            try {
                if (task.isSuccessful()) {
                    // We can get the ReviewInfo object
                    ReviewInfo reviewInfo = task.getResult();
                    Task<Void> flow = manager.launchReviewFlow(MapActivity.this, reviewInfo);
                    flow.addOnCompleteListener(task2 -> {
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                        //   Toast.makeText(getApplicationContext(), "In-app review returned.", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // There was some problem, continue regardless of the result.
                    // goToAppPage(activity);
                }
            } catch (Exception ex) {
                //   Toast.makeText(getApplicationContext(), "Exception from openReview():", Toast.LENGTH_SHORT).show();
            }
        });

    }



    //in appp update code
    private void checkUpdate() {

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                startUpdateFlow(appUpdateInfo);
            } else if  (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                startUpdateFlow(appUpdateInfo);
            }
        });
    }

    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, MapActivity.IMMEDIATE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMMEDIATE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
             //   Toast.makeText(getApplicationContext(), "Update canceled by user! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_OK) {
             //   Toast.makeText(getApplicationContext(), "Update success! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            } else {
              //  Toast.makeText(getApplicationContext(), "Update Failed! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
                checkUpdate();
            }
        }
    }

    //in appp update code




    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
      //  if (doubleBackToExitPressedOnce) {
        //    Intent intent = new Intent(QRmakerActivity.this, StartActivity.class);
         //   startActivity(intent);
       //     finish();
        //    return;
      //  }
     //   Switch_Visibility();
     //   this.doubleBackToExitPressedOnce = true;

      //  show_info_lay("Press Twice to Scan");
   }
}