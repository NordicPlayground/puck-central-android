package no.nordicsemi.puckcentral.location;

import android.content.Context;

import com.radiusnetworks.ibeacon.IBeacon;

import org.droidparts.Injector;
import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.bus.EventBus;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import no.nordicsemi.puckcentral.R;
import no.nordicsemi.puckcentral.db.PuckManager;
import no.nordicsemi.puckcentral.models.Puck;
import no.nordicsemi.puckcentral.triggers.Trigger;


public class LocationManager {

    @InjectDependency
    Context mCtx;

    @InjectDependency
    PuckManager mPuckManager;

    private Puck mClosestPuck;
    private DateTime mLastChanged;

    private final int THROTTLE = 3;

    public LocationManager() {
        mLastChanged = new DateTime();
    }

    public void updateLocation(Collection<IBeacon> iBeacons) {

        if (mLastChanged.plusSeconds(THROTTLE).isAfterNow()) {
            return;
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
        if (mPuckManager == null) {
            Injector.inject(Injector.getApplicationContext(), this);
        }

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
        updateClosestPuckGUI(mCtx.getString(R.string.no_known_pucks_nearby));
        if (mClosestPuck == null) {
            return;
        }

        Trigger.trigger(
                mClosestPuck,
                Trigger.TRIGGER_LEAVE_ZONE);
        mClosestPuck = null;
    }

    private void enterNewZone(Puck newClosestPuck) {
        mClosestPuck = newClosestPuck;
        updateClosestPuckGUI(mCtx.getString(R.string.currently_near_puck, mClosestPuck.getName()));
        Trigger.trigger(
                mClosestPuck,
                Trigger.TRIGGER_ENTER_ZONE);
    }

    public Puck getCurrentLocation() {
        return mClosestPuck;
    }

    public void updateClosestPuckGUI(String text) {
        EventBus.postEvent(Trigger.TRIGGER_UPDATE_CLOSEST_PUCK_TV, text);
    }
}
