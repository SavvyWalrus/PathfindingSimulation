module org.openjfx.PathfindingSimulation {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.openjfx.PathfindingSimulation to javafx.fxml;
    exports org.openjfx.PathfindingSimulation;
}
