/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.JackJogDuration;
import frc.robot.commands.DriveForATime;

public class GameOverCommand extends CommandGroup {

  protected class JackJogDurationFiresOnce extends JackJogDuration {
    long m_lastFiredTimeStamp = 0;

    public JackJogDurationFiresOnce(double timeout, double speed) {
      super(timeout, speed);
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
      if (tooSoon()) {
        return;
      }

      super.initialize();
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
      if (tooSoon()) {
        return;
      }

      super.execute();
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
      return super.isFinished() || tooSoon();
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
      if (tooSoon()) {
        return;
      }

      super.end();

      m_lastFiredTimeStamp = System.currentTimeMillis();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
      if (tooSoon()) {
        return;
      }

      super.interrupted();

      m_lastFiredTimeStamp = System.currentTimeMillis();
    }

    /*
     * Determine if the operator has pressed the GameOver button within the last 60
     * seconds.
     */
    public boolean tooSoon() {
      return (m_lastFiredTimeStamp > 0) && (System.currentTimeMillis() - m_lastFiredTimeStamp) < 60000;
    }
  }

  protected class DriveForATimeFiresOnce extends DriveForATime {
    long m_lastFiredTimeStamp = 0;

    public DriveForATimeFiresOnce(double timeout, double driveSpeed) {
      super(timeout, driveSpeed);
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
      if (tooSoon()) {
        return;
      }

      super.initialize();
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
      if (tooSoon()) {
        return;
      }

      super.execute();
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
      return super.isFinished() || tooSoon();
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
      if (tooSoon()) {
        return;
      }

      super.end();

      m_lastFiredTimeStamp = System.currentTimeMillis();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
      if (tooSoon()) {
        return;
      }

      super.interrupted();

      m_lastFiredTimeStamp = System.currentTimeMillis();
    }

    /*
     * Determine if the operator has pressed the GameOver button within the last 60
     * seconds or if this is the first time the command has been called, because:
     * 
     * 1) Don't return true if this is the first time the command has been called.
     * Check this by verifying that m_lastFiredTimeStamp is greater than 0 (it's
     * initialized to 0)
     * 
     * 2) Computing the difference between the current time and the timestamp of the
     * last time this command was initiated. If it's been too soon, return true to
     * keep the operator from invoking it too often. We still want to allow the team
     * to practice without resetting the roborio, but we don't want the operator to
     * inadvertantly invoke this command multiple times during competition, while
     * climbing. 60 seconds dwell between attempts is long enough.
     */
    public boolean tooSoon() {
      return ((m_lastFiredTimeStamp > 0) && ((System.currentTimeMillis() - m_lastFiredTimeStamp) < 60000));
    }

  }

  /**
   * This class is a sequence that:
   * 
   * 1) Starts the climbjacks moving up to
   * 
   * 2) Then drives the robot backwards at a set speed and duration that's been
   * tuned to effectively move the robot a few inches to allow the jacks to lift
   * up off the L1 platform.
   */
  public GameOverCommand() {
    // Add Commands here:
    // e.g. addSequential(new Command1());
    // e.g. addParallel(new Command1());

    /*
     * Start moving the climb jacks up to clear L1 platform during the end game for
     * 1.5 seconds.
     * 
     * negative speeds retract the climb jacks
     * 
     * positive speeds extend the climb jacks
     */
    addParallel(new JackJogDurationFiresOnce(1.5, -1.0));

    /*
     * Drive back on the platform for 0.15 seconds at 40% speed.
     * 
     * Negative speed values move forward.
     * 
     * Positive speed values move backwards.
     */
    addParallel(new DriveForATimeFiresOnce(0.15, 0.4));

  }
}
