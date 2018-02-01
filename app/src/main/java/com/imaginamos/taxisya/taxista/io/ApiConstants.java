package com.imaginamos.taxisya.taxista.io;

import com.imaginamos.taxisya.taxista.BuildConfig;

/**
 * Created by leo on 11/15/15.
 */
public class ApiConstants {

    public static final String BRAND_LIST = "nuevoCms/webservices/lista_marcas";
    public static final String LINE_LIST = "nuevoCms/webservices/lista_linea";
    public static final String COMPANIES_LIST = "nuevoCms/webservices/empresas_taxis";
    public static final String SERVICE_STATUS = " service/status";
    public static final String GOOGLE_KEY = "key=AIzaSyDwNDt0qiUwpZ0F7U9TAKf5mY8kSPGmxd8";
    public static final String DIRECTIONS_GOOGLE = "directions/json?" + GOOGLE_KEY;
    public static final String FINISH_SERVICE = "nuevoCms/webservices/terminarServicio";
    public static final String SERVICE_CHAT = "nuevoCms/webservices/service_chat";
    public static String app_code = "TAXISYA";
    //public static String app_secret_key = "oDX4b0Yk4usp0ptEf1XFK00KQSxAgV";
    public static String app_secret_key = "jN2Dk59eDX1h7W7NtiV3YYbzCFSHRY";
    public static boolean api_env = false;

    public static final String BASE_URL = "http://" + BuildConfig.HOST + "/public/";

    // USER
    public static final String DRIVER_LOGIN = "/user/login";
    public static final String DRIVER_REGISTER = "nuevoCms/webservices/registar_conductor";
    public static final String DRIVER_UPLOAD = "/uploads";
    public static final String DRIVER_COUNTRY = "/country";
    public static final String DRIVER_DEPARTMENT = "/department";
    public static final String DRIVER_CITY = "/city";

    public static final String APP_VERSION = "/app/versions";

}

