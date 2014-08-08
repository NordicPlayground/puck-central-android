package no.nordicsemi.puckcentral.bluetooth.gatt;

import java.util.ArrayList;

import no.nordicsemi.puckcentral.bluetooth.gatt.operations.GattOperation;

public class GattOperationBundle {
    final ArrayList<GattOperation> operations;

    public GattOperationBundle() {
        operations = new ArrayList<>();
    }

    public void addOperation(GattOperation operation) {
        operations.add(operation);
        operation.setBundle(this);
    }

    public ArrayList<GattOperation> getOperations() {
        return operations;
    }
}
