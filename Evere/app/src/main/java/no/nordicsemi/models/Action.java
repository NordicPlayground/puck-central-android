package no.nordicsemi.models;

import org.droidparts.annotation.sql.Column;
import org.droidparts.annotation.sql.Table;
import org.droidparts.model.Entity;

import no.nordicsemi.actuators.Actuator;

@Table
public class Action extends Entity {

    @Column
    private Integer actuatorId;

    @Column
    private String arguments;

    public Action() {}

    public Action(int actuatorId, String arguments) {
        this.actuatorId = actuatorId;
        this.arguments = arguments;
    }

    public Integer getActuatorId() {
        return actuatorId;
    }

    public Actuator getActuator() {
        return Actuator.getActuatorForId(actuatorId);
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
