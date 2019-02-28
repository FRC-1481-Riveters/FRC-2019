/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class GameOverCommand extends CommandGroup {
  /**
   * This class is a sequence that:
   * 
   * 1) Starts the climbjacks moving up to 
   * 
   * 2) Then drives the robot backwards at a set speed and duration that's been
   * tuned to effectively move the robot a few inches to allow the jacks to 
   * lift up off the L1 platform.
   */
  public GameOverCommand() {
    // Add Commands here:
    // e.g. addSequential(new Command1());
    // e.g. addParallel(new Command1());

    /*
     * Start moving the climb jacks up to clear L1 platform during the end game for 0.5 seconds.
     * negative speeds retract the climb jacks
     * positive speeds extend the climb jacks
     */
    addParallel(new JackJogDuration(0.5,-1.0));

    /* Drive back on the platform for 0.3 seconds at 40% speed. 
     * Negative speed values move forward.
     * Positive speed values move backwards. 
     */
    addParallel(new DriveForATime(0.15, 0.4));

  }
}
