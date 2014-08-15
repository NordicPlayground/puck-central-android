package no.nordicsemi.puckcentral.location;

import com.radiusnetworks.ibeacon.IBeacon;

import org.droidparts.Injector;
import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.bus.EventBus;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import no.nordicsemi.puckcentral.bluetooth.gatt.CubeConnectionManager;
import no.nordicsemi.puckcentral.db.PuckManager;
import no.nordicsemi.puckcentral.models.Puck;
import no.nordicsemi.puckcentral.triggers.Trigger;


public class LocationManager {

    @InjectDependency
    PuckManager mPuckManager;

    @InjectDependency
    CubeConnectionManager mCubeManager;

    private Puck mClosestPuck;
    private DateTime mLastChanged;

    private final int THROTTLE = 3;
    private boolean injected = false;

    public LocationManager() {
        mLastChanged = new DateTime();
    }

    public void updateLocation(Collection<IBeacon> iBeacons) {
        if (mLastChanged.plusSeconds(THROTTLE).isAfterNow()) {
            return;
        }

        if (!injected) {
            Injector.inject(Injector.getApplicationContext(), this);
            injected = true;
        }

        // There are few cases where there are exactly 0 iBeacons present,
        if (iBeacons.size() == 0) {
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

        for (IBeacon iBeacon : iBeaconsArray) {
            Puck puck = mPuckManager.forIBeacon(iBeacon);
            if (puck != null) {
                mCubeManager.checkAndConnectToPuck(puck);
            }
        }

        for (IBeacon iBeacon : iBeaconsArray) {
            if(iBeacon.getProximity() == IBeacon.PROXIMITY_IMMEDIATE) {
                setLocation(iBeacon);
                return;
            }
        }

        for (IBeacon iBeacon : iBeaconsArray) {
            if(iBeacon.getProximity() == IBeacon.PROXIMITY_NEAR) {
                setLocation(iBeacon);
                return;
            }
        }

        setLocation(null);
    }

    private void setLocation(IBeacon iBeacon) {
        mLastChanged = new DateTime();

        if (iBeacon == null) {
            leaveCurrentZone();
            return;
        }

        Puck newClosestPuck = mPuckManager.forIBeacon(iBeacon);
        if (newClosestPuck == null) {
            if (iBeacon.getProximity() == IBeacon.PROXIMITY_IMMEDIATE) {
                EventBus.postEvent(Trigger.TRIGGER_ZONE_DISCOVERED, iBeacon);
            } else {
                leaveCurrentZone();
            }
        } else if (!newClosestPuck.equals(mClosestPuck)) {
            leaveCurrentZone();
            enterNewZone(newClosestPuck);
        }
    }

    private void leaveCurrentZone() {
        if (mClosestPuck == null) {
            return;
        }

        broadcastNewClosestPuck(null);
        Trigger.trigger(
                mClosestPuck,
                Trigger.TRIGGER_LEAVE_ZONE);
        mClosestPuck = null;
    }

    private void enterNewZone(Puck newClosestPuck) {
        mClosestPuck = newClosestPuck;

        broadcastNewClosestPuck(mClosestPuck);
        Trigger.trigger(
                mClosestPuck,
                Trigger.TRIGGER_ENTER_ZONE);
    }

    public Puck getCurrentLocation() {
        return mClosestPuck;
    }

    public void broadcastNewClosestPuck(Puck puck) {
        EventBus.postEvent(Trigger.TRIGGER_CLOSEST_PUCK_CHANGED, puck);
    }
}
