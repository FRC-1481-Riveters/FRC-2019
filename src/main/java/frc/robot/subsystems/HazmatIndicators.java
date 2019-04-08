/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.Solenoid;
import java.util.HashMap;
import java.util.Map;
import frc.robot.RobotMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Add your docs here.
 */
public class HazmatIndicators extends Subsystem {
  private Solenoid m_redHazmatLED = new Solenoid(RobotMap.HazmatIndicatorPCMId, RobotMap.solenoidHazmatLEDRed);
  private Solenoid m_blueHazmatLED = new Solenoid(RobotMap.HazmatIndicatorPCMId, RobotMap.solenoidHazmatLEDBlue);
  private Solenoid m_greenHazmatLED = new Solenoid(RobotMap.HazmatIndicatorPCMId, RobotMap.solenoidHazmatLEDGreen);
  private Solenoid m_purpleHazmatLED = new Solenoid(RobotMap.HazmatIndicatorPCMId, RobotMap.solenoidLEDPurple);
  private Solenoid m_whiteHazmatLED = new Solenoid(RobotMap.HazmatIndicatorPCMId, RobotMap.solenoidLEDWhite);
  private Solenoid m_limeHazmatLED = new Solenoid(RobotMap.HazmatIndicatorPCMId, RobotMap.solenoidLEDLime);

  protected Color m_currentLEDColor = Color.off;
  /*
   * Create a timer that we can use to periodically flash the LEDs with
   * 
   */
  Timer backgroundTimer = new Timer();

  protected class HazmatIndicatorsPeriodicTask extends TimerTask {
    public void run() {
      if (m_currentLEDColor.name().contains("flashing")) {

        m_LEDs.get(m_currentLEDColor).setPulseDuration(0.350);
        m_LEDs.get(m_currentLEDColor).startPulse();
      }
    }
  }

  HazmatIndicatorsPeriodicTask backgroundTask = new HazmatIndicatorsPeriodicTask();

  public enum Color {
    off, red, blue, green, purple, white, lime, red_flashing, blue_flashing, green_flashing, purple_flashing,
    white_flashing, lime_flashing
  };

  private Map<Color, Solenoid> m_LEDs = new HashMap<>();

  public HazmatIndicators() {
    m_LEDs.put(Color.red, m_redHazmatLED);
    m_LEDs.put(Color.blue, m_blueHazmatLED);
    m_LEDs.put(Color.green, m_greenHazmatLED);
    m_LEDs.put(Color.purple, m_purpleHazmatLED);
    m_LEDs.put(Color.white, m_whiteHazmatLED);
    m_LEDs.put(Color.lime, m_limeHazmatLED);
    m_LEDs.put(Color.red_flashing, m_redHazmatLED);
    m_LEDs.put(Color.blue_flashing, m_blueHazmatLED);
    m_LEDs.put(Color.green_flashing, m_greenHazmatLED);
    m_LEDs.put(Color.purple_flashing, m_purpleHazmatLED);
    m_LEDs.put(Color.white_flashing, m_whiteHazmatLED);
    m_LEDs.put(Color.lime_flashing, m_limeHazmatLED);

    /*
     * Start the timer without delay, but have the timer fire every 700 ms to call
     * backgroundTask, which will toggle the hazmat indicator, if required.
     */
    backgroundTimer.schedule(backgroundTask, 0, 700);

  }

  public void setIndicator(Color color) {

    m_LEDs.forEach((key, value) -> m_LEDs.get(key).set(false));
  
    if (color != Color.off) {
      m_LEDs.get(color).set(true);
    }

    m_currentLEDColor = color; 
  }

  @Override
  public void initDefaultCommand() {

  }
}