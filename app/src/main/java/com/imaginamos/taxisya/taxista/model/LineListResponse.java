package com.imaginamos.taxisya.taxista.model;

import java.util.List;

/**
 * Created by javiervasquez on 24/01/18.
 */

public class LineListResponse {

    public Boolean success;
    public List<Datum> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public List<Datum> getData() {
        return data;
    }

    public class Datum {
        public String id;
        public String name_line;
        public String id_brand;

        public String estado;

        public String getId() {
            return id;
        }

        public String getName_line() {
            return name_line;
        }

        public String getId_brand() {
            return id_brand;
        }

        public String getEstado() {
            return estado;
        }
    }


}
