package no.nordicsemi.puckcentral.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import org.droidparts.adapter.cursor.EntityCursorAdapter;
import org.droidparts.adapter.holder.Text2Holder;
import org.droidparts.persist.sql.stmt.Select;

import no.nordicsemi.puckcentral.db.RuleManager;
import no.nordicsemi.puckcentral.models.Action;
import no.nordicsemi.puckcentral.models.Rule;

public class RuleAdapter2 extends EntityCursorAdapter<Rule> {

    public RuleAdapter2(Context ctx, Select<Rule> select) {
        super(ctx, new RuleManager(ctx), select);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view =  getLayoutInflater().inflate(android.R.layout.simple_list_item_2, null);
        Text2Holder holder = new Text2Holder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(Context context, View view, Rule rule) {
        entityManager.fillForeignKeys(rule);

        Text2Holder holder = (Text2Holder) view.getTag();
        holder.text1.setText(rule.getTrigger());

        StringBuilder sb = new StringBuilder();
        for (Action action : rule.getActions()) {
            sb.append(action.describeActuator());
            sb.append(": ");
            sb.append(action.describeArguments());
            sb.append("\n");
        }

        holder.text2.setText(sb.toString());
    }
}
