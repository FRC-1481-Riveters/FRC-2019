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

/**
 * Add your docs here.
 */
public class Hazmat_Arm extends Subsystem {
  public static WPI_TalonSRX m_hazmat_arm_talon = new WPI_TalonSRX (RobotMap.suctionArm_Talon);
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  private static DigitalInput m_limitSwitchExtended = new DigitalInput(RobotMap.climbJackLimitSwitchExtendInput);
  private static DigitalInput m_limitSwitchRetract = new DigitalInput(RobotMap.climbJackLimitSwitchRetractInput);
  int m_lastTargetPosition;
  public Hazmat_Arm() {
  
    m_lastTargetPosition = getActualPosition();
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
    SmartDashboard.putNumber("ClimbJackEncoderCounts",  getActualPosition());
   
    //SmartDashboard.putNumber("bullseyeElevatorPosition",  m_lastTargetPosition);
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
  public void setTargetPosition(int TargetPosition) {
    if (TargetPosition < RobotMap.climbJackJogRetractedLimit)  {
      TargetPosition = RobotMap.climbJackJogRetractedLimit;
    }
    if (TargetPosition > RobotMap.climbJackMaxExtend){
      TargetPosition = RobotMap.climbJackMaxExtend;
    }
    m_hazmat_arm_talon.set(ControlMode.Position, TargetPosition);
      m_lastTargetPosition = TargetPosition;
  }
  public int getTargetPosition() {

    return m_lastTargetPosition;
  }
  public int getActualPosition() {
    return m_hazmat_arm_talon.getSensorCollection().getQuadraturePosition();
  }
}
