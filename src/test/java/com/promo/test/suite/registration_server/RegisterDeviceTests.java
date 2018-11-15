
package com.promo.test.suite.registration_server;

import com.promo.test.framework.registration_server.RegisterDeviceHelper;
import com.promo.test.framework.utils.TestData;
import com.promo.test.suite.BaseApiTest;

import org.testng.annotations.Test;

public class RegisterDeviceTests extends BaseApiTest {

    public static final String TEST_DUID = "abcdefghi_mytest";

    public static final String TEST_APP = "postman";

    public static final String TEST_APP_KEY = "hogehoge";

    @TestData(id = "", description = "Required parameters")
    @Test(groups = "SmokeTest")
    public void requiredParametersTest() {

        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.addApplicationId(TEST_APP);
        regDev.addApplicationVersion("0.1");
        regDev.addDeviceUserId(TEST_DUID);
        regDev.addLanguage("en");
        regDev.addModel("some-tv");
        regDev.setAppKey(TEST_APP_KEY);
        regDev.send();

        regDev.validateResponseCodeOk();

    }

    @TestData(id = "", description = "Missing appId parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingAppIdTest() {

        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.addApplicationVersion("0.1");
        regDev.addDeviceUserId(TEST_DUID);
        regDev.addLanguage("en");
        regDev.addModel("some-tv");
        regDev.setAppKey(TEST_APP_KEY);
        regDev.send();

        regDev.validateResponseCode(422);
        regDev.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4005");
        regDev.validateDebug("4005", "Missing member appId");

    }

    @TestData(id = "", description = "Missing appVersion parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingAppVersionTest() {

        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.addApplicationId(TEST_APP);
        regDev.addDeviceUserId(TEST_DUID);
        regDev.addLanguage("en");
        regDev.addModel("some-tv");
        regDev.setAppKey(TEST_APP_KEY);
        regDev.send();

        regDev.validateResponseCode(422);
        regDev.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4005");
        regDev.validateDebug("4005", "Missing member appVersion");

    }

    @TestData(id = "", description = "Missing duid parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingDuidTest() {

        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.addApplicationId(TEST_APP);
        regDev.addApplicationVersion("0.1");
        regDev.addLanguage("en");
        regDev.addModel("some-tv");
        regDev.setAppKey(TEST_APP_KEY);
        regDev.send();

        regDev.validateResponseCode(422);
        regDev.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4005");
        regDev.validateDebug("4005", "Missing member duid");

    }

    @TestData(id = "", description = "Missing lang parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingLangTest() {

        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.addApplicationId(TEST_APP);
        regDev.addApplicationVersion("0.1");
        regDev.addDeviceUserId(TEST_DUID);
        regDev.addModel("some-tv");
        regDev.setAppKey(TEST_APP_KEY);
        regDev.send();

        regDev.validateResponseCode(422);
        regDev.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4005");
        regDev.validateDebug("4005", "Missing member lang");

    }

    @TestData(id = "", description = "Missing model parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingModelTest() {

        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.addApplicationId(TEST_APP);
        regDev.addApplicationVersion("0.1");
        regDev.addDeviceUserId(TEST_DUID);
        regDev.addLanguage("en");
        regDev.setAppKey(TEST_APP_KEY);
        regDev.send();

        regDev.validateResponseCode(422);
        regDev.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4005");
        regDev.validateDebug("4005", "Missing member model");

    }

    @TestData(id = "", description = "No app key, invalid signature")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidSignatureTest() {

        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.addApplicationId(TEST_APP);
        regDev.addApplicationVersion("0.1");
        regDev.addDeviceUserId(TEST_DUID);
        regDev.addLanguage("en");
        regDev.addModel("some-tv");
        regDev.send();

        regDev.validateResponseCode(401);
        regDev.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        regDev.validateDebug("4001", "Invalid signature");

    }

}
