
package com.promo.test.framework.registration_server;

import com.promo.test.suite.registration_server.RegistrationServerTestData;

public class RedeemedHelper extends RegistrationServerRequestHelper {

    public static final String API_COMMAND = "redeemed";

    public RedeemedHelper() {
        this(RegistrationServerTestData.REGISTRATION_SERVER_BASE_URI);
    }

    public RedeemedHelper(String newUri) {
        super(newUri + API_COMMAND);
    }

    // Send request

    public void send() {
        sendGetRequest();
    }

    // Add Parameters

    public void addApplicationId(String value) {
        addStringAsRequestParameter("appId", value);
    }

    public void addApplicationVersion(String value) {
        addStringAsRequestParameter("appVersion", value);
    }

    public void addDeviceUserId(String value) {
        addStringAsRequestParameter("duid", value);
    }

    public void addDeviceToken(String value) {
        addStringAsRequestParameter("deviceToken", value);
    }

}
