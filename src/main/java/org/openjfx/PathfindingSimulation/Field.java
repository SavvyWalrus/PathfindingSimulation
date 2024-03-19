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
	
	// Density of path grid per visual square (ie. Number of squares per square)
	// Causes instability at > 3
	private static final int PATH_GRID_SQUARE_NUM = 3;
	
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
	private static final boolean SHOW_PATH_VISUALIZATION = false;
	private static final boolean SHOW_OBSTACLE_GRID_POSITION = false;
	private static final boolean RECTANGLE_VISUALIZATION = false;
	private static final boolean DOT_VISUALIZATION = true;
	
	// Pathfinding activation
	private static final boolean PATHFINDING_ACTIVE = true;
	private static boolean refreshToggle = true;
	
    // Initializes a Random object
    Random rand = new Random();
    
    // Initializes the player object with no properties
    Player player = new Player();
    
    // Initializes the target object with no properties
    Target target = new Target(VISUAL_SQUARE_SIZE, PATH_GRID_SQUARE_NUM);
    
    // List of obstacle objects
    List<Rectangle> obstacles = new ArrayList<>();
    
    // List of keys currently pressed
    Set<KeyCode> keysPressed = ConcurrentHashMap.newKeySet();
    
    // Creates the background and adds it to a Node list
    List<Node> background = initializeBackground();
    
    // Grid representation for pathfinding
    Grid grid = new Grid(NUM_X_SQUARES * PATH_GRID_SQUARE_NUM, NUM_Y_SQUARES * PATH_GRID_SQUARE_NUM);
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
            success = target.initializeTarget(WINDOW_SIZE_WIDTH, WINDOW_SIZE_HEIGHT, VISUAL_SQUARE_SIZE, PATH_GRID_SQUARE_NUM, obstacles, this, grid);
            if (success) {
            	success = player.initializePlayer(WINDOW_SIZE_WIDTH, WINDOW_SIZE_HEIGHT, VISUAL_SQUARE_SIZE, PATH_GRID_SQUARE_NUM, obstacles, this);
            }
        }
		
		if (!success) {
        	System.out.println("ERROR: Unable to initialize field\nExiting program");
        	System.exit(0);
        }
		
		updatePath();
		
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
            int xGridPos = PATH_GRID_SQUARE_NUM * rand.nextInt(NUM_X_SQUARES - (obstacleGridSize) - 1);
            int yGridPos = PATH_GRID_SQUARE_NUM * rand.nextInt(NUM_Y_SQUARES - (obstacleGridSize) - 1);
            int xPos = xGridPos / PATH_GRID_SQUARE_NUM * VISUAL_SQUARE_SIZE + (OBSTACLE_BORDER_WIDTH / 2) + VISUAL_SQUARE_SIZE;
            int yPos = yGridPos / PATH_GRID_SQUARE_NUM * VISUAL_SQUARE_SIZE + (OBSTACLE_BORDER_WIDTH / 2) + VISUAL_SQUARE_SIZE;

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
                
                for(int x = 0; x <= obstacleGridSize; ++x) {
                	for(int y = 0; y <= obstacleGridSize; ++y) {
            			for (int subY = 0; subY < PATH_GRID_SQUARE_NUM; ++subY) {
            				for (int subX = 0; subX < PATH_GRID_SQUARE_NUM; ++subX) {
            					if (x == obstacleGridSize && subX == PATH_GRID_SQUARE_NUM - 1 || y == obstacleGridSize && subY == PATH_GRID_SQUARE_NUM - 1) continue;
            					grid.setObstacle(xGridPos + 1 + x * PATH_GRID_SQUARE_NUM + subX, yGridPos + 1 + y * PATH_GRID_SQUARE_NUM + subY, true);
            				}
            			}
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
        if (!PATHFINDING_ACTIVE || path == null || path.isEmpty()) {
            return false; // No path to follow
        }

        // Calculate the target position in pixels
        double targetX = path.get(0).getXPos() * VISUAL_SQUARE_SIZE / PATH_GRID_SQUARE_NUM + player.getPlayerBorderWidth();
        double targetY = path.get(0).getYPos() * VISUAL_SQUARE_SIZE / PATH_GRID_SQUARE_NUM + player.getPlayerBorderWidth();

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
	    player.moveTowards(dx, dy, timestep);

        if (hasReachedX && hasReachedY) {
            //path.remove(0); // Move to the next node
            player.setXGridPos(path.get(1).getXPos());
            player.setYGridPos(path.get(1).getYPos());
            updatePath();
        }

        return true; // Continuing movement
    }
    
    public void updatePath() {
    	playerPos = new GridNode(player.getXGridPos(), player.getYGridPos(), true);
    	path = grid.findPath(playerPos, target.getGoalNode());
    	
    	// Optional path and obstacle visualization
    	if (SHOW_OBSTACLE_GRID_POSITION) initializeObstacleGridVisualization();
        if (SHOW_PATH_VISUALIZATION) initializePathVisualization();
    }
    
    public int checkPlayerCollision() {
    	boolean overlaps = false;
        for(Rectangle currentObstacle : obstacles) {
        	if(player.getBoundsInParent().intersects(currentObstacle.getBoundsInParent())) {
        		overlaps = true;
        		break;
        	}
        }
        
        if(player.getX() + player.getTranslateX() >= WINDOW_SIZE_WIDTH - (2 * PATH_GRID_SQUARE_NUM) || player.getX() + player.getTranslateX() <= PATH_GRID_SQUARE_NUM) {
        	overlaps = true;
        } else if(player.getY() + player.getTranslateY() >= WINDOW_SIZE_HEIGHT - (2 * PATH_GRID_SQUARE_NUM) || player.getY() + player.getTranslateY() <= PATH_GRID_SQUARE_NUM) {
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
    	// Aborts if no path exists
    	if (path.isEmpty()) return;
    	
    	// Remove previous path visualization
    	this.getChildren().removeIf(node -> "pathNode".equals(node.getUserData()));
    	
    	// Dot Visualization
    	if (DOT_VISUALIZATION) {
    		for (GridNode node : path) {
    			Circle point = new Circle();
    			point.setRadius(TARGET_RADIUS / 3);
    			point.setFill(Color.GREEN);
    			point.setCenterX(node.getXPos() * VISUAL_SQUARE_SIZE / PATH_GRID_SQUARE_NUM + VISUAL_SQUARE_SIZE - VISUAL_SQUARE_SIZE / PATH_GRID_SQUARE_NUM / 2.0);
    			point.setCenterY(node.getYPos() * VISUAL_SQUARE_SIZE / PATH_GRID_SQUARE_NUM + VISUAL_SQUARE_SIZE - VISUAL_SQUARE_SIZE / PATH_GRID_SQUARE_NUM / 2.0);
    			point.setUserData("pathNode");
    			this.getChildren().add(point);
    		}
    	}
    	
		// Rectangle visualization
    	if (RECTANGLE_VISUALIZATION) {
    		for (GridNode node : path) {
    			Rectangle rect = new Rectangle();
    			rect.setWidth(VISUAL_SQUARE_SIZE);
    			rect.setHeight(VISUAL_SQUARE_SIZE);
    			rect.setFill(Color.GREEN);
    			rect.setX(node.getXPos() * VISUAL_SQUARE_SIZE / PATH_GRID_SQUARE_NUM);
    			rect.setY(node.getYPos() * VISUAL_SQUARE_SIZE / PATH_GRID_SQUARE_NUM);
    			rect.setUserData("pathNode");
    			this.getChildren().add(rect);
    		}
    	}
    }
    
    public void initializeObstacleGridVisualization() {
    	// Remove previous obstacle visualization
    	this.getChildren().removeIf(node -> "obstacleNode".equals(node.getUserData()));
    	
    	// Rectangle visualization
    	if (RECTANGLE_VISUALIZATION) {
    		for (int x = 0; x < NUM_X_SQUARES * PATH_GRID_SQUARE_NUM; ++x) {
        		for (int y = 0; y < NUM_X_SQUARES * PATH_GRID_SQUARE_NUM; ++y) {
        			if (!grid.getNode(x, y).isWalkable()) {
        				Rectangle rect = new Rectangle();
        				rect.setWidth(VISUAL_SQUARE_SIZE / PATH_GRID_SQUARE_NUM);
        				rect.setHeight(VISUAL_SQUARE_SIZE / PATH_GRID_SQUARE_NUM);
        				rect.setFill(Color.RED);
        				rect.setX(grid.getNode(x, y).getXPos() * VISUAL_SQUARE_SIZE / PATH_GRID_SQUARE_NUM);
        				rect.setY(grid.getNode(x, y).getYPos() * VISUAL_SQUARE_SIZE / PATH_GRID_SQUARE_NUM);
        				rect.setUserData("obstacleNode");
        				this.getChildren().add(rect);
        			}
        		}
    		}
    	}
    	
    	// Dot Visualization
    	if (DOT_VISUALIZATION) {
    		for (int x = 0; x < NUM_X_SQUARES * PATH_GRID_SQUARE_NUM; ++x) {
        		for (int y = 0; y < NUM_X_SQUARES * PATH_GRID_SQUARE_NUM; ++y) {
        			if (!grid.getNode(x, y).isWalkable()) {
        				Circle point = new Circle();
        				point.setRadius(TARGET_RADIUS / 3); point.setFill(Color.RED);
        				point.setCenterX(grid.getNode(x, y).getXPos() * VISUAL_SQUARE_SIZE / PATH_GRID_SQUARE_NUM - PATH_GRID_SQUARE_NUM + VISUAL_SQUARE_SIZE / PATH_GRID_SQUARE_NUM);
        				point.setCenterY(grid.getNode(x, y).getYPos() * VISUAL_SQUARE_SIZE / PATH_GRID_SQUARE_NUM - PATH_GRID_SQUARE_NUM + VISUAL_SQUARE_SIZE / PATH_GRID_SQUARE_NUM);
        				point.setUserData("obstacleNode");
        				this.getChildren().add(point);
        			}
        		}
    		}
    	}
    }
    
    public void checkFieldRefresh() {
    	if (refreshToggle) {
    		for (KeyCode keyCode : keysPressed) {
            	switch (keyCode) {
            		case SPACE:
        				initializeField();
        				refreshToggle = false;
        				return;
            	}
            }
    	} else {
    		for (KeyCode keyCode : keysPressed) {
            	switch (keyCode) {
            		case SPACE:
        				return;
            	}
            }
    		refreshToggle = true;
    	}
    }
}