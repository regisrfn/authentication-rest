package com.rufino.server.security;

import java.util.concurrent.TimeUnit;

public class SecurityConst {

    public static final long EXPIRATION_TIME = TimeUnit.DAYS.toMillis(1);
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String RUFINO_LLC = "Rufino, LLC";
    public static final String USER_ADMINISTRATION = "User Management Portal";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "Need to login to access this resource";
    public static final String ACCESS_DENIED_MESSAGE = "Not allowed access this resource";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = { "/api/v1/user/login", "/api/v1/user/register", "/api/v1/user/reset-password/**"};

}
