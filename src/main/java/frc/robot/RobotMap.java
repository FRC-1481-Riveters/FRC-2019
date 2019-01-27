/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
//  drive system constants
  public static int frontLeftMotor = 1;
  public static int frontRightMotor = 2;
  public static int rearLeftMotor = 3;
  public static int rearRightMotor = 4;
  public static int middleLeftMotor = 5;
  public static int middleRightMotor = 6;
 
  public static int chassisSRXMagEncoderLeft = 1;
  public static int chassisSRXMagEncoderRight = 2;

  public static int driverControllerAxisFrontAndBack = 1;
  public static int driverControllerAxisLeftAndRight = 4;

  public static int driverController = 0;
  public static int operatorController = 1;

  public static int usbCamera = 1;
  // Climb Jack System Constants
  public static int climbJack_talon = 7;
  public static int ClimbJackFullyRetracted = 0;
  public static int climbJackMaxExtend = 8000; //not really lol TODO: figure out what  this number is
  public static int climbJackJogRetractedLimit = 0;
  public static int climbJackJogExtendedLimit = 8000; //not really lol TODO: figure out what  this number is
  public static int climbJackJogExtendButton = 6; // right bumper
  public static int climbJackJogRetractButton = 5; // left bumper
  public static int climbJackFullyExtendButton = 3;   // button x 
  public static int climbJackFullyRetractButton =  4; // button y
  public static int climbJackLimitSwitchExtendInput = 1;
  public static int climbJackLimitSwitchRetractInput = 2;
  public static double climbJackSpeed = 10.0;
  public static int climbJackRate = 200;
  public static int climbJackIsExtendedThreshold = 6000;

  public final static int PID_PRIMARY = 0;
  public final static int kTimeoutMs = 30;

  //suction arm constants
  public static int suctionArm_Talon = 8; //TODO: find the true value of these motors
  //define solenoids here
  public static int suctionArmStartingEncoderCount = 1000000; //TODO: find the true value of these motors
  public static int cargoLoadingEncoderCount = 1000000; //TODO: find the true value of these motors
  public static int hatchLoadingEncoderCount = 1000000; //TODO: find the true value of these motors
  public static int cargoEncoderCount1 = 1000000; //TODO: find the true value of these motors
  public static int cargoEncoderCount2 = 1000000; //TODO: find the true value of these motors
  public static int hatchEncoderCount1 = 1000000; //TODO: find the true value of these motors
  public static int hatchEncoderCount2 = 1000000; //TODO: find the true value of these motors
  public static int suctionArmJoystick = 1000000; //TODO: find the right value of joystick needed
  public static int suctionArmUpButton = 1000000; //TODO: correct button value needed
  public static int suctionArmDownButton = 1000000; //TODO: correct button value needed
  public static int cargoSuctionButton = 1000000; //TODO: correct button value needed
  public static int hatchSuctionButton = 1000000; //TODO: correct button value needed
  public static int hazmatRate = 1000000; //TODO: find correct rate

 
}