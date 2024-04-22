package com.ojas.multitenant.exception;

public class TenantResolutionException extends RuntimeException {
    public TenantResolutionException(String message) {
        super(message);
    }
}
