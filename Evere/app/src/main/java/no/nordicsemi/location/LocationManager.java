package no.nordicsemi.location;

import com.radiusnetworks.ibeacon.IBeacon;

import org.droidparts.Injector;
import org.droidparts.annotation.inject.InjectDependency;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import no.nordicsemi.db.LocationPuckManager;
import no.nordicsemi.models.LocationPuck;


public class LocationManager {

    @InjectDependency
    LocationPuckManager mLocationPuckManager;

    private LocationPuck location;
    private DateTime mLastChanged;

    public LocationManager() {
        mLastChanged = new DateTime();
    }

    public void updateLocation(Collection<IBeacon> iBeacons) {

        if(mLastChanged.plusSeconds(5).isBeforeNow()) {
            return;
        }

        if(iBeacons.size() == 0) {
            setLocation(null);
            return;
        }

        IBeacon[] iBeaconsArray = iBeacons.toArray(new IBeacon[iBeacons.size()]);

        Arrays.sort(iBeaconsArray, new Comparator<IBeacon>() {
            @Override
            public int compare(IBeacon a, IBeacon b) {
                return a.getAccuracy() - b.getAccuracy() < 0 ? 1 : -1;
            }
        });

        for(IBeacon iBeacon : iBeaconsArray) {
            if(iBeacon.getProximity() == IBeacon.PROXIMITY_IMMEDIATE) {
                setLocation(iBeacon);
                return;
            }
        }

        for(IBeacon iBeacon : iBeaconsArray) {
            if(iBeacon.getProximity() == IBeacon.PROXIMITY_NEAR) {
                setLocation(iBeacon);
                return;
            }
        }
    }

    private void setLocation(IBeacon iBeacon) {
        if(mLocationPuckManager == null) {
            Injector.inject(Injector.getApplicationContext(), this);
        }
        mLastChanged = new DateTime();
        LocationPuck newLocation = mLocationPuckManager.getPuckByUUID(iBeacon.getProximityUuid(), iBeacon.getMajor(), iBeacon.getMinor());
        if(!newLocation.equals(location)) {
            return;
        }
        location = newLocation;

        /* trigger location enter/leave here */

    }

    public LocationPuck getCurrentLocation() {
        return location;
    }
}
