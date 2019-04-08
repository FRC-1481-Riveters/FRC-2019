/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class CargoArmDuration extends Command {
    double m_speed;

  public CargoArmDuration(double timeout, double speed) {
    
    setTimeout(timeout);
    
    /* Set the speed of the movement in % output of the drive system. */
    m_speed = speed;

    requires(Robot.m_cargo_arm);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    Robot.m_cargo_arm.openLoopJog(m_speed);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
  boolean IsCargoOutOfWay = false;
  
    if (Robot.m_cargo_arm.getActualPosition() < frc.robot.RobotMap.cargoPivotArmOutOfWay){
      IsCargoOutOfWay = true;
    }
    IsCargoOutOfWay = IsCargoOutOfWay || isTimedOut();
    return IsCargoOutOfWay;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.m_cargo_arm.openLoopJog(0.0);
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    Robot.m_cargo_arm.openLoopJog(0.0);
  }
}
