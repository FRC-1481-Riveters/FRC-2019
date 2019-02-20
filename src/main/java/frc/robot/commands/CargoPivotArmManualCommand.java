/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.RobotMap;
import frc.robot.Robot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CargoPivotArmManualCommand extends Command {
  public CargoPivotArmManualCommand() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    requires(Robot.m_cargo_arm);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    // Read the axes of the joysticks
    double throttleUpAxisValue = Robot.m_oi.operatorController.getRawAxis(RobotMap.cargoIntakeJogUpAxis);
    double throttleDownAxisValue = Robot.m_oi.operatorController.getRawAxis(RobotMap.cargoIntakeJogDownAxis);

    /*
     * If the axis for the Up Jog is pressed enough, start jogging the cargo arm up
     */
    if (throttleUpAxisValue > RobotMap.joystickIsActive) {
      SmartDashboard.putNumber("CargoArmUp", throttleUpAxisValue);
      Robot.m_cargo_arm.setTargetPosition(Robot.m_cargo_arm.getTargetPosition() + RobotMap.cargoPivotArmRate);
    }

    /*
     * If the axis for the down Jog is pressed enough, start jogging the cargo arm
     * down only if the Hasmat arm is out of the way.
     */
    if (throttleDownAxisValue > RobotMap.joystickIsActive) {
      SmartDashboard.putNumber("CargoArmDown", throttleDownAxisValue);
 //     if (Robot.m_hazmat_arm.getActualPosition() > RobotMap.hazmatNoCrashPosition) {
        Robot.m_cargo_arm.setTargetPosition(Robot.m_cargo_arm.getTargetPosition() - RobotMap.cargoPivotArmRate);
 //     } else {
 //       Robot.m_hazmat_arm.setTargetPosition(RobotMap.hazmatNoCrashPosition + RobotMap.hazmatNoCrashError);
 //     }
    }

  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
