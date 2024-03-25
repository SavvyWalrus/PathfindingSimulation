package org.openjfx.PathfindingSimulation;

public class Configuration {
	// Number of enemies
	private static int enemyNumber = 1;

	// Number of obstacles on the field
	private static final int MAX_OBSTACLES = 25;
	private static final int MIN_OBSTACLES = 15;

	// Size of the various obstacles in multiples of the background grid squares
	private static final int MAX_OBSTACLE_SIZE = 4;
	private static final int MIN_OBSTACLE_SIZE = 2;

	// Window size
	private static final int WINDOW_SIZE_WIDTH = 1000;
	private static final int WINDOW_SIZE_HEIGHT = 1000;

	// Size of background grid squares
	private static final int VISUAL_SQUARE_SIZE = 25;

	// Density of path grid per visual square (ie. Number of squares per square)
	// Causes instability at > 3 for strictly A*
	// Set between 3 and 5 for Theta* (5 recommended for more precise collision) / 3 for A*
	private static final int PATH_GRID_SQUARE_NUM = 5;

	// Number of visual grid squares
	private static final int NUM_X_SQUARES = WINDOW_SIZE_WIDTH / VISUAL_SQUARE_SIZE;
	private static final int NUM_Y_SQUARES = WINDOW_SIZE_HEIGHT / VISUAL_SQUARE_SIZE;

	// Thickness of borders
	private static final int OBSTACLE_BORDER_WIDTH = 6;

	// Size of target/goal
	private static final double TARGET_RADIUS = (VISUAL_SQUARE_SIZE / 2) - (3);

	// Variable representations
	private static final int LOSE = 1;
	private static final int WIN = 2;

	// Visualization settings
	// Flickers very quickly with path refreshing
	private static final boolean SHOW_PATH_VISUALIZATION = false;
	private static final boolean SHOW_OBSTACLE_GRID_POSITION = false;
	private static final boolean SHOW_PLAYER_HITBOX_VISUALIZATION = false;
	private static final boolean RECTANGLE_VISUALIZATION = false;
	private static final boolean DOT_VISUALIZATION = true;

	// Pathfinding activation
	private static final boolean PATHFINDING_ACTIVE = true;
	
	// Max field initialization attempts
	private static final int ATTEMPT_LIMIT = 10;

	// Dictates spacebar field refresh
	private static boolean refreshToggle = true;
	
	public static int getEnemyNumber() {
		return enemyNumber;
	}
	
	public static void setEnemyNumber(int value) {
		enemyNumber = value;
	}

	public static int getMaxObstacles() {
		return MAX_OBSTACLES;
	}

	public static int getMinObstacles() {
		return MIN_OBSTACLES;
	}

	public static int getMaxObstacleSize() {
		return MAX_OBSTACLE_SIZE;
	}

	public static int getMinObstacleSize() {
		return MIN_OBSTACLE_SIZE;
	}

	public static int getWindowSizeWidth() {
		return WINDOW_SIZE_WIDTH;
	}

	public static int getWindowSizeHeight() {
		return WINDOW_SIZE_HEIGHT;
	}

	public static int getVisualSquareSize() {
		return VISUAL_SQUARE_SIZE;
	}

	public static int getPathGridSquareNum() {
		return PATH_GRID_SQUARE_NUM;
	}

	public static int getNumXSquares() {
		return NUM_X_SQUARES;
	}

	public static int getNumYSquares() {
		return NUM_Y_SQUARES;
	}

	public static int getObstacleBorderWidth() {
		return OBSTACLE_BORDER_WIDTH;
	}

	public static double getTargetRadius() {
		return TARGET_RADIUS;
	}

	public static int getLose() {
		return LOSE;
	}

	public static int getWin() {
		return WIN;
	}

	public static boolean isShowPathVisualization() {
		return SHOW_PATH_VISUALIZATION;
	}

	public static boolean isShowObstacleGridPosition() {
		return SHOW_OBSTACLE_GRID_POSITION;
	}

	public static boolean isShowPlayerHitboxVisualization() {
		return SHOW_PLAYER_HITBOX_VISUALIZATION;
	}

	public static boolean isRectangleVisualization() {
		return RECTANGLE_VISUALIZATION;
	}

	public static boolean isDotVisualization() {
		return DOT_VISUALIZATION;
	}

	public static boolean isPathfindingActive() {
		return PATHFINDING_ACTIVE;
	}
	
	public static int getAttemptLimit() {
		return ATTEMPT_LIMIT;
	}
	
	public static boolean isRefreshToggle() {
		return refreshToggle;
	}

	public static void setRefreshToggle(boolean refreshToggle) {
		Configuration.refreshToggle = refreshToggle;
	}
}
