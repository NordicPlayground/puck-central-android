package no.nordicsemi.puckcentral.adapters;

import android.view.View;
import android.widget.TextView;

import org.droidparts.adapter.holder.ViewHolder;
import org.droidparts.annotation.inject.InjectView;

import no.nordicsemi.puckcentral.R;

public class TriggerViewHolder extends ViewHolder {

    @InjectView(id = R.id.tvTriggerName)
    public TextView mTvTriggerName;

    @InjectView(id = R.id.tvTriggerActions)
    public TextView mTvTriggerActions;

    public TriggerViewHolder(View view) {
        super(view);
    }
}
