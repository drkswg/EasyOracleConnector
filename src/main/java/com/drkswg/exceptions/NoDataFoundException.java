package com.drkswg.exceptions;

public class NoDataFoundException extends RuntimeException {
    public NoDataFoundException(String errorMessage) {
        super(errorMessage);
    }
}
