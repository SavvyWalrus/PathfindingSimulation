package org.openjfx.PathfindingSimulation;

import java.util.List;
import java.util.Random;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class Player extends Rectangle {
	// Movement variables
	private static final int MAX_SPEED = 150;
	private static final int ACCELERATION = 2;
	private double verticalMomentum = 0.0;
	private double horizontalMomentum = 0.0;
	
	private static final int PLAYER_BORDER_WIDTH = 3;
	private int xGridPos;
	private int yGridPos;
	
	public Player() {
		setXGridPos(0);
		setYGridPos(0);
	}
	
    // Creates the player object and randomly sets its position
    public boolean initializePlayer(int WINDOW_SIZE_WIDTH, int WINDOW_SIZE_HEIGHT, int VISUAL_SQUARE_SIZE, int PATH_GRID_SQUARE_NUM, List<Rectangle> obstacles, Field field) {
    	Random rand = new Random();
    	int numXSquares = WINDOW_SIZE_WIDTH / VISUAL_SQUARE_SIZE;
    	int numYSquares = WINDOW_SIZE_HEIGHT / VISUAL_SQUARE_SIZE;
    	int attemptLimit = 10;
    	int i = 0;
    	boolean failedAttempt = true;
    	
    	// Resets the player object's translate position for respawn
    	setTranslateX(0);
        setTranslateY(0);
    	
    	while(i < attemptLimit) {
    		setXGridPos(PATH_GRID_SQUARE_NUM * (rand.nextInt(numXSquares - 10) + 5));
    		setYGridPos(PATH_GRID_SQUARE_NUM * (rand.nextInt(10) + (numYSquares - 12)));
            int xPos = getXGridPos() / PATH_GRID_SQUARE_NUM * VISUAL_SQUARE_SIZE + PLAYER_BORDER_WIDTH;
            int yPos = getYGridPos() / PATH_GRID_SQUARE_NUM * VISUAL_SQUARE_SIZE + PLAYER_BORDER_WIDTH;
            
            setX(xPos - VISUAL_SQUARE_SIZE);
            setY(yPos - VISUAL_SQUARE_SIZE);
            setWidth((VISUAL_SQUARE_SIZE - PLAYER_BORDER_WIDTH) * 3);
            setHeight((VISUAL_SQUARE_SIZE - PLAYER_BORDER_WIDTH) * 3);
            
            boolean overlaps = false;
            for(Rectangle currentObstacle : obstacles) {
            	if(getBoundsInParent().intersects(currentObstacle.getBoundsInParent())) {
            		overlaps = true;
            		break;
            	}
            }
            
            if(!overlaps) {
            	setX(xPos);
            	setY(yPos);
            	setWidth(VISUAL_SQUARE_SIZE - 2 * PLAYER_BORDER_WIDTH);
            	setHeight(VISUAL_SQUARE_SIZE - 2 * PLAYER_BORDER_WIDTH);
            	setFill(Color.RED);
            	setStroke(Color.BLACK);
            	setStrokeWidth(PLAYER_BORDER_WIDTH);
            	field.getChildren().add(this);
            	failedAttempt = false;
            	break;
            } else {
            	++i;
            }
    	}
    	
    	return !failedAttempt;
    }
	
	public void moveUp(double timestep) {
		increaseVerticalMomentum();
    	setTranslateY(getTranslateY() + getVerticalMomentum() * timestep);
	}
	
	public void moveDown(double timestep) {
		decreaseVerticalMomentum();
    	setTranslateY(getTranslateY() + getVerticalMomentum() * timestep);
	}
	
	public void moveLeft(double timestep) {
		decreaseHorizontalMomentum();
    	setTranslateX(getTranslateX() + getHorizontalMomentum() * timestep);
	}
	
	public void moveRight(double timestep) {
		increaseHorizontalMomentum();
    	setTranslateX(getTranslateX() + getHorizontalMomentum() * timestep);
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
	
	public int getPlayerBorderWidth() {
		return PLAYER_BORDER_WIDTH;
	}
}
