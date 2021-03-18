package com.rufino.server.exception.domain;
import static com.rufino.server.constant.ExceptionConst.ACCOUNT_DISABLED;

public class AccountDisabledException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public AccountDisabledException() {
        super(ACCOUNT_DISABLED);
    }

}
