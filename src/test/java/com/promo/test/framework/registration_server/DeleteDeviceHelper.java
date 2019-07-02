
package com.promo.test.framework.registration_server;

import com.promo.test.suite.registration_server.RegistrationServerTestData;

public class DeleteDeviceHelper extends RegistrationServerApiCallHelper {

    private static final String API_COMMAND = "register/delete-device";

    public DeleteDeviceHelper() {
        this(RegistrationServerTestData.REGISTRATION_SERVER_BASE_URI);
    }

    public DeleteDeviceHelper(String newUri) {
        super(newUri + API_COMMAND);
        setHasHeaderSignature(false);
    }

    // Send request

    public void send() {
        sendGetRequest();
    }

    // Add Parameters

    public void addDeviceUserId(String value) {
        addStringAsRequestParameter("duid", value);
    }

}
