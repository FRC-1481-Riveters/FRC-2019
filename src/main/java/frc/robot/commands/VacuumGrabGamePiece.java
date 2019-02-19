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
import java.util.Collections;

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

    ArrayList<Vacuum.state> states = new ArrayList<>();

    /*
     * Determine the current operating states of all the vacuums. Are they grabbing?
     * (which is trying to suck up a new game piece) Are they holding? (which is
     * when a game piece has been detected already) Are they off? Store this
     * information in an ArrayList so it can be searched later.
     */
    for (Vacuum vacuum : m_vacuums) {
      states.add(vacuum.getState());
    }

    /*
     * Test if any of the vacuums are currently grabbing. Since the operator pressed
     * the button again, she wants us to abate the noise of full running pumps and
     * make them run slower. This bypasses the automatic suction detection and
     * transitions the pumps to the lower holding speed (which is quieter)
     */
    if (Collections.frequency(states, Vacuum.state.grabbing) > 0) {
      /*
       * At least one vacuum is grabbing (which is running at full speed) Set all the
       * vacuums to holding to abate the noise and bypass the automatic piece
       * detection algorithm.
       */
      for (Vacuum vacuum : m_vacuums) {
        vacuum.holdGamePiece();
      }
    } else {

      /*
       * Either the vacuums are off or they're holding. Run them at full speed to
       * regrab something or bypass the holding state manually.
       */
      for (Vacuum vacuum : m_vacuums) {
        vacuum.grabGamePiece();
      }
    }
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {

    boolean[] detectsGamePiece = new boolean[m_vacuums.size()];
    boolean isDetectedGamePiece = false;

    /* Check if the vacuums have detected a game piece */
    for (int index = 0; index < m_vacuums.size(); ++index) {

      detectsGamePiece[index] = m_vacuums.get(index).isDetectsGamePiece();

      if (detectsGamePiece[index]) {
        isDetectedGamePiece = true;
      }
    }

    if (isDetectedGamePiece) {
      /* Turn off all vacuums that DON'T have a game piece in them */
      for (int index = 0; index < m_vacuums.size(); ++index) {
        if (!detectsGamePiece[index]) {
          m_vacuums.get(index).releaseGamePiece();
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
