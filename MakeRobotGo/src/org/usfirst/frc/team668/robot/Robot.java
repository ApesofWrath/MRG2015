package org.usfirst.frc.team668.robot;

import org.usfirst.frc.team668.robot.commands.ExampleCommand;
import org.usfirst.frc.team668.robot.subsystems.ExampleSubsystem;

import com.ni.vision.NIVision.Image;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.AxisCamera;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	public static final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
	public static OI oi;
	public static Jaguar jaguar2, jaguar3;
	public static Joystick joystickLeft, joystickRight, joystickCamera;
	public static CANJaguar canJaguarLeft, canJaguarRight;
	public static CANTalon canTalonLeft, canTalonRight;
	public static Servo camServoVert, camServoHor;
	public static Image frame;
	public static AxisCamera camera;
	public static Compressor compressor1;
	public static DoubleSolenoid doubleSol1;

	public static final int INIT_STATE = 0;
	public static final int MOVE_FORWARD_STATE = 1;
	public static final int TURN_RIGHT_STATE = 2;
	public static final int PNEUMATIC_CHANGE_STATE = 3;
	public static final int MOVE_BACK_STATE = 4;
	public static final int TURN_BACK_STATE = 5;

	public long autoTimer = -1;
	public int state = INIT_STATE;
	
	boolean cameraInitialized = false;
	

	Command autonomousCommand;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		oi = new OI();
		// instantiate the command used for the autonomous period
		autonomousCommand = new ExampleCommand();
		SmartDashboard.putString("DB/String 0", "hello world");
		
		joystickLeft = new Joystick(1);
		joystickRight = new Joystick(2);
		joystickCamera = new Joystick(0);

		canTalonLeft = new CANTalon(2);
		canTalonRight = new CANTalon(1);
		

		 camServoHor = new Servo(5);
		 camServoVert = new Servo(4);
		
		if (VisionPractice.initializeCamera() > 0) cameraInitialized = true;
		SmartDashboard.putBoolean("Camera Initialized", cameraInitialized);

//		compressor1 = new Compressor(0);
//		compressor1.setClosedLoopControl(true);
//
//		doubleSol1 = new DoubleSolenoid(1, 1, 2);
	}

	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	public void autonomousInit() {
		// schedule the autonomous command (example)
		if (autonomousCommand != null)
			autonomousCommand.start();

		state = INIT_STATE;
	}

	/**
	 * This function is called periodically during autonomous
	 */

	public void autonomousPeriodic() {
		Scheduler.getInstance().run();

		switch (state) {

		case INIT_STATE:
			canTalonLeft.set(0.0);
			canTalonRight.set(0.0);
			state = MOVE_FORWARD_STATE;
			autoTimer = -1;
			SmartDashboard.putString("DB/String 1", "init");

			break;

		case MOVE_FORWARD_STATE:
			SmartDashboard.putString("DB/String 1", "move forward");
			canTalonLeft.set(-.35);
			canTalonRight.set(.35);
			if (autoTimer == -1)
				autoTimer = System.currentTimeMillis();
			if (System.currentTimeMillis() - autoTimer >= 3000) {

				state = TURN_RIGHT_STATE;

				autoTimer = -1;
			}

			break;

		case TURN_RIGHT_STATE:
			SmartDashboard.putString("DB/String 1", "turn right");
			canTalonRight.set(0.25);
			canTalonLeft.set(0.25);
			if (autoTimer == -1)
				autoTimer = System.currentTimeMillis();
			if (System.currentTimeMillis() - autoTimer >= 1250) {

				canTalonLeft.set(0.0);
				canTalonRight.set(0.0);

				state = PNEUMATIC_CHANGE_STATE;

				autoTimer = -1;
			}

			break;

		}
	}

	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (autonomousCommand != null)
			autonomousCommand.cancel();

		

		// jaguar2 = new Jaguar(2);
		// jaguar3 = new Jaguar(3);

		// canJaguarLeft = new CANJaguar(0); // Parameter is the CAN ID CHANGE
		// THE ID!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// canJaguarRight = new CANJaguar (1); // same as above
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		//
		// frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
		// camera = new AxisCamera("10.6.68.22");

		// jaguar2.set(-0.5);
		// jaguar3.set(0.5);

		PowerDistributionPanel pdp = new PowerDistributionPanel();

		System.out.println("Temperature: " + pdp.getTemperature());
		System.out.println("Voltage " + pdp.getVoltage());
		SmartDashboard.putNumber("Temperature", pdp.getTemperature());
		SmartDashboard.putNumber("Voltage", pdp.getVoltage());
		for (int i = 13; i < 16; i++) {
			System.out.println("Current from channel " + i + " is "
					+ pdp.getCurrent(i));
			SmartDashboard.putNumber("Current from Channel " + i,
					pdp.getCurrent(i));
		}
		/*
		 * SmartDashboard.putNumber("CANJaguarLeft Speed: ",
		 * canJaguarLeft.getSpeed());
		 * SmartDashboard.putNumber("CANJaguarRight Speed: ",
		 * canJaguarRight.getSpeed());
		 * SmartDashboard.putNumber("CANJaguarLeft Temperature: ",
		 * canJaguarLeft.getTemperature());
		 * SmartDashboard.putNumber("CANJaguarRight Temperature: ",
		 * canJaguarRight.getTemperature());
		 * SmartDashboard.putNumber("CANJaguarLeft Current: ",
		 * canJaguarLeft.getOutputCurrent());
		 * SmartDashboard.putNumber("CANJaguarRight Current: ",
		 * canJaguarRight.getOutputCurrent());
		 * SmartDashboard.putNumber("CANJaguarLeft Voltage: ",
		 * canJaguarLeft.getOutputVoltage());
		 * SmartDashboard.putNumber("CANJaguarRight Voltage: ",
		 * canJaguarRight.getOutputVoltage());
		 * 
		 * //jaguar2.set(joystick1.getX()); // X???
		 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		 * //jaguar3.set(joystick2.getX()); // X???
		 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		 */
		Timer.delay(0.1); // 0.1 seconds

	}

	/**
	 * This function is called when the disabled button is hit. You can use it
	 * to reset subsystems before shutting down.
	 */

	public void disabledInit() {

	}

	/**
	 * This function is called periodically during operator control
	 */

	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		// NIVision.Rect rect = new NIVision.Rect(10, 10, 100, 100);
		//
		// //camera moves
		 double moveCamHor = ((joystickCamera.getX() + 1) / 2);
		 double moveCamVert = (1 - ((joystickCamera.getY() + 1) / 2));
		 camServoHor.set(moveCamHor);
		 camServoVert.set(moveCamVert);
		//
		// //camera sees
		// camera.getImage(frame);
		// CameraServer.getInstance().setImage(frame);
		//

		// movement stuff
		double leftSpeed = (joystickLeft.getY());
		double rightSpeed = (joystickRight.getY());

		canTalonLeft.set(leftSpeed);
		canTalonRight.set(-rightSpeed);

//		if (joystickCamera.getRawButton(5)) {
//
//			doubleSol1.set(Value.kForward);
//
//		} else if (joystickCamera.getRawButton(6)) {
//
//			doubleSol1.set(Value.kReverse);
//
//		} else if (joystickCamera.getRawButton(3)) {
//
//			doubleSol1.set(Value.kOff);
//
//		}

		SmartDashboard.putNumber("CANTalon Left Bus Voltage ",
				canTalonLeft.getBusVoltage());

		SmartDashboard.putNumber("CANTalon Right Bus Voltage ",
				canTalonRight.getBusVoltage());

		if (joystickCamera.getRawButton(1) && cameraInitialized) {
			VisionPractice.takePicture("practice");
		}
	}

	/**
	 * This function is called periodically during test mode
	 */

	public void testPeriodic() {
		LiveWindow.run();
	}

}
