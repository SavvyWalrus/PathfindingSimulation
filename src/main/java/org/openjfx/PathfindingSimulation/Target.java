package org.openjfx.PathfindingSimulation;

import java.util.List;
import java.util.Random;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Target extends Circle {
	private static final int TARGET_BORDER_WIDTH = 3;
	private static double targetRadius;
	private static int visualGridSquareSize;
	private GridNode goalNode = null;
	
	// Movement variables
	private static final double MAX_SPEED = 1.5;
	private static final double ACCELERATION = 0.2;
	private double verticalMomentum = 0.0;
	private double horizontalMomentum = 0.0;
	
	private int xGridPos;
	private int yGridPos;
	
	public Target(int VISUAL_GRID_SQUARE_SIZE, int PATH_GRID_SQUARE_SIZE) {
		visualGridSquareSize = VISUAL_GRID_SQUARE_SIZE;
		targetRadius = (visualGridSquareSize / 2) - (TARGET_BORDER_WIDTH);
	}
	
    // Creates the target and randomly sets its position
    public boolean initializeTarget(int WINDOW_SIZE_WIDTH, int WINDOW_SIZE_HEIGHT, int VISUAL_SQUARE_SIZE, int PATH_GRID_SQUARE_SIZE, List<Rectangle> obstacles, Field field, Grid grid) {
    	Random rand = new Random();
    	int numXSquares = WINDOW_SIZE_WIDTH / VISUAL_SQUARE_SIZE;
    	int attemptLimit = 10;
    	int i = 0;
    	boolean failedAttempt = true;
    	
    	while(i < attemptLimit) {
    		xGridPos = PATH_GRID_SQUARE_SIZE * (rand.nextInt(numXSquares - 10) + 5);
    		yGridPos = PATH_GRID_SQUARE_SIZE * (rand.nextInt(10) + 2);
    		double xPos = xGridPos / PATH_GRID_SQUARE_SIZE * VISUAL_SQUARE_SIZE + VISUAL_SQUARE_SIZE / 2;
            double yPos = yGridPos / PATH_GRID_SQUARE_SIZE * VISUAL_SQUARE_SIZE + VISUAL_SQUARE_SIZE / 2;
            
            setCenterX(xPos);
            setCenterY(yPos);
            setRadius(targetRadius);
            
            boolean overlaps = false;
            for(Rectangle currentObstacle : obstacles) {
            	if(getBoundsInParent().intersects(currentObstacle.getBoundsInParent())) {
            		overlaps = true;
            		break;
            	}
            }
            
            if(!overlaps) {
            	setFill(Color.GOLD);
            	setStroke(Color.BLACK);
            	setStrokeWidth(TARGET_BORDER_WIDTH);
            	field.getChildren().add(this);
            	failedAttempt = false;
            	goalNode = grid.getNode(xGridPos, yGridPos);
            	break;
            } else {
            	++i;
            }
        }
    	
    	return !failedAttempt;
    }
    
    public void moveUp() {
		increaseVerticalMomentum();
    	setTranslateY(getTranslateY() + getVerticalMomentum());
	}
	
	public void moveDown() {
		decreaseVerticalMomentum();
    	setTranslateY(getTranslateY() + getVerticalMomentum());
	}
	
	public void moveLeft() {
		decreaseHorizontalMomentum();
    	setTranslateX(getTranslateX() + getHorizontalMomentum());
	}
	
	public void moveRight() {
		increaseHorizontalMomentum();
    	setTranslateX(getTranslateX() + getHorizontalMomentum());
	}
	
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
	
	public double getMaxSpeed() {
		return MAX_SPEED;
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
	
	public double getTargetRadius() {
		return targetRadius;
	}
	
	public int getTargetBorderWidth() {
		return TARGET_BORDER_WIDTH;
	}
	
	public GridNode getGoalNode() {
		return goalNode;
	}
	
	public void setGoalNode(GridNode newNode) {
		goalNode = newNode;
	}
}
