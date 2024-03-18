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
                    neighbor.setHCost(calculateDistance(neighbor, goalNode));
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
//    private double calculateDistance(GridNode start, GridNode destination) {
//    	double length = destination.getXPos() - start.getXPos();
//    	double height = destination.getYPos() - start.getYPos();
//    	return Math.hypot(length, height);
//    }
    
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

        return path;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public void clear() {
    	initializeNodes();
    }
}