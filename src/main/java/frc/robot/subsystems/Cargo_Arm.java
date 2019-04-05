/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.robot.commands.CargoPivotArmManualCommand;

import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.*;

/**
 * Add your docs here.
 */
public class Cargo_Arm extends Subsystem {
  public static WPI_TalonSRX m_cargo_arm_left_talon = new WPI_TalonSRX(RobotMap.cargoPivotArmLeft_Talon);
  public static WPI_TalonSRX m_cargo_arm_right_talon = new WPI_TalonSRX(RobotMap.cargoPivotArmRight_Talon);
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  int m_lastCargoTargetPosition;

  public Cargo_Arm() {
    m_lastCargoTargetPosition = getActualPosition();

    // set left talon to follow right talon
    m_cargo_arm_left_talon.follow(m_cargo_arm_right_talon);

    // invert left talon
    m_cargo_arm_left_talon.setInverted(true);
  }

  public void periodic() {
    SmartDashboard.putNumber("CargoArmEncoderCounts", getActualPosition());
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand())
    setDefaultCommand(new CargoPivotArmManualCommand());

    m_cargo_arm_right_talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, // Local Feedback Source

        RobotMap.PID_PRIMARY, // PID Slot for Source [0, 1]

        RobotMap.kTimeoutMs);

    m_cargo_arm_right_talon.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor0); // Feedback
                                                                                                                  // Device
                                                                                                                  // of
                                                                                                                  // Remote
                                                                                                                  // Talon

    m_cargo_arm_right_talon.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.CTRE_MagEncoder_Relative); // Quadrature Encoder of current Talon
    m_cargo_arm_right_talon.configNominalOutputForward(0);
    m_cargo_arm_right_talon.configNominalOutputReverse(0);
    m_cargo_arm_right_talon.configPeakOutputForward(1);
    m_cargo_arm_right_talon.configPeakOutputReverse(-1);
    m_cargo_arm_right_talon.setSensorPhase(true);
    m_cargo_arm_right_talon.config_kF(0, 0.0);
    m_cargo_arm_right_talon.config_kP(0, 0.2);
    m_cargo_arm_right_talon.config_kI(0, 0.0);
    m_cargo_arm_right_talon.config_kD(0, 0.0);

    SmartDashboard.putNumber("CargoArmEncoderCounts", getActualPosition());

  }

  public void openLoopJog(double speed) {

    double jogSpeed = speed;

    // set left talon to follow right talon
    m_cargo_arm_left_talon.follow(m_cargo_arm_right_talon);

    // invert left talon
    m_cargo_arm_left_talon.setInverted(true);

    m_cargo_arm_right_talon.set(ControlMode.PercentOutput, jogSpeed);
  }


  public void setTargetPosition(int CargoTargetPosition) {

    if (CargoTargetPosition > RobotMap.cargoPivotMaxRetract) {
      CargoTargetPosition = RobotMap.cargoPivotMaxRetract;
    }
    if (CargoTargetPosition < RobotMap.cargoPivotMaxExtend) {
      CargoTargetPosition = RobotMap.cargoPivotMaxExtend;
    }


    SmartDashboard.putNumber("CargoArmTargetPosition", CargoTargetPosition);
    SmartDashboard.putNumber("CargoArmLastTargetPosition", m_lastCargoTargetPosition);
    m_cargo_arm_right_talon.set(ControlMode.Position, CargoTargetPosition);
    m_lastCargoTargetPosition = CargoTargetPosition;
  }

  public int getTargetPosition() {

    return m_lastCargoTargetPosition;

  }

  public int getActualPosition() {

    return m_cargo_arm_right_talon.getSelectedSensorPosition();

  }
}
