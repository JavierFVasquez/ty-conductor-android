package com.imaginamos.taxisya.taxista.model;

/**
 * Created by javiervasquez on 27/11/17.
 */

public class MessageEvent {
    private String action;
    private String message;
    private String serivice_id;
    private String user_name;
    private String service;

    public String getSerivice_id() {
        return serivice_id;
    }

    public void setSerivice_id(String serivice_id) {
        this.serivice_id = serivice_id;
    }

    public MessageEvent(String action, String message) {
        this.action = action;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageEvent(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getService() {
        return service;
    }
}
