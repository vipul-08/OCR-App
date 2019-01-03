package com.mobicule.myapp;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ProofClient {
    @POST("/api/data")
    Call<DatabaseResponse> insertData(@Body RequestBody params);
}
