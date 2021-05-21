package com.rufino.server.exception;

import static com.rufino.server.constant.ExceptionConst.BAD_REQUEST_MSG;
import static com.rufino.server.constant.ExceptionConst.EMAIL_NOT_AVAILABLE;
import static com.rufino.server.constant.ExceptionConst.INTERNAL_SERVER_ERROR_MSG;
import static com.rufino.server.constant.ExceptionConst.METHOD_IS_NOT_ALLOWED;
import static com.rufino.server.constant.ExceptionConst.NOT_ENOUGH_PERMISSION;
import static com.rufino.server.constant.ExceptionConst.USERNAME_NOT_AVAILABLE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.rufino.server.domain.HttpResponse;
import com.rufino.server.exception.domain.AccountDisabledException;
import com.rufino.server.exception.domain.AccountLockedException;
import com.rufino.server.exception.domain.InvalidCredentialsException;
import com.rufino.server.exception.domain.InvalidTokenException;
import com.rufino.server.exception.domain.UserNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
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
        exception.printStackTrace();
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

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException() {
        return createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException exception) {
        return createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(UNAUTHORIZED, "Your session has expired. Please log in again.");
    }

    ///////////////////////////// PRIVATE //////////////////////////////////////
    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(), message), httpStatus);
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message,
            Map<String, String> errors) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(), message, errors), httpStatus);
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
            errorMap.put("username", USERNAME_NOT_AVAILABLE);
            response = createHttpResponse(BAD_REQUEST, USERNAME_NOT_AVAILABLE, errorMap);
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