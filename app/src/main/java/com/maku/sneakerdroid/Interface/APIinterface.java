package com.maku.sneakerdroid.Interface;

import com.google.gson.JsonObject;
import com.maku.sneakerdroid.pojos.DataUploadResponse;
import com.maku.sneakerdroid.pojos.ResponseBody;
import com.maku.sneakerdroid.pojos.UserReg;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIinterface {

    @Headers( "Content-Type: application/json" )
    @POST("/api/v1/mobile/participants/")
    Call<ResponseBody> reg(@Body JsonObject jsonObject);

    @Headers( "Content-Type: application/json" )
    @POST("/api/v1/mobile/probes-data/create-bulk")
    Call<DataUploadResponse> upload(@Body JsonObject jsonObject1);

}
