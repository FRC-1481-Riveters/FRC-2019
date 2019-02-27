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
import frc.robot.RobotMap;


public class Indicators extends Subsystem {
  private Solenoid m_redLED = new Solenoid(RobotMap.solenoidLEDRed);
  private Solenoid m_blueLED = new Solenoid(RobotMap.solenoidLEDBlue);
  private Solenoid m_greenLED = new Solenoid(RobotMap.solenoidLEDGreen);

  public enum Color {
    off, red, blue, green
  }

  private HashMap<Color, Solenoid> m_LEDs = new HashMap<>();

  public Indicators() {
    m_LEDs.put(Color.red, m_redLED);
    m_LEDs.put(Color.blue, m_blueLED);
    m_LEDs.put(Color.green, m_greenLED);
  }

  /* Turn on the color of the caller's choice by activating the appropriate
   * solenoid outputs.
   */
  public void setIndicator(Color color) {

    try {
      /*
       * Turn off all Solenoids that aren't currently driving the color we want to
       * turn on. Don't turn off a color that's already on to avoid possible flickering.
       * 
       * It's time to turn these LEDs off.
       */
      m_LEDs.forEach((key, value) -> {
        if (key != color) {
          if (m_LEDs.containsKey(color)) {
            m_LEDs.get(key).set(false);
          }
        }
      }

          );
      
      /* Turn on the color we want to display.
       * If the color is the special color "off", don't try turning that on; just leave everything off.
       */

      if (color != Color.off) {
        m_LEDs.get(color).set(true);
      }

    } catch (Exception e) {
      System.out.println(String.format("Tried to set indicator color to %s but couldn't. %s", color.toString(), e.toString()));
    }

  }

  @Override
  public void initDefaultCommand() {

  }
}
