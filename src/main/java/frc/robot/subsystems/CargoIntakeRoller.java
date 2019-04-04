/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.*;
import frc.robot.RobotMap;
import irsensor.IRSensor;

/**
 * Add your docs here.
 */
public class CargoIntakeRoller extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  public static WPI_TalonSRX m_cargo_arm_roller_talon = new WPI_TalonSRX(RobotMap.cargoIntakeArm_Talon);

  private IRSensor m_irSensor = new IRSensor(0, IRSensor.sensorType.GP2Y0A41SK0F);

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }

  public void setSpeed(double IntakeRollerSpeed) {
    m_cargo_arm_roller_talon.set(ControlMode.PercentOutput, IntakeRollerSpeed);
  }

  public boolean isCargoDetected() {
    return m_irSensor.getRawVoltage() < RobotMap.intakeSensorBallPresent;
  }
}