
package com.promo.test.framework.registration_server;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.promo.test.suite.CommonTestData;
import com.promo.test.suite.registration_server.RegistrationServerTestData;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Reporter;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * @author dalvarado
 */
public class RegistrationServerRequestHelper {

    protected Logger log = LogManager.getLogger(getClass());

    private String requestUri = null;

    protected Response requestResponse = null;

    private String appKeyForSignature = null;

    protected Map<String, String> requestParameterMap = new HashMap<String, String>();

    protected Map<String, String> requestHeaderMap = new HashMap<String, String>();

    protected Map<String, String> requestBodyMap = new HashMap<String, String>();

    public RegistrationServerRequestHelper() {
        this(RegistrationServerTestData.REGISTRATION_SERVER_BASE_URI);
    }

    public RegistrationServerRequestHelper(String newUri) {
        requestUri = newUri;
        RestAssured.useRelaxedHTTPSValidation();
        if (!CommonTestData.REQUEST_PROXY.isEmpty() && !CommonTestData.REQUEST_PROXY_PORT.isEmpty()) {
            RestAssured.proxy(CommonTestData.REQUEST_PROXY, Integer.valueOf(CommonTestData.REQUEST_PROXY_PORT));
        }
    }

    //// --- REQUEST PARAMETERS, HEADERS, BODY --- ////
    /**
     * Adds a pair of Strings as a parameter to be send with the Get request.
     *
     * @param key string for the name of the parameter.
     * @param value string for the value of the parameter.
     */
    public void addStringAsRequestParameter(String key, String value) {
        requestParameterMap.put(key, value);
    }

    public void addStringAsRequestHeader(String key, String value) {
        requestHeaderMap.put(key, value);
    }

    public void addStringAsRequestBody(String key, String value) {
        requestBodyMap.put(key, value);
    }

    // --- SEND REQUEST --- ////
    /**
     * Send the Get Request.
     */
    protected void sendGetRequest() {
        log.info("\n---> send REQUEST:");
        // @formatter:off
        requestResponse =
                given()
                    .params(requestParameterMap)
                    .log().all()
                .get(requestUri);
        // @formatter:on

        if (CommonTestData.DEBUG_LOG_API_CALL_RESPONSE.toLowerCase().contains("yes")) {
            requestResponse.then().log().all();
        }
    }

    /**
     * Send the Post Request.
     */
    protected void sendPostRequest() {

        String bodyString = simpleMapToJsonString(requestBodyMap);
        String timeStamp = getCurrentTimeAsTimeStamp();
        String targetUrl = "/" + requestUri.replace(RegistrationServerTestData.REGISTRATION_SERVER_ENDPOINT, "");
        String signature = generateSignatureForRequest(timeStamp + targetUrl + bodyString);

        addStringAsRequestHeader("promo-signature", signature);
        addStringAsRequestHeader("promo-ts", timeStamp);

        log.info("\n---> send REQUEST:");
        // @formatter:off
        requestResponse =
                given()
                    .contentType("application/json")
                    .headers(requestHeaderMap)
                    .body(bodyString)
                    .log().all()
                .when()
                    .post(requestUri);
        // @formatter:on

        if (CommonTestData.DEBUG_LOG_API_CALL_RESPONSE.toLowerCase().contains("yes")) {
            requestResponse.then().log().all();
        }

    }

    // Signature

    /**
     * Generates the signature for the specified string using MD5 Hash.
     *
     * @param strForSig candidate string for generating the signature.
     * @return Signature for specified input string.
     */
    protected String generateSignatureForRequest(String strForSig) {
        String signature = null;

        if (appKeyForSignature.isEmpty()) {
            log.warn("---> appKeyForSignature is empty");
        }

        try {
            String secret = appKeyForSignature;
            String message = strForSig;

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(message.getBytes()));
            signature = hash;
        } catch (Exception e) {
            log.error("---> generateSignatureForRequest() - Encountered exception: " + e.getMessage());
        }

        return signature;
    }

    public static String getCurrentTimeAsTimeStamp() {
        String unixTime = Long.toString(System.currentTimeMillis() / 1000L);
        return unixTime;
    }

    private String simpleMapToJsonString(Map<String, String> mapToConvert) {

        String resultString = "";
        Boolean firstParam = true;
        for (Map.Entry<String, String> mapEntry : mapToConvert.entrySet()) {
            if (firstParam) {
                firstParam = false;
            } else {
                resultString += ",";
            }
            resultString += "\"" + mapEntry.getKey() + "\": \"" + mapEntry.getValue() + "\"";
        }
        resultString = "{" + resultString + "}";

        return resultString;

    }

    // --- VALIDATIONS --- ////
    public void validateResponseCodeOk() {
        validateResponseCode(HttpStatus.SC_OK);
    }

    public void validateResponseCode(Integer expectedCode) {
        logToReport(MessageFormat.format("Validating response status code, expected value -{0}-", expectedCode));
     // @formatter:off
        requestResponse.then()
                .assertThat()
                    .statusCode(expectedCode);
        // @formatter:on

    }

    /**
     * Validates that a path has an expected value in the response.
     * The expected status code for the response is "200".
     *
     * @param pathToValidate string for the path to validate.
     * @param expectedValue string for the value to verify.
     */
    public void validateValue(String pathToValidate, String expectedValue) {
        String testValue = "";
        try {
            testValue = requestResponse.jsonPath().get(pathToValidate).toString();
        } catch (java.lang.NullPointerException e) {
            logErrorToReport(MessageFormat.format("Failed getting value from path -{0}-", pathToValidate));
        }
        logToReport(MessageFormat.format("Validating -{0}-, expected value -{1}-, test value -{2}-", pathToValidate,
                expectedValue, testValue));

        assertThat(expectedValue, equalTo(testValue));

    }

    // --- HELPER'S GET AND SET --- ////
    /**
     * Sets the URI for the request.
     *
     * @param newUri string for the new URI.
     */
    public void setRequestUri(String newUri) {
        requestUri = newUri;
    }

    public void setAppKey(String key) {
        appKeyForSignature = key;
    }

    // LOG TO LOG4J AND REPORTER

    private void logErrorToReport(String message) {
        logToReport(message, Level.ERROR);
    }

    public void logToReport(String message) {
        logToReport(message, Level.INFO);
    }

    private void logToReport(String message, Level logLevel) {
        log.log(logLevel, "---> " + message);
        Reporter.log(message + ".");
    }

    // @formatter:off
    // --- XML COMMON RESPONSE HEADER PATHS --- ////

    public static final String DEBUG_PATH = "debug";
    public static final String DEBUG_CODE_PATH = DEBUG_PATH + ".code";
    public static final String DEBUG_MESSAGE_PATH = DEBUG_PATH + ".message";
    public static final String ERROR_PATH = "error";
    public static final String ERROR_CODE_PATH = ERROR_PATH + ".code";

    // @formatter:on

}
