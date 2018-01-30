package mera.com.testapp.domain;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import mera.com.testapp.data.net.OpenskyApi;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class OpenskyApiManager {
    private static final String OPENSKY_URL = "https://opensky-network.org/api/";
    private OpenskyApi mOpenskyApi;

    OpenskyApiManager() {
        mOpenskyApi = getRetrofit().create(OpenskyApi.class);
    }

    @NonNull
    OpenskyApi getWebApiInterface() {
        return mOpenskyApi;
    }

    @NonNull
    private static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(OPENSKY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient())
                .build();
    }

    private static OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).build();
    }

    <T> T execute(Call<T> request) throws IOException {
        final Response<T> response = request.execute();
        return response.body();
    }
}