package ca.ubc.laiduling.util;

import android.util.Log;

public class DulingUtils {
    public static void logLocationRequests(String className){
        Log.d("duling", "Location Requested at: "+className);
    }

    public static void printStackTrace(){
        StackTraceElement[] stackElements = new Throwable().getStackTrace();
        if(stackElements != null){
            Log.d("duling", "+++++++++++++++Stack Start++++++++++++++++");
            for(int i=0; i<stackElements.length; i++){
                Log.d("duling", stackElements[i].toString());
            }
            Log.d("duling", "+++++++++++++++Stack End++++++++++++++++");
        }
    }
}

