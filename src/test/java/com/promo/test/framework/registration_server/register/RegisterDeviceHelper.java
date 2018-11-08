
package com.promo.test.framework.registration_server.register;

import com.promo.test.framework.registration_server.RegistrationServerRequestHelper;
import com.promo.test.suite.registration_server.RegistrationServerTestData;

public class RegisterDeviceHelper extends RegistrationServerRequestHelper {

    public static final String API_COMMAND = "register/device";

    public RegisterDeviceHelper() {
        this(RegistrationServerTestData.REGISTRATION_SERVER_BASE_URI);
    }

    public RegisterDeviceHelper(String newUri) {
        super(newUri + API_COMMAND);
    }

    public void send() {
        sendPostRequest();
    }

    public void addApplicationId(String value) {
        addStringAsRequestBody("appId", value);
    }

    public void addApplicationVersion(String value) {
        addStringAsRequestBody("appVersion", value);
    }

    public void addDeviceUserId(String value) {
        addStringAsRequestBody("duid", value);
    }

    public void addLanguage(String value) {
        addStringAsRequestBody("lang", value);
    }

    public void addModel(String value) {
        addStringAsRequestBody("model", value);
    }

}
