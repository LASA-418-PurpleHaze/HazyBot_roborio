package org.lasarobotics.hazybot;

import org.json.simple.JSONObject;

public class JSONObjectWrapper extends JSONObject {
    public JSONObjectWrapper(JSONObject jsonObject) {
        super(jsonObject);
    }
    public int getInt(String name) {
        return Integer.parseInt(this.get(name).toString());
    }
    public int getIntOrDefault(String name, int def) {
        return this.containsKey(name) ? getInt(name) : def;
    }

    public double getDouble(String name) {
        return Double.parseDouble(this.get(name).toString());
    }
    public double getDoubleOrDefault(String name, double def) {
        return this.containsKey(name) ? getDouble(name) : def;
    }

    public boolean getBoolean(String name) {
        return Boolean.parseBoolean(this.get(name).toString());
    }
    public boolean getBooleanOrDefault(String name, boolean def) {
        return this.containsKey(name) ? getBoolean(name) : def;
    }
}
