/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.robot.Robot;

import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.*;
import java.util.ArrayList;
import java.util.List;
import frc.robot.subsystems.HazmatIndicators;
import frc.robot.subsystems.HazmatIndicators.Color;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.EntryListenerFlags;

public class Hazmat_Arm extends Subsystem {
  private static WPI_TalonSRX m_hazmat_arm_talon = new WPI_TalonSRX(RobotMap.hazmatArm_Talon);

  public enum stepDirection {
    stepUp, stepDown
  }

  private enum armControlType {
    percent, position
  }

  private int m_targetPosition;
  private int m_currentArmCommandIndex;

  private List<Command> hazmatCommands = new ArrayList<>();

  protected class Command {
    public int EncoderCounts;
    public double driveStrength;
    public armControlType controlType;
    public String CommandNameString;
    public HazmatIndicators.Color color;

    /*
     * Use this constructor to create a Hazmat position command. This position
     * command uses an encoder reference value to drive the arm based on the encoder
     * feedback.
     */
    public Command(String HazmatPositionWord, HazmatIndicators.Color color, int HazmatPosition) {
      EncoderCounts = HazmatPosition;
      CommandNameString = HazmatPositionWord;
      this.color = color;
      this.driveStrength = 0.0;
      this.controlType = armControlType.position;
    }

    /*
     * Use this constructor to create a hazmat fixed drive strength command. This
     * drive command will drive the motor with a constant PWM value, which induces a
     * constant current, which causes constant torque on the arm. The arm will move
     * until it hits its mechanical stop. This is useful for pinning the arm against
     * its top or bottom mechanical stops during e.g. defense play.
     */
    public Command(String HazmatPositionWord, HazmatIndicators.Color color, double driveStrength) {
      EncoderCounts = 0;
      CommandNameString = HazmatPositionWord;
      this.color = color;
      this.driveStrength = driveStrength;
      this.controlType = armControlType.percent;
    }
  }

  private enum PerformanceLevel {
    calibration, full, unknown
  }

  private PerformanceLevel m_currentLevel = PerformanceLevel.unknown;

  private NetworkTableEntry hazmatArmMotor_Kp;
  private NetworkTableEntry hazmatArmMotor_Ki;
  private NetworkTableEntry hazmatArmMotor_Kd;
  private NetworkTableEntry hazmatArmMotor_Kf;

  private NetworkTableEntry hazmatArmMotor_MaxAccel;
  private NetworkTableEntry hazmatArmMotor_MaxVel;

  public Hazmat_Arm() {

    /* Configure the close loop gains for slot 0 */
    m_hazmat_arm_talon.selectProfileSlot(0, 0);

    NetworkTable smartDashNetworkTable = NetworkTableInstance.getDefault().getTable("SmartDashboard");

    /*
     * Get a reference to the four PID calibrations we're using for the Hazmat arm
     * so we can capture and detect changes to these values when they change on the
     * dashboard.
     */
    hazmatArmMotor_Kp = smartDashNetworkTable.getEntry("HazmatArmMotorKp");
    hazmatArmMotor_Ki = smartDashNetworkTable.getEntry("HazmatArmMotorKi");
    hazmatArmMotor_Kd = smartDashNetworkTable.getEntry("HazmatArmMotorKd");
    hazmatArmMotor_Kf = smartDashNetworkTable.getEntry("HazmatArmMotorKf");

    /* Set these NetworkTable signals to their initial values.. */
    hazmatArmMotor_Kp.setDouble(6.0);
    hazmatArmMotor_Ki.setDouble(0.0);
    hazmatArmMotor_Kd.setDouble(0.0);
    hazmatArmMotor_Kf.setDouble(0.0);

    /* ... and read these initial values to set them in the Talon's PID. */
    m_hazmat_arm_talon.config_kP(0, hazmatArmMotor_Kp.getDouble(6.0));
    m_hazmat_arm_talon.config_kI(0, hazmatArmMotor_Ki.getDouble(0.0));
    m_hazmat_arm_talon.config_kD(0, hazmatArmMotor_Kd.getDouble(0.0));
    m_hazmat_arm_talon.config_kF(0, hazmatArmMotor_Kf.getDouble(0.0));

    /*
     * Add listeners that will update the Talon's PID's configuration, in the
     * background, when the signal changes over the NetworkTable, e.g. if a client
     * (like a computer) updates the value during e.g. tuning.
     * 
     * These listeners run in the background and sleep until a change is made. Then
     * they wake-up, execute the code (to update the Talon's PID's configuration)
     * then go back to sleep until the value changes again.
     */

    hazmatArmMotor_Kp.addListener(event -> {
      m_hazmat_arm_talon.config_kP(0, SmartDashboard.getNumber("HazmatArmMotorKp", 6.0));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    hazmatArmMotor_Ki.addListener(event -> {
      m_hazmat_arm_talon.config_kI(0, SmartDashboard.getNumber("HazmatArmMotorKi", 0.0));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    hazmatArmMotor_Kd.addListener(event -> {
      m_hazmat_arm_talon.config_kD(0, SmartDashboard.getNumber("HazmatArmMotorKd", 0.0));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    hazmatArmMotor_Kf.addListener(event -> {
      m_hazmat_arm_talon.config_kF(0, SmartDashboard.getNumber("HazmatArmMotorKf", 0.0));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    hazmatArmMotor_MaxAccel = smartDashNetworkTable.getEntry("HazmatArmMotorMaxAccel");
    hazmatArmMotor_MaxVel = smartDashNetworkTable.getEntry("HazmatArmMotorMaxVel");

    hazmatArmMotor_MaxAccel.setDouble(250.0);
    hazmatArmMotor_MaxVel.setDouble(400.0);

    /*
     * This is the maximum velocity of the arm in units of encoder counts per 100 ms
     * (a decisecond)
     * 
     * The entire range of motion is about 1760 encoder counts. Design for
     * traversing this in 1.2 seconds
     * 
     * That's 1760 counts / 0.45 seconds * 1 second / 10 deciseconds = 400 counts /
     * decisecond
     * 
     */
    m_hazmat_arm_talon.configMotionCruiseVelocity((int) hazmatArmMotor_MaxVel.getDouble(400.0));

    /*
     * This is the maximum acceleration of the arm in units of encoder counts per
     * 100 ms (a decisecond)
     * 
     */
    m_hazmat_arm_talon.configMotionAcceleration((int) hazmatArmMotor_MaxAccel.getDouble(250.0));

    hazmatArmMotor_MaxAccel.addListener(event -> {
      m_hazmat_arm_talon.configMotionAcceleration((int) hazmatArmMotor_MaxAccel.getDouble(250));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    hazmatArmMotor_MaxVel.addListener(event -> {
      m_hazmat_arm_talon.configMotionCruiseVelocity((int) hazmatArmMotor_MaxVel.getDouble(400.0));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    m_hazmat_arm_talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

    /* Feedback device of remote talon */
    m_hazmat_arm_talon.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor0);
    /* Quadrature Encoder of current Talon */
    m_hazmat_arm_talon.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.CTRE_MagEncoder_Relative);
    m_hazmat_arm_talon.configNominalOutputForward(0.0);
    m_hazmat_arm_talon.configNominalOutputReverse(0.0);
    m_hazmat_arm_talon.setSensorPhase(true);
    m_hazmat_arm_talon.setInverted(false);

    /*
     * Initialize the arm's performance to a safe output that can't damage itself if
     * it resets in the wrong position and doesn't know where its zero position is.
     * When the arm figures out where it is, the system will increase the arm's
     * speed to maximum performance.
     */
    setPerformanceLevel(PerformanceLevel.full);

    m_targetPosition = getActualPosition();

    /*
     * It's important to add the positions in the order they'll appear on the
     * robot's tap up/tap down list. For instance, the first item add()'d' will be
     * the first position on the tap up/tap down list and should at the bottom of
     * the arm's travel. The last element will be the last item on the list, and
     * should at the top of the arm's travel.
     */
    hazmatCommands.add(
        new Command("Defense low constant hold", Color.blue_flashing, RobotMap.hazmatLowConstantHoldDriveStrength));
    hazmatCommands.add(new Command("hazmatPodLoadStart", Color.blue, RobotMap.hazmatPodLoadStart));
    hazmatCommands.add(new Command("hazmatPodIntake", Color.lime, RobotMap.hazmatPodIntake));
    hazmatCommands.add(new Command("hazmatHatchBottom", Color.red, RobotMap.hazmatHatchBottom));
    hazmatCommands.add(new Command("hazmatRocket1Pod", Color.purple, RobotMap.hazmatRocket1Pod));
    hazmatCommands.add(new Command("hazmatRocket2Hatch", Color.white, RobotMap.hazmatRocket2Hatch));
    hazmatCommands.add(new Command("hazmatRocket2Pod", Color.green, RobotMap.hazmatRocket2Pod));

    /*
     * Start the hazmat arm at the hazMat Pod Load Start, which is the lowest target
     * set position, and likely to be where the robot will start from.
     */
    m_currentArmCommandIndex = 1;
  }

  public void stepToNextIndexedPosition(stepDirection direction) {

    /*
     * Check if the system is running in full performance mode. If it isn't, don't
     * allow the tap-up/tap-down functionality, only tolerate jogging, which use
     * small relative movements.
     */
    if (getPerformanceLevel() != PerformanceLevel.full) {
      System.out.println("HazmatArm: Rejected stepping while in low performance mode.");
      return;
    }

    /*
     * Determine the next command we should execute. Just go, in order, up or down
     * the hazmatCommands list, figure out what each command requires and have the
     * arm perform that command.
     * 
     * Remember which command we're currently executing by storing the index of the
     * command from the hazmatCommands List in the m_currentArmCommandIndex. This is
     * just an integer that represents a place in the hazmatCommands List (Lists are
     * like improved arrays).
     * 
     * If this is a Tap-down event, decrement the m_currentArmCommandIndex if we're
     * above 0. If we're *at* zero don't go any lower (because there's no more
     * commands below zero; that's the beginning of the list).
     * 
     * If this is a tap-up event, increment the m_currentArmCommandIndex if we're
     * below the maximum number of elements in the command list. If we're already at
     * the number of elements in the command list, don't go any higher (because
     * there's no more commands above the last element in this list; it's the end of
     * the list)
     */
    switch (direction) {
    case stepDown:
      if (m_currentArmCommandIndex > 0) {
        m_currentArmCommandIndex--;
      }
      break;

    case stepUp:
      if (m_currentArmCommandIndex < (hazmatCommands.size() - 1)) {
        m_currentArmCommandIndex++;
      }
      break;

    default:
      break;
    }

    /*
     * Update the SmartDashboard's text display for this position so we know exactly
     * where we've been commanded.
     * 
     * Do this by looking up the CommandNameString that represents this new target
     * position and printing it on the dashboard.
     */

    SmartDashboard.putString("HazmatArmTargetName", hazmatCommands.get(m_currentArmCommandIndex).CommandNameString);

    /*
     * Update the color on the hazmat indicator arm with the new color of this
     * position. This helps the operator figure out where she's at simply by looking
     * at the color or the position of the LED, which changes with each hazmat arm
     * position.
     */
    Robot.m_hazmatIndicators.setIndicator(hazmatCommands.get(m_currentArmCommandIndex).color);

    /*
     * Set the hasmat arm's motor to the desired command position or drive strength.
     * 
     * Figure out which type of command (drive strength percentage or encoder count
     * set position) by examining the controlType, which is either percent or
     * position.
     * 
     * If it's percent, use the driveStrength value as a percentage to the motor
     * controller to drive the motor at a constant PWM level, which drives a
     * constant current, which drives a constant torque. This is most useful for
     * "pinning" the arm to its upper or lower mechanical stop.
     * 
     * If it's position, use the EncoderCounts value as a set position to the motor
     * controller to make the motor controller drive the arm until the feedback
     * encoder count is equal or really close to this commanded position. This is
     * useful for moving to set positions along the arm's travel that are useful for
     * game play, like the Cargo pod position or the Level 2 hatch opening.
     */
    switch (hazmatCommands.get(m_currentArmCommandIndex).controlType) {
    case percent:
      setArmDriveStrength(hazmatCommands.get(m_currentArmCommandIndex).driveStrength);
      break;

    case position:
      setTargetPosition(hazmatCommands.get(m_currentArmCommandIndex).EncoderCounts);
      break;

    default:
      break;
    }

  }

  public void periodic() {

    SmartDashboard.putNumber("HazmatArmEncoderCounts", getActualPosition());
    SmartDashboard.putNumber("HazmatTargetPositionCounts", getTargetPosition());

    /*
     * While the hazmat arm is in calibration mode, slowly move the arm down toward
     * the mechanical stops without using the encoder counts (use
     * ControlMode.PercentOutput). Also, as you're moving, reset the encoder counts
     * so that whent the calibration mode ends, the arm will be resting on its
     * mechanical stops (its starting position) and the counts will be 0; just like
     * after a perfect power-on.
     */
    if (getPerformanceLevel() == PerformanceLevel.calibration) {

      m_hazmat_arm_talon.getSensorCollection().setQuadraturePosition(0, 0);
      m_hazmat_arm_talon.set(ControlMode.PercentOutput, RobotMap.hazmatCalibrationModeDrive);
    }

  }

  @Override
  public void initDefaultCommand() {
    /* This subsystem doesn't need a default command */

  }

  public void setArmDriveStrength(double strength) {

    m_hazmat_arm_talon.set(ControlMode.PercentOutput, strength);

  }

  public void setTargetPosition(int TargetPosition) {

    int safeTargetPosition = TargetPosition;

    if (getPerformanceLevel() == PerformanceLevel.full) {

      if (TargetPosition < RobotMap.hazmatJogLowerLimit) {
        safeTargetPosition = RobotMap.hazmatJogLowerLimit;
      }

      if (TargetPosition > RobotMap.hazmatJogUpperLimit) {
        safeTargetPosition = RobotMap.hazmatJogUpperLimit;
      }

      m_hazmat_arm_talon.set(ControlMode.MotionMagic, safeTargetPosition);

    }

    m_targetPosition = safeTargetPosition;

  }

  public int getTargetPosition() {

    return m_targetPosition;
  }

  public int getActualPosition() {
    /*
     * This value is converted with a negative sign to switch the way the sensor
     * reports rotation. It's backwards compared to how the code expects the sensor
     * to rotate.
     */
    return -m_hazmat_arm_talon.getSelectedSensorPosition();
  }

  private void setPerformanceLevel(PerformanceLevel level) {

    if (level == m_currentLevel) {
      return;
    }

    switch (level) {
    case calibration:
      /*
       * While in calibration mode, reduce the maximum output of the arm's Talon so it
       * doesn't damage anything with quick movements or high torque.
       */
      m_hazmat_arm_talon.configPeakOutputForward(0.2);
      m_hazmat_arm_talon.configPeakOutputReverse(-0.2);

      m_currentLevel = PerformanceLevel.calibration;
      break;
    case full:
      /*
       * While in full mode, run the system at the maximum output of the arm's Talon
       * so it has full torque and maximum energy to move quickly.
       */
      m_hazmat_arm_talon.configPeakOutputForward(1.0);
      m_hazmat_arm_talon.configPeakOutputReverse(-1.0);

      m_currentLevel = PerformanceLevel.full;
      break;

    case unknown:
      break;
    }

  }

  private PerformanceLevel getPerformanceLevel() {
    return m_currentLevel;
  }

  public boolean isDefensiveMode() {

    return hazmatCommands.get(m_currentArmCommandIndex).controlType == armControlType.percent;
  }

  public void setCalibrationMode(boolean enable) {
    if (enable) {
      /*
       * During the calibration process, the arm will slowly move down toward its
       * initial position while resetting the quadrature encoder to 0.
       * 
       * This way, when the calibration sequence runs long enough for the arm to rest
       * on its mechanical stops at the bottom of its travel, and the encoder counters
       * have been reset to zero, it'll be in the same state it starts in; at its
       * rest, with encoder counts at 0.
       */
      setPerformanceLevel(PerformanceLevel.calibration);
    } else {
      setPerformanceLevel(PerformanceLevel.full);
    }
  }
}
