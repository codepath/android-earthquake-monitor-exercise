package com.facebook.earthquakemonitorexercise.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.*;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.facebook.earthquakemonitorexercise.app.R;
import com.facebook.earthquakemonitorexercise.app.models.Earthquake;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

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

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
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

    private void didUpdateData(final String jsonString) {
        // TODO: Deserialize the JSON response in order to construct an instance of the Earthquake class.
        // TODO: Move the camera so the earthquake locations are within the bounds of the screen.
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // TODO: Get the changed json string from SharedPreferences.
        // TODO: Call didUpdateData() so the latest markers can be adjusted on the screen.
    }

    // TODO: Define the onReceive callback to persist the json string when data is received from the IntentService.
}
