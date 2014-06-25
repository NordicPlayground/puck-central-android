package no.nordicsemi;

import android.bluetooth.BluetoothAdapter;

import com.radiusnetworks.ibeacon.Region;
import com.radiusnetworks.proximity.ibeacon.startup.RegionBootstrap;

import org.droidparts.AbstractApplication;

import no.nordicsemi.bluetooth.LocationBootstrapNotifier;


public class Application extends AbstractApplication {

    public void onCreate() {
        super.onCreate();
        bootstrapLocationServices();
        enableBluetooth();
    }

    public void bootstrapLocationServices() {
        new RegionBootstrap(
            new LocationBootstrapNotifier(),
            new Region("evere", "E20A39F473F54BC4A12F17D1AD07A961", 0x1337, null)
        );
    }

    public void enableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter != null) {
            bluetoothAdapter.enable();
        }
    }
}
