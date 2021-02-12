package com.rufino.server.exception;

import java.time.ZonedDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.springframework.http.HttpStatus;

@JsonInclude(Include.NON_NULL)
public class ApiException {
    private String message = "Not OK";
    private Map<String, String> errors;
    private Throwable throwable;
    private HttpStatus httpStatus;
    private final ZonedDateTime timestamp;

    public ApiException(Map<String, String> errors, Throwable throwable, HttpStatus httpStatus,
            ZonedDateTime timestamp) {
        this.setErrors(errors);
        this.setThrowable(throwable);
        this.setHttpStatus(httpStatus);
        this.timestamp = timestamp;
    }

    public ApiException(Map<String, String> errors, HttpStatus httpStatus, ZonedDateTime timestamp) {
        this.errors = errors;
        this.httpStatus = httpStatus;
        this.timestamp = timestamp;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}