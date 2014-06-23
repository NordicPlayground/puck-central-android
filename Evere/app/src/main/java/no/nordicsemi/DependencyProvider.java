package no.nordicsemi;

import android.content.Context;

import com.radiusnetworks.ibeacon.IBeaconManager;

import org.droidparts.AbstractDependencyProvider;
import org.droidparts.persist.sql.AbstractDBOpenHelper;

import no.nordicsemi.db.DBOpenHelper;

public class DependencyProvider extends AbstractDependencyProvider{

    private final DBOpenHelper mDBOpenHelper;
    private final IBeaconManager mIBeaconManager;

    public DependencyProvider(Context ctx) {
        super(ctx);
        mDBOpenHelper = new DBOpenHelper(ctx);
        mIBeaconManager = IBeaconManager.getInstanceForApplication(ctx);
    }

    @Override
    public AbstractDBOpenHelper getDBOpenHelper() {
        return mDBOpenHelper;
    }
}
