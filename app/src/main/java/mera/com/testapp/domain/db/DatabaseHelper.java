package mera.com.testapp.domain.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mera.com.testapp.data.State;

import static mera.com.testapp.domain.db.StateTable.KEY_STATE_COUNTRY;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int STATE_SIGNIFICANT_COLUMNS_AMOUNT = 4;

    public DatabaseHelper(Context context) {
        super(context, "database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(StateTable.CREATE_TABLE_STATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // table has read only fields
    }

    public void insert(List<State> states) {
        if (states == null || states.isEmpty()) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        for (State state : states) {
            ContentValues cv = new ContentValues(STATE_SIGNIFICANT_COLUMNS_AMOUNT);
            cv.put(StateTable.KEY_STATE_ICAO, state.getIcao24());
            cv.put(StateTable.KEY_STATE_CALLSIGN, state.getCallsign());
            cv.put(KEY_STATE_COUNTRY, state.getOriginCountry());
            cv.put(StateTable.KEY_STATE_VELOCITY, state.getVelocity());

            long rowId = db.insert(StateTable.STATE_TABLE, null, cv);
            if (rowId == -1) {
                break;
            }
        }
    }

    public Set<State> query(String countryFilter, SortType type) {
        Set<State> result = new HashSet<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor;
        String where = null;
        String[] whereArgs = null;

        if (!TextUtils.isEmpty(countryFilter)) {
            where = KEY_STATE_COUNTRY + "=?";
            whereArgs = new String[]{countryFilter};
        }
        cursor = db.query(StateTable.STATE_TABLE, null, where, whereArgs, null, null, getSortString(type));
        while (cursor.moveToNext()) {
            result.add(StateTable.convert(cursor));
        }
        return result;
    }

    public void delete() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(StateTable.STATE_TABLE, null, null);
    }

    private String getSortString(SortType sortType) {
        if (sortType == SortType.NONE) {
            return "";
        }

        String field;

        switch (sortType) {
            case VEL_ASC:
            case VEL_DESC:
                field = StateTable.KEY_STATE_VELOCITY;
                break;
            case SIGN_ASC:
            case SIGN_DESC:
                field = StateTable.KEY_STATE_CALLSIGN;
                break;
            default:
                field = KEY_STATE_COUNTRY;
                break;
        }

        String orderString = sortType.name().contains("ASC") ? "ASC" : "DESC";
        return field + " " + orderString;
    }

    public enum SortType {
        NONE, VEL_ASC, VEL_DESC, SIGN_ASC, SIGN_DESC,
    }
}