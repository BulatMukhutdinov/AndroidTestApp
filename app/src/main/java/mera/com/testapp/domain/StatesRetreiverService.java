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

import mera.com.testapp.domain.db.DatabaseHelper;
import mera.com.testapp.data.State;
import mera.com.testapp.data.StatesResponse;
import retrofit2.Call;

public class StatesRetreiverService extends Service {
    public static final String STATES_UPDATED_ACTION = "states_updated";

    private static final String TAG = StatesRetreiverService.class.getSimpleName();

    private LocalBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public StatesRetreiverService getService() {
            return StatesRetreiverService.this;
        }
    }

    public static Intent createServiceIntent(Context context) {
        return new Intent(context, StatesRetreiverService.class);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public Set<State> retreiveLocal(Context context, String countryFilter, DatabaseHelper.SortType sortType) {
        return DatabaseHelper.getInstance(context).query(countryFilter, sortType);
    }

    public void retreiveFromApi(final Context context) {
        new Thread(() -> {
            DatabaseHelper helper = DatabaseHelper.getInstance(context);
            helper.delete();
            helper.insert(getStates());
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