package com.scanlibrary;

import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NewClient {

    @POST("/image/upload2")
    Call<Response> sendData(@Body RequestBody params);

}
