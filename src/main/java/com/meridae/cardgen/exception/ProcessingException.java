package com.meridae.cardgen.exception;

public class ProcessingException extends Exception {
    public ProcessingException(String message, Exception e) {
        super(message, e);
    }
}
