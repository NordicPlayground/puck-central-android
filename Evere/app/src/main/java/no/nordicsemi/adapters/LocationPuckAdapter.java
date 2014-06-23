package no.nordicsemi.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import org.droidparts.adapter.cursor.EntityCursorAdapter;
import org.droidparts.adapter.holder.Text2Holder;
import org.droidparts.persist.sql.stmt.Select;

import no.nordicsemi.db.LocationPuckManager;
import no.nordicsemi.models.LocationPuck;

public class LocationPuckAdapter extends EntityCursorAdapter<LocationPuck> {

    public LocationPuckAdapter(Context ctx, Select<LocationPuck> select) {
        super(ctx, new LocationPuckManager(ctx), select);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, null);
        Text2Holder holder = new Text2Holder(v);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(Context context, View view, LocationPuck locationPuck) {
        Text2Holder holder = (Text2Holder) view.getTag();
        holder.text1.setText(locationPuck.getName());
        holder.text2.setText(locationPuck.getFormattedUUID());
    }
}
