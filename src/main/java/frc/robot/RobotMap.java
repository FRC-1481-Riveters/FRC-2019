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

  public static String parametersFileName = new String("parameters.ini");
  // drive system constants
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

  /*
   * This button makes the robot turn slower when the button is pressed. Drive
   * team calls this the "Detail Drive"
   */
  public static int driverControllerDetailDriverButton = 10;
  /*
   * Reduce the drive gain to 60% of the maximum gain to decrease the sensitivity
   * of the robot when it's drive at lower speeds (and the driver releases the
   * drivercontrollerDetailDriverButton)
   */
  public static double detailDriveGain = 0.60;

  public static int driverController = 0;
  public static int operatorController = 1;
  /*
   * Number of seconds from Neutral to Full speed on the drive talons/victors.
   * This limits how fast the drivers can change the speed of the robot and
   * reduces the load on the battery. Units are in seconds. The bigger the value,
   * the more sluggish the robot drives.
   * 
   * See configOpenloopRamp() for more details.
   */
  public static double driveFullThrottleRampTime = 0.025;

  public static int usbCamera = 1;
  // Climb Jack System Constants
  public static int climbJack_talon = 9;
  public static int ClimbJackFullyRetracted = 0;
  public static int climbJackMaxExtend = 53437;
  public static int climbJackJogRetractedLimit = 0;
  public static int climbJackJogExtendAxis = 3; // right trigger
  public static int climbJackJogRetractAxis = 2; // left trigger
  public static int climbJackFullyExtendButton = 3; // button x
  public static int climbJackFullyRetractButton = 4; // button y
  public static int climbJackLimitSwitchExtendInput = 1;
  public static int climbJackLimitSwitchRetractInput = 2;
  public static double climbJackSpeed = 10.0;
  public static int climbJackRate = 250;
  public static boolean climbJackLimitSwitch = false;
  public static int climbJackEndofTravel = 50000;
  public static double climbJackSlowRate = 5.0;

  public final static int PID_PRIMARY = 0;
  public final static int kTimeoutMs = 30;
  public final static double joystickIsActive = 0.1;

  // suction arm constants
  public static int hazmatArm_Talon = 8;
  // define solenoids here
  public static int hazmatPodIntake = 100; // 100 is good according to drive team
  public static int hazmatPodLoadStart = 0; // Cargo arm folded in
  public static int hazmatHatchBottom = 159;
  public static int hazmatHatch1Delivery = 212;
  public static int hazmatRocket1Pod = 492;
  public static int hazmatRocket2Hatch = 1002;
  public static int hazmatRocket2Pod = 1300;
  public static int hazmatMinHeightAboveCargoArm = 600; // TODO: find the true value of these motors
  public static int hazmatTargetHeightAboveCargoArm = hazmatMinHeightAboveCargoArm + 100;
  /*
   * this is the number of counts of error that hazmat will tolerate before saying
   * its reached its target location.
   */
  public static int hazmatPositionTolerance = 50;
  public static int hazmatArmUpButton = 4;
  public static int hazmatArmDownButton = 2;
  // public static int hazmatDeliverButton = 3;
  // public static int hazmatDownDeliverButton = 1;
  public static int hazmatJogExtendButton = 6;
  public static int hazmatJogRetractButton = 5;
  public static int hazmatRate = 8;
  public static int hazmatJogLowerLimit = 0;
  public static int hazmatJogUpperLimit = 1660;
  public static double hazmatSpeed = 0.45;
  public static int hazmatNoCrashPosition = 550;
  public static int hazmatNoCrashError = 50;

  public static int operatorControllerAxisFrontAndBack = 5;
  public static int operatorControllerAxisLeftAndRight = 4;

  // cargo intake arm constants
  public static int cargoPivotArmLeft_Talon = 4;
  public static int cargoPivotArmRight_Talon = 12;
  public static int cargoIntakeJogUpAxis = 2;
  public static int cargoIntakeJogDownAxis = 3;
  public static int cargoPivotArmStartPositionButton = 8; // same as storage
  public static int cargoPivotArmIntakePositionButton = 1;
  public static int cargoPivotArmClimbPositionButton = 3;
  public static int cargoPivotArmStartPosition = 0; // TODO: find the real number for this
  public static int cargoPivotArmIntakePosition = 33000;
  public static int cargoPivotArmClimbPosition = 24000;
  public static int cargoPivotArmRate = 600; // TODO: find the real number for this
  public static int cargoPivotMaxRetract = 60000; // TODO: find the real number
  public static int cargoPivotMaxExtend = 0; // TODO: find the real number
  public static int cargoLimitSwitchExtendInput = 4; // TODO: find the real input
  public static int cargoLimitSwitchRetractInput = 5; // TODO: find the real input
  public static int cargoIntakeSpinRate = 100; // TODO: find the real rate
  public static int CargoTargetPosition = 0;
  public static int lastCargoTargetPosition = 0;
  public static int CargoArmNoCrashPosition = -25000;
  /*
   * Maximum allowed duration in seconds before timing out the the command to
   * allow the default command to run.
   */
  public static double cargoPivotArmMovementTimeout = 1.75;

  // Intake Roller Constants
  public static double cargoIntakeSpeedFast = 1.0;
  public static double cargoIntakeSpeedSlow = 0.5;
  public static double cargoIntakeSpeedReverse = -0.8;
  public static double cargoIntakeSpeedZero = 0.0;
  public static int cargoIntakeRollersReverseButton = 5;
  public static int cargoIntakeRollersFastButton = 6;
  public static int cargoIntakeRollersSlowButton = 3;
  public static int cargoIntakeArm_Talon = 10;

  // Vacuum constants
  public static double vacuumInitialHoldSpeed = 1.0; // %
  public static double vacuumSustainHoldSpeed = 0.2; // %
  public static double vacuumGamePieceDetectedConductance = 0.2; // mhos
  public static int vacuumHatchCoverCANId = 5;
  public static int vacuumCargoCANId = 6;
  public static int solenoidHatchCoverID = 0;
  public static int solenoidCargoID = 1;
  public static int vacuumGrabGamePieceButton = 7;
  public static int vacuumDropGamePieceButton = 8;
  public static long vacuumPumpSpinUpTime = 500; // milliseconds before measuring the pump's impedance
  /*
   * Minimum number of "testForGamePiece()" detections in a row that indicate that
   * the game piece is in place.
   * 
   * This is sampled at about every 20 ms.
   * 
   * (This is the debounce count)
   */
  public static int gamePieceDetectionMinimumCounts = 20;

  public static long vacuumGamePieceDetectedJoystickRumbleTime = 2000; // milliseconds

  public static double cheeseyDriveTrhreshold = 0.5;
  public static double cheeseyDriveFactor = 0.5;
}
