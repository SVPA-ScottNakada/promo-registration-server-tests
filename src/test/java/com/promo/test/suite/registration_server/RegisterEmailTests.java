
package com.promo.test.suite.registration_server;

import com.promo.test.framework.registration_server.RegisterDeviceHelper;
import com.promo.test.framework.registration_server.RegisterEmailHelper;
import com.promo.test.framework.utils.TestData;
import com.promo.test.suite.BaseApiTest;

import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RegisterEmailTests extends BaseApiTest {

    public static final String TEST_DUID = RegistrationServerTestData.DUID;

    public static final String TEST_APP = RegistrationServerTestData.APP_NAME;

    public static final String TEST_APP_KEY = RegistrationServerTestData.APP_KEY;

    public static final String TEST_EMAIL = RegistrationServerTestData.EMAIL;

    public static final String TEST_SECOND_EMAIL = RegistrationServerTestData.SECOND_EMAIL;

    public static final String TEST_REGMETA = RegistrationServerTestData.REGMETA;

    public static String deviceToken = "";

    @BeforeClass(groups = "SmokeTest")
    public void makeSureDeviceIsRegistered() {
        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.logToReport("Make sure Device is registered");
        regDev.addApplicationId(TEST_APP);
        regDev.addApplicationVersion("0.1");
        regDev.addDeviceUserId(TEST_DUID);
        regDev.addLanguage("en");
        regDev.addModel("some-tv");
        regDev.setAppKey(TEST_APP_KEY);
        regDev.send();

        regDev.validateResponseCodeOk();
    }

    @TestData(id = "1526357", description = "Required parameters")
    @Test(groups = "SmokeTest")
    public void registerEmailTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail(TEST_EMAIL);
        regEmail.addRegisterMeta(TEST_REGMETA);
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        regEmail.validateResponseCodeOk();
        regEmail.validateNotNullOrEmpty(RegisterEmailHelper.DEVICE_TOKEN);

        regEmail.logToReport("Saving value for the first deviceToken");
        deviceToken = regEmail.getPathValue(RegisterEmailHelper.DEVICE_TOKEN);

    }

    @TestData(id = "1526449", description = "Register second email to same test duid, optional optIn = true")
    @Test(groups = "SmokeTest", dependsOnMethods = {"registerEmailTest"}, alwaysRun = true)
    public void registerSecondEmailTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail(TEST_SECOND_EMAIL);
        regEmail.addOptIn(true);
        regEmail.addRegisterMeta(TEST_REGMETA);
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        regEmail.validateResponseCodeOk();
        regEmail.validateNotNullOrEmpty(RegisterEmailHelper.DEVICE_TOKEN);

        regEmail.logToReport("Saving value for the second deviceToken");
        String secondDeviceToken = regEmail.getPathValue(RegisterEmailHelper.DEVICE_TOKEN);
        Boolean differentTokens = !secondDeviceToken.equals(deviceToken);
        regEmail.logResult(differentTokens, "Second deviceToken is different from the first -" + deviceToken + "-");
    }

    @TestData(id = "1526358", description = "Missing appId parameter")
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

        regEmail.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        regEmail.validateValue(RegisterEmailHelper.ERROR_CODE_PATH, "4005");
        regEmail.validateDebug("4005", "Missing member appId");

    }

    @TestData(id = "1526359", description = "Missing appVersion parameter")
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

        regEmail.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        regEmail.validateValue(RegisterEmailHelper.ERROR_CODE_PATH, "4005");
        regEmail.validateDebug("4005", "Missing member appVersion");

    }

    @TestData(id = "1526360", description = "Missing duid parameter")
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

        regEmail.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        regEmail.validateValue(RegisterEmailHelper.ERROR_CODE_PATH, "4005");
        regEmail.validateDebug("4005", "Missing member duid");

    }

    @TestData(id = "1526361", description = "Missing email parameter")
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

        regEmail.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        regEmail.validateValue(RegisterEmailHelper.ERROR_CODE_PATH, "4005");
        regEmail.validateDebug("4005", "Missing member email");

    }

    @TestData(id = "1526363", description = "Invalid optIn type")
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

        regEmail.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        regEmail.validateValue(RegisterEmailHelper.ERROR_CODE_PATH, "4002");
        regEmail.validateDebug("4002", "Invalid type for optIn. Expected boolean");

    }

    @TestData(id = "1526364", description = "Missing registerMeta parameter")
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

        regEmail.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        regEmail.validateValue(RegisterEmailHelper.ERROR_CODE_PATH, "4005");
        regEmail.validateDebug("4005", "Missing member registerMeta");

    }

    @TestData(id = "1526365", description = "No app key, invalid signature")
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

        regEmail.validateResponseCode(HttpStatus.SC_UNAUTHORIZED);
        regEmail.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        regEmail.validateDebug("4001", "Invalid signature");

    }

    @TestData(id = "1526366", description = "Invalid appId")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidAppIdTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId("ThisShouldNotWork");
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail(TEST_EMAIL);
        regEmail.addOptIn(true);
        regEmail.addRegisterMeta(TEST_REGMETA);
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        regEmail.validateResponseCode(HttpStatus.SC_UNAUTHORIZED);
        regEmail.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        regEmail.validateDebug("4001", "Missing App key");

    }

    @TestData(id = "1526367", description = "Invalid email pattern")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidEmailPatternTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail("ThisShouldNotWork");
        regEmail.addOptIn(true);
        regEmail.addRegisterMeta(TEST_REGMETA);
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        regEmail.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        regEmail.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4002");
        regEmail.validateDebug("4002", "Invalid pattern for email.");

    }

    @TestData(id = "1526368", description = "Invalid registerMeta pattern")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidRegisterMetaPatternTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail(TEST_EMAIL);
        regEmail.addOptIn(true);
        regEmail.addRegisterMeta("ThisShouldNotWork");
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        regEmail.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        regEmail.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4002");
        regEmail.validateDebug("4002", "Invalid pattern for registerMeta.");

    }

    @TestData(id = "1526369", description = "Invalid registerMeta ")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidRegisterMetaTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail(TEST_EMAIL);
        regEmail.addOptIn(true);
        regEmail.addRegisterMeta("https://api.erabu.sony.tv/ThisShouldNotWork/ThisWontWork/1234");
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        regEmail.validateResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        regEmail.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4301");

    }

    @TestData(id = "1526370", description = "Invalid app key")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidAppKeyTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail(TEST_EMAIL);
        regEmail.addOptIn(true);
        regEmail.addRegisterMeta(TEST_REGMETA);
        regEmail.setAppKey("ThisShouldNotWork");
        regEmail.send();

        regEmail.validateResponseCode(HttpStatus.SC_UNAUTHORIZED);
        regEmail.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        regEmail.validateDebug("4001", "Invalid signature");

    }

}
