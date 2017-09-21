package org.lasarobotics.hazybot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.VictorSP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hardware {
    private static Map<String, List<HazyMotor>> motorMap = new HashMap<>();
    private static Map<String, JSONObject> inputMap = new HashMap<>();
    private static Map<Integer, Joystick> joystickMap = new HashMap<>();

    /**
     * update hardware wrapper to reflect config
     *
     * @param config
     */
    public static void loadConfig(JSONObject config) {
        JSONObject motors = (JSONObject) config.get("motors");
        motors.forEach((k, v) -> {
            String groupName = (String) k;
            JSONArray motorGroup = (JSONArray) v;
            List<HazyMotor> motorList = new ArrayList<>();

            motorGroup.forEach(motor -> {
                JSONObject parameters = (JSONObject) v;
                int port = (int) parameters.get("port");
                boolean reversed = (boolean) parameters.get("reversed");
                double scale = (double) parameters.get("scale");
                boolean enabled = (boolean) parameters.get("enabled");

                motorList.add(new HazyMotor(port, reversed, scale, enabled));
            });

            motorMap.put(groupName, motorList);
        });

        JSONObject inputs = (JSONObject) config.get("inputs");
        inputs.forEach((k, v) -> {
            String inputName = (String) k;
            JSONObject parameters = (JSONObject) v;
            inputMap.put(inputName, parameters);
        });
    }

    // cache joystick so we don't get it every loop
    private static Joystick getJoystick(int port) {
        if (joystickMap.containsKey(port)) {
            return joystickMap.get(port);
        } else {
            Joystick joystick = new Joystick(port);
            joystickMap.put(port, joystick);
            return joystick;
        }
    }

    /**
     * read value (currently assumed to be a double) from named input
     * @param name input name
     * @return input value
     * @throws ConfigException
     */
    public static double getInput(String name) throws ConfigException {
        JSONObject parameters = inputMap.get(name);

        if (parameters == null)
            throw ConfigException.inputUndefined(name);

        int port = (int) parameters.get("port");
        int index = (int) parameters.get("index");
        Joystick joystick = getJoystick(port);

        String type = (String) parameters.get("type");
        switch (type) {
            case "axis":
                return joystick.getRawAxis(index);
            case "button":
                double on = (double) parameters.get("on");
                double off = (double) parameters.get("off");
                return joystick.getRawButton(index) ? on : off;
            default:
                throw ConfigException.invalidInputType(type);
        }
    }

    /**
     * set value (currently assumed to be a double) to named output
     * @param name output name
     * @param value output value
     * @throws ConfigException
     */
    public static void setOutput(String name, double value) throws ConfigException {
        List<HazyMotor> motorGroup = motorMap.get(name);
        if (motorGroup == null)
            throw ConfigException.motorUndefined(name);

        motorGroup.forEach(motor -> motor.setValue(value));
    }

    private static class HazyMotor {
        VictorSP victor;
        double scale;
        boolean enabled;

        public HazyMotor(int port, boolean reversed, double scale, boolean enabled) {
            victor = new VictorSP(port);
            victor.setInverted(reversed);
            this.scale = scale;
            this.enabled = enabled;
        }

        public void setValue(double value) {
            if (enabled) {
                victor.set(scale * value);
            }
        }
    }
}
