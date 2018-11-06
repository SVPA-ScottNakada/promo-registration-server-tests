
package com.promo.test.suite;

import com.promo.test.framework.utils.ConfigUtils;

public class CommonTestData {

    // Api Request

    public static final String DEBUG_LOG_API_CALL_RESPONSE =
            ConfigUtils.getInstance().getValueFromEnvOrConfigFile("request.debug.log.api.call.response");

    public static final String REQUEST_PROXY = ConfigUtils.getInstance().getValueFromEnvOrConfigFile("request.proxy");

    public static final String REQUEST_PROXY_PORT =
            ConfigUtils.getInstance().getValueFromEnvOrConfigFile("request.proxy.port");

    // TestRail

    public static final String TESTRAIL_ACTIVE =
            ConfigUtils.getInstance().getValueFromEnvOrConfigFile("testrail.active");

    public static final String TESTRAIL_API_URL =
            ConfigUtils.getInstance().getValueFromEnvOrConfigFile("testrail.api.url");

    public static final String TESTRAIL_USER = ConfigUtils.getInstance().getValueFromEnvOrConfigFile("testrail.user");

    public static final String TESTRAIL_USER_ID =
            ConfigUtils.getInstance().getValueFromEnvOrConfigFile("testrail.user.id");

    public static final String TESTRAIL_PASSWORD =
            ConfigUtils.getInstance().getValueFromEnvOrConfigFile("testrail.password");

    public static final String TESTRAIL_PROJECT_ID =
            ConfigUtils.getInstance().getValueFromEnvOrConfigFile("testrail.project.id");

    public static final String TESTRAIL_SUITE_REGISTRATION_SERVER_ID =
            ConfigUtils.getInstance().getValueFromEnvOrConfigFile("testrail.suite.regportal.id");

}
