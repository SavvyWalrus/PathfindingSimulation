package org.openjfx.PathfindingSimulation;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApplication extends Application {
	
	private static final int WINDOW_SIZE_WIDTH = 1000;
	private static final int WINDOW_SIZE_HEIGHT = 1000;
	private static final int LOSE = 1;
	private static final int WIN = 2;
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        
        // Game field setup
        Field gameField = new Field();
        gameField.initializeField();
        
        // Menu setup
        // GameMenu gameMenu = new GameMenu();
        
        // Places the game field and menu in the layout
        root.setCenter(gameField);
        //root.setRight(gameMenu);
        
        Scene scene = new Scene(root, WINDOW_SIZE_WIDTH, WINDOW_SIZE_HEIGHT);
        
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> gameField.addKey(e));
        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> gameField.removeKey(e));
        
        primaryStage.setTitle("Pathfinding Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
        
     // Player movement loop
        new AnimationTimer() {
            private long lastTime = 0;
            
            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                
                // Calculate time step in seconds
                double timeStep = (now - lastTime) / 1_000_000_000.0;
                if (timeStep <= 0 || timeStep > 1.0) {
                    timeStep = 0.001;
                }
                
                // Debug function (SPACEBAR)
                gameField.checkFieldRefresh();
                
                if (gameField.updateComputerPosition(timeStep)) {
                    int collisionType = gameField.checkPlayerCollision();
                    
                    switch(collisionType) {
                        case 0:
                            break;
                        case LOSE:
                            gameField.clearMomentum();
                            gameField.initializeField();
                            //gameMenu.incrementDeaths();
                            break;
                        case WIN:
                            gameField.clearMomentum();
                            gameField.initializeField();
                            //gameMenu.incrementScore();
                            break;
                        default:
                            break;
                    }
                }
                lastTime = now;
            }
        }.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}