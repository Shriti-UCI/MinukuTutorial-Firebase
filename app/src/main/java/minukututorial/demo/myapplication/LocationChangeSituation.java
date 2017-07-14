package minukututorial.demo.myapplication;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.umich.si.inteco.minuku.manager.MinukuSituationManager;
import edu.umich.si.inteco.minuku.model.LocationDataRecord;
import edu.umich.si.inteco.minukucore.event.ActionEvent;
import edu.umich.si.inteco.minukucore.event.MinukuEvent;
import edu.umich.si.inteco.minukucore.event.StateChangeEvent;
import edu.umich.si.inteco.minukucore.exception.DataRecordTypeNotFound;
import edu.umich.si.inteco.minukucore.model.DataRecord;
import edu.umich.si.inteco.minukucore.model.StreamSnapshot;
import edu.umich.si.inteco.minukucore.situation.Situation;

/**
 * Created by shriti on 5/31/17.
 */

public class LocationChangeSituation implements Situation {

    private String TAG = "LocationChangeSituation";
    public LocationChangeSituation() {
        try {
            MinukuSituationManager.getInstance().register(this);
            Log.d(TAG, "Registered situation LocationChangeSituation");
        } catch (DataRecordTypeNotFound dataRecordTypeNotFound) {
            Log.d(TAG, "Registration failed for LocationChangeSituation");
            dataRecordTypeNotFound.printStackTrace();
        }
    }

    @Override
    public <T extends ActionEvent> T assertSituation(StreamSnapshot streamSnapshot, MinukuEvent minukuEvent) {
        if(minukuEvent instanceof StateChangeEvent) {
            List<DataRecord> dataRecords = new ArrayList<>();
            Log.d(TAG, "LocationChangeSituation has received a state change event from main activity");
            if(shouldShowNotification(streamSnapshot)){
                return (T) new LocationChangeActionEvent("LOCATION_CHANGE_SITUATION", dataRecords);
            }
        }
        Log.d(TAG, "Situation is returning null. No notification will be generated");
        return null;
    }

    @Override
    public List<Class<? extends DataRecord>> dependsOnDataRecordType() throws DataRecordTypeNotFound {
        List<Class<? extends  DataRecord>> dependsOn = new ArrayList<>();
        dependsOn.add(LocationDataRecord.class);
        return dependsOn;
    }

    private boolean shouldShowNotification(StreamSnapshot streamSnapshot){
        LocationDataRecord currentLocationDataRecord = streamSnapshot.getCurrentValue(LocationDataRecord.class);
        LocationDataRecord previousLocationLocationDataRecord = streamSnapshot.getPreviousValue(LocationDataRecord.class);
        if(currentLocationDataRecord == null || previousLocationLocationDataRecord == null) {
            Log.d(TAG, "One of the locations is null");
            return false;
        }
        Location currentLocation = new Location("");
        currentLocation.setLatitude(currentLocationDataRecord.getLatitude());
        currentLocation.setLongitude(currentLocationDataRecord.getLongitude());

        Location previousLocation = new Location("");
        previousLocation.setLatitude(previousLocationLocationDataRecord.getLatitude());
        previousLocation.setLongitude(previousLocationLocationDataRecord.getLongitude());

        float distanceInMeters = currentLocation.distanceTo(previousLocation);
        Log.d(TAG, String.valueOf(distanceInMeters));
        if(distanceInMeters == 0.0) {
            Log.d(TAG, "distance between two locations is 0 meters. Returning true");
            return true;
        }
        else {
            Log.d(TAG, "distance between two locations is not 0 meters. Returning false");
            return false;
        }
    }
}

