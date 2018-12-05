
package com.promo.test.framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.text.MessageFormat;
import java.util.Properties;

public class ConfigUtils {

    protected Logger log = LogManager.getLogger(getClass());

    private static final String CONFIG_FILE = "config.properties";

    private Properties systemProps = null;

    private Properties configProps = null;

    private static class SingletonHolder {

        private static final ConfigUtils INSTANCE = new ConfigUtils();
    }

    public static ConfigUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Loads configurations from the specified configuration file into the local Properties object.
     * 
     * @param configFile Configuration file to load
     * @return
     */
    public Properties loadConfiguration(String configFile) {

        log.debug("--- loadConfiguration() ---");

        if (configProps == null) {
            configProps = new Properties();

            try {

                // --- Load the configuration file into the Properties object ---

                log.debug("loadConfiguration(): Loading configurations from " + configFile);

                InputStream cfs = new FileInputStream(configFile);
                configProps.load(cfs);

            } catch (IOException e) {
                log.error(MessageFormat.format("Failed to find configuration properties file \"{0}\"", CONFIG_FILE));
                e.printStackTrace();
            }
        }

        return configProps;
    }

    /**
     * Retrieves the config file from passed in System property;
     * Defaults to 'config.properties' if not specified.
     * 
     * @return Path to the configuration file
     */
    private String getConfigFilePath() {
        return System.getProperty("config", CONFIG_FILE);
    }

    /**
     * Returns the value for the specified configuration key.
     * The value is first read from environment if available;
     * Otherwise, it defaults to the value specified in the configuration file.
     * 
     * @param key Configuration key to lookup.
     * @return Value for the specified configuration key.
     */
    public String getValueFromEnvOrConfigFile(String key) {

        // Handle invalid input
        if ((key == null) || (key.trim().length() == 0)) {
            throw new InvalidParameterException("Invalid/Empty key specified!");
        }

        // Instantiate a local copy of the system properties
        if (systemProps == null) {
            systemProps = System.getProperties();
        }

        // Read the value for the configuration key from system properties
        String keyValue = systemProps.getProperty(key);

        // --- Proceed to lookup the value from the configuration file, if not found in system properties ---

        if (keyValue == null) {

            // Instantiate and load configuration properties, if not done already.
            if (configProps == null) {
                String configFile = getConfigFilePath();
                loadConfiguration(configFile);
            }

            keyValue = configProps.getProperty(key);

            if (null == keyValue) {
                log.error("getValueFromEnvOrConfigFile() - couldnt get value for key: " + key);
            } else {
                if (keyValue.isEmpty()) {
                    log.warn("getValueFromEnvOrConfigFile() - empty key: " + key);
                }
            }
        }

        log.debug("getValueFromEnvOrConfigFile() - key: " + key + "; value: " + keyValue);

        return keyValue;
    }
}
