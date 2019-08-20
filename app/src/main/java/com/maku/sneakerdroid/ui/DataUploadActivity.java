package com.maku.sneakerdroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maku.sneakerdroid.Interface.APIinterface;
import com.maku.sneakerdroid.R;
import com.maku.sneakerdroid.constants.Contants;
import com.maku.sneakerdroid.pojos.DataUploadResponse;
import com.maku.sneakerdroid.pojos.ResponseBody;
import com.maku.sneakerdroid.pojos.UserReg;
import com.maku.sneakerdroid.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataUploadActivity extends AppCompatActivity {

    private static final String TAG = "DataUploadActivity";

    //    vars
    Context mContext;
    boolean is_system = false;
    String id;

    private SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_upload);

        Intent intent = getIntent();

        String name = intent.getStringExtra("name");

        Log.d(TAG, "onCreate: name " + name);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        id = mSharedPreferences.getString(Contants.PREFERENCES_USER_ID, null);

        checkStatus(name);
    }

    private void checkStatus(String name) {
        //check if systemApp, or installed app, or uninstalled app
        PackageManager pm = getPackageManager();
        if (pm.getLaunchIntentForPackage(name) != null) {

            if (ApplicationInfo.FLAG_SYSTEM != 0) {
                // check system apps
                is_system = true;
                Log.d(TAG, "checkStatus: is installed: " + Helper.isPackageInstalled(this, name) + " is system: " + is_system);
                    uploadToserver(name,Helper.isPackageInstalled(this, name), is_system);
            } else {
                // check if not a system app
                is_system = false;
                Log.d(TAG, "checkStatus: is installed:" + Helper.isPackageInstalled(this, name) + " is system: " + is_system);
                uploadToserver(name,Helper.isPackageInstalled(this, name), is_system);
            }
        }

    }

    private void uploadToserver(String name, boolean packageInstalled, boolean is_system) {

        Log.d(TAG, "uploadToserver: name " + name + " is_installed " + packageInstalled + " is_system " + is_system);

        // setting custom timeouts (time outs should be small, so as to enhance user experience)
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(150, TimeUnit.SECONDS);
        client.readTimeout(150, TimeUnit.SECONDS);
        client.writeTimeout(150, TimeUnit.SECONDS);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Contants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();

        APIinterface apIinterface = retrofit.create(APIinterface.class);

        JsonObject gson = new JsonObject();

        Date currentTime = Calendar.getInstance().getTime();

        try {
            // prepare call in Retrofit 2.0
            JSONObject paramObject = new JSONObject();

            //  dprobe json object 1
            JsonObject data = new JsonObject();
            data.addProperty("name", name);
            data.addProperty("is_system", is_system);
            data.addProperty("is_installed", packageInstalled);

            //  dprobe json object 2
            JsonObject data2 = new JsonObject();
            data2.addProperty("logged_time", String.valueOf(currentTime));
            data2.addProperty("probe", 1);

//            json array
            JSONArray probes_data = new JSONArray();
            probes_data.put(data);
            probes_data.put(data2);

            paramObject.put("app_version", 500);
            paramObject.put("participant_id", id);
            paramObject.put("probes_data", probes_data);

            //            parse the jso object
            JsonParser jsonParser = new JsonParser();
            gson = (JsonObject) jsonParser.parse(paramObject.toString());

            Call<DataUploadResponse> userCall = apIinterface.upload(gson);
            userCall.enqueue(new Callback<DataUploadResponse>() {

                @Override
                public void onResponse(Call<DataUploadResponse> call, Response<DataUploadResponse> response) {
                    Log.d(TAG, "onResponse: " + response.body().getMessage());
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "SUCCESS !", Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<DataUploadResponse> call, Throwable t) {
                    try {
                        Log.e("Tag", "error" + t.toString());

                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
