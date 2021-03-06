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
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imaginamos.taxisya.taxista.GcmKeepAlive;
import com.imaginamos.taxisya.taxista.R;
import com.imaginamos.taxisya.taxista.io.ApiConstants;
import com.imaginamos.taxisya.taxista.io.ApiService;
import com.imaginamos.taxisya.taxista.io.Connect;
import com.imaginamos.taxisya.taxista.io.Connectivity;
import com.imaginamos.taxisya.taxista.io.MiddleConnect;
import com.imaginamos.taxisya.taxista.io.MyService;
import com.imaginamos.taxisya.taxista.io.UpdateReceiver;
import com.imaginamos.taxisya.taxista.model.Actions;
import com.imaginamos.taxisya.taxista.model.CompaniesListResponse;
import com.imaginamos.taxisya.taxista.model.Conf;
import com.imaginamos.taxisya.taxista.model.MessageEvent;
import com.imaginamos.taxisya.taxista.model.ServiceStatusResponse;
import com.imaginamos.taxisya.taxista.utils.BDAdapter;
import com.imaginamos.taxisya.taxista.utils.Dialogos;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.paymentez.androidsdk.PaymentezSDKClient;
import com.paymentez.androidsdk.models.DebitCardResponseHandler;
import com.paymentez.androidsdk.models.PaymentezDebitParameters;
import com.paymentez.androidsdk.models.PaymentezResponseDebitCard;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static io.fabric.sdk.android.Fabric.TAG;

//public class MapActivity extends FragmentActivity implements OnClickListener, LocationListener {
public class RecoveryMapActivity extends Activity implements OnClickListener, LocationListener, UpdateReceiver.UpdateReceiverListener, Connectivity.ConnectivityQualityCheckListener, OnMapReadyCallback {

    PaymentezSDKClient paymentezsdk;
    public TextView view_direccion, nombre, direccion;
    private ImageView icon_radio_ope;
    private ProgressDialog pDialog;
    private Button btnLlegada;
    private Button btnCancelar;
    private Button btnFinalizar;
    public static Button btn_pay;
    private Button btnConfirmCode;
    private EditText mUnits;
    private String sUnits;
    private CheckBox mCheck1;
    private CheckBox mCheck2;
    private CheckBox mCheck3;
    private TextView mTotValue;
    private EditText mCode;
    private GoogleMap map;
    private ArrayList<LatLng> markerPoints;
    public double latitud;
    public double longitud = 0;
    public double userLat;
    public double userLng;
    private String driver_id;
    private String service_id;
    private String mTotUnits = "0";
    private String mTotCharge1 = "0";
    private String mTotCharge2 = "0";
    private String mTotCharge3 = "0";
    private String mTotCharge4 = "0";
    private String mTotService = "0";
    private String mTransactionId = "";
    private String mUserPhone = "";
//    private BroadcastReceiver mReceiver;
    private UpdateReceiver mNetworkMonitor;
    private ImageView mConnectivityLoaderImage;
    private RelativeLayout mNoConnectivityPanel;
    private Connectivity connectivityChecker = new Connectivity(this);
    private Timer myTimer = new Timer();
    private int reintento = 0;
    private int status = 0;
    private Conf conf;
    private int rand_id = -1;
    private int type_agend = -1;
    private int status_service = 0;
    private String mServiceId;
    private int mStatusOld;
    private BDAdapter mySQLiteAdapter;
    private Cursor mCursor;
    private boolean isFinished = true;
    private LatLng mCliente;
    private int mPayType = 1;
    private String mUserId;
    private String mUserCardReference;
    private String mUserEmail;
    private LinearLayout mLinear1;
    private LinearLayout mLinear2;
    public String from_lng, from_lat, service_recoverid, reCode;
    private GcmKeepAlive gcmKeepAlive;
    private Button BT_Chat;

    private long mTotalTrip = 0;
    private boolean first_time = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {


        if (event.getAction().equals(Actions.ACTION_USER_CANCELED_SERVICE)) {
            Toast.makeText(getApplicationContext(), getString(R.string.error2), Toast.LENGTH_LONG).show();
            toFinish();

        } else if (event.getAction().equals(Actions.ACTION_OPE_CANCELED_SERVICER)) {
            Toast.makeText(getApplicationContext(), getString(R.string.oper_cancel), Toast.LENGTH_LONG).show();
            toFinish();

        } else if (event.getAction().equals(Actions.ACTION_DRIVER_CLOSE_SESSION)) {
            Log.v("DRIVER_CLOSE_SESSION", "in MapActivity");
            Toast.makeText(getApplicationContext(), R.string.login_deshabilito_login_otro_dispositivo, Toast.LENGTH_LONG).show();

            loggedInOtherDevie();

        } else if (event.getAction().equals(Actions.ACTION_MESSAGE_MASSIVE)) {

            Log.v("MESSAGE_MASSIVE", "mensaje global recibido");
            String message = event.getMessage();
            mostrarMensaje(message);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("onCreate", "RecoveryMapActivity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_map);
        paymentezsdk = new PaymentezSDKClient(this, ApiConstants.api_env, ApiConstants.app_code, ApiConstants.app_secret_key);

        try {
            overridePendingTransition(R.anim.pull_in_from_right, R.anim.hold);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        } catch (Exception e) {
        }

        conf = new Conf(this);

        mySQLiteAdapter = new BDAdapter(this);
        mySQLiteAdapter.openToWrite();

        if (!Connectivity.isConnected(this)) {
            new Dialogos(RecoveryMapActivity.this, R.string.error_net);
        }



        btnLlegada = (Button) findViewById(R.id.btnLlegada);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        btnFinalizar = (Button) findViewById(R.id.btnFinalizar);
        btn_pay = (Button) findViewById(R.id.btn_pay);
        btnConfirmCode = (Button) findViewById(R.id.btnConfirmCode);
        mUnits = (EditText) findViewById(R.id.totUnits);
        // mnits = (EditText) findViewById(R.id.totUnits);
        mCode = (EditText) findViewById(R.id.etCodeAuthorization);
        mCheck1 = (CheckBox) findViewById(R.id.chkRecargo1);
        mCheck2 = (CheckBox) findViewById(R.id.chkRecargo2);
        mCheck3 = (CheckBox) findViewById(R.id.chkRecargo3);
        mTotValue = (TextView) findViewById(R.id.totViaje);
        mNoConnectivityPanel = (RelativeLayout) findViewById(R.id.layout_no_connectivity);
        mConnectivityLoaderImage = (ImageView) findViewById(R.id.loader_icon);
        btnLlegada.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
        btnFinalizar.setOnClickListener(this);
        btn_pay.setOnClickListener(this);
        btnConfirmCode.setOnClickListener(this);
        mLinear1 = (LinearLayout) findViewById(R.id.layout_pay);
        mLinear2 = (LinearLayout) findViewById(R.id.layout_code_authorization);
        icon_radio_ope = (ImageView) findViewById(R.id.icon_radio_ope);
        nombre = (TextView) findViewById(R.id.nombre_cliente);
        //view_direccion = (TextView) findViewById(R.id.direccion_cliente);
        direccion = (TextView) findViewById(R.id.address);


        markerPoints = new ArrayList<LatLng>();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String from_lng = extras.getString("from_lng");
            String from_lat = extras.getString("from_lat");
            String pay_type = extras.getString("pay_type");
            String address = extras.getString("address");
            String user_id = extras.getString("user_id");
            String status_id = extras.getString("status_id");
            String pay_reference = extras.getString("pay_reference");
            String barrio = extras.getString("barrio");
            String service_id = extras.getString("service_recoverid");
            //String rCode = extras.getString("rCode");

            nombre.setText(getString(R.string.waitservice_texto_barrio2) + extras.getString("barrio"));
            direccion.setText(extras.getString("address"));
            userLat = Double.parseDouble(from_lat);
            userLng = Double.parseDouble(from_lng);

            if(status_id.equals("2")){

                btnLlegada.setVisibility(View.VISIBLE);
                btnCancelar.setVisibility(View.VISIBLE);
                btnFinalizar.setVisibility(View.GONE);
                btnConfirmCode.setVisibility(View.GONE);

            } else if(status_id.equals("4") || pay_reference.equals("VALE")){
                btnLlegada.setVisibility(View.GONE);
                btnCancelar.setVisibility(View.GONE);
                btnFinalizar.setVisibility(View.GONE);
                btnConfirmCode.setVisibility(View.VISIBLE);

            }
        }

        Bundle reicieveParams = getIntent().getExtras();
        latitud = reicieveParams.getDouble("lat");
        longitud = reicieveParams.getDouble("lng");
        service_id = reicieveParams.getString("service_recoverid");


        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:254682418821:android:a62d955d84b45b78") // Required for Analytics.
                .setApiKey("AIzaSyDUvcRAQqBsyMlmJ-kZ_nGaqCwMNSNxKfI") // Required for Auth.
                .setDatabaseUrl("https://taxis-ya-usuario-android.firebaseio.com/") // Required for RTDB.
                .build();

        FirebaseApp.initializeApp(this /* Context */, options, "secondary");


        BT_Chat = (Button) findViewById(R.id.BT_Chat);

        FirebaseApp secondary = FirebaseApp.getInstance("secondary");
        FirebaseDatabase database = FirebaseDatabase.getInstance(secondary);
        DatabaseReference chat_ref = database.getReference("chat/taxi_user/"+service_id);


//        chat_ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(first_time) {
//                    BT_Chat.setBackgroundResource(R.drawable.orange_border);
//                    BT_Chat.setTextColor(ResourcesCompat.getColor(getResources(), R.color.text_orange, null));
//                    Log.i("Chat", "New Message");
//                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                    vibrator.vibrate(500);
//                    Toast.makeText(RecoveryMapActivity.this, R.string.new_message, Toast.LENGTH_SHORT).show();
//                }
//                first_time = true;
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e("The read failed: " , String.valueOf(databaseError.getCode()));
//            }
//        });

        BT_Chat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RecoveryMapActivity.this,ChatActivity.class);
                Bundle b = new Bundle();
                b.putString("service_id", service_id);
                i.putExtras(b);
                startActivity(i);
            }
        });

        Log.v("MapActivity", "latitud = " + String.valueOf(latitud) + " longitud = " + String.valueOf(longitud));


        if (rand_id == 3) {
            icon_radio_ope.setVisibility(View.VISIBLE);
        } else if (type_agend != 0) {
            if (type_agend == 1) {
                icon_radio_ope.setImageResource(R.drawable.aero_over);
            } else if (type_agend == 2) {
                icon_radio_ope.setImageResource(R.drawable.fuerab_over);
            } else if (type_agend == 3) {
                icon_radio_ope.setImageResource(R.drawable.mensajeria_over);
            } else if (type_agend == 4) {
                icon_radio_ope.setImageResource(R.drawable.horas_over);
            }
            icon_radio_ope.setVisibility(View.VISIBLE);
        }

        if (status_service == 4) {
            btnLlegada.setVisibility(View.GONE);
            btnCancelar.setVisibility(View.GONE);
            btnFinalizar.setVisibility(View.VISIBLE);
        }

        driver_id = conf.getIdUser();

//        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        MapFragment fm = (MapFragment) getFragmentManager().findFragmentById(R.id.map);


        //map = fm.getMap();
        fm.getMapAsync(this);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria(); // object to retrieve provider
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        String provider = locationManager.getBestProvider(criteria, true);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 0, (LocationListener) this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 200, 0, (LocationListener) this);

//        IntentFilter intentfilter = new IntentFilter();
//        intentfilter.addAction(Actions.ACTION_USER_CANCELED_SERVICE);
//        intentfilter.addAction(Actions.ACTION_OPE_CANCELED_SERVICER);
//        intentfilter.addAction(Actions.ACTION_DRIVER_CLOSE_SESSION);
//        intentfilter.addAction(Actions.ACTION_MESSAGE_MASSIVE);
//
//        mReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                if (intent.getAction().equals(Actions.ACTION_USER_CANCELED_SERVICE)) {
//                    Toast.makeText(getApplicationContext(), getString(R.string.error2), Toast.LENGTH_LONG).show();
//                    toFinish();
//
//                } else if (intent.getAction().equals(Actions.ACTION_OPE_CANCELED_SERVICER)) {
//                    Toast.makeText(getApplicationContext(), getString(R.string.oper_cancel), Toast.LENGTH_LONG).show();
//                    toFinish();
//
//                } else if (intent.getAction().equals(Actions.ACTION_DRIVER_CLOSE_SESSION)) {
//                    Log.v("DRIVER_CLOSE_SESSION", "in MapActivity");
//                    Toast.makeText(getApplicationContext(), R.string.login_deshabilito_login_otro_dispositivo, Toast.LENGTH_LONG).show();
//
//                    loggedInOtherDevie();
//
//                } else if (intent.getAction().equals(Actions.ACTION_MESSAGE_MASSIVE)) {
//
//                    Log.v("MESSAGE_MASSIVE", "mensaje global recibido");
//                    String message = intent.getExtras().getString("message");
//                    mostrarMensaje(message);
//
//                }
//
//            }
//
//        };
//
//        registerReceiver(mReceiver, intentfilter);
        validateService();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mUnits.length() > 0) {
                    calcValor();
                }

            }
        };
        mUnits.addTextChangedListener(textWatcher);
        gcmKeepAlive = new GcmKeepAlive(this);
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            Log.e(TAG, "LAT -> :" + location.getProvider() + "->" + location.getLatitude() + " LOG" + location.getLongitude());
            longitud = location.getLongitude();
            latitud = location.getLatitude();

            Log.e(TAG, "sendMyPosition");
            MiddleConnect.sendMyPosition(this, driver_id, String.valueOf(latitud), String.valueOf(longitud), new AsyncHttpResponseHandler()
            {
                @Override
                public void onStart() {
                    //Log.e(AsyncHttpResponseHandler.TAG, "onStart");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String response = new String(responseBody);
                    //Log.e(AsyncHttpResponseHandler.TAG, "onSuccess" + response);

                    gcmKeepAlive.broadcastIntents();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    // String response = new String(responseBody);
                    //Log.e(AsyncHttpResponseHandler.TAG, "onFailure" );
                }

                @Override
                public void onFinish() {
                    //Log.e(AsyncHttpResponseHandler.TAG, "onFinish");
                }
            });
//            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        mCliente = new LatLng(userLat, userLng);
        map.setMyLocationEnabled(true);
        markerPoints.add(mCliente);

        MarkerOptions options = new MarkerOptions();
        Log.v("MapActivity", "driver_id = " + String.valueOf(driver_id) + " cliente =" + mCliente.toString());
        Log.v("MapActivity", "option.position(cliente) = " + String.valueOf(latitud) + " , " + String.valueOf(longitud));
        Log.v("MapActivity", "MyService = " + String.valueOf(MyService.latitud) + " , " + String.valueOf(MyService.longitud));

        CameraPosition camPos = new CameraPosition.Builder().target(new LatLng(userLat, userLng)).zoom(17.0f).build();
        CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(camPos);
        options.position(mCliente);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.us_uno));
        map.addMarker(options);
        map.moveCamera(camUpdate);

    }

    @Override
    protected void onResume() {

        mCheck1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                calcValor();
            }
        });
        mCheck2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                calcValor();
            }
        });
        mCheck3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                calcValor();
            }
        });

        Log.v("TAXISTA_SRVCONF1", "ini");
        validateService();

        super.onResume();

        displayConnectivityPanel(!Connectivity.isConnected(this) && !connectivityChecker.getConnectivityCheckResult());
        connectivityChecker.startConnectivityMonitor();
        mNetworkMonitor = new UpdateReceiver(this);
        registerReceiver(mNetworkMonitor, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        Log.v("TAXISTA_SRVCONF1", "end");

    }

    void mostrarMensaje(final String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.login_titulo_dialogo_mensaje);
        builder.setMessage(message);
        builder.setNeutralButton(R.string.login_aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.e("PUSH", "mensaje: " + message);
            }
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    void mostrarAviso(final String mensaje) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(RecoveryMapActivity.this);
                builder.setTitle("Información");
                builder.setMessage(mensaje);
                builder.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (myTimer != null) myTimer.cancel();
                        toFinish();
                    }
                });
                builder.setCancelable(false);
                builder.create();
                builder.show();

            }
        });

    }

    public void validateService() {
        // monitorea si se cancela el servicio
        reintento = 0;
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e("TIMER_EJECUTANDO1", "CARGANDO TAXISTA INI EJECUCIÓN *** " + String.valueOf(reintento));
                reintento++;

                try {
                    Log.e("TIMER_EJECUTANDO1", "checkService()");
                    checkService();
                } catch (JSONException je) {

                }

                Log.e("TIMER EJECUTNDO", "CONFIRMATION cerrando activity status " + String.valueOf(status));
                Log.e("TIMER_EJECUTANDO1", "CARGANDO TAXISTA FIN EJECUTANDO *** ");
            }
        }, 5000, 10000); // 20000

    }

    public boolean checkService() throws JSONException {

        //service_id = null; //conf.getServiceId();

        Log.e("TIMER_EJECUTANDO1", "checkService() ini ");

        if (service_id != null && !service_id.isEmpty()) {
            Log.v("checkService", "driver_id=" + driver_id + " service_id=" + service_id);
        } else {
            Log.v("checkService", "driver_id=" + driver_id + " service_id=" + "NULO");
        }

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
        Call<ServiceStatusResponse> call_profile = service.serviceStatus(driver_id, service_id);
        call_profile.enqueue(new retrofit2.Callback<ServiceStatusResponse>() {
            @Override
            public void onResponse(Call<ServiceStatusResponse> call, retrofit2.Response<ServiceStatusResponse> response) {

                try {

                    int status_service = Integer.parseInt(response.body().getStatus_id());
                    Log.v("checkService", "status_id: " + String.valueOf(status_service));
                    Log.e("TIMER_EJECUTANDO1", "checkService() status " + String.valueOf(status_service));

                    // si hay un servicio asignado lo recupera
                    if (status_service >= 6) {
                        myTimer.cancel();
                        //finish();
                        toFinish();
                    }

                } catch (Exception e) {
                    Log.e("TIMER_EJECUTANDO1", "checkService() response catch 1 ");
                    Log.v("checkService", "Problema json" + e.toString());
                }

                try {
                    int error = response.body().getError();
                    Log.e("TIMER_EJECUTANDO1", "checkService() rj error 1");

                    if (error == 1) {
                        Log.e("TIMER_EJECUTANDO1", "checkService() rj error 2");
                        mostrarAviso(getString(R.string.mapview_servicio_cancelado));
                    }
                } catch (Exception e2) {

                }

            }

            @Override
            public void onFailure(Call<ServiceStatusResponse> call, Throwable t) {
                Log.w("-----Error-----", t.toString());
            }
        });

        return true;

    }

    private void arrivedService(final int action) {
        //Log.v("arrivedService", " a params: " + driver_id + " " + service_id);
        if (service_id == null ) {
            service_id = conf.getServiceId();
        }
        Log.v("arrivedService", " b params: " + driver_id + " " + service_id);

        //MiddleConnect.arrived(this, driver_id, new AsyncHttpResponseHandler() {
        MiddleConnect.arrived2(this, driver_id, service_id, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

                try {
                    pDialog = new ProgressDialog(RecoveryMapActivity.this);
                    pDialog.setMessage(getString(R.string.map_enviando_aviso_cliente));
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();
                } catch (Exception e) {

                }
            }

            @Override
            //public void onSuccess(String response) {
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);
                Log.e("RESPOSNE", response + "");
                try {

                    JSONObject responsejson = new JSONObject(response);

                    if (responsejson != null && responsejson.length() > 0) {

                        if (responsejson.getBoolean("success")) {
                            Log.v("SERVICE_CMS", "    MAP ARRIVED ok service_id= " + service_id);

                            // actualizar estado del servicio
                            Log.v("VALIDATE_SERVICE", "arrived service_id " + service_id);

                            mySQLiteAdapter.updateStatusService(service_id, "4");
                            Toast.makeText(getApplicationContext(), getString(R.string.the_user_arrived), Toast.LENGTH_SHORT).show();

                            if (action == 2) {
                                toFinish();
                            } else {

                                if (mPayType == 0) {
                                    mLinear2.setVisibility(View.VISIBLE);
                                    btnLlegada.setVisibility(View.GONE);
                                    btnCancelar.setVisibility(View.VISIBLE);
                                    btnConfirmCode.setVisibility(View.VISIBLE);
                                    btnFinalizar.setVisibility(View.GONE);

                                } else {
                                    btnLlegada.setVisibility(View.GONE);
                                    btnCancelar.setVisibility(View.VISIBLE);
                                    btnFinalizar.setVisibility(View.GONE);
                                }
                                if (mPayType == 1) {
                                    mLinear2.setVisibility(View.VISIBLE);
                                    btnLlegada.setVisibility(View.GONE);
                                    btnCancelar.setVisibility(View.VISIBLE);
                                    btnConfirmCode.setVisibility(View.GONE);
                                    btnFinalizar.setVisibility(View.VISIBLE);

                                } else {
                                    btnLlegada.setVisibility(View.GONE);
                                    btnCancelar.setVisibility(View.VISIBLE);
                                    btnFinalizar.setVisibility(View.VISIBLE);
                                }

                                if (mPayType == 3) {
                                    mLinear2.setVisibility(View.VISIBLE);
                                    btnLlegada.setVisibility(View.GONE);
                                    btnCancelar.setVisibility(View.VISIBLE);
                                    btnConfirmCode.setVisibility(View.VISIBLE);

                                } else {
                                    btnLlegada.setVisibility(View.GONE);
                                    btnCancelar.setVisibility(View.VISIBLE);
                                    btnFinalizar.setVisibility(View.VISIBLE);
                                }
                            }


                        } else {
                            Log.v("SERVICE_CMS", "MAP ARRIVED bad service_id= " + service_id);

                            err_arrived();
                        }

                    } else {
                        err_arrived();
                    }

                } catch (Exception e) {
                    err_arrived();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                Log.e("RESPOSNE", response + "");
                err_arrived();
            }

            @Override
            public void onFinish() {
                Log.v("SERVICE_CMS", "MAP finish service_id= " + service_id);

                try {
                    pDialog.dismiss();
                } catch (Exception e) {

                }
            }
        });

    }

    private void err_arrived() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        Toast.makeText(getApplicationContext(), getString(R.string.error_arrived), Toast.LENGTH_SHORT).show();
    }

    // finish service +
    private void finishService() {
        Log.v("FINISH2", "finishService() ini ");
        MiddleConnect.finishService(this, driver_id, service_id, MyService.latitud, MyService.longitud, mTotUnits, mTotCharge1, mTotCharge2, mTotCharge3, mTotCharge4, mTotService, mTransactionId, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

                try {
                    pDialog = new ProgressDialog(RecoveryMapActivity.this);
                    pDialog.setMessage(getString(R.string.mapa_finalizando_servicio));
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();
                } catch (Exception e) {

                }
            }

            @Override
            //public void onSuccess(String response) {
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);
                Log.v("FINISH2", "finishService() onSuccess() - " + response);
                Log.v("SERVICE_CMS", "    MAP FINISHED success service_id= " + service_id + " response " + response);
                Log.e("RESPOSNE", response + "");
                try {
                    JSONObject responsejson = new JSONObject(response);
                    savePreferencias("servicieTomado", "false");
                    Log.v("VALIDATE_SERVICE", "finished service_id " + service_id);

                    mySQLiteAdapter.updateStatusService(service_id, "5");

                    if (responsejson != null && responsejson.length() > 0) {
                        String result = responsejson.getString("error");
                        if (Integer.valueOf(result) == 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.servicio_finalizado), Toast.LENGTH_SHORT).show();
                        } else {
                            err_finish_service();
                        }

                    } else {
                        err_finish_service();
                    }
                } catch (Exception e) {
                    Log.v("FINISH2", "finishService() onSuccess() exception - e" + e.toString());

                    err_finish_service();
                }
            }

            @Override
            //public void onFailure(Throwable e, String response) {
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                Log.v("FINISH2", "finishService() onFailure() - " + response);

                Log.v("SERVICE_CMS", "    MAP FINISHED failure service_id= " + service_id + " response " + response);

                Log.e("RESPOSNE", response + "");
                err_finish_service();
            }

            @Override
            public void onFinish() {
                try {
                    pDialog.dismiss();
                } catch (Exception e) {

                }
                Log.v("FINISH2", "finishService() onFinish()");

                if (isFinished)
                    toFinish();
            }
        });

    }

    private void err_finish_service() {
        isFinished = false;
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        Toast.makeText(getApplicationContext(), getString(R.string.error_arrived), Toast.LENGTH_SHORT).show();
    }
    // finish service -

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnLlegada:
                Log.v("BTN1", "solictaxi");
                arrivedService(1);
                break;

            case R.id.btnConfirmCode:
                Log.v("BTN1", "btn_confirm_code");
                if (confirmCodeAuthorization()) {
                    mLinear2.setVisibility(View.VISIBLE);
                    btnCancelar.setVisibility(View.GONE);
                    btnConfirmCode.setVisibility(View.GONE);
                    btnFinalizar.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.btnCancelar:
                Log.v("BTN1", "btn_volver");
                setModalCancelSerivce();
                final CharSequence[] items = {"Trafico pesado", "Varado", "Dirección Errada"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Razón cancelar servicio");
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Haz elegido la opcion: " + items[item], Toast.LENGTH_SHORT);
                        toast.show();
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                break;

            case R.id.btnFinalizar:

                Log.v("BTN1", "finalizar_action");
                Log.v("FINISH2", "call finishService()");

                btnFinalizar.setVisibility(View.VISIBLE);
                String code = mCode.getText().toString();
                String reCode = getIntent().getExtras().getString("rCode");

                if (code.equals(mUserPhone)) {

                    confirmCodeAuthorization();
                    if (!code.equals(mUserPhone)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.codigo_invalido), Toast.LENGTH_LONG).show();
                        return;
                    }

                }
                if (code.equals(reCode)) {
                    confirmCodeAuthorization();
                }
                if (!code.equals(reCode)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.codigo_invalido), Toast.LENGTH_LONG).show();
                    return;
                }

                if ((mPayType == 1) || (mPayType == 2) || (mPayType == 3) || (type_agend == 1) || (type_agend == 2) || (type_agend == 3) || (type_agend == 4)) {
                    mLinear2.setVisibility(View.GONE);
                    mLinear1.setVisibility(View.VISIBLE);
                    btnFinalizar.setVisibility(View.GONE);
                    btn_pay.setVisibility(View.VISIBLE);
                }
                else {
                    finishService();
                    //toFinish();
                }
                break;

            case R.id.btn_pay:
                String sUnits = mUnits.getText().toString();
                if(sUnits.equals("") || sUnits.equals("0") || sUnits.equals("0") || sUnits.equals("1")|| sUnits.equals("2") || sUnits.equals("3") || sUnits.equals("4") || sUnits.equals("5") || sUnits.equals("6") || sUnits.equals("7") || sUnits.equals("8") || sUnits.equals("9") || sUnits.equals("10") || sUnits.equals("11") || sUnits.equals("12") || sUnits.equals("13") || sUnits.equals("14") || sUnits.equals("15") || sUnits.equals("16") || sUnits.equals("17") || sUnits.equals("18") || sUnits.equals("19") || sUnits.equals("20") || sUnits.equals("21") || sUnits.equals("22") || sUnits.equals("23") || sUnits.equals("24") || sUnits.equals("25") || sUnits.equals("26") || sUnits.equals("27")) {
                    Toast.makeText(getApplicationContext(), "Las unidades no pueden estar vacias ni ser menores a 28", Toast.LENGTH_LONG).show();
                } else {
                    prepareReceipt(String.valueOf(mTotalTrip));
                }
        }

    }

    public boolean confirmCodeAuthorization() {
        Log.v("BTN1", "confirmCodeAuthorization +");

        String strCode = mCode.getText().toString();
        if (strCode != null) {
            if (strCode.equals(mUserPhone)) return true;
        }

        return false;
    }

    public void calcValor() {
        Log.v("CAL_VAL", "ini");
        int c1 = 0;
        int c2 = 0;
        int c3 = 0;
        int p = 700;
        int val = 0;
        int valp = 0;

        int roundTo = 100;
        // get unidades
        int units = 0;
        if (!TextUtils.isEmpty(mUnits.getText().toString())) {
            //if (mUnits.getText().toString() != null)
            units = Integer.valueOf(mUnits.getText().toString());
        }
        if (mCheck1.isChecked()) {
            c1 = 4100;
            mTotCharge1 = "4100";
        }
        if (mCheck2.isChecked()) {
            c2 = 2000;
            mTotCharge2 = "2000";
        }
        if (mCheck3.isChecked()) {
            c3 = 5000;
            mTotCharge3 = "5000";
        }

        val = 82 * units;
        val = roundTo * Math.round(val / roundTo);
        //val = p + (82 * units)  + c1 + c2 + c3;
        val = p + val + c1 + c2 + c3;
        valp = val - p;

        mTotValue.setText("Total: " + String.valueOf(valp) + "+" + String.valueOf(p) + "= $ " + String.valueOf(val));
        mTotalTrip = Integer.valueOf(val);
        mTotUnits = String.valueOf(units);
        mTotService = String.valueOf(val);

    }

    private void setModalCancelSerivce() {
        AlertDialog.Builder mdialog = new AlertDialog.Builder(this);

        mdialog.setTitle(getString(R.string.important));
        mdialog.setMessage(getString(R.string.confim_cancel_service));
        mdialog.setCancelable(false);
        mdialog.setPositiveButton(R.string.mapa_respuesta_si,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogo1, int id) {
                        cancelarService();
                    }
                });
        mdialog.setNegativeButton(R.string.mapa_respuesta_no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.cancel();
                    }
                });
        mdialog.show();
    }

    private void cancelarService() {

        MiddleConnect.cancelService(this, driver_id, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

                try {
                    pDialog = new ProgressDialog(RecoveryMapActivity.this);
                    pDialog.setMessage(getString(R.string.mapa_cancelando_servicio));
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();
                } catch (Exception e) {

                }
            }

            @Override
            //public void onSuccess(String response) {
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);
                Log.e("RESPOSNE", response + "");
                try {
                    JSONObject responsejson = new JSONObject(response);

                    savePreferencias("servicieTomado", "false");

                    if (responsejson != null && response.length() > 0) {

                        if (responsejson.getBoolean("success")) {
                            toFinish();

                        } else {
                            err_cancel();
                        }

                    }

                } catch (Exception e) {
                    err_cancel();
                }
            }

            @Override
            //public void onFailure(Throwable e, String response) {
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                String response = new String(responseBody);
                Log.e("RESPOSNE", response + "");
                err_cancel();
            }

            @Override
            public void onFinish() {
                try {
                    savePreferencias("servicieTomado", "false");

                    pDialog.dismiss();
                } catch (Exception e) {

                }
            }

        });

    }

    private void err_cancel() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        Toast.makeText(getApplicationContext(), getString(R.string.error_cancel_service), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!moveTaskToBack(true)) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                this.startActivity(i);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("onDestroy", "MapActivity");

        if (mySQLiteAdapter != null) {
            mySQLiteAdapter.close();
        }

        if (myTimer != null) {
            myTimer.cancel();
            myTimer.purge();
            myTimer = null;
        }
    }

    private void toFinish() {
        Log.v("FINISH2", "finishService() toFinish()");

//        if (mReceiver != null) {
//            unregisterReceiver(mReceiver);
//        }
        EventBus.getDefault().unregister(this);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("service", "ended");
        editor.commit();

        // activar service
        Log.v("finish", "MapActivity toFinish()");
        savePreferencias("servicieTomado", "false");
        finish();
    }

    private void loggedInOtherDevie() {
//        if (mReceiver != null) {
//            unregisterReceiver(mReceiver);
//        }
        EventBus.getDefault().unregister(this);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("service", "ended");
        editor.commit();
        Log.v("finish", "MapActivity loggedInOtherDevie()");
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        connectivityChecker.stopConnectivityMonitor();
        unregisterReceiver(mNetworkMonitor);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onProviderDisabled(String arg0) {


    }

    @Override
    public void onProviderEnabled(String arg0) {


    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {


    }

    public void savePreferencias(String key, String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void displayConnectivityPanel(boolean display) {
        mNoConnectivityPanel.setVisibility(display ? View.VISIBLE : View.GONE);
        if (display)
            mConnectivityLoaderImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.connection_loader));
    }

    @Override
    public void onNetworkConnectivityChange(boolean connected) {
        //displayConnectivityPanel(!connected);
    }

    @Override
    public void onConnectivityQualityChecked(boolean Optimal) {
        //displayConnectivityPanel(!Optimal);
    }

    public boolean typePayment() {
        if (mPayType == 2) return true;
        return false;

    }

    public void prepareReceipt(String value) {
        PaymentezDebitParameters debitParameters = new PaymentezDebitParameters();

        Log.v("prepareReceipt", "s value " + value);

        double v1 = Double.valueOf(value);

        debitParameters.setUid(mUserId);
        debitParameters.setEmail(mUserEmail);
        debitParameters.setCardReference(mUserCardReference);
        debitParameters.setProductAmount(v1);
        debitParameters.setProductDescription("Servicio de transporte");
        debitParameters.setDevReference("Prueba cobro servicio");

        Log.v("prepareReceipt", "ini ");
        Log.v("prepareReceipt", "mUserId " + mUserId);
        Log.v("prepareReceipt", "mUserEmail " + mUserEmail);
        Log.v("prepareReceipt", "mUserReference " + mUserCardReference);
        Log.v("prepareReceipt", "val " + v1);


        if (mPayType == 1) {
            mTransactionId = "EFECTIVO";
            finishService();

        } else if (mPayType == 3) {
            mTransactionId = "VALE";
            finishService();
        } else if (((type_agend == 1) || (type_agend == 2) || (type_agend == 3) || (type_agend == 4))) {

            mTransactionId = "AGENDAMIENTO";
            finishService();
        } else {


            final ProgressDialog pd = new ProgressDialog(RecoveryMapActivity.this);
            pd.setMessage("");
            pd.show();

            paymentezsdk.debitCard(debitParameters, new DebitCardResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, PaymentezResponseDebitCard paymentezResponse) {
                    pd.dismiss();
                    Log.v("prepareReceipt", "success " + paymentezResponse.toString());

                    if (!paymentezResponse.isSuccess()) {
                        android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(RecoveryMapActivity.this);

                        Log.v("prepareReceipt", "paymentezResponse " + paymentezResponse.getTransactionId());
                        builder1.setMessage("Error: " + paymentezResponse.getErrorMessage());
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        finishService();
                                    }
                                });
                        android.support.v7.app.AlertDialog alert11 = builder1.create();
                        alert11.show();

                    } else {
                        Log.v("prepareReceipt", "v2 ");
                        if (paymentezResponse.getStatus().equals("failure") && paymentezResponse.shouldVerify()) {
                            android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(RecoveryMapActivity.this);

                            //String message = "You must verify the transaction_id: " + paymentezResponse.getTransactionId();
                            String message = "Se presento un problema al realizar la transacción. Su código de transacción es: " + paymentezResponse.getTransactionId();

                            mTransactionId = paymentezResponse.getTransactionId();

                            builder1.setMessage(message);

                            builder1.setCancelable(false);
                            builder1.setPositiveButton(getString(R.string.btn_ok_text),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            finishService();
                                        }
                                    });
                            android.support.v7.app.AlertDialog alert11 = builder1.create();
                            alert11.show();
                        } else {
                            android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(RecoveryMapActivity.this);

                            mTransactionId = paymentezResponse.getTransactionId();
                            builder1.setMessage("Transacción exitosa. Su código de transacción es: " + paymentezResponse.getTransactionId());
                            builder1.setCancelable(false);
                            builder1.setPositiveButton(getString(R.string.btn_ok_text),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            finishService();
                                        }
                                    });
                            android.support.v7.app.AlertDialog alert11 = builder1.create();
                            alert11.show();


                            System.out.println("TRANSACTION INFO");
                            System.out.println(paymentezResponse.getStatus());
                            System.out.println(paymentezResponse.getPaymentDate());
                            System.out.println(paymentezResponse.getAmount());
                            System.out.println(paymentezResponse.getTransactionId());
                            System.out.println(paymentezResponse.getStatusDetail());

                            System.out.println("TRANSACTION card_data");
                            System.out.println(paymentezResponse.getCardData().getAccountType());
                            System.out.println(paymentezResponse.getCardData().getType());
                            System.out.println(paymentezResponse.getCardData().getNumber());
                            System.out.println(paymentezResponse.getCardData().getQuotas());

                            System.out.println("TRANSACTION carrier_data");
                            System.out.println(paymentezResponse.getCarrierData().getAuthorizationCode());
                            System.out.println(paymentezResponse.getCarrierData().getAcquirerId());
                            System.out.println(paymentezResponse.getCarrierData().getTerminalCode());
                            System.out.println(paymentezResponse.getCarrierData().getUniqueCode());
                        }
                    }
                }


                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    pd.dismiss();
                    Log.v("prepareReceipt", "fail " + responseString);
                    System.out.println("Failure: " + responseString);
                }


            });
        }

    }


    private double s(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        twoDForm.setDecimalFormatSymbols(dfs);

        String doubleString = displayNumberAmount(twoDForm.format(d));
        return Double.valueOf(doubleString);
    }

    public static String displayNumberAmount(String amount) {

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.CANADA_FRENCH);

        Number number = 0;

        try {
            number = numberFormat.parse(amount);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return String.format(Locale.US, "%1$,.2f", number);
    }

    /*@Override
    public void onStop(){
        super.onStop();
        unregisterReceiver(mReceiver);
    }*/

}
