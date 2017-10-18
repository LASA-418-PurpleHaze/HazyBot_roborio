package org.lasarobotics.hazybot.inputs;

import org.json.simple.JSONObject;
import org.lasarobotics.hazybot.ConfigException;
import org.lasarobotics.hazybot.JSONObjectWrapper;

import java.util.HashMap;
import java.util.Map;

public abstract class Input {
    private static Map<String, Class<? extends Input>> inputTypes = new HashMap<>();

    /**
     * register new Input subclass to a name so it can be used in config
     *
     * @param name      name used in config
     * @param inputType subclass of Input
     */
    public static final void registerInputType(String name, Class<? extends Input> inputType) {
        inputTypes.put(name, inputType);
    }

    /**
     * create new Input corresponding to config section
     *
     * @param config
     * @return
     * @throws ConfigException
     */
    public static final Input fromConfig(JSONObject config) throws ConfigException {
        String type = (String) config.get("type");
        if (!inputTypes.containsKey(type))
            throw ConfigException.undefinedInputType(type);
        config.remove(type);

        try {
            Input input = inputTypes.get(type).newInstance();
            input.config(new JSONObjectWrapper(config));
            return input;
        } catch (Exception e) {
            System.err.println("WARNING: Input couldn't be created. " +
                    "Using ConstInput. The erroneous config entry was:");
            System.err.println(config);
            System.err.println(e.getMessage());

            // return ConstInput that always returns 0
            Input constInput = new ConstInput();
            JSONObject constInputConfig = new JSONObject();
            constInputConfig.put("value", 0);
            constInput.config(new JSONObjectWrapper(constInputConfig));
            return constInput;
        }
    }

    /**
     * update Input's state to reflect new config
     *
     * @param config
     */
    public void config(JSONObjectWrapper config) throws ConfigException {
    }

    /**
     * free an Input's resources
     */
    public void free() {
    }

    /**
     * get value of Input (e.g., get joystick position)
     *
     * @return
     */
    public abstract double getInput();
}
