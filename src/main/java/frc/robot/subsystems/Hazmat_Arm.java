/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.robot.Robot;

import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.*;
import java.util.Arrays;

/**
 * Add your docs here.
 */
public class Hazmat_Arm extends Subsystem {
  public static WPI_TalonSRX m_hazmat_arm_talon = new WPI_TalonSRX (RobotMap.hazmatArm_Talon);
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  private static DigitalInput m_limitSwitchExtended = new DigitalInput(RobotMap.hazmatLimitSwitch);
  int m_lastHazmatTargetPosition;

  public int hazmatPositions[] = new int [6];
  public Hazmat_Arm() {
  
    m_lastHazmatTargetPosition = getActualPosition();

    hazmatPositions[0] = RobotMap.hazmatPodIntake;
    hazmatPositions[1] = RobotMap.hazmatPodLoadStart;
    hazmatPositions[2] = RobotMap.hazmatHatchBottom;
    hazmatPositions[3] = RobotMap.hazmatRocket1Pod;
    hazmatPositions[4] = RobotMap.hazmatRocket2Hatch;
    hazmatPositions[5] = RobotMap.hazmatRocket2Pod;
  }
  public void periodic() {

    //m_elevator_talon.config_kF(0,  SmartDashboard.getNumber("MotorKF", 0.0), 30); 
    //m_elevator_talon.config_kP(0,  SmartDashboard.getNumber("MotorKp", 0.0), 30); 
    //m_elevator_talon.config_kI(0,  SmartDashboard.getNumber("MotorKI", 0.0), 30); 
    //m_elevator_talon.config_kD(0,  SmartDashboard.getNumber("MotorKD", 0.0), 30); 
    //m_elevator_talon.configClosedloopRamp(SmartDashboard.getNumber("ElevatorRampRate",0.1),0);
  
    
  
    if (m_limitSwitchExtended.get() == false) {
      m_hazmat_arm_talon.getSensorCollection().setQuadraturePosition(0,0);
    }
    //SmartDashboard.putBoolean("ElevatorLimitSwitch", m_limitSwitchElevator.get());
    SmartDashboard.putNumber("HazmatArmEncoderCounts",  getActualPosition());
   
    //SmartDashboard.putNumber("bullseyeElevatorPosition",  m_lastTargetPosition);
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
  public void setTargetPosition(int TargetPosition) {
    if (TargetPosition < RobotMap.hazmatJogLowerLimit)  {
      TargetPosition = RobotMap.hazmatJogLowerLimit;
    }
    if (TargetPosition > RobotMap.hazmatJogUpperLimit){
      TargetPosition = RobotMap.hazmatJogUpperLimit;
    }
    m_hazmat_arm_talon.set(ControlMode.Position, TargetPosition);
      m_lastHazmatTargetPosition = TargetPosition;
  }
  public int getTargetPosition() {

    return m_lastHazmatTargetPosition;
  }
  public int getActualPosition() {
    return m_hazmat_arm_talon.getSensorCollection().getQuadraturePosition();
  }
}
