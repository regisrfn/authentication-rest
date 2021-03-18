package com.rufino.server.exception.domain;
import static com.rufino.server.constant.ExceptionConst.ACCOUNT_LOCKED;

public class AccountLockedException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public AccountLockedException() {
        super(ACCOUNT_LOCKED);
    }

}
