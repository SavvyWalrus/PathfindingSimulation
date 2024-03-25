package org.openjfx.PathfindingSimulation;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import java.util.*;

public class Field extends Pane {
	// Initializes a Random object
	Random rand = new Random();

	// Initializes the player object with no properties
	Player player = new Player(this);

	// Initializes the target object with no properties
	Target target = new Target(Configuration.getVisualSquareSize(), Configuration.getPathGridSquareNum());

	// Creates the background and adds it to a Node list
	List<Node> background = initializeBackground();

	// Grid representation for pathfinding
	Grid grid = new Grid((Configuration.getNumXSquares()) * Configuration.getPathGridSquareNum(),
			(Configuration.getNumYSquares()) * Configuration.getPathGridSquareNum());

	// Initializes Computer Controller with no enemies
	ComputerController controller = new ComputerController(this, grid, player);

	// List of obstacle objects
	ObstacleList obstacles = new ObstacleList(this, grid);

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
				if (success) {
					controller.clearEnemies();
					for (int count = 0; count < Configuration.getEnemyNumber(); ++count) {
						success = controller.initializeEnemy(obstacles.getObstacles());
						if (!success)
							break;
					}
				}
			}
		}

		if (!success) {
			System.out.println("ERROR: Unable to initialize field\nExiting program");
			System.exit(0);
		}

		if (Configuration.isShowPlayerHitboxVisualization())
			PlayerController.initializePlayerHitboxVisualization();

		// Optional obstacle visualization
		if (Configuration.isShowObstacleGridPosition())
			obstacles.initializeObstacleGridVisualization();

		if (controller.containsEnemies())
			controller.initializePaths();
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
		border.setWidth(Configuration.getWindowSizeWidth() -
		Configuration.getVisualSquareSize());
		border.setHeight(Configuration.getWindowSizeHeight() -
		Configuration.getVisualSquareSize()); border.setStroke(Color.CRIMSON);
		border.setStrokeWidth(Configuration.getVisualSquareSize());
		border.setFill(Color.TRANSPARENT); background.add(border);

		return background;
	}

	public void checkFieldRefresh() {
		if (Configuration.isRefreshToggle()) {
			for (KeyCode keyCode : MainApplication.getKeysPressed()) {
				switch (keyCode) {
				case SPACE:
					initializeField();
					Configuration.setRefreshToggle(false);
					return;
				default:
					continue;
				}
			}
		} else {
			for (KeyCode keyCode : MainApplication.getKeysPressed()) {
				switch (keyCode) {
				case SPACE:
					return;
				default:
					continue;
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

	public ComputerController getController() {
		return controller;
	}

	public ObstacleList getObstacles() {
		return obstacles;
	}
}