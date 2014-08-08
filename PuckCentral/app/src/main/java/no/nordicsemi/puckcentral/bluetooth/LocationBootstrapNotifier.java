package no.nordicsemi.puckcentral.bluetooth;

import android.content.Context;
import android.content.Intent;

import com.radiusnetworks.ibeacon.Region;
import com.radiusnetworks.proximity.ibeacon.startup.BootstrapNotifier;

import org.droidparts.Injector;
import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.util.L;

public class LocationBootstrapNotifier implements BootstrapNotifier {

    @InjectDependency
    private Context mContext;
    private Intent mLocationRangeMonitorServiceIntent;

    public LocationBootstrapNotifier() {
        Injector.inject(Injector.getApplicationContext(), this);
    }

    @Override
    public Context getApplicationContext() {
        return mContext;
    }

    @Override
    public void didEnterRegion(Region region) {
        L.i("Entered region");
        mLocationRangeMonitorServiceIntent = (new Intent(mContext, LocationRangeMonitorService.class));
        mContext.startService(mLocationRangeMonitorServiceIntent);
        L.i("Service should now be started");
    }

    @Override
    public void didExitRegion(Region region) {
        mContext.stopService(mLocationRangeMonitorServiceIntent);
        L.i("Exited region");
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        L.i("Determined state for region: " + i);
    }
}
