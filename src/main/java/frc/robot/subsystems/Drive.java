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
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.robot.commands.DriveCommandJoystick;
import frc.robot.RobotMap;

public class Drive extends Subsystem {
	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	WPI_TalonSRX m_frontLeft = new WPI_TalonSRX(RobotMap.frontLeftMotor);
	WPI_VictorSPX m_midLeft = new WPI_VictorSPX(RobotMap.middleLeftMotor);
	WPI_VictorSPX m_rearLeft = new WPI_VictorSPX(RobotMap.rearLeftMotor);

	// change m_frontleft to a victor to use for last year's robot
	// WPI_VictorSPX m_frontLeft = new WPI_VictorSPX(RobotMap.frontLeftMotor);

	SpeedControllerGroup m_left = new SpeedControllerGroup(m_frontLeft, m_midLeft, m_rearLeft);

	WPI_TalonSRX m_frontRight = new WPI_TalonSRX(RobotMap.frontRightMotor);
	WPI_VictorSPX m_midRight = new WPI_VictorSPX(RobotMap.middleRightMotor);
	WPI_VictorSPX m_rearRight = new WPI_VictorSPX(RobotMap.rearRightMotor);

	// change m_frontright to a victor to use for last year's robot
	// WPI_VictorSPX m_frontRight = new WPI_VictorSPX(RobotMap.frontRightMotor);

	SpeedControllerGroup m_right = new SpeedControllerGroup(m_frontRight, m_midRight, m_rearRight);

	private DifferentialDrive m_drive = new DifferentialDrive(m_left, m_right);

	public Drive() {

		/*
		 * Set each of the motor controllers to "brake" when they're not being driven.
		 * This will cause the robot to slow down quickly when the joysticks are
		 * released and will improve the drive team's ability to control the robot.
		 */
		m_frontLeft.setNeutralMode(NeutralMode.Brake);
		m_midLeft.setNeutralMode(NeutralMode.Brake);
		m_rearLeft.setNeutralMode(NeutralMode.Brake);
		m_frontRight.setNeutralMode(NeutralMode.Brake);
		m_midRight.setNeutralMode(NeutralMode.Brake);
		m_rearRight.setNeutralMode(NeutralMode.Brake);

		/*
		 * Slow down the controllers' rate of change from Neutral to Full speed. This
		 * reduces the load on the battery and keeps the system from browning out when
		 * the driver rapidly changes the robot's drive direction.
		 */
		m_frontLeft.configOpenloopRamp(RobotMap.driveFullThrottleRampTime);
		m_midLeft.configOpenloopRamp(RobotMap.driveFullThrottleRampTime);
		m_rearLeft.configOpenloopRamp(RobotMap.driveFullThrottleRampTime);
		m_frontRight.configOpenloopRamp(RobotMap.driveFullThrottleRampTime);
		m_midRight.configOpenloopRamp(RobotMap.driveFullThrottleRampTime);
		m_rearRight.configOpenloopRamp(RobotMap.driveFullThrottleRampTime);

	}

	public void periodic() {
		// Override me!
	}

	@Override
	public void initDefaultCommand() {
		setDefaultCommand(new DriveCommandJoystick());
	}

	public void driveDirection(double FRSpeed, double turningSpeed, double LRSpeed) {

		/*
		 * Invoke arcadeDrive with squareInputs set to true to improve steering and
		 * control at low speeds with joysticks.
		 */
		// Add some Cheesey Drive
		// When FRSpeed > 0.5, Increase turningSpeed proportionately to improve turning at speed

		

		m_drive.arcadeDrive(turningSpeed, -FRSpeed, true);
	}

	public void driveDirection(double FRSpeed, double turningSpeed) {

		/*
		 * Invoke arcadeDrive with squareInputs set to true to improve steering and
		 * control at low speeds with joysticks.
		 */
		m_drive.arcadeDrive(turningSpeed, -FRSpeed, true);
	}
	
	 public void driveDirectionLinear(double FRSpeed, double turningSpeed) {

	    /*
	     * Invoke arcadeDrive without any quadratic biasing to make the drive
	     * more linear for automated control systems.
	     */
	    m_drive.arcadeDrive(turningSpeed, -FRSpeed, false);
	  }
}
