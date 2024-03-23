package org.openjfx.PathfindingSimulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ObstacleList {
	int maxObstacles;
	int minObstacles;
	int maxObstacleSize;
	int minObstacleSize;
	
	// List of obstacle objects
	List<Rectangle> obstacles = new ArrayList<>();
	
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
	
	public List<Rectangle> getObstacles() {
		return obstacles;
	}
}
