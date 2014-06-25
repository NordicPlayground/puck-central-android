package no.nordicsemi;

import android.content.Context;

import com.radiusnetworks.proximity.ibeacon.IBeaconManager;

import org.droidparts.AbstractDependencyProvider;
import org.droidparts.net.http.RESTClient;
import org.droidparts.persist.sql.AbstractDBOpenHelper;

import no.nordicsemi.db.ActionManager;
import no.nordicsemi.db.DBOpenHelper;
import no.nordicsemi.db.PuckManager;
import no.nordicsemi.db.RuleManager;

public class DependencyProvider extends AbstractDependencyProvider{

    private final DBOpenHelper mDBOpenHelper;
    private final IBeaconManager mIBeaconManager;
    private final RESTClient mRESTClient;
    private final Context mContext;

    public DependencyProvider(Context ctx) {
        super(ctx);
        mDBOpenHelper = new DBOpenHelper(ctx);
        mIBeaconManager = IBeaconManager.getInstanceForApplication(ctx);
        mRESTClient = new RESTClient(ctx);
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
}
