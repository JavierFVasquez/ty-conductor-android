package com.imaginamos.taxisya.taxista.activities;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.imaginamos.taxisya.taxista.R;
import com.crashlytics.android.Crashlytics;
import com.imaginamos.taxisya.taxista.BuildConfig;
import com.imaginamos.taxisya.taxista.io.ApiService;
import com.imaginamos.taxisya.taxista.io.Connect;
import com.imaginamos.taxisya.taxista.io.MiddleConnect;
import com.imaginamos.taxisya.taxista.io.MyService;
import com.imaginamos.taxisya.taxista.model.Conf;
import com.imaginamos.taxisya.taxista.model.Preferencias;
import com.imaginamos.taxisya.taxista.model.ServiceStatusResponse;
import com.imaginamos.taxisya.taxista.utils.BDAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import io.fabric.sdk.android.Fabric;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.R.attr.value;

/**
 * Created by TaxisYa on 14/10/16.
 */

public class InicialActivityLogin extends Activity implements OnClickListener  {

    private Button btnLogin;
    private Button btnRegister;
    private String service_id, id_driver, login, uuid, id_user,driver_id, id;
    private int status_service = 0;
    private Intent intent_service;
    private Conf conf;
    private BDAdapter mySQLiteAdapter;
    private double lat, lng = 0;
    private ProgressDialog pDialog;
    private String TAG = "Splashanim";
    private String name = "";
    private TextToSpeech tts;
    private int qualification = 0;
    private Preferencias mPref;
    public String address, from_lat, from_lng, pay_type;

    ArrayList<HashMap<String, String>> contactList;

    public void requestPermissions (){
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CAMERA

        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("onCreate", "InicialActivityLogin");
        super.onCreate(savedInstanceState);


        requestPermissions ();



        setContentView(R.layout.activity_inicial);

        conf = new Conf(this);
        login = conf.getUser();
        id_driver = conf.getIdUser();
        driver_id = conf.getIdUser();
        uuid = conf.getUuid();

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        enable();

    }

    private void enable() {
        Log.v("MainActivity", "enable");

     //   clearServices();
//        MiddleConnect.enableDrive(this, lat, lng, driver_id, id, new AsyncHttpResponseHandler() {

        MiddleConnect.enableDrive(this, lat, lng, driver_id, uuid, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                try {
                    pDialog = new ProgressDialog(InicialActivityLogin.this);
                    pDialog.setMessage(getString(R.string.enable));
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();
                } catch (Exception e) {
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.e(TAG, "" + response);

                try {

                    JSONObject repsonsejson = new JSONObject(response);

                    int error = repsonsejson.getInt("error");
                    Log.v(TAG,"login_error =" + String.valueOf(error));
                    if (error == 0) {

                        if (repsonsejson.getBoolean("success")) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

                            Intent i = new Intent(InicialActivityLogin.this, LoginActivity.class);
                            startActivity(i);

                        } else {
                            //err_enable();
                        }
                    }
                    else if (error == 1) {
                        Log.v(TAG,"showAlert");

                        String msg_alert = "Vehiculo en uso";
                        if (repsonsejson.has("driver")) {

                            JSONObject driver = repsonsejson.getJSONObject("driver");
                            String name = driver.getString("name");
                            String cellphone = driver.getString("cellphone");

                            msg_alert = getString(R.string.enable_error_message_part1) + " " + name + " " + getString(R.string.enable_error_message_part2) + " " + cellphone +".";

                        }
                        showEnableAlert(msg_alert);

                    }

                } catch (Exception e) {
                   // err_enable();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
              //  String response = new String(responseBody);
                Log.e(TAG, "");
                //err_enable();
            }

            @Override
            public void onFinish() {
                try {
                    pDialog.dismiss();

                } catch (Exception e) {
                }

            }
        });
    }

    public void savePreferencias(String key, String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }
    private void shutDown() {

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

    }

    void showEnableAlert(final String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.enable_titulo_dialogo);
        builder.setMessage(message);
        builder.setNeutralButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.v(TAG, "alert: " + message);
            }
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    private void err_enable() {

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);

        Toast.makeText(getApplicationContext(), getString(R.string.error_net), Toast.LENGTH_SHORT).show();

    }


    private boolean isMyServiceRunning() {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRestart() {

        super.onRestart();
        //enable();

    }

    @Override
    public void onResume() {
        super.onResume();
        //enable();

        try {
            checkService();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnRegister:
                Intent btnRegister = new Intent(this, RegisterDriverActivity.class);
                startActivity(btnRegister);
                break;

            case R.id.btnLogin:
                Intent btnLogin = new Intent(this, LoginActivity.class);
                startActivity(btnLogin);
                break;

        }
    }

    public boolean checkService() throws JSONException {

        id_driver = conf.getIdUser();
        service_id = "";
        Log.v("checkService", "ini");


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttp3.OkHttpClient.Builder httpClient = new okhttp3.OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Connect.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        ApiService service = retrofit.create(ApiService.class);
        Call<ServiceStatusResponse> call_profile;

        if(service_id == null){
            Log.i("DRIVER ID",driver_id);
            call_profile = service.serviceStatus(driver_id);
        }else{
            call_profile = service.serviceStatus(driver_id, service_id);
        }
        call_profile.enqueue(new retrofit2.Callback<ServiceStatusResponse>() {
            @Override
            public void onResponse(Call<ServiceStatusResponse> call, retrofit2.Response<ServiceStatusResponse> response) {

                try {

                    int status_service = Integer.parseInt(response.body().getStatus_id());
                    // si hay un servicio asignado lo recupera
                    if ((status_service == 2) || (status_service == 4)) {
                        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                        Log.d("LoginActivity", "Refreshed token: " + refreshedToken);
                        uuid = refreshedToken;
                        conf.setUuid(uuid);

                        Intent intent = new Intent(InicialActivityLogin.this, MapActivity.class);
                        intent.putExtra("lat", Double.parseDouble(response.body().getFrom_lat()));
                        intent.putExtra("lng", Double.parseDouble(response.body().getFrom_lng()));
                        intent.putExtra("to_lat", Double.parseDouble(response.body().getTo_lat()));
                        intent.putExtra("to_lng", Double.parseDouble(response.body().getTo_lng()));
                        intent.putExtra("charge1", !response.body().getCharge1().trim().equals("") ? Integer.parseInt(response.body().getCharge1()) : 0);
                        intent.putExtra("charge2",  !response.body().getCharge1().trim().equals("") ? Integer.parseInt(response.body().getCharge2()) : 0);
                        intent.putExtra("charge3",  !response.body().getCharge1().trim().equals("") ? Integer.parseInt(response.body().getCharge3()) : 0);
                        intent.putExtra("charge4",  !response.body().getCharge1().trim().equals("") ? Integer.parseInt(response.body().getCharge4()) : 0);
                        intent.putExtra("valor_app", Integer.parseInt(response.body().getValor_app()));
                        intent.putExtra("destination", response.body().getDestination());

                        intent.putExtra("id_servicio", response.body().getId());

                        String type = String.valueOf(response.body().getSchedule_type());
                        String direccion = response.body().getAddress();

                        intent.putExtra("direccion", direccion);
                        intent.putExtra("status_service", status_service);
                        intent.putExtra("kind_id", response.body().getSchedule_id());
                        intent.putExtra("schedule_type", response.body().getSchedule_type());
                        intent.putExtra("name", response.body().getIndex_id());
                        intent.putExtra("pay_type", response.body().getPay_type());
                        intent.putExtra("card_reference", response.body().getUser_card_reference());
                        intent.putExtra("code", response.body().getCode());

                        startActivity(intent);
                        //finish();

                    } else if (status_service == 5) {
                        if (response.body().getQualification() != null) {
                            Log.v("MainActivity", "checkService() servicio asignado recuperado sin calificar");
                            conf.deleteServiceId();
                        }
                    } else {
                       conf.deleteServiceId();

                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Log.w("-----Error-----", e.toString());
                }


            }

            @Override
            public void onFailure(Call<ServiceStatusResponse> call, Throwable t) {
                Log.w("-----Error-----", t.toString());
            }
        });

        return true;
    }
}