package org.openjfx.PathfindingSimulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class ObstacleList {
	int maxObstacles;
	int minObstacles;
	int maxObstacleSize;
	int minObstacleSize;
	
	// List of obstacle objects
	List<Rectangle> obstacles = new ArrayList<>();
	
	static Pane pane;
	static Grid grid;
	
	ObstacleList(Pane pane, Grid grid) {
		ObstacleList.pane = pane;
		ObstacleList.grid = grid;
	}
	
	// Dynamically creates the obstacles and adds them to the obstacle list
	public void initializeObstacles(Pane pane, Grid grid) {
		Random rand = new Random();
		int numObstacles = rand.nextInt(Configuration.getMaxObstacles() - Configuration.getMinObstacles() + 1) + Configuration.getMinObstacles();

		if (obstacles.size() != 0) {
			obstacles.clear();
		}

		while (obstacles.size() < numObstacles) {
			int obstacleGridSize = rand.nextInt(Configuration.getMaxObstacleSize() - Configuration.getMinObstacleSize() + 1) + Configuration.getMinObstacleSize();
			int obstacleActualSize = Configuration.getVisualSquareSize() * obstacleGridSize - Configuration.getObstacleBorderWidth();
			int xGridPos = Configuration.getPathGridSquareNum() * rand.nextInt(Configuration.getNumXSquares() - (obstacleGridSize) - 1);
			int yGridPos = Configuration.getPathGridSquareNum() * rand.nextInt(Configuration.getNumYSquares() - (obstacleGridSize) - 1);
			int xPos = xGridPos / Configuration.getPathGridSquareNum() * Configuration.getVisualSquareSize() + (Configuration.getObstacleBorderWidth() / 2)
					+ Configuration.getVisualSquareSize();
			int yPos = yGridPos / Configuration.getPathGridSquareNum() * Configuration.getVisualSquareSize() + (Configuration.getObstacleBorderWidth() / 2)
					+ Configuration.getVisualSquareSize();

			Rectangle newObstacle = new Rectangle(xPos, yPos, obstacleActualSize, obstacleActualSize);

			boolean overlaps = false;
			for (Rectangle currentObstacle : obstacles) {
				if (newObstacle.getBoundsInParent().intersects(currentObstacle.getBoundsInParent())) {
					overlaps = true;
					break;
				}
			}

			if (!overlaps) {
				newObstacle.setFill(Color.MEDIUMBLUE);
				newObstacle.setStroke(Color.BLACK);
				newObstacle.setStrokeWidth(Configuration.getObstacleBorderWidth());
				obstacles.add(newObstacle);
				pane.getChildren().add(newObstacle);

				for (int x = 0; x <= obstacleGridSize; ++x) {
					for (int y = 0; y <= obstacleGridSize; ++y) {
						for (int subY = 0; subY < Configuration.getPathGridSquareNum(); ++subY) {
							for (int subX = 0; subX < Configuration.getPathGridSquareNum(); ++subX) {
								if (x == obstacleGridSize && subX == Configuration.getPathGridSquareNum() - 1
										|| y == obstacleGridSize && subY == Configuration.getPathGridSquareNum() - 1)
									continue;
								grid.setObstacle(xGridPos + 1 + x * Configuration.getPathGridSquareNum() + subX,
										yGridPos + 1 + y * Configuration.getPathGridSquareNum() + subY, true);
							}
						}
					}
				}
			}
		}
	}
	
	public void initializeObstacleGridVisualization() {
		// Remove previous obstacle visualization
		pane.getChildren().removeIf(node -> "obstacleNode".equals(node.getUserData()));

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
						pane.getChildren().add(rect);
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
						pane.getChildren().add(point);
					}
				}
			}
		}
	}
	
	public List<Rectangle> getObstacles() {
		return obstacles;
	}
}
