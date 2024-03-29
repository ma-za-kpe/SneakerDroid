package com.maku.sneakerdroid.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.internal.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbb20.CountryCodePicker;
import com.maku.sneakerdroid.Interface.APIinterface;
import com.maku.sneakerdroid.R;
import com.maku.sneakerdroid.constants.Contants;
import com.maku.sneakerdroid.network.APIClient;
import com.maku.sneakerdroid.pojos.DeviceDetails;
import com.maku.sneakerdroid.pojos.ResponseBody;
import com.maku.sneakerdroid.pojos.UserReg;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    //    widgets
    private ProgressBar mProgressBar;

    private EditText editTextfirstname;
    private EditText editTextlastname;
    private EditText editTextphone;
    private EditText editTextprojectcode;
    private EditText editTextappversion;
    private EditText editTextfcmkey;

    private TextView TextviewDevicemodel;
    private TextView TextviewDeviceType;
    private TextView TextviewDeviceHardware;
    private TextView TextviewDevicemanufacturer;

    private CountryCodePicker ccp;

    private Button mButtonRegister;

    private LinearLayout mLinearLayout;
    private LinearLayout mLinearLayout1;

    //    vars
    ArrayList<DeviceDetails> mDeviceDetails = new ArrayList<>();
    String deviceModel = android.os.Build.MODEL;
    String deviceType = android.os.Build.DEVICE;
    String hardware = android.os.Build.HARDWARE;
    String manufacture = android.os.Build.MANUFACTURER;

    String selected_country_code;
    String fullNumber;

    Context mContext;

//    userreg fields
    String firstname;
    String lastname;
    String phone;
    String projectCode;
    Integer appVersion;
    String fcmKey;

    //    create new instance of the DeviceDetails model class
    DeviceDetails mDeviceDetailss = new DeviceDetails();

//    share preferences
private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: " + deviceModel + deviceType);

    //    initialize the widgets

        editTextfirstname = findViewById(R.id.firstname);
        editTextlastname = findViewById(R.id.lastname);
        editTextphone = findViewById(R.id.input_number);
        editTextprojectcode = findViewById(R.id.projectcode);
        editTextappversion = findViewById(R.id.appversion);
        editTextfcmkey = findViewById(R.id.fcmkey);

        TextviewDevicemodel = findViewById(R.id.devicemodel);
        TextviewDeviceType = findViewById(R.id.devicetype);
        TextviewDeviceHardware = findViewById(R.id.hardware);
        TextviewDevicemanufacturer = findViewById(R.id.manufacturer);

        ccp = findViewById(R.id.ccp);

        mButtonRegister = findViewById(R.id.register);

        mLinearLayout = findViewById(R.id.detailsGroup);
        mLinearLayout1 = findViewById(R.id.sensitiveData);

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar1);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();


        Thread.currentThread().setName("Main Thread");
        Log.v(TAG, "onCreate() has run");
        Thread genThread = new Thread(new RegisterRunnable());
        genThread.start();

        mButtonRegister.setOnClickListener(this);

        // this method is used to hide the views
        hideViews();

        setDeviceDetails();

    }

//    hide the view a user is not meant to see
    private void hideViews() {
        Log.d(TAG, "hideViews: that the user doesnt have to see");
        mLinearLayout1.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.GONE);
    }

//set device details to the model class
    private void setDeviceDetails() {
       mDeviceDetailss.setDeviceModel(deviceModel);
       mDeviceDetailss.setDeviceType(deviceType);
       mDeviceDetailss.setHardware(hardware);
       mDeviceDetailss.setManufacturer(manufacture);
       
       getDeviceDetails();
    }

    private void getDeviceDetails() {
        Log.d(TAG, "getDeviceDetails: " + mDeviceDetailss.getDeviceModel());

        //   edittext fcm key
        editTextfcmkey.setText(Contants.FCM_KEY);

        TextviewDevicemodel.setText(mDeviceDetailss.getDeviceModel());
        TextviewDeviceType.setText(mDeviceDetailss.getDeviceType());
        TextviewDeviceHardware.setText(mDeviceDetailss.getHardware());
        TextviewDevicemanufacturer.setText(mDeviceDetailss.getManufacturer());
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");


    }

    private void registerProcess(String firstname, String lastname, String phone, String projectCode, Integer appVersion, String fcmKey, DeviceDetails deviceDetailss) {

        Log.d(TAG, "registerProcess: field items " + firstname +" "+ lastname +" "+ phone +" "+ projectCode +" "+ appVersion +" "+ fcmKey +" "+ deviceDetailss);

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

        UserReg userReg = new UserReg();
        userReg.setFirstName(firstname);
        userReg.setLastName(lastname);
        userReg.setPhoneNumber(phone);
        userReg.setProjectCode(projectCode);
        userReg.setAppVersion(appVersion);
        userReg.setFcmKey(fcmKey);
        userReg.setDeviceDetails(deviceDetailss);

        JsonObject gson = new JsonObject();

        // prepare call in Retrofit 2.0
        try {
            JSONObject paramObject = new JSONObject();

            //  device details json
            JsonObject device_details = new JsonObject();
            device_details.addProperty("device_model", deviceModel);
            device_details.addProperty("device_type", deviceType);
            device_details.addProperty("hardware", hardware);
            device_details.addProperty("manufacturer", manufacture);

            paramObject.put("first_name", firstname);
            paramObject.put("last_name", lastname);
            paramObject.put("phone_number", phone);
            paramObject.put("project_code", projectCode);
            paramObject.put("app_version", appVersion);
            paramObject.put("fcm_key", fcmKey);
            paramObject.put("device_details", device_details);

//            parse the jso object
            JsonParser jsonParser = new JsonParser();
            gson = (JsonObject) jsonParser.parse(paramObject.toString());

            Call<ResponseBody> userCall = apIinterface.reg(gson);
            userCall.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d(TAG, "onResponse: " + response.body().getParticipantDetails().getId());
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "SUCCESS ! token is " + response.body().getAccessToken() , Snackbar.LENGTH_LONG).show();

                    addToSharedPreferences(response.body().getParticipantDetails().getId());
                    
                    //   Move to the next acitvity
                    Intent intent = new Intent(MainActivity.this, AppsActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
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

    private void addToSharedPreferences(int id) {
        String id1 = String.valueOf(Integer.parseInt(String.valueOf(id)));
        mEditor.putString(Contants.PREFERENCES_USER_ID, id1).apply();
    }

    private class RegisterRunnable implements Runnable, View.OnClickListener {
        private static final String TAG = "RegisterRunnable";
        @Override
        public void run() {
            Log.d(TAG, "run: ");
            mButtonRegister.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: ");


            if (v.getId() == R.id.register) {

                phone = editTextphone.getText().toString();
                Log.d(TAG, "onClick: phone " + phone);

                //   get the correct phone number format (international format)
                selected_country_code = ccp.getSelectedCountryCodeWithPlus();

                //  form fields

                firstname = editTextfirstname.getText().toString();
                Log.d(TAG, "onClick: firstname " + firstname);
                lastname = editTextlastname.getText().toString();
                Log.d(TAG, "onClick: lastname " + lastname);
                fullNumber = selected_country_code + phone;
                Log.d(TAG, "Test user mobile " + fullNumber);
                projectCode = editTextprojectcode.getText().toString();
                Log.d(TAG, "onClick: projectCode " + projectCode);
                appVersion = Integer.parseInt(editTextappversion.getText().toString());
                Log.d(TAG, "onClick: appVersion " + appVersion);
                fcmKey = editTextfcmkey.getText().toString();
                // and device details that are initialised above
                Log.d(TAG, "onClick: device details " + mDeviceDetailss);

                Log.d(TAG, "onClick: field items " + firstname +" "+ lastname +" "+ fullNumber +" "+ projectCode +" "+ appVersion +" "+ fcmKey +" "+ mDeviceDetailss);


                if(!firstname.isEmpty() && !lastname.isEmpty() && !fullNumber.isEmpty()) {

                    registerProcess(firstname, lastname, fullNumber, projectCode, appVersion, fcmKey, mDeviceDetailss);

                } else {

                    Snackbar.make(v.getRootView(), "Fields are empty !", Snackbar.LENGTH_LONG).show();
                }

            }
        }
    }
}

