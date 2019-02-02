/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.*;
import frc.robot.commands.JackJogExtendCommand;
import frc.robot.commands.JackJogRetractCommand;
import frc.robot.commands.JogExtendHazmatCommand;
import frc.robot.commands.JogRetractHazmatCommand;
import frc.robot.commands.goUpHazmatCommand;
/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
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
  public Joystick driverController = new Joystick(RobotMap.driverController);
  public Joystick operatorController = new Joystick(RobotMap.operatorController);

  private Button ButtonClimbJogExtend = new JoystickButton(operatorController, RobotMap.climbJackJogExtendButton);
  private Button ButtonClimbJogRetract = new JoystickButton(operatorController, RobotMap.climbJackJogRetractButton);
  private Button ButtonHazmatJogExtend = new JoystickButton(operatorController, RobotMap.hazmatArmUpButton);
  private Button ButtonHazmatJogRetract = new JoystickButton(operatorController, RobotMap.hazmatArmDownButton);
  private Button ButtonHazmatUpPosition = new JoystickButton(operatorController, RobotMap.upDeliverHazmatButton);
  private Button ButtonHazmatDownPosition = new JoystickButton(operatorController, RobotMap.downDeliverHazmatButton);

  public OI(){
  ButtonClimbJogExtend.whileHeld(new JackJogExtendCommand());
  ButtonClimbJogRetract.whileHeld (new JackJogRetractCommand());
  ButtonHazmatJogExtend.whileHeld(new JogExtendHazmatCommand());
  ButtonHazmatJogRetract.whileHeld(new JogRetractHazmatCommand());
  ButtonHazmatUpPosition.whileHeld(new goUpHazmatCommand());
  }
}