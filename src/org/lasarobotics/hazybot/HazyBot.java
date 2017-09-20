package org.lasarobotics.hazybot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.VictorSP;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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

        //placeholder for when we decide json structure
        Mode jsonMode;
        for (Object motorName : motors.keySet()) {
            JSONObject motor = (JSONObject) motors.get(motorName);

            if (jsonMode == Mode.SINGLE_BUTTON) {
                motorList.add(HazyMotor.fromButton(joysticklist.get((Integer) motor.get("joystick")),
                                    (Integer) motor.get("port"),
                                    (Integer) motor.get("button")));
                                        
            } else if (jsonMode == Mode.SINGLE_AXIS) {
                motorList.add(HazyMotor.fromAxis(joysticklist.get((Integer) motor.get("joystick")),
                                    (Integer) motor.get("port"),
                                    (Integer) motor.get("axis")));
            }
        }
    }

    @Override
    public void teleopPeriodic() {
        for (HazyMotor m : motorList) {
            m.update();
        }
    }
}

enum Mode {
    SINGLE_BUTTON,
    SINGLE_AXIS,
    MULTI,
    MECANUM
}

class HazyMotor {

    Mode mode;

    Joystick m_joystick1;
    Joystick m_joystick2;
    // list of buttons that can be used as input
    ArrayList<Integer> buttons;
    ArrayList<Integer> axes;

    VictorSP victor;

    // probably a good idea to make one general constructor which can be used in static initializers
    /*
    public HazyMotor(Joystick j, int button, int port) {
        victor = new VictorSP(port);
    }
    */

    //
    public HazyMotor(Mode m, Joystick j1, Joystick j2, int port, ArrayList<Integer> buttons, ArrayList<Integer> axes) {
        mode = m;
        victor = new VictorSP(port);
        m_joystick1 = j1;
        if (m_joystick2 != null) {
            m_joystick2 = j2;
        }
        this.buttons = buttons;
        this.axes = axes;

    }

    // for use in single-input, do not use if you need multiple inputs
    public static HazyMotor fromButton(Joystick j, int port, int button) {
        return new HazyMotor(Mode.SINGLE_BUTTON, j, null, port, new ArrayList<Integer>(button), null);
    }

    // for use in single-input, do not use if you need multiple inputs
    public static HazyMotor fromAxis(Joystick j, int port, int axis) {
        return new HazyMotor(Mode.SINGLE_AXIS, j, null, port, null, new ArrayList<Integer>(axis));
    }

    public void update() {
        if (this.mode == Mode.SINGLE_BUTTON) {
            if (m_joystick1.getRawButton(buttons.get(0))) {
                victor.set(1.0);
            } else {
                victor.set(0.0);
            }
        } else if (this.mode == Mode.SINGLE_AXIS) {
            victor.setSpeed(m_joystick1.getRawAxis(axes.get(0)));
        }
    }
}
