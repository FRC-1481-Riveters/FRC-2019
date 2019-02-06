/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.motorcontrol.SensorCollection;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.robot.commands.DriveCommandJoystick;
import frc.robot.commands.DriveCommandAuton;
import edu.wpi.first.wpilibj.PIDController;

import edu.wpi.first.wpilibj.command.PIDSubsystem;
import java.util.HashMap;
import frc.robot.RobotMap;

/**
 * Add your docs here.
 */
public class Drive extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

	WPI_TalonSRX m_frontLeft = new WPI_TalonSRX(RobotMap.frontLeftMotor);
	WPI_VictorSPX m_midLeft = new WPI_VictorSPX(RobotMap.middleLeftMotor);
	WPI_VictorSPX m_rearLeft = new WPI_VictorSPX(RobotMap.rearLeftMotor);

	//change m_frontleft to a victor to use for last year's robot
	//WPI_VictorSPX m_frontLeft = new WPI_VictorSPX(RobotMap.frontLeftMotor);

	SpeedControllerGroup m_left = new SpeedControllerGroup(m_frontLeft, m_midLeft, m_rearLeft);

	WPI_TalonSRX m_frontRight = new WPI_TalonSRX(RobotMap.frontRightMotor);
	WPI_VictorSPX m_midRight = new WPI_VictorSPX(RobotMap.middleRightMotor);
	WPI_VictorSPX m_rearRight = new WPI_VictorSPX(RobotMap.rearRightMotor);

	//change m_frontright to a victor to use for last year's robot
	//WPI_VictorSPX m_frontRight = new WPI_VictorSPX(RobotMap.frontRightMotor);

	SpeedControllerGroup m_right = new SpeedControllerGroup(m_frontRight, m_midRight, m_rearRight);

	public DifferentialDrive m_drive = new DifferentialDrive(m_left, m_right);


  public enum driveSide {
		Left, Right
	};


	private static HashMap<driveSide, SensorCollection> m_odometers = new HashMap<>();
	static {
		m_odometers.put(driveSide.Left, new WPI_TalonSRX(RobotMap.chassisSRXMagEncoderLeft).getSensorCollection());
		m_odometers.put(driveSide.Right, new WPI_TalonSRX(RobotMap.chassisSRXMagEncoderRight).getSensorCollection());
  }
  
  public void periodic() {
		// Override me!

	}

	/* Compute the absolute distance travelled per a given side */
	/*public double getCurrentDistance(driveSide side) {
		int ticksOdometer = m_odometers.get(side).getQuadraturePosition();
		/* TODO: Find a better way to do this.
		if (side == driveSide.Right) {
			ticksOdometer =  ticksOdometer * -1;
		}
    return new RobotMap().convertDriveTicksToInches(ticksOdometer);
	}
*/
	@Override
	public void initDefaultCommand() {

		setDefaultCommand(new DriveCommandJoystick());

		

	}
	public void driveDirection(float FRSpeed, float turningSpeed, float LRSpeed) {
		m_drive.arcadeDrive(turningSpeed, FRSpeed);
	}
	public void driveDirection(float FRSpeed, float turningSpeed) {
		m_drive.arcadeDrive( turningSpeed, FRSpeed);
	}
}
