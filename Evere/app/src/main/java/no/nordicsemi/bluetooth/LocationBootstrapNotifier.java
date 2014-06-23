package no.nordicsemi.bluetooth;

import android.content.Context;
import android.util.Log;

import com.radiusnetworks.ibeacon.Region;
import com.radiusnetworks.proximity.ibeacon.startup.BootstrapNotifier;

import org.json.JSONException;

import no.nordicsemi.actuators.RingerActuator;

public class LocationBootstrapNotifier implements BootstrapNotifier {


    private final Context mContext;

    public LocationBootstrapNotifier(Context context) {
        mContext = context;
    }

    @Override
    public Context getApplicationContext() {
        return mContext;
    }

    @Override
    public void didEnterRegion(Region region) {
        String regionName = region.getUniqueId();
        Log.i("YO", "entered region " + regionName + ", " + region);

        switch(regionName) {
            case "office":
                try {
                    new RingerActuator(getApplicationContext()).actuate("{\"mode\": 1}");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            break;
            case "kitchen":
                try {
                    new RingerActuator(getApplicationContext()).actuate("{\"mode\": 0}");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}
