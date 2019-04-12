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
import frc.robot.commands.DriveCommandJoystickLimited;
import frc.robot.Robot;
import frc.robot.subsystems.Indicators;

public class GameOverCommand extends CommandGroup {

  protected class JackJogDurationFiresOnce extends JackJogDuration {
    long m_lastFiredTimeStamp = 0;
    boolean m_isRunning = false;

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

      /*
       * Since we've called the initialize() function, we're committed to running this
       * command until it's done. Keep this child class from inadvertently timing out
       * in the middle of running to make sure it completes its full execution and
       * runs end() or interrupted() naturally.
       */
      m_isRunning = true;
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
      Robot.m_indicators.setIndicator(Indicators.Color.green);
      if (tooSoon()) {
        return;
      }

      super.execute();
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
      return tooSoon() || super.isFinished();
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
      if (tooSoon()) {
        return;
      }

      super.end();

      m_lastFiredTimeStamp = System.currentTimeMillis();
      m_isRunning = false;
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
      m_isRunning = false;
    }

    /*
     * Determine if the operator has pressed the GameOver button within the last 60
     * seconds.
     */
    public boolean tooSoon() {

      if (m_isRunning) {
        /*
         * We've already started running this command. Don't stop running it until its
         * end() or interrupted() is called. We don't want to run part of the command
         * when the child command is not allowed to start juuuuuuuust before the 60
         * seconds ends, then the command IS allowed to start right in the middle of the
         * child command running (because the 60 seconds timed out right there.) This
         * would result in some of the command executing (like the execute() or end()
         * functions) without the initialize() function having run. That's bad. Don't
         * let that happen.
         */
        return false;
      }

      if (m_lastFiredTimeStamp == 0) {
        /*
         * This is the first time the command has been run. Allow it to run by
         * indicating that it's NOT too soon.
         */
        return false;
      }

      if ((System.currentTimeMillis() - m_lastFiredTimeStamp) > 60000) {
        /*
         * The command has been run at least once before and it's been at least 60
         * seconds. It's NOT too soon to re-run this command.
         */
        return false;
      }

      /*
       * This command should not run. It's too soon since the last time it was run.
       */
      return true;
    }
  }

  protected class DriveForATimeFiresOnce extends DriveForATime {
    long m_lastFiredTimeStamp = 0;
    boolean m_isRunning = false;

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

      /*
       * Since we've called the initialize() function, we're committed to running this
       * command until it's done. Keep this child class from inadvertently timing out
       * in the middle of running to make sure it completes its full execution and
       * runs end() or interrupted() naturally.
       */
      m_isRunning = true;
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
      Robot.m_indicators.setIndicator(Indicators.Color.green);
      if (tooSoon()) {
        return;
      }

      super.execute();
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
      return tooSoon() || super.isFinished();
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
      if (tooSoon()) {
        return;
      }

      super.end();

      m_lastFiredTimeStamp = System.currentTimeMillis();
      m_isRunning = false;
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
      m_isRunning = false;
    }

    /*
     * Determine if the operator has pressed the GameOver button within the last 60
     * seconds.
     */
    public boolean tooSoon() {

      if (m_isRunning) {
        /*
         * We've already started running this command. Don't stop running it until its
         * end() or interrupted() is called. We don't want to run part of the command
         * when the child command is not allowed to start juuuuuuuust before the 60
         * seconds ends, then the command IS allowed to start right in the middle of the
         * child command running. This would result in some of the command executing
         * (like the execute() or end() functions) without the initialize() function
         * having run. That's bad. Don't let that happen.
         */
        return false;
      }

      if (m_lastFiredTimeStamp == 0) {
        /*
         * This is the first time the command has been run. Allow it to run by
         * indicating that it's NOT too soon.
         */
        return false;
      }

      if ((System.currentTimeMillis() - m_lastFiredTimeStamp) > 60000) {
        /*
         * The command has been run at least once before and it's been at least 60
         * seconds. It's NOT too soon to re-run this command.
         */
        return false;
      }

      /*
       * This command should not run. It's too soon since the last time it was run.
       */
      return true;
    }
  }

  protected class CargoArmUp extends CargoArmDuration {
    long m_lastFiredTimeStamp = 0;
    boolean m_isRunning = false;

    public CargoArmUp(double timeout, double speed) {
      super(timeout, speed);
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
      if (tooSoon()) {
        return;
      }

      super.initialize();

      /*
       * Since we've called the initialize() function, we're committed to running this
       * command until it's done. Keep this child class from inadvertently timing out
       * in the middle of running to make sure it completes its full execution and
       * runs end() or interrupted() naturally.
       */
      m_isRunning = true;
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
      return tooSoon() || super.isFinished();
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
      if (tooSoon()) {
        return;
      }

      super.end();

      m_lastFiredTimeStamp = System.currentTimeMillis();
      m_isRunning = false;
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
      m_isRunning = false;
    }

    /*
     * Determine if the operator has pressed the GameOver button within the last 60
     * seconds.
     */
    public boolean tooSoon() {

      if (m_isRunning) {
        /*
         * We've already started running this command. Don't stop running it until its
         * end() or interrupted() is called. We don't want to run part of the command
         * when the child command is not allowed to start juuuuuuuust before the 60
         * seconds ends, then the command IS allowed to start right in the middle of the
         * child command running. This would result in some of the command executing
         * (like the execute() or end() functions) without the initialize() function
         * having run. That's bad. Don't let that happen.
         */
        return false;
      }

      if (m_lastFiredTimeStamp == 0) {
        /*
         * This is the first time the command has been run. Allow it to run by
         * indicating that it's NOT too soon.
         */
        return false;
      }

      if ((System.currentTimeMillis() - m_lastFiredTimeStamp) > 60000) {
        /*
         * The command has been run at least once before and it's been at least 60
         * seconds. It's NOT too soon to re-run this command.
         */
        return false;
      }

      /*
       * This command should not run. It's too soon since the last time it was run.
       */
      return true;
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
    // If cargo arm is up, then do not move cargo arm

    addSequential(new CargoArmUp(1, -0.75));

    addSequential(new DriveForATimeFiresOnce(1, -0.5));
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
    addSequential(new DriveForATimeFiresOnce(0.1, 0.4));

    /*
     * Take control of the Drive sub-system to limit the driver from moving
     * backwards on the platform inadvertantly when they might fall off.
     * 
     * Do this by limiting the direction of movement to only forward direction
     * (which is ironically a negative value.)
     * 
     * Limit this for no more than 20 seconds, after which time the driver has
     * probably gotten their senses back and won't move the joystick backwards.
     */
    addSequential(new DriveCommandJoystickLimited(20.0, -1.0, 0.0));

  }
}
