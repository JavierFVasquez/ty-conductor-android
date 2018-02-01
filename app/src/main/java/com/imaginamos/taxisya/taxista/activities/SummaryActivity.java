package com.imaginamos.taxisya.taxista.activities;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imaginamos.taxisya.taxista.R;
import com.imaginamos.taxisya.taxista.io.ApiService;
import com.imaginamos.taxisya.taxista.io.Connect;
import com.imaginamos.taxisya.taxista.model.ServiceStatusResponse;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SummaryActivity extends Activity {

    private TextView TV_Origen;
    private TextView TV_Destino;
    private TextView TV_Total;
    private LinearLayout LL_Recargo_1;
    private TextView TV_Recargo_1;
    private LinearLayout LL_Recargo_2;
    private TextView TV_Recargo_2;
    private LinearLayout LL_Recargo_3;
    private TextView TV_Recargo_3;
    private Button BT_Finish;


    private String service_id;
    private String driver_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        service_id = getIntent().getExtras().getString("service_id");
        driver_id = getIntent().getExtras().getString("driver_id");

        TV_Origen = findViewById(R.id.TV_Origen);
        TV_Destino = findViewById(R.id.TV_Destino);
        TV_Total = findViewById(R.id.TV_Total);
        LL_Recargo_1 = findViewById(R.id.LL_Recargo_1);
        TV_Recargo_1 = findViewById(R.id.TV_Recargo_1);
        LL_Recargo_2 = findViewById(R.id.LL_Recargo_2);
        TV_Recargo_2 = findViewById(R.id.TV_Recargo_2);
        LL_Recargo_3 = findViewById(R.id.LL_Recargo_3);
        TV_Recargo_3 = findViewById(R.id.TV_Recargo_3);
        BT_Finish = findViewById(R.id.BT_Finish);

        BT_Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


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

                int status_service = Integer.parseInt(response.body().getStatus_id());

                TV_Origen.setText(response.body().getAddress());
                TV_Destino.setText(response.body().getDestination());
                TV_Total.setText("$ " + response.body().getValor_app());
                if(Integer.parseInt(response.body().getCharge1()) != 0) {
                    LL_Recargo_1.setVisibility(View.VISIBLE);
                }
                TV_Recargo_1.setText(response.body().getCharge1());
                if(Integer.parseInt(response.body().getCharge2()) != 0) {
                    LL_Recargo_2.setVisibility(View.VISIBLE);
                }
                TV_Recargo_2.setText(response.body().getCharge2());
                if(Integer.parseInt(response.body().getCharge4()) != 0) {
                    LL_Recargo_3.setVisibility(View.VISIBLE);
                }
                TV_Recargo_3.setText(response.body().getCharge4());
            }

            @Override
            public void onFailure(Call<ServiceStatusResponse> call, Throwable t) {
                Log.w("-----Error-----", t.toString());
            }
        });

    }
}
