package com.rufino.server.domain;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class HttpResponse {
    private int httpStatusCode;
    private HttpStatus httpStatus;
    private String message, reason;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime timestamp;
    private Map<String, String> errors;

    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {

        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.message = message;
        this.reason = reason;
        this.timestamp = ZonedDateTime.now(ZoneId.of("Z"));
    }

    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message,
            Map<String, String> errors) {

        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.message = message;
        this.reason = reason;
        this.timestamp = ZonedDateTime.now(ZoneId.of("Z"));
        this.errors = errors;
    }

    public HttpResponse() {
        this.timestamp = ZonedDateTime.now(ZoneId.of("Z"));
    }

}
