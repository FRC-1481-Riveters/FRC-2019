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


/**
 * Add your docs here.
 */
public class Indicators extends Subsystem {
  private Solenoid m_redLED = new Solenoid(RobotMap.solenoidLEDRed);
  private Solenoid m_blueLED = new Solenoid(RobotMap.solenoidLEDBlue);
  private Solenoid m_greenLED = new Solenoid(RobotMap.solenoidLEDGreen);

  public enum Color {
    off, red, blue, green
  }

  private Map<Color, Solenoid> m_LEDs = new HashMap<>();

  public Indicators() {
    m_LEDs.put(Color.red, m_redLED);
    m_LEDs.put(Color.blue, m_blueLED);
    m_LEDs.put(Color.green, m_greenLED);
  }

  public void setIndicator(Color color) {

    m_LEDs.forEach((key, value) -> m_LEDs.get(key).set(false));

    if (color != Color.off) {
      /* Before turning on this LED's solenoid, clear any sticky faults that might
       * have been detected in the past.
       * 
       * This will help the system update the solenoid channel "black list" and, if the
       * solenoid channel had been shorted in the past, and the short has been cleared now,
       * the LED will turn on.
       */
      m_LEDs.get(color).clearAllPCMStickyFaults();
      m_LEDs.get(color).set(true);
    }
   
  }

  @Override
  public void initDefaultCommand() {

  }
}