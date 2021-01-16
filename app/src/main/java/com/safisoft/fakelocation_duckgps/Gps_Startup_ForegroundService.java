package com.safisoft.fakelocation_duckgps;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;

import static android.app.Service.START_NOT_STICKY;


public class Gps_Startup_ForegroundService extends Service {
    public static final String CHANNEL_ID = "Start Notification 1";

    public static volatile boolean shouldContinue_Startup_foreground = true;
    private FusedLocationProviderClient mFusedLocationClient;


    Handler handler;
    Cursor c = null;
    DbConnction myDbHelper;
    LocationManager locMgr;
    int int_accuracy ;

    @Override
    public void onCreate() {
        super.onCreate();


        Uri uri = Uri.parse("android.resource://" +getApplicationContext().getPackageName() + "/" + R.raw.quack);
       // String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                //  .setSound(uri)
                .setOngoing(true)
                .setSmallIcon(R.drawable.seekduck2)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setContentTitle("Duck Fake Location is Running")
                .setContentText("Touch to open")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        //do heavy work on a background thread
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        myDbHelper = new DbConnction(Gps_Startup_ForegroundService.this);
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


        handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                SetSameLocation();
                handler.postDelayed(this, int_accuracy);

                if (!shouldContinue_Startup_foreground){
                    handler.removeCallbacksAndMessages(null);
                    stopForegroundService();


                }
            }
        }, int_accuracy);




    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }



    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,
                    "Foreground Service Channel", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    @Override
    public void onDestroy() {

        if (shouldContinue_Startup_foreground) {
            Intent Start = new Intent(Gps_Startup_ForegroundService.this, RestartService_BroadcastReceiver.class);
            sendBroadcast(Start);
        }


    }



    public void stopForegroundService()
    {
        // Stop foreground service and remove the notification.
        stopForeground(true);
        // Stop the foreground service.
        stopSelf();
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void SetSameLocation() {

        c = myDbHelper.query_user_data("fake_location", null, null, null, null, null, null);
        c.moveToPosition(0);


        String lati = c.getString(1);
        String longi = c.getString(2);

        double lati_d = Double.parseDouble(lati);
        double longi_d = Double.parseDouble(longi);

        setMock(lati_d, longi_d, LocationManager.GPS_PROVIDER);
        setMock(lati_d, longi_d, LocationManager.NETWORK_PROVIDER);


        c = myDbHelper.query_user_data("settings_accuracy", null, null, null, null, null, null);
        c.moveToPosition(0);
        String s_accuracy = c.getString(1);
        int_accuracy = Integer.parseInt(s_accuracy);

        try {
            c.close();
        } finally {
            // this gets called even if there is an exception somewhere above
            if(c != null)
                c.close();
        }


    }


    private void setMock(double latitude, double longitude, String provider) {

      //  locMgr = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
     //   locMgr.addTestProvider(provider, false, false,
     //           false, false, true, true, true, 0, 5);
        Location newLocation = new Location(provider);

        newLocation.setLatitude(latitude);
        newLocation.setLongitude(longitude);
        newLocation.setAccuracy(0);
        newLocation.setAltitude(0);
        newLocation.setTime(System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            newLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }

    //    locMgr.setTestProviderEnabled(provider, true);

    //    locMgr.setTestProviderStatus(provider, LocationProvider.AVAILABLE, null, System.currentTimeMillis());

     //   locMgr.setTestProviderLocation(provider, newLocation);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.setMockMode(true);
        mFusedLocationClient.setMockLocation(newLocation);



    }









}