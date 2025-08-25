package com.gruastremart.api.utils.constants;

public class Constants {
    // Version
    public static final String API_VERSION_PATH = "/v1";

    // Actuator
    public static final String ACTUATOR_PATH = "/actuator/**";

    // Swagger
    public static final String SWAGGER_PATH = "/swagger*/**";
    public static final String SWAGGER_API_DOCS_PATH = "/v3/api-docs/**";

    // H2 Console
    public static final String H2_CONSOLE_PATH = "/console/**";

    //Error
    public static final String ERROR_PATH = "/error";

    // Users
    public static final String USERS_PATH = "/users";

    // Auth
    public static final String LOGIN_PATH = "/login";
    public static final String LOGIN_URL = API_VERSION_PATH + USERS_PATH + LOGIN_PATH;

    // Cache
    public static final String OPERATOR_LOCATIONS_CACHE = "operatorLocations";
    public static final String CRANE_PRICING_CACHE = "cranePricing";

    // Email
    public static final String SEND_EMAIL_URL = API_VERSION_PATH + "/emails/contact";
    public static final String SEND_CONTACTFORM_URL = API_VERSION_PATH + "/contact-forms";
    public static final String REGISTER_FORM_URL = API_VERSION_PATH + "/users/register";

    // Auth
    public static final String FORGOT_PASSWORD_URL = API_VERSION_PATH + "/auth/forgot-password";
    public static final String RESET_PASSWORD_URL = API_VERSION_PATH + "/auth/reset-password";
}
