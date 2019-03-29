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

public class VacuumGrabGamePiece extends Command {

  static private ArrayList<Vacuum> m_vacuums = new ArrayList<>();

  static {
    m_vacuums.add(Robot.m_CargoVacuum);
    m_vacuums.add(Robot.m_HatchCoverVacuum);
  }

  public VacuumGrabGamePiece(double timeout) {
    super(timeout);

    effectSubsystemRequires();
  }

  public VacuumGrabGamePiece() {
    effectSubsystemRequires();
  }

  private void effectSubsystemRequires() {
    // Use requires() here to declare subsystem dependencies
    for (Vacuum vacuum : m_vacuums) {
      requires(vacuum);
    }
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {

    /* Command all the vacuums to try to grab a game piece. */
    for (Vacuum vacuum : m_vacuums) {
      vacuum.grabGamePiece();
    }
    
    Robot.m_indicators.setIndicator(Indicators.Color.blue);
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

    /* Check if any of the vacuums have detected a game piece */
    for (int index = 0; index < m_vacuums.size(); ++index) {

      detectsGamePiece[index] = m_vacuums.get(index).isDetectsGamePiece();

      if (detectsGamePiece[index]) {
        /* At least one vacuum has detected a game piece.
         * Remember this because it's important later.
         */
        isDetectedGamePiece = true;
      }
    }

    /* Check if any vacuums have found a game piece.
     * 
     * If at least one vacuum has a game piece, turn off all the other vacuums.
     * 
     * If no vacuum has a game piece, just keep letting them all look with this
     * command, until it times out (if it was invoked with a timeout.)
     */
    if (isDetectedGamePiece) {
      
      Robot.m_indicators.setIndicator(Indicators.Color.red);
      
      /* Turn off all vacuums that DON'T have a game piece in them */
      for (int index = 0; index < m_vacuums.size(); ++index) {
        if (!detectsGamePiece[index]) {
          /* This vacuum doesn't have a game piece, so turn it off. */
          m_vacuums.get(index).releaseGamePiece();
        }
      }
    }
    
    /* All the vacuums have been tested, and if at least one of them indicated
     * that it had a game piece, quit this command after turning off all the other vacuums.
     * Vacuums have no idea if another vacuum has found a game piece already, so this command
     * has to test them and shutdown any vacuums that are no longer needed.
     * 
     * If the command timed out looking for a game piece, but never finding them, just leave
     * the vacuums running until they're either shut off or the lucky one finds
     * a game piece and slows down automatically.
     */

    return (isTimedOut() || isDetectedGamePiece);
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
