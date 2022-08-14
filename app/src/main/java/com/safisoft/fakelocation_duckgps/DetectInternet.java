package com.safisoft.fakelocation_duckgps;



import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.InetAddress;


public class DetectInternet {


        public static boolean isConnectedOld() {
            try {
                InetAddress ipAddr = InetAddress.getByName("google.com");
                return !ipAddr.equals("");
            } catch (Exception e) {
                return false;
            }
        }






}
