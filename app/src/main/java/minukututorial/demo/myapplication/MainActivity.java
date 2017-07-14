package minukututorial.demo.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import edu.umich.si.inteco.minuku.config.Constants;
import edu.umich.si.inteco.minuku.config.UserPreferences;
import edu.umich.si.inteco.minuku.dao.LocationDataRecordDAO;
import edu.umich.si.inteco.minuku.manager.MinukuDAOManager;
import edu.umich.si.inteco.minuku.manager.MinukuNotificationManager;
import edu.umich.si.inteco.minuku.manager.MinukuSituationManager;
import edu.umich.si.inteco.minuku.manager.MinukuStreamManager;
import edu.umich.si.inteco.minuku.model.LocationDataRecord;
import edu.umich.si.inteco.minuku.model.MoodDataRecord;
import edu.umich.si.inteco.minuku.streamgenerator.LocationStreamGenerator;
import edu.umich.si.inteco.minukucore.event.StateChangeEvent;
import edu.umich.si.inteco.minukucore.exception.StreamNotFoundException;

/**
 * Created by shriti on 5/31/17.
 *
 */

public class MainActivity extends AppCompatActivity{
    //LocationDataRecordDAO mDAO;
    TextView latitude;
    TextView longitude;
    List<LocationDataRecord> currentLocationList;
    LocationDataRecord currentLocation;
    public static final String TAG = "MainActivity";

    private void initialize() {
        Log.d(TAG, "Initializing inside main activity.");
        MinukuDAOManager daoManager = MinukuDAOManager.getInstance();
        //For location
        LocationDataRecordDAO locationDataRecordDAO = new LocationDataRecordDAO();
        daoManager.registerDaoFor(LocationDataRecord.class, locationDataRecordDAO);

        //initialize the stream generator so that it gets registered in the system
        LocationStreamGenerator locationStreamGenerator =
                new LocationStreamGenerator(getApplicationContext());
        //
        MinukuSituationManager situationManager = MinukuSituationManager.getInstance();
        //
        LocationChangeSituation locationChangeSituation = new LocationChangeSituation();
        LocationChangeAction locationChangeAction = new LocationChangeAction();
    }

    public static final String encodeEmail(String unencodedEmail) {
        if (unencodedEmail == null) return null;
        return unencodedEmail.replace(".", ",");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Constants.getInstance().hasFirebaseUrlBeenSet()) {
            Log.d(TAG, "Updating firebase url.");
            Constants.getInstance().setFirebaseUrl("https://minukututorial.firebaseio.com");
        }

        if (UserPreferences.getInstance().getPreference(Constants.ID_SHAREDPREF_EMAIL) == null) {

            String email = "coolluke@gmail.com";
            UserPreferences.getInstance().writePreference(Constants.ID_SHAREDPREF_EMAIL, email);
            UserPreferences.getInstance().writePreference(Constants.KEY_ENCODED_EMAIL,
                    encodeEmail(email));
        }


        Log.d(TAG, "MainActivity onCreate called..");
        setContentView(R.layout.activity_main);

        //EventBus.getDefault().register(this);

        initialize();
        Log.d(TAG, "MainActivity initalized.");
        startService(new Intent(getBaseContext(), BackgroundService.class));
        Log.d(TAG, "Backgroundservice started.");
        //
        startService(new Intent(getBaseContext(), MinukuNotificationManager.class));
        Log.d(TAG, "MinukuNotificationManager started.");
        Log.d(TAG, "Firebase URL is " + Constants.getInstance().getFirebaseUrl());

        latitude = (TextView) findViewById(R.id.current_latitude);
        longitude = (TextView) findViewById(R.id.current_longitude);

        refreshLocation();
    }

    //Location stream generator creates an event when location changes
    //this is a handler for that event
    //it updates the location text in the activity layout, that is when location changes
    //it also creates a state change event for a LocationChangeSituation to be triggered
    /*@org.greenrobot.eventbus.Subscribe
    public void onLocationDataChangeEvent(LocationDataRecord d) {
        Log.d(TAG, "Got new location. Updating");
        latitude.setText(String.valueOf(d.getLatitude()));
        longitude.setText(String.valueOf(d.getLongitude()));
        //generate handle state change event here
        StateChangeEvent locationStateChangeEvent = new StateChangeEvent(LocationDataRecord.class);
        Log.d(TAG, "prepared a state change event to trigger a situation check");
        MinukuStreamManager.getInstance().handleStateChangeEvent(locationStateChangeEvent);
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "activity resuming now");
        refreshLocation();
    }
    private void refreshLocation(){
        try {
            Log.d(TAG, "Trying to get current location.");
            currentLocation =
                    MinukuStreamManager.getInstance().getStreamFor(LocationDataRecord.class).getCurrentValue();
        } catch (StreamNotFoundException e) {
            e.printStackTrace();
        }

        if(currentLocation!= null) {
            Log.d(TAG, "Updating UI with current location.");
            latitude.setText(String.valueOf(currentLocation.getLatitude()));
            longitude.setText(String.valueOf(currentLocation.getLongitude()));
        }
        else {
            Log.d(TAG, "Current location is null.");
            latitude.setText("unknown");
            longitude.setText("unknown");
        }
    }

    /*
    It works with this, there is not event bus registration in stream manager
    StreamGenerators are calling the event handler in stream manager
    So we had to handle state change event here in main activity - this works, situation gets called
    Change: need to have stream manager register to event bus,
    and remove the explicit call to handler in stream generators,
    instead just generate and event and post it on the event bus
    @Subscribe
    public void handleStateChange(StateChangeEvent event) {
        MinukuStreamManager.getInstance().handleStateChangeEvent(event);    }*/

    /**
     * Now testing after changing the current stream manager, event bus registration
     * This is not working, private constructor, need to see how to access "this"
     */

}
