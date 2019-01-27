/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import frc.robot.*;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.RobotMap;
import frc.robot.commands.JackJogExtendCommand;
import frc.robot.commands.DoNothingCommand;

import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.*;


/**
 * Add your docs here.
 */
public class Climb_Jack extends Subsystem {
  public static WPI_TalonSRX m_climbJack_talon = new WPI_TalonSRX (RobotMap.climbJack_talon);
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  private static DigitalInput m_limitSwitchExtended = new DigitalInput(RobotMap.climbJackLimitSwitchExtendInput);
  private static DigitalInput m_limitSwitchRetract = new DigitalInput(RobotMap.climbJackLimitSwitchRetractInput);
  int m_lastTargetPosition;
  public Climb_Jack() {
  
    m_lastTargetPosition = getActualPosition();
  }
  public void periodic() {

    //m_elevator_talon.config_kF(0,  SmartDashboard.getNumber("MotorKF", 0.0), 30); 
    //m_elevator_talon.config_kP(0,  SmartDashboard.getNumber("MotorKp", 0.0), 30); 
    //m_elevator_talon.config_kI(0,  SmartDashboard.getNumber("MotorKI", 0.0), 30); 
    //m_elevator_talon.config_kD(0,  SmartDashboard.getNumber("MotorKD", 0.0), 30); 
    //m_elevator_talon.configClosedloopRamp(SmartDashboard.getNumber("ElevatorRampRate",0.1),0);
  
    
  
    if (m_limitSwitchExtended.get() == false) {
    m_climbJack_talon.getSensorCollection().setQuadraturePosition(0,0);
    }
    //SmartDashboard.putBoolean("ElevatorLimitSwitch", m_limitSwitchElevator.get());
    SmartDashboard.putNumber("ClimbJackEncoderCounts",  getActualPosition());
   
    //SmartDashboard.putNumber("bullseyeElevatorPosition",  m_lastTargetPosition);
  }
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
    setDefaultCommand(new DoNothingCommand());
  

		m_climbJack_talon.configSelectedFeedbackSensor(	FeedbackDevice.QuadEncoder,				// Local Feedback Source

		RobotMap.PID_PRIMARY,					// PID Slot for Source [0, 1]

    RobotMap.kTimeoutMs);	
    
    m_climbJack_talon.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor0, RobotMap.kTimeoutMs);				// Feedback Device of Remote Talon

    
    m_climbJack_talon.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.CTRE_MagEncoder_Relative, RobotMap.kTimeoutMs);	// Quadrature Encoder of current Talon		
    m_climbJack_talon.configNominalOutputForward(0, 30); 
    m_climbJack_talon.configNominalOutputReverse(0, 30); 
    m_climbJack_talon.configPeakOutputForward(1, 30); 
    m_climbJack_talon.configPeakOutputReverse(-1, 30); 
    m_climbJack_talon.setSensorPhase(false);
    m_climbJack_talon.config_kF(0, 0.0, 30); 
    m_climbJack_talon.config_kP(0, 1.0, 30); 
    m_climbJack_talon.config_kI(0, 0.0, 30); 
    m_climbJack_talon.config_kD(0, 0.0, 30); 

    //SmartDashboard.putNumber("MotorKF", 0.0); 
    //SmartDashboard.putNumber("MotorKp", 1.0);
    //SmartDashboard.putNumber("MotorKI", 0.0);
    //SmartDashboard.putNumber("MotorKD", 0.0);

    SmartDashboard.putNumber("ElevatorEncoderCounts", getActualPosition());

    //SmartDashboard.putBoolean("ElevatorLimitSwitch", m_limitSwitchElevator.get());
    //SmartDashboard.putNumber("ElevatorRampRate",0.1);
  
   
    
  }

  public void setTargetPosition(int TargetPosition) {

  if (TargetPosition < RobotMap.climbJackJogRetractedLimit)  {
    TargetPosition = RobotMap.climbJackJogRetractedLimit;
  }
  if (TargetPosition > RobotMap.climbJackMaxExtend){
    TargetPosition = RobotMap.climbJackMaxExtend;
  }
  m_climbJack_talon.set(ControlMode.Position, TargetPosition);
    m_lastTargetPosition = TargetPosition;
  }
  public int getTargetPosition() {

    return m_lastTargetPosition;
     
      }
  
      public int getActualPosition() {
  
        return m_climbJack_talon.getSensorCollection().getQuadraturePosition();
         
          }
}
