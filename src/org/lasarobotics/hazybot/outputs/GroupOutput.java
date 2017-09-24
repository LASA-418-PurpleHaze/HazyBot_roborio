package org.lasarobotics.hazybot.outputs;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.lasarobotics.hazybot.ConfigException;

import java.util.HashMap;

public class GroupOutput extends Output {
    /* map configs to outputs so duplicates are avoided and no unnecessary
       work is done when config is updated */
    private HashMap<JSONObject, Output> outputs = new HashMap<>();

    public void config(JSONObject config) throws ConfigException {
        JSONArray outputArray = (JSONArray) config.get("outputs");
        HashMap<JSONObject, Output> newOutputs = new HashMap<>();

        for (Object o : outputArray) {
            JSONObject outputConfig = (JSONObject) o;
            // reuse old Output if config unchanged
            if (outputs.containsKey(outputConfig)) {
                newOutputs.put(outputConfig, outputs.get(outputConfig));
            } else {
                newOutputs.put(outputConfig, Output.fromConfig(outputConfig));
            }
        }

        // free newly unused Outputs
        for (JSONObject outputConfig : outputs.keySet()) {
            if (!newOutputs.containsKey(outputConfig))
                outputs.get(outputConfig).free();
        }

        outputs = newOutputs;
    }

    public void setOutput(double value) {
        for (Output output : outputs.values()) {
            output.setOutput(value);
        }
    }
}
