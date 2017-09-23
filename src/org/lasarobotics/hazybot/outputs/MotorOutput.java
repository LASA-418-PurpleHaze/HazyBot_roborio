package org.lasarobotics.hazybot.outputs;

import edu.wpi.first.wpilibj.VictorSP;
import org.json.simple.JSONObject;
import org.lasarobotics.hazybot.ConfigException;
import org.lasarobotics.hazybot.Hardware;

public class MotorOutput extends Output<Double> {
    private VictorSP victorSP;
    private int port;
    private boolean enabled;
    private double scale;

    public void config(JSONObject config) throws ConfigException {
        int port = (int) config.get("port");

        // ensure motor not already in use
        if (Hardware.activePorts.contains(port)) {
            System.err.println("duplicate motors not allowed");
            throw ConfigException.invalidConfigOption("port", port);
        }

        this.port = port;
        victorSP = new VictorSP(port);
        enabled = (boolean) config.getOrDefault("enabled", true);
        scale = (double) config.getOrDefault("scale", 1);
        victorSP.setInverted((boolean) config.getOrDefault("reversed", false));
    }

    @Override
    public void free() {
        victorSP.free();
        Hardware.activePorts.remove(port);
    }

    public void setOutput(Double value) {
        if (enabled)
            victorSP.set(scale * value);
    }
}
