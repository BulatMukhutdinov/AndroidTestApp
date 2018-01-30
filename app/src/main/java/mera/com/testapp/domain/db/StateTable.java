package mera.com.testapp.domain.db;

import android.database.Cursor;
import android.text.TextUtils;

import mera.com.testapp.data.State;

class StateTable {
    static final String STATE_TABLE = "state_table";
    static final String KEY_STATE_ICAO = "state_icao";
    static final String KEY_STATE_CALLSIGN = "state_callsign";
    static final String KEY_STATE_COUNTRY = "state_country";
    static final String KEY_STATE_VELOCITY = "state_velocity";

    private static final String TEXT = "TEXT";

    static final String CREATE_TABLE_STATE = "CREATE TABLE IF NOT EXISTS " + STATE_TABLE + " (" +
            KEY_STATE_ICAO + " " + TEXT + ", " +
            KEY_STATE_CALLSIGN + " " + TEXT + ", " +
            KEY_STATE_COUNTRY + " " + TEXT + ", " +
            KEY_STATE_VELOCITY + " " + TEXT + " )";

    static State convert(Cursor cursor) {
        String icao = cursor.getString(cursor.getColumnIndex(KEY_STATE_ICAO));
        String callsign = cursor.getString(cursor.getColumnIndex(KEY_STATE_CALLSIGN));
        String country = cursor.getString(cursor.getColumnIndex(KEY_STATE_COUNTRY));
        String velocity = cursor.getString(cursor.getColumnIndex(KEY_STATE_VELOCITY));
        return new State(icao, callsign, country, TextUtils.isEmpty(velocity) ? 0f : Float.parseFloat(velocity));
    }

    private StateTable() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
