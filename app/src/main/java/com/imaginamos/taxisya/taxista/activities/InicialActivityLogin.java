package com.imaginamos.taxisya.taxista.activities;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.Toast;

import com.carouseldemo.controls.CarouselItem;
import com.imaginamos.taxisya.taxista.R;
import com.crashlytics.android.Crashlytics;
import com.imaginamos.taxisya.taxista.BuildConfig;
import com.imaginamos.taxisya.taxista.io.MiddleConnect;
import com.imaginamos.taxisya.taxista.io.MyService;
import com.imaginamos.taxisya.taxista.model.Conf;
import com.imaginamos.taxisya.taxista.model.Preferencias;
import com.imaginamos.taxisya.taxista.utils.BDAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import io.fabric.sdk.android.Fabric;

import static android.R.attr.value;

/**
 * Created by TaxisYa on 14/10/16.
 */

public class InicialActivityLogin extends Activity implements OnClickListener  {

    private Button btnLogin;
    private Button btnRegister;
    private CarouselItem current_item;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("onCreate", "InicialActivityLogin");
        super.onCreate(savedInstanceState);
        if (BuildConfig.USE_CRASHLYTICS)
            Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_inicial);

        conf = new Conf(this);
        login = conf.getUser();
        id_driver = conf.getIdUser();
        uuid = conf.getUuid();

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

    }

    private void enable() {
        Log.v("MainActivity", "enable");

     //   clearServices();
//        MiddleConnect.enableDrive(this, lat, lng, driver_id, id, new AsyncHttpResponseHandler() {

        MiddleConnect.enableDrive(this, lat, lng, driver_id, id, new AsyncHttpResponseHandler() {

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

                            savePreferencias("servicieTomado", "false");

                            Intent i = new Intent(InicialActivityLogin.this, WaitServiceActivity.class);

                            if (name != null && name != "") {
                                i.putExtra("name", name);
                            }

                            if (repsonsejson.has("services")) {
                                i.putExtra("services", repsonsejson.getString("services"));
                            }

                            //startActivity(i);
                            startActivityForResult(i, 1000);

                            shutDown();

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

        //reBuildView();
        if (current_item != null) {
            current_item.setVisibility(View.VISIBLE);
        }

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

        //service_id = conf.getServiceId();
        id_driver = conf.getIdUser();
        service_id = "";
        Log.v("checkService", "ini");
        //Log.v("checkService", "id_driver=" + driver_id + " service_id=" + service_id);

        MiddleConnect.checkStatusService(this, driver_id, service_id, "uuid", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                Log.v("checkService", "onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);

                try {

                    JSONObject responsejson = new JSONObject(response);
                    JSONArray services = responsejson.getJSONArray("services");

                    // looping through All Services
                    for (int i = 0; i < services.length(); i++) {
                        JSONObject c = services.getJSONObject(i);

                        String service_id = c.getString("id");
                        String user_id = c.getString("user_id");
                        String driver_id = c.getString("driver_id");
                        String status_id = c.getString("status_id");
                        String address = c.getString("address");
                        String from_lat = c.getString("from_lat");
                        String from_lng = c.getString("from_lng");
                        String pay_type = c.getString("pay_type");
                        String pay_reference = c.getString("pay_reference");
                        String qualification = c.getString("qualification");
                        String barrio = c.getString("barrio");
                        String rCode = c.getString("code");

                        if(id_driver == null){

                        }

                        else if (id_driver.equals(driver_id)) {

                            if (status_id.equals("2") || status_id.equals("4")) {

                                Intent intent = new Intent(InicialActivityLogin.this, RecoveryMapActivity.class);
                                intent.putExtra("address",address);
                                intent.putExtra("from_lat",from_lat);
                                intent.putExtra("from_lng",from_lng);
                                intent.putExtra("pay_type",pay_type);
                                intent.putExtra("user_id",user_id);
                                intent.putExtra("pay_reference",pay_reference);
                                intent.putExtra("status_id",status_id);
                                intent.putExtra("barrio",barrio);
                                intent.putExtra("service_recoverid",service_id);
                                intent.putExtra("rCode",rCode);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Servicio recuperado", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if (status_id.equals("5") && qualification.equals(null)) {
                            Toast.makeText(getApplicationContext(), "El usuario no ha calificado el servicio.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    return;

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //  String response = new String(responseBody);
                // Log.v("checkService", "onFailure = " + response);

            }

            @Override
            public void onFinish() {
                Log.v("checkService", "onFinish");
            }

        });
        return true;
    }
}