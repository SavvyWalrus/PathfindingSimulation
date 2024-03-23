package org.openjfx.PathfindingSimulation;

import java.util.List;
import java.util.Random;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class Player extends Rectangle {
	// Movement variables
	private static int MAX_SPEED = 200;
	private static final int ACCELERATION = 1;
	private double cumulativePixelsMoved = 0;
	private double verticalMomentum = 0.0;
	private double horizontalMomentum = 0.0;
	private int visualSquareSize;
	private int pathGridSquareNum;
	private double currentX;
	private double currentY;
	
	private static final int PLAYER_BORDER_WIDTH = 3;
	private int xGridPos;
	private int yGridPos;
	
	PlayerController controller;
	
	public Player(Pane pane) {
		setXGridPos(0);
		setYGridPos(0);
		controller = new PlayerController(this, pane);
	}
	
    // Creates the player object and randomly sets its position
    public boolean initializePlayer(List<Rectangle> obstacles, Field field) {
    	Random rand = new Random();
    	visualSquareSize = Configuration.getVisualSquareSize();
    	pathGridSquareNum = Configuration.getPathGridSquareNum();
    	int numXSquares = Configuration.getWindowSizeWidth() / visualSquareSize;
    	int numYSquares = Configuration.getWindowSizeHeight() / visualSquareSize;
    	int attemptLimit = 10;
    	int i = 0;
    	boolean failedAttempt = true;
    	
    	// Resets the player object's translate position for respawn
    	setTranslateX(0);
        setTranslateY(0);
        setCumulativePixelsMoved(0.0);
    	
    	while(i < attemptLimit) {
    		setXGridPos(pathGridSquareNum * (rand.nextInt(numXSquares - 10) + 5));
    		setYGridPos(pathGridSquareNum * (rand.nextInt(10) + (numYSquares - 12)));
            int xPos = getXGridPos() / pathGridSquareNum * visualSquareSize + PLAYER_BORDER_WIDTH;
            int yPos = getYGridPos() / pathGridSquareNum * visualSquareSize + PLAYER_BORDER_WIDTH;
            
            setX(xPos - visualSquareSize);
            setY(yPos - visualSquareSize);
            setWidth((visualSquareSize - PLAYER_BORDER_WIDTH) * 3);
            setHeight((visualSquareSize - PLAYER_BORDER_WIDTH) * 3);
            
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
            	setWidth(visualSquareSize - 2 * PLAYER_BORDER_WIDTH);
            	setHeight(visualSquareSize - 2 * PLAYER_BORDER_WIDTH);
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
	
	public void moveUp(double timestep, Grid grid) {
		currentY = getY() + getTranslateY();
		
		double predictedMovement = getVerticalMomentum() * timestep;
		int predictedYGridPos = (int)((currentY + predictedMovement) / visualSquareSize * pathGridSquareNum);
		if (grid.getNode(getXGridPos(), predictedYGridPos).isWalkable()) {
			setTranslateY(getTranslateY() + predictedMovement);
	    	increaseVerticalMomentum();
		} else {
			setVerticalMomentum(0);
			predictedYGridPos = (int)(currentY / visualSquareSize * pathGridSquareNum);
			double newY = (predictedYGridPos + 1) / pathGridSquareNum * visualSquareSize;
			setTranslateY(newY - getY());
		}
	}
	
	public void moveDown(double timestep, Grid grid) {
		currentY = getY() + getTranslateY();
		
		double predictedMovement = getVerticalMomentum() * timestep;
		int predictedYGridPos = (int)((currentY + predictedMovement) / visualSquareSize * pathGridSquareNum);
		if (grid.getNode(getXGridPos(), predictedYGridPos).isWalkable()) {
			setTranslateY(getTranslateY() + predictedMovement);
			decreaseVerticalMomentum();
		} else {
			double diff = ((predictedYGridPos + 1) / pathGridSquareNum * visualSquareSize) - currentY + 2 * PLAYER_BORDER_WIDTH;
			if (diff >= 0) setTranslateY(getTranslateY() + diff - 1.01);
			setVerticalMomentum(0);
		}
	}
	
	public void moveLeft(double timestep, Grid grid) {
		currentX = getX() + getTranslateX();
		
		double predictedMovement = getHorizontalMomentum() * timestep;
		int predictedXGridPos = (int)((currentX + predictedMovement) / visualSquareSize * pathGridSquareNum);
		if (grid.getNode(predictedXGridPos, getYGridPos()).isWalkable()) {
			setTranslateX(getTranslateX() + predictedMovement);
			decreaseHorizontalMomentum();
		} else {
			setHorizontalMomentum(0);
			predictedXGridPos = (int)(currentX / visualSquareSize * pathGridSquareNum);
			double newX = (predictedXGridPos + 1) / pathGridSquareNum * visualSquareSize;
			setTranslateX(newX - getX());
		}
	}
	
	public void moveRight(double timestep, Grid grid) {
		currentX = getX() + getTranslateX();
		
		double predictedMovement = getHorizontalMomentum() * timestep;
		int predictedXGridPos = (int)((currentX + predictedMovement) / visualSquareSize * pathGridSquareNum);
		if (grid.getNode(predictedXGridPos, getYGridPos()).isWalkable()) {
			setTranslateX(getTranslateX() + predictedMovement);
			increaseHorizontalMomentum();
		} else {
			double diff = ((predictedXGridPos + 1) / pathGridSquareNum * visualSquareSize) - currentX + 2 * PLAYER_BORDER_WIDTH;
			if (diff >= 0) setTranslateX(getTranslateX() + diff - 1.01);
			setHorizontalMomentum(0);
		}
	}
	
	public void moveTowards(double dx, double dy, double timestep, Grid grid) {
	    double distance = Math.sqrt(dx * dx + dy * dy);
	    
	    if (distance <= MAX_SPEED * timestep) {
	        // If the remaining distance is less than or equal to the maximum distance
	        // that can be covered in one timestep at maximum speed,
	        // simply move to the destination.
	        setTranslateX(getTranslateX() + dx);
	        setTranslateY(getTranslateY() + dy);
	    } else {
	        // Calculate the ratio of dx and dy relative to the total distance
	        double ratioX = dx / distance;
	        double ratioY = dy / distance;
	        
	        if (ratioX > 0) {
	        	increaseHorizontalMomentum();
	        } else if (ratioX < 0) {
	        	decreaseHorizontalMomentum();
	        } else {
	        	if (getHorizontalMomentum() > 0) {
	        		decreaseHorizontalMomentum();
	        	} else if (getHorizontalMomentum() < 0) {
	        		increaseHorizontalMomentum();
	        	}
	        }
	        
	        if (ratioY < 0) {
	        	increaseVerticalMomentum();
	        } else if (ratioY > 0) {
	        	decreaseVerticalMomentum();
	        } else {
	        	if (getVerticalMomentum() > 0) {
	        		decreaseVerticalMomentum();
	        	} else if (getVerticalMomentum() < 0) {
	        		increaseVerticalMomentum();
	        	}
	        }

	        // Calculate the maximum distance that can be covered in each direction
	        double maxDistanceX = ratioX * getHorizontalMomentum() * timestep;
	        double maxDistanceY = ratioY * getVerticalMomentum() * timestep;

	        // Determine the actual distance to move in each direction
	        double moveX = Math.min(maxDistanceX, Math.abs(dx));
	        double moveY = Math.min(maxDistanceY, Math.abs(dy));

	        // Adjust the sign of the movement based on the direction
	        moveX *= Math.signum(dx);
	        moveY *= Math.signum(dy);
	        
	        // Move the object
	        int predictedXGridPos = (int)((currentX + moveX) / visualSquareSize * pathGridSquareNum);
	        if (grid.getNode(predictedXGridPos, getYGridPos()).isWalkable()) {
	        	setTranslateX(getTranslateX() + moveX);
	        }
	        int predictedYGridPos = (int)((currentY + moveY) / visualSquareSize * pathGridSquareNum);
	        if (grid.getNode(getXGridPos(), predictedYGridPos).isWalkable()) {
	        	setTranslateY(getTranslateY() + moveY);
	        }
	        
	        setCumulativePixelsMoved(cumulativePixelsMoved += Math.hypot(moveX, moveY));
	    }
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
	
	public int getPlayerBorderWidth() {
		return PLAYER_BORDER_WIDTH;
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
	
	public PlayerController getController() {
		return controller;
	}
}
