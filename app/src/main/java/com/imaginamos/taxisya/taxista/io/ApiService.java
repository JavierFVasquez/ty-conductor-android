package com.imaginamos.taxisya.taxista.io;

import com.google.gson.JsonElement;
import com.imaginamos.taxisya.taxista.activities.AppResponse;
import com.imaginamos.taxisya.taxista.activities.RegisterResponse;
import com.imaginamos.taxisya.taxista.activities.UploadResponse;
import com.imaginamos.taxisya.taxista.model.BandListResponse;
import com.imaginamos.taxisya.taxista.model.CompaniesListResponse;
import com.imaginamos.taxisya.taxista.model.DirectionsResponse;
import com.imaginamos.taxisya.taxista.model.FinishServiceResponse;
import com.imaginamos.taxisya.taxista.model.LineListResponse;
import com.imaginamos.taxisya.taxista.model.RegisterDriverResponse;
import com.imaginamos.taxisya.taxista.model.ServiceStatusResponse;
import com.imaginamos.taxisya.taxista.model.SimpleResponse;
import com.squareup.okhttp.RequestBody;

import java.util.Map;

import retrofit.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit.mime.TypedFile;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by leo on 11/15/15.
 */
public interface ApiService {
    // login
//    params.put("type", HomeActivity.TYPE_USER);
    @FormUrlEncoded
    @POST(ApiConstants.DRIVER_LOGIN)
    void login(@Field("type") String type,
               @Field("login") String login,
               @Field("pwd") String pwd,
               @Field("uuid") String uuid,
               Callback<RegisterResponse> callback);

    @GET(ApiConstants.BRAND_LIST)
    Call<BandListResponse> brandList();

    @FormUrlEncoded
    @POST(ApiConstants.LINE_LIST)
    Call<LineListResponse> lineList(@Field("id_marca") String id_marca);

    @GET(ApiConstants.COMPANIES_LIST)
    Call<CompaniesListResponse> companiesList();

    @GET(ApiConstants.SERVICE_CHAT)
    Call<SimpleResponse> sendNotification(@Query("id_usuario") String id_usuario);

    @GET(ApiConstants.DIRECTIONS_GOOGLE)
    Call<DirectionsResponse> directions(@Query("origin") String Origin, @Query("destination") String Destination);

    @FormUrlEncoded
    @POST(ApiConstants.DRIVER_REGISTER)
    Call<RegisterDriverResponse> registerDriver(@Field("nombre") String nombre,
                                                @Field("apellido") String apellido,
                                                @Field("email") String email,
                                                @Field("password") String password,
                                                @Field("celular") String celular,
                                                @Field("telefono") String telefono,
                                                @Field("cedula") String cedula,
                                                @Field("direccion") String direccion,
                                                @Field("tc") String tc,
                                                @Field("placa") String placa,
                                                @Field("movil") String movil,
                                                @Field("marca") String marca,
                                                @Field("linea") String linea,
                                                @Field("modelo") String modelo,
                                                @Field("empresa") int empresa,
                                                @Field("foto_conductor") String foto_conductor,
                                                @Field("foto_documento") String foto_documento,
                                                @Field("foto_licencia") String foto_licencia,
                                                @Field("foto_propiedad") String foto_propiedad,
                                                @Field("foto_operacion") String foto_operacion);
@FormUrlEncoded
    @POST(ApiConstants.FINISH_SERVICE)
    Call<FinishServiceResponse> finishService(@Field("id_servicio") int id_servicio,
                                              @Field("id_conductor") int id_conductor,
                                              @Field("unidades") int unidades,
                                              @Field("valor") double valor,
                                              @Field("pasajero") int pasajero);


    @FormUrlEncoded
    @POST(ApiConstants.DRIVER_UPLOAD)
    void update(@Field("type") String type,
                @Field("name") String name,
                @Field("lastname") String lastname,
                @Field("email") String email,
                @Field("login") String login,
                @Field("pwd") String pwd,
                @Field("token") String token,
                @Field("cellphone") String cellphone,
                @Field("uuid") String uuid, Callback<UploadResponse> callback);
    @FormUrlEncoded
    @POST(ApiConstants.SERVICE_STATUS)
    Call<ServiceStatusResponse> serviceStatus(
            @Field("driver_id") String driver_id,
            @Field("service_id") String service_id);

    @FormUrlEncoded
    @POST(ApiConstants.SERVICE_STATUS)
    Call<ServiceStatusResponse> serviceStatus(
            @Field("driver_id") String driver_id);

    @Multipart
    @POST("/upload/login.php")
    void setDriverImage(
            @QueryMap Map<String, String> params,
            // the @Part has the parameter "pathImage".
            // You should pass this in your php code.
            @Part("pathImage") TypedFile file,
            Callback<JsonElement> response);

    @Multipart
    @POST("/uploads")
    void uploadImage(@Part("file") TypedFile file, Callback<RegisterResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.APP_VERSION)
    void appVersion(@Field("descript") String descript, Callback<AppResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.DRIVER_COUNTRY)
    void getCountries(@Field("empty") String empty, Callback<RegisterResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.DRIVER_DEPARTMENT)
    void getDepartments(@Field("empty") String empty, Callback<RegisterResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.DRIVER_CITY)
    void getCities(@Field("empty") String empty, Callback<RegisterResponse> callback);

}
