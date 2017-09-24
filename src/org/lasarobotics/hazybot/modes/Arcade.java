package org.lasarobotics.hazybot.modes;

import org.lasarobotics.hazybot.ConfigException;
import org.lasarobotics.hazybot.Hardware;

public class Arcade extends Mode {
    public void teleopPeriodic() throws ConfigException {
        double throttle_power = Hardware.getInput("throttle");
        double wheel_power = Hardware.getInput("wheel");

        double left_power = throttle_power + wheel_power;
        double right_power = throttle_power - wheel_power;

        Hardware.setOutput("left", left_power);
        Hardware.setOutput("right", right_power);
    }
}
