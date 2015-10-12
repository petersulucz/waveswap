package com.example.braeden.waveswap_app;

import android.util.Log;

/**
 * Created by peter on 10/7/2015.
 */
public class CaptainsLog {

    private static final String TAG = "Waveswap";

    /**
     * Log some sort of important, but like not that important stuff
     * @param info the stuff to log
     */
    public static void Info(String info){
        Log.i(TAG, info);
    }
}
