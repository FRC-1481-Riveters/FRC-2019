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

  public enum Color {
    off, red, blue, green, purple, white, lime
  };

  private HashMap<Color, Solenoid> m_LEDs = new HashMap<>();

  public HazmatIndicators() {
    m_LEDs.put(Color.red, m_redHazmatLED);
    m_LEDs.put(Color.blue, m_blueHazmatLED);
    m_LEDs.put(Color.green, m_greenHazmatLED);
    m_LEDs.put(Color.purple, m_purpleHazmatLED);
    m_LEDs.put(Color.white, m_whiteHazmatLED);
    m_LEDs.put(Color.lime, m_limeHazmatLED);
  }

  public void setIndicator(Color color) {

    m_LEDs.forEach((key, value) -> m_LEDs.get(key).set(false));

    if (color != Color.off) {
      m_LEDs.get(color).set(true);
    }

  }

  @Override
  public void initDefaultCommand() {

  }
}