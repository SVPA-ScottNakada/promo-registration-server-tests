
package com.promo.test.framework.registration_server;

import com.promo.test.suite.registration_server.RegistrationServerTestData;

public class RegisterEmailHelper extends RegistrationServerApiCallHelper {

    private static final String API_COMMAND = "register/email";

    public RegisterEmailHelper() {
        this(RegistrationServerTestData.REGISTRATION_SERVER_BASE_URI);
    }

    public RegisterEmailHelper(String newUri) {
        super(newUri + API_COMMAND);
    }

    // Send request

    public void send() {
        sendPostRequest();
    }

    // Add Parameters

    public void addApplicationId(String value) {
        addStringAsBodyParameter("appId", value);
    }

    public void addApplicationVersion(String value) {
        addStringAsBodyParameter("appVersion", value);
    }

    public void addDeviceUserId(String value) {
        addStringAsBodyParameter("duid", value);
    }

    public void addEmail(String value) {
        addStringAsBodyParameter("email", value);
    }

    public void addOptIn(Boolean value) {
        addBooleanAsRequestBody("optIn", value);
    }

    public void addRegisterMeta(String value) {
        addStringAsBodyParameter("registerMeta", value);
    }

    // @formatter:off
    // --- JSON RESPONSE PATHS --- ////

    public static final String DEVICE_STATE = "deviceState";
    public static final String DEVICE_TOKEN = "deviceToken";

    // @formatter:on

}
