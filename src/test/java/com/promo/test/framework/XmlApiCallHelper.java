
package com.promo.test.framework;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import org.apache.http.HttpStatus;
import org.testng.Reporter;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.exception.XmlPathException;

public class XmlApiCallHelper extends BaseApiCallHelper {

    private XmlPath responseAsXML = null;

    public XmlApiCallHelper(String newUri) {
        super(newUri);
        RestAssured.defaultParser = Parser.XML;
        setContentType("application/xml");
    }
    // --- SEND REQUEST --- ////

    /*
     * (non-Javadoc)
     * @see com.biv.test.framework.BaseRequestHelper#sendGetRequest(java.lang.Boolean)
     */
    @Override
    protected void sendGetRequest(Boolean followRedirects) {
        super.sendGetRequest(followRedirects);
        setResponseAsXmlPath();
    }

    /*
     * (non-Javadoc)
     * @see com.biv.test.framework.BaseRequestHelper#sendPostRequest()
     */
    @Override
    protected void sendPostRequest() {
        super.sendPostRequest();
        setResponseAsXmlPath();
    }

    /**
     * Takes the response and if it's in XML saves it in responseAsXML for future use.
     */
    private void setResponseAsXmlPath() {

        // The content type could have been changed, so we only extract if its still XML
        if (getContentType().equals("application/xml")) {
            try {
                responseAsXML = response.then().extract().xmlPath();
            } catch (XmlPathException e) {

                log.warn("---> Response is not XML");

            }
        }
    }

    // --- VALIDATIONS --- ////

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
     * @param expectedStatusCode integer for the response's status code.
     */
    public void validateValue(String pathToValidate, String expectedValue, Integer expectedStatusCode) {
        String actualValue = response.then().extract().path(pathToValidate).toString();
        String actualStatusCode = String.valueOf(response.then().extract().statusCode());
        logToReport(MessageFormat.format("Validating -{0}-, expected value -{1}-, actual value -{2}-", pathToValidate,
                expectedValue, actualValue));
        if (expectedStatusCode != HttpStatus.SC_OK) {
            logToReport(
                    MessageFormat.format("Validating response Status Code, expected value -{0}-, actual value -{1}-",
                            expectedStatusCode, actualStatusCode));
        }
        // @formatter:off
        response.then()
                .assertThat()
                    .statusCode(expectedStatusCode)
                        .body(pathToValidate, equalTo(expectedValue));
        // @formatter:on
    }

    /**
     * Validates that a path contains an expected value in the response.
     *
     * @param pathToValidate string for the path to validate.
     * @param expectedValue string for the value to verify.
     */
    public void validateValueContains(String pathToValidate, String expectedValue) {
        String actualValue = response.then().extract().path(pathToValidate).toString();
        logToReport(MessageFormat.format("Validating -{0}-, actual value -{1}- contains -{2}- ", pathToValidate,
                actualValue, expectedValue));

        assertThat(actualValue, containsString(expectedValue));
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
        String actualValue = response.then()
                                .assertThat()
                                    .statusCode(HttpStatus.SC_OK)
                                    .body(pathToValidate, notNullValue())
                                .extract()
                                    .path(pathToValidate).toString();
        // @formatter:on
        logToReport(MessageFormat.format("path value is -{0}-", actualValue));

        // When a path would have to return a null value the extract() function instead returns the string "null"
        assertThat(actualValue, not(equalTo("null")));
        assertThat(actualValue, not(isEmptyString()));
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
        String actualValue = response.then()
                                .assertThat()
                                    .statusCode(HttpStatus.SC_OK)
                                    .body(pathToValidate, notNullValue())
                                .extract()
                                    .path(pathToValidate).toString();
        // @formatter:on
        logToReport(MessageFormat.format("path value is -{0}-", actualValue));

        // When a path would have to return a null value the extract() function instead returns the string "null"
        assertThat(actualValue, isEmptyString());
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
        pathToCount = pathToCount + ".size()";
        String actualCount = response.then().extract().path(pathToCount).toString();
        logToReport(MessageFormat.format("Validating -{0}-, expected count -{1}-, actual count -{2}-", originalPath,
                expectedCount, actualCount));
        // @formatter:off
        response.then()
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
        String actualValue = getPathValue(pathToValidate);
        logToReport(MessageFormat.format("Validating -{0}- lenght between -{1}- and -{2}-, lenght is -{3}-",
                actualValue, minValue, maxValue, actualValue.length()));
        assertThat(actualValue.length(), greaterThanOrEqualTo(minValue));
        assertThat(actualValue.length(), lessThanOrEqualTo(maxValue));
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
        pathToCount = pathToCount + ".size()";
        Integer actualCount = response.then().extract().path(pathToCount);
        logToReport(MessageFormat.format("Validating -{0}- count between -{1}- and -{2}-, count is -{3}-", originalPath,
                minValue, maxValue, actualCount));
        assertThat(actualCount, greaterThanOrEqualTo(minValue));
        assertThat(actualCount, lessThanOrEqualTo(maxValue));
    }

    /**
     * Validates that the number of times a path is present in a response is greater or equal than a given value.
     *
     * @param pathToCount string for the path to validate.
     * @param valueToCompare integer for the minimum value to expect.
     */
    public void validateCountGreaterThanOrEqualTo(String pathToCount, Integer valueToCompare) {
        String originalPath = pathToCount;
        pathToCount = pathToCount + ".size()";
        Integer actualCount = response.then().extract().path(pathToCount);
        logToReport(MessageFormat.format("Validating -{0}- Greater than or equal to -{1}-, count is -{2}-",
                originalPath, valueToCompare, actualCount));
        assertThat(actualCount, greaterThanOrEqualTo(valueToCompare));
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
        String actualValue = getValueWhere(whereTagPath, isEqualTo, getTagValue);
        Reporter.log(MessageFormat.format("In xpath -{0}- where value equals -{1}-, Validating value from -{2}-",
                whereTagPath, isEqualTo, getTagValue));
        validateStringsAreEqual(actualValue, expectedValue);
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
        String actualValue = getValueWhere(whereTagPath, isEqualTo, getTagValue);
        Reporter.log(MessageFormat.format(
                "In xpath -{0}- where value equals -{1}-, Validating value from -{2}- is not null or empty",
                whereTagPath, isEqualTo, getTagValue));
        logToReport(MessageFormat.format("Validating -{0}- not null or empty", actualValue));
        assertThat(actualValue, not(equalTo("null")));
        assertThat(actualValue, not(isEmptyString()));
    }

    // --- RESPONSE'S PATH INFO METHODS --- ////
    /**
     * Returns the value of a path in the response.
     *
     * @param pathForValue string for the path.
     * @return string with the extracted value.
     */
    public String getPathValue(String pathForValue) {
        String actualValue = "";
        Integer pathCount = getPathCount(pathForValue);
        if (pathCount == 1) {
            try {
                actualValue = response.then().extract().path(pathForValue);
            } catch (ClassCastException e) {
                log.info("---> Error getting path value");
                actualValue = "";
            }
        }
        log.info(MessageFormat.format("---> Returning value -{0}-", actualValue));
        return actualValue;
    }

    /**
     * Returns the number of times a path is present in the response.
     *
     * @param pathToCount string for the path to count.
     * @return integer with the count.
     */
    public Integer getPathCount(String pathToCount) {
        String originalPath = pathToCount;
        pathToCount = pathToCount + ".size()";
        Integer actualCount = response.then().extract().path(pathToCount);
        log.info(MessageFormat.format("---> Counting -{0}-, total count -{1}-", originalPath, actualCount));
        return actualCount;
    }

    /**
     * Returns the possible different values of a path in the response as a list of strings.
     *
     * @param pathForList string for the path to search for.
     * @return list of strings with the possible values.
     */
    public List<String> getListOfValues(String pathForList) {
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
        String findInPath = whereTagPath.substring(0, whereTagPath.lastIndexOf("."));
        whereTagPath = whereTagPath.replace(findInPath, "");
        getTagValue = getTagValue.replace(findInPath, "");

        findInPath = findInPath + ".find { it" + whereTagPath + "=='" + isEqualTo + "' }" + getTagValue;

        String value = responseAsXML.getString(findInPath);
        log.info(MessageFormat.format("---> Getting from -{0}-, value -{1}-", findInPath, value));
        return value;
    }

    /**
     * Looks for a specific value in all the possible instances of a path in the response and returns the values of a
     * sibling path as a list of strings.
     *
     * @param whereTagPath string for the path to look for a specific value.
     * @param isEqualTo string for the value where searching for.
     * @param getTagValue string for the sibling path to extract the value.
     * @return list of strings with the possible values in the sibling path.
     */
    public List<String> getListOfValuesWhere(String whereTagPath, String isEqualTo, String getTagValue) {
        String findInPath = whereTagPath.substring(0, whereTagPath.lastIndexOf("."));
        whereTagPath = whereTagPath.replace(findInPath, "");
        getTagValue = getTagValue.replace(findInPath, "");

        findInPath = findInPath + ".find { it" + whereTagPath + "=='" + isEqualTo + "' }" + getTagValue;

        List<String> listOfValues = responseAsXML.getList(findInPath);
        log.info(MessageFormat.format("---> Getting list of values from -{0}-, list -{1}-", findInPath, listOfValues));
        return listOfValues;
    }

}
