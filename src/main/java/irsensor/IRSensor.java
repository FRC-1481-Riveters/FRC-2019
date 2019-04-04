package irsensor;

import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import java.util.HashMap;
import java.util.Map;

public class IRSensor extends SendableBase implements PIDSource {

  private AnalogInput m_anaInput;

  protected class SensorConstants {
    /*
     * These are the linearization constants that will fit the sensor's distance
     * data to the inverse of a 3rd order polynomial to support the sensor's
     * transfer function of the form Distance in cm of Voltage in volts:
     * 
     * Distance(Voltage) = 1 / (a*Voltage^3 + b*Voltage^2 + c*voltage + d)
     * 
     * Why is this an *inverted* polynomial? Well, that's because the sensor's
     * nature is to generate a reciprocal relationship between the distance measure
     * and the sensor's output voltage. This inverted relationship further fits a
     * cubic function really well.
     */
    public double a; // a * x^3 +
    public double b; // b * x^2 +
    public double c; // c * x +
    public double d; // d

    public SensorConstants(double a, double b, double c, double d) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;

    }
  }

  private SensorConstants m_constants;

  public enum sensorType {
    GP2Y0A51SK0F, GP2Y0A41SK0F, Unknown
  }

  /*
   * Setup a database of constants for different IR sensors. These sensors all
   * have similar transfer functions that relate the sensor's output voltage into
   * distance.
   */
  private Map<sensorType, SensorConstants> m_sensorData = new HashMap<>() {
    {/*                                                     a             b              c            d */
      put(sensorType.GP2Y0A51SK0F, new SensorConstants(0.09978106474, -0.2796664032, 0.4242351842, -0.06078793247));
      put(sensorType.GP2Y0A41SK0F, new SensorConstants(0.007868008148, -0.02931278423, 0.1183284175, -0.01775494828));
      put(sensorType.Unknown, new SensorConstants(0.0, 0.0, 1.0, 0.0));
    }
  };

  private SensorConstants getConstant(sensorType type) {
    
    SensorConstants constantValues;
    
    if (m_sensorData.containsKey(type)) {
      constantValues = m_sensorData.get(type);
    } else {
      constantValues = m_sensorData.get(sensorType.Unknown);
    }

    return constantValues;
  }

  public IRSensor(int analogChannel, sensorType type) {

    m_constants = getConstant(type);
    m_anaInput = new AnalogInput(analogChannel);
    setName("IRSensor_" + type.toString(), analogChannel);
  }

  public double getRawVoltage() {

    return m_anaInput.getVoltage();
  }

  /**
   * Set which parameter of the device you are using as a process control
   * variable.
   *
   * @param pidSource An enum to select the parameter.
   */
  public void setPIDSourceType(PIDSourceType pidSource) {

  }

  /**
   * Get which parameter of the device you are using as a process control
   * variable.
   *
   * @return the currently selected PID source parameter
   */
  public PIDSourceType getPIDSourceType() {
    return PIDSourceType.kDisplacement;
  }

  /**
   * Get the result to use in PIDController.
   *
   * @return the result to use in PIDController
   */
  public double pidGet() {
    return getRangeCm();
  }

  public double getRawCounts() {
    return m_anaInput.getValue();
  }

  public double getRangeCm() {
    double voltage = getRawVoltage();

    try {
      /* Compute the distance estimate for this sensor's voltage by running it through the following transfer function:
       * 
       * Distance(Voltage) = 1 / (a*Voltage^3 + b*Voltage^2 + c*Voltage + d)
       */
      return 1.0 / ((m_constants.a * Math.pow(voltage, 3.0)) + (m_constants.b * Math.pow(voltage, 2.0))
          + (m_constants.c * voltage) + m_constants.d);
    } catch (ArithmeticException ex) {
      System.out.printf("IRSensor couldn't compute range from voltage %3.2f. Returned 0.0: %s", voltage, ex.toString());
    }

    return 0.0;
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.setSmartDashboardType("IRSensor");
    builder.addDoubleProperty("Range (cm)", this::getRangeCm, null);
    builder.addDoubleProperty("RawVolts", this::getRawVoltage, null);
    builder.addDoubleProperty("RawCounts", this::getRawCounts, null);
  }

}
