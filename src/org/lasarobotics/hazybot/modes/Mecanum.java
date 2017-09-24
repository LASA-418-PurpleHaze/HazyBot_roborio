package org.lasarobotics.hazybot.modes;

import org.lasarobotics.hazybot.ConfigException;
import org.lasarobotics.hazybot.Hardware;

public class Mecanum extends Mode {
    public void teleopPeriodic() throws ConfigException {
        double drive_power = Hardware.getInput("drive");
        double strafe_power = Hardware.getInput("strafe");
        double turn_power = Hardware.getInput("turn");

        double left_front_power = drive_power + turn_power + strafe_power;
        double left_back_power = drive_power + turn_power - strafe_power;
        double right_front_power = drive_power - turn_power + strafe_power;
        double right_back_power = drive_power - turn_power - strafe_power;

        Hardware.setOutput("left_front", left_front_power);
        Hardware.setOutput("left_back", left_back_power);
        Hardware.setOutput("right_front", right_front_power);
        Hardware.setOutput("right_back", right_back_power);
    }
}
