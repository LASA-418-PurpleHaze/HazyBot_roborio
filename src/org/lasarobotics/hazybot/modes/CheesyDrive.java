package org.lasarobotics.hazybot.modes;

import org.json.simple.JSONObject;
import org.lasarobotics.hazybot.ConfigException;
import org.lasarobotics.hazybot.Hardware;
import org.lasarobotics.hazybot.JSONObjectWrapper;

public class CheesyDrive extends Mode {

    private double leftPwm, rightPwm, oldWheel, quickStopAccumulator;
    int quick_turn = 0;
    double config_sensitivity = 0.0;

    public void config(JSONObjectWrapper config) throws ConfigException {
        quick_turn = config.getInt("quick_turn");
        config_sensitivity = config.getDouble("sensitivity");
    }

    public void teleopPeriodic() throws ConfigException {
        double throttle_power = Hardware.getInput("throttle");
        double wheel_power = Hardware.getInput("wheel");

        // need to add parameter for quickturn
        cheesyDrive(throttle_power, wheel_power);
        
        Hardware.setOutput("left", leftPwm);
        Hardware.setOutput("right", rightPwm);
    }

    private void cheesyDrive(double throttle, double wheel) {

        double wheelNonLinearity;

        double negInertia = wheel - oldWheel;
        oldWheel = wheel;

        wheelNonLinearity = 0.6;
        // Apply a sin function that's scaled to make it feel better.
        wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel)
                / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
        wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel)
                / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
        wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel)
                / Math.sin(Math.PI / 2.0 * wheelNonLinearity);

        double overPower;
        double sensitivity;

        double angularPower;
        double linearPower;

        // Negative inertia!
        double negInertiaAccumulator = 0.0;
        double negInertiaScalar;
        negInertiaScalar = 4.0;   
        sensitivity = config_sensitivity;

        double negInertiaPower = negInertia * negInertiaScalar;
        negInertiaAccumulator += negInertiaPower;

        wheel = wheel + negInertiaAccumulator;
        if (negInertiaAccumulator > 1) {
            negInertiaAccumulator -= 1;
        } else if (negInertiaAccumulator < -1) {
            negInertiaAccumulator += 1;
        } else {
            negInertiaAccumulator = 0;
        }
        linearPower = throttle;

        // Quickturn!
        if (quick_turn != 0) {
            if (Math.abs(linearPower) < 0.2) {
                double alpha = 0.1;
                quickStopAccumulator = (1 - alpha) * quickStopAccumulator
                        + alpha * limit(wheel, 1.0) * 5;
            }
            overPower = 1.0;
            sensitivity = 1.0;
            angularPower = wheel;
        } else {
            overPower = 0.0;
            angularPower = Math.abs(throttle) * wheel * sensitivity
                    - quickStopAccumulator;
            if (quickStopAccumulator > 1) {
                quickStopAccumulator -= 1;
            } else if (quickStopAccumulator < -1) {
                quickStopAccumulator += 1;
            } else {
                quickStopAccumulator = 0.0;
            }
        }

        rightPwm = leftPwm = linearPower;
        leftPwm += angularPower;
        rightPwm -= angularPower;

        if (leftPwm > 1.0) {
            rightPwm -= overPower * (leftPwm - 1.0);
            leftPwm = 1.0;
        } else if (rightPwm > 1.0) {
            leftPwm -= overPower * (rightPwm - 1.0);
            rightPwm = 1.0;
        } else if (leftPwm < -1.0) {
            rightPwm += overPower * (-1.0 - leftPwm);
            leftPwm = -1.0;
        } else if (rightPwm < -1.0) {
            leftPwm += overPower * (-1.0 - rightPwm);
            rightPwm = -1.0;
        }
    }

    private static double limit(double v, double limit) {
        return (Math.abs(v) < limit) ? v : limit * (v < 0 ? -1 : 1);
    }
}
