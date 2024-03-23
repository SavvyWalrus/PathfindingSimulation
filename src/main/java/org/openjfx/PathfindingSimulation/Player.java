package org.openjfx.PathfindingSimulation;

import java.util.List;
import java.util.Random;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class Player extends Entity {
	PlayerController controller;
	
	public Player(Pane pane) {
		setXGridPos(0);
		setYGridPos(0);
		controller = new PlayerController(this, pane);
	}
	
    // Creates the player object and randomly sets its position
    public boolean initializePlayer(List<Rectangle> obstacles, Field field) {
    	Random rand = new Random();
    	int i = 0;
    	boolean failedAttempt = true;
    	
    	// Resets the player object's translate position for respawn
    	setTranslateX(0);
        setTranslateY(0);
        setCumulativePixelsMoved(0.0);
    	
    	while(i < Configuration.getAttemptLimit()) {
    		setXGridPos(getPathGridSquareNum() * (rand.nextInt(Configuration.getNumXSquares() - 10) + 5));
    		setYGridPos(getPathGridSquareNum() * (rand.nextInt(10) + (Configuration.getNumYSquares() - 12)));
            int xPos = getXGridPos() / getPathGridSquareNum() * getVisualSquareSize() + getBorderWidth();
            int yPos = getYGridPos() / getPathGridSquareNum() * getVisualSquareSize() + getBorderWidth();
            
            setX(xPos - getVisualSquareSize());
            setY(yPos - getVisualSquareSize());
            setWidth((getVisualSquareSize() - getBorderWidth()) * 3);
            setHeight((getVisualSquareSize() - getBorderWidth()) * 3);
            
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
            	setWidth(getVisualSquareSize() - 2 * getBorderWidth());
            	setHeight(getVisualSquareSize() - 2 * getBorderWidth());
            	setFill(Color.RED);
            	setStroke(Color.BLACK);
            	setStrokeWidth(getBorderWidth());
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
		setCurrentY(getY() + getTranslateY());
		
		double predictedMovement = getVerticalMomentum() * timestep;
		int predictedYGridPos = (int)((getCurrentY() + predictedMovement) / getVisualSquareSize() * getPathGridSquareNum());
		if (grid.getNode(getXGridPos(), predictedYGridPos).isWalkable()) {
			setTranslateY(getTranslateY() + predictedMovement);
	    	increaseVerticalMomentum();
		} else {
			setVerticalMomentum(0);
			predictedYGridPos = (int)(getCurrentY() / getVisualSquareSize() * getPathGridSquareNum());
			double newY = (predictedYGridPos + 1) / getPathGridSquareNum() * getVisualSquareSize();
			setTranslateY(newY - getY());
		}
	}
	
	public void moveDown(double timestep, Grid grid) {
		setCurrentY(getY() + getTranslateY());
		
		double predictedMovement = getVerticalMomentum() * timestep;
		int predictedYGridPos = (int)((getCurrentY() + predictedMovement) / getVisualSquareSize() * getPathGridSquareNum());
		if (grid.getNode(getXGridPos(), predictedYGridPos).isWalkable()) {
			setTranslateY(getTranslateY() + predictedMovement);
			decreaseVerticalMomentum();
		} else {
			double diff = ((predictedYGridPos + 1) / getPathGridSquareNum() * getVisualSquareSize()) - getCurrentY() + 2 * getBorderWidth();
			if (diff >= 0) setTranslateY(getTranslateY() + diff - 1.01);
			setVerticalMomentum(0);
		}
	}
	
	public void moveLeft(double timestep, Grid grid) {
		setCurrentX(getX() + getTranslateX());
		
		double predictedMovement = getHorizontalMomentum() * timestep;
		int predictedXGridPos = (int)((getCurrentX() + predictedMovement) / getVisualSquareSize() * getPathGridSquareNum());
		if (grid.getNode(predictedXGridPos, getYGridPos()).isWalkable()) {
			setTranslateX(getTranslateX() + predictedMovement);
			decreaseHorizontalMomentum();
		} else {
			setHorizontalMomentum(0);
			predictedXGridPos = (int)(getCurrentX() / getVisualSquareSize() * getPathGridSquareNum());
			double newX = (predictedXGridPos + 1) / getPathGridSquareNum() * getVisualSquareSize();
			setTranslateX(newX - getX());
		}
	}
	
	public void moveRight(double timestep, Grid grid) {
		setCurrentX(getX() + getTranslateX());
		
		double predictedMovement = getHorizontalMomentum() * timestep;
		int predictedXGridPos = (int)((getCurrentX() + predictedMovement) / getVisualSquareSize() * getPathGridSquareNum());
		if (grid.getNode(predictedXGridPos, getYGridPos()).isWalkable()) {
			setTranslateX(getTranslateX() + predictedMovement);
			increaseHorizontalMomentum();
		} else {
			double diff = ((predictedXGridPos + 1) / getPathGridSquareNum() * getVisualSquareSize()) - getCurrentX() + 2 * getBorderWidth();
			if (diff >= 0) setTranslateX(getTranslateX() + diff - 1.01);
			setHorizontalMomentum(0);
		}
	}
	
	public PlayerController getController() {
		return controller;
	}
}
