package org.lasarobotics.hazybot;

import org.lasarobotics.hazybot.inputs.InputID;

public class ConfigException extends Exception {
    String message;

    public ConfigException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * attempt to load an unregistered mode
     *
     * @param modeName
     * @return
     */
    public static ConfigException invalidMode(String modeName) {
        return new ConfigException(String.format("invalid mode % ", modeName));
    }

    /**
     * input type (currently joystick axis or button) unrecognized
     *
     * @param type
     * @return
     */
    public static ConfigException invalidInputType(String type) {
        return new ConfigException(String.format("invalid input type %s", type));
    }

    /**
     * input type (currently joystick axis or button) unrecognized
     *
     * @param type
     * @return
     */
    public static ConfigException invalidOutputType(String type) {
        return new ConfigException(String.format("invalid output type %s", type));
    }

    /**
     * one of the mode's required inputs wasn't defined in the config
     *
     * @param inputName
     * @return
     */
    public static ConfigException inputUndefined(String inputName) {
        return new ConfigException(String.format("input %s undefined", inputName));
    }

    /**
     * one of the mode's required outputs wasn't defined in the config
     *
     * @param outputName
     * @return
     */
    public static ConfigException outputUndefined(String outputName) {
        return new ConfigException(String.format("output %s undefined", outputName));
    }

    /**
     * one of the config fields has an invalid value
     *
     * @param key
     * @param value
     * @return
     */
    public static ConfigException invalidConfigOption(String key, Object value) {
        return new ConfigException(String.format("bad config option %s: %s", key, value.toString()));
    }
}
