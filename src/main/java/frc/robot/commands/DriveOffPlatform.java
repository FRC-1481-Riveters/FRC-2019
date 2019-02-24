/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class DriveOffPlatform extends CommandGroup {
  /**
   * This class is a sequence that:
   * 
   * 1) Starts the vacuum pumps to hold a game piece that's already been placed
   * against the suction cups, but doesn't wait forever for the suction to build.
   * 
   * 2) Then drives the robot forward at a set speed and duration that's been
   * tuned to effectively land the robot safely after dismounting from the L2
   * platform.
   */
  public DriveOffPlatform() {
    // Add Commands here:
    // e.g. addSequential(new Command1());
    // e.g. addParallel(new Command1());

    /*
     * Initiate vacuuming up a game piece that's already on the hazmat vacuum
     * suction cups. Wait no longer than 3 seconds for the gamepiece to be detected.
     */
    addSequential(new VacuumGrabGamePiece(3.0));

    /* Drive off the platform for 2.0 seconds at 50% speed. */
    addSequential(new DriveForATime(2.0, 0.5));

  }
}
