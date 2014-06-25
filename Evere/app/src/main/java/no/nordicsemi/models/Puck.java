package no.nordicsemi.models;

import org.droidparts.annotation.sql.Column;
import org.droidparts.annotation.sql.Table;
import org.droidparts.model.Entity;

import java.util.ArrayList;

import no.nordicsemi.db.DB;

@Table
public class Puck extends Entity {
    @Column(name = DB.Column.NAME)
    private String mName;

    @Column(name = DB.Column.MINOR)
    private int mMinor;

    @Column(name = DB.Column.MAJOR)
    private int mMajor;

    @Column(name = DB.Column.PROXIMITY_UUID)
    private String mProximityUUID;

    @Column(name = DB.Column.SERVICE_UUIDS)
    private ArrayList<String> mServiceUUIDs;

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

    public ArrayList<String> getServiceUUIDs() {
        return mServiceUUIDs;
    }

    public Puck(String name, int minor, int major, String proximityUUID, ArrayList<String>
            serviceUUIDs) {
        this.mName = name;
        this.mMinor = minor;
        this.mMajor = major;
        this.mProximityUUID = proximityUUID;
        this.mServiceUUIDs = serviceUUIDs;
    }

    public String getFormattedUUID() {
        return String.format("%s - %s - %s", mProximityUUID, Integer.toHexString(mMajor),
                Integer.toHexString(mMinor));
    }
}
