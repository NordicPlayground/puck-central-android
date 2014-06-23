package no.nordicsemi.db;

import android.content.Context;

import org.droidparts.persist.sql.EntityManager;
import org.droidparts.persist.sql.stmt.Is;
import org.droidparts.persist.sql.stmt.Where;

import no.nordicsemi.models.LocationPuck;

public class LocationPuckManager extends EntityManager<LocationPuck> {

    public LocationPuckManager(Context ctx) {
        super(LocationPuck.class, ctx);
    }

    public boolean locationPuckExists(LocationPuck locationPuck) {
        Where identicalLocationPuck = new Where("minor", Is.EQUAL, locationPuck.getMinor())
                .and("major", Is.EQUAL, locationPuck.getMajor())
                .and("proximityUUID", Is.EQUAL, locationPuck.getProximityUUID());
        return select().where(identicalLocationPuck).count() > 0;
    }
}
