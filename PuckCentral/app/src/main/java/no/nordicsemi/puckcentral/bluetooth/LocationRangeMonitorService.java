package no.nordicsemi.puckcentral.bluetooth;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;
import com.radiusnetworks.proximity.ibeacon.IBeaconManager;

import org.droidparts.Injector;
import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.util.L;

import java.util.Collection;
import java.util.HashMap;

import no.nordicsemi.puckcentral.location.LocationManager;
import no.nordicsemi.puckcentral.models.Puck;


public class LocationRangeMonitorService extends Service implements IBeaconConsumer {

    @InjectDependency
    IBeaconManager mIBeaconManager;

    @InjectDependency
    private LocationManager mLocationManager;

    @Override
    public void onIBeaconServiceConnect() {
        final HashMap<Integer, String> names = new HashMap<>();
        names.put(IBeacon.PROXIMITY_IMMEDIATE, "IMMEDIATE");
        names.put(IBeacon.PROXIMITY_NEAR, "NEAR");
        names.put(IBeacon.PROXIMITY_FAR, "FAR");
        names.put(IBeacon.PROXIMITY_UNKNOWN, "UNKNOWN");

        mIBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region) {
                L.v("[" + iBeacons.size() + "] iBeacons:");
                for (IBeacon iBeacon : iBeacons) {
                    L.v(iBeacon.getProximityUuid() + "-" + iBeacon.getMajor() + "-" + iBeacon
                            .getMinor());
                    L.v("accuracy: " + iBeacon.getAccuracy());
                    L.v("proximity: " + names.get(iBeacon.getProximity()));
                }

                mLocationManager.updateLocation(iBeacons);
                Puck location = mLocationManager.getCurrentLocation();
                L.v("Current location: " + location);
            }

        });

        try {
            mIBeaconManager.startRangingBeaconsInRegion(
                new Region("puckcentral", "E20A39F473F54BC4A12F17D1AD07A961", 0x1337, null));
        } catch (RemoteException e) {
            L.e(e);
        }
    }

    @Override
    public Context getApplicationContext() {
        return this;
    }

    @Override
    public void onCreate() {
        Injector.inject(this, this);
        mIBeaconManager.bind(this);
    }

    public void onDestroy() {
        mIBeaconManager.unBind(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
