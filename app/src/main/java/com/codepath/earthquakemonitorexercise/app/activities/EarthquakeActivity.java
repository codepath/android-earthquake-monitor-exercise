package com.codepath.earthquakemonitorexercise.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.codepath.earthquakemonitorexercise.app.R;
import com.codepath.earthquakemonitorexercise.app.models.Earthquake;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonSyntaxException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

public class EarthquakeActivity extends FragmentActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    private LinkedHashMap<String, Earthquake.Feature> mFeatures = new LinkedHashMap();
    final LatLngBounds.Builder mBoundsBuilder = new LatLngBounds.Builder();
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        // TODO: Register for the particular broadcast based on ACTION string
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO: Unregister the listener when the application is paused
    }
    
    // TODO: Define the onReceive callback here to persist the json string when data is received from the IntentService.
    
    // When the preference value changes, redraw the map
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // TODO: Get the changed json string from SharedPreferences using `key`.
        // TODO: Call didUpdateData(String jsonString) to draw the new markers on the screen.
    }
    
    // Deserialize the json response and redraw the earthquake location accordingly
    private void didUpdateData(final String jsonString) {
        // Deserialize the JSON response in order to construct an instance of the Earthquake class.
        if (jsonString == null || jsonString.length() == 0) return;
        try {
            boolean updateMapCamera = mFeatures.size() == 0;
            // Construct the Earthquake object from jsonString
            final Earthquake earthquake = Earthquake.GSON.fromJson(jsonString, Earthquake.class);
            if (earthquake != null) {
                if (earthquake.metadata != null && earthquake.features != null) {
                    setTitle(earthquake.metadata.title + " (" + earthquake.metadata.count  + ")");
                    for (final Earthquake.Feature feature : earthquake.features) {
                        final String id = feature.id;
                        if (!mFeatures.containsValue(id)) {
                            mFeatures.put(id, feature);
                            addMarker(id);
                        }
                    }
                }
            }
            // Move the camera so the earthquake locations are within the bounds of the screen.
            if (updateMapCamera && mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBoundsBuilder.build(), (int) (50 * Resources.getSystem().getDisplayMetrics().density)));
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e. the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // If map does exist, configure the map type to HYBRID
            if (mMap != null) mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
    }

    // Add markers to the map and prepare info window.
    private void addMarker(final String id) {
        final Earthquake.Feature feature = mFeatures.get(id);
        if (feature != null && feature.geometry != null && feature.geometry.coordinates.size() >= 2) {
            final double lon = feature.geometry.coordinates.get(0);
            final double lat = feature.geometry.coordinates.get(1);
            final String title = feature.properties.title;
            final Date date = new Date(feature.properties.time);
            final String snippet = DATE_FORMAT.format(date);
            addMarker(title, snippet, feature.properties.mag, feature.properties.url, lat, lon);
        }
    }

    // This is where we add markers and info window to the map.
    private void addMarker(final String title, final String snippet, final double mag, final String url, final double lat, final double lon) {
        final GoogleMap map = mMap;
        final LatLng latlng = new LatLng(lat, lon);
        mBoundsBuilder.include(latlng);
        final Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_marker).copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(icon);
        Paint paint = new Paint();

        // Scale the color of the icon based on the magnitude value.
        // M 3.0 & above - RED
        // M 1.0 & below - YELLOW
        // Anything between 1.0 and 3.0 - Variations of ORANGE
        final StringBuilder colorString = new StringBuilder("#FF");
        // 00 = 3.0f & above, FF = 1.0f and below
        if (mag >= 3.0f) {
            colorString.append("00");
        } else if (mag <= 1.0f) {
            colorString.append("FF");
        } else {
            double normalized = 1.0f - ((mag - 1.0f) / 2);
            int scale = (int)(normalized * 255.0f);
            // java.util.Formatter syntax:
            // Flag '0' - The result will be zero-padded
            // Width - 2
            // Conversion 'X' - The result is formatted as a hexadecimal integer, uppercase
            colorString.append(String.format("%02X", scale));
        }
        colorString.append("00");
        int color = Color.parseColor(colorString.toString());
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(icon, 0f, 0f, paint);

        // Create the marker on the map
        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                .snippet(snippet.toString())
                .title(title)
                .position(latlng));

        // Open the earthquake summary web page in chrome.
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
