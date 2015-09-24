package com.codepath.earthquakemonitorexercise.app.models;

import com.google.gson.Gson;

import java.util.ArrayList;

// Model earthquake class to contain the json returned by the API call.
public class Earthquake {
    public static final Gson GSON = new Gson();
    public Metadata metadata;
    public static class Metadata {
        public long generated;
        public String title;
        public int count;
    }

    public ArrayList<Feature> features;
    public static class Feature {
        public String type;
        public String id;
        public Properties properties;
        public static class Properties {
            public double mag;
            public String title;
            public long time;
            public String url;
        }
        public Geometry geometry;
        public static class Geometry {
            public ArrayList<Double> coordinates;
        }
    }
}
