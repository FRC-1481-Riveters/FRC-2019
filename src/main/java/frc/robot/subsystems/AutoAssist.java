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

public class AutoAssist extends Subsystem {

  private Solenoid m_autoassistCameraLightControl = new Solenoid(RobotMap.autoAssistCameraPCMId,
      RobotMap.autoAssistCameraSolenoidId);

  public void disabledInit() {

    m_autoassistCameraLightControl.set(false);
  }

  public void autonomousInit() {

    m_autoassistCameraLightControl.set(true);
  }

  public void teleopInit() {
    
    m_autoassistCameraLightControl.set(true);
  }

  @Override
  public void initDefaultCommand() {
  }
}
