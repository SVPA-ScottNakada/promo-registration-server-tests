
package com.promo.test.suite.registration_server;

import com.promo.test.framework.registration_server.RedeemedHelper;
import com.promo.test.framework.registration_server.RegisterDeviceHelper;
import com.promo.test.framework.utils.TestData;
import com.promo.test.suite.BaseApiTest;

import org.testng.annotations.Test;

public class RedeemedTests extends BaseApiTest {

    public static final String TEST_DUID = "abcdefghi_mytest";

    public static final String TEST_APP = "postman";

    public static final String TEST_APP_KEY = "hogehoge";

    public static final String TEST_DEVTOKEN = "e80302132ee18faa4b8704293d15cdf06c191ge8d094501f4fb766c9c281d6a3"; // <--
                                                                                                                   // Gibberish

    @TestData(id = "", description = "Required parameters")
    @Test(groups = "BrokenTest")
    public void requiredParametersTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(TEST_DEVTOKEN);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCodeOk();

    }

    @TestData(id = "", description = "Missing appId parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingAppIdTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(TEST_DEVTOKEN);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCode(422);
        redeemed.validateValue(RedeemedHelper.ERROR_CODE_PATH, "4005");
        redeemed.validateDebug("4005", "Missing member appId");

    }

    @TestData(id = "", description = "Missing appVersion parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingAppVersionTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(TEST_DEVTOKEN);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCode(422);
        redeemed.validateValue(RedeemedHelper.ERROR_CODE_PATH, "4005");
        redeemed.validateDebug("4005", "Missing member appVersion");

    }

    @TestData(id = "", description = "Missing duid parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingDuidTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceToken(TEST_DEVTOKEN);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCode(422);
        redeemed.validateValue(RedeemedHelper.ERROR_CODE_PATH, "4005");
        redeemed.validateDebug("4005", "Missing member duid");

    }

    @TestData(id = "", description = "Missing deviceToken parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingDeviceTokenTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCode(422);
        redeemed.validateValue(RedeemedHelper.ERROR_CODE_PATH, "4005");
        redeemed.validateDebug("4005", "Missing member deviceToken");

    }

    @TestData(id = "", description = "No app key, invalid signature")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidSignatureTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(TEST_DEVTOKEN);
        redeemed.send();

        redeemed.validateResponseCode(401);
        redeemed.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        redeemed.validateDebug("4001", "Invalid signature");

    }

}
