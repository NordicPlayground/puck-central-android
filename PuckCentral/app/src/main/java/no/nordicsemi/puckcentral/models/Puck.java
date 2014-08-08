package no.nordicsemi.puckcentral.models;

import org.droidparts.annotation.sql.Column;
import org.droidparts.annotation.sql.Table;
import org.droidparts.model.Entity;

import java.util.ArrayList;
import java.util.UUID;

import no.nordicsemi.puckcentral.db.DB;

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

    @Column(name = DB.Column.ADDRESS)
    private String mAddress;

    @Column(name = DB.Column.SERVICE_UUIDS)
    private ArrayList<UUID> mServiceUUIDs;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object) this).getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Puck puck = (Puck) o;

        if (mMajor != puck.mMajor) return false;
        if (mMinor != puck.mMinor) return false;
        if (!mProximityUUID.equals(puck.mProximityUUID)) return false;
        // Consider adding check for puck address

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + mMinor;
        result = 31 * result + mMajor;
        result = 31 * result + mProximityUUID.hashCode();
        return result;
    }

    public String getProximityUUID() {
        return mProximityUUID;
    }

    public ArrayList<UUID> getServiceUUIDs() {
        return mServiceUUIDs;
    }

    public void setServiceUUIDs(ArrayList<UUID> serviceUUIDs) {
        mServiceUUIDs = serviceUUIDs;
    }

    public Puck() {}

    public Puck(String name, int minor, int major, String proximityUUID, String address, ArrayList<UUID>
            serviceUUIDs) {
        this.mName = name;
        this.mMinor = minor;
        this.mMajor = major;
        this.mAddress = address;
        this.mProximityUUID = proximityUUID;
        this.mServiceUUIDs = serviceUUIDs;
    }

    public String getFormattedUUID() {
        return String.format("%s - %s - %s", mProximityUUID, Integer.toHexString(mMajor),
                Integer.toHexString(mMinor));
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }
}
