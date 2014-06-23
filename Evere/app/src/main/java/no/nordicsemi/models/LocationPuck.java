package no.nordicsemi.models;

import org.droidparts.annotation.sql.Column;
import org.droidparts.annotation.sql.Table;
import org.droidparts.model.Entity;

import no.nordicsemi.db.LocationPuckManager;

@Table
public class LocationPuck extends Entity {
    @Column(name = LocationPuckManager.COLUMN_NAME)
    private String mName;

    @Column(name = LocationPuckManager.COLUMN_MINOR)
    private int mMinor;

    @Column(name = LocationPuckManager.COLUMN_MAJOR)
    private int mMajor;

    @Column(name = LocationPuckManager.COLUMN_PROXIMITY_UUID)
    private String mProximityUUID;

    public void setName(String name) {
        this.mName = name;
    }

    public String getName() {
        return mName;
    }

    public int getMinor() {
        return mMinor;
    }

    public int getMajor() {
        return mMajor;
    }

    public String getProximityUUID() {
        return mProximityUUID;
    }

    public LocationPuck() {}

    public LocationPuck(String name, int minor, int major, String proximityUUID) {
        this.mName = name;
        this.mMinor = minor;
        this.mMajor = major;
        this.mProximityUUID = proximityUUID;
    }

    public String getFormattedUUID() {
        return String.format("%s - %s - %s", mProximityUUID, Integer.toHexString(mMajor),
                Integer.toHexString(mMinor));
    }
}
