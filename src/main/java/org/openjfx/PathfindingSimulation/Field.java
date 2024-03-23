package org.openjfx.PathfindingSimulation;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Field extends Pane {
	// Initializes a Random object
	Random rand = new Random();

	// Initializes the player object with no properties
	Player player = new Player(this);

	// Initializes the target object with no properties
	Target target = new Target(Configuration.getVisualSquareSize(), Configuration.getPathGridSquareNum());

	// List of obstacle objects
	ObstacleList obstacles = new ObstacleList();

	// Creates the background and adds it to a Node list
	List<Node> background = initializeBackground();

	// Grid representation for pathfinding
	Grid grid = new Grid(Configuration.getNumXSquares() * Configuration.getPathGridSquareNum(), Configuration.getNumYSquares() * Configuration.getPathGridSquareNum());
	GridNode playerPos;
	List<GridNode> path;

	// Initializes and sets the various field layers
	public void initializeField() {
		// Variables for ensuring proper task execution
		boolean success = false;
		int attemptLimit = 10;
		int numAttempts = 0;

		while (!success && numAttempts++ < attemptLimit) {
			grid.clear();
			this.getChildren().clear();
			this.getChildren().addAll(background);
			obstacles.initializeObstacles(this, grid);
			success = target.initializeTarget(obstacles.getObstacles(), this, grid);
			if (success) {
				success = player.initializePlayer(obstacles.getObstacles(), this);
			}
		}

		if (!success) {
			System.out.println("ERROR: Unable to initialize field\nExiting program");
			System.exit(0);
		}
		
		if (Configuration.isShowPlayerHitboxVisualization())
			PlayerController.initializePlayerHitboxVisualization();

		updatePath();
	}

	// Sets up the checkerboard background
	private static List<Node> initializeBackground() {
		List<Node> background = new ArrayList<>();

		for (int i = 0; i < Configuration.getNumXSquares(); ++i) {
			for (int j = i % 2; j < Configuration.getNumYSquares(); j += 2) {
				Rectangle checkerboard = new Rectangle();
				checkerboard.setX(i * Configuration.getVisualSquareSize());
				checkerboard.setY(j * Configuration.getVisualSquareSize());
				checkerboard.setWidth(Configuration.getVisualSquareSize());
				checkerboard.setHeight(Configuration.getVisualSquareSize());
				checkerboard.setFill(Color.LAVENDER);
				background.add(checkerboard);
			}
		}

		Rectangle border = new Rectangle();
		border.setX(Configuration.getVisualSquareSize() / 2);
		border.setY(Configuration.getVisualSquareSize() / 2);
		border.setWidth(Configuration.getWindowSizeWidth() - Configuration.getVisualSquareSize());
		border.setHeight(Configuration.getWindowSizeHeight() - Configuration.getVisualSquareSize());
		border.setStroke(Color.CRIMSON);
		border.setStrokeWidth(Configuration.getVisualSquareSize());
		border.setFill(Color.TRANSPARENT);
		background.add(border);

		return background;
	}

	public boolean updateComputerPosition(double timestep) {
		if (!Configuration.isPathfindingActive() || path == null || path.isEmpty()) {
			return false; // No path to follow
		}

		// Calculate the target position in pixels
		double targetX = (double)(path.get(0).getXPos() * Configuration.getVisualSquareSize()) / (double)Configuration.getPathGridSquareNum() + player.getPlayerBorderWidth();
		double targetY = (double)(path.get(0).getYPos() * Configuration.getVisualSquareSize()) / (double)Configuration.getPathGridSquareNum() + player.getPlayerBorderWidth();

		// Current position including translations
		player.setCurrentX(player.getX() + player.getTranslateX());
		player.setCurrentY(player.getY() + player.getTranslateY());

		// Calculate direction vector components
		double dx = targetX - player.getCurrentX();
		double dy = targetY - player.getCurrentY();

		// Check if reached the current target node within a threshold
		boolean hasReachedX = Math.abs(player.getCurrentX() - targetX) <= 0.9;
		boolean hasReachedY = Math.abs(player.getCurrentY() - targetY) <= 0.9;

		// Apply movement and speed
		if (!hasReachedX || !hasReachedY) {
			player.moveTowards(dx, dy, timestep, grid);
		}
		
		player.updateGridPos();
		
		// Refreshes if the refresh pixel threshold or the next node has been reached
		if (player.getCumulativePixelsMoved() >= Configuration.getMaxPixelsBeforePathRefresh() && !(hasReachedX && hasReachedY)) {
			player.setCumulativePixelsMoved(0.0);
			
			updatePath();
			
			if (path.isEmpty()) {
				return false; // No path to follow
			}
			
			// Necessary to prevent sudden direction shift
			if (path.size() > 0) path.remove(0);
		} else if (hasReachedX && hasReachedY) {
			player.setXGridPos(path.get(1).getXPos());
			player.setYGridPos(path.get(1).getYPos());
			updatePath();
		}
		
		// Optional path visualization
		if (Configuration.isShowPathVisualization())
			initializePathVisualization();
		
		if (Configuration.isShowPlayerHitboxVisualization())
			PlayerController.initializePlayerHitboxVisualization();

		return true; // Continuing movement
	}

	public void updatePath() {
		playerPos = new GridNode(player.getXGridPos(), player.getYGridPos(), true);
		path = grid.findPath(playerPos, target.getGoalNode());
		
		// Optional obstacle visualization
		if (Configuration.isShowObstacleGridPosition())
			initializeObstacleGridVisualization();
	}

	public void initializePathVisualization() {
		// Aborts if no path exists
		if (path.isEmpty())
			return;

		// Remove previous path visualization
		this.getChildren().removeIf(node -> "pathNode".equals(node.getUserData()));

		// Dot Visualization
		if (Configuration.isDotVisualization()) {
			for (GridNode node : path) {
				Circle point = new Circle();
				point.setRadius(Configuration.getTargetRadius() / 3);
				point.setFill(Color.GREEN);
				point.setCenterX(node.getXPos() * Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum() + player.getPlayerBorderWidth());
				point.setCenterY(node.getYPos() * Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum() + player.getPlayerBorderWidth());
				point.setUserData("pathNode");
				this.getChildren().add(point);
			}
		}

		// Rectangle visualization
		if (Configuration.isRectangleVisualization()) {
			for (GridNode node : path) {
				Rectangle rect = new Rectangle();
				rect.setWidth(Configuration.getVisualSquareSize());
				rect.setHeight(Configuration.getVisualSquareSize());
				rect.setFill(Color.GREEN);
				rect.setX(node.getXPos() * Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum());
				rect.setY(node.getYPos() * Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum());
				rect.setUserData("pathNode");
				this.getChildren().add(rect);
			}
		}
	}

	public void initializeObstacleGridVisualization() {
		// Remove previous obstacle visualization
		this.getChildren().removeIf(node -> "obstacleNode".equals(node.getUserData()));

		// Rectangle visualization
		if (Configuration.isRectangleVisualization()) {
			for (int x = 0; x < Configuration.getNumXSquares() * Configuration.getPathGridSquareNum(); ++x) {
				for (int y = 0; y < Configuration.getNumXSquares() * Configuration.getPathGridSquareNum(); ++y) {
					if (!grid.getNode(x, y).isWalkable()) {
						Rectangle rect = new Rectangle();
						rect.setWidth(Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum());
						rect.setHeight(Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum());
						rect.setFill(Color.RED);
						rect.setX(grid.getNode(x, y).getXPos() * Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum());
						rect.setY(grid.getNode(x, y).getYPos() * Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum());
						rect.setUserData("obstacleNode");
						this.getChildren().add(rect);
					}
				}
			}
		}

		// Dot Visualization
		if (Configuration.isDotVisualization()) {
			for (int x = 0; x < Configuration.getNumXSquares() * Configuration.getPathGridSquareNum(); ++x) {
				for (int y = 0; y < Configuration.getNumXSquares() * Configuration.getPathGridSquareNum(); ++y) {
					if (!grid.getNode(x, y).isWalkable()) {
						Circle point = new Circle();
						point.setRadius(Configuration.getTargetRadius() / 6);
						point.setFill(Color.RED);
						point.setCenterX(grid.getNode(x, y).getXPos() * Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum()
								- Configuration.getPathGridSquareNum() + Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum());
						point.setCenterY(grid.getNode(x, y).getYPos() * Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum()
								- Configuration.getPathGridSquareNum() + Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum());
						point.setUserData("obstacleNode");
						this.getChildren().add(point);
					}
				}
			}
		}
	}

	public void checkFieldRefresh() {
		if (Configuration.isRefreshToggle()) {
			for (KeyCode keyCode : MainApplication.getKeysPressed()) {
				switch (keyCode) {
				case SPACE:
					initializeField();
					Configuration.setRefreshToggle(false);
					return;
				}
			}
		} else {
			for (KeyCode keyCode : MainApplication.getKeysPressed()) {
				switch (keyCode) {
				case SPACE:
					return;
				}
			}
			Configuration.setRefreshToggle(true);
		}
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Target getTarget() {
		return target;
	}
	
	public Grid getGrid() {
		return grid;
	}
}