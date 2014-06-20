package no.nordicsemi.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.droidparts.activity.Activity;
import org.droidparts.annotation.inject.InjectView;

import java.util.ArrayList;

import no.nordicsemi.R;
import no.nordicsemi.adapters.LocationPuckAdapter;
import no.nordicsemi.models.LocationPuck;


public class MainActivity extends Activity {

    @InjectView
    ListView lvLocationPucks;

    @Override
    public void onPreInject() {
        super.onPreInject();
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<LocationPuck> locationPucks = new ArrayList<>();
        locationPucks.add(new LocationPuck("Citchen"));
        locationPucks.add(new LocationPuck("Meeting Room"));

        LocationPuckAdapter adapter = new LocationPuckAdapter(this,
                android.R.layout.simple_list_item_1, locationPucks);
        lvLocationPucks.setAdapter(adapter);
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
