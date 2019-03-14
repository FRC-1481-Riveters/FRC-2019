/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RobotMap;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.EntryListenerFlags;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

import edu.wpi.first.wpilibj.Preferences;


public class autoassistAlignment extends Command {
  private PIDController m_PidControllerLeftRight;
  private gyroPIDSource m_gyroTurning = new gyroPIDSource();
  protected double m_output;
  private pidOutput m_pidOutput = new pidOutput();
  private NetworkTableEntry targetInformation;
  private NetworkTableEntry autoAssistPID_Kp;
  private NetworkTableEntry autoAssistPID_Ki;
  private NetworkTableEntry autoAssistPID_Kd;
  private double m_targetHeading;
  private long m_targetHeadingTimestamp;
  private double m_cameraAngleOffset = 0.0;

  protected class gyroPIDSource implements PIDSource {
    @Override
    public double pidGet() {
      return getCurrentHeading();
    }

    @Override
    public PIDSourceType getPIDSourceType() {
      return PIDSourceType.kDisplacement;
    }

    @Override
    public void setPIDSourceType(PIDSourceType pidSource) {
      /* don't care about this */
    }
  }

  protected class pidOutput implements PIDOutput {
    @Override
    public void pidWrite(double output) {
      m_output = output;
    }
  }

  public autoassistAlignment() {
    requires(Robot.m_gyro);
    requires(Robot.m_drive);

    m_PidControllerLeftRight = new PIDController(0.03, 0.00008, 0, m_gyroTurning, m_pidOutput, 0.02);
    NetworkTableInstance ntinst = NetworkTableInstance.getDefault();
    NetworkTable visionTable = ntinst.getTable("Vision");
    targetInformation = visionTable.getEntry("targetInformation");

    NetworkTable smartDashNetworkTable = NetworkTableInstance.getDefault().getTable("SmartDashboard");

    /*
     * Create some controls for the PID controlling the autoassist drive so we can
     * tune them on a real robot.
     */
    autoAssistPID_Kp = smartDashNetworkTable.getEntry("autoAssistPID_Kp");
    autoAssistPID_Ki = smartDashNetworkTable.getEntry("autoAssistPID_Ki");
    autoAssistPID_Kd = smartDashNetworkTable.getEntry("autoAssistPID_Kd");

    /*
     * Initialize the dashboard controls with the current configuration from the PID
     * so they have realistic values.
     */
    autoAssistPID_Kp.setDouble(m_PidControllerLeftRight.getP());
    autoAssistPID_Ki.setDouble(m_PidControllerLeftRight.getI());
    autoAssistPID_Kd.setDouble(m_PidControllerLeftRight.getD());

    /*
     * When the autoAssistPID_Kp value changes, update the PID with the new value
     */
    autoAssistPID_Kp.addListener(event -> {
      m_PidControllerLeftRight.setP(autoAssistPID_Kp.getDouble(0.0));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    /*
     * When the autoAssistPID_Ki value changes, update the PID with the new value
     */
    autoAssistPID_Ki.addListener(event -> {
      m_PidControllerLeftRight.setI(autoAssistPID_Ki.getDouble(0.0));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    /*
     * When the autoAssistPID_Kd value changes, update the PID with the new value
     */
    autoAssistPID_Kd.addListener(event -> {
      m_PidControllerLeftRight.setD(autoAssistPID_Kd.getDouble(0.0));
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    /*
     * When new targetInformation data arrives from the coprocessor, compute the new
     * heading and set the PID's setpoint to this new heading so the robot will
     * start steering toward the vision target.
     */
    targetInformation.addListener(event -> {

      try {
        /*
         * Extract the three numbers from the targetInformation array. For example,
         * we'll get an array of doubles that looks like this:
         * 
         * [3.141568,150.0,15.6]
         * 
         * Where 3.141568 is the heading of the target that the vision system found and
         * 150 is the time the vision system took to process that image and 15.6 is the
         * distance to the target in inches; it's the age of that target heading.
         * 
         * aNumbers[0] = 3.141568
         * 
         * aNumbers[1] = 150.0
         * 
         * aNumbers[2] = 15.6
         */
        double[] aNumbers = new double[3];
        aNumbers = targetInformation.getDoubleArray(new double[] { 0.0, 0.0, 0.0 } /* Use 0 as the default values */);

        /*
         * Copy the values to variables with clearer names.
         */
        double targetHeadingAtTimestamp = aNumbers[0];
        long targetHeadingAge = (long) aNumbers[1];
        double targetDistanceAtTimestamp = aNumbers[2];

        /*
         * Determine the timestamp, in terms of the roborio's clock, of the
         * targetHeading received from the vision processor. Just subtract the age of
         * the targetHeading from the roborio's timestamp (last_change) that represents
         * when the message was received from the vision coprocessor. This should be
         * very close to when the vision coprocessor started working on the image that
         * it used to determine the heading of the target.
         */
        m_targetHeadingTimestamp = targetInformation.getInfo().last_change - targetHeadingAge;

        /*
         * Now look back in time to the heading of the robot (its "pose") at the time
         * the image was taken by the vision coprocessor. Then, simply determine the
         * targetHeading the coprocessor computed + the heading of the robot when it
         * took the picture to figure out the new heading of the robot.
         * 
         * Also, compensate for any offset error that the camara's mount is causing.
         * This is when the camera is not quite perfectly facing directly forward along
         * the axis of the robot.
         */
        m_targetHeading = getPreviousRobotHeading(m_targetHeadingTimestamp) + targetHeadingAtTimestamp + m_cameraAngleOffset;

        /* Tell the PID that's steering the robot where its new heading is. */
        m_PidControllerLeftRight.setSetpoint(m_targetHeading);
        SmartDashboard.putNumber("autoAssistTargetHeading", m_targetHeading);

      } catch (Exception e) {
        System.out.println(String.format("Couldn't parse \"%s\" from vision coprocessor %s",
            targetInformation.getString(""), e.toString()));
      }

    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {

    Robot.m_gyro.resetGyroHeading();
    
    /* Load the latest camara angle offset from the parameters. */
    m_cameraAngleOffset = Preferences.getInstance().getDouble("visionCameraAngleOffset", 0.0);

    m_PidControllerLeftRight.enable();
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {

    // while driving in joystick you need drive.java and the usage of button 0
    // you need joystick to feed to drive
    // and direction you are facing

    double throttleJoystick = Robot.m_oi.driverController.getRawAxis(RobotMap.driverControllerAxisFrontAndBack);
    /*
     * Invoke the linear drive controller that doesn't perform any squaring of the
     * drive commands to make the PID's control of the steering easier to tune.
     * 
     * However, retain the squaring of the throttle joystick to make it easier for
     * the driver to control their speed while driving with the autoassistAlignment.
     */
    Robot.m_drive.driveDirectionLinear(
        Math.pow(throttleJoystick, 2.0) * Math.signum(throttleJoystick) * RobotMap.detailDriveGain, m_output);

    SmartDashboard.putNumber("autoAssistdriveOutput", m_output);

    SmartDashboard.putNumber("autoAssistCurrentHeading", getCurrentHeading());

  }

  protected double getCurrentHeading() {
    return Robot.m_gyro.getGyroHeading();
  }

  private double getPreviousRobotHeading(long timeStamp) {
    return Robot.m_gyro.getGyroHeading();
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {

    return false;

  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    m_PidControllerLeftRight.reset();
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    m_PidControllerLeftRight.reset();
  }
}
