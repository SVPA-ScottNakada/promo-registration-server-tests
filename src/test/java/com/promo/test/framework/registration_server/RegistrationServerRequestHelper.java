
package com.promo.test.framework.registration_server;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.not;

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
import java.util.List;
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

    private Response response = null;

    private RequestSpecification requestSpecification = null;

    private String contentType = "application/json";

    private Boolean hasHeaderSignature = true;

    private JsonPath responseInJsonPath = null;

    private String appKeyForSignature = "";

    private Map<String, String> requestParameterMap = new HashMap<String, String>();

    private Map<String, String> requestHeaderMap = new HashMap<String, String>();

    private Map<String, Object> requestBodyMap = new HashMap<String, Object>();

    private final String ENVIRONMENT_HAS_DEBUG_ERRORS =
            RegistrationServerTestData.REGISTRATION_SERVER_ENVIRONMENT_HAS_DEBUG_ERRORS;

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
        response =
                requestSpecification.given()
                    .log().all()
                .get(requestUri);
        // @formatter:on
        setResponseAsJsonPath();

        if (CommonTestData.DEBUG_LOG_API_RESPONSES.toLowerCase().contains("yes")) {
            response.then().log().all();
        }

    }

    /**
     * Send the Post Request.
     */
    protected void sendPostRequest() {

        setupRequestSpecification();

        log.info("\n---> send REQUEST:");
        // @formatter:off
        response =
                requestSpecification.given()
                    .log().all()
                .when()
                    .post(requestUri);
        // @formatter:on
        setResponseAsJsonPath();

        if (CommonTestData.DEBUG_LOG_API_RESPONSES.toLowerCase().contains("yes")) {
            response.then().log().all();
        }

    }

    private void setupRequestSpecification() {

        requestSpecification = RestAssured.with().contentType(contentType);
        String bodyString = "";

        if (!requestParameterMap.isEmpty()) {
            requestSpecification.params(requestParameterMap);
            bodyString = simpleMapToUrlParameterString(requestParameterMap);
        }

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

    }

    private void setResponseAsJsonPath() {
        try {
            responseInJsonPath = new JsonPath(response.then().extract().jsonPath().prettify());
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

    private String simpleMapToUrlParameterString(Map<String, String> mapToConvert) {

        String resultString = "";
        for (Map.Entry<String, String> mapEntry : mapToConvert.entrySet()) {
            if (resultString.isEmpty()) {
                // We add "?" instead of "&"
                resultString = "?";
            } else {
                resultString += "&";
            }

            resultString += mapEntry.getKey() + "=" + mapEntry.getValue();
        }

        return resultString;

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
     * @param expectedStatusCode integer for the value to verify.
     */
    public void validateResponseCode(Integer expectedStatusCode) {
        logToReport(MessageFormat.format("Validating response status code, expected value -{0}-", expectedStatusCode));
        // @formatter:off
        response.then()
                .assertThat()
                    .statusCode(expectedStatusCode);
        // @formatter:on

    }

    /**
     * Validates that a path has an expected value in the response.
     *
     * @param pathToValidate string for the path to validate.
     * @param expectedValue string for the value to verify.
     */
    public void validateValue(String pathToValidate, String expectedValue) {
        String actualValue = responseInJsonPath.getString(pathToValidate);
        logToReport(MessageFormat.format("Validating -{0}-, expected value -{1}-, actual value -{2}-", pathToValidate,
                expectedValue, actualValue));
        assertThat(actualValue, equalTo(expectedValue));

    }

    /**
     * Validates that a value is present in one of the many possible instances of a path in the response.
     *
     * @param pathToValidate string for the path to validate.
     * @param expectedValue string for the value to verify.
     */
    public void validateValueInList(String pathToValidate, Object expectedValue) {
        List<Object> listOfValues = responseInJsonPath.getList(pathToValidate);
        logToReport(MessageFormat.format("Validating -{0}- list of values, expected -{1}- in list -{2}-",
                pathToValidate, expectedValue, listOfValues));
        assertThat(listOfValues, hasItems(expectedValue));
    }

    /**
     * Validates number of instances of a path in the response.
     *
     * @param pathToValidate string for the path to validate.
     * @param expectedCount integer for expected count.
     */
    public void validatePathCount(String pathToValidate, Integer expectedCount) {
        List<Object> listOfValues = responseInJsonPath.getList(pathToValidate);
        Integer actualCount = listOfValues.size();
        logToReport(MessageFormat.format("Validating -{0}- list, expected count -{1}-, actual count -{2}-",
                pathToValidate, expectedCount, actualCount));
        assertThat(actualCount, equalTo(expectedCount));
    }

    /**
     * Validates that a response's path value is not null or empty.
     *
     * @param pathToValidate string for the path to validate.
     */
    public void validateNotNullOrEmpty(String pathToValidate) {
        logToReport(MessageFormat.format("Validating -{0}- not null or empty", pathToValidate));
        String actualValue = getPathValue(pathToValidate);

        assertThat(actualValue, notNullValue());
        // When a path would have to return a null value the extract() function instead returns the string "null"
        assertThat(actualValue, not(equalTo("null")));
        assertThat(actualValue, not(isEmptyString()));

    }

    /**
     * Validates the debug code and message in the JSON response.
     *
     * @param expectedCode string for the debug code.
     * @param expectedMessage string for the debug message.
     */
    public void validateDebug(String expectedCode, String expectedMessage) {
        if (hasDebugErrors()) {
            validateValue(DEBUG_PATH, null);
        } else {
            validateValue(DEBUG_CODE_PATH, expectedCode);
            validateValue(DEBUG_MESSAGE_PATH, expectedMessage);
        }
    }

    public void logResult(Boolean result, String verificationMessage) {
        if (result){
            logToReport(MessageFormat.format("Verified \"{0}\"", verificationMessage));
        }
        else {
            logErrorToReport(MessageFormat.format("Failed verification \"{0}\"", verificationMessage));
        }
    }

    private Boolean hasDebugErrors() {
        if (ENVIRONMENT_HAS_DEBUG_ERRORS.equalsIgnoreCase("yes")) {
            return true;
        } else {
            return false;
        }
    }

    // --- RESPONSE'S PATH INFO METHODS --- ////
    /**
     * Returns the value of a path in the response.
     *
     * @param pathForValue string for the path.
     * @return string with the extracted value.
     */
    public String getPathValue(String pathForValue) {
        String actualValue = responseInJsonPath.getString(pathForValue);
        logToReport(MessageFormat.format("For path -{0}- returning value -{1}-", pathForValue, actualValue));
        return actualValue;
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

    public void logErrorToReport(String message) {
        logToReport(message, Level.ERROR);
        throw new RuntimeException(message);
    }

    private void logToReport(String message, Level logLevel) {
        log.log(logLevel, "---> " + message);
        Reporter.log(message + ".");
    }

    // @formatter:off
    // --- JSON COMMON RESPONSE HEADER PATHS --- ////

    public static final String ERROR_PATH = "error";
    public static final String ERROR_CODE_PATH = ERROR_PATH + ".code";

    // Debug messages should be checked using validateDebug function
    private static final String DEBUG_PATH = "debug";
    private static final String DEBUG_CODE_PATH = DEBUG_PATH + ".code";
    private static final String DEBUG_MESSAGE_PATH = DEBUG_PATH + ".message";


    // @formatter:on

}
