## Evere

Evere is the name of the Android app.

## How to create your own actuator

## A note on the android bluetooth stack

The Android Bluetooth LE stack is notoriously unreliable as of Android 4.4 KitKat. The Bluetooth stack is asynchronous, but the BLE stack may fail randomly if it ever has to execute more than one concurrent write or read operation, essentially making the entire process serial.

To work around these problems we've created a GattManager (no.nordicsemi.bluetooth.gatt.GattManager). It ensures serial execution of BLE operations, with retry on failure. It also supports bundling of operations, for easier development.

Contributions are gladly accepted, as the GattManager mainly contains the features we needed during development, as well as a few extra operations added in for good measure.

### GattManager usage

Usage is straigthforward. Instantiate a new gatt operation bundle, add add any needed gatt operations to it. Gatt operations are contained inside wrapper classes (GattCharacteristic[Operation]Operation) to allow queueing inside the GattManager. 

```
GattOperationBundle bundle = new GattOperationBundle();
```

Add required operations:
```
  
```

Finally, queue the bundle in the GattManager, and it will ensure correct execution.
```
gattManager.queue(bundle)
```
