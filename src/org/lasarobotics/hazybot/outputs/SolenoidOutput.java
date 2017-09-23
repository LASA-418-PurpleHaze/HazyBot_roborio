package org.lasarobotics.hazybot.outputs;

import edu.wpi.first.wpilibj.Solenoid;
import org.json.simple.JSONObject;
import org.lasarobotics.hazybot.ConfigException;

import java.util.ArrayList;
import java.util.List;

// takes Double so it's easily interoperable with the Inputs
public class SolenoidOutput extends Output<Double> {
    private static List<Integer> activePorts = new ArrayList<>();
    private Solenoid solenoid;
    private int port;

    public void config(JSONObject config) throws ConfigException {
        int port = (int) config.get("port");

        // ensure solenoid not already in use
        if (activePorts.contains(port)) {
            System.err.println("duplicate solenoids not allowed");
            throw ConfigException.invalidConfigOption("port", port);
        }

        this.port = port;
        solenoid = new Solenoid(port);
    }

    @Override
    public void free() {
        solenoid.free();
        activePorts.remove(port);
    }

    public void setOutput(Double value) {
        /* enable if input is positive, more compatible with current Inputs
           than taking a boolean */
        boolean on = value > 0;
        solenoid.set(on);
    }
}
