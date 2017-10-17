package org.lasarobotics.lib.controlloop;



public class HazyPID extends ControlLoop {

    double kP, kI, kD, kFF;
    double error, errorSum;

    public HazyPID() {

    }

    /**
     * Set new PID constants.
     *
     * @param kP Proportional constant.
     * @param kI Integral constant.
     * @param kD Derivative constant.
     * @param kFF Feed forward constant.
     * @param doneBound "close enough" range to decide if on target or not.
     */
    public void setPID(double kP, double kI, double kD, double kFF, double doneBound) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kFF = kFF;
        this.targetRange = doneBound;
    }

    /**
     * Calculate the voltage that needs to be sent to the motor.
     *
     * @param currentValue The current value of the variable we are trying to
     * control. i.e. the current position of an arm or RPM of a shooter.
     * @param dt The time since the last time we called this method.
     * @return The output to be set to the motor.
     */
    public double calculate(double currentValue, double dt) {
        //Update count to decide if done or not.
        doneCount = (Math.abs(targetValue - previousValue) < targetRange) ? doneCount + 1 : 0;

        //Update error and errorSum. If statements ensure the following:
        //minU <= errorSum * kI <= maxU
        error = targetValue - currentValue;
        if (maxU >= kI * (errorSum + error * dt) && minU <= kI * (errorSum + error * dt)) {
            errorSum += error * dt;
        } else if (errorSum > 0) {
            errorSum = maxU / kI;
        } else {
            errorSum = minU / kI;
        }

        //Can't calculate rate of change with one data point.
        if (!firstCycle) {
            currentU = kP * error + kI * errorSum - (kD * (currentValue - previousValue) / dt) + kFF * targetValue;
        } else {
            firstCycle = false;
            currentU = kP * error + kI * errorSum + kFF * targetValue;
        }

        //Limit output.
        if (currentU > maxU) {
            currentU = maxU;
        } else if (currentU < minU) {
            currentU = minU;
        }

        //Store current sensor value for next calculation.
        previousValue = currentValue;

        return currentU;
    }

    /**
     * Reset the PID controller. Does not reset its operating parameters.
     */
    @Override
    public void reset() {
        error = 0;
        errorSum = 0;
        previousValue = 0;
        targetValue = 0;
        firstCycle = true;
    }
}
