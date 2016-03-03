package it.jaschke.alexandria.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import it.jaschke.alexandria.R;

/**
 * Created by Hari Nivas Kumar R P on 3/4/2016.
 */
public class Utility {

    public static boolean isOfflineModeNotifiedToUser = false;

    // Added from StackOverFlow
    public static boolean isNetworkAvailable(Context context, View snackBarView, String logTag) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            isOfflineModeNotifiedToUser = false;
            return true;
        }
        Log.e(logTag, "No Internet Connection available.!");
        if ((snackBarView != null) &&
                !isOfflineModeNotifiedToUser){
            Snackbar.make(snackBarView, R.string.you_are_offline, Snackbar.LENGTH_SHORT).show();
            isOfflineModeNotifiedToUser = true;
        }
        return false;
    }
}
