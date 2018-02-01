package com.imaginamos.taxisya.taxista.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

//import com.google.android.gcm.GCMRegistrar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.imaginamos.taxisya.taxista.R;
import com.imaginamos.taxisya.taxista.io.ApiService;
import com.imaginamos.taxisya.taxista.io.Connect;
import com.imaginamos.taxisya.taxista.io.Connectivity;
import com.imaginamos.taxisya.taxista.io.GPSTracker;
import com.imaginamos.taxisya.taxista.io.MiddleConnect;
import com.imaginamos.taxisya.taxista.io.MyService;
import com.imaginamos.taxisya.taxista.model.Actions;
import com.imaginamos.taxisya.taxista.model.Conf;
import com.imaginamos.taxisya.taxista.model.ServiceStatusResponse;
import com.imaginamos.taxisya.taxista.utils.BDAdapter;
import com.imaginamos.taxisya.taxista.utils.Dialogos;
import com.imaginamos.taxisya.taxista.utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {

    private String TAG = "Splashanim";

    private String uuid, login, id_driver;
    private ProgressDialog pDialog;
    private Animation traslation;
    private ImageView fondo, nombre;
    private Animation fade_out;
    private View old_item;
    private int old_postion;
    private Conf conf;
    private double lat, lng = 0;
    private TextToSpeech tts;
    private String name = "";
    private BroadcastReceiver mReceiver;

    private AlertDialog.Builder builder;
    private int currentVersionCode;
    private String currentVersionName;
    private Boolean updateAvailable = false;
    private String service_id = null, driver_id;
    private int status_service = 0;
    private Intent intent_service;

    private LocationManager locationManager;
    private ImageView IV_Enable;
    private ImageView IV_History;
    private ImageView IV_Cerrar_Sesion;
    private BDAdapter mySQLiteAdapter;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("onCreate", "MainActivity");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        overridePendingTransition(R.anim.pull_in_from_right, R.anim.hold);

       if (!Utils.checkPlayServices(this)) {
           Toast.makeText(this, getString(R.string.update_play_service), Toast.LENGTH_SHORT).show();

       } else if (!Connectivity.isConnected(this)) {
           new Dialogos(MainActivity.this, R.string.error_net);
       }

        conf = new Conf(this);
        IV_Enable = (ImageView) findViewById(R.id.IV_Enable);
        IV_Enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (updateAvailable)
                    showDialog();
                else {
                    if (enableService()) {
                        //view.setVisibility(View.VISIBLE);
                    }

                }
            }
        });

        IV_History = (ImageView) findViewById(R.id.IV_History);
        IV_History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (updateAvailable)
                    showDialog();
                else {
                    Intent intent = new Intent(MainActivity.this, HistoricActivity.class);
                    startActivity(intent);
                    shutDown();
                }
            }
        });


        IV_Cerrar_Sesion = (ImageView) findViewById(R.id.IV_Cerrar_Sesion);
        IV_Cerrar_Sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutService();
            }
        });

        login = conf.getUser();
        id_driver = conf.getIdUser();
        driver_id = conf.getIdUser();
        service_id= conf.getServiceId();
        uuid = conf.getUuid();
        fondo = (ImageView) findViewById(R.id.fondo);
        nombre = (ImageView) findViewById(R.id.nombre_head);


        setView();

        GPSTracker gps = new GPSTracker(this);

        if (gps.canGetLocation()) {
            lat = gps.getLatitude();

            lng = gps.getLongitude();

            Log.e("POSITION", lat + ":" + lng);

        } else {
            Toast.makeText(this, "El GPS no esta habilitado", Toast.LENGTH_LONG).show();
        }

        gps.stopUsingGPS();

//    lat = MyService.latitud;
//    lng = MyService.longitud;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {

            name = getIntent().getExtras().getString("name");

            tts = new TextToSpeech(this, this);

        } catch (Exception e) {
        }

        //checkAppVersions();

        try {
            checkService();
        } catch (Exception e) {
            Log.e("--ERROR--", e.toString());
        }

// broadcast
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(Actions.ACTION_DRIVER_CLOSE_SESSION);
        intentfilter.addAction(Actions.ACTION_MESSAGE_MASSIVE);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.v("DRIVER_CLOSE_SESSION", "in MainActivity - antes de verificar");
                if (intent.getAction().equals(Actions.ACTION_DRIVER_CLOSE_SESSION)) {
                    Log.v("DRIVER_CLOSE_SESSION", "in MainActivity");
                    Toast.makeText(getApplicationContext(), "Se deshabilitó, se inicio una sesión en otro dispositivo", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);

                    conf.setPass(null);
                    conf.setUser(null);

                    startActivity(i);
                    finish();
                } else if (intent.getAction().equals(Actions.ACTION_MESSAGE_MASSIVE)) {

                    Log.v("MESSAGE_MASSIVE", "mensaje global recibido");
                    String message = intent.getExtras().getString("message");
                    mostrarMensaje(message);

                }

            }
        };
        registerReceiver(mReceiver, intentfilter);



    }

    void mostrarMensaje(final String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Información importante");
        builder.setMessage(message);
        builder.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.e("PUSH", "mensaje: " + message);
            }
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean enableService() {
        // check if gps enabled
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "El GPS no esta habilitado", Toast.LENGTH_LONG).show();
            return true;
        } else {
            setRequestedOrientation(getResources().getConfiguration().orientation);
            enable(id_driver, uuid);
            return false;
        }
    }

    private void err_logout() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        Toast.makeText(
                getApplicationContext(),
                getString(R.string.error_net),
                Toast.LENGTH_SHORT).show();
        reBuildView();
    }

    private void err_enable() {

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        vibrator.vibrate(200);

        Toast.makeText(
                getApplicationContext(),
                getString(R.string.error_net),
                Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onRestart() {

        super.onRestart();

        overridePendingTransition(R.anim.hold, R.anim.pull_out_to_right);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String status = sharedPref.getString("service", "");
        Log.v(TAG, "onActivityResult = status " + status);
        if (status != null && !TextUtils.isEmpty(status)) {
            if (status.equals("ended")) {
                enableService();
            }
        }
    }


    @SuppressWarnings("deprecation")
    private void reBuildView() {

    }

    @Override
    public void onDestroy() {
        Log.v("onDestroy", "MainActivity");
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        unregisterReceiver(mReceiver);

        super.onDestroy();
    }

    private void setView() {

//        try {

//            carousel.setOnItemClickListener(null);
//
//            carousel.setOnItemClickListener(new OnItemClickListener() {
//
//                public void onItemClick(CarouselAdapter<?> parent, final View view, final int position, long id) {
//
//                    if (carousel.getSelectedItemPosition() == position) {
//                        fade_out = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out_item);
//
//                        final CarouselItem item = (CarouselItem) parent.getChildAt(position);
//
//                        item.getImage().setAnimation(fade_out);
//
//                        fade_out.setAnimationListener(new AnimationListener() {
//
//                            @Override
//                            public void onAnimationEnd(Animation animation) {
//
//                                current_item = item;
//
//                                //item.setVisibility(View.INVISIBLE);
//                                //view.setVisibility(View.INVISIBLE);
//
//                                if (!Connectivity.isConnected(MainActivity.this)) {
//                                    //view.setVisibility(View.VISIBLE);
//
//                                    new Dialogos(MainActivity.this, R.string.error_net);
//
//                                } else {
//
//                                    switch (position) {
//
//                                        case 0:
//                                            logoutService();
//                                            break;
//                                        case 1:
//                                            if (updateAvailable)
//                                                showDialog();
//                                            else {
//                                                if (enableService()) {
//                                                    //view.setVisibility(View.VISIBLE);
//                                                }
//
//                                            }
//                                            break;
//                                        case 2:
//                                            if (updateAvailable)
//                                                showDialog();
//                                            else {
//                                                Intent intent = new Intent(MainActivity.this, HistoricActivity.class);
//                                                startActivity(intent);
//                                                shutDown();
//                                            }
//                                            break;
//
//                                    }
//                                }
//
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(Animation arg0) {
//                            }
//
//                            @Override
//                            public void onAnimationStart(Animation arg0) {
//                            }
//
//                        });
//
//                    } else {
//                        carousel.setSelection(position, true);
//                    }
//
//                }
//
//            });

//            carousel.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//                public void onItemSelected(CarouselAdapter<?> parent,
//                                           View view, int position, long id) {
//
//                    if (old_item != null) {
//                        try {
//                            if (old_postion == 0) {
//                                ((ImageView) old_item
//                                        .findViewById(R.id.item_image))
//                                        .setImageResource(R.drawable.cerrar_normal);
//
//                            } else if (old_postion == 1) {
//                                ((ImageView) old_item
//                                        .findViewById(R.id.item_image))
//                                        .setImageResource(R.drawable.habilitarme_normal);
//                            } else if (old_postion == 2) {
//
//                                ((ImageView) old_item
//                                        .findViewById(R.id.item_image))
//                                        .setImageResource(R.drawable.historial_normal);
//                            }
//                        } catch (Exception e) {
//
//                        }
//                    }
//
//                    old_postion = position;
//
//                    old_item = view;
//
//                    try {
//                        if (position == 0) {
//                            ((ImageView) view.findViewById(R.id.item_image))
//                                    .setImageResource(R.drawable.cerrar_over);
//
//                        } else if (position == 1) {
//                            ((ImageView) view.findViewById(R.id.item_image))
//                                    .setImageResource(R.drawable.habilitarme_over);
//                        } else if (position == 2) {
//
//                            ((ImageView) view.findViewById(R.id.item_image))
//                                    .setImageResource(R.drawable.historial_over);
//                        }
//                    } catch (Exception e) {
//                    }
//
//                }
//
//                public void onNothingSelected(CarouselAdapter<?> parent) {
//
//                }
//
//            });
//
//        } catch (Exception e) {
//            Log.e(TAG, "" + e.toString());
//        }
//
    }

    private void logoutService() {

        MiddleConnect.logout(this, login, uuid, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage(getString(R.string.saliendo_aplicacion));
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                Log.e("ERROR", "" + response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.e("ERROR", "" + response);
                try {
                    JSONObject reponsejson = new JSONObject(response);

                    if (checkLogout(reponsejson)) {

                        conf.setPass(null);
                        conf.setUser(null);

                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                        shutDown();
                        finish();

                    } else {
                        err_logout();
                    }
                } catch (Exception e) {
                    err_logout();
                }

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

    private boolean checkLogout(JSONObject json) {

        int logstatus = -1;

        if (json != null && json.length() > 0) {

            JSONObject json_data;
            try {
                json_data = json;
                logstatus = json_data.getInt("error");
                Log.e("error: ", String.valueOf(logstatus));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (logstatus == 0) {// [{"logstatus":"0"}]

                Log.e("Logout: ", "valido");
//                GCMRegistrar.unregister(this);
//                Log.e("info", "unregistered....." + GCMRegistrar.getRegistrationId(this));
                return true;

            } else {// [{"logstatus":"1"}]
                Log.e("Logout: ", "invalido");
                return false;
            }

        }
        else { // json obtenido invalido verificar parte WEB.
            Log.e("JSON LOGOUT:  ", "ERROR");
            return false;
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
                    pDialog = new ProgressDialog(MainActivity.this);
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


                            Intent i = new Intent(MainActivity.this, WaitServiceActivity.class);

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

    private Boolean checkAppVersions() {

        MiddleConnect.getAppVersions(this, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                try {
                    pDialog = new ProgressDialog(MainActivity.this);
                    pDialog.setMessage(getString(R.string.verificando));
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
                    Log.d(TAG, "appVersions = " + repsonsejson.toString());

                    if (repsonsejson.has("driverVersions")) {
                        JSONArray arrayApp = repsonsejson.getJSONArray("driverVersions");
                        JSONObject driver = arrayApp.getJSONObject(0);
                        Log.d(TAG, "driverVersions = " + driver.getString("version"));
                        PackageInfo pckginfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
                        currentVersionName = pckginfo.versionName;

                        float current = Float.valueOf(currentVersionName);
                        Float remote = Float.parseFloat(driver.getString("version"));
                        Log.d(TAG, "current = " + String.valueOf(current) + " remote = " + remote);
                        if (current < remote) {
                            Log.d(TAG, "la versión instalada esta desactualizada");
                            updateAvailable = true;
                           /*
                           builder = new AlertDialog.Builder(MainActivity.this);
                           builder.setMessage(getString(R.string.message_app_version))
                               .setTitle(getString(R.string.title_app_version))
                               .setPositiveButton(getString(R.string.button_app_version), new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                   dialog.cancel();

                                   final String appPackageName = getApplicationContext().getPackageName();
                                   try {
                                       startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));

                                   }
                                   catch (android.content.ActivityNotFoundException anfe) {
                                       startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                                   }

                               }
                            });

                            builder.create();

                            builder.show();
                            */
                        } else {
                            Log.d(TAG, "la versión instalada es la última");
                        }
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                Log.e(TAG, "" + response);
            }

            @Override
            public void onFinish() {
                try {
                    pDialog.dismiss();
                } catch (Exception e) {
                }
            }
        });

        return false;
    }

    public boolean checkService() throws JSONException {

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

        Log.i("DRIVER ID",driver_id);
        call_profile = service.serviceStatus(driver_id);
        call_profile.enqueue(new retrofit2.Callback<ServiceStatusResponse>() {
            @Override
            public void onResponse(Call<ServiceStatusResponse> call, retrofit2.Response<ServiceStatusResponse> response) {

                try {

                    int status_service = Integer.parseInt(response.body().getStatus_id());

                    if ((status_service == 2) || (status_service == 4)) {

                        Intent intent = new Intent(MainActivity.this, MapActivity.class);
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

                        enable_position_service();
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
                        }
                    } else {
                        Log.v("MainActivity", "checkService() servicio asignado no tenia");
                        Log.v("MainActivity", "responsejson = " + response.body().getDriver().toString());

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
//        MiddleConnect.checkStatusService(this, id_driver, service_id, "uuid", new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onStart() {
//                Log.v("checkService", "onStart");
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                String response = new String(responseBody);
//                try {
//                    Log.w("Service Response" , response);
//                    //Log.v("checkService", "SUCCES: "+response);
//                    JSONObject responsejson = new JSONObject(response);
//                    //if (responsejson.getInt("status_id"))
//                    //{
//                    status_service = responsejson.getInt("status_id");
//                    Log.v("checkService", "status_id: " + String.valueOf(status_service));
//
//
//                    // si hay un servicio asignado lo recupera
//                    if ((status_service == 2) || (status_service == 4)) {
//                        Log.v("MainActivity", "checkService() servicio asignado recuperado");
//                        Log.v("MainActivity", "responsejson = " + responsejson.toString());
//                        Log.v("MainActivity", "responsejson = " + responsejson.getJSONObject("driver").toString());
//
//                        Log.v("MainActivity", "responsejson id = " + responsejson.getString("id"));
//                        Log.v("MainActivity", "responsejson lat = " + responsejson.getString("from_lat"));
//
//                        Intent intent = new Intent(MainActivity.this, MapActivity.class);
//                        intent.putExtra("lat", Double.parseDouble(responsejson.getString("from_lat")));
//                        intent.putExtra("lng", Double.parseDouble(responsejson.getString("from_lng")));
//
//                        intent.putExtra("id_servicio", responsejson.getString("id"));
//                        Log.v("MainActivity", "responsejson schedule_type = " + responsejson.getString("schedule_type"));
//
//                        String type = String.valueOf(responsejson.getString("schedule_type"));
//                        String direccion = "";
//                        if ((type.equals("2")) || (type.equals("3"))) {
//                            String serviceDateTime = responsejson.getString("service_date_time");
//                            String substr = serviceDateTime.substring(11, 16);
//
//                            direccion = responsejson.getString("index_id") + " - " +
//                                    responsejson.getString("comp1") + " # " +
//                                    responsejson.getString("comp2") + " - " +
//                                    responsejson.getString("no") + " " +
//                                    responsejson.getString("obs") + " Barrio: " + responsejson.getString("barrio") +
//                                    "\n" +
//                                    responsejson.getString("destination") +
//                                    " Hora: " + substr;
//                        } else {
//                            // determina nuevo formato
//                            String cad = responsejson.getString("index_id");
//                            //if (cad != null && cad != "") {
//                            if (cad.length() > 0) {
//                                direccion = responsejson.getString("index_id") + " - " + responsejson.getString("comp1") + " # " + responsejson.getString("comp2") + " - " + responsejson.getString("no") + " " + responsejson.getString("obs") + " Barrio: " + responsejson.getString("barrio");
//                            } else {
//                                //direccion = responsejson.getString("no");
//                                direccion = responsejson.getString("no") + " Barrio: " + responsejson.getString("barrio");
//                            }
//                        }
//
//                        enable_position_service();
//
//
//                        intent.putExtra("direccion", direccion);
//                        intent.putExtra("status_service",status_service);
//                        intent.putExtra("kind_id", responsejson.getInt("schedule_id"));
//                        intent.putExtra("schedule_type", responsejson.getInt("schedule_type"));
//                        intent.putExtra("name", responsejson.getString("index_id"));
//                        intent.putExtra("pay_type", responsejson.getString("pay_type"));
//                        intent.putExtra("card_reference", responsejson.getString("card_reference"));
//                        intent.putExtra("code", responsejson.getString("code"));
//
//                        startActivity(intent);
//                        //finish();
//
//                    } else if (status_service == 5) {
//                        if (responsejson.isNull("qualification")) {
//                            Log.v("MainActivity", "checkService() servicio asignado recuperado sin calificar");
//                        }
//                    } else {
//                        Log.v("MainActivity", "checkService() servicio asignado no tenia");
//                        Log.v("MainActivity", "responsejson = " + responsejson.getJSONObject("driver").toString());
//
//                    }
//
//
//                } catch (Exception e) {
//                    Log.v("checkService", "Problema json" + e.toString());
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                String response = new String(responseBody);
//                Log.v("checkService", "onFailure");
//
//            }
//
//            @Override
//            public void onFinish() {
//
//                Log.v("checkService", "onFinish");
//
//            }
//
//        });


        if (status_service == 2)
            return true;
        else
            return false;

    }


    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            Locale locSpanish = Locale.getDefault();

            tts.isLanguageAvailable(locSpanish);

            int result = 0 ;

            try {
                result = tts.setLanguage(locSpanish);
            }catch (Exception e ){
                result = TextToSpeech.LANG_NOT_SUPPORTED;
            }

            tts.setPitch(1);

            tts.setSpeechRate(Utils.getVoiceSpeedPreference(getApplicationContext()));

            if (result != TextToSpeech.LANG_MISSING_DATA || result != TextToSpeech.LANG_NOT_SUPPORTED) {
                try {

                    tts.speak(getString(R.string.welcome, name), TextToSpeech.QUEUE_FLUSH, null);

                } catch (Exception e) {
                    Log.e("error", "" + e.toString());
                }
            }

        } else {
            Log.e("TTS", "Initilization Failed");
        }
    }

    private void shutDown() {

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

    }

    private void showDialog() {
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getString(R.string.message_app_version))
                .setTitle(getString(R.string.title_app_version))
                .setPositiveButton(getString(R.string.button_app_version), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        final String appPackageName = getApplicationContext().getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));

                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                        }

                    }
                });

        builder.create();

        builder.show();
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

        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public void savePreferencias(String key, String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }


}
