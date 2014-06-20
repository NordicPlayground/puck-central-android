package no.nordicsemi.models;

import org.droidparts.annotation.sql.Column;
import org.droidparts.annotation.sql.Table;
import org.droidparts.model.Entity;

@Table
public class LocationPuck extends Entity {

    @Column
    private String mName;

    public String getName() {
        return mName;
    }

    public LocationPuck() {}

    public LocationPuck(String name) {
        this.mName = name;
    }

    public void setName(String name) {
        this.mName = name;
    }
}
