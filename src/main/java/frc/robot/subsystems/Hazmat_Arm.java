/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;

import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.*;
import java.util.ArrayList;
import java.util.Collections;


public class Hazmat_Arm extends Subsystem {
  private static WPI_TalonSRX m_hazmat_arm_talon = new WPI_TalonSRX(RobotMap.hazmatArm_Talon);


  public enum stepDirection {
    stepUp, stepDown
  }

  private int m_targetPosition;

  private boolean hasRecentlyTrippedFwdLimitSwitch = false;
  private boolean hasRecentlyTrippedRevLimitSwitch = false;
  
  private ArrayList<Integer> hazmatPositions = new ArrayList<>();

  private enum PerformanceLevel {
    limited, full
  }
  
  private PerformanceLevel m_currentLevel = PerformanceLevel.limited;

  public Hazmat_Arm() {

    SmartDashboard.putNumber("HazmatArmMotorKF", 0.0);
    SmartDashboard.putNumber("HazmatArmMotorKp", 6.0);
    SmartDashboard.putNumber("HazmatArmMotorKI", 0.0);
    SmartDashboard.putNumber("HazmatArmMotorKD", 0.0);

    /* Configure the close loop gains for slot 0 */
    m_hazmat_arm_talon.selectProfileSlot(0, 0);
    
    m_hazmat_arm_talon.config_kF(0, SmartDashboard.getNumber("HazmatArmMotorKF", 0.0));
    m_hazmat_arm_talon.config_kP(0, SmartDashboard.getNumber("HazmatArmMotorKp", 6.0));
    m_hazmat_arm_talon.config_kI(0, SmartDashboard.getNumber("HazmatArmMotorKI", 0.0));
    m_hazmat_arm_talon.config_kD(0, SmartDashboard.getNumber("HazmatArmMotorKD", 0.0));

    /*
     * This is the maximum velocity of the arm in units of encoder counts per 100 ms (a decisecond)
     * 
     * The entire range of motion is about 1760 encoder counts.
     * Design for traversing this in 1.2 seconds
     * 
     * That's 1760 counts / 1.2 seconds * 1 second / 10 deciseconds = 147 counts / decisecond
     * 
     */
    m_hazmat_arm_talon.configMotionCruiseVelocity(147);

    /*
     * This is the maximum acceleration of the arm in units of encoder counts per 100 ms (a decisecond)
     * 
     */
    m_hazmat_arm_talon.configMotionAcceleration(200);
    
    m_hazmat_arm_talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

    /* Feedback device of remote talon */
    m_hazmat_arm_talon.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor0);
    /* Quadrature Encoder of current Talon */
    m_hazmat_arm_talon.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.CTRE_MagEncoder_Relative);
    m_hazmat_arm_talon.configNominalOutputForward(0.0);
    m_hazmat_arm_talon.configNominalOutputReverse(0.0);
    m_hazmat_arm_talon.setSensorPhase(true);
    m_hazmat_arm_talon.setInverted(false);


    /*
     * Initialize the arm's performance to a safe output that can't damage itself if
     * it resets in the wrong position and doesn't know where its zero position is.
     * When the arm figures out where it is, the system will increase the arm's
     * speed to maximum performance.
     */
    setPerformanceLevel(PerformanceLevel.full);

    m_targetPosition = getActualPosition();

    /*
     * You can add a new position to the hazmatPosition ArrayList in any order. The
     * list is sorted by the value of the position later, so the order that the
     * positions are added isn't important.
     */
    hazmatPositions.add(RobotMap.hazmatPodIntake);
    hazmatPositions.add(RobotMap.hazmatPodLoadStart);
    hazmatPositions.add(RobotMap.hazmatHatchBottom);
    hazmatPositions.add(RobotMap.hazmatHatch1Delivery);
    hazmatPositions.add(RobotMap.hazmatRocket1Pod);
    hazmatPositions.add(RobotMap.hazmatRocket2Hatch);
    hazmatPositions.add(RobotMap.hazmatRocket2Pod);

    /*
     * Sort the hazmatPositions list by the value of its positions. This is super
     * important as the stepping function stepToNextIndexedPosition() *requires*
     * that the list be in ascending order.
     */
    hazmatPositions.trimToSize();
    Collections.sort(hazmatPositions);
  }

  public void stepToNextIndexedPosition(stepDirection direction) {
    
    /*
     * Check if the system is running in full performance mode. If it isn't, don't
     * allow the tap-up/tap-down functionality, only tolerate jogging, which use small
     * relative movements.
     */
    if (getPerformanceLevel() != PerformanceLevel.full) {
      System.out.println("HazmatArm: Rejected stepping while in low performance mode.");
      return;
    }

    /*
     * Get a snapshot of our current target position. This is where the arm *thinks*
     * it's supposed to go.
     */
    int currentPosition = getTargetPosition();

    /* Determine which way the command is telling us to step; up or down. */
    switch (direction) {
    case stepUp:

      /*
       * Ok. We want to step up to the next fixed position. Try to figure out where
       * the arm is are compared to all the fixed positions we know about. Remember,
       * we might not be sitting right on a preset fixed position. Maybe we jogged to
       * our current position. We could be anywhere.
       * 
       * Search the list of preset positions for the first fixed position that's
       * *higher* than our current target position. When we find that next position
       * (that's higher than where the arm is supposed to be sitting) set our new
       * target position to that new, higher position.
       */

      /*
       * Because the hazmatPosition ArrayList is sorted by position value, start with
       * the smallest value on the list (which is at the beginning), and search up;
       * moving to the next higher value with each increment. That way, we can just
       * stop when we find a position that's higher than our current position.
       */
      for (int index = 0; index < hazmatPositions.size(); ++index) {
        /* Check if this fixed position is higher than our current position... */
        if (hazmatPositions.get(index) > currentPosition) {
          /*
           * ... it *is* higher than our current position. Move to this new position right
           * away.
           */
          setTargetPosition(hazmatPositions.get(index));

          /*
           * Since we've found our new position, and set our arm moving to this new
           * position, we don't need to search any longer for any other positions, so bail
           * out of this for loop with the "break" statement.
           */
          break;
        }
      }
      break;
    case stepDown:

      /*
       * Ok. We want to step down to the next lower fixed position. Try to figure out
       * where the arm is compared to all the fixed positions we know about. Remember,
       * we might not be sitting right on a preset fixed position. Maybe we jogged to
       * our current position. We could be anywhere.
       * 
       * Seach the list of preset positions for the first fixed position that's
       * *lower* than our current target position. When we find that next position
       * (that's lower than where the arm is supposed to be sitting) set our new
       * target position to that new, lower position.
       */

      /*
       * Because the hazmatPosition ArrayList is sorted by position value, start with
       * the largest value in the list (which is at the end), and search down; moving
       * to the next lower value with each decrement. That way, we can just stop when
       * we find a position that's lower than our current position.
       */
      for (int index = hazmatPositions.size() - 1; index >= 0; --index) {
        /* Check if this fixed position is lower than our current position... */
        if (hazmatPositions.get(index) < currentPosition) {
          /*
           * ... it *is* lower than our current position. Move to this new position right
           * away.
           */
          setTargetPosition(hazmatPositions.get(index));
          break;
        }
      }
      break;
    default:
      break;
    }
  }

  public void periodic() {

    m_hazmat_arm_talon.config_kF(0, SmartDashboard.getNumber("HazmatArmMotorKF", 0.0));
    m_hazmat_arm_talon.config_kP(0, SmartDashboard.getNumber("HazmatArmMotorKp", 6.0));
    m_hazmat_arm_talon.config_kI(0, SmartDashboard.getNumber("HazmatArmMotorKI", 0.0));
    m_hazmat_arm_talon.config_kD(0, SmartDashboard.getNumber("HazmatArmMotorKD", 0.0));

    SmartDashboard.putNumber("HazmatArmEncoderCounts", getActualPosition());
    SmartDashboard.putNumber("HazmatTargetPositionCounts", getTargetPosition());

    /*
     * Check if the hazmat_arm has hit its lower limit switch. If it has: 1) Reset
     * the arm's encoder count; this IS the arm's zero point. 2) Increase the arm's
     * performance by switching from the limited PID output limits to the full speed
     * that the arm can support.
     * 
     * Check if the hazmat_arm has hit its upper limit switch. If it has: 1) Reset
     * the arm's encoder count; this IS the arm's upper limit point. 2) Increase the
     * arm's performance by switching from the limited PID output limits to the full
     * speed that the arm can support.
     * 
     * 
     * However, only reset the encoder once per limit switch trip. This helps
     * improve the accuracy of the encoder counts on the Talon as the RoboRIO's lag
     * in processing the limit switch tripping could cause the Talon to constantly
     * reset its encoder counts even when the limit switch isn't tripped.
     * 
     * Use hasRecentlyTrippedRevLimitSwitch and hasRecentlyTrippedRevLimitSwitch to do this.
     */
    if (m_hazmat_arm_talon.getSensorCollection().isRevLimitSwitchClosed()) {

      if (!hasRecentlyTrippedRevLimitSwitch) {
        hasRecentlyTrippedRevLimitSwitch = true;
        
        m_hazmat_arm_talon.getSensorCollection().setQuadraturePosition(RobotMap.hazmatJogLowerLimit, 0);

        /*
         * We've found our zero position. We know exactly where the arm is located.
         * Enable the arm's full performance now that we know it's working properly.
         */
        setPerformanceLevel(PerformanceLevel.full);
      }

    } else {
      hasRecentlyTrippedRevLimitSwitch = false;
    }
    
    if (m_hazmat_arm_talon.getSensorCollection().isFwdLimitSwitchClosed()) {

      if (!hasRecentlyTrippedFwdLimitSwitch) {
        hasRecentlyTrippedFwdLimitSwitch = true;
        
        m_hazmat_arm_talon.getSensorCollection().setQuadraturePosition(RobotMap.hazmatJogUpperLimit, 0);

        /*
         * We've found our upper position. We know exactly where the arm is located.
         * Enable the arm's full performance now that we know it's working properly.
         */
        setPerformanceLevel(PerformanceLevel.full);
      }

    } else {
      hasRecentlyTrippedFwdLimitSwitch = false;
    }

  }

  @Override
  public void initDefaultCommand() {
    /* This subsystem doesn't need a default command */

  }

  public void setTargetPosition(int TargetPosition) {

    int safeTargetPosition = TargetPosition;

    if (getPerformanceLevel() == PerformanceLevel.full) {
      if (TargetPosition < RobotMap.hazmatJogLowerLimit) {
        safeTargetPosition = RobotMap.hazmatJogLowerLimit;
      }

      if (TargetPosition > RobotMap.hazmatJogUpperLimit) {
        safeTargetPosition = RobotMap.hazmatJogUpperLimit;
      }
    }

    m_hazmat_arm_talon.set(ControlMode.MotionMagic, safeTargetPosition);
    m_targetPosition = safeTargetPosition;

  }

  public int getTargetPosition() {

    return m_targetPosition;
  }

  public int getActualPosition() {
    /*
     * This value is converted with a negative sign to switch the way the sensor
     * reports rotation. It's backwards compared to how the code expects the sensor
     * to rotate.
     */
    return -m_hazmat_arm_talon.getSensorCollection().getQuadraturePosition();
  }

  private void setPerformanceLevel(PerformanceLevel level) {

    switch (level) {
    case limited:
      /* While in limited mode, reduce the maximum output of the arm's Talon so it doesn't 
       * damage anything with quick movements or high torque.
       */
      m_hazmat_arm_talon.configPeakOutputForward(0.2);
      m_hazmat_arm_talon.configPeakOutputReverse(-0.2);
      
      m_currentLevel = PerformanceLevel.limited;
      break;
    case full:
      /* While in full mode, run the system at the maximum output of the arm's Talon so it
       * has full torque and maximum energy to move quickly.
       */
      m_hazmat_arm_talon.configPeakOutputForward(0.5);
      m_hazmat_arm_talon.configPeakOutputReverse(-0.5);
      
      m_currentLevel = PerformanceLevel.full;
      break;
    }

  }

  private PerformanceLevel getPerformanceLevel() {
    return m_currentLevel;
  }
}
