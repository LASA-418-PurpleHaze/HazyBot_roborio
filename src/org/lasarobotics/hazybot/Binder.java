package org.lasarobotics.hazybot;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
* Binds inputs to outputs as specified in the "bind" section of json
*
*/
public class Binder {

    private static Map<String, String> inputOutputMap = new HashMap<>();

    public static void setMap(Map<String, String> map) {

        inputOutputMap = map;
    }

    public static void teleopPeriodic() {
        for (String outputName : inputOutputMap.keySet()) {
            try {
                Hardware.setOutput(outputName, Hardware.getInput(inputOutputMap.get(outputName)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}