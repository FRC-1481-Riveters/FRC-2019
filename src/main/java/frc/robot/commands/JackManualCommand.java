/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.RobotMap;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class JackManualCommand extends Command {
  public JackManualCommand() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    requires(Robot.m_climb_jack);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
     // Read the axes of the joysticks
     double throttleUpAxisValue = Robot.m_oi.driverController.getRawAxis(RobotMap.climbJackJogExtendAxis);
     double throttleDownAxisValue = Robot.m_oi.driverController.getRawAxis(RobotMap.climbJackJogRetractAxis);

//     m_climbJackLimitSwitch 

     // System.out.println(throttleUpAxisValue);
     // if climbJackRectractTrigger is pulled, retract the climb jacks
     int triggerPulled = 0;
     if (throttleUpAxisValue > RobotMap.joystickIsActive)  {
       SmartDashboard.putNumber("ClimbJackRetract", throttleUpAxisValue);
       Robot.m_climb_jack.setTargetPosition(Robot.m_climb_jack.getTargetPosition() - RobotMap.climbJackRate);
     }
 
     // if climbJackExtendTrigger is pulled, extend the climb jacks
     if ((throttleDownAxisValue > RobotMap.joystickIsActive) && (RobotMap.climbJackLimitSwitch != true)){
       SmartDashboard.putNumber("ClimbJackExtend", throttleDownAxisValue);
       if (Robot.m_climb_jack.getTargetPosition() < RobotMap.climbJackEndofTravel)
          {Robot.m_climb_jack.setTargetPosition(Robot.m_climb_jack.getTargetPosition() + (int)(RobotMap.climbJackRate * throttleDownAxisValue));
          }
        else
        {
          Robot.m_climb_jack.setTargetPosition(Robot.m_climb_jack.getTargetPosition() + (int)(RobotMap.climbJackSlowRate * throttleDownAxisValue));
        }
     }
 
     if (triggerPulled == 1) {
       // Robot.m_cargo_arm.setTargetPosition(Robot.m_cargo_arm.getTargetPosition());
     }
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
