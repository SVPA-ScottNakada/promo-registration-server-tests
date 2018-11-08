
package com.promo.test.framework.registration_server;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import com.promo.test.suite.CommonTestData;
import com.promo.test.suite.registration_server.RegistrationServerTestData;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
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
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;

/**
 * @author dalvarado
 */
public class RegistrationServerRequestHelper {

    protected Logger log = LogManager.getLogger(getClass());

    private String requestUri = null;

    protected Response requestResponse = null;

    protected Map<String, String> requestParameterMap = new HashMap<String, String>();

    protected Map<String, String> requestHeaderMap = new HashMap<String, String>();

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

    //// --- REQUEST PARAMETERS --- ////
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

        requestResponse.then().log().ifValidationFails();
        requestResponse.then().log().ifError();
        if (CommonTestData.DEBUG_LOG_API_CALL_RESPONSE.toLowerCase().contains("yes")) {
            requestResponse.then().log().all();
        }
    }

    /**
     * Send the Post Request.
     */
    protected void sendPostRequest() {
        
        
        String body = "{\"appId\": \"postman\", \"appVersion\": \"0.1\",\"duid\": \"abcdefghi_mytest\",\"lang\": \"en\",\"model\": \"some-tv\"}";
       
        String timeStamp = getCurrentTimeAsTimeStamp();
        
        String targetUrl = "/" + requestUri.replace(RegistrationServerTestData.REGISTRATION_SERVER_ENDPOINT, "");
        
        String signature = generateSignatureForRequest(timeStamp + targetUrl + body);
        
        addStringAsRequestHeader("promo-signature", signature);
        addStringAsRequestHeader("promo-ts", timeStamp);
        
        
        log.info("\n---> send REQUEST:");
        // @formatter:off
        requestResponse =
                given()
                    .contentType("application/json")
                    .headers(requestHeaderMap)
                    .body(body)
                    .log().all()
                .when()
                    .post(requestUri);
        // @formatter:on

        requestResponse.then().log().ifValidationFails();
        requestResponse.then().log().ifError();
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
        System.out.println("String for request= " + strForSig);
        String signature = null;
        
        try {
            String secret = "hogehoge";
            String message = strForSig;

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(message.getBytes()));
            signature = hash;
           }
           catch (Exception e){
               log.error("---> generateSignatureForRequest() - Encountered exception: " + e.getMessage());
           }
          
        System.out.println("Signature for request= " + signature);
        return signature;
    }
    
    public static String getCurrentTimeAsTimeStamp() {
        String unixTime = Long.toString(System.currentTimeMillis() / 1000L);
        return unixTime;
    }

    // --- VALIDATIONS --- ////
    public void validateResponseCode() {
     // @formatter:off
        requestResponse.then()
                .assertThat()
                    .statusCode(HttpStatus.SC_OK);
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
        validateValue(pathToValidate, expectedValue, HttpStatus.SC_OK);
    }

    /**
     * Validates that a path has an expected value in the response.
     *
     * @param pathToValidate string for the path to validate.
     * @param expectedValue string for the value to verify.
     * @param statusCode integer for the response's status code.
     */
    public void validateValue(String pathToValidate, String expectedValue, Integer statusCode) {
        String testValue = requestResponse.then().extract().path(pathToValidate).toString();
        String testStatusCode = String.valueOf(requestResponse.then().extract().statusCode());
        logToReport(MessageFormat.format("Validating -{0}-, expected value -{1}-, test value -{2}-", pathToValidate,
                expectedValue, testValue));
        if (statusCode != HttpStatus.SC_OK) {
            logToReport(MessageFormat.format("Validating response Status Code, expected value -{0}-, test value -{1}-",
                    statusCode, testStatusCode));
        }
        // @formatter:off
        requestResponse.then()
                .assertThat()
                    .statusCode(statusCode)
                        .body(pathToValidate, equalTo(expectedValue));
        // @formatter:on
    }

    /**
     * Validates that a path contains an expected value in the response.
     *
     * @param pathToValidate string for the path to validate.
     * @param expectedValue string for the value to verify.
     * @param statusCode integer for the response's status code.
     */
    public void validateValueContains(String pathToValidate, String expectedValue) {
        String testValue = requestResponse.then().extract().path(pathToValidate).toString();
        logToReport(MessageFormat.format("Validating -{0}-, test value -{1}- contains value -{2}- ", pathToValidate,
                testValue, expectedValue));

        assertThat(testValue, containsString(expectedValue));

    }

    /**
     * Validates that a response's path value is not null or empty.
     * The expected status code for the response is "200".
     *
     * @param pathToValidate string for the path to validate.
     */
    public void validateNotNullOrEmpty(String pathToValidate) {
        // @formatter:off
        logToReport(MessageFormat.format("Validating -{0}- not null or empty", pathToValidate));
        String testValue = requestResponse.then()
                                .assertThat()
                                    .statusCode(HttpStatus.SC_OK)
                                    .body(pathToValidate, notNullValue())
                                .extract()
                                    .path(pathToValidate).toString();
        // @formatter:on
        logToReport(MessageFormat.format("path value is -{0}-", testValue));

        // When a path would have to return a null value the extract() function instead returns the string "null"
        assertThat(testValue, not(equalTo("null")));
        assertThat(testValue, not(isEmptyString()));
    }

    /**
     * Validates that a response's path value is empty.
     * The expected status code for the response is "200".
     *
     * @param pathToValidate string for the path to validate.
     */
    public void validateEmpty(String pathToValidate) {
        // @formatter:off
        logToReport(MessageFormat.format("Validating -{0}- is empty", pathToValidate));
        String testValue = requestResponse.then()
                                .assertThat()
                                    .statusCode(HttpStatus.SC_OK)
                                    .body(pathToValidate, notNullValue())
                                .extract()
                                    .path(pathToValidate).toString();
        // @formatter:on
        logToReport(MessageFormat.format("path value is -{0}-", testValue));

        // When a path would have to return a null value the extract() function instead returns the string "null"
        assertThat(testValue, isEmptyString());
    }

    /**
     * Validates the number of times a path is present in a response.
     * The expected status code for the response is "200".
     *
     * @param pathToCount string for the path to validate.
     * @param expectedCount integer for the expected count.
     */
    public void validateCount(String pathToCount, Integer expectedCount) {
        String originalPath = pathToCount;
        pathToCount = originalPath + ".size()";
        String testCount = requestResponse.then().extract().path(pathToCount).toString();
        logToReport(MessageFormat.format("Validating -{0}-, expected count -{1}-, test count -{2}-", originalPath,
                expectedCount, testCount));
        // @formatter:off
        requestResponse.then()
                .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                        .body(pathToCount, equalTo(expectedCount));
        // @formatter:on
    }

    /**
     * Validates that a value is present in one of the many possible instances of a path in the response.
     *
     * @param pathToValidate string for the path to validate.
     * @param expectedValue string for the value to verify.
     */
    public void validateValueInList(String pathToValidate, String expectedValue) {
        Reporter.log(MessageFormat.format("Validating xpath -{0}- list of values", pathToValidate));
        List<String> listOfValues = getListOfValues(pathToValidate);
        logToReport(MessageFormat.format("Validating -{0}- in list", expectedValue));
        assertThat(listOfValues, hasItems(expectedValue));
    }

    /**
     * Validates a value's length in a given path.
     *
     * @param pathToValidate string for the path to validate.
     * @param minValue integer for the minimum value to expect.
     * @param maxValue integer for the maximum value to expect.
     */
    public void validateValueLengthBetween(String pathToValidate, Integer minValue, Integer maxValue) {
        String testValue = getPathValue(pathToValidate);
        logToReport(MessageFormat.format("Validating -{0}- lenght between -{1}- and -{2}-, lenght is -{3}-", testValue,
                minValue, maxValue, testValue.length()));
        assertThat(testValue.length(), greaterThanOrEqualTo(minValue));
        assertThat(testValue.length(), lessThanOrEqualTo(maxValue));
    }

    /**
     * Validates that the number of times a path is present in a response is in a certain range.
     *
     * @param pathToCount string for the path to validate.
     * @param minValue integer for the minimum value to expect.
     * @param maxValue integer for the maximum value to expect.
     */
    public void validateCountBetween(String pathToCount, Integer minValue, Integer maxValue) {
        String originalPath = pathToCount;
        pathToCount = originalPath + ".size()";
        Integer testCount = requestResponse.then().extract().path(pathToCount);
        logToReport(MessageFormat.format("Validating -{0}- count between -{1}- and -{2}-, count is -{3}-", originalPath,
                minValue, maxValue, testCount));
        assertThat(testCount, greaterThanOrEqualTo(minValue));
        assertThat(testCount, lessThanOrEqualTo(maxValue));
    }

    /**
     * Validates that the number of times a path is present in a response is greater or equal than a given value.
     *
     * @param pathToCount string for the path to validate.
     * @param valueToCompare integer for the minimum value to expect.
     */
    public void validateCountGreaterThanOrEqualTo(String pathToCount, Integer valueToCompare) {
        String originalPath = pathToCount;
        pathToCount = originalPath + ".size()";
        Integer testCount = requestResponse.then().extract().path(pathToCount);
        logToReport(MessageFormat.format("Validating -{0}- Greater than or equal to -{1}-, count is -{2}-",
                originalPath, valueToCompare, testCount));
        assertThat(testCount, greaterThanOrEqualTo(valueToCompare));
    }

    /**
     * Looks for a specific value in all the possible instances of a path in the response and validates the value of a
     * sibling path.
     *
     * @param whereTagPath string for the path to look for a specific value.
     * @param isEqualTo string for the value where searching for.
     * @param getTagValue string for the sibling path to validate.
     * @param expectedValue string for the value to verify.
     */
    public void validateValueWhere(String whereTagPath, String isEqualTo, String getTagValue, String expectedValue) {
        String testValue = getValueWhere(whereTagPath, isEqualTo, getTagValue);
        Reporter.log(MessageFormat.format("In xpath -{0}- where value equals -{1}-, Validating value from -{2}-",
                whereTagPath, isEqualTo, getTagValue));
        validateStringsAreEqual(testValue, expectedValue);
    }

    /**
     * Looks for a specific value in all the possible instances of a path in the response and validates the value is not
     * null of empty.
     *
     * @param whereTagPath string for the path to look for a specific value.
     * @param isEqualTo string for the value where searching for.
     * @param getTagValue string for the sibling path to validate.
     */
    public void validateNotNullOrEmptyWhere(String whereTagPath, String isEqualTo, String getTagValue) {
        String testValue = getValueWhere(whereTagPath, isEqualTo, getTagValue);
        Reporter.log(MessageFormat.format(
                "In xpath -{0}- where value equals -{1}-, Validating value from -{2}- is not null or empty",
                whereTagPath, isEqualTo, getTagValue));
        logToReport(MessageFormat.format("Validating -{0}- not null or empty", testValue));
        assertThat(testValue, not(equalTo("null")));
        assertThat(testValue, not(isEmptyString()));
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

    // --- RESPONSE'S PATH INFO METHODS --- ////
    /**
     * Returns the value of a path in the response.
     *
     * @param pathForValue string for the path.
     * @return string with the extracted value.
     */
    public String getPathValue(String pathForValue) {
        String testValue = "";
        Integer pathCount = getPathCount(pathForValue);
        if (pathCount == 1) {
            try {
                testValue = requestResponse.then().extract().path(pathForValue);
            } catch (ClassCastException e) {
                log.info("---> Error getting path value");
                testValue = "";
            }
        }
        log.info(MessageFormat.format("---> Returning value -{0}-", testValue));
        return testValue;
    }

    /**
     * Returns the number of times a path is present in the response.
     *
     * @param pathToCount string for the path to count.
     * @return integer with the count.
     */
    public Integer getPathCount(String pathToCount) {
        String originalPath = pathToCount;
        pathToCount = originalPath + ".size()";
        Integer testCount = requestResponse.then().extract().path(pathToCount);
        log.info(MessageFormat.format("---> Counting -{0}-, total count -{1}-", originalPath, testCount));
        return testCount;
    }

    /**
     * Returns the possible different values of a path in the response as a list of strings.
     *
     * @param pathForList string for the path to search for.
     * @return list of strings with the possible values.
     */
    public List<String> getListOfValues(String pathForList) {
        XmlPath responseAsXML = requestResponse.then().extract().xmlPath();
        List<String> listOfValues = responseAsXML.getList(pathForList);
        log.info(MessageFormat.format("---> Getting list of values from -{0}-, list -{1}-", pathForList, listOfValues));
        return listOfValues;
    }

    /**
     * Searches for two different paths and returns its pairings of values as a Map.
     *
     * @param pathForKey string for the path to assign as the Map key.
     * @param pathForValue string for the path to assign as the Map value.
     * @return map string with the key-value pairings.
     */
    public Map<String, String> getMapOfValues(String pathForKey, String pathForValue) {
        XmlPath responseAsXML = requestResponse.then().extract().xmlPath();
        List<String> listOfKeys = responseAsXML.getList(pathForKey);
        log.info(MessageFormat.format("---> Getting list of keys for Map -{0}-, list -{1}-", pathForKey, listOfKeys));
        List<String> listOfValues = responseAsXML.getList(pathForValue);
        log.info(MessageFormat.format("---> Getting list of values for Map from -{0}-, list -{1}-", pathForValue,
                listOfValues));

        Map<String, String> mapOfValues = new HashMap<>();

        for (int i = 0; i < listOfKeys.size(); i++) {
            String key = listOfKeys.get(i);
            String value = listOfValues.get(i);
            mapOfValues.put(key, listOfValues.get(i));
            log.info(MessageFormat.format("---> Putting into map key -{0}-, value -{1}-", key, value));
        }

        return mapOfValues;
    }

    /**
     * Looks for a specific value in all the possible instances of a path in the response and returns the value of a
     * sibling path.
     *
     * @param whereTagPath string for the path to look for a specific value.
     * @param isEqualTo string for the value where searching for.
     * @param getTagValue string for the sibling path to extract the value.
     * @return string with the value in the sibling path.
     */
    public String getValueWhere(String whereTagPath, String isEqualTo, String getTagValue) {
        XmlPath responseAsXML = requestResponse.then().extract().xmlPath();

        String findInPath = whereTagPath.substring(0, whereTagPath.lastIndexOf("."));
        whereTagPath = whereTagPath.replace(findInPath, "");
        getTagValue = getTagValue.replace(findInPath, "");

        findInPath = findInPath + ".find { it" + whereTagPath + "=='" + isEqualTo + "' }" + getTagValue;

        String value = responseAsXML.getString(findInPath);
        log.info(MessageFormat.format("---> Getting from -{0}-, value -{1}-", findInPath, value));
        return value;
    }

    /**
     * Function to log specific conditions needed to meet test criteria.
     *
     * @param result of the criteria that needs to be met.
     * @param verificationMessage text specifying the criteria that was tested.
     */
    public void logResult(Boolean result, String verificationMessage) {
        if (result)
            logToReport(MessageFormat.format("Verified \"{0}\"", verificationMessage));
        else {
            logToReport(MessageFormat.format("Failed verification \"{0}\"", verificationMessage));
            throw new RuntimeException(MessageFormat.format("Failed verification \"{0}\"", verificationMessage));
        }
    }

    private void logToReport(String message) {
        log.info("---> " + message);
        Reporter.log(message + ".");
    }

}
