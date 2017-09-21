package org.lasarobotics.hazybot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.VictorSP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class HazyBot extends IterativeRobot {
    static String configFilepath = "idkwheretoputityet";
    Mode mode;

    @Override
    public void robotInit() {
        JSONObject robotConfig = null;

        try {
            robotConfig = (JSONObject) JSONValue.parseWithException(new FileReader(new File(configFilepath)));
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }

        String modeName = (String) robotConfig.get("mode");
        Hardware hardware = new Hardware(robotConfig);
        JSONObject options = (JSONObject) robotConfig.get("options");

        switch (modeName) {
            case "mecanum":
               mode = new Mecanum(hardware, options);
               break;
            default:
                ConfigException.invalidMode(modeName);
        }
    }

    @Override
    public void teleopPeriodic() {
        mode.teleopPeriodic();
    }
}

interface Mode {
    void teleopPeriodic();
}

class ConfigException extends Exception {
    String message;
    public ConfigException(String message) {
        this.message = message;
    }
    @Override
    public String getMessage() {
        return message;
    }
    public static ConfigException invalidMode(String modeName) {
        return new ConfigException(String.format("invalid mode % ", modeName));
    }
    public static ConfigException invalidInputType(String type) {
        return new ConfigException(String.format("invalid input type %s", type));
    }
    public static ConfigException motorUndefined(String motorName) {
        return new ConfigException(String.format("motor group %s undefined", motorName));
    }
    public static ConfigException inputUndefined(String inputName) {
        return new ConfigException(String.format("input %s undefined", inputName));
    }
}

class Hardware {
    private Map<String, List<HazyMotor>> motorMap = new HashMap<>();
    private Map<String, JSONObject> inputMap = new HashMap<>();
    // cache joystick so we don't get it every loop
    private Map<Integer, Joystick> joystickMap = new HashMap<>();

    public Hardware(JSONObject config) {
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
                motorList.add(new HazyMotor(port, reversed, scale));
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

    private Joystick getJoystick(int port) {
        if (joystickMap.containsKey(port)) {
            return joystickMap.get(port);
        } else {
            Joystick joystick = new Joystick(port);
            joystickMap.put(port, joystick);
            return joystick;
        }
    }

    public double getInput(String name) throws ConfigException {
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

    public void setOutput(String name, double value) throws ConfigException {
        List<HazyMotor> motorGroup = motorMap.get(name);
        if (motorGroup == null)
            throw ConfigException.motorUndefined(name);

        motorGroup.forEach(motor -> motor.setValue(value));
    }
}

class HazyMotor {
    VictorSP victor;
    double scale;

    public HazyMotor(int port, boolean reversed, double scale) {
        victor = new VictorSP(port);
        victor.setInverted(reversed);
        this.scale = scale;
    }
    public void setValue(double value) {
        victor.set(scale * value);
    }
}
