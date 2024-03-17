package org.openjfx.PathfindingSimulation;

import java.util.List;
import java.util.Random;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class Player extends Rectangle {
	// Movement variables
	private static final double MAX_SPEED = 1.5;
	private static final double ACCELERATION = 0.2;
	private double verticalMomentum = 0.0;
	private double horizontalMomentum = 0.0;
	
	private static final int PLAYER_WIDTH = 25;
	private static final int PLAYER_BORDER_WIDTH = 3;
	private int xGridPos;
	private int yGridPos;
	
	public Player() {
		setXGridPos(0);
		setYGridPos(0);
	}
	
    // Creates the player object and randomly sets its position
    public boolean initializePlayer(int WINDOW_SIZE_WIDTH, int WINDOW_SIZE_HEIGHT, int GRID_SQUARE_SIZE, List<Rectangle> obstacles, Field field) {
    	Random rand = new Random();
    	int numXSquares = WINDOW_SIZE_WIDTH / GRID_SQUARE_SIZE;
    	int numYSquares = WINDOW_SIZE_HEIGHT / GRID_SQUARE_SIZE;
    	int attemptLimit = 10;
    	int i = 0;
    	boolean failedAttempt = true;
    	
    	// Resets the player object's translate position for respawn
    	setTranslateX(0);
        setTranslateY(0);
    	
    	while(i < attemptLimit) {
    		setXGridPos(rand.nextInt(numXSquares - 10) + 5);
    		setYGridPos(rand.nextInt(10) + (numYSquares - 12));
            int xPos = GRID_SQUARE_SIZE * getXGridPos() + PLAYER_BORDER_WIDTH;
            int yPos = GRID_SQUARE_SIZE * getYGridPos() + PLAYER_BORDER_WIDTH;
            
            setX(xPos - GRID_SQUARE_SIZE);
            setY(yPos - GRID_SQUARE_SIZE);
            setWidth((getPlayerWidth() - PLAYER_BORDER_WIDTH) * 3);
            setHeight((getPlayerWidth() - PLAYER_BORDER_WIDTH) * 3);
            
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
            	setWidth(getPlayerWidth() - 2 * PLAYER_BORDER_WIDTH);
            	setHeight(getPlayerWidth() - 2 * PLAYER_BORDER_WIDTH);
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
	
	public int getPlayerWidth() {
		return PLAYER_WIDTH;
	}
	
	public int getPlayerBorderWidth() {
		return PLAYER_BORDER_WIDTH;
	}
}
