package com.rufino.server.exception;

import static com.rufino.server.constant.ExceptionConst.*;
import static org.springframework.http.HttpStatus.*;

import java.util.HashMap;
import java.util.Map;

import com.rufino.server.domain.HttpResponse;
import com.rufino.server.exception.domain.AccountDisabledException;
import com.rufino.server.exception.domain.AccountLockedException;
import com.rufino.server.exception.domain.InvalidCredentialsException;
import com.rufino.server.exception.domain.InvalidTokenException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class ApiHandlerException implements ErrorController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    public static final String ERROR_PATH = "/error";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return createHttpResponse(BAD_REQUEST, BAD_REQUEST_MSG, errors);
    }

    @ExceptionHandler(value = { ApiRequestException.class })
    public ResponseEntity<HttpResponse> handleApiRequestException(ApiRequestException e) {
        return createHttpResponse(e.getHttpStatus(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<HttpResponse> InvalidTokenErrorException(InvalidTokenException exception) {
        return createHttpResponse(FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(value = { DataIntegrityViolationException.class })
    public ResponseEntity<HttpResponse> handleDBException(DataIntegrityViolationException e) {
        return handleSqlError(e);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<HttpResponse> noHandlerFoundException(NoHandlerFoundException e) {
        LOGGER.error(e.getMessage());
        return createHttpResponse(BAD_REQUEST, "There is no mapping for this URL");
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<HttpResponse> BadCredentialsException(InvalidCredentialsException e) {
        return createHttpResponse(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(AccountDisabledException.class)
    public ResponseEntity<HttpResponse> AccountDisabledErrorException(AccountDisabledException exception) {
        return createHttpResponse(UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<HttpResponse> AccountLockedErrorException(AccountLockedException exception) {
        return createHttpResponse(UNAUTHORIZED, exception.getMessage());
    }

    ///////////////////////////// PRIVATE //////////////////////////////////////
    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase()), httpStatus);
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message,
            Map<String, String> errors) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase(), errors), httpStatus);
    }

    private ResponseEntity<HttpResponse> handleSqlError(DataIntegrityViolationException e) {
        ResponseEntity<HttpResponse> response;
        String errorMsg = e.getMessage();
        Map<String, String> errorMap = new HashMap<>();

        errorMsg = errorMsg.replace("\n", "").replace("\r", "");

        String pattern = ".*\\w*; SQL.*;.*\\[(uk_user_\\w+)\\].*";
        String error = (errorMsg.replaceAll(pattern, "$1"));

        switch (error) {

        case "uk_user_email":
            errorMap.put("email", EMAIL_NOT_AVAILABLE);
            response = createHttpResponse(BAD_REQUEST, EMAIL_NOT_AVAILABLE, errorMap);
            break;
        case "uk_user_username":
            errorMap.put("username", EMAIL_NOT_AVAILABLE);
            response = createHttpResponse(BAD_REQUEST, USERNAME_NOT_AVAILABLE);
            break;
        default:
            response = createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
            break;
        }

        return response;
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

}