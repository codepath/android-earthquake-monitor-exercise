package com.codepath.earthquakemonitorexercise.app.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {
    static final String TAG = "Utils";

    // Get json from URL
    public static String getJSON(String url) {
        // Set up HTTP GET
        HttpURLConnection conn = null;
        try {
            // Declare a URL Connection
            URL u = new URL(url);
            conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-length", "0");
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            // Open InputStream to connection
            conn.connect();
            int status = conn.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    // Read content from the resource pointed by the connection URL.
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    // Convert response to string using String Builder
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(TAG).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TAG).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(TAG).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }
}
