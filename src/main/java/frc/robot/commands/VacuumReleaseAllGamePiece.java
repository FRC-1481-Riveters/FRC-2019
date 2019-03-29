/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Indicators;
import frc.robot.subsystems.Vacuum;
import java.util.ArrayList;
import frc.robot.RobotMap;

public class VacuumReleaseAllGamePiece extends Command {

  static private ArrayList<Vacuum> m_vacuums = new ArrayList<>();

  static {
    m_vacuums.add(Robot.m_CargoVacuum);
    m_vacuums.add(Robot.m_HatchCoverVacuum);
  }

  public VacuumReleaseAllGamePiece() {
    // Use requires() here to declare subsystem dependencies
    for (Vacuum vacuum : m_vacuums) {
      requires(vacuum);
    }
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    
    /* Tell the driver that we have received their request to release the game piece */
    Robot.m_oi.rumbleDriver(RobotMap.vacuumGamePieceReleaseJoystickRumbleTime);
    
    /* Release all game pieces by instructing the vacuums to release the game pieces. */
    for (Vacuum vacuum : m_vacuums) {
      vacuum.releaseGamePiece();
    }
    
    Robot.m_indicators.setIndicator(Indicators.Color.off);
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return true;
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
