/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import frc.robot.*;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.command.*;
import frc.robot.RobotMap;
import frc.robot.commands.CargoPivotArmJogUpCommand;
import frc.robot.commands.DoNothingCommandCargo;

import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.*;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Add your docs here.
 */
public class Cargo_Arm extends Subsystem {
  public static WPI_TalonSRX m_cargo_arm_left_talon = new WPI_TalonSRX (RobotMap.cargoPivotArmLeft_Talon);
  public static WPI_TalonSRX m_cargo_arm_right_talon = new WPI_TalonSRX (RobotMap.cargoPivotArmRight_Talon);
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
public Cargo_Arm(){
  RobotMap.lastCargoTargetPosition = getActualPosition();
}
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand())
    setDefaultCommand(new DoNothingCommandCargo());
    
  }
  public void setTargetPosition(int CargoTargetPosition) {

    if (RobotMap.CargoTargetPosition < RobotMap.cargoPivotMaxRetract)  {
      CargoTargetPosition = RobotMap.cargoPivotMaxRetract;
    }
    if (CargoTargetPosition > RobotMap.cargoPivotMaxExtend){
      CargoTargetPosition = RobotMap.cargoPivotMaxExtend ;
    }
    m_cargo_arm_left_talon.set(ControlMode.Position, CargoTargetPosition);
      RobotMap.lastCargoTargetPosition = RobotMap.CargoTargetPosition;
    }
  public int getTargetPosition() {

    return RobotMap.lastCargoTargetPosition;
     
      }
  public int getActualPosition() {
  
    return m_cargo_arm_left_talon.getSensorCollection().getQuadraturePosition();
     
      }
}
