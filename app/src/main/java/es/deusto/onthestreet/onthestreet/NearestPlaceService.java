package es.deusto.onthestreet.onthestreet;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * This service looks for the nearest place every minute and displays a notification when the latter changes.
 */

public class NearestPlaceService extends Service implements SensorEventListener {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null;}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Display a notification or so...
        Log.i("MYLOG", "THE SERVICE HAS STARTED!!!!!!");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("MYLOG", "THE SERVICE HAS STOPPED!");
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
