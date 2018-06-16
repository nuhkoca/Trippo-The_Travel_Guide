package com.nuhkoca.trippo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.nuhkoca.trippo.TrippoApp;

public class ConnectionUtil {

    public static boolean sniff() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) TrippoApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo != null &&
                networkInfo.isConnected() &&
                networkInfo.isConnectedOrConnecting() &&
                networkInfo.isAvailable();
    }
}