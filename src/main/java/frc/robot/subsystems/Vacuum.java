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
import frc.robot.Robot;
import edu.wpi.first.wpilibj.Solenoid;

public class Vacuum extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  private WPI_TalonSRX m_vacuumTalon;
  private Solenoid m_ventSolenoid;
  private String m_subsystemName;
  private long m_timeStampOfEnable = 0;

  private long m_timeStampOfDetectedGamePiece = 0;

  public Vacuum(int TalonCANID, int solenoidID, String name) {

    /*
     * Create the talon and solenoid and assign them to their respective references
     * so we can use them later.
     */
    m_vacuumTalon = new WPI_TalonSRX(TalonCANID);
    m_ventSolenoid = new Solenoid(solenoidID);

    m_subsystemName = name;

    /*
     * Set the subsystem name to Vacuum + the configured name. This distinquishes
     * one vacuum from another vacuum.
     * 
     * e.g.: VacuumCargo VacuumHatchCover
     */
    setName("Vacuum" + name);
  }

  public void holdGamePiece() {
    /*
     * When we turn on the vacuums, get a timestamp of the first instant that the
     * vacuum motor turns on. We'll use this timestamp to figure out how long the
     * motor is running later.
     */
    if (m_timeStampOfEnable == 0) {
      m_timeStampOfEnable = System.currentTimeMillis();
    }
    /*
     * Close the venting solenoid so we can start pulling a vacuum with the vacuum
     * motor.
     */
    m_ventSolenoid.set(false);
    /*
     * Turn on the vacuum motor to start pulling a vacuum and grab onto a game
     * piece.
     */
    m_vacuumTalon.set(RobotMap.vacuumInitialHoldSpeed);
  }

  public void releaseGamePiece() {
    /*
     * Turn off the motor to reduce the level of vacuum. We want to drop the game
     * piece.
     */
    m_vacuumTalon.set(0.0);
    /*
     * Open the venting solenoid for this vacuum system to allow the game piece to
     * fall off the suction cup by breaking the vacuum in this system.
     */
    m_ventSolenoid.set(true);
    /*
     * Reset the timestamp to 0 so we can detect the first time we turn on the
     * vacuum motor (to measure the time the motor is on)
     */
    m_timeStampOfEnable = 0;
  }

  public boolean isDetectsGamePiece() {

    boolean isDetected = false;

    if (m_timeStampOfEnable > 0 && (System.currentTimeMillis() - m_timeStampOfEnable) > 200) {
      /*
       * If the motor is running for at least 200 ms AND the output current is BELOW 2
       * amps (vacuumGamePieceDetectedCurrent) , you probably have a game piece.
       * 
       * If the motor is running for at least 200 ms AND the output current is ABOVE 2
       * amps (vacuumGamePieceDetectedCurrent), you haven't got a game piece.
       * 
       * The 200 ms gives the motor on the vacuum pump to spin up so the current
       * measurement from the Talon has had time to settle out to a meaningful number.
       * 
       * It's weird. Trust me. This is how it works.
       */

      isDetected = m_vacuumTalon.getOutputCurrent() < RobotMap.vacuumGamePieceDetectedCurrent;
    }

    return isDetected;
  }

  @Override
  public void periodic() {
    /*
     * Periodically review if the game piece has been captured by this vacuum
     * system. If it has, set the vacuum system to the minimum sustaining vacuum
     * level. This level is enough to hold vacuum on the game piece but is not as
     * noisy as full vacuum speed.
     */
    if (isDetectsGamePiece()) {

      if (m_timeStampOfDetectedGamePiece == 0) {
        m_timeStampOfDetectedGamePiece = System.currentTimeMillis();
        Robot.m_oi.rumbleDriver(true);
      } else {
        if ((System.currentTimeMillis() - m_timeStampOfDetectedGamePiece) > 2000) {
          Robot.m_oi.rumbleDriver(false);
        }
      }
      m_vacuumTalon.set(RobotMap.vacuumSustainHoldSpeed);
      
    } else {
      m_timeStampOfDetectedGamePiece = 0;
    }

    SmartDashboard.putNumber(m_subsystemName + "VacuumMotorCurrent", m_vacuumTalon.getOutputCurrent());
  }

  @Override
  public void initDefaultCommand() {
  }

}
