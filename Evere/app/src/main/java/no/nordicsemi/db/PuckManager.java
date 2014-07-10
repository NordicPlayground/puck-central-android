package no.nordicsemi.db;

import android.content.Context;

import com.radiusnetworks.ibeacon.IBeacon;

import org.droidparts.persist.sql.EntityManager;
import org.droidparts.persist.sql.stmt.Is;
import org.droidparts.persist.sql.stmt.Select;
import org.droidparts.persist.sql.stmt.Where;

import java.util.ArrayList;

import no.nordicsemi.models.Puck;

public class PuckManager extends EntityManager<Puck> {

    public PuckManager(Context ctx) {
        super(Puck.class, ctx);
    }

    public Puck forIBeacon(IBeacon iBeacon) {
        if (iBeacon == null) {
            return null;
        }
        return readFirst(find(iBeacon.getProximityUuid(), iBeacon.getMajor(), iBeacon.getMinor()));
    }

    public Select<Puck> find(String UUID, int major, int minor) {
        Where query =
                new Where(DB.Column.PROXIMITY_UUID, Is.EQUAL, UUID)
                .and(DB.Column.MAJOR, Is.EQUAL, major)
                .and(DB.Column.MINOR, Is.EQUAL, minor);
        return select().where(query);
    }

    public Puck read(String UUID, int major, int minor) {
        return readFirst(find(UUID, major, minor));
    }

    public ArrayList<Puck> getAll() {
        return readAll(select());
    }

}
