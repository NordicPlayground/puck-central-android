package no.nordicsemi.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.droidparts.activity.Activity;
import org.droidparts.annotation.inject.InjectView;

import no.nordicsemi.R;
import no.nordicsemi.adapters.LocationPuckAdapter;
import no.nordicsemi.db.LocationPuckManager;
import no.nordicsemi.models.LocationPuck;


public class MainActivity extends Activity {

    @InjectView
    ListView lvLocationPucks;
    private LocationPuckAdapter adapter;

    @Override
    public void onPreInject() {
        super.onPreInject();
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new LocationPuckAdapter(this, new LocationPuckManager(this)
                .select());
        lvLocationPucks.setAdapter(adapter);

        // Called for debug purposes. Should be moved to iBeacon location entered callback.
        locationPuckDiscovered();
    }

    public void locationPuckDiscovered() {
        final String[] names = getResources().getStringArray(R.array.locationPuckNames);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.puck_discovered_dialog_title))
                .setSingleChoiceItems(names,0, null)
                .setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int index = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        adapter.create(new LocationPuck(names[index]));
                    }
                })
                .setNegativeButton(getString(R.string.reject), null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
