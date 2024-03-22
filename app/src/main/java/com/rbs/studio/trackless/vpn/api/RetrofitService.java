package com.rbs.studio.trackless.vpn.api;

import com.rbs.studio.trackless.vpn.model.GeoResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitService {
    @GET("json")
    Call<GeoResponse> getLocation();



}
