/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.buttons.*;
import frc.robot.commands.JackJogExtendCommand;
import frc.robot.commands.JackJogRetractCommand;
import frc.robot.commands.HazmatJogExtendCommand;
import frc.robot.commands.HazmatJogRetractCommand;
import frc.robot.commands.HazmatGoUpCommand;
import frc.robot.commands.HazmatGoDownCommand;
import frc.robot.RobotMap;
import frc.robot.commands.CargoArmRollerFast;
import frc.robot.commands.CargoArmRollerReverse;
import frc.robot.commands.CargoArmRollerSlow;
import frc.robot.commands.CargoPivotArmClimbPositionCommand;
import frc.robot.commands.CargoSetPositions;
import frc.robot.commands.VacuumGrabGamePiece;
import frc.robot.commands.VacuumReleaseAllGamePiece;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {

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
    // until it is finished as determined by it's isFinished method.
    // button.whenPressed(new ExampleCommand());

    // Run the command while the button is being held down and interrupt it once
    // the button is released.
    // button.whileHeld(new ExampleCommand());

    // Start the command when the button is released and let it run the command
    // until it is finished as determined by it's isFinished method.
    // button.whenReleased(new ExampleCommand());
    public RumbleTimerJoystick driverController = new RumbleTimerJoystick(RobotMap.driverController);
    public Joystick operatorController = new Joystick(RobotMap.operatorController);

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

    private Button ButtonVacuumGrabGamePiece = new JoystickButton(driverController, RobotMap.vacuumGrabGamePieceButton);
    private Button ButtonVacuumReleaseGamePiece = new JoystickButton(driverController,
            RobotMap.vacuumDropGamePieceButton);

    public void rumbleDriver(long durationMilliseconds) {

        driverController.rumbleTime(durationMilliseconds);
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

        ButtonVacuumGrabGamePiece.whenPressed(new VacuumGrabGamePiece());
        ButtonVacuumReleaseGamePiece.whenPressed(new VacuumReleaseAllGamePiece());
    }
}