/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RobotMap;

public class Vacuum extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  private WPI_TalonSRX m_vacuumTalon;
  private String m_name;
  private long m_timeStampOfEnable = 0;

  public Vacuum(int TalonCANID, String name) {

    m_vacuumTalon = new WPI_TalonSRX(TalonCANID);
    m_name = name;

    setName("Vacuum" + name);
  }

  public void holdGamePiece() {

    if (m_timeStampOfEnable == 0) {
      m_timeStampOfEnable = System.currentTimeMillis();
    }
    m_vacuumTalon.set(RobotMap.vacuumInitialHoldSpeed);
  }

  public void releaseGamePiece() {

    m_vacuumTalon.set(0.0);
    m_timeStampOfEnable = 0;
  }

  public boolean isDetectsGamePiece() {

    boolean isDetected = false;

    if (m_timeStampOfEnable > 0 && (System.currentTimeMillis() - m_timeStampOfEnable) > 200) {
      /*
       * If the motor is running for at least 200 ms AND the output current is BELOW 2
       * amps, you probably have a game piece.
       * 
       * If the motor is running for at least 200 ms AND the output current is ABOVE 2
       * amps, you haven't got a game piece.
       * 
       * It's weird. Trust me. This is how it works.
       */

      isDetected = m_vacuumTalon.getOutputCurrent() < RobotMap.vacuumGamePieceDetectedCurrent;
    }

    return isDetected;
  }

  @Override
  public void periodic() {

    if (isDetectsGamePiece()) {
      m_vacuumTalon.set(RobotMap.vacuumSustainHoldSpeed);
    }

    SmartDashboard.putNumber(m_name + "VacuumMotorCurrent", m_vacuumTalon.getOutputCurrent());
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
