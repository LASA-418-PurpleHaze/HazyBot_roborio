# HazyBot_roborio
Provides ConfigurableRobot, a subclass of IterativeRobot that is configurable via JSON.

### Hardware support
* input from joystick (axis or button)
* output to solenoid
* output to victor

### Basic Usage
config.json:
```json
{
  "inputs": {
    "example_joystick": {
      "type": "joystick_axis",
      "port": 0,
      "axis": 0
    }
  },
  "outputs": {
    "example_motor": {
      "type": "motor",
      "port": 0
    }
  },
  "mode": {
    "name": "example_mode",
    "example_mode_option": 42
  }
}
```
code:
```java
public class ExampleMode extends Mode {
  int example_mode_option;
  public void config(JSONObject config) throws ConfigException {
    example_mode_option = (int) config.get("example_mode_option");
  }
  public void teleopPeriodic() throws ConfigException {
    // get input
    double joystick_x = (double) Hardware.getInput("example_joystick");
    // set output
    Hardware.setOutput("example_motor", joystick_x);
  }
}
...
// somewhere in your entry point
Mode.registerMode("example_mode", ExampleMode.class);
```
