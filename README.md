# Puck Central

Puck Central is the name of the Android app.

## Extending Puck Central

### Creating your own actuator.

Puck Central comes bundled with a number of actuators (located in no.nordicsemi.actuators), amongst others the Puck, Spotify, Http, and IR actuators.

Creating your own actuator is straigtforward. Subclass the Actuator class, and implement the required methods. Look at the existing actuators, such as HttpActuator and RingerActuator for examples on how to wire everything together.

### Creating your own puck actuator.

Puck actuators actuate on pucks, for example sending an IR signal, or displaying something on a remote screen.
Simply subclass PuckActuator, and implement the requred methods. Look at the DisplayActuator and IRActuator for good examples at how to get going.

For Puck Central to recognize your new Puck, you have to register it in GattServices. You should create an entry there for the Gatt service and characteristics your device offers, as well as create any triggers they might support (Put the triggers in the Trigger class).

## A note on the android bluetooth stack

The Android Bluetooth LE stack is notoriously unreliable as of Android 4.4 KitKat. The Bluetooth stack is asynchronous, but the BLE stack may fail randomly if it ever has to execute more than one concurrent write or read operation, essentially making the entire process serial.

To work around these problems we've created a GattManager (no.nordicsemi.bluetooth.gatt.GattManager). It ensures serial execution of BLE operations, with retry on failure. It also supports bundling of operations, for easier development.

Contributions are gladly accepted, as the GattManager mainly contains the features we needed during development, as well as a few extra operations added in for good measure.

### GattManager usage

Usage is straigthforward. Instantiate a new gatt operation bundle, add add any needed gatt operations to it. Gatt operations are contained inside wrapper classes (GattCharacteristic[Operation]Operation) to allow queueing inside the GattManager. Finally, queue the bundle in the GattManager, and it will ensure correct execution.

### Issues with Cube Puck dropping connections

Cube Pucks may suddenly disconnect and reconnect in the current implementation. After much testing i believe the problem lies within the Android BLE stack. Improvements to the Cube Puck connection code is welcome. It is found in the CubeConnectionManager within the bluetooth folder.

```
GattOperationBundle bundle = new GattOperationBundle();

bundle.addOperation(new GattCharacteristicWriteOperation(
  someBluetoothDevice,
  someGattServiceUUID,
  someGattCharacteristicUUID,
  new byte[] { VALUE_TO_WRITE }));
  
gattManager.queue(bundle);
```
