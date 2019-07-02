
package com.promo.test.framework;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import com.promo.test.suite.CommonTestData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.Reporter;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * @author dalvarado
 */
public class BaseApiCallHelper {

    protected Logger log = LogManager.getLogger(getClass());

    // Request

    private String requestUri = null;

    private RequestSpecification requestSpecification = null;

    private String contentType = null;

    protected Map<String, Object> requestParameterMap = new LinkedHashMap<String, Object>();

    protected Map<String, Object> queryParameterMap = new LinkedHashMap<String, Object>();

    protected Map<String, Object> headerParameterMap = new LinkedHashMap<String, Object>();

    protected Map<String, Object> bodyParameterMap = new LinkedHashMap<String, Object>();

    protected Map<String, Object> formParameterMap = new LinkedHashMap<String, Object>();

    private String bodyParameterString = "";

    // Response

    protected Response response = null;

    protected Map<String, String> responseHeaderMap = new LinkedHashMap<String, String>();

    // Constructor

    public BaseApiCallHelper(String newUri) {
        setRequestUri(newUri);
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

    /**
     * Adds a pair of Strings as a parameter to be send with the Post request.
     *
     * @param key string for the name of the parameter.
     * @param value string for the value of the parameter.
     */
    public void addStringAsQueryParameter(String key, String value) {
        queryParameterMap.put(key, value);
    }

    /**
     * Adds a pair of Strings in the Header to be send with the request.
     *
     * @param key string for the name.
     * @param value string for the value.
     */
    public void addStringAsHeaderParameter(String key, String value) {
        headerParameterMap.put(key, value);
    }

    /**
     * Adds a pair of Strings in the Body to be send with the request.
     *
     * @param key string for the name.
     * @param value string for the value.
     */
    public void addStringAsBodyParameter(String key, String value) {
        bodyParameterMap.put(key, value);
    }

    /**
     * Adds a Boolean value in the Body to be send with the request.
     *
     * @param key string for the name.
     * @param value string for the value.
     */
    public void addBooleanAsRequestBody(String key, Boolean value) {
        bodyParameterMap.put(key, value);
    }

    /**
     * Adds a pair of Strings as a form parameter to be send with the request.
     *
     * @param key string for the name.
     * @param value string for the value.
     */
    public void addStringAsFormParameter(String key, String value) {
        formParameterMap.put(key, value);
    }

    // --- SEND REQUEST --- ////
    /**
     * Send the Get Request.
     */
    protected void sendGetRequest() {
        sendGetRequest(true);
    }

    /**
     * Send the Get Request.
     * 
     * @param followRedirects boolean to configure if the request should follow redirects
     */
    protected void sendGetRequest(Boolean followRedirects) {

        // we add qa_data as a REQUEST parameter to help the warning filters
        if (CommonTestData.QA_DATA_PARAMETER.equalsIgnoreCase("yes")) {
            addStringAsRequestParameter("qa_data", "true");
        }

        setupRequestSpecification();
        log.info("\n---> send REQUEST:");
        response = requestSpecification.given().redirects().follow(followRedirects).log().all().get(requestUri);

        populateResponseHeaderMap();

        if (CommonTestData.DEBUG_LOG_API_RESPONSES.equalsIgnoreCase("yes") && !contentType.equals("image/png")) {
            response.then().log().all();
        }
    }

    /**
     * Send the Post Request.
     */
    protected void sendPostRequest() {

        // we add qa_data as a QUERY parameter to help the warning filters
        if (CommonTestData.QA_DATA_PARAMETER.equalsIgnoreCase("yes")) {
            addStringAsQueryParameter("qa_data", "true");
        }

        setupRequestSpecification();
        log.info("\n---> send REQUEST:");
        response = requestSpecification.given().log().all().when().post(requestUri);

        populateResponseHeaderMap();

        if (CommonTestData.DEBUG_LOG_API_RESPONSES.equalsIgnoreCase("yes")) {
            response.then().log().all();
        }

    }

    private void setupRequestSpecification() {

        requestSpecification = RestAssured.with().contentType(contentType);

        if (!requestParameterMap.isEmpty()) {
            requestSpecification.params(requestParameterMap);
        }

        if (!queryParameterMap.isEmpty()) {
            requestSpecification.queryParams(queryParameterMap);
        }

        if (!headerParameterMap.isEmpty()) {
            requestSpecification.headers(headerParameterMap);
        }

        if (!formParameterMap.isEmpty()) {
            requestSpecification.formParams(formParameterMap);
        }

        // the bodyParameterMap is only used to temporary store values, each call needs to pass that info into the
        // bodyParameterString according to its needs
        if (!bodyParameterString.isEmpty() || !bodyParameterMap.isEmpty()) {
            if (bodyParameterString.isEmpty()) {
                log.warn("\n---> bodyParameterMap's data hasn't been populated into bodyParameterString");
            }
            requestSpecification.body(bodyParameterString);
        }

    }

    // --- VALIDATIONS --- ////

    /**
     * Validates the expected status code for the response.
     *
     * @param expectedStatusCode integer for the response's expected status code.
     */
    public void validateResponseStatusCode(Integer expectedStatusCode) {

        String actualStatusCode = String.valueOf(response.then().extract().statusCode());

        logToReport(MessageFormat.format("Validating response Status Code, expected value -{0}-, actual value -{1}-",
                expectedStatusCode, actualStatusCode));

        // @formatter:off
        response.then()
                .assertThat()
                    .statusCode(expectedStatusCode);
        // @formatter:on
    }

    /**
     * Validates that a pair of strings are equal.
     *
     * @param valueOne string for the first string to compare.
     * @param valueTwo string for the second string to compare.
     */
    public void validateStringsAreEqual(String valueOne, String valueTwo) {
        logToReport(MessageFormat.format("Validating -{0}- is equal to -{1}-", valueOne, valueTwo));
        assertThat(valueOne, equalTo(valueTwo));
    }

    /**
     * Validates that a pair of strings are not equal.
     *
     * @param valueOne string for the first string to compare.
     * @param valueTwo string for the second string to compare.
     */
    public void validateStringsAreNotEqual(String valueOne, String valueTwo) {
        logToReport(MessageFormat.format("Validating -{0}- is not equal to -{1}-", valueOne, valueTwo));
        assertThat(valueOne, not(equalTo(valueTwo)));
    }

    /**
     * Validates that a string matches a given regular expression.
     *
     * @param value string to validate.
     * @param pattern regex pattern.
     */
    public void validatePattern(String value, String pattern) {
        logToReport(MessageFormat.format("Validating -{0}- has pattern: {1}", value, pattern));
        Assert.assertTrue(value.matches(pattern));
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
     * Returns the URI of the request.
     *
     * @return string with the request's URI.
     */
    public String getRequestUri() {
        return requestUri;
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
     * Gets the Content-Type for the request.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the bodyParameterString for the request.
     *
     * @param newValue string.
     */
    protected void setBodyParameterString(String newValue) {
        bodyParameterString = newValue;
    }

    // --- RESPONSE'S HEADER INFO METHODS --- ////

    private void populateResponseHeaderMap() {

        Headers allHeaders = response.headers();

        for (Header header : allHeaders) {
            responseHeaderMap.put(header.getName(), header.getValue());
        }

    }

    public Boolean containsHeader(String header) {
        Boolean doesHeaderExist = responseHeaderMap.containsKey(header);
        if (doesHeaderExist) {
            log.info(MessageFormat.format("---> Found header -{0}- in response", header));
        } else {
            log.info(MessageFormat.format("---> Couldnt find header -{0}- in response", header));
        }
        return doesHeaderExist;
    }

    public String getResponseHeaderValue(String header) {
        String actualHeaderValue = "";
        if (responseHeaderMap.containsKey(header)) {
            actualHeaderValue = responseHeaderMap.get(header);
            log.info(MessageFormat.format("---> Getting header -{0}- from response, returning -{1}-", header,
                    actualHeaderValue));
        } else {
            log.warn(MessageFormat.format("---> Couldn't find header -{0}- in response, returning empty string",
                    header));
        }
        return actualHeaderValue;
    }

    public String getResponseHeadersAsString() {
        return responseHeaderMap.toString();
    }

    // --- LOG METHODS --- ////

    /**
     * Function to log specific conditions needed to meet test criteria.
     *
     * @param result of the criteria that needs to be met.
     * @param verificationMessage text specifying the criteria that was tested.
     */
    public void logResult(Boolean result, String verificationMessage) {
        if (result) {
            logToReport(MessageFormat.format("Verified \"{0}\"", verificationMessage));
        } else {
            throw new RuntimeException(MessageFormat.format("Failed verification \"{0}\"", verificationMessage));
        }
    }

    protected void logToReport(String message) {
        log.info("---> " + message);
        Reporter.log(message + ".");
    }

    // MAP to String functions

    protected String simpleMapToUrlParameterString(Map<String, Object> mapToConvert) {

        return simpleMapToString(mapToConvert, "?", "=", "&", "");

    }

    protected String simpleMapToString(Map<String, Object> mapToConvert, String start, String equals, String separator,
            String end) {
        String resultString = "";
        for (Map.Entry<String, Object> mapEntry : mapToConvert.entrySet()) {
            if (resultString.isEmpty()) {
                // No separator
            } else {
                // Add separator between entries
                resultString += separator;
            }
            // key equals value
            resultString += mapEntry.getKey() + equals + mapEntry.getValue();
        }

        return start + resultString + end;

    }

    // General functions

    /**
     * @return String with current Unix time
     */
    public static String getCurrentTimeAsTimeStamp() {
        String unixTime = Long.toString(System.currentTimeMillis() / 1000L);
        return unixTime;
    }

}
