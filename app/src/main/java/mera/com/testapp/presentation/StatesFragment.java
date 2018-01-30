package mera.com.testapp.presentation;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Objects;
import java.util.Set;

import mera.com.testapp.R;
import mera.com.testapp.data.State;
import mera.com.testapp.databinding.FragmentListBinding;
import mera.com.testapp.domain.StatesRetreiverService;
import mera.com.testapp.domain.db.DatabaseHelper;

public class StatesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = StatesFragment.class.getSimpleName();
    private FragmentListBinding binding;

    private Context context;
    private static final String[] COUNTRIES = new String[]{"All", "Germany", "United States"};

    private StatesAdapter adapter;

    private StatesRetreiverService service;
    private boolean isServiceBound;

    private StatesReceiver statesReceiver = new StatesReceiver();

    private String countryFilter;

    private int chosenFilterPosition;

    public StatesFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);

        binding.listRefresh.setOnRefreshListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        binding.list.setLayoutManager(layoutManager);

        adapter = new StatesAdapter();
        binding.list.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        context.registerReceiver(statesReceiver, new IntentFilter(StatesRetreiverService.STATES_UPDATED_ACTION));
        context.bindService(StatesRetreiverService.createServiceIntent(context), connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        context.unregisterReceiver(statesReceiver);
        context.unbindService(connection);
        isServiceBound = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.list_filter) {
            showFilterDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefresh() {
        if (isServiceAvailable()) {
            binding.listRefresh.setRefreshing(true);
            service.retreiveFromApi(context);
        }
    }

    private void showFilterDialog() {
        if (getActivity() == null) {
            Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setSingleChoiceItems(COUNTRIES, chosenFilterPosition, (dialog, which) -> {
                binding.listRefresh.setRefreshing(true);
                chosenFilterPosition = which;
                if (which == 0) {
                    countryFilter = "";
                } else {
                    countryFilter = COUNTRIES[which];
                }
                getActivity().sendBroadcast(new Intent(StatesRetreiverService.STATES_UPDATED_ACTION));
                dialog.dismiss();
            });
            builder.create().show();
        }
    }

    private boolean isServiceAvailable() {
        return service != null && isServiceBound;
    }

    private class StatesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "StatesReceiver: onReceive action: " + intent.getAction());
            if (Objects.equals(intent.getAction(), StatesRetreiverService.STATES_UPDATED_ACTION)) {
                updateStateListAsync();
            }
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isServiceBound = true;
            StatesRetreiverService.LocalBinder localBinder = (StatesRetreiverService.LocalBinder) iBinder;
            service = localBinder.getService();
            updateStateListAsync();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isServiceBound = false;
            service = null;
        }
    };

    private void updateStateListAsync() {
        binding.listRefresh.setRefreshing(true);
        new AsyncTask<Void, Void, Set<State>>() {
            @Override
            protected Set<State> doInBackground(Void... voids) {
                return service.retreiveLocal(context, countryFilter, DatabaseHelper.SortType.NONE);
            }

            @Override
            protected void onPostExecute(Set<State> localStates) {
                binding.listRefresh.setRefreshing(false);
                adapter.setData(localStates);
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    activity.updateActionBar(localStates.size());
                }
                if (localStates != null && !localStates.isEmpty()) {
                    binding.emptyWarn.setVisibility(View.GONE);
                } else {
                    binding.emptyWarn.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
    }
}