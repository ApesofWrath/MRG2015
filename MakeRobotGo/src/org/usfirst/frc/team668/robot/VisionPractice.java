package org.usfirst.frc.team668.robot;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.AxisCamera;
import edu.wpi.first.wpilibj.image.*;
import edu.wpi.first.wpilibj.Timer;

import java.util.Calendar;
import java.util.Date;

/**
 * Copy of 2014's vision code for vision practice
 */
// we have chosen to use the Axis M1013 camera because it is the newest version
// of axis camera and has the largest field of view
public class VisionPractice {

	// Camera constants used for distance calculation
	static final int Y_IMAGE_RES = 480; // X Image resolution in pixels, should
										// be 120, 240 or 480
	static final double AXIS_M1013 = 49; // Axis M1013
	static final double AXIS_M1011 = 37.4; // Axis M1011
	static final double VIEW_ANGLE = AXIS_M1013;
	static final double PI = 3.1415926535897932384626433832795028841971;

	// Score limits used for target identification
	static final int RECTANGULARITY_LIMIT = 35;
	static final int ASPECT_RATIO_LIMIT = 40;

	// Score limits used for hot target determination
	static final int TAPE_WIDTH_LIMIT = 42;
	static final int VERTICAL_SCORE_LIMIT = 50;
	static final int LR_SCORE_LIMIT = 50;

	// Minimum area of particles to be considered
	static final int AREA_MINIMUM = 150;

	// Maximum number of particles to process
	static final int MAX_PARTICLES = 8;

	static AxisCamera camera; // the axis camera object (connected to the
								// switch)

	public static class Scores {

		double rectangularity;
		double aspectRatioVertical;
		double aspectRatioHorizontal;
	}

	public static class TargetReport {

		int verticalIndex;
		int horizontalIndex;
		boolean Hot;
		double totalScore;
		double leftScore;
		double rightScore;
		double tapeWidthScore;
		double verticalScore;
	};

	public static class ResultReport { // reports results of vision search

		boolean targetExists;
		boolean isHot;
		double distance;

		public ResultReport(boolean isHot, double distance) { // creates a
																// result report
																// that reports
																// a target
			this.isHot = isHot;
			this.distance = distance;
			this.targetExists = true;
		}

		public ResultReport() { // creates a result report that reports no
								// target found
			this.isHot = false;
			this.distance = -1;
			this.targetExists = false;
		}
	}

	public static double initializeCamera() { // returns time elapsed
		Timer t = new Timer();
		t.start();
		try {
			camera = new AxisCamera("10.6.68.20");
			HSLImage image;
			while (t.get() < 10) {
				if (camera.isFreshImage()) {
					image = camera.getImage();
					if (image != null) {
						t.stop();
						return t.get();
					}
				}
			}
			image = camera.getImage();
			if (image != null) {
				t.stop();
				return t.get();
			} else return -1.0;
		}
		
		catch (NIVisionException e) {
			SmartDashboard.putString("Error", e.getMessage());
			return -1.0;
		}
	}

	public static boolean takePicture(String prefix) {
		try {
			Calendar cal = Calendar.getInstance();
			Date d = cal.getTime();
			ColorImage image = camera.getImage();
			image.write("/" + prefix + "img" + d.getTime() + ".bmp");
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}