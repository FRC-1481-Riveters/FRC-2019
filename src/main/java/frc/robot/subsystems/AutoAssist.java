/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.RobotMap;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.util.Timer;
import java.util.TimerTask;

public class AutoAssist extends Subsystem {

  private Solenoid m_autoassistCameraLightControl = new Solenoid(RobotMap.autoAssistCameraPCMId,
      RobotMap.autoAssistCameraSolenoidId);

  /*
   * Create a networktable entry called autoAssistConnectionTest for use by the
   * RPi to see if the connection has gone stale. This message is always sent no
   * later than once every 500 ms (give or take).
   */
  NetworkTableEntry autoAssistConnectionTest = NetworkTableInstance.getDefault().getTable("Vision")
      .getEntry("autoAssistConnectionTest");

  /*
   * Create a timer that we can use to periodically send a new networktables
   * message.
   */
  Timer backgroundTimer = new Timer();

  protected class AutoAssistPeriodicTask extends TimerTask {
    public void run() {
      /*
       * Update the network table message with a snapshot of the current system time.
       * It doesn't matter what we're sending, as long is the value is changing, which
       * will induce the networktables to send a fresh message.
       */
      autoAssistConnectionTest.setNumber(System.currentTimeMillis());
    }
  }

  AutoAssistPeriodicTask backgroundTask = new AutoAssistPeriodicTask();

  public AutoAssist() {
    /*
     * Start the timer without delay, but have the timer fire every 500 ms to call
     * backgroundTask, which will fire off a new autoAssistConnectionTest message.
     */
    backgroundTimer.schedule(backgroundTask, 0, 500);
  }

  public void disabledInit() {

    /*
     * Turn off the camera light any time the module enters disabled mode because we
     * don't need the light when we're not capable of moving.
     */
    m_autoassistCameraLightControl.set(false);
  }

  public void autonomousInit() {

    /*
     * Turn on the camera light any time the module enters auto mode because we need
     * the light when we're capable of moving and using autoassist.
     * 
     * If there's any sticky faults due to e.g. an intermittent short on the PCM
     * solenoid outputs, attempt to clear it before enabling this solenoid.
     * 
     * If the short circuit doesn't clear, the PCM will log a sticky fault again
     * against this output channel and blacklist it (keep it from turning on to keep
     * the rest of the non-shorted PCM's outputs working.)
     */
    m_autoassistCameraLightControl.clearAllPCMStickyFaults();
    m_autoassistCameraLightControl.set(true);
  }

  public void teleopInit() {

    /*
     * Turn on the camera light any time the module enters teleop mode because we
     * need the light when we're capable of moving and using autoassist.
     * 
     * If there's any sticky faults due to e.g. an intermittent short on the PCM
     * solenoid outputs, attempt to clear it before enabling this solenoid.
     * 
     * If the short circuit doesn't clear, the PCM will log a sticky fault again
     * against this output channel and blacklist it (keep it from turning on to keep
     * the rest of the non-shorted PCM's outputs working.)
     */
    m_autoassistCameraLightControl.clearAllPCMStickyFaults();
    m_autoassistCameraLightControl.set(true);
  }

  @Override
  public void initDefaultCommand() {
  }
}
