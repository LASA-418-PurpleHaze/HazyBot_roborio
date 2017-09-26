<img src="http://www.robosquad.org/files/9113/5405/6341/lasa_hires.png" width="300" title="We would like to haze with you." />

# HazyBot_roborio

Provides ConfigurableRobot, a subclass of IterativeRobot that is configurable via a JSON file that is watched for changes and reloaded live.

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
    // get mode option
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
