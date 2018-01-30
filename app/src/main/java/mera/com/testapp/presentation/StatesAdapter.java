package mera.com.testapp.presentation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;
import java.util.Set;

import mera.com.testapp.R;
import mera.com.testapp.data.State;

class StatesAdapter extends RecyclerView.Adapter<StatesAdapter.StateCard> {

    private State[] dataset;

    StatesAdapter() {
        dataset = new State[0];
    }

    public void setData(Set<State> dataset) {
        this.dataset = dataset.toArray(new State[dataset.size()]);
        notifyDataSetChanged();
    }

    @Override
    public StateCard onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        StateCard stateCard = new StateCard(v);

        stateCard.icao24 = v.findViewById(R.id.icao24);
        stateCard.callsign = v.findViewById(R.id.callsign);
        stateCard.originCountry = v.findViewById(R.id.origin_country);
        stateCard.velocity = v.findViewById(R.id.velocity);

        return stateCard;
    }

    @Override
    public void onBindViewHolder(StateCard holder, int position) {
        holder.icao24.setText(dataset[position].getIcao24());
        holder.callsign.setText(dataset[position].getCallsign());
        holder.originCountry.setText(dataset[position].getOriginCountry());
        holder.velocity.setText(formatVelocity(dataset[position].getVelocity()));
    }

    private String formatVelocity(float velocity) {
        if (velocity == (long) velocity) {
            return String.format(Locale.getDefault(), "%d", (long) velocity);
        } else {
            return String.format("%s", velocity);
        }
    }

    @Override
    public int getItemCount() {
        return dataset.length;
    }

    static class StateCard extends RecyclerView.ViewHolder {
        View rootView;
        TextView icao24;
        TextView callsign;
        TextView originCountry;
        TextView velocity;

        StateCard(View view) {
            super(view);
            rootView = view;
        }
    }
}
