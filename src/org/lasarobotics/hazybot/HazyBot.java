package org.lasarobotics.hazybot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.VictorSP;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class HazyBot extends IterativeRobot {

    static String configFilepath = "idkwheretoputityet";
    List<HazyMotor> motorList;
    List<Joystick> joysticklist;
    
    @Override
    public void robotInit() {
        JSONObject robotConfig = null;
        
        try {
            robotConfig = (JSONObject) JSONValue.parseWithException(new FileReader(new File(configFilepath)));
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } catch (ParseException ex) {
        }

        JSONObject motors = (JSONObject) robotConfig.get("motors");
        for (Object motorName : motors.keySet()) {
            JSONObject motor = (JSONObject) motors.get(motorName);
            motorList.add(new HazyMotor(joysticklist.get((Integer) motor.get("joystick")),
                                        (Integer) motor.get("button"), 
                                        (Integer) motor.get("port")));
        }
    }

    @Override
    public void teleopPeriodic() {
        for (HazyMotor m : motorList) {
            m.update();
        }
    }
}

class HazyMotor {
    
    Joystick m_joystick;
    int button;
    VictorSP victor;
    
    public HazyMotor(Joystick j, int button, int port) {
        victor = new VictorSP(port);
    }
    
    public void update() {
        if (m_joystick.getRawButton(button)) {
            victor.set(1.0);
        } else {
            victor.set(0.0);
        }
    }
}