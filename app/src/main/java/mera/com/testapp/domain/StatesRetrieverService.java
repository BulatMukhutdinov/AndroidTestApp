package mera.com.testapp.domain;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mera.com.testapp.data.State;
import mera.com.testapp.data.StatesResponse;
import mera.com.testapp.domain.db.DatabaseHelper;
import retrofit2.Call;

public class StatesRetrieverService extends Service {
    public static final String STATES_UPDATED_ACTION = "states_updated";

    private static final String TAG = StatesRetrieverService.class.getSimpleName();

    private LocalBinder binder = new LocalBinder();

    private DatabaseHelper databaseHelper;

    public class LocalBinder extends Binder {
        public StatesRetrieverService getService() {
            return StatesRetrieverService.this;
        }
    }

    public static Intent createServiceIntent(Context context) {
        return new Intent(context, StatesRetrieverService.class);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public Set<State> retrieveLocal(Context context, String countryFilter, DatabaseHelper.SortType sortType) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }
        return databaseHelper.query(countryFilter, sortType);
    }

    public void retrieveFromAdi(final Context context) {
        new Thread(() -> {
            if (databaseHelper == null) {
                databaseHelper = new DatabaseHelper(context);
            }
            databaseHelper.delete();
            databaseHelper.insert(getStates());
            sendBroadcast(new Intent(STATES_UPDATED_ACTION));
        }).start();
    }

    private List<State> getStates() {
        List<State> statesArray = new ArrayList<>();
        OpenskyApiManager apiManager = new OpenskyApiManager();
        Call<StatesResponse> call = apiManager.getWebApiInterface().getStates();
        try {
            StatesResponse statesResponse = apiManager.execute(call);
            if (statesResponse != null) {
                List<List<String>> statesRaw = statesResponse.getStates();
                for (List<String> stateRaw : statesRaw) {
                    statesArray.add(State.parse(stateRaw));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return statesArray;
    }
}