package org.lasarobotics.lib.controlloop;

public class HazyPVIff extends ControlLoop {

    private double kP, kI, kV, kFFV, kFFA;
    private double positionError, postitionErrorSum;

    public HazyPVIff() {
    }

    /**
     * Set new PID constants.
     *
     * @param kP Proportional constant for position.
     * @param kI Integral constant.
     * @param kV Proportional constant for velocity.
     * @param kFFV Feedforward constant for velocity.
     * @param kFFA Feedforward constant for acceleration.
     * @param doneBound "close enough" range to decide if on target or not.
     */
    public void setPID(double kP, double kI, double kV, double kFFV, double kFFA, double doneBound) {
        this.kP = kP;
        this.kI = kI;
        this.kV = kV;
        this.kFFV = kFFV;
        this.kFFA = kFFA;
        this.targetRange = doneBound;
    }

    /**
     * Calculate the voltage that needs to be sent to the motor.
     *
     * @param currentPosition The current sensor measured position.
     * @param currentVelocity The current sensor measured velocity.
     * @param targetPosition The current target position. When paired with a
     * motion profile, this is NOT the final goal position.
     * @param targetVelocity The current target velocity.
     * @param targetAcceleration The current target acceleration.
     * @param dt The time since this method was last called.
     * @return
     */
    public double calculate(double currentPosition, double currentVelocity,
            double targetPosition, double targetVelocity, double targetAcceleration, double dt) {

        //Update count to decide if done or not.
        doneCount = (Math.abs(targetValue - previousValue) < targetRange) ? doneCount + 1 : 0;

        //Update error and errorSum. If statements ensure the following:
        //minU <= errorSum * kI <= maxU
        positionError = targetPosition - currentPosition;
        if (maxU >= kI * (postitionErrorSum + positionError * dt) && minU <= kI * (postitionErrorSum + positionError * dt)) {
            postitionErrorSum += positionError * dt;
        } else if (postitionErrorSum > 0) {
            postitionErrorSum = maxU / kI;
        } else {
            postitionErrorSum = minU / kI;
        }

        currentU = kP * positionError + kI * postitionErrorSum + kV * (targetVelocity - currentVelocity) + kFFV * targetVelocity + kFFA * targetAcceleration;

        //Limit output.
        if (currentU > maxU) {
            currentU = maxU;
        } else if (currentU < minU) {
            currentU = minU;
        }

        //Store current sensor value for next calculation.
        previousValue = currentPosition;

        return currentU;
    }

    @Override
    public void reset() {
        positionError = 0;
        postitionErrorSum = 0;
        previousValue = 0;
        doneCount = 0;
        targetValue = 0;
    }

}
