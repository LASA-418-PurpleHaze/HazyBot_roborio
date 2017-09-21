package org.lasarobotics.hazybot;

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
     * one of the mode's required motors wasn't defined in the config
     *
     * @param motorName
     * @return
     */
    public static ConfigException motorUndefined(String motorName) {
        return new ConfigException(String.format("motor group %s undefined", motorName));
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
}
