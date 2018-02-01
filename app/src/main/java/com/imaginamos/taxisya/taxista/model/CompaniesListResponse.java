package com.imaginamos.taxisya.taxista.model;

import java.util.List;

/**
 * Created by javiervasquez on 24/01/18.
 */

public class CompaniesListResponse {

    public Boolean success;
    public List<Datum> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public List<Datum> getData() {
        return data;
    }

    public class Datum {

        public String name_company;
        public String description;
        public String id_cities;
        public String id_departments;
        public String id_contry;
        public String secretary_bog;
        public String app_taxisya;
        public String estado;
        private int id;

        public String getName_company() {
            return name_company;
        }

        public String getDescription() {
            return description;
        }

        public String getId_cities() {
            return id_cities;
        }

        public String getId_departments() {
            return id_departments;
        }

        public String getId_contry() {
            return id_contry;
        }

        public String getSecretary_bog() {
            return secretary_bog;
        }

        public String getApp_taxisya() {
            return app_taxisya;
        }

        public String getEstado() {
            return estado;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

}
