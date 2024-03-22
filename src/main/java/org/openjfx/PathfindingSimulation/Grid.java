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

        return true; // Line of sight is clear for all adjusted checks
    }

    // Line of sight check (I no understand this) >.<
    private boolean checkLine(GridNode start, GridNode destination) {
        int x0 = start.getXPos();
        int y0 = start.getYPos();
        int x1 = destination.getXPos();
        int y1 = destination.getYPos();
        int dy = y1 - y0;
        int dx = x1 - x0;
        int f = 0;
        int sy = (dy < 0) ? -1 : 1;
        int sx = (dx < 0) ? -1 : 1;
        dy = Math.abs(dy);
        dx = Math.abs(dx);

        if (dx >= dy) {
            while (x0 != x1) {
                f += dy;
                if (f >= dx) {
                    if (isObstacle(x0 + ((sx - 1) / 2), y0 + ((sy - 1) / 2))) return false;
                    y0 += sy;
                    f -= dx;
                }
                if (f != 0 && isObstacle(x0 + ((sx - 1) / 2), y0 + ((sy - 1) / 2))) return false;
                if (dy == 0 && isObstacle(x0 + ((sx - 1) / 2), y0) && isObstacle(x0 + ((sx - 1) / 2), y0 - 1)) return false;
                x0 += sx;
            }
        } else {
            while (y0 != y1) {
                f += dx;
                if (f >= dy) {
                    if (isObstacle(x0 + ((sx - 1) / 2), y0 + ((sy - 1) / 2))) return false;
                    x0 += sx;
                    f -= dy;
                }
                if (f != 0 && isObstacle(x0 + ((sx - 1) / 2), y0 + ((sy - 1) / 2))) return false;
                if (dx == 0 && isObstacle(x0, y0 + ((sy - 1) / 2)) && isObstacle(x0 - 1, y0 + ((sy - 1) / 2))) return false;
                y0 += sy;
            }
        }

        return true; // Line of sight is clear
    }
}