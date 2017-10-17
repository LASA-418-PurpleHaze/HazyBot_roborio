package org.lasarobotics.lib;

import edu.wpi.first.wpilibj.Joystick;
        

public class HazyJoystick {
    
    Joystick controller;
    double deadband;
    
    public HazyJoystick(int joystickPort, double deadband){
        
        controller = new Joystick(joystickPort);
        this.deadband = deadband;
    }
    
    private double handleDeadband(double input){
        
        if (Math.abs(input) > deadband){
            
            if(input > 0){
                return (input - deadband)/(1 - deadband);  
            } else {
                return (input + deadband)/(1 - deadband);
            }
            
        } else {
            return 0.0;
        }
    }
    
    public double getXAxis(){
        return handleDeadband(controller.getRawAxis(0));
    }
    
    public double getYAxis(){
        return handleDeadband(controller.getRawAxis(1));
    }
    
    public double getZAxis(){
        return handleDeadband(controller.getRawAxis(2));
    }
    
    public boolean getTrigger(){
        return controller.getRawButton(1);
    }
    
    public boolean getTopBackButton(){
        return controller.getRawButton(2);
    }
    
    public boolean getTopFrontButton(){
        return controller.getRawButton(3);
    }
    
    public boolean getTopleftButton(){
        return controller.getRawButton(4);
    }
    
    public boolean getTopRightButton(){
        return controller.getRawButton(5);
    }
    
    public boolean getLeftFrontButton(){
        return controller.getRawButton(6);
    }
    
    public boolean getLeftBackButton(){
        return controller.getRawButton(7);
    }
    
    public boolean getBackLeftButton(){
        return controller.getRawButton(8);
    }
    
    public boolean getBackRightButton(){
        return controller.getRawButton(9);
    }
    
    public boolean getRightBackButton(){
        return controller.getRawButton(10);
    }
    
    public boolean getRightFrontButton(){
        return controller.getRawButton(11);
    }

    public boolean getRawButton(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
