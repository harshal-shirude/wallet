package com.agrostar.wallet.response;

import com.agrostar.wallet.enums.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericResponse<T> {

    private T data;
    private ResponseStatus status;
    private String message;

    public GenericResponse() {
        this.status = ResponseStatus.SUCCESS;
    }

    public GenericResponse(T data) {
        this.status = ResponseStatus.SUCCESS;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
