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
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTable;

import java.util.Timer;
import java.util.TimerTask;

public class Gyro extends Subsystem {

  AHRS m_ahrs = new AHRS(SPI.Port.kMXP);

  GyroDiary m_diary = new GyroDiary();
  double m_lastPrintedHeading;

  private NetworkTableEntry m_gyroNetworkDebug;

  private GyroDiaryTask m_updateGyroDiaryTask = new GyroDiaryTask();
  private Timer m_gyroUpdateTimer = new Timer();

  protected class GyroDiaryTask extends TimerTask {
    public void run() {
      m_diary.add(getGyroHeading());
    }
  }

  public Gyro() {

    NetworkTableInstance ntinst = NetworkTableInstance.getDefault();
    NetworkTable SmartDashboard = ntinst.getTable("SmartDashboard");
    m_gyroNetworkDebug = SmartDashboard.getEntry("gyroHeading");

    /*
     * Schedule a background task that runs every 20 ms, starting immediately, to
     * update the gyro diary with a reading from the gyro
     */
    m_gyroUpdateTimer.schedule(m_updateGyroDiaryTask, 0, 20);

  }

  @Override
  public void initDefaultCommand() {
  }

  public void periodic() {

  }

  public double getGyroHeading() {
    return m_ahrs.getYaw();
  }

  public double gyroDiary(long timeStamp) {
    return m_diary.getHeadingAtTimeStamp(timeStamp);
  }

}
