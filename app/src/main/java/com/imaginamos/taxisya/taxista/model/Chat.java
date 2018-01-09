package com.imaginamos.taxisya.taxista.model;

/**
 * Created by javiervasquez on 23/12/17.
 */

public class Chat {
    private String message;
    private int typeId;
    private String service_id;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getService_id() {
        return service_id;
    }
}
