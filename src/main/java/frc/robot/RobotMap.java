/*----------------------------------------------------------------------------*/
/* Cspyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
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
  public static int rearLeftMotor = 2;
  public static int middleLeftMotor = 3;
  public static int frontRightMotor = 15;
  public static int rearRightMotor = 14;
  public static int middleRightMotor = 13;
 
  public static int chassisSRXMagEncoderLeft = 1;
  public static int chassisSRXMagEncoderRight = 2;

  public static int driverControllerAxisFrontAndBack = 1;
  public static int driverControllerAxisLeftAndRight = 4;

  public static int driverController = 0;
  public static int operatorController = 1;

  public static int usbCamera = 1;
  // Climb Jack System Constants
  public static int climbJack_talon = 9;
  public static int ClimbJackFullyRetracted = 0;
  public static int climbJackMaxExtend = 38500; //not really lol TODO: figure out what  this number is
  public static int climbJackJogRetractedLimit = 0;
  //public static int climbJackJogExtendedLimit = 40000; //not really lol TODO: figure out what  this number is
  public static int climbJackJogExtendButton = 3; // right trigger
  public static int climbJackJogRetractButton = 2; // left trigger
  public static int climbJackFullyExtendButton = 3;   // button x 
  public static int climbJackFullyRetractButton =  4; // button y
  public static int climbJackLimitSwitchExtendInput = 1;
  public static int climbJackLimitSwitchRetractInput = 2;
  public static double climbJackSpeed = 10.0;
  public static int climbJackRate = 250;
  public static int climbJackIsExtendedThreshold = 6000;

  public final static int PID_PRIMARY = 0;
  public final static int kTimeoutMs = 30;
  public final static double joystickIsActive = 0.1;

  //suction arm constants
  public static int hazmatArm_Talon = 8;
  //define solenoids here
  public static int hazmatPodIntake = 100; //TODO: find the true value of these motors
  public static int hazmatPodLoadStart = 200; //TODO: find the true value of these motors
  public static int hazmatHatchBottom = 300; //TODO: find the true value of these motors
  public static int hazmatRocket1Pod = 400; //TODO: find the true value of these motors
  public static int hazmatRocket2Hatch = 500; //TODO: find the true value of these motors
  public static int hazmatRocket2Pod = 600; //TODO: find the true value of these motors
  public static int hazmatMinHeightAboveCargoArm = 600; //TODO: find the true value of these motors
  public static int hazmatTargetHeightAboveCargoArm = hazmatMinHeightAboveCargoArm + 100;
  /* this is the number of counts of error that hazmat will tolerate before saying its
   * reached its target location.
   */
  public static int hazmatPositionTolerance = 50; 
  public static int hazmatArmUpButton = 4;
  public static int hazmatArmDownButton = 2;
 // public static int hazmatDeliverButton = 3; 
  //public static int hazmatDownDeliverButton = 1;
  public static int hazmatJogExtendButton =  5;
  public static int hazmatJogRetractButton = 6;
  public static int hazmatRate = 8;
  public static int hazmatJogLowerLimit = 0;
  public static int hazmatJogUpperLimit = 1660;
  public static double hazmatSpeed = 0.45;

  public static int operatorControllerAxisFrontAndBack = 5;
  public static int operatorControllerAxisLeftAndRight = 4;
 
// cargo intake arm constants
public static int cargoPivotArmLeft_Talon = 4;
public static int cargoPivotArmRight_Talon = 12;
public static int cargoIntakeJogUpAxis = 2; 
public static int cargoIntakeJogDownAxis = 3; 
public static int cargoPivotArmStartPositionButton = 7; //same as storage 
public static int cargoPivotArmIntakePositionButton = 1; //should be 0
public static int cargoPivotArmClimbPositionButton = 2;
public static int cargoPivotArmStartPosition = 0; //TODO: find  the real number for  this
public static int cargoPivotArmIntakePosition = 100;  //TODO: find the  real number for this 
public static int cargoPivotArmClimbPosition = 200; //TODO:  find the real number for  this
public static int cargoPivotArmRate = 600;  //TODO: find the  real number for this 
public static int cargoPivotMaxRetract = 55000; //TODO: find the real number
public static int cargoPivotMaxExtend = 0; //TODO: find the real number
public static int cargoLimitSwitchExtendInput = 4; //TODO: find the  real input
public static int cargoLimitSwitchRetractInput = 5; //TODO: find the real input
public static int cargoIntakeSpinRate = 100; //TODO: find  the  real rate
public static int CargoTargetPosition = 0;
public static int lastCargoTargetPosition = 0;

//Intake Roller Constants
public static double cargoIntakeSpeedFast = 0.5;
public static double cargoIntakeSpeedSlow = 0.3;
public static double cargoIntakeSpeedReverse = -0.5;
public static double cargoIntakeSpeedZero = 0.0;
public static int cargoIntakeRollersReverseButton = 5;
public static int cargoIntakeRollersFastButton = 6;
public static int cargoIntakeRollersSlowButton = 3;
public static int cargoIntakeArm_Talon = 10;
}