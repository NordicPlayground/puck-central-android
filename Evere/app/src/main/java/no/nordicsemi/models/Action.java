package no.nordicsemi.models;

import org.droidparts.annotation.sql.Column;
import org.droidparts.annotation.sql.Table;
import org.droidparts.model.Entity;

@Table
public class Action extends Entity {

    @Column
    private Integer actuatorId;

    @Column
    private String arguments;

    public Action() {
    }

    public Integer getActuatorId() {
        return actuatorId;
    }

    public void setActuatorId(Integer actuatorId) {
        this.actuatorId = actuatorId;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }
}
