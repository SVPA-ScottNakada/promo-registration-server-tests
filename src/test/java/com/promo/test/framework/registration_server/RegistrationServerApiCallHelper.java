
package com.promo.test.framework.registration_server;

import com.promo.test.framework.JsonApiCallHelper;
import com.promo.test.suite.registration_server.RegistrationServerTestData;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author dalvarado
 */
public class RegistrationServerApiCallHelper extends JsonApiCallHelper {

    private Boolean hasHeaderSignature = true;

    private String appKeyForSignature = "";

    public RegistrationServerApiCallHelper() {
        this(RegistrationServerTestData.REGISTRATION_SERVER_BASE_URI);
    }

    public RegistrationServerApiCallHelper(String newUri) {
        super(newUri);
    }

    // --- SEND REQUEST --- ////

    /*
     * (non-Javadoc)
     * @see com.promo.test.framework.JsonApiCallHelper#sendGetRequest(java.lang.Boolean)
     */
    @Override
    protected void sendGetRequest(Boolean redirect) {
        addBodyStringAndHeaderSignature();
        super.sendGetRequest(redirect);
    }

    /*
     * (non-Javadoc)
     * @see com.promo.test.framework.JsonApiCallHelper#sendPostRequest()
     */
    @Override
    protected void sendPostRequest() {
        addBodyStringAndHeaderSignature();
        super.sendPostRequest();
    }

    private void addBodyStringAndHeaderSignature() {
        String bodyString = "";
        
        if (!requestParameterMap.isEmpty()) {
            bodyString = simpleMapToUrlParameterString(requestParameterMap);
        }

        if (!bodyParameterMap.isEmpty()) {
            bodyString = simpleMapToJsonString(bodyParameterMap);
            setBodyParameterString(bodyString);
        }

        if (hasHeaderSignature) {
            String timeStamp = getCurrentTimeAsTimeStamp();
            String targetUrl =
                    "/" + getRequestUri().replace(RegistrationServerTestData.REGISTRATION_SERVER_ENDPOINT, "");
            String signature = generateSignatureForRequest(timeStamp + targetUrl + bodyString);
            addStringAsHeaderParameter("promo-signature", signature);
            addStringAsHeaderParameter("promo-ts", timeStamp);
        }
    }

    // --- SIGNATURE --- //

    /**
     * Generates the signature for the specified string using HmacSHA256.
     *
     * @param stringForSignature candidate string for generating the signature.
     * @return Signature for specified input string.
     */
    private String generateSignatureForRequest(String stringForSignature) {

        if (appKeyForSignature.isEmpty()) {
            log.warn("---> appKeyForSignature is empty");
            return "";
        }

        String signature = null;
        try {
            String secret = appKeyForSignature;
            String message = stringForSignature;

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

    // --- VALIDATIONS --- //

    /**
     * Validates that the response's Status Code value is OK.
     */
    public void validateResponseCodeOk() {
        validateResponseStatusCode(HttpStatus.SC_OK);
    }

    /**
     * Validates the debug code and message in the JSON response.
     *
     * @param expectedCode string for the debug code.
     * @param expectedMessage string for the debug message.
     */
    public void validateDebugError(String expectedCode, String expectedMessage) {
        if (RegistrationServerTestData.REGISTRATION_SERVER_ENVIRONMENT_HAS_DEBUG_ERRORS.equalsIgnoreCase("yes")) {
            validateValue(DEBUG_CODE_PATH, expectedCode);
            validateValue(DEBUG_MESSAGE_PATH, expectedMessage);
        } else {
            validatePathCount(DEBUG_PATH, 0);
        }
    }

    public void log(String message) {
        logToReport(message);
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
     * Specifies if a request needs to send a signature in the header.
     *
     * @param hasSignature
     */
    public void setHasHeaderSignature(Boolean hasSignature) {
        hasHeaderSignature = hasSignature;
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
