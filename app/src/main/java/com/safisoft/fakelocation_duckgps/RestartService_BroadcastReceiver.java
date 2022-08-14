package com.safisoft.fakelocation_duckgps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class RestartService_BroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, Gps_Startup_Service.class));
        } else {
            context.startService(new Intent(context, Gps_Startup_Service.class));
        }

    }
}
