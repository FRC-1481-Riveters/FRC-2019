/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.*;
import frc.robot.commands.HazmatJogExtendCommand;
import frc.robot.commands.HazmatJogRetractCommand;
import frc.robot.commands.HazmatGoUpCommand;
import frc.robot.commands.HazmatGoDownCommand;
import frc.robot.RobotMap;
import frc.robot.commands.CargoArmRollerFast;
import frc.robot.commands.CargoArmRollerReverse;
import frc.robot.commands.CargoArmRollerSlow;
import frc.robot.commands.CargoSetPositions;
import frc.robot.commands.CargoPivotArmIntakePositionCommand;
import frc.robot.commands.CargoPivotArmStartPositionCommand;
import frc.robot.commands.VacuumGrabGamePiece;
import frc.robot.commands.VacuumReleaseAllGamePiece;
import frc.robot.commands.autoassistAlignment;
import frc.robot.commands.GameOverCommand;
import frc.robot.commands.HazmatCalibrationCommand;
import edu.wpi.first.wpilibj.buttons.InternalButton;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {

    private long m_triggerHazmatCalibrationTimeStamp = 0;

    public class RumbleTimerJoystick extends Joystick {

        private long m_timeStampTimeout;

        public RumbleTimerJoystick(int port) {
            super(port);
        }

        public void rumbleTime(long durationMilliseconds) {

            long newTimestamp = durationMilliseconds + System.currentTimeMillis();

            m_timeStampTimeout = Math.max(m_timeStampTimeout, newTimestamp);

            setRumble(RumbleType.kLeftRumble, 1.0);
            setRumble(RumbleType.kRightRumble, 1.0);

        }

        public void periodic() {
            if (m_timeStampTimeout != 0 && System.currentTimeMillis() > m_timeStampTimeout) {
                setRumble(RumbleType.kLeftRumble, 0);
                setRumble(RumbleType.kRightRumble, 0);
                m_timeStampTimeout = 0;
            }
        }
    }

    public void periodic() {
        driverController.periodic();
        operatorController.periodic();

        /*
         * This is how you can trigger Commands when 2 real buttons are pressed (or
         * something like a joystick axis must be at least a certain minimum value, e.g.
         * > 10%.)
         * 
         * Determine if the TriggerHazmatCalibration button is being pressed. This
         * button is an InternalButton; it doesn't exist on any real device, like a
         * joystick. It's a virtual button. It's triggered when both the Hazmat step up
         * AND step down buttons are pressed for at least
         * hazMatOIEmergencyRecoveryDetectionTime ms long (requiring both buttons to be
         * held, and held for a length of time keeps the operator from accidentally
         * entering Hazmat calibration mode while they're driving around and messing up
         * a perfectly calibrated Hazmat arm.)
         * 
         * 
         */

        if (operatorController.getRawButton(RobotMap.hazmatArmUpButton)
                && operatorController.getRawButton(RobotMap.hazmatArmDownButton)) {

            /*
             * Both buttons are being held. If this is the first time the buttons have been
             * pressed simultaneously, save a copy of the current system timestamp. We'll
             * use this timestamp later to determine how long the buttons have been held.
             */

            if (m_triggerHazmatCalibrationTimeStamp == 0) {
                /*
                 * This is the first time that periodic() has run since both buttons were
                 * pressed simultaneously. Get a copy of the timestamp so we can use it later to
                 * determine how long both buttons have been pressed.
                 */
                m_triggerHazmatCalibrationTimeStamp = System.currentTimeMillis();
            }
        } else {
            /*
             * The buttons aren't being held simultaneously right now. Reset the timestamp
             * so we're ready for the next time the operator presses both buttons at the
             * same time.
             */
            m_triggerHazmatCalibrationTimeStamp = 0;
        }

        /*
         * If both buttons have been held for at least
         * hazMatOIEmergencyRecoveryDetectionTime ms, set the virtual InternalButton
         * TriggerHazmatCalibration to pressed. The InternalButton logic does the rest
         * to call the calibration command.
         * 
         * If the m_triggerHazmatCalibrationTimeStamp is 0, the buttons aren't being
         * held together at all. Set the pressed() interface to false.
         * 
         * If the time since the first timestamp of the first time we pressed both
         * buttons together isn't more than hazMatOIEmergencyRecoveryDetectionTime
         * earlier than the current time (i.e. we haven't waited long enough to be sure
         * that the operator really really wanted to press both buttons) set the
         * pressed() interface to false.
         */
        TriggerHazmatCalibration.setPressed(m_triggerHazmatCalibrationTimeStamp > 0 && (System.currentTimeMillis()
                - m_triggerHazmatCalibrationTimeStamp) > RobotMap.hazMatOIEmergencyRecoveryDetectionTime);

    }
    //// CREATING BUTTONS
    // One type of button is a joystick button which is any button on a
    //// joystick.
    // You create one by telling it which joystick it's on and which button
    // number it is.
    // Joystick stick = new Joystick(port);
    // Button button = new JoystickButton(stick, buttonNumber);

    // There are a few additional built in buttons you can use. Additionally,
    // by subclassing Button you can create custom triggers and bind those to
    // commands the same as any other Button.

    //// TRIGGERING COMMANDS WITH BUTTONS
    // Once you have a button, it's trivial to bind it to a button in one of
    // three ways:

    // Start the command when the button is pressed and let it run the command
    // until it is finished as determined by its isFinished method.
    // button.whenPressed(new ExampleCommand());

    // Run the command while the button is being held down and interrupt it once
    // the button is released.
    // button.whileHeld(new ExampleCommand());

    // Start the command when the button is released and let it run the command
    // until it is finished as determined by its isFinished method.
    // button.whenReleased(new ExampleCommand());
    public RumbleTimerJoystick driverController = new RumbleTimerJoystick(RobotMap.driverController);
    public RumbleTimerJoystick operatorController = new RumbleTimerJoystick(RobotMap.operatorController);

    private Button ButtonHazmatJogExtend = new JoystickButton(operatorController, RobotMap.hazmatJogExtendButton);
    private Button ButtonHazmatJogRetract = new JoystickButton(operatorController, RobotMap.hazmatJogRetractButton);
    private Button ButtonHazmatUpPosition = new JoystickButton(operatorController, RobotMap.hazmatArmUpButton);
    private Button ButtonHazmatDownPosition = new JoystickButton(operatorController, RobotMap.hazmatArmDownButton);

    private Button ButtonCargoStartPosition = new JoystickButton(operatorController,
            RobotMap.cargoPivotArmStartPositionButton);
    private Button ButtonCargoIntakePosition = new JoystickButton(operatorController,
            RobotMap.cargoPivotArmIntakePositionButton);
    private Button ButtonCargoClimbPosition = new JoystickButton(operatorController,
            RobotMap.cargoPivotArmClimbPositionButton);
    private Button ButtonCargoIntakeRollersReverse = new JoystickButton(driverController,
            RobotMap.cargoIntakeRollersReverseButton);
    private Button ButtonCargoIntakeRollersFast = new JoystickButton(driverController,
            RobotMap.cargoIntakeRollersFastButton);
    private Button ButtonCargoIntakeRollersSlow = new JoystickButton(driverController,
            RobotMap.cargoIntakeRollersSlowButton);

    private InternalButton TriggerHazmatCalibration = new InternalButton();

    private Button ButtonVacuumGrabGamePiece = new JoystickButton(driverController, RobotMap.vacuumGrabGamePieceButton);
    private Button ButtonVacuumReleaseGamePiece = new JoystickButton(driverController,
            RobotMap.vacuumDropGamePieceButton);

    private Button ButtonAutoassistVision = new JoystickButton(driverController, RobotMap.autoassistVisionButton);

    private Button ButtonGameOver = new JoystickButton(driverController, RobotMap.gameOverButton);

    public void rumbleDriver(long durationMilliseconds) {

        driverController.rumbleTime(durationMilliseconds);
    }

    public void rumbleOperator(long durationMilliseconds) {
        operatorController.rumbleTime(durationMilliseconds);
    }

    public OI() {

        ButtonHazmatJogExtend.whileHeld(new HazmatJogExtendCommand());
        ButtonHazmatJogRetract.whileHeld(new HazmatJogRetractCommand());

        ButtonHazmatUpPosition.whenPressed(new HazmatGoUpCommand());
        ButtonHazmatDownPosition.whenPressed(new HazmatGoDownCommand());

        ButtonCargoIntakeRollersReverse.whileHeld(new CargoArmRollerReverse());
        ButtonCargoIntakeRollersFast.whileHeld(new CargoArmRollerFast());
        ButtonCargoIntakeRollersSlow.whileHeld(new CargoArmRollerSlow());
        ButtonCargoClimbPosition.whenPressed(new CargoSetPositions());
        ButtonCargoIntakePosition.whenPressed(new CargoPivotArmIntakePositionCommand());
        ButtonCargoStartPosition.whenPressed(new CargoPivotArmStartPositionCommand());

        ButtonVacuumGrabGamePiece.whenPressed(new VacuumGrabGamePiece());
        ButtonVacuumReleaseGamePiece.whenPressed(new VacuumReleaseAllGamePiece());

        ButtonAutoassistVision.whileHeld(new autoassistAlignment());

        ButtonGameOver.whenPressed(new GameOverCommand());

        TriggerHazmatCalibration.whileHeld(new HazmatCalibrationCommand());
    }
}