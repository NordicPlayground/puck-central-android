package no.nordicsemi.db;

import android.content.Context;
import android.location.Location;

import org.droidparts.persist.sql.EntityManager;

import no.nordicsemi.models.LocationPuck;

public class LocationPuckManager extends EntityManager<LocationPuck> {

    public LocationPuckManager(Context ctx) {
        super(LocationPuck.class, ctx);
    }
}
