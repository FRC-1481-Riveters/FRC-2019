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

    protected class LinearizationConstants {
        public double m;
        public double b;
        public double k;

        public LinearizationConstants(double m, double b, double k) {
            this.m = m;
            this.b = b;
            this.k = k;
        }
    }

    private LinearizationConstants m_constants;

    public enum sensorType {
        GP2Y0A51SK0F, GP2Y0A41SK0F
    }

    private Map<sensorType, LinearizationConstants> m_sensorData = new HashMap<>() {{
        put(sensorType.GP2Y0A51SK0F, new LinearizationConstants(6.33, -0.474, 0.3));
        put(sensorType.GP2Y0A41SK0F, new LinearizationConstants(6.33, -0.474, 0.3));
    }};
  

    private LinearizationConstants getConstant(sensorType type) {
        LinearizationConstants constantValues;
        if (m_sensorData.containsKey(type)) {
            constantValues = m_sensorData.get(type);
        } else {
            constantValues = m_sensorData.get(sensorType.GP2Y0A51SK0F);
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
            return (1.0 / ((m_constants.m * voltage) + m_constants.b)) - m_constants.k;
        } catch (ArithmeticException ex) {
            System.out.printf("IRSensor couldn't compute range from voltage %3.2f. Returned 0.0: %s", voltage,
                    ex.toString());
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
