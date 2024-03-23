package org.openjfx.PathfindingSimulation;

import javafx.scene.shape.Rectangle;

public class Entity extends Rectangle {
	// Movement variables
	private static int MAX_SPEED = 200;
	private static final int ACCELERATION = 1;
	private double cumulativePixelsMoved = 0;
	private double verticalMomentum = 0.0;
	private double horizontalMomentum = 0.0;
	private int visualSquareSize = Configuration.getVisualSquareSize();
	private int pathGridSquareNum = Configuration.getPathGridSquareNum();
	private double currentX;
	private double currentY;
	
	private static final int BORDER_WIDTH = 3;
	private int xGridPos;
	private int yGridPos;
	
	public void increaseVerticalMomentum() {
		setVerticalMomentum(Math.min(-1 * MAX_SPEED, verticalMomentum + ACCELERATION));
	}
	
	public void decreaseVerticalMomentum() {
		setVerticalMomentum(Math.max(MAX_SPEED, verticalMomentum - ACCELERATION));
	}
	
	public void increaseHorizontalMomentum() {
		setHorizontalMomentum(Math.max(MAX_SPEED, horizontalMomentum - ACCELERATION));
	}
	
	public void decreaseHorizontalMomentum() {
		setHorizontalMomentum(Math.min(-1 * MAX_SPEED, horizontalMomentum + ACCELERATION));
	}
	
	public void setXGridPos(int pos) {
		xGridPos = pos;
	}
	
	public int getXGridPos() {
		return xGridPos;
	}
	
	public void setYGridPos(int pos) {
		yGridPos = pos;
	}
	
	public int getYGridPos() {
		return yGridPos;
	}
	
	public void calculateNewGridPos(int XPixelGridPos, int YPixelGridPos) {
		setXGridPos(XPixelGridPos);
		setYGridPos(YPixelGridPos);
	}
	
	public double getMaxSpeed() {
		return MAX_SPEED;
	}
	
	public void setMaxSpeed(int speed) {
		MAX_SPEED = speed;
	}
	
	public double getAcceleration() {
		return ACCELERATION;
	}
	
	public double getVerticalMomentum() {
		return verticalMomentum;
	}
	
	public void setVerticalMomentum(double newMomentum) {
		verticalMomentum = newMomentum;
	}
	
	public double getHorizontalMomentum() {
		return horizontalMomentum;
	}
	
	public void setHorizontalMomentum(double newMomentum) {
		horizontalMomentum = newMomentum;
	}
	
	public int getBorderWidth() {
		return BORDER_WIDTH;
	}
	
	public double getCumulativePixelsMoved() {
		return cumulativePixelsMoved;
	}
	
	public void setCumulativePixelsMoved(double value) {
		cumulativePixelsMoved = value;
	}
	
	public void setCurrentX(double x) {
		currentX = x;
	}
	
	public double getCurrentX() {
		return currentX;
	}
	
	public void setCurrentY(double y) {
		currentY = y;
	}
	
	public double getCurrentY() {
		return currentY;
	}
	
	public void updateGridPos() {
		setXGridPos((int)(currentX / visualSquareSize * pathGridSquareNum));
		setYGridPos((int)(currentY / visualSquareSize * pathGridSquareNum));
	}
	
	public int getVisualSquareSize() {
		return visualSquareSize;
	}
	
	public int getPathGridSquareNum() {
		return pathGridSquareNum;
	}
}
