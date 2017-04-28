package es.deusto.onthestreet.onthestreet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.LocationListener;

import java.util.ArrayList;

/**
 * This service looks for the nearest place every minute and displays a notification when the latter changes.
 */

public class NearestPlaceService extends Service implements LocationListener {

    private LocationManager lm;
    private ArrayList<Place> arraylPlaces;
    private long waiting_interval = 60; // in seconds!!
    private float minDistance = 20f; // Minimum distance between location updates, in meters
    private double nearestPlaceLongitude = 0;
    private double nearestPlaceLatitude = 0;
    private double distanceToPlace = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null;}

    @SuppressWarnings("MissingPermission") // TODO: Maybe I should implement a proper way of checking permission (in the future)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { // No need to access intent for the moment.
        // Display a notification or so...
        Log.i("MYLOG", "NearestPlace SERVICE HAS STARTED!!!!!!");

        // Activate location gathering to check for the nearest place:
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Every minute the app will check whether the nearest place/location has changed, looking among the nearest places and choosing the nearest one for comparison.
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,waiting_interval*1000,minDistance, (android.location.LocationListener) this); // elapsed time should be specified in milliseconds

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("MYLOG", "NearestPlace SERVICE HAS STOPPED!");
        lm.removeUpdates((android.location.LocationListener)this);
        super.onDestroy();
    }

    /**
     * Looks the nearest place based on user's location
     * @return the Place which is nearest.
     */
    public Place getNearestPlace(Location location) {
        // First of all, the nearest distance limit value set by the user is retrieved:
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        float shortestDistance = Float.valueOf(sharedPref.getString("locationDistance", "200")); // This refers to the radius distance where the nearest place can be
        Place nearestPlace = null;
        for (Place p : ListPlacesActivity.arraylPlaces_backup) { // The list of places is directly obtained from ListPlacesActivity
            if (p.getLongitude() != 0 && p.getLatitude() != 0) {
                Location tmp_lo = new Location("");
                tmp_lo.setLongitude(p.getLongitude());
                tmp_lo.setLatitude(p.getLatitude());
                if (location.distanceTo(tmp_lo) <= shortestDistance) {
                    nearestPlace = new Place(p.getName(),p.getNeighborhood(),p.getDescription(),p.getlContacts(),p.getLongitude(),p.getLatitude());
                    distanceToPlace = location.distanceTo(tmp_lo);
                }
            }
        }
        return nearestPlace;
    }

    /**
     * What to do when user's location has changed. Now, it is necessary to check whether the user is still next to previous
     * nearest place or the latter has changed.
     * @param location user's current location
     */
    @Override
    public void onLocationChanged(Location location) {
        // The nearest Place is obtained
        Place tmpPl = getNearestPlace(location);
        // Obtain its Longitude and Latitude values. Depending on whether they are different from the previous nearest place,
        // then, we should display a notification.
        if (tmpPl != null) {
            if (tmpPl.getLongitude() != nearestPlaceLongitude || tmpPl.getLatitude() != nearestPlaceLatitude) {
                // The nearest place changed!!
                // So, we need to notify the user:
                    // Creating the notification:
                    NotificationCompat.Builder nBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.ic_location_drop_on_black_24dp)
                                    .setContentTitle("You are next to one of your favourite places!")
                                    .setContentText(tmpPl.getName() + " is approx. " + distanceToPlace + "m from you! Would you fancy visiting? :)");
                    Notification notif = nBuilder.build();

                    // Displaying the notification:
                    NotificationManager mNotificationManager =
                            (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(0, notif);

                // Assigning (updating) now the new coordinate values for the place:
                nearestPlaceLongitude = tmpPl.getLongitude();
                nearestPlaceLatitude = tmpPl.getLatitude();
            }
        }
    }

    /**
     * Removes the notification with id 0 from the notification area
     * @param context current context
     */
    private void removeNotification(Context context){
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);
    }
}
