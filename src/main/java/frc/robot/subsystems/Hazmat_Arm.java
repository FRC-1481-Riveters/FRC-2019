/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;

import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.*;
import java.util.ArrayList;
import java.util.Collections;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.EntryListenerFlags;

public class Hazmat_Arm extends Subsystem {
  private static WPI_TalonSRX m_hazmat_arm_talon = new WPI_TalonSRX(RobotMap.hazmatArm_Talon);

  public enum stepDirection {
    stepUp, stepDown
  }

  private int m_targetPosition;

  private ArrayList<Position> hazmatPositions = new ArrayList<>();

  protected class Position {
    public int EncoderCounts;
    public String EncoderCountHazmatWord;

    public Position(int HazmatPosition, String HazmatPositionWord) {
      EncoderCounts = HazmatPosition;
      EncoderCountHazmatWord = HazmatPositionWord;
    }
  }

  private enum PerformanceLevel {
    limited, full
  }

  private PerformanceLevel m_currentLevel = PerformanceLevel.limited;

  private NetworkTableEntry hazmatArmMotor_Kp;
  private NetworkTableEntry hazmatArmMotor_Ki;
  private NetworkTableEntry hazmatArmMotor_Kd;
  private NetworkTableEntry hazmatArmMotor_Kf;

  private NetworkTableEntry hazmatArmMotor_MaxAccel;
  private NetworkTableEntry hazmatArmMotor_MaxVel;

  public Hazmat_Arm() {

    /* Configure the close loop gains for slot 0 */
    m_hazmat_arm_talon.selectProfileSlot(0, 0);

    NetworkTable smartDashNetworkTable = NetworkTableInstance.getDefault().getTable("SmartDashboard");

    /*
     * Get a reference to the four PID calibrations we're using for the Hazmat arm
     * so we can capture and detect changes to these values when they change on the
     * dashboard.
     */
    hazmatArmMotor_Kp = smartDashNetworkTable.getEntry("HazmatArmMotorKp");
    hazmatArmMotor_Ki = smartDashNetworkTable.getEntry("HazmatArmMotorKi");
    hazmatArmMotor_Kd = smartDashNetworkTable.getEntry("HazmatArmMotorKd");
    hazmatArmMotor_Kf = smartDashNetworkTable.getEntry("HazmatArmMotorKf");

    /* Set these NetworkTable signals to their initial values.. */
    hazmatArmMotor_Kp.setDouble(6.0);
    hazmatArmMotor_Ki.setDouble(0.0);
    hazmatArmMotor_Kd.setDouble(0.0);
    hazmatArmMotor_Kf.setDouble(0.0);

    /* ... and read these initial values to set them in the Talon's PID. */
    m_hazmat_arm_talon.config_kP(0, hazmatArmMotor_Kp.getDouble(6.0));
    m_hazmat_arm_talon.config_kI(0, hazmatArmMotor_Ki.getDouble(0.0));
    m_hazmat_arm_talon.config_kD(0, hazmatArmMotor_Kd.getDouble(0.0));
    m_hazmat_arm_talon.config_kF(0, hazmatArmMotor_Kf.getDouble(0.0));
    
    /*
     * Add listeners that will update the Talon's PID's configuration, in the
     * background, when the signal changes over the NetworkTable, e.g. if a client
     * (like a computer) updates the value during e.g. tuning.
     * 
     * These listeners run in the background and sleep until a change is made. Then
     * they wake-up, execute the code (to update the Talon's PID's configuration)
     * then go back to sleep until the value changes again.
     */

    hazmatArmMotor_Kp.addListener(event -> {
      m_hazmat_arm_talon.config_kP(0, SmartDashboard.getNumber("HazmatArmMotorKp", 6.0));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    hazmatArmMotor_Ki.addListener(event -> {
      m_hazmat_arm_talon.config_kI(0, SmartDashboard.getNumber("HazmatArmMotorKi", 0.0));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    hazmatArmMotor_Kd.addListener(event -> {
      m_hazmat_arm_talon.config_kD(0, SmartDashboard.getNumber("HazmatArmMotorKd", 0.0));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    hazmatArmMotor_Kf.addListener(event -> {
      m_hazmat_arm_talon.config_kF(0, SmartDashboard.getNumber("HazmatArmMotorKf", 0.0));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    /*
     * This is the maximum velocity of the arm in units of encoder counts per 100 ms
     * (a decisecond)
     * 
     * The entire range of motion is about 1760 encoder counts. Design for
     * traversing this in 1.2 seconds
     * 
     * That's 1760 counts / 1.2 seconds * 1 second / 10 deciseconds = 147 counts /
     * decisecond
     * 
     */
    m_hazmat_arm_talon.configMotionCruiseVelocity(147);

    /*
     * This is the maximum acceleration of the arm in units of encoder counts per
     * 100 ms (a decisecond)
     * 
     */
    m_hazmat_arm_talon.configMotionAcceleration(200);

    hazmatArmMotor_MaxAccel = smartDashNetworkTable.getEntry("HazmatArmMotorMaxAccel");
    hazmatArmMotor_MaxVel = smartDashNetworkTable.getEntry("HazmatArmMotorMaxVel");

    hazmatArmMotor_MaxAccel.setDouble(200.0);
    hazmatArmMotor_MaxVel.setDouble(147.0);

    hazmatArmMotor_MaxAccel.addListener(event -> {
      m_hazmat_arm_talon.configMotionAcceleration((int) hazmatArmMotor_MaxAccel.getDouble(200));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    hazmatArmMotor_MaxVel.addListener(event -> {
      m_hazmat_arm_talon.configMotionCruiseVelocity((int) hazmatArmMotor_MaxVel.getDouble(147.0));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

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
    hazmatPositions.add(new Position(RobotMap.hazmatPodIntake, "hazmatPodIntake"));
    hazmatPositions.add(new Position(RobotMap.hazmatPodLoadStart, "hazmatPodLoadStart"));
    hazmatPositions.add(new Position(RobotMap.hazmatHatchBottom, "hazmatHatchBottom"));
    hazmatPositions.add(new Position(RobotMap.hazmatRocket1Pod, "hazmatRocket1Pod"));
    hazmatPositions.add(new Position(RobotMap.hazmatRocket2Hatch, "hazmatRocket2Hatch"));
    hazmatPositions.add(new Position(RobotMap.hazmatRocket2Pod, "hazmatRocket2Pod"));

    /*
     * Sort the hazmatPositions list by the value of its positions. This is super
     * important as the stepping function stepToNextIndexedPosition() *requires*
     * that the list be in ascending order, but first, trim down the List's size to
     * exactly the number of elements that have been added. This makes access to the
     * list (later) more efficient.
     */
    hazmatPositions.trimToSize();
    /*
     * Because the objects that make up hazmatPositions, "Positions" don't have
     * accessor functions to read the positions values, just create a lamba function
     * that compares two objects' EncoderCounts values together and let the
     * Collections.sort() function perform its bubble sort on the objects.
     */
    Collections.sort(hazmatPositions, (o1, o2) -> o1.EncoderCounts - o2.EncoderCounts);
  }

  public void stepToNextIndexedPosition(stepDirection direction) {

    /*
     * Check if the system is running in full performance mode. If it isn't, don't
     * allow the tap-up/tap-down functionality, only tolerate jogging, which use
     * small relative movements.
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
        if (hazmatPositions.get(index).EncoderCounts > currentPosition) {
          /*
           * ... it *is* higher than our current position. Move to this new position right
           * away.
           */
          setTargetPosition(hazmatPositions.get(index).EncoderCounts);

          SmartDashboard.putString("HazmatArmTargetName", hazmatPositions.get(index).EncoderCountHazmatWord);
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
        if (hazmatPositions.get(index).EncoderCounts < currentPosition) {
          /*
           * ... it *is* lower than our current position. Move to this new position right
           * away.
           */
          setTargetPosition(hazmatPositions.get(index).EncoderCounts);
          SmartDashboard.putString("HazmatArmTargetName", hazmatPositions.get(index).EncoderCountHazmatWord);
          break;
        }
      }
      break;
    default:
      break;
    }
  }

  public void periodic() {

    SmartDashboard.putNumber("HazmatArmEncoderCounts", getActualPosition());
    SmartDashboard.putNumber("HazmatTargetPositionCounts", getTargetPosition());

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
    return -m_hazmat_arm_talon.getSelectedSensorPosition();
  }

  private void setPerformanceLevel(PerformanceLevel level) {

    switch (level) {
    case limited:
      /*
       * While in limited mode, reduce the maximum output of the arm's Talon so it
       * doesn't damage anything with quick movements or high torque.
       */
      m_hazmat_arm_talon.configPeakOutputForward(0.4); // 0.2
      m_hazmat_arm_talon.configPeakOutputReverse(-0.4); // -0.2

      m_currentLevel = PerformanceLevel.limited;
      break;
    case full:
      /*
       * While in full mode, run the system at the maximum output of the arm's Talon
       * so it has full torque and maximum energy to move quickly.
       */
      m_hazmat_arm_talon.configPeakOutputForward(1.0);
      m_hazmat_arm_talon.configPeakOutputReverse(-1.0);

      m_currentLevel = PerformanceLevel.full;
      break;
    }

  }

  private PerformanceLevel getPerformanceLevel() {
    return m_currentLevel;
  }
}
