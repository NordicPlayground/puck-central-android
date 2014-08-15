package no.nordicsemi.puckcentral;

import android.content.Context;

import com.radiusnetworks.proximity.ibeacon.IBeaconManager;

import org.droidparts.AbstractDependencyProvider;
import org.droidparts.net.http.RESTClient;
import org.droidparts.persist.sql.AbstractDBOpenHelper;

import no.nordicsemi.puckcentral.bluetooth.gatt.CubeConnectionManager;
import no.nordicsemi.puckcentral.bluetooth.gatt.GattManager;
import no.nordicsemi.puckcentral.db.ActionManager;
import no.nordicsemi.puckcentral.db.DBOpenHelper;
import no.nordicsemi.puckcentral.db.PuckManager;
import no.nordicsemi.puckcentral.db.RuleManager;
import no.nordicsemi.puckcentral.location.LocationManager;

public class DependencyProvider extends AbstractDependencyProvider{

    private final DBOpenHelper mDBOpenHelper;
    private final IBeaconManager mIBeaconManager;
    private final RESTClient mRESTClient;
    private final LocationManager mLocationManager;
    private final Context mContext;
    private final GattManager mGattManager;
    private final CubeConnectionManager mCubeConnectionManager;

    public DependencyProvider(Context ctx) {
        super(ctx);
        mDBOpenHelper = new DBOpenHelper(ctx);
        mIBeaconManager = IBeaconManager.getInstanceForApplication(ctx);
        mRESTClient = new RESTClient(ctx);
        mLocationManager = new LocationManager();
        mGattManager = new GattManager();
        mCubeConnectionManager = new CubeConnectionManager(ctx, mGattManager);
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

    public CubeConnectionManager getCubeConnectionManager() {
        return mCubeConnectionManager;
    }
}
