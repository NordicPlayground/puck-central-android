package no.nordicsemi.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

import org.droidparts.Injector;
import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.util.L;
import org.json.JSONException;

import java.util.Collection;

import no.nordicsemi.actuators.RingerActuator;


public class LocationRangeMonitorService extends Service implements IBeaconConsumer {

    @InjectDependency
    IBeaconManager mIBeaconManager;

    @Override
    public void onIBeaconServiceConnect() {
        Log.e("SDSDFSDFSDF", "onIBeaconServiceConnect()");
        try {
            mIBeaconManager.startRangingBeaconsInRegion(new Region("office",
                "E20A39F473F54BC4A12F17D1AD07A961", 0x1337, 0x0F1C));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Context getApplicationContext() {
        return this;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return false;
    }

    @Override
    public void onCreate() {
        Injector.inject(this, this);
        L.i("onCrate was called o");
        mIBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region) {
                Log.i("LocationRangeMonitor", "didRangeBeaconsInRegion()");
                String regionName = region.getUniqueId();
                for(IBeacon iBeacon : iBeacons) {
                    if(iBeacon.getProximity() == IBeacon.PROXIMITY_NEAR) {
                        Log.i("LocationRangeMonitor", "In near proximity of " + regionName);
                        if(regionName == "office") {
                            try {
                                new RingerActuator(getApplicationContext()).actuate("{\"mode\": 1}");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
        mIBeaconManager.bind(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
