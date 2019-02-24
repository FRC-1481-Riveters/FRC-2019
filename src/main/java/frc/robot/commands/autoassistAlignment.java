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
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class autoassistAlignment extends Command {
  private PIDController m_PidControllerLeftRight;
  private double m_setpoint = 0;
  private mysteryPIDSource m_gyroTurning = new mysteryPIDSource(); // fix later
  private double m_output;
  private pidOutput m_pidOutput = new pidOutput();
  private NetworkTableEntry targetErrorEntry;
  private NetworkTableEntry targetProcessingTimeEntry;

  private class mysteryPIDSource implements PIDSource {

    @Override
    public double pidGet() {
      return Robot.m_gyro.getGyroHeading();

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

  private class pidOutput implements PIDOutput {
    @Override
    public void pidWrite(double output) {
      m_output = output;
    }
  }

  public autoassistAlignment() {
    requires(Robot.m_gyro);
    requires(Robot.m_drive);
    m_PidControllerLeftRight = new PIDController(0.2, 0, 0, m_gyroTurning, m_pidOutput);
    NetworkTableInstance ntinst = NetworkTableInstance.getDefault();
    NetworkTable visionTable = ntinst.getTable("Vision");
    targetErrorEntry = visionTable.getEntry("targetError");
    targetProcessingTimeEntry = visionTable.getEntry("targetProcessingTime");
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);

    // does this subtract the processing time from the duration of time
    // when do I put the gyro diary at ?
    // timestamp of when the rpi minus the duration of time
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {

    m_PidControllerLeftRight.setSetpoint(getTargetHeading());

    m_PidControllerLeftRight.enable();
   
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {

    double targetHeading = getTargetHeading();
    m_PidControllerLeftRight.setSetpoint(targetHeading);
    SmartDashboard.putNumber("autoAssistTargetHeading", targetHeading);


    // while driving in joystick you need drive.java and the usage of button 0
    // you need joystick to feed to drive
    // and direction you are facing

    double throttleJoystick = Robot.m_oi.driverController.getRawAxis(RobotMap.driverControllerAxisFrontAndBack);
    Robot.m_drive.driveDirection(throttleJoystick, m_output);

    SmartDashboard.putNumber("autoAssistdriveOutput", m_output);

    SmartDashboard.putNumber("autoAssistCurrentHeading", getCurrentHeading());

  }

  private double getTargetHeading() {

    double Gyroheading = getCurrentHeading();

    /*
     * Compute the timestamp, in terms of the roborio's timebase, when the raspberry
     * pi's vision target was computed.
     */
    long messageTimeStamp = (long) (targetProcessingTimeEntry.getInfo().last_change
        - targetProcessingTimeEntry.getDouble(0));

    double targetheading = getPreviousRobotHeading(messageTimeStamp) + targetErrorEntry.getDouble(Gyroheading);

    return targetheading;
  }

  private double getCurrentHeading() {
    return Robot.m_gyro.getGyroHeading();
  }

  private double getPreviousRobotHeading(long timeStamp) {
    return Robot.m_gyro.gyroDiary(timeStamp);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {

    return false;

  }

  // Called once after isFinished returns true
  @Override
  protected void end() {

    m_PidControllerLeftRight.disable();
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {

    m_PidControllerLeftRight.disable();
  }
}
