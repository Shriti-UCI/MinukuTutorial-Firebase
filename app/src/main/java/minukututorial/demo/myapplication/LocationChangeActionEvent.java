package minukututorial.demo.myapplication;

import java.util.List;

import edu.umich.si.inteco.minukucore.event.ActionEvent;
import edu.umich.si.inteco.minukucore.model.DataRecord;

/**
 * Created by shriti on 5/31/17.
 */

public class LocationChangeActionEvent extends ActionEvent {

    public LocationChangeActionEvent(String typeOfEvent, List<DataRecord> dataRecords) {
        super(typeOfEvent, dataRecords);
    }
}
