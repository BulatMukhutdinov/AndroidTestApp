package mera.com.testapp.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.List;

import lombok.Getter;

public class State implements Parcelable {

    private static final int ICAO_INDEX = 0;
    private static final int CALLSIGN_INDEX = 1;
    private static final int COUNTRY_INDEX = 2;
    private static final int VELOCITY_INDEX = 10;

    @Getter
    private String icao24;
    @Getter
    private String callsign;
    @Getter
    private String originCountry;
    @Getter
    private float velocity;

    private float timePosition;
    private float timeVelocity;
    private float longitude;
    private float latitude;
    private float altitude;
    private boolean isOnGround;
    private float heading;
    private float verticalRate;

    public State(String icao, String callsign, String country, float velocity) {
        this.icao24 = icao;
        this.callsign = callsign;
        this.originCountry = country;
        this.velocity = velocity;
    }

    private State(Parcel parcel) {
        icao24 = parcel.readString();
        callsign = parcel.readString();
        originCountry = parcel.readString();
        timePosition = parcel.readFloat();
        timeVelocity = parcel.readFloat();
        longitude = parcel.readFloat();
        latitude = parcel.readFloat();
        altitude = parcel.readFloat();
        isOnGround = parcel.readInt() == 1;
        velocity = parcel.readFloat();
        heading = parcel.readFloat();
        verticalRate = parcel.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(icao24);
        dest.writeString(callsign);
        dest.writeString(originCountry);
        dest.writeFloat(timePosition);
        dest.writeFloat(timeVelocity);
        dest.writeFloat(longitude);
        dest.writeFloat(latitude);
        dest.writeFloat(altitude);
        dest.writeInt(isOnGround ? 1 : 0);
        dest.writeFloat(velocity);
        dest.writeFloat(heading);
        dest.writeFloat(verticalRate);
    }

    public static final Parcelable.Creator<State> CREATOR = new Parcelable.Creator<State>() {
        public State createFromParcel(Parcel in) {
            return new State(in);
        }

        public State[] newArray(int size) {
            return new State[size];
        }
    };

    public static State parse(List<String> stateRaw) {
        return new State(stateRaw.get(ICAO_INDEX), stateRaw.get(CALLSIGN_INDEX),
                stateRaw.get(COUNTRY_INDEX), Float.parseFloat(stateRaw.get(VELOCITY_INDEX)));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        State state = (State) obj;
        return TextUtils.equals(icao24, state.icao24)
                && TextUtils.equals(callsign, state.callsign)
                && originCountry.equals(state.originCountry)
                && velocity == state.velocity;
    }

    @Override
    public int hashCode() {
        return (int) velocity;
    }
}