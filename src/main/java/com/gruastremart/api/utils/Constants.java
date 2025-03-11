package com.gruastremart.api.utils;

public class Constants {
    // Version
    public static final String API_VERSION_PATH = "/v1";

    // Users
    public static final String USERS_PATH = "/users";

    // Auth
    public static final String LOGIN_PATH = "/login";
    public static final String LOGIN_URL = API_VERSION_PATH + USERS_PATH + LOGIN_PATH;

    // Cache
    public static final String LOGIN_ATTEMPTS_CACHE = "loginAttempts";

    // JWT
    public static final String HEADER_AUTHORIZACION_KEY = "Authorization";
    public static final String TOKEN_BEARER_PREFIX = "Bearer ";

    // Email
    public static final String SEND_EMAIL_URL = API_VERSION_PATH + "/emails/contact";
}
