/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;

import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.*;

/**
 * Add your docs here.
 */
public class Hazmat_Arm extends Subsystem {
  public static WPI_TalonSRX m_hazmat_arm_talon = new WPI_TalonSRX(RobotMap.hazmatArm_Talon);
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  private static DigitalInput m_limitSwitchExtended = new DigitalInput(RobotMap.hazmatLimitSwitch);
  int m_lastHazmatTargetPosition;

  public int hazmatPositions[] = new int[6];

  public Hazmat_Arm() {

    SmartDashboard.putNumber("HazmatArmMotorKF", 0.0);
    SmartDashboard.putNumber("HazmatArmMotorKp", 6.0);
    SmartDashboard.putNumber("HazmatArmMotorKI", 0.0);
    SmartDashboard.putNumber("HazmatArmMotorKD", 0.0);

    m_hazmat_arm_talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, RobotMap.PID_PRIMARY, 0);

    /* Feedback device of remote talon */
    m_hazmat_arm_talon.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor0, 0);
    /* Quadrature Encoder of current Talon */
    m_hazmat_arm_talon.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.CTRE_MagEncoder_Relative, 0);
    m_hazmat_arm_talon.configNominalOutputForward(0, 0);
    m_hazmat_arm_talon.configNominalOutputReverse(0, 0);
    m_hazmat_arm_talon.configPeakOutputForward(.5, 0);
    m_hazmat_arm_talon.configPeakOutputReverse(-.5, 0);
    m_hazmat_arm_talon.setSensorPhase(true);
    m_hazmat_arm_talon.setInverted(false);

    m_lastHazmatTargetPosition = getActualPosition();

    hazmatPositions[0] = RobotMap.hazmatPodIntake;
    hazmatPositions[1] = RobotMap.hazmatPodLoadStart;
    hazmatPositions[2] = RobotMap.hazmatHatchBottom;
    hazmatPositions[3] = RobotMap.hazmatRocket1Pod;
    hazmatPositions[4] = RobotMap.hazmatRocket2Hatch;
    hazmatPositions[5] = RobotMap.hazmatRocket2Pod;
  }

  public void periodic() {

    m_hazmat_arm_talon.config_kF(0, SmartDashboard.getNumber("HazmatArmMotorKF", 0.0), 0);
    m_hazmat_arm_talon.config_kP(0, SmartDashboard.getNumber("HazmatArmMotorKp", 0.0), 0);
    m_hazmat_arm_talon.config_kI(0, SmartDashboard.getNumber("HazmatArmMotorKI", 0.0), 0);
    m_hazmat_arm_talon.config_kD(0, SmartDashboard.getNumber("HazmatArmMotorKD", 0.0), 0);

    if (m_limitSwitchExtended.get() == false) {
      m_hazmat_arm_talon.getSensorCollection().setQuadraturePosition(0, 0);
    }

    SmartDashboard.putNumber("HazmatArmEncoderCounts", getActualPosition());
    SmartDashboard.putNumber("HazmatTargetPositionCounts", getTargetPosition());

  }

  @Override
  public void initDefaultCommand() {

  }

  public void setTargetPosition(int TargetPosition) {
    if (TargetPosition < RobotMap.hazmatJogLowerLimit) {
      TargetPosition = RobotMap.hazmatJogLowerLimit;
    }
    if (TargetPosition > RobotMap.hazmatJogUpperLimit) {
      TargetPosition = RobotMap.hazmatJogUpperLimit;
    }
    m_hazmat_arm_talon.set(ControlMode.Position, TargetPosition);
    m_lastHazmatTargetPosition = TargetPosition;

  }

  public int getTargetPosition() {

    return m_lastHazmatTargetPosition;
  }

  public int getActualPosition() {
    return -m_hazmat_arm_talon.getSensorCollection().getQuadraturePosition();
  }
}
