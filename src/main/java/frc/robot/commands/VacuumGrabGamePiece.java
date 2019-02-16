/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Vacuum;
import java.util.ArrayList;

public class VacuumGrabGamePiece extends Command {

  static private ArrayList<Vacuum> m_vacuums = new ArrayList<>();

  static {
    m_vacuums.add(Robot.m_CargoVacuum);
    m_vacuums.add(Robot.m_HatchCoverVacuum);
  }

  public VacuumGrabGamePiece() {
    // Use requires() here to declare subsystem dependencies
    for (Vacuum vacuum : m_vacuums) {
      requires(vacuum);
    }
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    for (Vacuum vacuum : m_vacuums) {
      vacuum.holdGamePiece();
    }
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    boolean isDetectedGamePiece = false;

    /* Check if the vacuums have detected a game piece */
    for (Vacuum vacuum : m_vacuums) {
      if (vacuum.isDetectsGamePiece()) {
        isDetectedGamePiece = true;
      }
    }

    if (isDetectedGamePiece) {
      /* Turn off all vacuums that DON'T have a game piece in them */
      for (Vacuum vacuum : m_vacuums) {
        if (!vacuum.isDetectsGamePiece()) {
          vacuum.releaseGamePiece();
        }
      }
    }
    return isDetectedGamePiece;
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
