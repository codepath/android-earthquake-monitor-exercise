package com.facebook.earthquakemonitorexercise.app.services;

import android.app.IntentService;
import android.content.Intent;

// Service to run every minute and poll the earthquake database for new activities.
public class EarthquakeService extends IntentService {
    static final String TAG = "EarthquakeService";
    private final String url = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";
    public static final String KEY_RESULTS = "KeyResults";
    public static final String KEY_RESULT_CODE = "KeyResultCode";
    public static final String ACTION = "com.facebook.earthquakemonitorexercise.earthquakeservice";

    public EarthquakeService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO: Get json from the  earthquake data returned by the url
        // TODO: Construct an intent and fire a broadcast to send the json string to the activity.
    }
}
