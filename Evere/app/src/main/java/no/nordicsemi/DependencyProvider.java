package no.nordicsemi;

import android.content.Context;

import com.radiusnetworks.ibeacon.IBeaconManager;

import org.droidparts.AbstractDependencyProvider;
import org.droidparts.net.http.RESTClient;
import org.droidparts.persist.sql.AbstractDBOpenHelper;

import no.nordicsemi.db.DBOpenHelper;

public class DependencyProvider extends AbstractDependencyProvider{

    private final DBOpenHelper mDBOpenHelper;
    private final IBeaconManager mIBeaconManager;
    private final RESTClient mRESTClient;

    public DependencyProvider(Context ctx) {
        super(ctx);
        mDBOpenHelper = new DBOpenHelper(ctx);
        mIBeaconManager = IBeaconManager.getInstanceForApplication(ctx);
        mRESTClient = new RESTClient(ctx);
    }

    @Override
    public AbstractDBOpenHelper getDBOpenHelper() {
        return mDBOpenHelper;
    }

    public RESTClient getRESTClient() {
        return mRESTClient;
    }
}
