package no.nordicsemi.db;

public class DB {

    public interface Column extends org.droidparts.annotation.sql.Column {

        String PROXIMITY_UUID = "proximityUUID";
        String MAJOR = "major";
        String MINOR = "minor";
        String NAME = "name";
        String SERVICE_UUIDS = "serviceUUIDs";

        String PUCK = "puck";
        String TRIGGER = "trigger";
        String ACTIONS = "actions";
        String ADDRESS = "address";
    }
}
