package minukututorial.demo.myapplication;

import android.app.Application;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import edu.umich.si.inteco.minukucore.event.ShowNotificationEvent;
import edu.umich.si.inteco.minukucore.event.ShowNotificationEventBuilder;

/**
 * Created by shriti on 5/31/17.
 */

public class LocationChangeAction {

    public LocationChangeAction() {
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void handleLocationChangeEvent(LocationChangeActionEvent locationChangeActionEvent) {
        Log.d("LocationChangeAction", "Handling location change action event");
        EventBus.getDefault().post(
                new ShowNotificationEventBuilder()
                .setExpirationAction(ShowNotificationEvent.ExpirationAction.DISMISS)
                .setExpirationTimeSeconds(60)
                .setViewToShow(MainActivity.class)
                        .setIconID(R.drawable.cast_ic_notification_small_icon)
                .setTitle("Pinggg!")
                .setMessage("You haven't moved at all.")
                .setCategory("")
                .setParams(new HashMap<String, String>())
                .createShowNotificationEvent()
        );
    }
}
