
package com.promo.test.framework.registration_server.register_device;

import com.promo.test.framework.registration_server.RegistrationServerRequestHelper;
import com.promo.test.suite.registration_server.RegistrationServerTestData;

public class RegisterDeviceHelper extends RegistrationServerRequestHelper {

    public static final String API_COMMAND = "register/device";

    public RegisterDeviceHelper() {
        this(RegistrationServerTestData.REGISTRATION_SERVER_BASE_URI);
    }

    public RegisterDeviceHelper(String newUri) {
        super(newUri + API_COMMAND );
    }

    public void send() {
        sendPostRequest();
    }

}
