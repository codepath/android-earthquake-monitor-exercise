# Earthquake Monitor

## Overview

Adroid app to monitor earthquakes registered in the last hour by leveraging the API provided by [earthquake.usgs.gov site](http://earthquake.usgs.gov/). The app will plot locations of earthquakes on a map. It will also automatically update the map when a new earthquake takes place with the use of an `IntentService`.

To achieve this, we'll be performing the following steps:
* The app periodically runs an `IntentService` to get the latest earthquake data from the server.
* It then sends this data to the activity through a `Broadcast`.
* The callback in the activity saves this json data in `SharedPreferences`.
* To plot real time earthquake locations, the activity implements a `SharedPreferences.OnSharedPreferenceChangeListener` which then updates the markers on the map.

## Usage
This app is intended to be the base project on top of which new features can be added. To use it, clone the project and import it using the following steps:

![Imgur](http://i.imgur.com/x5iXb8Y.gif)

Once you have imported base app, go ahead and run it, and you should see the following output : 
  
![Imgur](http://i.imgur.com/eK7z5mSl.jpg)

Now we will go ahead and implement the exercise. At the end of exercise, final output will be 

![Imgur](http://i.imgur.com/qwXBYxzl.jpg)
