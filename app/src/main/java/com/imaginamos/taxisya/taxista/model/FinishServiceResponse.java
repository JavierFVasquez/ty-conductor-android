package com.imaginamos.taxisya.taxista.model;

/**
 * Created by javiervasquez on 26/01/18.
 */

public class FinishServiceResponse {
    private String error;
    private boolean success;
    private String message;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean getSuccess() {
        return success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
