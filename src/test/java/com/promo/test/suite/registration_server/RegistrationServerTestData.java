
package com.promo.test.suite.registration_server;

import com.promo.test.framework.utils.ConfigUtils;

public class RegistrationServerTestData {

    // Registration Server

    public static final String REGISTRATION_SERVER_ENDPOINT =
            ConfigUtils.getInstance().getValueFromEnvOrConfigFile("registration.server.baseurl");

    public static final String REGISTRATION_SERVER_BASE_URI = REGISTRATION_SERVER_ENDPOINT + "promo/";

    public static final String REGISTRATION_SERVER_BASE_URI_IS_PRODUCTION =
            ConfigUtils.getInstance().getValueFromEnvOrConfigFile("registration.server.baseurl.is.production");

    // Test Data

    // DUID
    public static final String DUID = ConfigUtils.getInstance().getValueFromEnvOrConfigFile("test.duid.01");

    public static final String DUID_02 = ConfigUtils.getInstance().getValueFromEnvOrConfigFile("test.duid.02");

    public static final String DUID_FLOW = ConfigUtils.getInstance().getValueFromEnvOrConfigFile("test.duid.flow");

    // APP
    public static final String APP_NAME = ConfigUtils.getInstance().getValueFromEnvOrConfigFile("test.app.name");

    public static final String APP_KEY = ConfigUtils.getInstance().getValueFromEnvOrConfigFile("test.app.key");

    // EMAIL
    public static final String EMAIL = ConfigUtils.getInstance().getValueFromEnvOrConfigFile("test.email");

    public static final String SECOND_EMAIL =
            ConfigUtils.getInstance().getValueFromEnvOrConfigFile("test.email.second");

    // REGMETA
    public static final String REGMETA_URL = ConfigUtils.getInstance().getValueFromEnvOrConfigFile("test.regmeta.url");

    public static final String REGMETA_ID_001 =
            ConfigUtils.getInstance().getValueFromEnvOrConfigFile("test.regmeta.id.01");

    public static final String REGMETA = REGMETA_URL + REGMETA_ID_001;

    // PROMOMETA

    public static final String PROMOMETA_URL =
            ConfigUtils.getInstance().getValueFromEnvOrConfigFile("test.promometa.url");

    public static final String PROMOMETA_ID_001 =
            ConfigUtils.getInstance().getValueFromEnvOrConfigFile("test.promometa.id.01");

    public static final String PROMOMETA_ID_002 =
            ConfigUtils.getInstance().getValueFromEnvOrConfigFile("test.promometa.id.02");

    public static final String PROMOMETA_ID_003 =
            ConfigUtils.getInstance().getValueFromEnvOrConfigFile("test.promometa.id.03");

    public static final String PROMOMETA_001 = PROMOMETA_URL + PROMOMETA_ID_001;

    public static final String PROMOMETA_002 = PROMOMETA_URL + PROMOMETA_ID_002;

    public static final String PROMOMETA_003 = PROMOMETA_URL + PROMOMETA_ID_003;

}
