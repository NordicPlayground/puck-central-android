package no.nordicsemi.bluetooth;

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


public class LocationRangeMonitorService extends Service implements IBeaconConsumer {

    @InjectDependency
    IBeaconManager mIBeaconManager;

    @Override
    public void onIBeaconServiceConnect() {
        mIBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region) {
                L.i("[" + iBeacons.size() + "] iBeacons:");
                for(IBeacon iBeacon : iBeacons) {
                    L.i(iBeacon.getProximityUuid() + "-" + iBeacon.getMajor() + "-" + iBeacon.getMinor());
                }
            }
        });

        try {
            mIBeaconManager.startRangingBeaconsInRegion(
                new Region("evere", "E20A39F473F54BC4A12F17D1AD07A961", 0x1337, null));
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
