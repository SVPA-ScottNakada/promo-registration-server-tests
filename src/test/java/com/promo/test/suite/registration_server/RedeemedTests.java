
package com.promo.test.suite.registration_server;

import com.promo.test.framework.registration_server.RedeemedHelper;
import com.promo.test.framework.registration_server.RegisterDeviceHelper;
import com.promo.test.framework.registration_server.RegisterEmailHelper;
import com.promo.test.framework.utils.TestData;
import com.promo.test.suite.BaseApiTest;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

public class RedeemedTests extends BaseApiTest {

    public static final String TEST_DUID = RegistrationServerTestData.DUID;

    public static final String TEST_APP = RegistrationServerTestData.APP_NAME;

    public static final String TEST_APP_KEY = RegistrationServerTestData.APP_KEY;

    public static final String TEST_EMAIL = RegistrationServerTestData.EMAIL;

    public static final String TEST_REGMETA = RegistrationServerTestData.REGMETA;

    public static String testDevToken = "";

    public void setDevToken() {
        if (testDevToken.isEmpty()) {
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
            testDevToken = regEmail.getPathValue(RegisterEmailHelper.DEVICE_TOKEN);
        }
    }

    @TestData(id = "1526330", description = "Required parameters")
    @Test(groups = "SmokeTest")
    public void requiredParametersTest() {
        setDevToken();

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(testDevToken);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCodeOk();
        redeemed.validateNotNullOrEmpty(RedeemedHelper.PROMOS);

    }

    @TestData(id = "1526331", description = "Missing appId parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingAppIdTest() {
        setDevToken();

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(testDevToken);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeemed.validateValue(RedeemedHelper.ERROR_CODE_PATH, "4005");
        redeemed.validateDebug("4005", "Missing member appId");

    }

    @TestData(id = "1526332", description = "Missing appVersion parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingAppVersionTest() {
        setDevToken();

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(testDevToken);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeemed.validateValue(RedeemedHelper.ERROR_CODE_PATH, "4005");
        redeemed.validateDebug("4005", "Missing member appVersion");

    }

    @TestData(id = "1526333", description = "Missing duid parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingDuidTest() {
        setDevToken();

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceToken(testDevToken);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeemed.validateValue(RedeemedHelper.ERROR_CODE_PATH, "4005");
        redeemed.validateDebug("4005", "Missing member duid");

    }

    @TestData(id = "1526334", description = "Missing deviceToken parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingDeviceTokenTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeemed.validateValue(RedeemedHelper.ERROR_CODE_PATH, "4005");
        redeemed.validateDebug("4005", "Missing member deviceToken");

    }

    @TestData(id = "1526335", description = "No app key, invalid signature")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidSignatureTest() {
        setDevToken();

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(testDevToken);
        redeemed.send();

        redeemed.validateResponseCode(HttpStatus.SC_UNAUTHORIZED);
        redeemed.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        redeemed.validateDebug("4001", "Invalid signature");

    }

    @TestData(id = "1526336", description = "Invalid appId")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidAppIdTest() {
        setDevToken();

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId("ThisShouldNotWork");
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(testDevToken);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCode(HttpStatus.SC_UNAUTHORIZED);
        redeemed.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        redeemed.validateDebug("4001", "Missing App key");

    }

    @TestData(id = "1526337", description = "Invalid deviceToken pattern")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidDeviceTokenPatternTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken("ThisShouldNotWork");
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeemed.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4002");
        redeemed.validateDebug("4002", "Invalid pattern for deviceToken.");

    }

    @TestData(id = "1526338", description = "Invalid deviceToken")
    @Test(groups = {"BrokenTest", "NegativeTest"})
    public void invalidDeviceTokenTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken("x10101111xx11fzz1b1101101d11cdf01c101gx1d001101f1fb111c0c111d1z1");
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCode(HttpStatus.SC_UNAUTHORIZED);
        redeemed.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        redeemed.validateDebug("4001", "Invalid signature");

    }

    @TestData(id = "1526339", description = "Invalid app key")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidAppKeyTest() {
        setDevToken();

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(testDevToken);
        redeemed.setAppKey("ThisShouldNotWork");
        redeemed.send();

        redeemed.validateResponseCode(HttpStatus.SC_UNAUTHORIZED);
        redeemed.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        redeemed.validateDebug("4001", "Invalid signature");

    }

}
