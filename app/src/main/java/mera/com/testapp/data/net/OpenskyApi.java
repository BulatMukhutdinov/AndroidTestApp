package mera.com.testapp.data.net;

import mera.com.testapp.data.StatesResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface OpenskyApi {

    @GET("states/all")
    Call<StatesResponse> getStates();
}