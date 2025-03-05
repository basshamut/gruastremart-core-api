package com.gruastremart.api.utils;

import org.springframework.beans.factory.annotation.Value;

public class Constants {
    //Version
    public static final String API_VERSION_PATH = "/v1";

    //Auth
    public static final String LOGIN_PATH = "/login";
    public static final String LOGIN_URL = API_VERSION_PATH + LOGIN_PATH;

    //Cache
    public static final String LOGIN_ATTEMPTS_CACHE = "loginAttempts";

    // JWT
    public static final String HEADER_AUTHORIZACION_KEY = "Authorization";
    public static final String TOKEN_BEARER_PREFIX = "Bearer ";

}
