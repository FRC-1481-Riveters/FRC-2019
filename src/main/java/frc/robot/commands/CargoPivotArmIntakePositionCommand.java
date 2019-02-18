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

public class CargoPivotArmIntakePositionCommand extends Command {
  public CargoPivotArmIntakePositionCommand() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    requires(Robot.m_cargo_arm);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    setTimeout(RobotMap.cargoPivotArmMovementTimeout);
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    Robot.m_cargo_arm.setTargetPosition(RobotMap.cargoPivotArmIntakePosition);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return (isTimedOut() || Math.abs(Robot.m_cargo_arm.getActualPosition() - RobotMap.cargoPivotArmIntakePosition) < 100);
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
