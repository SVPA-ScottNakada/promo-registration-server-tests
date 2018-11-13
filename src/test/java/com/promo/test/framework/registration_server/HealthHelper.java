
package com.promo.test.framework.registration_server;

import com.promo.test.suite.registration_server.RegistrationServerTestData;

public class HealthHelper extends RegistrationServerRequestHelper {

    public static final String API_COMMAND = "health";

    public HealthHelper() {
        this(RegistrationServerTestData.REGISTRATION_SERVER_BASE_URI);
    }

    public HealthHelper(String newUri) {
        super(newUri + API_COMMAND);
    }

    // Send request

    public void send() {
        sendGetRequest();
    }

}
