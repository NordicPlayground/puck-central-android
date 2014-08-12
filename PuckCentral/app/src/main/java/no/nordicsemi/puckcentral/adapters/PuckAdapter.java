package no.nordicsemi.puckcentral.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import org.droidparts.adapter.cursor.EntityCursorAdapter;
import org.droidparts.annotation.inject.InjectDependency;
import org.droidparts.persist.sql.stmt.Select;

import no.nordicsemi.puckcentral.R;
import no.nordicsemi.puckcentral.db.PuckManager;
import no.nordicsemi.puckcentral.db.RuleManager;
import no.nordicsemi.puckcentral.models.Puck;

public class PuckAdapter extends EntityCursorAdapter<Puck> {

    @InjectDependency
    private RuleManager mRuleManager;

    @InjectDependency
    private PuckManager mPuckManager;

    public PuckAdapter(Context ctx, Select<Puck> select) {
        super(ctx, new PuckManager(ctx), select);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view =  getLayoutInflater().inflate(R.layout.puck_list_item, null);
        PuckViewHolder puckViewHolder = new PuckViewHolder(view);
        view.setTag(puckViewHolder);
        return view;
    }

    @Override
    public void bindView(final Context context, View view, final Puck puck) {
        entityManager.fillForeignKeys(puck);

        PuckViewHolder holder = (PuckViewHolder) view.getTag();
        holder.mTvPuckName.setText(puck.getName());

        RuleAdapter2 ruleAdapter2 = new RuleAdapter2(context, mRuleManager.getRulesForPuck(puck));
        holder.mLlTriggerList.removeAllViews();
        for (int i=0; i<ruleAdapter2.getCount(); i++) {
            View item = ruleAdapter2.getView(i, null, null);
            holder.mLlTriggerList.addView(item);
        }

        holder.mBtnDeletePuck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle(R.string.rule_remove)
                        .setPositiveButton(context.getString(R.string.delete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mPuckManager.delete(puck.id)) {
                                    requeryData();
                                }
                            }
                        })
                        .setNegativeButton(context.getString(R.string.abort), null);
                builder.create().show();
            }
        });
    }
}
