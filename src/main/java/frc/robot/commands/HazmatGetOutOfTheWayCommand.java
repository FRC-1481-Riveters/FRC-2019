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

/* This command will move the Hazmat arm out of the way of the
 * cargo arm, if required.
 * If the hazmat arm is already out of the way, this command
 * won't move the hazmat arm, as it doesn't need to move.
 * 
 * This command automatically finishes when the hazmat arm
 * is out of the way of the cargo arm.
 */
public class HazmatGetOutOfTheWayCommand extends Command {
  public HazmatGetOutOfTheWayCommand() {

    requires(Robot.m_hazmat_arm);

  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    setTimeout(1.0);

  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    /*
     * Check if we need to move the arm by checking if its actual current position
     * is above the cargo arm.
     * 
     * Also, check if the hazmat arm is on its way to a position below the minimum
     * height above the cargo arm.
     * 
     * If either case is true, tell the hazmat arm to get out of the way of the
     * cargo arm by moving it to a height above the cargo arm's rotating arc.
     */
    if (Robot.m_hazmat_arm.getActualPosition() < RobotMap.hazmatMinHeightAboveCargoArm
        || Robot.m_hazmat_arm.getTargetPosition() < RobotMap.hazmatMinHeightAboveCargoArm) {
      Robot.m_hazmat_arm.setTargetPosition(RobotMap.hazmatTargetHeightAboveCargoArm);
    }

  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    /*
     * Return true, to indicate that this has completed its motion, by checking if
     * the arm is at its target location.
     */
    return (isTimedOut() || (Robot.m_hazmat_arm.getActualPosition() >= RobotMap.hazmatMinHeightAboveCargoArm));
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
