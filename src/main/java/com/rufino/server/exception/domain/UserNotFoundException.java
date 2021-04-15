package com.rufino.server.exception.domain;
import static com.rufino.server.constant.ExceptionConst.USER_NOT_FOUND;

public class UserNotFoundException extends RuntimeException{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(){
        super(USER_NOT_FOUND);
    }


        
}
