
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

    public static final String TEST_DUID_SECOND = RegistrationServerTestData.DUID_02;

    public static final String TEST_APP = RegistrationServerTestData.APP_NAME;

    public static final String TEST_APP_KEY = RegistrationServerTestData.APP_KEY;

    public static final String TEST_EMAIL = RegistrationServerTestData.EMAIL;

    public static final String TEST_SECOND_EMAIL = RegistrationServerTestData.SECOND_EMAIL;

    public static final String TEST_REGMETA = RegistrationServerTestData.REGMETA;

    public static String deviceToken = "";

    @BeforeClass(groups = "SmokeTest")
    public void makeSureDeviceIsRegistered() {
        // make Sure Device Is Registered
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

        // make Sure Second Device Is Registered
        RegisterDeviceHelper regDev02 = new RegisterDeviceHelper();
        regDev02.logToReport("Make sure second Device is registered");
        regDev02.addApplicationId(TEST_APP);
        regDev02.addApplicationVersion("0.1");
        regDev02.addDeviceUserId(TEST_DUID_SECOND);
        regDev02.addLanguage("en");
        regDev02.addModel("some-tv");
        regDev02.setAppKey(TEST_APP_KEY);
        regDev02.send();
        regDev02.validateResponseCodeOk();
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

        RegisterEmailHelper reg2ndEmail = new RegisterEmailHelper();
        reg2ndEmail.addApplicationId(TEST_APP);
        reg2ndEmail.addApplicationVersion("0.1");
        reg2ndEmail.addDeviceUserId(TEST_DUID);
        reg2ndEmail.addEmail(TEST_SECOND_EMAIL);
        reg2ndEmail.addOptIn(true);
        reg2ndEmail.addRegisterMeta(TEST_REGMETA);
        reg2ndEmail.setAppKey(TEST_APP_KEY);
        reg2ndEmail.send();

        reg2ndEmail.validateResponseCodeOk();
        reg2ndEmail.validateNotNullOrEmpty(RegisterEmailHelper.DEVICE_TOKEN);

        reg2ndEmail.logToReport("Saving value for the second deviceToken");
        String secondDeviceToken = reg2ndEmail.getPathValue(RegisterEmailHelper.DEVICE_TOKEN);
        Boolean differentTokens = !secondDeviceToken.equals(deviceToken);
        reg2ndEmail.logResult(differentTokens, "Second deviceToken is different from the first -" + deviceToken + "-");
    }

    @TestData(id = "", description = "Register email to second device")
    @Test(groups = "SmokeTest", dependsOnMethods = {"registerEmailTest"}, alwaysRun = true)
    public void registerEmailToSecondDeviceTest() {

        RegisterEmailHelper regEmail2ndDev = new RegisterEmailHelper();
        regEmail2ndDev.addApplicationId(TEST_APP);
        regEmail2ndDev.addApplicationVersion("0.1");
        regEmail2ndDev.addDeviceUserId(TEST_DUID_SECOND);
        regEmail2ndDev.addEmail(TEST_EMAIL);
        regEmail2ndDev.addRegisterMeta(TEST_REGMETA);
        regEmail2ndDev.setAppKey(TEST_APP_KEY);
        regEmail2ndDev.send();

        regEmail2ndDev.validateResponseCodeOk();
        regEmail2ndDev.validateNotNullOrEmpty(RegisterEmailHelper.DEVICE_TOKEN);

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

    @TestData(id = "1526733", description = "Invalid duid")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidDuidTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId("ThisShouldNotWork");
        regEmail.addEmail(TEST_EMAIL);
        regEmail.addRegisterMeta(TEST_REGMETA);
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        regEmail.validateResponseCode(HttpStatus.SC_BAD_REQUEST);
        regEmail.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4201");
        regEmail.validateDebug("4201", "Device not found");

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
