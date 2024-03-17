package org.openjfx.PathfindingSimulation;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.layout.Pane;

public class GameMenu extends Pane {
    private int score = 0;
    private int deaths = 0;
    private Label scoreLabel;
    private Label deathsLabel;
    private Label messageLabel;

    public GameMenu() {
        // Initialize UI components
        initializeUI();
    }

    private void initializeUI() {
        // VBox for layout
        VBox layout = new VBox(10); // Spacing between elements
        layout.setAlignment(Pos.CENTER);

        // Score label
        scoreLabel = new Label("Score: " + score);
        scoreLabel.setFont(new Font("Arial", 16));

        // Deaths label
        deathsLabel = new Label("Deaths: " + deaths);
        deathsLabel.setFont(new Font("Arial", 16));

        // Message label (for win/lose messages)
        messageLabel = new Label("");
        messageLabel.setFont(new Font("Arial", 16));

        // Reset button
        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> resetGame());

        // Add components to the VBox layout
        layout.getChildren().addAll(scoreLabel, deathsLabel, messageLabel, resetButton);

        // Add the VBox to the GameMenu pane
        this.getChildren().add(layout);
    }

    public void incrementScore() {
        score++;
        scoreLabel.setText("Score: " + score);
    }

    public void incrementDeaths() {
        deaths++;
        deathsLabel.setText("Deaths: " + deaths);
    }

    public void displayWinMessage() {
        messageLabel.setText("You Win!");
    }

    public void displayLoseMessage() {
        messageLabel.setText("You Lose!");
    }

    private void resetGame() {
        // Reset score and deaths
        score = 0;
        deaths = 0;
        scoreLabel.setText("Score: 0");
        deathsLabel.setText("Deaths: 0");
        messageLabel.setText("");

        // Reset the field (assuming you have a reference to the field object)
        // You might need to pass a reference to the Field class to this method or make it accessible
        // field.resetField();
    }

    // Add methods to update the game menu based on game events
}