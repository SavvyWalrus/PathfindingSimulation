package org.openjfx.PathfindingSimulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class ComputerController {
	private List<Enemy> enemies = new ArrayList<Enemy>();
	private static Player player;
	
	private static Pane pane;
	private static Grid grid;
	
	ComputerController(Pane pane, Grid grid, Player player) {
		ComputerController.player = player;
		ComputerController.pane = pane;
		ComputerController.grid = grid;
	}
	
	public boolean initializeEnemy(List<Rectangle> obstacles) {
		Random rand = new Random();
		Enemy enemy = new Enemy(pane, grid);
    	int i = 0;
    	boolean failedAttempt = true;
    	
    	while(i < Configuration.getAttemptLimit()) {
    		enemy.setXGridPos(enemy.getPathGridSquareNum() * (rand.nextInt(Configuration.getNumXSquares() - 10) + 5));
    		enemy.setYGridPos(enemy.getPathGridSquareNum() * (rand.nextInt(10) + 2));
            int xPos = enemy.getXGridPos() / enemy.getPathGridSquareNum() * enemy.getVisualSquareSize() + enemy.getBorderWidth();
            int yPos = enemy.getYGridPos() / enemy.getPathGridSquareNum() * enemy.getVisualSquareSize() + enemy.getBorderWidth();
            
            enemy.setX(xPos - enemy.getVisualSquareSize());
            enemy.setY(yPos - enemy.getVisualSquareSize());
            enemy.setWidth((enemy.getVisualSquareSize() - enemy.getBorderWidth()) * 3);
            enemy.setHeight((enemy.getVisualSquareSize() - enemy.getBorderWidth()) * 3);
            
            boolean overlaps = false;
            for(Rectangle currentObstacle : obstacles) {
            	if(enemy.getBoundsInParent().intersects(currentObstacle.getBoundsInParent())) {
            		overlaps = true;
            		break;
            	}
            }
            
            if(!overlaps) {
            	enemy.setX(xPos);
            	enemy.setY(yPos);
            	enemy.setWidth(enemy.getVisualSquareSize() - 2 * enemy.getBorderWidth());
            	enemy.setHeight(enemy.getVisualSquareSize() - 2 * enemy.getBorderWidth());
            	enemy.setFill(Color.PURPLE);
            	enemy.setStroke(Color.BLACK);
            	enemy.setStrokeWidth(enemy.getBorderWidth());
            	pane.getChildren().add(enemy);
            	enemies.add(enemy);
            	failedAttempt = false;
            	break;
            } else {
            	++i;
            }
    	}
    	
    	return !failedAttempt;
	}
	
	public void initializePaths() {
		for (Enemy enemy : enemies) {
			GridNode playerNode = grid.getNode(player.getXGridPos(), player.getYGridPos());
			GridNode enemyPos = new GridNode(enemy.getXGridPos(), enemy.getYGridPos(), true);
			enemy.setPath(grid.findPath(enemyPos, playerNode));
			
			if (Configuration.isShowPathVisualization()) {
				initializePathVisualization(enemy);
			}
		}
	}

	public void updatePath(Enemy enemy) {
		GridNode playerNode = grid.getNode(player.getXGridPos(), player.getYGridPos());
		GridNode enemyPos = new GridNode(enemy.getXGridPos(), enemy.getYGridPos(), true);
		enemy.setPath(grid.findPath(enemyPos, playerNode));
	}
	

	private int counter = 0;
	
	public void refreshPaths() {
		if (enemies.size() == 0) return;
		
		if (counter >= enemies.size()) {
			counter = 0;
		}
		
		updatePath(enemies.get(counter));
		if (enemies.get(counter).getPath().size() > 0) enemies.get(counter).getPath().remove(0);
		++counter;
	}
	
	private int visualizationCounter = 0;
	
	public void refreshVisualizations() {
	    if (!Configuration.isShowPathVisualization()) return;
	    
        if (visualizationCounter < enemies.size()) {
            initializePathVisualization(enemies.get(visualizationCounter));
            ++visualizationCounter;
        } else {
            visualizationCounter = 0;
        }
	}
	
	public void initializePathVisualization(Enemy enemy) {
		// Aborts if no path exists
		if (enemy.getPath().isEmpty())
			return;

		// Remove previous path visualization
		pane.getChildren().removeIf(node -> "pathNode".equals(node.getUserData()));

		// Dot Visualization
		if (Configuration.isDotVisualization()) {
			for (GridNode node : enemy.getPath()) {
				Circle point = new Circle();
				point.setRadius(Configuration.getTargetRadius() / 3);
				point.setFill(Color.GREEN);
				point.setCenterX(node.getXPos() * Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum() + enemy.getBorderWidth());
				point.setCenterY(node.getYPos() * Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum() + enemy.getBorderWidth());
				point.setUserData("pathNode");
				pane.getChildren().add(point);
			}
		}

		// Rectangle visualization
		if (Configuration.isRectangleVisualization()) {
			for (GridNode node : enemy.getPath()) {
				Rectangle rect = new Rectangle();
				rect.setWidth(Configuration.getVisualSquareSize());
				rect.setHeight(Configuration.getVisualSquareSize());
				rect.setFill(Color.GREEN);
				rect.setX(node.getXPos() * Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum());
				rect.setY(node.getYPos() * Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum());
				rect.setUserData("pathNode");
				pane.getChildren().add(rect);
			}
		}
	}
	
	public boolean updateComputerPosition(double timestep) {
		boolean returnValue = true;
		for (Enemy enemy : enemies) {
			if (!Configuration.isPathfindingActive() || enemy.getPath() == null || enemy.getPath().isEmpty()) {
				returnValue = false;
				continue; // No path to follow
			}
	
			// Calculate the target position in pixels
			double targetX = (double)(enemy.getPath().get(0).getXPos() * Configuration.getVisualSquareSize()) / (double)Configuration.getPathGridSquareNum() + enemy.getBorderWidth();
			double targetY = (double)(enemy.getPath().get(0).getYPos() * Configuration.getVisualSquareSize()) / (double)Configuration.getPathGridSquareNum() + enemy.getBorderWidth();
	
			// Current position including translations
			enemy.setCurrentX(enemy.getX() + enemy.getTranslateX());
			enemy.setCurrentY(enemy.getY() + enemy.getTranslateY());
	
			// Calculate direction vector components
			double dx = targetX - enemy.getCurrentX();
			double dy = targetY - enemy.getCurrentY();
	
			// Check if reached the current target node within a threshold
			boolean hasReachedX = Math.abs(enemy.getCurrentX() - targetX) <= 0.9;
			boolean hasReachedY = Math.abs(enemy.getCurrentY() - targetY) <= 0.9;
	
			// Apply movement and speed
			if (!hasReachedX || !hasReachedY) {
				enemy.moveTowards(dx, dy, timestep, grid);
			}
			
			enemy.updateGridPos();
			
			if (hasReachedX && hasReachedY) {
				if (enemy.getPath().size() > 1) {
					enemy.setXGridPos(enemy.getPath().get(1).getXPos());
					enemy.setYGridPos(enemy.getPath().get(1).getYPos());
				}
				updatePath(enemy);
			}
			
			if (Configuration.isShowPlayerHitboxVisualization())
				PlayerController.initializePlayerHitboxVisualization();
		}
		
		return returnValue;
	}
	
	public void addEnemy(Enemy enemy) {
		enemies.add(enemy);
	}
	
	public Enemy getEnemy(int index) {
		return enemies.get(index);
	}
	
	public boolean containsEnemies() {
		if (!enemies.isEmpty()) return true;
		else return false;
	}
	
	public List<Enemy> getEnemies() {
		return enemies;
	}
	
	public void clearEnemies() {
		enemies.clear();
	}
}
