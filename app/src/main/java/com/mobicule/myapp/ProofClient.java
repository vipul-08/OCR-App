package com.mobicule.myapp;


import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ProofClient {
    @POST("/api/data")
    Call<JSONObject> insertData(@Body JSONObject data);
}
