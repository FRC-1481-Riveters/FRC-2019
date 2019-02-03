/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.commands.doNothingGyro;
/**
 * Add your docs here.
 */
public class Gyro extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  AHRS ahrs = new AHRS(SPI.Port.kMXP);

  double lastPrintedHeading;
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    setDefaultCommand(new doNothingGyro());
    
  }


    public void periodic() {
      // Override me!
      double currentHeading = ahrs.getAngle();
      if (Math.abs(currentHeading - lastPrintedHeading) > 0.1) {
        lastPrintedHeading = currentHeading;
        System.out.println(lastPrintedHeading);
      }
   
    }
}
