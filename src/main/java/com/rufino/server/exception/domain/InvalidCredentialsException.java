package com.rufino.server.exception.domain;
import static com.rufino.server.constant.ExceptionConst.INCORRECT_CREDENTIALS;;

public class InvalidCredentialsException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvalidCredentialsException() {
        super(INCORRECT_CREDENTIALS);
    }

}
