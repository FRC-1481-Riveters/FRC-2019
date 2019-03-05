/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.robot.commands.JackManualCommand;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.*;


/**
 * Add your docs here.
 */
public class Climb_Jack extends Subsystem {
  public static WPI_TalonSRX m_climbJack_talon = new WPI_TalonSRX (RobotMap.climbJack_talon);
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  //private static DigitalInput m_limitSwitchExtended = new DigitalInput(RobotMap.climbJackLimitSwitchExtendInput);
  //private static DigitalInput m_limitSwitchRetract = new DigitalInput(RobotMap.climbJackLimitSwitchRetractInput);
  int m_lastClimbJackTargetPosition;
  
  public Climb_Jack() {
  
    m_lastClimbJackTargetPosition = getActualPosition();

   SmartDashboard.putNumber("climbJackMotorKF", 0.0); 
   SmartDashboard.putNumber("climbJackMotorKp", 0.6); 
   SmartDashboard.putNumber("climbJackMotorKI", 0.0); 
   SmartDashboard.putNumber("climbJackMotorKD", 0.0);
  }
  public void periodic() {

    m_climbJack_talon.config_kF(0,  SmartDashboard.getNumber("climbJackMotorKF", 0.0)); 
    m_climbJack_talon.config_kP(0,  SmartDashboard.getNumber("climbJackMotorKp", 0.0)); 
    m_climbJack_talon.config_kI(0,  SmartDashboard.getNumber("climbJackMotorKI", 0.0)); 
    m_climbJack_talon.config_kD(0,  SmartDashboard.getNumber("climbJackMotorKD", 0.0)); 
    
    //if (m_limitSwitchExtended.get() == false) {
    //m_climbJack_talon.getSensorCollection().setQuadraturePosition(0,0);
    //}

    //SmartDashboard.putBoolean("ElevatorLimitSwitch", m_limitSwitchElevator.get());
    SmartDashboard.putNumber("ClimbJackEncoderCounts",  getActualPosition());
    SmartDashboard.putNumber("ClimbJackTarget Position",  getTargetPosition());
   
   
    //SmartDashboard.putNumber("bullseyeElevatorPosition",  m_lastTargetPosition);

    RobotMap.climbJackLimitSwitch = m_climbJack_talon.getSensorCollection().isFwdLimitSwitchClosed();
    SmartDashboard.putBoolean("ClimbJackLimitSwitch", RobotMap.climbJackLimitSwitch);

  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
    setDefaultCommand(new JackManualCommand());
  

		m_climbJack_talon.configSelectedFeedbackSensor(	FeedbackDevice.QuadEncoder,				// Local Feedback Source

		RobotMap.PID_PRIMARY,					// PID Slot for Source [0, 1]

    RobotMap.kTimeoutMs);	
    
    m_climbJack_talon.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor0, RobotMap.kTimeoutMs);				// Feedback Device of Remote Talon

    
    m_climbJack_talon.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.CTRE_MagEncoder_Relative, RobotMap.kTimeoutMs);	// Quadrature Encoder of current Talon		
    m_climbJack_talon.configNominalOutputForward(0); 
    m_climbJack_talon.configNominalOutputReverse(0); 
    m_climbJack_talon.configPeakOutputForward(1); 
    m_climbJack_talon.configPeakOutputReverse(-1); 
    m_climbJack_talon.setSensorPhase(true);
    m_climbJack_talon.setInverted(false);
    m_climbJack_talon.config_kF(0, 0.0); 
    m_climbJack_talon.config_kP(0, 1.0); 
    m_climbJack_talon.config_kI(0, 0.0); 
    m_climbJack_talon.config_kD(0, 0.0);
    m_climbJack_talon.configClosedloopRamp(0.04); // 60 ms ramp rate limit
    //SmartDashboard.putNumber("MotorKF", 0.0); 
    //SmartDashboard.putNumber("MotorKp", 1.0);
    //SmartDashboard.putNumber("MotorKI", 0.0);
    //SmartDashboard.putNumber("MotorKD", 0.0);

    SmartDashboard.putNumber("ClimbJackEncoderCounts", getActualPosition());

    //SmartDashboard.putBoolean("ElevatorLimitSwitch", m_limitSwitchElevator.get());
    //SmartDashboard.putNumber("ElevatorRampRate",0.1);
  
   
    
  }

  public void openLoopJog(double speed) {

    double jogSpeed = speed;

    m_climbJack_talon.set(ControlMode.PercentOutput, jogSpeed);

    /* Update the current target position with the actual position so the jacks don't
     * improperly move if the system changes back to a closed-loop mode with a call
     * to setTargetPosition().
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

    return m_climbJack_talon.getSensorCollection().getQuadraturePosition();

  }
}
