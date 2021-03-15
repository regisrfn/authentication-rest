package com.rufino.server.exception;

import static com.rufino.server.constant.ExceptionConst.BAD_REQUEST_MSG;
import static com.rufino.server.constant.ExceptionConst.EMAIL_NOT_AVAILABLE;
import static com.rufino.server.constant.ExceptionConst.INTERNAL_SERVER_ERROR_MSG;
import static com.rufino.server.constant.ExceptionConst.USERNAME_NOT_AVAILABLE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.util.HashMap;
import java.util.Map;

import com.rufino.server.domain.HttpResponse;
import com.rufino.server.exception.domain.InvalidTokenException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiHandlerException {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }

    @ExceptionHandler(value = { ApiRequestException.class })
    public ResponseEntity<HttpResponse> handleApiRequestException(ApiRequestException e) {
        return createHttpResponse(e.getHttpStatus(), e.getMessage());
    }

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

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<HttpResponse> InvalidTokenErrorException(InvalidTokenException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(value = { DataIntegrityViolationException.class })
    public ResponseEntity<HttpResponse> handleDBException(DataIntegrityViolationException e) {
        return handleSqlError(e);
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

}