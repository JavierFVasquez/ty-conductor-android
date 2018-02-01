package com.imaginamos.taxisya.taxista.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.imaginamos.taxisya.taxista.R;
import com.imaginamos.taxisya.taxista.Holders.ChatHolder;
import com.imaginamos.taxisya.taxista.io.ApiService;
import com.imaginamos.taxisya.taxista.io.Connect;
import com.imaginamos.taxisya.taxista.model.Chat;
import com.imaginamos.taxisya.taxista.model.DirectionsResponse;
import com.imaginamos.taxisya.taxista.model.SimpleResponse;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ChatActivity extends AppCompatActivity {

    private RecyclerView RV_Chat_Content;
    private EditText ET_Chat_Text;
    private Button BT_Send;
    private String service_id;
    private RecyclerView.Adapter adapter;
    private boolean first_time = false;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        service_id = getIntent().getExtras().getString("service_id","0");
        user_id = getIntent().getExtras().getString("user_id","0");

        RV_Chat_Content = (RecyclerView) findViewById(R.id.RV_Chat_Content);
        ET_Chat_Text = (EditText) findViewById(R.id.ET_Chat_Text);
        BT_Send = (Button) findViewById(R.id.BT_Send);


        FirebaseApp secondary = FirebaseApp.getInstance("secondary");

        FirebaseDatabase database = FirebaseDatabase.getInstance(secondary);

        DatabaseReference chat_ref = database.getReference("chat/taxi_user/"+service_id);
        Query queryRef = chat_ref.orderByChild("service_id").equalTo(service_id);


        RV_Chat_Content.setHasFixedSize(false);
        RV_Chat_Content.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        adapter = new FirebaseRecyclerAdapter<Chat, ChatHolder>(Chat.class, R.layout.item_chat, ChatHolder.class, queryRef) {
            @Override
            public void populateViewHolder(ChatHolder ChatHolder, final Chat chat, int position) {


                switch (chat.getTypeId()){
                    case 1:
                        ChatHolder.getTV_Message_Text_Taxi().setText(chat.getMessage());
                        ChatHolder.getRL_Message_Container_User().setVisibility(View.GONE);
                        break;
                    default:
                        ChatHolder.getTV_Message_Text_User().setText(chat.getMessage());
                        ChatHolder.getRL_Message_Container_Taxi().setVisibility(View.GONE);
                        break;
                }


            }
        };
        RV_Chat_Content.setAdapter(adapter);
        LinearLayoutManager lm = (LinearLayoutManager) RV_Chat_Content.getLayoutManager();
        lm.setStackFromEnd(true);

        chat_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(first_time) {
                    RV_Chat_Content.scrollToPosition(RV_Chat_Content.getAdapter().getItemCount() - 1);
                }
                first_time = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: " , String.valueOf(databaseError.getCode()));
            }
        });

        BT_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ET_Chat_Text.getText().toString().trim().length()>0) {
                    first_time= false;
                    FirebaseApp secondary = FirebaseApp.getInstance("secondary");
                    FirebaseDatabase database = FirebaseDatabase.getInstance(secondary);
                    DatabaseReference chat_ref = database.getReference();
                    String key = chat_ref.push().getKey();


                    Chat new_message = new Chat();

                    new_message.setMessage(ET_Chat_Text.getText().toString().trim());
                    new_message.setTypeId(0);
                    new_message.setService_id(service_id);


                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/chat/taxi_user/" + service_id + "/" + key, new_message);
                    chat_ref.updateChildren(childUpdates);

                    ET_Chat_Text.setText("");

                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                    httpClient.addInterceptor(logging);

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Connect.BASE_URL_IP)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(httpClient.build())
                            .build();
                    ApiService service = retrofit.create(ApiService.class);
                    Call<SimpleResponse> call_directions=service.sendNotification(user_id);
                    call_directions.enqueue(new Callback<SimpleResponse>() {
                        @Override
                        public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {

                        }

                        @Override
                        public void onFailure(Call<SimpleResponse> call, Throwable t) {
                            Log.w("-----Error-----",t.toString());
                        }
                    });
                }
            }
        });


    }

}
