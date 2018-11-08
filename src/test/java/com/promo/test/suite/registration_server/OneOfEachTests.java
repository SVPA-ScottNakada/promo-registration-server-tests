
package com.promo.test.suite.registration_server;

import com.promo.test.framework.registration_server.DeleteDeviceHelper;
import com.promo.test.framework.registration_server.HealthHelper;
import com.promo.test.framework.registration_server.RegisterDeviceHelper;
import com.promo.test.suite.BaseApiTest;

import org.testng.annotations.Test;

public class OneOfEachTests extends BaseApiTest {

    public static final String TEST_DUID = "abcdefghi_mytest";

    public static final String TEST_APP = "postman";

    public static final String TEST_APP_KEY = "hogehoge";

    @Test
    public void registerDeviceTest() {

        RegisterDeviceHelper register = new RegisterDeviceHelper();
        register.addApplicationId(TEST_APP);
        register.addApplicationVersion("0.1");
        register.addDeviceUserId(TEST_DUID);
        register.addLanguage("en");
        register.addModel("some-tv");
        register.setAppKey(TEST_APP_KEY);
        register.send();

        register.validateResponseCodeOk();

    }

    @Test
    public void registerDeviceTestMissingAppId() {

        RegisterDeviceHelper register = new RegisterDeviceHelper();
        register.addApplicationVersion("0.1");
        register.addDeviceUserId(TEST_DUID);
        register.addLanguage("en");
        register.addModel("some-tv");
        register.setAppKey(TEST_APP_KEY);
        register.send();

        register.validateResponseCode(422);
        register.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4005");

    }

    @Test
    public void deleteDeviceTest() {

        DeleteDeviceHelper register = new DeleteDeviceHelper();
        register.addDeviceUserId(TEST_DUID);
        register.send();

        register.validateResponseCodeOk();

    }

    @Test
    public void healthTest() {

        HealthHelper register = new HealthHelper();
        register.send();

        register.validateResponseCodeOk();

    }

}
