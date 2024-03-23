package org.openjfx.PathfindingSimulation;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.Pane;

public class Enemy extends Entity {
	Enemy enemy;
	ComputerController controller;
	static Pane pane;
	static Grid grid;
	
	List<GridNode> path = new ArrayList<GridNode>();
	
	Enemy(Pane pane, Grid grid) {
		Enemy.pane = pane;
		Enemy.grid = grid;
	}
	
	public void moveTowards(double dx, double dy, double timestep, Grid grid) {
	    double distance = Math.sqrt(dx * dx + dy * dy);
	    
	    if (distance <= getMaxSpeed() * timestep) {
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
	        int predictedXGridPos = (int)((getCurrentX() + moveX) / getVisualSquareSize() * getPathGridSquareNum());
	        if (grid.getNode(predictedXGridPos, getYGridPos()).isWalkable()) {
	        	setTranslateX(getTranslateX() + moveX);
	        }
	        int predictedYGridPos = (int)((getCurrentY() + moveY) / getVisualSquareSize() * getPathGridSquareNum());
	        if (grid.getNode(getXGridPos(), predictedYGridPos).isWalkable()) {
	        	setTranslateY(getTranslateY() + moveY);
	        }
	        
	        setCumulativePixelsMoved(getCumulativePixelsMoved() + Math.hypot(moveX, moveY));
	    }
	}
	
	public void setPath(List<GridNode> path) {
		this.path = path;
	}
	
	public List<GridNode> getPath() {
		return path;
	}
}
