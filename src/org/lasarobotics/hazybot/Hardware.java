package org.lasarobotics.hazybot;

import org.json.simple.JSONObject;
import org.lasarobotics.hazybot.inputs.Input;
import org.lasarobotics.hazybot.outputs.Output;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hardware {
    private static Map<String, Input> inputs = new HashMap<>();
    private static Map<String, Output> outputs = new HashMap<>();

    /**
     * list of active output ports to avoid duplication
     */
    public static List<Integer> activePorts;

    /**
     * update Hardware wrapper to reflect config
     *
     * @param inputConfigs
     * @param outputConfigs
     * @throws ConfigException
     */
    static void config(JSONObject inputConfigs, JSONObject outputConfigs) throws ConfigException {
        for (Object n : inputConfigs.keySet()) {
            String inputName = (String) n;
            JSONObject inputConfig = (JSONObject) inputConfigs.get(inputName);

            // update Input config if it already exists
            if (inputs.containsKey(inputName)) {
                Input input = inputs.get(inputName);
                input.config(inputConfig);
            } else {
                inputs.put(inputName, Input.fromConfig(inputConfig));
            }
        }

        // free newly unused Inputs
        for (String inputName : inputs.keySet()) {
            if (!inputConfigs.containsKey(inputName)) {
                inputs.get(inputName).free();
            }
        }

        for (Object n : outputConfigs.keySet()) {
            String outputName = (String) n;
            JSONObject outputConfig = (JSONObject) outputConfigs.get(outputName);

            // update Output config if it already exists
            if (outputs.containsKey(outputName)) {
                Output output = outputs.get(outputName);
                output.config(outputConfig);
            } else {
                outputs.put(outputName, Output.fromConfig(outputConfig));
            }
        }

        // free newly unused Outputs
        for (String outputName : outputs.keySet()) {
            if (!outputConfigs.containsKey(outputName)) {
                outputs.get(outputName).free();
            }
        }
    }

    /**
     * get value of named Input
     *
     * @param name Input name
     * @return Input value
     * @throws ConfigException
     */
    public static double getInput(String name) throws ConfigException {
        if (!inputs.containsKey(name))
            throw ConfigException.undefinedInput(name);

        Input input = inputs.get(name);
        return input.getInput();
    }

    /**
     * set value of named Output
     *
     * @param name  Output name
     * @param value Output value
     * @throws ConfigException
     */
    public static void setOutput(String name, double value) throws ConfigException {
        if (!outputs.containsKey(name))
            throw ConfigException.undefinedOutput(name);

        Output output = outputs.get(name);
        output.setOutput(value);
    }
}
