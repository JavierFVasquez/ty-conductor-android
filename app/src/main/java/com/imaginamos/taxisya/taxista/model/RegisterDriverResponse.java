package com.imaginamos.taxisya.taxista.model;

import java.util.List;

/**
 * Created by javiervasquez on 24/01/18.
 */

public class RegisterDriverResponse {

    public Boolean success;
    public List<Datum> data = null;
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public List<Datum> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class Datum {

        public String id_conductor;
        public String nombre;
        public String apellido;
        public String email;
        public String cedula;
        public String direccion;
        public String celular;
        public String telefono;
        public String numeroTc;
        public String id_vehiculo;
        public String placa;
        public String marca;
        public String linea;
        public String movil;
        public String modelo;
        public String id_ciudad;
        public String id_empresa;


        public String getId_conductor() {
            return id_conductor;
        }

        public String getNombre() {
            return nombre;
        }

        public String getApellido() {
            return apellido;
        }

        public String getEmail() {
            return email;
        }

        public String getCedula() {
            return cedula;
        }

        public String getDireccion() {
            return direccion;
        }

        public String getCelular() {
            return celular;
        }

        public String getTelefono() {
            return telefono;
        }

        public String getNumeroTc() {
            return numeroTc;
        }

        public String getId_vehiculo() {
            return id_vehiculo;
        }

        public String getPlaca() {
            return placa;
        }

        public String getMarca() {
            return marca;
        }

        public String getLinea() {
            return linea;
        }

        public String getMovil() {
            return movil;
        }

        public String getModelo() {
            return modelo;
        }

        public String getId_ciudad() {
            return id_ciudad;
        }

        public String getId_empresa() {
            return id_empresa;
        }
    }



}
