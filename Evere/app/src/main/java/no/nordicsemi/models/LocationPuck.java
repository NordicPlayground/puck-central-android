package no.nordicsemi.models;

import org.droidparts.annotation.sql.Column;
import org.droidparts.annotation.sql.Table;
import org.droidparts.model.Entity;

@Table
public class LocationPuck extends Entity {
    @Column
    private String name;

    @Column
    private int minor;

    @Column
    private int major;

    @Column
    private String proximityUUID;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getMinor() {
        return minor;
    }

    public int getMajor() {
        return major;
    }

    public String getProximityUUID() {
        return proximityUUID;
    }

    public LocationPuck() {}

    public LocationPuck(String name, int minor, int major, String proximityUUID) {
        this.name = name;
        this.minor = minor;
        this.major = major;
        this.proximityUUID = proximityUUID;
    }

    public String getFormattedUUID() {
        return String.format("%s - %s - %s", proximityUUID, Integer.toHexString(major),
                Integer.toHexString(minor));
    }
}
