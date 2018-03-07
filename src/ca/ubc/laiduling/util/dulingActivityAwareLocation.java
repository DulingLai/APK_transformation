package ca.ubc.laiduling.util;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import com.google.android.gms.location.ActivityRecognitionClient;




import java.util.Timer;
import java.util.TimerTask;

public class dulingActivityAwareLocation extends Service implements LocationListener {

    protected static final String TAG = "DulingDelayedTask";

//    private static final long ACTIVITY_UPDATE_INTERVAL = 1000;
//    private final double MAX_ACCURACY = 7.8;
//
//    public long timeToNextUpdate = 0;
//    public long lastLocationTime = 0;
//    public long lastActivityTime = 0;
//    public double lastSpeed = 0;
//    public boolean activityReceived = false;
//    public double lastAccuracy = 0;
//    public double bestAccuracy = 999;
//    public int numOfUpdates = 0;

//    public boolean origLocationRequest = false;
//    public boolean transLocationRequest = false;

    private LocationManager DulingLocManager;
//    private ActivityRecognitionClient mActivityRecognitionClient;
//    private Intent activityRecognitionIntent;
//    private PendingIntent activityRecognitionPendingIntent;

    // timer for delayed task
//    private Timer timer = new Timer();
//    myTimer mTask = new myTimer();

//    private class myTimer extends TimerTask {
//        @Override
//        public void run() {
//            Log.v(TAG, "Timer expired, request for new location!");
//            requestLocation();
//        }
//    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate");
        // instance of location manager and activity recognition client.
        DulingLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        requestLocation();
//        mActivityRecognitionClient = new ActivityRecognitionClient(this);
//
//        // start activity recognition service
//        activityRecognitionIntent = new Intent(this, DulingActivityRecognition.class);
//        activityRecognitionPendingIntent = PendingIntent.getService(this, 0, activityRecognitionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        startService(activityRecognitionIntent);
//        mActivityRecognitionClient.requestActivityUpdates(ACTIVITY_UPDATE_INTERVAL, activityRecognitionPendingIntent);
//        Log.v(TAG, "Starting Activity Service with intent");
//
//        // register broadcast receivers for activity data
//        LocalBroadcastManager.getInstance(this).registerReceiver(DulingActivityDataReceiver, new IntentFilter("DulingActivityRecognition"));
//
//        // request for initial location (once)
//        checkActivityData();
    }

//    private void checkActivityData() {
//        // check if we have received activity data from activity recognition service
//        if (!activityReceived) {
//            originalLocationRequest();
//        } else {
//            checkUserActivity(lastSpeed);
//        }
//    }

//    private void checkUserActivity(double speed) {
//        if (speed == 1.39) {
//            Log.e(TAG, "request transformed location updates in checkUserActivity()");
//            requestTransformedLocationUpdates();
//        } else if (speed == 0) {
//            Log.e(TAG, "removing location updates in checkUserActivity()");
//            removeLocationUpdates();
//        } else {
//            Log.e(TAG, "request original location updates in checkUserActivity()");
//            originalLocationRequest();
//        }
//    }
//
//    private void originalLocationRequest() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        //TODO add intent handler to extract minTime and distance from intent
//        DulingLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//        origLocationRequest = true;
//    }
//
//    private void removeLocationUpdates() {
//        DulingLocManager.removeUpdates(DulingActivityAwareLocation.this);
//    }
//
//    private synchronized void requestTransformedLocationUpdates() {
//        if (lastAccuracy>0){
//            if (lastAccuracy > MAX_ACCURACY) {
//                lastAccuracy = MAX_ACCURACY;
//            }
//        } else {
//            lastAccuracy = MAX_ACCURACY;
//        }
//
//        // if we have requested original location update, remove it
//        if (origLocationRequest){
//            removeLocationUpdates();
//            origLocationRequest = false;
//        }
//
//        // if already exist a delayed task, remove it
//        if (mTask!=null){
//            mTask.cancel();
//            //debug
//            Log.e(TAG,"Scheduled Task Canceled");
//        }
//
//        // calculate when we should start to request for location: 2 seconds before we reach the end of accuracy circle
//        timeToNextUpdate = (long) (lastAccuracy/lastSpeed*1000 - 2000);
//
//        // debug
//        Log.e(TAG, "Accuracy is " + lastAccuracy + "m; " + String.valueOf(timeToNextUpdate) + " ms before next update");
//
//        // register a timer to fire the location update
//        mTask = new myTimer();
//        timer.schedule(mTask,timeToNextUpdate);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // stop activity recognition service
//        stopService(activityRecognitionIntent);
//        mActivityRecognitionClient.removeActivityUpdates(activityRecognitionPendingIntent);
//        Log.v(TAG,"Stop Activity Recognition.");
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(DulingActivityDataReceiver);
        DulingLocManager.removeUpdates(this);
    }

//    /*
//    BroadcastReceiver to receive activity recognition results
//     */
//    private BroadcastReceiver DulingActivityDataReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.e(TAG, "Activity Data Received in DelayedTask");
//
//            activityReceived = true;
//            lastSpeed = intent.getDoubleExtra("DulingSpeed",lastSpeed);
//            lastActivityTime = intent.getLongExtra("DulingActivityTimestamp", lastActivityTime);
//
//            checkUserActivity(lastSpeed);
//        }
//    };

    public void requestLocation() throws SecurityException {
        DulingLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60*1000, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "New Location Received!");
//        Intent locationIntent = new Intent("ca.ubc.duling.location");
//        locationIntent.putExtra("duling.location", location);
//        sendBroadcast(locationIntent);
//
//        if (transLocationRequest){
//            int MAX_NUM_UPDATES = 3;
//            if (numOfUpdates < MAX_NUM_UPDATES){
//                if (location.getAccuracy() < bestAccuracy){
//                    bestAccuracy = location.getAccuracy();
//                }
//                numOfUpdates++;
//            } else {
//                Log.v(TAG,"Max Number of Update received, remove updates");
//                removeLocationUpdates();
//                transLocationRequest = false;
//                numOfUpdates = 0;
//                lastAccuracy = bestAccuracy;
//                lastLocationTime = location.getTime();
//                bestAccuracy = 999;
//                checkActivityData();
//            }
//        } else {
//            // update the location accuracy and timestamp
//            lastAccuracy = location.getAccuracy();
//            lastLocationTime = location.getTime();
//            checkActivityData();
//        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
