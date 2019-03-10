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

public class autoassistAlignment extends Command {

  public autoassistAlignment() {

    requires(Robot.m_vision);
    requires(Robot.m_drive);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {

    Robot.m_vision.SteeringPIDEnable();
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {

    // while driving in joystick you need drive.java and the usage of button 0
    // you need joystick to feed to drive
    // and direction you are facing

    double throttleJoystick = Robot.m_oi.driverController.getRawAxis(RobotMap.driverControllerAxisFrontAndBack);
    /*
     * Invoke the linear drive controller that doesn't perform any squaring of the
     * drive commands to make the PID's control of the steering easier to tune.
     * 
     * However, retain the squaring of the throttle joystick to make it easier for
     * the driver to control their speed while driving with the autoassistAlignment.
     */
    Robot.m_drive.driveDirectionLinear(
        Math.pow(throttleJoystick, 2.0) * Math.signum(throttleJoystick) * RobotMap.detailDriveGain,
        Robot.m_vision.getDriveSteeringOutput());
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {

    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {

    Robot.m_vision.SteeringPIDReset();
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {

    Robot.m_vision.SteeringPIDReset();
  }
}
