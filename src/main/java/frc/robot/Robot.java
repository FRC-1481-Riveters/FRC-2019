/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;

import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Climb_Jack;
import frc.robot.subsystems.Hazmat_Arm;
import frc.robot.subsystems.Gyro;
import frc.robot.subsystems.Cargo_Arm;
import frc.robot.subsystems.CargoIntakeRoller;
import frc.robot.subsystems.Vacuum;
import frc.robot.subsystems.Indicators;

import frc.robot.commands.DriveOffPlatform;

import frc.robot.commands.*;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {


  public static OI m_oi;
  public static Gyro m_gyro;
  public static Drive m_drive;
  public static Climb_Jack m_climb_jack;
  public static Hazmat_Arm m_hazmat_arm;
  public static Cargo_Arm m_cargo_arm;
  public static CargoIntakeRoller m_CargoIntakeRoller;
  public static Vacuum m_HatchCoverVacuum;
  public static Vacuum m_CargoVacuum;
  public static Indicators m_indicators;


  Command m_autonomousCommand;
  SendableChooser<Command> m_chooser = new SendableChooser<>();

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {

    m_HatchCoverVacuum = new Vacuum(RobotMap.vacuumHatchCoverCANId,RobotMap.solenoidHatchCoverID,"HatchCover");
    m_CargoVacuum = new  Vacuum(RobotMap.vacuumCargoCANId,RobotMap.solenoidCargoID,"Cargo");

    m_indicators = new Indicators();
    m_drive = new Drive();
    m_climb_jack = new Climb_Jack();
    m_hazmat_arm = new Hazmat_Arm();
    m_gyro = new Gyro();
    m_cargo_arm = new Cargo_Arm();
    m_CargoIntakeRoller = new CargoIntakeRoller();
    m_oi = new OI();  //OI must be done after other instantiations
    // chooser.addOption("My Auto", new MyAutoCommand());
    SmartDashboard.putData("Auto mode", m_chooser);

    CameraServer cameraServer = CameraServer.getInstance();
    UsbCamera camera = cameraServer.startAutomaticCapture();
 
    // Set the resolution
    camera.setResolution(160, 120);
    camera.setFPS(30);


    SmartDashboard.putData("autoassistAlignment", new autoassistAlignment());
 
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {

    m_oi.periodic();
  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   * You can use it to reset any subsystem information you want to clear when
   * the robot is disabled.
   */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString code to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional commands to the
   * chooser code above (like the commented example) or additional comparisons
   * to the switch structure below with additional strings & commands.
   */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = new DriveOffThenRegularDriveAuton();
    /*
     * String autoSelected = SmartDashboard.getString("Auto Selector",
     * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
     * = new MyAutoCommand(); break; case "Default Auto": default:
     * autonomousCommand = new ExampleCommand(); break; }v
     */

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.start();
    }
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
