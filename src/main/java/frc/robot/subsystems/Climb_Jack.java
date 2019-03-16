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
import frc.robot.commands.JackManualCommand;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.*;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.EntryListenerFlags;

/**
 * Add your docs here.
 */
public class Climb_Jack extends Subsystem {
  public static WPI_TalonSRX m_climbJack_talon = new WPI_TalonSRX(RobotMap.climbJack_talon);
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  // private static DigitalInput m_limitSwitchExtended = new
  // DigitalInput(RobotMap.climbJackLimitSwitchExtendInput);
  // private static DigitalInput m_limitSwitchRetract = new
  // DigitalInput(RobotMap.climbJackLimitSwitchRetractInput);
  int m_lastClimbJackTargetPosition;

  private NetworkTableEntry climbJackMotor_Kp;
  private NetworkTableEntry climbJackMotor_Ki;
  private NetworkTableEntry climbJackMotor_Kd;
  private NetworkTableEntry climbJackMotor_Kf;

  public Climb_Jack() {

    m_lastClimbJackTargetPosition = getActualPosition();

    NetworkTable smartDashNetworkTable = NetworkTableInstance.getDefault().getTable("SmartDashboard");

    /*
     * Get a reference to the four PID calibrations we're using for the climbjack motor
     *  so we can capture and detect changes to these values when they change on the
     * dashboard.
     */
    climbJackMotor_Kp = smartDashNetworkTable.getEntry("climbJackMotorKp");
    climbJackMotor_Ki = smartDashNetworkTable.getEntry("climbJackMotorKI");
    climbJackMotor_Kd = smartDashNetworkTable.getEntry("climbJackMotorKD");
    climbJackMotor_Kf = smartDashNetworkTable.getEntry("climbJackMotorKF");

    /* Set these NetworkTable signals to their initial values.. */
    climbJackMotor_Kp.setDouble(0.6);
    climbJackMotor_Ki.setDouble(0.0);
    climbJackMotor_Kd.setDouble(0.0);
    climbJackMotor_Kf.setDouble(0.0);
    
    /* ... and read these initial values to set them in the Talon's PID. */
    m_climbJack_talon.config_kF(0, climbJackMotor_Kf.getDouble(0.0));
    m_climbJack_talon.config_kP(0, climbJackMotor_Kp.getDouble(0.6));
    m_climbJack_talon.config_kI(0, climbJackMotor_Ki.getDouble(0.0));
    m_climbJack_talon.config_kD(0, climbJackMotor_Kd.getDouble(0.0));

    /*
     * Add listeners that will update the Talon's PID's configuration, in the
     * background, when the signal changes over the NetworkTable, e.g. if a client
     * (like a computer) updates the value during e.g. tuning.
     * 
     * These listeners run in the background and sleep until a change is made. Then
     * they wake-up, execute the code (to update the Talon's PID's configuration)
     * then go back to sleep until the value changes again.
     */
    climbJackMotor_Kp.addListener(event -> {
      m_climbJack_talon.config_kP(0, climbJackMotor_Kp.getDouble(0.6));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    climbJackMotor_Ki.addListener(event -> {
      m_climbJack_talon.config_kI(0, climbJackMotor_Ki.getDouble(0.0));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    climbJackMotor_Kd.addListener(event -> {
      m_climbJack_talon.config_kD(0, climbJackMotor_Kd.getDouble(0.0));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    climbJackMotor_Kf.addListener(event -> {
      m_climbJack_talon.config_kF(0, climbJackMotor_Kf.getDouble(0.0));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    m_climbJack_talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, // Local Feedback Source

        RobotMap.PID_PRIMARY, // PID Slot for Source [0, 1]

        RobotMap.kTimeoutMs);

    // Feedback Device of Remote Talon
    m_climbJack_talon.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor0, RobotMap.kTimeoutMs);

    // Quadrature Encoder of current Talon
    m_climbJack_talon.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.CTRE_MagEncoder_Relative, RobotMap.kTimeoutMs);
    m_climbJack_talon.configNominalOutputForward(0);
    m_climbJack_talon.configNominalOutputReverse(0);
    m_climbJack_talon.configPeakOutputForward(1);
    m_climbJack_talon.configPeakOutputReverse(-1);
    m_climbJack_talon.setSensorPhase(true);
    m_climbJack_talon.setInverted(false);
    

    m_climbJack_talon.configClosedloopRamp(0.04); // 60 ms ramp rate limit

    SmartDashboard.putNumber("ClimbJackEncoderCounts", getActualPosition());

    // SmartDashboard.putBoolean("ElevatorLimitSwitch",
    // m_limitSwitchElevator.get());
    // SmartDashboard.putNumber("ElevatorRampRate",0.1);

  }

  public void periodic() {

    // if (m_limitSwitchExtended.get() == false) {
    // m_climbJack_talon.getSensorCollection().setQuadraturePosition(0,0);
    // }

    // SmartDashboard.putBoolean("ElevatorLimitSwitch",
    // m_limitSwitchElevator.get());
    SmartDashboard.putNumber("ClimbJackEncoderCounts", getActualPosition());
    SmartDashboard.putNumber("ClimbJackTarget Position", getTargetPosition());

    // SmartDashboard.putNumber("bullseyeElevatorPosition", m_lastTargetPosition);

    RobotMap.climbJackLimitSwitch = m_climbJack_talon.getSensorCollection().isFwdLimitSwitchClosed();
    SmartDashboard.putBoolean("ClimbJackLimitSwitch", RobotMap.climbJackLimitSwitch);

  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    setDefaultCommand(new JackManualCommand());

  }

  public void openLoopJog(double speed) {

    double jogSpeed = speed;

    m_climbJack_talon.set(ControlMode.PercentOutput, jogSpeed);

    /*
     * Update the current target position with the actual position so the jacks
     * don't improperly move if the system changes back to a closed-loop mode with a
     * call to setTargetPosition().
     */
    m_lastClimbJackTargetPosition = getActualPosition();
  }

  public void setTargetPosition(int TargetPosition) {

    if (TargetPosition < RobotMap.climbJackJogRetractedLimit) {
      TargetPosition = RobotMap.climbJackJogRetractedLimit;
    }
    if (TargetPosition > RobotMap.climbJackMaxExtend) {
      TargetPosition = RobotMap.climbJackMaxExtend;
    }
    m_climbJack_talon.set(ControlMode.Position, TargetPosition);
    m_lastClimbJackTargetPosition = TargetPosition;

  }

  public int getTargetPosition() {

    return m_lastClimbJackTargetPosition;

  }

  public int getActualPosition() {

    return m_climbJack_talon.getSelectedSensorPosition();

  }
}
