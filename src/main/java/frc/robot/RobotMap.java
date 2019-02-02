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
  public static int climbJackRate = 100;
  public static int climbJackIsExtendedThreshold = 6000;

  public final static int PID_PRIMARY = 0;
  public final static int kTimeoutMs = 30;

  //suction arm constants
  public static int hazmatArm_Talon = 8; //TODO: find the true value of these motors
  //define solenoids her
  public static int hazmatPodIntake = 100; //TODO: find the true value of these motors
  public static int hazmatPodLoadStart = 200; //TODO: find the true value of these motors
  public static int hazmatHatchBottom = 300; //TODO: find the true value of these motors
  public static int hazmatRocket1Pod = 400; //TODO: find the true value of these motors
  public static int hazmatRocket2Hatch = 500; //TODO: find the true value of these motors
  public static int hazmatRocket2Pod = 600; //TODO: find the true value of these motors
  public static int hazmatArmUpButton = 4;
  public static int hazmatArmDownButton = 5;
  public static int upDeliverHazmatButton = 3; 
  public static int downDeliverHazmatButton = 1;
  public static int hazmatRate = 2000;
  public static int hazmatLimitSwitch = 3; //i added this for you TODO: find the correct number for this
  public static int hazmatJogLowerLimit = 100;
  public static int hazmatJogUpperLimit = 890000;

  public static int operatorControllerAxisFrontAndBack = 5;
  public static int operatorControllerAxisLeftAndRight = 4;
 
// cargo intake arm constants
public static int cargoIntakeArm_Talon = 10;
public static int cargoPivotArm_Talon = 11;
public static int cargoPivotArmButton = 1; //TODO: find out if this is a joystick or button
public static int cargoIntakeAxis = 5; //TODO: find out if this is a joystick or button
public static int cargoPivotArmStoragePosition = 1; //TODO: make sure  this is the real number 
public static int cargoPivotArmClimbPosition = 100; //TODO: find  the real number for  this
public static int cargoPivotArmBallIntakePosition = 200;  //TODO: find the  real number for this 
public static int cargoPivotArmRate = 100;  //TODO: find the  real number for this 
public static int cargoPivotLimitSwtichMaxBack = 0; //TODO: find the real number
public static int cargoPivotLimitSwitchMaxForward = 300; //TODO: find the real number
public static int cargoIntakeSpinRate = 100; //TODO: find  the  real rate


}