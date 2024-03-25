package org.openjfx.PathfindingSimulation;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApplication extends Application {
	// Variable representations
	private static final int LOSE = 1;
	private static final int WIN = 2;
	
	private boolean delayTasks = false;
	
	// List of keys currently pressed
	static Set<KeyCode> keysPressed = ConcurrentHashMap.newKeySet();
	
	ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
		
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
        
        Scene scene = new Scene(root, Configuration.getWindowSizeWidth(), Configuration.getWindowSizeHeight());
        
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> addKey(e));
        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> removeKey(e));
        
        primaryStage.setTitle("Pathfinding Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                	if (!delayTasks) {
                	    Platform.runLater(() -> {
		            		gameField.getController().refreshPaths();
		                    gameField.getController().refreshVisualizations();
                	    });
                	}
                } catch (Exception e) {
                    e.printStackTrace(); // Log or handle the exception appropriately
                }
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
        
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
                
				if (PlayerController.updatePlayerPosition(timeStep, gameField.getGrid())) {
                    int collisionType = PlayerController.checkPlayerCollision(gameField.getGrid(), gameField.getTarget());
                    
                    switch(collisionType) {
                        case 0:
                            break;
                        case LOSE:
                        	delayTasks = true;
                        	PlayerController.clearMomentum();
                        	Configuration.setEnemyNumber(1);
                            gameField.initializeField();
                            //gameMenu.incrementDeaths();
                            delayTasks = false;
                            break;
                        case WIN:
                        	delayTasks = true;
                        	PlayerController.clearMomentum();
                        	Configuration.setEnemyNumber(Configuration.getEnemyNumber() + 1);
                            gameField.initializeField();
                            //gameMenu.incrementScore();
                            delayTasks = false;
                            break;
                        default:
                            break;
                    }
                }
				
				if (gameField.getController().updateComputerPosition(timeStep)) {
					for (Enemy enemy : gameField.getController().getEnemies()) {
						if (gameField.getPlayer().getBoundsInParent().intersects(enemy.getBoundsInParent())) {
							delayTasks = true;
							PlayerController.clearMomentum();
							Configuration.setEnemyNumber(1);
                            gameField.initializeField();
                            delayTasks = false;
						}
					}
				}
				
                lastTime = now;
            }
        }.start();
    }
    
    @Override
    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow(); // Attempt to stop all actively executing tasks
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    
	public void addKey(KeyEvent e) {
		keysPressed.add(e.getCode());
	}

	public void removeKey(KeyEvent e) {
		keysPressed.remove(e.getCode());
	}
	
	public static Set<KeyCode> getKeysPressed() {
		return keysPressed;
	}
}