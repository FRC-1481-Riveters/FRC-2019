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
       * This number is used by the RPi to synchronize its internal date clock and
       * timestamp its video feed (and any log messages that are saved).
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
  }

  public void autonomousInit() {
  }

  public void teleopInit() {
  }

  @Override
  public void initDefaultCommand() {
  }

  public void setAssistStatus(boolean running) {

    /* The autoassist status has changed. Take appropriate steps to support this
     * command in its new state (either running [running = true] or stopped [running = false])
     */
    
    
    if (running) {
      /* The autoassist is running. Enable the autoassist light because it helps
       * the camera see the reflective tape targets.
       */
      setAssistLightState(true);
    } else {
      
      /* The autoassist isn't running. Disable the autoassist light, because we don't
       * need it any longer, and it's very bright, which could confuse other robots' vision
       * systems (if we're defending) or irritate the volunteers who have to look at it 
       * when it's on.
       */
      setAssistLightState(false);
    }
  }
  
  private void setAssistLightState(boolean state) {

    /*
     * If we're trying to turn on the autoassist light, clear any faults on this
     * PCM. These faults might have been due to a temporary short on the PCM that's
     * been cleared already. If we don't clear this sticky fault, the light might
     * not turn on because the PCM will have placed it on its "black list" to keep
     * the output driver from shorting out alllllll the outputs because of this
     * output's previous shorted state.
     */
    if (state == true) {
      m_autoassistCameraLightControl.clearAllPCMStickyFaults();
    }

    m_autoassistCameraLightControl.set(state);
  }
}
