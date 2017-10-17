package org.lasarobotics.lib.controlloop;

public abstract class ControlLoop {

    double minU, maxU, currentU;
    double targetValue, previousValue;
    boolean firstCycle;
    double targetRange;
    int doneCount, minCount;
    
    public ControlLoop() {
        this.maxU = 1;
        this.minU = -1;
        this.minCount = 5;
        this.firstCycle = true;
    }
    
    /**
     * Set the minimum consecutive cycles to be on target for in order for 
     * onTarget() to return true.
     *
     * @param newcount The new minimum count.
     */
    public void setMinCount(int newcount) {
        minCount = newcount;
    }
    
    /**
     * Check if the control loop is on target. i.e. an arm is in the right spot,
     * or a flywheel is at the right RPM.
     *
     * @return True if on target, false if not.
     */
    public boolean onTarget() {
        return doneCount >= minCount;
    }
    
    /**
     * Set new motor output values.
     *
     * @param maxU New maximum output.
     * @param minU New negative maximum output.
     */
    public void setMaxMin(double maxU, double minU) {
        this.maxU = maxU;
        this.minU = minU;
    }
    
    /**
     * Set the desired position or speed.
     *
     * @param target The desired target.
     */
    public void setTarget(double target) {
        targetValue = target;
    }

    /**
     * @return The current target value the pid is trying to get to.
     */
    public double getTarget() {
        return targetValue;
    }
    
    /**
     * Force implementation of a proper reset for safety reasons.
     */
    public abstract void reset();
    
    /**
     * Utility function to scale motor output such that -1 = -12V and +1 = 12V.
     * For example, passing in 0.5 as the output will return a scaled value such
     * that 6V is applied to the motor, regardless of battery state.
     * 
     * @param output The desired output on a -1 to 1 scale.
     * @param batteryVoltage The current voltage of the battery
     * @return Corrected motor output.
     */
    public static double scaleOutputTo12V(double output, double batteryVoltage) {
        return output * 12 / batteryVoltage;
    }
}
