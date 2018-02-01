package com.imaginamos.taxisya.taxista.model;

/**
 * Created by javiervasquez on 1/02/18.
 */

public class SimpleResponse {


    public class Data {

        public String message;

        public String getMessage() {
            return message;
        }
    }

        public Boolean success;
        public Data data;

    public Boolean getSuccess() {
        return success;
    }

    public Data getData() {
        return data;
    }
}
