package com.safisoft.fakelocation_duckgps;


import android.Manifest;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.safisoft.fakelocation_duckgps.Gps_Startup_Service.shouldContinue_Startup;
import static com.safisoft.fakelocation_duckgps.Gps_Startup_ForegroundService.shouldContinue_Startup_foreground;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;

    DbConnction myDbHelper;
    Cursor c = null;
    ImageButton start_but, stop_but, satview_but, mapview_but, zoomin_but, zoomout_but, settings_but, zoomallout_but, streetview_but, save_favorite_but, view_favorite_but;
    ImageView arrow_down, arrow_up;
    AdView chooseadview;
    InterstitialAd mInterstitialAd ;
    ScrollView scrool_option;
    CameraPosition cameraPosition;
    SaveLocationCustomDialogClass cdd;
    ViewLocationCustomDialogClass viewdialog;
    SettingsDialog settingsDialog;
    User_Guide user_guide ;
    int step = 0;
    String lat;
    String lng;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        myDbHelper = new DbConnction(MapsActivity.this);
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




        start_but = (ImageButton)findViewById(R.id.start_but);
        stop_but= (ImageButton)findViewById(R.id.stop_but);
        satview_but= (ImageButton)findViewById(R.id.satview_but);
        mapview_but= (ImageButton)findViewById(R.id.mapview_but);
        zoomin_but= (ImageButton)findViewById(R.id.zoomin_but);
        zoomout_but= (ImageButton)findViewById(R.id.zoomout_but);
        zoomallout_but= (ImageButton)findViewById(R.id.zoomallout_but);
        settings_but= (ImageButton)findViewById(R.id.settings_but);
        streetview_but= (ImageButton)findViewById(R.id.streetview_but);
        save_favorite_but= (ImageButton)findViewById(R.id.save_favorite_but);
        view_favorite_but= (ImageButton)findViewById(R.id.view_favorite_but);
        scrool_option = (ScrollView)findViewById(R.id.scrool_option) ;
        arrow_up = (ImageView)findViewById(R.id.arrow_up);
        arrow_down = (ImageView)findViewById(R.id.arrow_down);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
       // ed_tx_search =(EditText)findViewById(R.id.ed_tx_search);

        arrow_down.setVisibility(View.INVISIBLE);







        MobileAds.initialize(this, "ca-app-pub-5637187199850424~2526704880");//real ad
        //MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");//test add
        chooseadview = (AdView) findViewById(R.id.chooseadView);
        AdRequest adRequest = new AdRequest.Builder().build();
        chooseadview.loadAd(adRequest);


        mInterstitialAd = new InterstitialAd(this);
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");//test ad
        mInterstitialAd.setAdUnitId("ca-app-pub-5637187199850424/3733806316");//real ad
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrool_option.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    arrow_down.setVisibility(View.VISIBLE);
                    arrow_up.setVisibility(View.VISIBLE);

                    if(!scrool_option.canScrollVertically(1)){
                        arrow_down.setVisibility(View.VISIBLE);
                        arrow_up.setVisibility(View.INVISIBLE);
                        }
                    if(!scrool_option.canScrollVertically(-1)){
                        arrow_down.setVisibility(View.INVISIBLE);
                        arrow_up.setVisibility(View.VISIBLE);
                        }
                }
            });
        }
        else {arrow_down.setVisibility(View.VISIBLE);
            arrow_up.setVisibility(View.VISIBLE);}




        chooseadview.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                chooseadview.setVisibility(View.VISIBLE);
            }
        });

        settings_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsDialog=new SettingsDialog(MapsActivity.this);
                settingsDialog.show();
                settingsDialog.setCanceledOnTouchOutside(false);
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
                viewdialog=new ViewLocationCustomDialogClass(MapsActivity.this);
                viewdialog.show();
                viewdialog.setCanceledOnTouchOutside(false);

                viewdialog.simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        TextView tv_locationName = (TextView) view.findViewById(R.id.textv_locationName);

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
                            Toast.makeText(MapsActivity.this, "Select Location to GO", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            cameraPosition = new CameraPosition.Builder().target(new LatLng(lat_move_double, lon_move_double))
                                    .zoom(15).bearing(360).tilt(1).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            location_name = "";
                            viewdialog.dismiss();
                        }


                    }
                });

                viewdialog.del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(location_name == null || location_name == "")
                        {
                            Toast.makeText(MapsActivity.this, "Select Location to Delete", Toast.LENGTH_SHORT).show();
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
                    }
                });




            }
        });

        save_favorite_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdd=new SaveLocationCustomDialogClass(MapsActivity.this);
                cdd.show();
                cdd.setCanceledOnTouchOutside(false);
                lat = Double.toString(map_fake_lat);
                lng = Double.toString(map_fake_long);
                cdd.Longitude_txv.setText(lng);
                cdd.Latitude_txv.setText(lat);

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
                                Toast.makeText(MapsActivity.this, "Insert name for this Location", Toast.LENGTH_SHORT).show();
                            }
                            if(!locname.equals("")) {

                                myDbHelper.row_query("saved_location","location_name",locname);
                                int count = cdd.cursor.getCount();
                                for (int i = 0 ; i < count ; i++) {
                                    cdd.cursor.moveToPosition(i);
                                    String get_poss_name = cdd.cursor.getString(1);
                                    if (locname.equals(get_poss_name))
                                    {
                                        Toast.makeText(getApplicationContext(),"Name already excesses",Toast.LENGTH_SHORT).show();
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
                                myDbHelper.insertRecord(locname, lat, lng);
                                step = 0;
                                cdd.dismiss();
                                Toast.makeText(MapsActivity.this,"saved", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                cdd.no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        step = 0 ;
                        cdd.dismiss();
                    }
                });
            }
        });


        mapview_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                        Notificaton_Start();
                    }
                    vibe.vibrate(100);
                    Toast.makeText(getApplicationContext(), "Fake Location SET", Toast.LENGTH_SHORT).show();
                    String s_map_fake_lat = Double.toString(map_fake_lat);
                    String s_map_fake_long = Double.toString(map_fake_long);

                    myDbHelper.updateRecord("fake_location", "fake_lat", "1", s_map_fake_lat);
                    myDbHelper.updateRecord("fake_location", "fake_long", "1", s_map_fake_long);
                    shouldContinue_Startup = true;
                    shouldContinue_Startup_foreground = true;

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        Intent serviceIntent = new Intent(MapsActivity.this, Gps_Startup_ForegroundService.class);
                        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
                        ContextCompat.startForegroundService(MapsActivity.this, serviceIntent);
                    }else {
                        startService(new Intent(MapsActivity.this, Gps_Startup_Service.class));
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "please enable mock locations first", Toast.LENGTH_LONG).show();

                    user_guide = new User_Guide(MapsActivity.this);
                    user_guide.show();
                    user_guide.setCanceledOnTouchOutside(false);
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
                                Toast.makeText(getApplicationContext(),"please enable developer mode manually",Toast.LENGTH_LONG).show();
                            }


                        }
                    });

                }

            }
        });

        stop_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Intent serviceIntent = new Intent(MapsActivity.this, Gps_Startup_ForegroundService.class);
                    stopService(serviceIntent);
                    shouldContinue_Startup_foreground = false;
                }

                stopService(new Intent(MapsActivity.this, Gps_Startup_Service.class));
                shouldContinue_Startup = false;
                vibe.vibrate(100);
                Toast.makeText(MapsActivity.this, "Fake Location STOP", Toast.LENGTH_SHORT).show();
                NotificationManager notificationManager = (NotificationManager)MapsActivity.this.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(1);
                notificationManager.cancelAll();

            }
        });

    }


   /* @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1 : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
*/
    /*private void init(){
        ed_tx_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||event.getAction() == KeyEvent.ACTION_DOWN ||event.getAction() == KeyEvent.KEYCODE_ENTER){
                    String address = ed_tx_search.getText().toString();
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    List<Address> List = new ArrayList<>();

                    try {
                        geocoder.getFromLocationName(address,1);

                    }
                    catch (Exception e)
                    {}

                    if(List.size() > 0){
                        Address address1 = List.get(0);
                        Toast.makeText(getApplicationContext(),address1.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

    }
*/

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


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(database_lat,database_long), 13));
        cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(database_lat,database_long))      // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
                .bearing(360)                // Sets the orientation of the camera to wast
                .tilt(1)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                 map_fake_lat = cameraPosition.target.latitude ;
                 map_fake_long =cameraPosition.target.longitude ;
            }
        });
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {}
        });
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {}
        });
    }



    public void Notificaton_Start() {
        Intent intent = getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.safisoft.fakelocation_duckgps");
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri uri = Uri.parse("android.resource://" +getApplicationContext().getPackageName() + "/" + R.raw.quack);
            NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel("1", "Start Notification",NotificationManager.IMPORTANCE_HIGH);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
               // notificationChannel.setSound(uri, null);
                builder = builder.setContentIntent(pendingIntent)
                       // .setSound(uri)
                        .setOngoing(true)
                        .setSmallIcon(R.drawable.seekduck2)
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
                    .setSmallIcon(R.drawable.seekduck2)
                    .setOngoing(true)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentTitle("Duck Fake Location is Running")
                    .setContentText("Touch to open")
                    .setPriority(Notification.PRIORITY_MAX);
            notificationManager.notify(0, builder_repet.build());
        }
    }


   @Override
   public void onResume(){
        super.onResume();

    }




























}
