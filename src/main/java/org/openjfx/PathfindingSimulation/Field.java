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
	private static final int PATH_GRID_SQUARE_SIZE = 5;
	
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
	
    // Initializes a Random object
    Random rand = new Random();
    
    // Initializes the player object with no properties
    Player player = new Player();
    
    // Initializes the target object with no properties
    Target target = new Target(VISUAL_SQUARE_SIZE, PATH_GRID_SQUARE_SIZE);
    
    // List of obstacle objects
    List<Rectangle> obstacles = new ArrayList<>();
    
    // List of keys currently pressed
    Set<KeyCode> keysPressed = ConcurrentHashMap.newKeySet();
    
    // Creates the background and adds it to a Node list
    List<Node> background = initializeBackground();
    
    // Grid representation for pathfinding
    Grid grid = new Grid(NUM_X_SQUARES, NUM_Y_SQUARES);
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
            initializeObstacles();
            success = target.initializeTarget(WINDOW_SIZE_WIDTH, WINDOW_SIZE_HEIGHT, VISUAL_SQUARE_SIZE, PATH_GRID_SQUARE_SIZE, obstacles, this, grid);
            if (success) {
            	success = player.initializePlayer(WINDOW_SIZE_WIDTH, WINDOW_SIZE_HEIGHT, VISUAL_SQUARE_SIZE, PATH_GRID_SQUARE_SIZE, obstacles, this);
            }
        }
		
		if (!success) {
        	System.out.println("ERROR: Unable to initialize field\nExiting program");
        	System.exit(0);
        }
		
		//updatePath();
	}
    
	// Sets up the checkerboard background
    private static List<Node> initializeBackground() {
    	List<Node> background = new ArrayList<>();
    	
    	for (int i = 0; i < NUM_X_SQUARES; ++i) {
    		for (int j = i % 2; j < NUM_Y_SQUARES; j+=2) {
    			Rectangle checkerboard = new Rectangle();
        		checkerboard.setX(i * VISUAL_SQUARE_SIZE);
        		checkerboard.setY(j * VISUAL_SQUARE_SIZE);
        		checkerboard.setWidth(VISUAL_SQUARE_SIZE);
        		checkerboard.setHeight(VISUAL_SQUARE_SIZE);
        		checkerboard.setFill(Color.LAVENDER);
        		background.add(checkerboard);
    		}
    	}
    	
    	Rectangle border = new Rectangle();
    	border.setX(VISUAL_SQUARE_SIZE / 2);
    	border.setY(VISUAL_SQUARE_SIZE / 2);
    	border.setWidth(WINDOW_SIZE_WIDTH - VISUAL_SQUARE_SIZE);
    	border.setHeight(WINDOW_SIZE_HEIGHT - VISUAL_SQUARE_SIZE);
    	border.setStroke(Color.CRIMSON);
    	border.setStrokeWidth(VISUAL_SQUARE_SIZE);
    	border.setFill(Color.TRANSPARENT);
    	background.add(border);
    	
    	return background;
    }
    
    // Dynamically creates the obstacles and adds them to the obstacle list
    private void initializeObstacles() {
    	int numObstacles = rand.nextInt(MAX_OBSTACLES - MIN_OBSTACLES + 1) + MIN_OBSTACLES;
    	
    	if(obstacles.size() != 0) {
    		obstacles.clear();
    	}
    	
    	while(obstacles.size() < numObstacles) {
    		int obstacleGridSize = rand.nextInt(MAX_OBSTACLE_SIZE - MIN_OBSTACLE_SIZE + 1) + MIN_OBSTACLE_SIZE;
            int obstacleActualSize = VISUAL_SQUARE_SIZE * obstacleGridSize - OBSTACLE_BORDER_WIDTH;
            int xGridPos = PATH_GRID_SQUARE_SIZE * rand.nextInt(NUM_X_SQUARES - (obstacleGridSize) - 1);
            int yGridPos = PATH_GRID_SQUARE_SIZE * rand.nextInt(NUM_Y_SQUARES - (obstacleGridSize) - 1);
            int xPos = PATH_GRID_SQUARE_SIZE * xGridPos + (OBSTACLE_BORDER_WIDTH / 2) + VISUAL_SQUARE_SIZE;
            int yPos = PATH_GRID_SQUARE_SIZE * yGridPos + (OBSTACLE_BORDER_WIDTH / 2) + VISUAL_SQUARE_SIZE;

            Rectangle newObstacle = new Rectangle(xPos, yPos, obstacleActualSize, obstacleActualSize);

            boolean overlaps = false;
            for(Rectangle currentObstacle : obstacles) {
                if(newObstacle.getBoundsInParent().intersects(currentObstacle.getBoundsInParent())) {
                    overlaps = true;
                    break;
                }
            }

            if(!overlaps) {
            	newObstacle.setFill(Color.MEDIUMBLUE);
            	newObstacle.setStroke(Color.BLACK);
            	newObstacle.setStrokeWidth(OBSTACLE_BORDER_WIDTH);
                obstacles.add(newObstacle);
                this.getChildren().add(newObstacle);
                
                for(int i = 0; i < obstacleGridSize; ++i) {
                	for(int j = 0; j < obstacleGridSize; ++j) {
                		grid.setObstacle(xGridPos + 1 + i, yGridPos + 1 + j, true);
                	}
                }
            }
        }
    }
    
    // Updates the player's position
    public boolean updatePlayerPosition(double timestep) {
        if (!keysPressed.isEmpty()) {
        	for (KeyCode keyCode : keysPressed) {
                switch (keyCode) {
                    case UP:
                    	player.moveUp(timestep);
                    	break;
                    case DOWN:
                    	player.moveDown(timestep);
                    	break;
                    case LEFT:
                    	player.moveLeft(timestep);
                    	break;
                    case RIGHT:
                    	player.moveRight(timestep);
                    	break;
                }
            }
        	return true;
        } else {
            clearMomentum();
            return false;
        }
    }
    
    public boolean updateComputerPosition(double timestep) {
        if (path == null || path.isEmpty()) {
            return false; // No path to follow
        }

        // Calculate the target position in pixels
        double targetX = path.get(0).getXPos() * PATH_GRID_SQUARE_SIZE + player.getPlayerBorderWidth();
        double targetY = path.get(0).getYPos() * PATH_GRID_SQUARE_SIZE + player.getPlayerBorderWidth();

        // Current position including translations
        double currentX = player.getX() + player.getTranslateX();
        double currentY = player.getY() + player.getTranslateY();

        // Calculate direction vector components
        double dx = targetX - currentX;
        double dy = targetY - currentY;

        // Check if reached the current target node within a threshold
        boolean hasReachedX = Math.abs(currentX - targetX) <= 1;
        boolean hasReachedY = Math.abs(currentY - targetY) <= 1;
        
        // Apply movement and speed
	    if (!hasReachedX && dx > 0) {
	    	player.moveRight(timestep);
	    } else if (!hasReachedX && dx < 0) {
	    	player.moveLeft(timestep);
	    }
    	
    	if (!hasReachedY && dy < 0) {
    		player.moveUp(timestep);
    	} else if (!hasReachedY && dy > 0) {
    		player.moveDown(timestep);
    	}

        if (hasReachedX && hasReachedY) {
            //path.remove(0); // Move to the next node
            player.setXGridPos(path.get(1).getXPos());
            player.setYGridPos(path.get(1).getYPos());
            updatePath();
            
            // Optional path visualization
            if (path.size() > 1) initializePathVisualization();
        }

        return true; // Continuing movement
    }
    
    public void updatePath() {
    	playerPos = new GridNode(player.getXGridPos(), player.getYGridPos(), true);
    	path = grid.findPath(playerPos, target.getGoalNode());
    }
    
    public int checkPlayerCollision() {
    	boolean overlaps = false;
        for(Rectangle currentObstacle : obstacles) {
        	if(player.getBoundsInParent().intersects(currentObstacle.getBoundsInParent())) {
        		overlaps = true;
        		break;
        	}
        }
        
        if(player.getX() + player.getTranslateX() >= WINDOW_SIZE_WIDTH - (2 * PATH_GRID_SQUARE_SIZE) || player.getX() + player.getTranslateX() <= PATH_GRID_SQUARE_SIZE) {
        	overlaps = true;
        } else if(player.getY() + player.getTranslateY() >= WINDOW_SIZE_HEIGHT - (2 * PATH_GRID_SQUARE_SIZE) || player.getY() + player.getTranslateY() <= PATH_GRID_SQUARE_SIZE) {
        	overlaps = true;
        }
        
        if(overlaps) {
        	return LOSE;
        } else if(player.getBoundsInParent().intersects(target.getBoundsInParent())) {
        	return WIN;
        }
        
        return 0;
    }
    
    public void clearMomentum() {
    	player.setVerticalMomentum(0);
    	player.setHorizontalMomentum(0);
    }
    
    public void addKey(KeyEvent e) {
    	keysPressed.add(e.getCode());
    }
    
    public void removeKey(KeyEvent e) {
    	keysPressed.remove(e.getCode());
    }
    
    public void initializePathVisualization() {
    	// Remove previous path visualization
    	this.getChildren().removeIf(node -> "pathNode".equals(node.getUserData()));
    	
    	// Visualization
		 for (GridNode node : path) {
			 Circle point = new Circle();
			 point.setRadius(TARGET_RADIUS / 3); point.setFill(Color.RED);
			 point.setCenterX(node.getXPos() * PATH_GRID_SQUARE_SIZE + PATH_GRID_SQUARE_SIZE / 2);
			 point.setCenterY(node.getYPos() * PATH_GRID_SQUARE_SIZE + PATH_GRID_SQUARE_SIZE / 2);
			 point.setUserData("pathNode");
			 this.getChildren().add(point);
		 }
    }
    
    public void checkFieldRefresh() {
    	for (KeyCode keyCode : keysPressed) {
        	switch (keyCode) {
        		case SPACE:
        			initializeField();
        	}
        }
    }
}