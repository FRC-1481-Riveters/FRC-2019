/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class HazmatCalibrationCommand extends Command {

  public HazmatCalibrationCommand() {
    requires(Robot.m_hazmat_arm);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    setTimeout(5.0);

    /*
     * Enable the arm's calibration mode sequence for the duration of this command.
     * The subsystem runs its own calibration sequence and this command just enables
     * the operator to use the controller to start and maintain the calibration
     * sequence.
     */
    Robot.m_hazmat_arm.setCalibrationMode(true);
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return isTimedOut();
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {

    Robot.m_hazmat_arm.setCalibrationMode(false);

    /*
     * Clear out the previous hazmat target position and reset it to the arm's
     * current position. This keeps the arm from jumping up to its previously set
     * target position (that might have been completely wrong during uncalibrated
     * operation).
     * 
     * Ideally, the actualPosition is 0 because that's what the calibratin procedure
     * ends with, but dont assume that and simply ask the arm where it *thinks* it
     * is and set it to that same value so it stays there and doesn't move anywhere.
     */
    Robot.m_hazmat_arm.setTargetPosition(Robot.m_hazmat_arm.getActualPosition());
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {

    Robot.m_hazmat_arm.setCalibrationMode(false);

    /*
     * Clear out the previous hazmat target position and reset it to the arm's
     * current position. This keeps the arm from jumping up to its previously set
     * target position (that might have been completely wrong during uncalibrated
     * operation).
     * 
     * Ideally, the actualPosition is 0 because that's what the calibratin procedure
     * ends with, but dont assume that and simply ask the arm where it *thinks* it
     * is and set it to that same value so it stays there and doesn't move anywhere.
     */
    Robot.m_hazmat_arm.setTargetPosition(Robot.m_hazmat_arm.getActualPosition());
  }
}
