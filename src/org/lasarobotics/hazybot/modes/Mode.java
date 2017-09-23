package org.lasarobotics.hazybot.modes;

import org.json.simple.JSONObject;
import org.lasarobotics.hazybot.ConfigException;

public abstract class Mode {
    /**
     * @throws ConfigException throws so that a ConfigException can be caught
     *                         and logged by main loop if the mode doesn't need to handle it in any
     *                         particular way
     */
    public abstract void teleopPeriodic() throws ConfigException;

    /**
     * called on init and whenever mode options change in the config
     * default implementation discards any options
     *
     * @param options holds mode-specific configuration
     */
    public void updateOptions(JSONObject options) {
    }
}
