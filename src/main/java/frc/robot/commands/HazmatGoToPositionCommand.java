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

/* This command will move the Hazmat arm to a fixed
 * location.
 */
public class HazmatGoToPositionCommand extends Command {

  int m_targetLocation;

  public HazmatGoToPositionCommand(int targetLocation) {

    requires(Robot.m_hazmat_arm);

    m_targetLocation = targetLocation;

  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {

  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {

    Robot.m_hazmat_arm.setTargetPosition(m_targetLocation);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    /*
     * Return true, to indicate that this has completed its motion, by checking if
     * the arm is above the height of the cargo arm. If it is, indicate that the
     * command may complete.
     */
    return (isTimedOut() || (Math.abs(Robot.m_hazmat_arm.getActualPosition() - m_targetLocation) <= RobotMap.hazmatPositionTolerance));
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
