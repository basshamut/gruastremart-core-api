package com.gruastremart.api.utils.constants;

public class Constants {
    // Version
    public static final String API_VERSION_PATH = "/v1";

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
}
