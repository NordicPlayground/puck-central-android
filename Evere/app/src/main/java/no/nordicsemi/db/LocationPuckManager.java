package no.nordicsemi.db;

import android.content.Context;

import org.droidparts.persist.sql.EntityManager;
import org.droidparts.persist.sql.stmt.Is;
import org.droidparts.persist.sql.stmt.Where;

import no.nordicsemi.models.LocationPuck;

public class LocationPuckManager extends EntityManager<LocationPuck> {

    public static final String COLUMN_PROXIMITY_UUID = "proximityUUID";
    public static final String COLUMN_MAJOR = "major";
    public static final String COLUMN_MINOR = "minor";
    public static final String COLUMN_NAME = "name";

    public LocationPuckManager(Context ctx) {
        super(LocationPuck.class, ctx);
    }

    public boolean locationPuckExists(LocationPuck locationPuck) {
        Where identicalLocationPuck = new Where(COLUMN_PROXIMITY_UUID, Is.EQUAL,
                locationPuck.getProximityUUID())
                .and(COLUMN_MAJOR, Is.EQUAL, locationPuck.getMajor())
                .and(COLUMN_MINOR, Is.EQUAL, locationPuck.getMinor());
        return select().where(identicalLocationPuck).count() > 0;
    }
}
