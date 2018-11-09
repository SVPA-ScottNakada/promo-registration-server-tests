
package com.promo.test.suite.registration_server;

import com.promo.test.framework.utils.ConfigUtils;

public class RegistrationServerTestData {

    // Registration Server

    public static final String REGISTRATION_SERVER_ENDPOINT =
            ConfigUtils.getInstance().getValueFromEnvOrConfigFile("registration.server.baseurl");

    public static final String REGISTRATION_SERVER_BASE_URI = REGISTRATION_SERVER_ENDPOINT + "promo/";

    public static final String REGISTRATION_SERVER_BASE_URI_IS_PRODUCTION =
            ConfigUtils.getInstance().getValueFromEnvOrConfigFile("registration.server.baseurl.is.production");

}
