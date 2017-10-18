package org.lasarobotics.hazybot.inputs;

import edu.wpi.first.wpilibj.Joystick;
import org.json.simple.JSONObject;
import org.lasarobotics.hazybot.ConfigException;
import org.lasarobotics.hazybot.JSONObjectWrapper;

import java.util.HashMap;
import java.util.Map;

public class JoystickInput {
    private static Map<Integer, Joystick> joysticks = new HashMap<>();

    // get existing Joystick if previously allocated
    private static Joystick getJoystick(int port) {
        if (joysticks.containsKey(port)) {
            return joysticks.get(port);
        } else {
            return new Joystick(port);
        }
    }

    public static class Axis extends Input {
        private Joystick joystick;
        private int axis;
        private double deadband;

        @Override
        public void config(JSONObjectWrapper config) throws ConfigException {
            int port = config.getInt("port");
            joystick = getJoystick(port);
            deadband = config.getDoubleOrDefault("deadband", 0);
        }

        public double getInput() {
            // return value if outside deadband
            double value = joystick.getRawAxis(axis);
            return value > deadband ? value : 0;
        }
    }
    public static class Button extends Input {
        private Joystick joystick;
        private int button;
        private double on, off;
        private boolean toggle;

        // button must be pressed for this many cycles to toggle
        private boolean wasPressed = false;
        private boolean toggled = false;

        @Override
        public void config(JSONObjectWrapper config) throws ConfigException {
            int port = config.getInt("port");
            button = config.getInt("button");
            on = config.getDoubleOrDefault("on", 1);
            off = config.getDoubleOrDefault("off", 0);
            toggle = config.getBooleanOrDefault("toggle", false);
        }

        public double getInput() {
            boolean pressed = joystick.getRawButton(button);
            if (toggle) {
                if (pressed != wasPressed) {
                    toggled = !toggled;
                }
                return toggled ? on : off;
            } else {
                return pressed ? on : off;
            }
        }
    }
}
