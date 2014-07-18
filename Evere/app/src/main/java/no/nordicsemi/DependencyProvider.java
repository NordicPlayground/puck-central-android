package no.nordicsemi;

import android.content.Context;

import com.radiusnetworks.proximity.ibeacon.IBeaconManager;

import org.droidparts.AbstractDependencyProvider;
import org.droidparts.net.http.RESTClient;
import org.droidparts.persist.sql.AbstractDBOpenHelper;

import no.nordicsemi.bluetooth.gatt.GattManager;
import no.nordicsemi.db.ActionManager;
import no.nordicsemi.db.DBOpenHelper;
import no.nordicsemi.db.PuckManager;
import no.nordicsemi.db.RuleManager;
import no.nordicsemi.location.LocationManager;

public class DependencyProvider extends AbstractDependencyProvider{

    private final DBOpenHelper mDBOpenHelper;
    private final IBeaconManager mIBeaconManager;
    private final RESTClient mRESTClient;
    private final LocationManager mLocationManager;
    private final Context mContext;
    private final GattManager mGattManager;

    public DependencyProvider(Context ctx) {
        super(ctx);
        mDBOpenHelper = new DBOpenHelper(ctx);
        mIBeaconManager = IBeaconManager.getInstanceForApplication(ctx);
        mRESTClient = new RESTClient(ctx);
        mLocationManager = new LocationManager();
        mGattManager = new GattManager();
        mContext = ctx;
    }

    @Override
    public AbstractDBOpenHelper getDBOpenHelper() {
        return mDBOpenHelper;
    }

    public RESTClient getRESTClient() {
        return mRESTClient;
    }

    public Context getContext() {
        return mContext;
    }

    public IBeaconManager getIBeaconManager() {
        return mIBeaconManager;
    }

    public ActionManager getActionManager() {
        return new ActionManager(mContext);
    }

    public RuleManager getRuleManager() {
        return new RuleManager(mContext);
    }

    public PuckManager getPuckManager() {
        return new PuckManager(mContext);
    }

    public LocationManager getLocationManager() {
        return mLocationManager;
    }

    public GattManager getGattManager() {
        return mGattManager;
    }
}
