package com.agrostar.wallet.exceptions;

public class OperationNotAllowedException extends RuntimeException {

    private String operation;
    private String entity;
    private String message;
    private Object id;

    public OperationNotAllowedException(String operation, String entity, String message, Object id) {
        super(String.format("%s operation can not be done on %s with id %s : %s", operation, entity, id, message));
        this.operation = operation;
        this.entity = entity;
        this.message = message;
    }

    public String getOperation() {
        return operation;
    }

    public String getEntity() {
        return entity;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Object getId() {
        return id;
    }
}
