package irsensor;

import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class IRSensor extends SendableBase implements PIDSource {

    private AnalogInput m_anaInput;
    private sensorType m_type;

    public enum sensorType {
        GP2Y0A51SK0F
    }

    public IRSensor(int analogChannel) {

        m_type = sensorType.GP2Y0A51SK0F;
        m_anaInput = new AnalogInput(analogChannel);
    }

    public IRSensor(int analogChannel, sensorType type) {

        m_type = type;
        m_anaInput = new AnalogInput(analogChannel);
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
        return getRangeInches();
    }

    public double getRawCounts() {
        return m_anaInput.getValue();
    }

    @Deprecated
    public void setSensorType(sensorType type) {
        m_type = type;
    }

    @Deprecated
    public double getRangeInches() {
        return 0.0;
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("IRSensor");
        builder.addDoubleProperty("Value", this::getRangeInches, null);
        builder.addDoubleProperty("RawVolts", this::getRawVoltage, null);
        builder.addDoubleProperty("RawCounts", this::getRawCounts, null);
    }

}
