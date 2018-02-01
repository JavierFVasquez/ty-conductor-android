package com.imaginamos.taxisya.taxista.model;

import java.util.List;

/**
 * Created by javiervasquez on 24/01/18.
 */

public class BandListResponse {

    public Boolean success;
    public String message;
    public List<Datum> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public class Datum {
        public String id;
        public String name_brands;
        public String estado;

        public String getId() {
            return id;
        }

        public String getName_brands() {
            return name_brands;
        }

        public String getEstado() {
            return estado;
        }
    }


}
