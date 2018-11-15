
package com.promo.test.suite.registration_server;

import com.promo.test.framework.registration_server.RegisterDeviceHelper;
import com.promo.test.framework.registration_server.RegisterEmailHelper;
import com.promo.test.framework.utils.TestData;
import com.promo.test.suite.BaseApiTest;

import org.testng.annotations.Test;

public class RegisterEmailTests extends BaseApiTest {

    public static final String TEST_DUID = "abcdefghi_mytest";

    public static final String TEST_APP = "postman";

    public static final String TEST_APP_KEY = "hogehoge";

    public static final String TEST_EMAIL = "this@email.com";

    public static final String TEST_REGMETA =
            "https://api.erabu.sony.tv/60bad103452303d5ec8e512ca6cec5de6121ea47/c14b51e9-fe30-4527-805a-126bcadd8273/items/6691";

    @TestData(id = "", description = "Required parameters")
    @Test(groups = "BrokenTest")
    public void registerEmailTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail(TEST_EMAIL);
        regEmail.addOptIn(true);
        regEmail.addRegisterMeta(TEST_REGMETA);
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        regEmail.validateResponseCodeOk();

    }

    @TestData(id = "", description = "Missing appId parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingAppIdTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail(TEST_EMAIL);
        regEmail.addOptIn(true);
        regEmail.addRegisterMeta(TEST_REGMETA);
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        regEmail.validateResponseCode(422);
        regEmail.validateValue(RegisterEmailHelper.ERROR_CODE_PATH, "4005");
        regEmail.validateDebug("4005", "Missing member appId");

    }

    @TestData(id = "", description = "Missing appVersion parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingAppVersionTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail(TEST_EMAIL);
        regEmail.addOptIn(true);
        regEmail.addRegisterMeta(TEST_REGMETA);
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        regEmail.validateResponseCode(422);
        regEmail.validateValue(RegisterEmailHelper.ERROR_CODE_PATH, "4005");
        regEmail.validateDebug("4005", "Missing member appVersion");

    }

    @TestData(id = "", description = "Missing duid parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingDuidTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addEmail(TEST_EMAIL);
        regEmail.addOptIn(true);
        regEmail.addRegisterMeta(TEST_REGMETA);
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        regEmail.validateResponseCode(422);
        regEmail.validateValue(RegisterEmailHelper.ERROR_CODE_PATH, "4005");
        regEmail.validateDebug("4005", "Missing member duid");

    }

    @TestData(id = "", description = "Missing email parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingEmailTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addOptIn(true);
        regEmail.addRegisterMeta(TEST_REGMETA);
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        regEmail.validateResponseCode(422);
        regEmail.validateValue(RegisterEmailHelper.ERROR_CODE_PATH, "4005");
        regEmail.validateDebug("4005", "Missing member email");

    }

    @TestData(id = "", description = "Missing optIn parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingOptInTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail(TEST_EMAIL);
        regEmail.addRegisterMeta(TEST_REGMETA);
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        regEmail.validateResponseCode(422);
        regEmail.validateValue(RegisterEmailHelper.ERROR_CODE_PATH, "4005");
        regEmail.validateDebug("4005", "Missing member optIn");

    }

    @TestData(id = "", description = "Invalid optIn type")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidOptInTypeTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail(TEST_EMAIL);
        regEmail.addStringAsRequestBody("optIn", "true");
        regEmail.addRegisterMeta(TEST_REGMETA);
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        regEmail.validateResponseCode(422);
        regEmail.validateValue(RegisterEmailHelper.ERROR_CODE_PATH, "4002");
        regEmail.validateDebug("4002", "Invalid type for optIn. Expected boolean");

    }

    @TestData(id = "", description = "Missing registerMeta parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingRegisterMetaTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail(TEST_EMAIL);
        regEmail.addOptIn(true);
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        regEmail.validateResponseCode(422);
        regEmail.validateValue(RegisterEmailHelper.ERROR_CODE_PATH, "4005");
        regEmail.validateDebug("4005", "Missing member registerMeta");

    }

    @TestData(id = "", description = "No app key, invalid signature")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidSignatureTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail(TEST_EMAIL);
        regEmail.addOptIn(true);
        regEmail.addRegisterMeta(TEST_REGMETA);
        regEmail.send();

        regEmail.validateResponseCode(401);
        regEmail.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        regEmail.validateDebug("4001", "Invalid signature");

    }

}
