/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.RobotMap;
import frc.robot.Robot;

public class DriveCommandJoystickLimited extends Command {

  private double m_minSpeed = -1.0;
  private double m_maxSpeed = 1.0;

  public DriveCommandJoystickLimited(double timeout, double minSpeed, double maxSpeed) {
    requires(Robot.m_drive);

    m_minSpeed = minSpeed;
    m_maxSpeed = maxSpeed;

    setTimeout(timeout);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {

    // Read the axes of the joysticks
    double throttleJoystick = Robot.m_oi.driverController.getRawAxis(RobotMap.driverControllerAxisFrontAndBack);
    double steerJoystick = Robot.m_oi.driverController.getRawAxis(RobotMap.driverControllerAxisLeftAndRight);

    boolean driveFullSpeedButton = Robot.m_oi.driverController
        .getRawButton(RobotMap.driverControllerDetailDriverButton);

    if (!driveFullSpeedButton) {

      double speed = limitSpeed(throttleJoystick * RobotMap.detailDriveGain);
      Robot.m_drive.driveDirection(speed, (steerJoystick * RobotMap.detailDriveTurn));
    } else {

      double speed = limitSpeed(throttleJoystick);
      Robot.m_drive.driveDirection(speed, steerJoystick);
    }
  }

  /*
   * Limit the robot's speed to between the m_minSpeed (the fastest speed
   * forwards) and the m_maxSpeed, (which is the fastest speed backwards)
   */

  private double limitSpeed(double speed) {
    double limitedSpeed = speed;
    
    limitedSpeed = Math.max(limitedSpeed, m_minSpeed);
    limitedSpeed = Math.min(limitedSpeed, m_maxSpeed);

    return limitedSpeed;
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return isTimedOut();
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
