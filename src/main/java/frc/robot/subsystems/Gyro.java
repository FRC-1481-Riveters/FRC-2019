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
import gyrohelper.GyroDiary;

public class Gyro extends Subsystem {

  AHRS m_ahrs = new AHRS(SPI.Port.kMXP);
  
  GyroDiary m_diary = new GyroDiary();
  double m_lastPrintedHeading;

  @Override
  public void initDefaultCommand() {
  }

  public void periodic() {

    double currentHeading = getGyroHeading();

    if (Math.abs(currentHeading - m_lastPrintedHeading) > 0.1) {
      m_lastPrintedHeading = currentHeading;
      System.out.println(m_lastPrintedHeading);
    }

    m_diary.add(currentHeading);

  }

  public double getGyroHeading() {
    return m_ahrs.getYaw();
  }

  public double gyroDiary(long timeStamp) {
    return m_diary.getHeadingAtTimeStamp(timeStamp);
  }

}
