package org.lasarobotics.hazybot.outputs;

import org.json.simple.JSONObject;
import org.lasarobotics.hazybot.ConfigException;
import org.lasarobotics.hazybot.JSONObjectWrapper;

import java.util.HashMap;
import java.util.Map;

public abstract class Output {
    private static Map<String, Class<? extends Output>> outputTypes = new HashMap<>();

    /**
     * register new Output subclass to a name so it can be used in config
     *
     * @param name       name used in config
     * @param outputType subclass of Output
     */
    public static final void registerOutputType(String name, Class<? extends Output> outputType) {
        outputTypes.put(name, outputType);
    }

    /**
     * create new Output corresponding to config section
     *
     * @param config
     * @return
     * @throws ConfigException
     */
    public static final Output fromConfig(JSONObject config) throws ConfigException {
        String type = (String) config.get("type");
        if (!outputTypes.containsKey(type))
            throw ConfigException.invalidOutputType(type);
        config.remove(type);

        try {
            Output output = outputTypes.get(type).newInstance();
            output.config(new JSONObjectWrapper(config));
            return output;
        } catch (Exception e) {
            System.err.println("WARNING: Output couldn't be created. " +
                    "Using NullOutput. The erroneous config entry was:");
            System.err.println(config);
            System.err.println(e.getMessage());

            return new NullOutput();
        }
    }

    /**
     * update Output's state to reflect new config
     *
     * @param config
     */
    public abstract void config(JSONObjectWrapper config) throws ConfigException;

    /**
     * free an Output's resources
     */
    public void free() {
    }

    /**
     * set value of Output (e.g., set motor speed)
     *
     * @param value
     */
    public abstract void setOutput(double value);
}
