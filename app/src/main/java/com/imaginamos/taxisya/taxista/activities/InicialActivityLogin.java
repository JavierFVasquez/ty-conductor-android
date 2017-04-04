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
import com.imaginamos.taxisya.taxista.utils.BDAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.fabric.sdk.android.Fabric;

/**
 * Created by TaxisYa on 14/10/16.
 */

public class InicialActivityLogin extends Activity implements OnClickListener  {

    private Button btnLogin;
    private Button btnRegister;
    private CarouselItem current_item;
    private String service_id, id_driver, login, uuid;
    private int status_service = 0;
    private Intent intent_service;
    private Conf conf;
    private BDAdapter mySQLiteAdapter;
    private double lat, lng = 0;
    private ProgressDialog pDialog;
    private String TAG = "Splashanim";
    private String name = "";
    private TextToSpeech tts;


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

    @Override
    public void onRestart() {

        super.onRestart();
        try {
            checkService();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            checkService();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void clearServices() {
        Log.v("MainActivity","clearServices");
        mySQLiteAdapter = new BDAdapter(this);
        mySQLiteAdapter.openToWrite();
        mySQLiteAdapter.deleteAllServices();
        mySQLiteAdapter.close();
    }

    private void enable(String driver_id, String id) {
        Log.v("MainActivity", "enable");

        clearServices();
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
                            err_enable();
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
                    err_enable();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                Log.e(TAG, "" + response);
                err_enable();
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

        Toast.makeText(
                getApplicationContext(),
                getString(R.string.error_net),
                Toast.LENGTH_SHORT).show();

        //reBuildView();
        if (current_item != null) {
            current_item.setVisibility(View.VISIBLE);
        }

    }

    public boolean checkService() throws JSONException {

        id_driver = conf.getIdUser();
        service_id = "";
        Log.v("checkService", "ini");
        Log.v("checkService", "id_user=" + id_driver + " service_id=" + service_id);
            MiddleConnect.checkStatusService(this, id_driver, service_id, "uuid", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                Log.v("checkService", "onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {

                    JSONObject responsejson = new JSONObject(response);

                    status_service = responsejson.getInt("status_id");
                    Log.v("checkService", "status_id: " + String.valueOf(status_service));

                    // si hay un servicio asignado lo recupera
                    if ((status_service == 2) || (status_service == 4)) {

                        service_id = responsejson.getString("id");
                        conf.setServiceId(service_id);

                        Intent intent = new Intent(InicialActivityLogin.this, MapActivity.class);
                        intent.putExtra("lat", Double.parseDouble(responsejson.getString("from_lat")));
                        intent.putExtra("lng", Double.parseDouble(responsejson.getString("from_lng")));
                        intent.putExtra("id_servicio", responsejson.getString("id"));

                        Log.v("InicialActivityLogin", "checkService() servicio asignado recuperado");
                        Log.v("InicialActivityLogin", "responsejson = " + responsejson.toString());
                        Log.v("InicialActivityLogin", "responsejson = " + responsejson.getJSONObject("driver").toString());
                        Log.v("InicialActivityLogin", "responsejson id = " + responsejson.getString("id"));
                        Log.v("InicialActivityLogin", "responsejson lat = " + responsejson.getString("from_lat"));
                        Log.v("InicialActivityLogin", "responsejson schedule_type = " + responsejson.getString("schedule_type"));

                        String type = String.valueOf(responsejson.getString("schedule_type"));
                        String direccion = "";

                        if ((type.equals("2")) || (type.equals("3"))) {
                            String serviceDateTime = responsejson.getString("service_date_time");
                            String substr = serviceDateTime.substring(11, 16);

                            direccion = responsejson.getString("index_id") + " - " +
                                    responsejson.getString("comp1") + " # " +
                                    responsejson.getString("comp2") + " - " +
                                    responsejson.getString("no") + " " +
                                    responsejson.getString("obs") + " Barrio: " + responsejson.getString("barrio") +
                                    "\n" +
                                    responsejson.getString("destination") +
                                    " Hora: " + substr;
                        } else {
                            // determina nuevo formato
                            String cad = responsejson.getString("index_id");
                            //if (cad != null && cad != "") {
                            if (cad.length() > 0) {
                                direccion = responsejson.getString("index_id") + " - " + responsejson.getString("comp1") + " # " + responsejson.getString("comp2") + " - " + responsejson.getString("no") + " " + responsejson.getString("obs") + " Barrio: " + responsejson.getString("barrio");
                            } else {
                                //direccion = responsejson.getString("no");
                                direccion = responsejson.getString("no") + " Barrio: " + responsejson.getString("barrio");
                            }
                        }

                        enable_position_service();

                        intent.putExtra("direccion", direccion);
                        intent.putExtra("status_service",status_service);
                        intent.putExtra("kind_id", responsejson.getInt("schedule_id"));
                        intent.putExtra("schedule_type", responsejson.getInt("schedule_type"));
                        intent.putExtra("name", responsejson.getString("index_id"));
                        intent.putExtra("pay_type", responsejson.getString("pay_type"));
                        intent.putExtra("card_reference", responsejson.getString("card_reference"));
                        intent.putExtra("code", responsejson.getString("code"));

                        startActivity(intent);
                        //finish();

                    } else if (status_service == 5) {
                        if (responsejson.isNull("qualification")) {
                            Log.v("MainActivity", "checkService() servicio asignado recuperado sin calificar");
                        }
                    } else {
                        Log.v("InicialActivityLogin", "checkService() servicio asignado no tenia");
                        Log.v("InicialActivityLogin", "responsejson = " + responsejson.getJSONObject("driver").toString());

                    }


                } catch (Exception e) {
                    Log.v("checkService", "Problema json" + e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                Log.v("checkService", "onFailure");
                //Toast.makeText(getApplicationContext(), "test 1", Toast.LENGTH_SHORT).show();
                onFinish();
            }

            @Override
            public void onFinish() {
                Log.v("checkService", "onFinish");
            }

        });
        if (status_service == 2)
            return true;
        else
            return false;

    }
    private void enable_position_service() {

        if (intent_service == null && !isMyServiceRunning()) {
            intent_service = new Intent(this, MyService.class);
            intent_service.putExtra("driver_id", id_driver);
            intent_service.putExtra("uuid", uuid);
            startService(intent_service);
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
}