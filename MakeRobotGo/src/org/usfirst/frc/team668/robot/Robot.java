//Test Code for a Robot Named Sarah: The Sequel
//Dedicated to Jacob Snyder
//Written by the Programming Team of 668, 2015

//DEAR FUTURE: THIS IS WHAT WE KNOW SO FAR

package org.usfirst.frc.team668.robot;

import org.usfirst.frc.team668.robot.commands.ExampleCommand;
import org.usfirst.frc.team668.robot.subsystems.ExampleSubsystem;

import com.ni.vision.NIVision.Image;

import edu.wpi.first.wpilibj.Encoder;
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

import java.util.ArrayList;

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
	public static Encoder encoder1;
	public static Encoder encoder2;

	public static final int INIT_STATE = 0;
	public static final int DO_SEQUENCE_STATE = 1;
	public static final int PNEUMATIC_CHANGE_STATE = 2;
	public static final int DO_SEQUENCE_DIFFERENTLY_STATE = 3;
	public static final int WAIT_FOR_END_STATE = 4; 

	public long autoTimer = -1;
	public int state = INIT_STATE;
	
	boolean cameraInitialized = false;
	

	Command autonomousCommand;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	
	public void autonSequence(double forwardDistance, double turnDistance, double motorSpeed) {
		ArrayList<Double> fullRightDistance = new ArrayList<Double>(5);
		ArrayList<Double> fullLeftDistance = new ArrayList<Double>(5);
		
		double forwardVal = forwardDistance; // The normal distance to move forwards
		double turnVal = turnDistance; // No, you don't need separate left and right turns.
		boolean doneRight = false, doneLeft = false;
		
//		fullRightDistance.add(0,forwardVal); fullLeftDistance(0,forwardVal); // Forward
//		fullRightDistance.add(1,turnVal); fullLeftDistance(1,-turnVal); // Turn Left
//		fullRightDistance.add(2,forwardVal); fullLeftDistance(2,forwardVal);
//		fullRightDistance.add(3,-turnVal); fullLeftDistance(3,turnVal); // Turn Right
//		fullRightDistance.add(4,forwardVal); fullLeftDistance(4,forwardVal);
		
		for(int i = 0; i < 5; i++) { //If we add extra steps, change necessary
			encoder1.reset();
			encoder2.reset();
			
			while(!doneRight && !doneLeft) {
				if (encoder1.getDistance() < fullRightDistance.get(i)){ // Motors will only be set if we want the final distance to be forwards
					canTalonRight.set(motorSpeed);
				} else {
					canTalonRight.set(0.0);
					doneRight = true;
				}
				
				if (encoder2.getDistance() < fullLeftDistance.get(i)){
					canTalonLeft.set(motorSpeed);
				} else {
					canTalonLeft.set(0.0);
					doneLeft = true;
				}
			}
			
			doneRight = false;
			doneLeft = false;
			
		}
	}
	
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

		SmartDashboard.putBoolean("Camera Initialized", false);
		double c = VisionPractice.initializeCamera();
		if (c > 0) cameraInitialized = true;
		SmartDashboard.putBoolean("Camera Initialized", cameraInitialized);
		SmartDashboard.putNumber("Init Time", c);

		compressor1 = new Compressor(10); // 10 is CANfirmed (check at 10.6.68.21) That was Ari with the CAN! Maybe he should kick the CAN. Ari aCAN!
		compressor1.setClosedLoopControl(true); // we don't know what this is
 
		doubleSol1 = new DoubleSolenoid(10, 1, 0); // values confirmed to be correct
		
		encoder1 = new Encoder(1, 2);
		encoder2 = new Encoder(3, 4);
		
		encoder1.reset();
		encoder2.reset();
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
//			state = MOVE_FORWARD_STATE;
			SmartDashboard.putString("DB/String 1", "init");

			break;

		case DO_SEQUENCE_STATE:
			
			SmartDashboard.putString("DB/String 1", "sequency");
			autonSequence(10.0,10.0,0.333);
			state = PNEUMATIC_CHANGE_STATE;
			
			break;

		case PNEUMATIC_CHANGE_STATE:
			
			SmartDashboard.putString("DB/String 1", "pneumatics");
			for (int i=0; i<5; i++) { //this makes the repeats! it's not necessaries!<
//				doubleSol1.set("Forward");
				Timer.delay(2);
//				doubleSol1.set("Reverse");
				Timer.delay(2);
			}
			
			state = DO_SEQUENCE_DIFFERENTLY_STATE;
//			doubleSol1.set("Off")

			break;
			
		case DO_SEQUENCE_DIFFERENTLY_STATE:

			SmartDashboard.putString("DB/String 1", "sequency");
			autonSequence(20.0,10.0,0.333);
			state = WAIT_FOR_END_STATE;
			
			break;

		case WAIT_FOR_END_STATE:
			
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
		 
//		 double rateRight = encoder1.getRate(); //gets rate
//		 double rateLeft = encoder2.getRate();
//		 
//		 double distanceRight = encoder1.getDistance(); // gets distance
//		 double distanceLeft = encoder2.getDistance();
//		 
//		 boolean directionRight = encoder1.getDirection(); // gets direction
//		 boolean directionLeft = encoder2.getDirection();
//		 
//		 SmartDashboard.putNumber("Right Motor Speed", rateRight);
//		 SmartDashboard.putNumber("Left Motor Speed", rateLeft);
//		 
//		 SmartDashboard.putNumber("Right Motor Distance", distanceRight);
//		 SmartDashboard.putNumber("Left Motor Speed", distanceLeft);
//		 
//		 SmartDashboard.putBoolean("Right Direction", directionRight);
//		 SmartDashboard.putBoolean("Left Motor Direction", directionLeft);
		 
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

//LIVE LONG AND PROSPER

//The End.