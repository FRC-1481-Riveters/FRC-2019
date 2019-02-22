/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;
import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.RobotMap;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.CameraServer;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Gyro;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;


public class autoassistAlignment extends Command {
  private PIDController m_PidControllerLeftRight;
  private double m_setpoint = 0;
  private mysteryPIDSource m_gyroTurning = new mysteryPIDSource(); // fix later 
  private double m_output;
  private pidOutput m_pidOutput  =  new pidOutput();
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
    
    //does this subtract the processing time from the duration of time 
    //when do I put the gyro diary at ? 
    //  timestamp of when the rpi minus the duration of time
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    // Robot.m_gyro.timeStamp = targetProcessingTimeEntry.getDouble(targetProcessingTime);
  
   
  m_PidControllerLeftRight.enable();
     // server.setQuality(50);
     // LocalVariableDetection();
      
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    // while driving in joystick you need drive.java and the usage  of button 0 
    // you need joystick to feed to drive 
    // and direction you are facing 
    double Gyroheading = Robot.m_gyro.getGyroHeading();
    double targetheading  = Robot.m_gyro.gyroDiary ((long)(targetProcessingTimeEntry.getInfo().last_change - targetProcessingTimeEntry.getDouble(0))) + targetErrorEntry.getDouble(Gyroheading);
    m_PidControllerLeftRight.setSetpoint(targetheading);
    double throttleJoystick = Robot.m_oi.driverController.getRawAxis(RobotMap.driverControllerAxisFrontAndBack);
    Robot.m_drive.driveDirection((float) throttleJoystick,(float) m_output);
    
        
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    
    return false;
    //call timeout 
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
