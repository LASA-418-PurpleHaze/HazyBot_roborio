package org.lasarobotics.hazybot.modes;

import org.json.simple.JSONObject;
import org.lasarobotics.hazybot.ConfigException;

import java.util.HashMap;
import java.util.Map;

public abstract class Mode {
    private static Map<String, Class<? extends Mode>> modes = new HashMap<>();
    private static Mode currentMode;

    /**
     * register Mode with name to be used in config
     *
     * @param modeName mode name
     * @param mode     mode class
     */
    public static final void registerMode(String modeName, Class<? extends Mode> mode) {
        modes.put(modeName, mode);
    }

    /**
     * update current Mode if necessary
     *
     * @param modeName
     * @throws ConfigException
     */
    public static final void setMode(String modeName) throws ConfigException {
        if (!modes.containsKey(modeName))
            throw ConfigException.undefinedMode(modeName);

        Class<? extends Mode> modeClass = modes.get(modeName);

        // initialize mode if first time or mode changed
        if (currentMode == null || modeClass != currentMode.getClass()) {
            try {
                currentMode = modeClass.newInstance();
            } catch (Exception e) {
                /* just log error and continue with current mode so we don't
                   crash the whole program with a typo in the mode name */
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * get currently active Mode instance (e.g., to call teleopPeriodic)
     *
     * @return
     */
    public static Mode getMode() {
        return currentMode;
    }

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
     * @param config holds mode-specific configuration
     */
    public void config(JSONObject config) {
    }
}
