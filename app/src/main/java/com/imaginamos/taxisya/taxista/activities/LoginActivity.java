package com.imaginamos.taxisya.taxista.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.imaginamos.taxisya.taxista.BuildConfig;
import com.imaginamos.taxisya.taxista.R;
import com.imaginamos.taxisya.taxista.io.Connectivity;
import com.imaginamos.taxisya.taxista.io.DriverService;
import com.imaginamos.taxisya.taxista.io.GooglePushNotification;
import com.imaginamos.taxisya.taxista.io.MiddleConnect;
import com.imaginamos.taxisya.taxista.model.Actions;
import com.imaginamos.taxisya.taxista.model.Car;
import com.imaginamos.taxisya.taxista.model.Conf;
import com.imaginamos.taxisya.taxista.model.Preferencias;
import com.imaginamos.taxisya.taxista.utils.Dialogos;
import com.imaginamos.taxisya.taxista.utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.fabric.sdk.android.Fabric;

public class LoginActivity extends Activity implements View.OnClickListener{

    private EditText user;
    private EditText pass;
    private Button reset_pass;
    private Button btnLogin;
   // private ImageView btn_volver;
    private String id_user, uuid, driver_id, service_id, status_id, id_driver, service;
    private ProgressDialog pDialog;
    private Conf conf;
    private int inte = 0;
    private String last_user, last_pass;
    private boolean login_automatico = false;
    private ArrayList<Car> mCars;
    private int gIndice_seleccionado = 0;
    private String mName;
    private CheckBox CheckTerms;
    private TextView Terminos;
    private String direccion;
    private int status_service = 0;
    private int car_id = 0;
    private Preferencias mPref;

    private int status = -1;

    private BroadcastReceiver mReceiver;

    @Override
    public void onRestart() {
        super.onRestart();
        overridePendingTransition(R.anim.hold, R.anim.pull_out_to_right);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.USE_CRASHLYTICS)
            Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_login);
        user = (EditText) findViewById(R.id.txtUser);
        pass = (EditText) findViewById(R.id.txtPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        CheckTerms = (CheckBox) findViewById(R.id.CheckTerms);
        Terminos = (TextView) findViewById(R.id.Terminos);
        Terminos.setOnClickListener(this);
        direccion = "";
        reset_pass = (Button) findViewById(R.id.reset_pass);

        conf = new Conf(this);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("LoginActivity", "Refreshed token: " + refreshedToken);
        uuid = refreshedToken;
        conf.setUuid(uuid);

        if (Connectivity.isConnected(this)) {
            if (conf.getUser() != null && conf.getPass() != null) {

                login_automatico = true;
                login(conf.getUser(), conf.getPass());
                btnLogin.setEnabled(false);
            //    btnRegister.setEnabled(false);

           } else {

                if (!Utils.checkPlayServices(this)) {
                    Toast.makeText(this, getString(R.string.update_play_service), Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            //new Dialogos(LoginActivity.this, R.string.error_net);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("No tiene una conexión a internet");
            alertDialog.setMessage(getString(R.string.enable_internet));
            alertDialog.setPositiveButton("Habilitar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(intent);
                        }
                    });

            alertDialog.setNegativeButton("Cancelar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), InicialActivityLogin.class);
                            startActivity(intent);
                        }
                    });
            alertDialog.show();
        }

        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(Actions.ACTION_MESSAGE_MASSIVE);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Actions.ACTION_MESSAGE_MASSIVE)) {

                    Log.v("MESSAGE_MASSIVE", "mensaje global recibido");
                    String message = intent.getExtras().getString("message");
                    mostrarMensaje(message);

                }

            }

        };

        registerReceiver(mReceiver, intentfilter);


        /*try {
            checkService();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        //id_driver = conf.getIdUser();
        //contactList = new ArrayList<>();
    }



    @Override
    public void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }

        //Intent is = new Intent(this, DriverService.class);
        //startService(is);

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    void mostrarMensaje(final String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.login_titulo_dialogo);
        builder.setMessage(message);
        builder.setNeutralButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.e("PUSH", "mensaje: " + message);
            }
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    void mostrarPlacas() {

        LayoutInflater li = LayoutInflater.from(this);

        View promptsView = li.inflate(R.layout.my_cars, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(promptsView);

        // set dialog message

        alertDialogBuilder.setTitle(R.string.login_titulo_placas);
        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        final Spinner mSpinner = (Spinner) promptsView.findViewById(R.id.spinner);
        final Button mButton = (Button) promptsView.findViewById(R.id.button);

        mSpinner.setOnItemSelectedListener(new OnSpinnerItemClicked());

        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                alertDialog.dismiss();

                // llamar update
                String car_id = mCars.get(gIndice_seleccionado).getId();

                updateCar(id_user, car_id);

            }

        });


        for (int i = 0; i < mCars.size(); i++) {
            //Log.v("CARS","car.id " + cars.get(i).id + " placa=" + cars.get(i).placa);
            Log.v("CARS", "car.id " + mCars.get(i).getId() + " placa=" + mCars.get(i).getPlaca());
        }


        List<String> r = new ArrayList<String>();
        for (int j = 0; j < mCars.size(); j++) {
            r.add(mCars.get(j).getPlaca());
        }

        ArrayAdapter cd = new ArrayAdapter(this, android.R.layout.simple_spinner_item, r);
        cd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(cd);


        // show it
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);


    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            /*case R.id.btn_volver:
                finish();
                break;*/
            case R.id.Terminos:
                direccion = "http://www.taxisya.co/Terms/TerminosTaxisya.htm";
                iraWeb(direccion);
            break;


        }
    }




    public class OnSpinnerItemClicked implements OnItemSelectedListener {


        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            // TODO Auto-generated method stub
            Log.v("CARS", "seleccionada " + String.valueOf(arg2));

            gIndice_seleccionado = arg2;

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    }

    public void loginService(View view) {


        if (!Connectivity.isConnected(this)) {
            new Dialogos(LoginActivity.this, R.string.error_net);
        } else {

            //uuid = conf.getUuid();

            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d("HomeActivity", "Refreshed token: " + refreshedToken);
            uuid = refreshedToken;

            String email = user.getText().toString();

            String password = pass.getText().toString();

            if (checklogindata(email, password)) {

                if (CheckTerms.isChecked()==false) {
                    String s = ("Acepte los terminos y condiciones");
                    Toast.makeText(this, s, Toast.LENGTH_LONG).show();
                }
                else{
                    login(email, password);
                }
            } else {
                Toast.makeText(this, R.string.login_aviso_ingreso_datos, Toast.LENGTH_SHORT).show();

            }
        }

    }

    public void registerService(View view) {
        Intent i = new Intent(LoginActivity.this, RegisterDriverActivity.class);
        startActivity(i);
        //finish();
    }

    public void resetPassword(View view) {
        Intent i = new Intent(getApplicationContext(), ResetPassActivity.class);
        startActivity(i);
    }

    public void err_login() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        Toast.makeText(getApplicationContext(), getString(R.string.error_net), Toast.LENGTH_SHORT).show();
    }

    public boolean checklogindata(String username, String password) {

        if (username.trim().equals("") || password.trim().equals("")) {
            return false;

        } else {

            return true;
        }

    }

    private void login(String user, String pass) {
        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage(getString(R.string.login_titulo_autenticando));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
//
//        GooglePushNotification google = new GooglePushNotification(getApplicationContext(), hand);

        last_pass = pass;
        last_user = user;

        loginfinal(last_user, last_pass);
        btnLogin.setEnabled(true);

    }


    private void loginfinal(final String user, final String pass) {

        MiddleConnect.login(this, user, pass, uuid, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);
                Log.e("ERROR", response + "");
                Log.v("LOGIN3", " response1 " + headers.toString() );
                Log.v("LOGIN3", " response2 " + response );

                try {

                    JSONObject responsejson = new JSONObject(response);

                    if (responsejson != null && responsejson.length() > 0) {

                        status = responsejson.getInt("error");

                        if (status == 0) {
                            id_user = responsejson.getString("id");

                            SharedPreferences prefs = getSharedPreferences("taxista", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("driver_id", id_user);
                            editor.putString("login", user);
                            editor.putString("password", pass);
                            editor.commit();

                            if (login_automatico) {
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);

                                if (responsejson.getString("name") != null) {
                                    i.putExtra("name", responsejson.getString("name"));
                                }

                                startActivity(i);

                                finish();

                            } else {

                                mName = responsejson.getString("name");
                                JSONArray lista = responsejson.getJSONArray("cars");
                                if (lista.length() > 0) {

                                    Log.v("CARS", "cars" + lista.toString());

                                    //ArrayList<Car> cars = new ArrayList<Car>();
                                    mCars = new ArrayList<Car>();


                                    for (int i = 0; i < lista.length(); i++) {
                                        JSONObject obj = lista.getJSONObject(i);
                                        Car c = new Car();

                                        c.setId(obj.getString("id"));
                                        c.setPlaca(obj.getString("placa"));
                                        //c.id = obj.getString("id");
                                        //c.placa = obj.getString("placa");
                                        //cars.add(c);
                                        mCars.add(c);
                                    }

                                }

                                mostrarPlacas();
                            }

                        } else if (status == 1) {

                            new Dialogos(LoginActivity.this,
                                    R.string.error_uno);
                        } else if (status == 2) {
                            new Dialogos(LoginActivity.this,
                                    R.string.error_dos);
                        } else if (status == 3) {
                            new Dialogos(LoginActivity.this,
                                    R.string.error_tres);
                        } else {
                            err_login();
                        }
                    }

                } catch (Exception e) {
                    err_login();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                Log.e("ERROR", response + "");
                err_login();
            }

            @Override
            public void onFinish() {
                pDialog.dismiss();
                btnLogin.setEnabled(true);      

            //    btnRegister.setEnabled(true);
            }
        });

    }


    /*public boolean checkService() throws JSONException {

        //service_id = conf.getServiceId();
        id_driver = conf.getIdUser();
        service_id = "";
        Log.v("checkService", "ini");
        Log.v("checkService", "id_driver=" + driver_id + " service_id=" + service_id);

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

                                String id = c.getString("id");
                                String user_id = c.getString("user_id");
                                String driver_id = c.getString("driver_id");
                                String status_id = c.getString("status_id");
                                String qualification = c.getString("qualification");
                                String kind_id = c.getString("kind_id");

                                /*if (id_user.equals(driver_id)){
                                    Toast.makeText(getApplicationContext(), "Servicio recuperado", Toast.LENGTH_SHORT).show();
                                } elseif(status_id.equals("2")){


                                    Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                                    startActivity(intent);


                                }else if (status_id.equals(5)|| qualification.equals(null)){
                                    Toast.makeText(getApplicationContext(), "El usuario no ha calificado el servicio.", Toast.LENGTH_SHORT).show();
                                }

                                HashMap<String, String> contact = new HashMap<>();

                                contact.put("id", id);
                                contact.put("user_id", user_id);
                                contact.put("driver_id", driver_id);
                                contact.put("status_id", status_id);
                                contact.put("qualification", qualification);
                                contact.put("kind_id", kind_id);
                                contactList.add(contact);

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
    }*/

    // update car +
    private void updateCar(final String driver_id, final String car_id) {

        MiddleConnect.update_car(this, driver_id, car_id, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.e("ERROR", "onSuccess " + response + "");

                if (response.equals("{\"error\":\"0\"}")) {

                    Log.e("ERROR", "onSuccess " + " ------- " + response);

                    Log.v("ERROR", "onStatus status = 0");
                    //id_user = responsejson.getString("id");

                    SharedPreferences prefs = getSharedPreferences("taxista", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString("driver_id", id_user);
                    //editor.putString("login", user);
                    //editor.putString("password", pass);
                    editor.commit();

                    removeTempRegister();

                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    if (mName != null) {
                        i.putExtra("name", mName);
                    }

                    startActivity(i);

                    finish();

                } else {
                    err_login();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                Log.e("ERROR", "onFailure" + response + "");
                err_login();
            }

            @Override
            public void onFinish() {
                pDialog.dismiss();
                btnLogin.setEnabled(true);
             //   btnRegister.setEnabled(true);
            }
        });

    }

// update car -

    Handler hand = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);

            String uuid_result = conf.getUuid();



            if (inte < 5) {
                if (uuid_result == null) {
                    inte++;
                    Log.v("HANDLE", "uuid nulo");
                    Log.e("INTENTO", inte + " UUID:" + uuid);

                    GooglePushNotification google = new GooglePushNotification(getApplicationContext(), hand);

                } else {
                    //uuid = uuid_result;
                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                    Log.d("HomeActivity", "Refreshed token: " + refreshedToken);
                    uuid = refreshedToken;

                    Log.v("HANDLE", "uuid " + uuid);
                    loginfinal(last_user, last_pass);
                }

            } else {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.login_aviso_error_login, Toast.LENGTH_SHORT).show();
            }

        }


    };

    public void removeTempRegister() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("register_name");
        editor.remove("register_identity");
        editor.remove("register_license");
        editor.remove("register_email");
        editor.remove("register_phone");
        editor.remove("register_cellphone");
        editor.remove("register_address");
        editor.remove("register_password");
        editor.remove("register_car_plate");
        editor.remove("register_car_brand");
        editor.remove("register_car_line");
        editor.remove("register_car_mobile_id");
        editor.remove("register_car_year");
        editor.remove("register_car_company");
        editor.remove("register_photo");
        editor.remove("register_doc1");
        editor.remove("register_doc2");
        editor.remove("register_doc3");
        editor.remove("register_doc4");

        editor.apply();
    }

    public void iraWeb (String d){
        Uri uri = Uri.parse(d);
        Intent intentNav = new Intent (Intent.ACTION_VIEW,uri);
        startActivity(intentNav);

    }

}
