package org.openjfx.PathfindingSimulation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class Grid {
    private final int width;
    private final int height;
    private final GridNode[][] nodes;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.nodes = new GridNode[width][height];
        initializeNodes();
    }

    private void initializeNodes() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
            	// Assumes all nodes are walkable initially
                nodes[x][y] = new GridNode(x, y, true);
            }
        }
    }

    public void setObstacle(int x, int y, boolean isObstacle) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            nodes[x][y].setWalkable(!isObstacle);
        }
    }

    public GridNode getNode(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return nodes[x][y];
        }
        return null;
    }

    public List<GridNode> findPath(GridNode startNode, GridNode goalNode) {
        PriorityQueue<GridNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(GridNode::getFCost));
        Set<GridNode> closedSet = new HashSet<>();
        startNode.setGCost(0);
        startNode.setHCost(calculateDistance(startNode, goalNode));
        startNode.setFCost(startNode.getGCost() + startNode.getHCost());
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
        	// Node in openSet having the lowest fCost
            GridNode current = openSet.poll();

            if (current.equals(goalNode)) {
                return reconstructPath(goalNode);
            }

            closedSet.add(current);

            // Process each neighbor of the current node
            for (GridNode neighbor : getNeighbors(current)) {
                if (!neighbor.isWalkable() || closedSet.contains(neighbor)) continue;

                double tentativeGCost = current.getGCost() + calculateDistance(current, neighbor);
                
                if (tentativeGCost < neighbor.getGCost() || !openSet.contains(neighbor)) {
                    neighbor.setGCost(tentativeGCost);
                    neighbor.setHCost(calculateTrueDistance(neighbor, goalNode));
                    neighbor.setParent(current);
                    neighbor.setFCost(neighbor.getGCost() + neighbor.getHCost());

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        // No path found
        return new ArrayList<>();
    }
    
    private List<GridNode> getNeighbors(GridNode current) {
        List<GridNode> neighborList = new ArrayList<>();
        for (int y = -1; y <= 1; ++y) {
            for (int x = -1; x <= 1; ++x) {
                int newX = current.getXPos() + x;
                int newY = current.getYPos() + y;

                // Skip the current node
                if (x == 0 && y == 0) continue;

                // Check grid bounds
                if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                	// Check diagonals for adjacency
                	if (Math.abs(x) + Math.abs(y) == 2) {
                    	GridNode xAdjacent = nodes[current.getXPos()][current.getYPos() + y];
                    	GridNode yAdjacent = nodes[current.getXPos() + x][current.getYPos()];
                    	if (!xAdjacent.isWalkable() || !yAdjacent.isWalkable()) {
                    		continue;
                    	}
                    }
                    neighborList.add(nodes[newX][newY]);
                }
            }
        }

        return neighborList;
    }
    
    // True distance
    private double calculateTrueDistance(GridNode start, GridNode destination) {
    	double length = destination.getXPos() - start.getXPos();
    	double height = destination.getYPos() - start.getYPos();
    	return Math.hypot(length, height);
    }
    
    // Precalculated distance
    private double calculateDistance(GridNode start, GridNode destination) {
    	if (destination == null) {
    		System.out.println("NULL POSITION ON TARGET");
    		return 0;
    	}
    	if (Math.abs(start.getXPos() - destination.getXPos()) == Math.abs(start.getYPos() - destination.getYPos())) return 1.4;
    	else return 1.0;
    }
    
    private List<GridNode> reconstructPath(GridNode goalNode) {
        List<GridNode> path = new ArrayList<>();
        for (GridNode node = goalNode; node != null; node = node.getParent()) {
            path.add(0, node);
        }

        // Start from the end of the path and move towards the start
        int i = path.size() - 1;
        while (i > 0) {
            int j = 0;
            // Try to find the furthest node from the current node i that is directly visible
            while (j < i - 1) {
                if (lineOfSight(path.get(i), path.get(j))) {
                    // If there's a line of sight, remove intermediate nodes
                    for (int k = i - 1; k > j; k--) {
                        path.remove(k);
                    }
                    // Adjust i, since we've modified the path and therefore changed its size
                    i = j + 1;
                    break; // Break out of the loop since we've found the furthest node in sight
                } else {
                    ++j; // Move towards i and check the next node
                }
            }
            i--; // Move to the previous node to check for its line of sight with preceding nodes
        }

        return path;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public boolean isObstacle(int x, int y) {
    	return !nodes[x][y].isWalkable();
    }
    
    public void clear() {
    	initializeNodes();
    }
    
    // Theta* algorithm
    public boolean lineOfSight(GridNode start, GridNode destination) {
    	if (start.getXPos() + 1 >= width || start.getYPos() + 1 >= height || destination.getXPos() + 1 >= width || destination.getYPos() + 1 >= height) return false;
    	
    	boolean sideToSide = false;
    	boolean upAndDown = false;
    	
    	if (start.getXPos() == destination.getXPos()) upAndDown = true;
    	if (start.getYPos() == destination.getYPos()) sideToSide = true;
    	
    	if (upAndDown != sideToSide) {
    		if (!checkLine(start, destination)) return false;
    		else return true;
    	} else {
    		// Check direct line of sight
            if (!checkLine(start, destination)) return false;

            // Adjust for object size by checking additional lines to the right, down, and diagonally down-right
            GridNode rightStart = nodes[start.getXPos() + 1][start.getYPos()];
            GridNode rightEnd = nodes[destination.getXPos() + 1][destination.getYPos()];
            if (rightStart == null || rightEnd == null || !checkLine(rightStart, rightEnd)) return false;

            GridNode downStart = nodes[start.getXPos()][start.getYPos() + 1];
            GridNode downEnd = nodes[destination.getXPos()][destination.getYPos() + 1];
            if (downStart == null || downEnd == null || !checkLine(downStart, downEnd)) return false;

            GridNode diagStart = nodes[start.getXPos() + 1][start.getYPos() + 1];
            GridNode diagEnd = nodes[destination.getXPos() + 1][destination.getYPos() + 1];
            if (diagStart == null || diagEnd == null || !checkLine(diagStart, diagEnd)) return false;
    	}

        return true; // Line of sight is clear for all adjusted checks
    }

    // Line of sight check (I no understand this) >.<
    private boolean checkLine(GridNode start, GridNode destination) {
        // Initial positions of the start and destination nodes
        int startX = start.getXPos();
        int startY = start.getYPos();
        int destX = destination.getXPos();
        int destY = destination.getYPos();

        // Calculate differences in X and Y positions
        int deltaY = destY - startY;
        int deltaX = destX - startX;

        // Fraction that increments as we move, used to decide when to step in y-direction (for dx >= dy) or x-direction (for dy > dx)
        int fraction = 0;

        // Determine the step direction along each axis
        int stepY = (deltaY < 0) ? -1 : 1; // Vertical step direction
        int stepX = (deltaX < 0) ? -1 : 1; // Horizontal step direction

        // Take the absolute values of deltas to work with positive numbers
        deltaY = Math.abs(deltaY);
        deltaX = Math.abs(deltaX);

        if (deltaX >= deltaY) {
            // Moving primarily along the X-axis
            while (startX != destX) {
                fraction += deltaY;
                if (fraction >= deltaX) {
                    // Time to step in Y
                    if (isObstacle(startX + ((stepX - 1) / 2), startY + ((stepY - 1) / 2))) return false;
                    startY += stepY;
                    fraction -= deltaX;
                }
                // Check for obstacles in the current path
                if (fraction != 0 && isObstacle(startX + ((stepX - 1) / 2), startY + ((stepY - 1) / 2))) return false;
                // Special case for horizontal lines to prevent stepping over obstacles
                if (deltaY == 0 && isObstacle(startX + ((stepX - 1) / 2), startY) && isObstacle(startX + ((stepX - 1) / 2), startY - 1)) return false;
                startX += stepX;
            }
        } else {
            // Moving primarily along the Y-axis
            while (startY != destY) {
                fraction += deltaX;
                if (fraction >= deltaY) {
                    // Time to step in X
                    if (isObstacle(startX + ((stepX - 1) / 2), startY + ((stepY - 1) / 2))) return false;
                    startX += stepX;
                    fraction -= deltaY;
                }
                // Check for obstacles in the current path
                if (fraction != 0 && isObstacle(startX + ((stepX - 1) / 2), startY + ((stepY - 1) / 2))) return false;
                // Special case for vertical lines to prevent stepping over obstacles
                if (deltaX == 0 && isObstacle(startX, startY + ((stepY - 1) / 2)) && isObstacle(startX - 1, startY + ((stepY - 1) / 2))) return false;
                startY += stepY;
            }
        }

        return true; // Line of sight is clear
    }
}