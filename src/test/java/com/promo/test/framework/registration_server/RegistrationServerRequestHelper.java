
package com.promo.test.framework.registration_server;

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
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.exception.JsonPathException;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * @author dalvarado
 */
public class RegistrationServerRequestHelper {

    protected Logger log = LogManager.getLogger(getClass());

    private String requestUri = null;

    private Response requestResponse = null;

    private RequestSpecification requestSpecification = null;

    private String contentType = "application/json";

    private Boolean hasHeaderSignature = true;

    private JsonPath requestInJsonPath = null;

    private String appKeyForSignature = "";

    private Map<String, String> requestParameterMap = new HashMap<String, String>();

    private Map<String, String> requestHeaderMap = new HashMap<String, String>();

    private Map<String, Object> requestBodyMap = new HashMap<String, Object>();

    private final String REQUEST_URI_IS_PRODUCTION =
            RegistrationServerTestData.REGISTRATION_SERVER_BASE_URI_IS_PRODUCTION;

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
     * Adds a pair of Strings as a parameter to be send with the request.
     *
     * @param key string for the name.
     * @param value string for the value.
     */
    public void addStringAsRequestParameter(String key, String value) {
        requestParameterMap.put(key, value);
    }

    /**
     * Adds a pair of Strings in the Header to be send with the request.
     *
     * @param key string for the name.
     * @param value string for the value.
     */
    public void addStringAsRequestHeader(String key, String value) {
        requestHeaderMap.put(key, value);
    }

    /**
     * Adds a pair of Strings in the Body to be send with the request.
     *
     * @param key string for the name.
     * @param value string for the value.
     */
    public void addStringAsRequestBody(String key, String value) {
        requestBodyMap.put(key, value);
    }

    /**
     * Adds a Boolean value in the Body to be send with the request.
     *
     * @param key string for the name.
     * @param value string for the value.
     */
    public void addBooleanAsRequestBody(String key, Boolean value) {
        requestBodyMap.put(key, value);
    }

    // --- SEND REQUEST --- ////
    /**
     * Send the Get Request.
     */
    protected void sendGetRequest() {

        setupRequestSpecification();

        log.info("\n---> send REQUEST:");
        // @formatter:off
        requestResponse =
                requestSpecification.given()
                    .log().all()
                .get(requestUri);
        // @formatter:on
        setRequestAsJsonPath();

        if (CommonTestData.DEBUG_LOG_API_CALL_RESPONSE.toLowerCase().contains("yes")) {
            requestResponse.then().log().all();
        }

    }

    /**
     * Send the Post Request.
     */
    protected void sendPostRequest() {

        setupRequestSpecification();

        log.info("\n---> send REQUEST:");
        // @formatter:off
        requestResponse =
                requestSpecification.given()
                    .log().all()
                .when()
                    .post(requestUri);
        // @formatter:on
        setRequestAsJsonPath();

        if (CommonTestData.DEBUG_LOG_API_CALL_RESPONSE.toLowerCase().contains("yes")) {
            requestResponse.then().log().all();
        }

    }

    private void setupRequestSpecification() {

        requestSpecification = RestAssured.with().contentType(contentType);
        String bodyString = "";

        if (!requestBodyMap.isEmpty()) {
            bodyString = simpleMapToJsonString(requestBodyMap);
            requestSpecification.body(bodyString);
        }

        if (hasHeaderSignature) {
            String timeStamp = getCurrentTimeAsTimeStamp();
            String targetUrl = "/" + requestUri.replace(RegistrationServerTestData.REGISTRATION_SERVER_ENDPOINT, "");
            String signature = generateSignatureForRequest(timeStamp + targetUrl + bodyString);
            addStringAsRequestHeader("promo-signature", signature);
            addStringAsRequestHeader("promo-ts", timeStamp);
        }

        if (!requestHeaderMap.isEmpty()) {
            requestSpecification.headers(requestHeaderMap);
        }

        if (!requestParameterMap.isEmpty()) {
            requestSpecification.params(requestParameterMap);
        }

    }

    private void setRequestAsJsonPath() {
        try {
            requestInJsonPath = new JsonPath(requestResponse.then().extract().jsonPath().prettify());
        } catch (JsonPathException e) {
            log.warn("---> Response is not JSON");
        }
    }

    // Signature

    /**
     * Generates the signature for the specified string using HmacSHA256.
     *
     * @param strForSig candidate string for generating the signature.
     * @return Signature for specified input string.
     */
    private String generateSignatureForRequest(String strForSig) {

        if (appKeyForSignature.isEmpty()) {
            log.warn("---> appKeyForSignature is empty");
            return "";
        }

        String signature = null;

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

    private static String getCurrentTimeAsTimeStamp() {
        String unixTime = Long.toString(System.currentTimeMillis() / 1000L);
        return unixTime;
    }

    private String simpleMapToJsonString(Map<String, Object> mapToConvert) {

        String resultString = "";
        for (Map.Entry<String, Object> mapEntry : mapToConvert.entrySet()) {
            if (resultString.isEmpty()) {
                // No comma
            } else {
                resultString += ",";
            }

            String key = "\"" + mapEntry.getKey() + "\"";
            String value = mapEntry.getValue().toString();

            if (mapEntry.getValue().getClass().getTypeName().contains("String")) {
                value = "\"" + value + "\"";
            }

            resultString += key + ": " + value;
        }

        return "{" + resultString + "}";

    }

    // --- VALIDATIONS --- ////
    /**
     * Validates that the response's Status Code value is OK.
     */
    public void validateResponseCodeOk() {
        validateResponseCode(HttpStatus.SC_OK);
    }

    /**
     * Validates that the response's Status Code value has an expected value.
     *
     * @param expectedCode integer for the value to verify.
     */
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
     *
     * @param pathToValidate string for the path to validate.
     * @param expectedValue string for the value to verify.
     */
    public void validateValue(String pathToValidate, String expectedValue) {
        String testValue = requestInJsonPath.getString(pathToValidate);
        logToReport(MessageFormat.format("Validating -{0}-, expected value -{1}-, test value -{2}-", pathToValidate,
                expectedValue, testValue));
        assertThat(testValue, equalTo(expectedValue));

    }

    /**
     * Validates the debug code and message in the JSON response.
     *
     * @param code string for the debug code.
     * @param message string for the debug message.
     */
    public void validateDebug(String code, String message) {
        if (isProduction()) {
            validateValue(DEBUG_PATH, null);
        } else {
            validateValue(DEBUG_CODE_PATH, code);
            validateValue(DEBUG_MESSAGE_PATH, message);
        }
    }

    /**
     * Returns true if the configuration property registration.server.baseurl.is.production is set to "yes",
     * anything else returns false
     */
    public Boolean isProduction() {
        if (REQUEST_URI_IS_PRODUCTION.equalsIgnoreCase("yes")) {
            return true;
        } else {
            return false;
        }
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

    /**
     * Sets the app_key for the request's header signature.
     *
     * @param newKey string for the new app_key.
     */
    public void setAppKey(String newKey) {
        appKeyForSignature = newKey;
    }

    /**
     * Sets the Content-Type for the request.
     *
     * @param newType string for the new Content-Type.
     */
    public void setContentType(String newType) {
        contentType = newType;
    }

    /**
     * Specifies if a request needs to send a signature in the header.
     *
     * @param hasSignature
     */
    public void setHasHeaderSignature(Boolean hasSignature) {
        hasHeaderSignature = hasSignature;
    }

    // LOG TO LOG4J AND REPORTER

    public void logToReport(String message) {
        logToReport(message, Level.INFO);
    }

    private void logToReport(String message, Level logLevel) {
        log.log(logLevel, "---> " + message);
        Reporter.log(message + ".");
    }

    // @formatter:off
    // --- XML COMMON RESPONSE HEADER PATHS --- ////

    public static final String ERROR_PATH = "error";
    public static final String ERROR_CODE_PATH = ERROR_PATH + ".code";

    // Debug messages should be checked using validateDebug function
    private static final String DEBUG_PATH = "debug";
    private static final String DEBUG_CODE_PATH = DEBUG_PATH + ".code";
    private static final String DEBUG_MESSAGE_PATH = DEBUG_PATH + ".message";


    // @formatter:on

}
