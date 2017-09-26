package org.lasarobotics.hazybot.modes;

import org.json.simple.JSONObject;
import org.lasarobotics.hazybot.ConfigException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;

public abstract class Mode {
    private static Map<String, Class<? extends Mode>> modes = new HashMap<>();
    private static List<Mode> currentModes;

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
     * Iterate through and setup modes
     * Once iteration is done, mode config method is run.
     *
     * @param modeName
     * @throws ConfigException
     */
    public static final void setAndConfigModes(JSONArray modeList) throws ConfigException {
        for(int i = 0; i < modeList.size(); i++) {
            JSONObject modeObject = (JSONObject) modeList.get(i);
            String modeName = (String) modeObject.get("name");
            if (!modes.containsKey(modeName))
                throw ConfigException.undefinedMode(modeName);

          // initialize mode if first time or mode changed
          Class<? extends Mode> modeClass = modes.get(modeName);
          // check if mode is already in currentModes
          boolean skip = false;
          for(int j = 0; j < currentModes.size(); j++) {
              skip = modeClass == currentModes.get(i).getClass();
          }
          if (!skip) {
            try {
                Mode currentMode = modeClass.newInstance();
                currentModes.add(currentMode);
                currentMode.config(modeObject);
            } catch (Exception e) {
                /* just log error and continue with current mode so we don't
                   crash the whole program with a typo in the mode name */
                System.err.println(e.getMessage());
            }
        }
        }
    }

    /**
     * get currently active Mode instance (e.g., to call teleopPeriodic)
     *
     * @return
     */
//    public static Mode getMode() {
//        return currentMode;
//    }

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
    public void config(JSONObject config) throws ConfigException {
    }
    
    public static void teleopPeriodicAll() throws ConfigException {
        for(int i = 0; i < currentModes.size(); i++) {
            currentModes.get(i).teleopPeriodic();
        }
    }
}
