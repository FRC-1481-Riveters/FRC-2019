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

public class DriveCommandJoystick extends Command {
  public DriveCommandJoystick() {
    requires(Robot.m_drive);
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
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

    boolean detailDriveButton = Robot.m_oi.driverController.getRawButton(RobotMap.driverControllerDetailDriverButton);

    if (detailDriveButton) {
      Robot.m_drive.driveDirection((float)( throttleJoystick* RobotMap.detailDriveGain), (float) (steerJoystick * RobotMap.detailDriveGain));
    } else {
      Robot.m_drive.driveDirection((float) throttleJoystick, (float) steerJoystick);

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
