package org.lasarobotics.hazybot;

import edu.wpi.first.wpilibj.IterativeRobot;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.lasarobotics.hazybot.inputs.Input;
import org.lasarobotics.hazybot.inputs.JoystickInput;
import org.lasarobotics.hazybot.modes.Mecanum;
import org.lasarobotics.hazybot.modes.Mode;
import org.lasarobotics.hazybot.outputs.GroupOutput;
import org.lasarobotics.hazybot.outputs.MotorOutput;
import org.lasarobotics.hazybot.outputs.Output;
import org.lasarobotics.hazybot.outputs.SolenoidOutput;

/**
 * IterativeRobot that can read from a config file (and watch for changes)
 * and automatically configure motors, inputs, and mode options
 */
public class ConfigurableRobot extends IterativeRobot {
    private final static Path configFilepath = Paths.get("config file location");
    WatchKey watchKey;
    Mode mode;

    /* probably a better place to but this, but idk what classes the roboRIO/cRIO loads,
    so I can't trust static blocks in the other classes to execute */
    static {
        Mode.registerMode("Mecanum", Mecanum.class);

        Input.registerInputType("joystick_axis", JoystickInput.Axis.class);
        Input.registerInputType("joystick_button", JoystickInput.Button.class);

        Output.registerOutputType("group", GroupOutput.class);
        Output.registerOutputType("motor", MotorOutput.class);
        Output.registerOutputType("solenoid", SolenoidOutput.class);

    }


    @Override
    public void robotInit() {
        try {
            // load initial config file
            updateConfig();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        // watch config file for modifications
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            watchKey = configFilepath.register(watchService, ENTRY_MODIFY, ENTRY_CREATE);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    // so we don't poll filesystem on every cycle
    int loopModulus = 0;
    int pollPeriod = 50;

    @Override
    public void teleopPeriodic() {
        // poll for config files changes every pollPeriod loops
        loopModulus = (loopModulus + 1) % pollPeriod;
        if (loopModulus == 0 && didConfigChange()) {
            try {
                updateConfig();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        try {
            mode.teleopPeriodic();
        } catch (ConfigException e) {
            // log and ignore ConfigException if not already handled by the mode
            System.err.println(e.getMessage());
        }
    }


    private JSONObject readConfig() throws IOException, ParseException {
        return (JSONObject) JSONValue.parseWithException(
                new FileReader(configFilepath.toFile())
        );
    }

    private void updateConfig() throws ConfigException {
        JSONObject config;
        try {
            config = readConfig();
        } catch (Exception e) {
            /* just log error and continue if config can't be read, so robot
               doesn't crash completely if we accidentally delete config or something */
            System.err.println(e.getMessage());
            return;
        }

        // update Mode if necessary
        JSONObject modeConfig = (JSONObject) config.get("mode");
        String modeName = (String) modeConfig.get("name");
        modeConfig.remove(modeName);
        Mode.setMode(modeName);
        Mode.getMode().config(modeConfig);

        // update Hardware config
        JSONObject inputConfigs = (JSONObject) config.get("inputs");
        JSONObject outputConfigs = (JSONObject) config.get("outputs");
        Hardware.config(inputConfigs, outputConfigs);
    }

    /**
     * polls filesystem to determine if config file changed
     *
     * @return true if config changed, false if not
     */
    private boolean didConfigChange() {
        for (WatchEvent<?> ev : watchKey.pollEvents()) {
            WatchEvent.Kind<?> kind = ev.kind();

            // check if config modified or recreated
            if (kind == ENTRY_MODIFY || kind == ENTRY_CREATE) {
                WatchEvent<Path> event = (WatchEvent<Path>) ev;
                Path filepath = event.context();

                // can't watch individual files, so check if actually the config
                return filepath.equals(configFilepath);
            }
        }
        return false;
    }
}

