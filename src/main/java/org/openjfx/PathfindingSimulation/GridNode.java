package org.openjfx.PathfindingSimulation;

public class GridNode {
    private final int x;
    private final int y;
    private boolean walkable;

    // Pathfinding variables (for A* algorithm)
    private double gCost; // Cost from the start node
    private double hCost; // Heuristic cost to the end node
    private double fCost; // Total cost (gCost + hCost)
    private GridNode parent; // For reconstructing the path

    public GridNode(int x, int y, boolean walkable) {
        this.x = x;
        this.y = y;
        
        setWalkable(walkable);
        setGCost(Double.POSITIVE_INFINITY);
        setHCost(Double.POSITIVE_INFINITY);
        setFCost(Double.POSITIVE_INFINITY);
    }

    // Getter and setter methods

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public double getGCost() {
    	return gCost;
    }
    
    public void setGCost(double newCost) {
    	gCost = newCost;
    }
    
    public double getHCost() {
    	return hCost;
    }
    
    public void setHCost(double newCost) {
    	hCost = newCost;
    }
    
    public double getFCost() {
    	return fCost;
    }
    
    public void setFCost(double newCost) {
    	fCost = newCost;
    }
    
    public GridNode getParent() {
    	return parent;
    }
    
    public void setParent(GridNode newParent) {
    	parent = newParent;
    }
    
    public int getXPos() {
    	return x;
    }
    
    public int getYPos() {
    	return y;
    }
}