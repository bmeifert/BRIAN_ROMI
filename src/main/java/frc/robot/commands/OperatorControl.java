// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.Drivetrain;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.commands.JoystickWrapper;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;

public class OperatorControl extends CommandBase {
  private final Drivetrain m_drivetrain;
  // Assumes a gamepad plugged into channnel 0
  private final Joystick m_controller = new Joystick(0);

  public static enum DriveAutos{Drive, ButtonA, ButtonB, ButtonX, ButtonY};
  public static DriveAutos driveState=DriveAutos.Drive;
  public static int count=0;
  public static double start=0;
  
  public OperatorControl(Drivetrain drivetrain) {
        m_drivetrain = drivetrain;
        addRequirements(drivetrain);
    }      

    // Called every tick (20ms)
	@SuppressWarnings("static-access")
	@Override
	public void execute() {
        // START CONTROL SETUP
		// Get stick inputs
		JoystickWrapper driveJoystick = new JoystickWrapper(m_controller, 0.05);
        // END CONTROLS SETUP

        switch (driveState) {
            case Drive: {
                //System.out.println("Drive State Drive");
                if (driveJoystick.isAButton()) {
                    driveState=DriveAutos.ButtonA;
                    m_drivetrain.resetGyro();
                    count=20;
                } else if (driveJoystick.isBButton()) {
                    driveState=DriveAutos.ButtonB;
                    m_drivetrain.resetGyro();
                    count=20;
                } else if (driveJoystick.isYButton()) {
                    driveState=DriveAutos.ButtonY;
                    m_drivetrain.resetEncoders();
                    m_drivetrain.resetGyro();
                    start=m_drivetrain.getGyroAngleX();
                } else {
                    double forward = driveJoystick.getLeftStickY();
                    double turn = driveJoystick.getRightStickX();
                    if (forward > -0.1 && forward <= 0.1 ) {
                        forward=0;
                    }
                    if (turn > -0.1 && turn <= 0.1 ) {
                        turn=0;
                    }
                    m_drivetrain.arcadeDrive(forward, turn);
                }
                break;
            }
            case ButtonA: {
                if (driveJoystick.isXButton()) {
                    driveState=DriveAutos.Drive;                
                }    
                count--;
                if (count == 0) {
                    start=m_drivetrain.getGyroAngleX();            
                }
                if (count > 0) break;
                double currz=m_drivetrain.getGyroAngleZ();
                System.out.println("Drive State ButtonA, start " + start + ", currz " + currz);
                if (currz >= start+88) {
                    driveState=DriveAutos.Drive;
                    m_drivetrain.arcadeDrive(0,0);
                } else {
                    if (currz >= start + 55) {
                        m_drivetrain.arcadeDrive(0,.35);
                    } else {
                        m_drivetrain.arcadeDrive(0,.5);
                    }                           
                }
                break;
            }
            case ButtonB: {
                if (driveJoystick.isXButton()) {
                    driveState=DriveAutos.Drive;                
                }    
                count--;
                if (count == 0) {
                    start=m_drivetrain.getGyroAngleX();            
                }
                if (count > 0) break;
                double currz=m_drivetrain.getGyroAngleZ();
                System.out.println("Drive State ButtonB, start " + start + ", currz " + currz);
                if (currz <= start-88) {
                    driveState=DriveAutos.Drive;
                    m_drivetrain.arcadeDrive(0,0);
                } else {
                    if (currz <= start - 55) {
                        m_drivetrain.arcadeDrive(0,-.35);
                    } else {
                        m_drivetrain.arcadeDrive(0,-.5);
                    }                          
                }
                break;
            }
            case ButtonY: {
                if (driveJoystick.isXButton()) {
                    driveState=DriveAutos.Drive;                
                }    
                if (Math.abs(m_drivetrain.getAverageDistanceInch()) >= 18) {
                    driveState=DriveAutos.Drive;                
                    m_drivetrain.arcadeDrive(0,0);
                } else {
                    m_drivetrain.arcadeDrive(.75,0);
                    System.out.println("Drive State ButtonY, " + m_drivetrain.getAverageDistanceInch());
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {}

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {}

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
      return false;
    }

}
