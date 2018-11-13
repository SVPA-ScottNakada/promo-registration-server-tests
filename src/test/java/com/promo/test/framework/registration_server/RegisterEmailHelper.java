
package com.promo.test.framework.registration_server;

import com.promo.test.suite.registration_server.RegistrationServerTestData;

public class RegisterEmailHelper extends RegistrationServerRequestHelper {

    public static final String API_COMMAND = "register/email";

    public RegisterEmailHelper() {
        this(RegistrationServerTestData.REGISTRATION_SERVER_BASE_URI);
    }

    public RegisterEmailHelper(String newUri) {
        super(newUri + API_COMMAND);
        setContentType("application/x-www-form-urlencoded");
    }

    // Send request

    public void send() {
        sendPostRequest();
    }

    // Add Parameters

    public void addApplicationId(String value) {
        addStringAsRequestBody("appId", value);
    }

    public void addApplicationVersion(String value) {
        addStringAsRequestBody("appVersion", value);
    }

    public void addDeviceUserId(String value) {
        addStringAsRequestBody("duid", value);
    }

    public void addEmail(String value) {
        addStringAsRequestBody("email", value);
    }

    // boolean??
    public void addOptIn(String value) {
        addStringAsRequestBody("optIn", value);
    }

    public void addRegisterMeta(String value) {
        addStringAsRequestBody("registerMeta", value);
    }

}
