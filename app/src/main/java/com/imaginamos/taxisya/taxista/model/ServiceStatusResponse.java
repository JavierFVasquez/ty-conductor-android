package com.imaginamos.taxisya.taxista.model;

/**
 * Created by javiervasquez on 25/01/18.
 */

public class ServiceStatusResponse {


    public String id;
    public String user_id;
    public String driver_id;
    public String car_id;
    public String from_lat;
    public String from_lng;
    public String status_id;
    public String to_lat;
    public String to_lng;
    public Object start_datetime;
    public Object finish_datetime;
    public Object qualification;
    public Object reason_qualification;
    public String index_id;
    public Object comp1;
    public Object comp2;
    public Object no;
    public String barrio;
    public String obs;
    public String kind_id;
    public String schedule_id;
    public String schedule_type;
    public String updated_at;
    public String created_at;
    public String destination;
    public String service_date_time;
    public String user_name;
    public String address;
    public String cms_users_id;
    public String pay_type;
    public String pay_reference;
    public String user_card_reference;
    public String user_email;
    public String units;
    public String charge1;
    public String charge2;
    public String charge3;
    public String charge4;
    public String value;
    public String code;
    public String company_id;
    public Object state_payment;
    public String cedula;
    public String date_state_payment;
    public String commit;
    public String facturado;
    public String km_recorrido;
    public String tiempo_recorido;
    public String valor_app;
    public Object n_pasajeros;
    public Object id_carrera_ant;
    public Object valor_total;
    public Object schedule;
    public User user;
    public State state;
    public Driver driver;
    private int error;


    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public String getCar_id() {
        return car_id;
    }

    public String getFrom_lat() {
        return from_lat;
    }

    public String getFrom_lng() {
        return from_lng;
    }

    public String getStatus_id() {
        return status_id;
    }

    public String getTo_lat() {
        return to_lat;
    }

    public String getTo_lng() {
        return to_lng;
    }

    public Object getStart_datetime() {
        return start_datetime;
    }

    public Object getFinish_datetime() {
        return finish_datetime;
    }

    public Object getQualification() {
        return qualification;
    }

    public Object getReason_qualification() {
        return reason_qualification;
    }

    public String getIndex_id() {
        return index_id;
    }

    public Object getComp1() {
        return comp1;
    }

    public Object getComp2() {
        return comp2;
    }

    public Object getNo() {
        return no;
    }

    public String getBarrio() {
        return barrio;
    }

    public String getObs() {
        return obs;
    }

    public String getKind_id() {
        return kind_id;
    }

    public String getSchedule_id() {
        return schedule_id;
    }

    public String getSchedule_type() {
        return schedule_type;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getDestination() {
        return destination;
    }

    public String getService_date_time() {
        return service_date_time;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getAddress() {
        return address;
    }

    public String getCms_users_id() {
        return cms_users_id;
    }

    public String getPay_type() {
        return pay_type;
    }

    public String getPay_reference() {
        return pay_reference;
    }

    public String getUser_card_reference() {
        return user_card_reference;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUnits() {
        return units;
    }

    public String getCharge1() {
        return charge1;
    }

    public String getCharge2() {
        return charge2;
    }

    public String getCharge3() {
        return charge3;
    }

    public String getCharge4() {
        return charge4;
    }

    public String getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public String getCompany_id() {
        return company_id;
    }

    public Object getState_payment() {
        return state_payment;
    }

    public String getCedula() {
        return cedula;
    }

    public String getDate_state_payment() {
        return date_state_payment;
    }

    public String getCommit() {
        return commit;
    }

    public String getFacturado() {
        return facturado;
    }

    public String getKm_recorrido() {
        return km_recorrido;
    }

    public String getTiempo_recorido() {
        return tiempo_recorido;
    }

    public String getValor_app() {
        return valor_app;
    }

    public Object getN_pasajeros() {
        return n_pasajeros;
    }

    public Object getId_carrera_ant() {
        return id_carrera_ant;
    }

    public Object getValor_total() {
        return valor_total;
    }

    public Object getSchedule() {
        return schedule;
    }

    public User getUser() {
        return user;
    }

    public State getState() {
        return state;
    }

    public Driver getDriver() {
        return driver;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public class Car {

        public String id;
        public String placa;
        public String car_brand;
        public String model;
        public String movil;
        public String empresa;
        public String pay_date;
        public String year;
        public String city_id;
        public String payment;
        public String carroceria;
        public String estado;
        public String factor;
        public String id_empresa;
        public String id_brand;
        public String id_line;

        public String getId() {
            return id;
        }

        public String getPlaca() {
            return placa;
        }

        public String getCar_brand() {
            return car_brand;
        }

        public String getModel() {
            return model;
        }

        public String getMovil() {
            return movil;
        }

        public String getEmpresa() {
            return empresa;
        }

        public String getPay_date() {
            return pay_date;
        }

        public String getYear() {
            return year;
        }

        public String getCity_id() {
            return city_id;
        }

        public String getPayment() {
            return payment;
        }

        public String getCarroceria() {
            return carroceria;
        }

        public String getEstado() {
            return estado;
        }

        public String getFactor() {
            return factor;
        }

        public String getId_empresa() {
            return id_empresa;
        }

        public String getId_brand() {
            return id_brand;
        }

        public String getId_line() {
            return id_line;
        }
    }


    public class Driver {

        public String id;
        public String login;
        public String car_id;
        public String movil;
        public String cellphone;
        public String score_driver;
        public String num_score;
        public Object picture;
        public String status;
        public Object account_status;
        public String name;
        public String lastname;
        public String email;
        public String created_at;
        public String crt_lat;
        public String crt_lng;
        public String uuid;
        public String updated_at;
        public String available;
        public String license;
        public Object session_id;
        public String cedula;
        public String dir;
        public String telephone;
        public Object join_date;
        public String token;
        public String city_id;
        public String estado;
        public String numero_tc;
        public Car car;

        public String getId() {
            return id;
        }

        public String getLogin() {
            return login;
        }

        public String getCar_id() {
            return car_id;
        }

        public String getMovil() {
            return movil;
        }

        public String getCellphone() {
            return cellphone;
        }

        public String getScore_driver() {
            return score_driver;
        }

        public String getNum_score() {
            return num_score;
        }

        public Object getPicture() {
            return picture;
        }

        public String getStatus() {
            return status;
        }

        public Object getAccount_status() {
            return account_status;
        }

        public String getName() {
            return name;
        }

        public String getLastname() {
            return lastname;
        }

        public String getEmail() {
            return email;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getCrt_lat() {
            return crt_lat;
        }

        public String getCrt_lng() {
            return crt_lng;
        }

        public String getUuid() {
            return uuid;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public String getAvailable() {
            return available;
        }

        public String getLicense() {
            return license;
        }

        public Object getSession_id() {
            return session_id;
        }

        public String getCedula() {
            return cedula;
        }

        public String getDir() {
            return dir;
        }

        public String getTelephone() {
            return telephone;
        }

        public Object getJoin_date() {
            return join_date;
        }

        public String getToken() {
            return token;
        }

        public String getCity_id() {
            return city_id;
        }

        public String getEstado() {
            return estado;
        }

        public String getNumero_tc() {
            return numero_tc;
        }

        public Car getCar() {
            return car;
        }
    }

    public class State {

        public String id;
        public String descrip;

    }

    public class User {

        public String id;
        public String name;
        public String email;
        public String login;
        public Object crt_lat;
        public Object crt_lng;
        public String cellphone;
        public Object telephone;
        public String lastname;
        public String created_at;
        public String uuid;
        public String updated_at;
        public String type;
        public String token;
        public String diageo;
        public Object pwd_token;
        public String estado;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getLogin() {
            return login;
        }

        public Object getCrt_lat() {
            return crt_lat;
        }

        public Object getCrt_lng() {
            return crt_lng;
        }

        public String getCellphone() {
            return cellphone;
        }

        public Object getTelephone() {
            return telephone;
        }

        public String getLastname() {
            return lastname;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUuid() {
            return uuid;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public String getType() {
            return type;
        }

        public String getToken() {
            return token;
        }

        public String getDiageo() {
            return diageo;
        }

        public Object getPwd_token() {
            return pwd_token;
        }

        public String getEstado() {
            return estado;
        }
    }
}
