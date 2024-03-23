package org.openjfx.PathfindingSimulation;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PlayerController {
	static Player player;
	static Pane pane;
	
	PlayerController(Player player, Pane pane) {
		PlayerController.player = player;
		PlayerController.pane = pane;
	}
	
	// Updates the player's position
	public static boolean updatePlayerPosition(double timestep, Grid grid) {
		boolean up = false;
		boolean down = false;
		boolean left = false;
		boolean right = false;
		
		if (!MainApplication.getKeysPressed().isEmpty()) {
			for (KeyCode keyCode : MainApplication.getKeysPressed()) {
				switch (keyCode) {
				case UP:
					up = true;
					break;
				case DOWN:
					down = true;
					break;
				case LEFT:
					left = true;
					break;
				case RIGHT:
					right = true;
					break;
				default:
					break;
				}
			}
			
			if ((!up && !down) || (up && down)) {
				player.setVerticalMomentum(0);
			} else if (up) {
				player.moveUp(timestep, grid);
			} else {
				player.moveDown(timestep, grid);
			}
			
			if ((!left && !right) || (left && right)) {
				player.setHorizontalMomentum(0);
			} else if (left) {
				player.moveLeft(timestep, grid);
			} else {
				player.moveRight(timestep, grid);
			}
			
			// Current position including translations
			player.setCurrentX(player.getX() + player.getTranslateX());
			player.setCurrentY(player.getY() + player.getTranslateY());
			player.updateGridPos();
			
			if (Configuration.isShowPlayerHitboxVisualization())
				initializePlayerHitboxVisualization();
			
			return true;
		} else {
			clearMomentum();
			return false;
		}
	}
	
	public static int checkPlayerCollision(Grid grid, Target target) {
		boolean overlaps = false;
		
		for (int i = 0; i < 1; ++i) {
			for (int j = 0; j < 1; ++j) {
				if (!grid.getNode(player.getXGridPos() + i, player.getYGridPos() + j).isWalkable()) {
					overlaps = true;
					break;
				}
			}
		}

		if (overlaps) {
			System.out.println("COLLISION BREAK");
		} else if (player.getBoundsInParent().intersects(target.getBoundsInParent())) {
			return Configuration.getWin();
		}

		return 0;
	}
	
	public static void clearMomentum() {
		player.setVerticalMomentum(0);
		player.setHorizontalMomentum(0);
	}
	
	public static void initializePlayerHitboxVisualization() {
		// Remove previous player visualization
		pane.getChildren().removeIf(node -> "playerNode".equals(node.getUserData()));
		
		// Dot Visualization
		if (true) {
			for (int i = 0; i < Configuration.getPathGridSquareNum(); ++i) {
				for (int j = 0; j < Configuration.getPathGridSquareNum(); ++j) {
					Circle point = new Circle();
					point.setRadius(Configuration.getTargetRadius() / 6);
					point.setFill(Color.BLUE);
					point.setCenterX((player.getXGridPos() + i) * Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum()
							- Configuration.getPathGridSquareNum() + Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum());
					point.setCenterY((player.getYGridPos() + j) * Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum()
							- Configuration.getPathGridSquareNum() + Configuration.getVisualSquareSize() / Configuration.getPathGridSquareNum());
					point.setUserData("playerNode");
					pane.getChildren().add(point);
				}
			}
		}
	}
}
