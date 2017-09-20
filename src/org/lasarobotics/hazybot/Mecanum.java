package org.lasarobotics.hazybot;

import org.json.simple.JSONObject;

public class Mecanum implements Mode {
    Hardware hardware;

    public Mecanum(Hardware hardware, JSONObject options) {
        this.hardware = hardware;
    }
    public void teleopPeriodic() {
        double drive_power = hardware.getInput("drive");
        double strafe_power = hardware.getInput("strafe");
        double turn_power = hardware.getInput("turn");

        double left_front_power = drive_power + turn_power + strafe_power;
        double left_back_power = drive_power + turn_power - strafe_power;
        double right_front_power = drive_power - turn_power + strafe_power;
        double right_back_power = drive_power - turn_power - strafe_power;

        hardware.setOutput("left_front", left_front_power);
        hardware.setOutput("left_back", left_back_power);
        hardware.setOutput("right_front", right_front_power);
        hardware.setOutput("right_back", right_back_power);
    }
}
