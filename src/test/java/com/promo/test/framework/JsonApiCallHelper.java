
package com.promo.test.framework;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.exception.JsonPathException;

public class JsonApiCallHelper extends BaseApiCallHelper {

    private JsonPath responseInJsonPath = null;

    public JsonApiCallHelper(String newUri) {
        super(newUri);
        RestAssured.defaultParser = Parser.JSON;
        setContentType("application/json");
    }

    // --- SEND REQUEST --- ////

    /*
     * (non-Javadoc)
     * @see com.biv.test.framework.BaseApiCallHelper#sendGetRequest(java.lang.Boolean)
     */
    @Override
    protected void sendGetRequest(Boolean followRedirects) {
        super.sendGetRequest(followRedirects);
        setResponseAsJsonPath();
    }

    /*
     * (non-Javadoc)
     * @see com.biv.test.framework.BaseApiCallHelper#sendPostRequest()
     */
    @Override
    protected void sendPostRequest() {
        super.sendPostRequest();
        setResponseAsJsonPath();
    }

    private void setResponseAsJsonPath() {
        // The content type could have been changed, so we only extract if its still Json
        if (getContentType().equals("application/json")) {
            try {
                responseInJsonPath = new JsonPath(response.then().extract().jsonPath().prettify());
            } catch (JsonPathException e) {
                log.warn("---> Response is not JSON");
            }
        }
    }

    // --- VALIDATIONS --- ////

    // TODO: Rework functions that use responseInJsonPath.getList, currently works for what we need, but if
    // the response is not in the correct format it can throw casting exceptions

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

    // --- MAP TO JSON STRING--- ////

    protected String simpleMapToJsonString(Map<String, Object> mapToConvert) {
        Map<String, Object> mapWithQuotes = new LinkedHashMap<String, Object>();

        for (Map.Entry<String, Object> mapEntry : mapToConvert.entrySet()) {
            String key = "\"" + mapEntry.getKey() + "\"";
            String value = mapEntry.getValue().toString();
            if (mapEntry.getValue().getClass().getTypeName().contains("String")) {
                value = "\"" + value + "\"";
            }
            mapWithQuotes.put(key, value);
        }
        return simpleMapToString(mapWithQuotes, "{", ": ", ",", "}");
    }

}
