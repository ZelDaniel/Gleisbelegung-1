package org.gleisbelegung.ui.window;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class MainWindow implements WindowInterface {
    WindowController controller;

    public MainWindow(Stage stage){
        controller = new WindowController(this, stage, "Gleisbelegung");
    }

    @Override public Scene init() {
        Pane pane = new BorderPane();
        pane.setStyle("-fx-background-color: #303030;");

        Scene scene = new Scene(pane);
        return scene;
    }

    @Override public void onResize(double width, double height) {

    }

    @Override public void onMaximize() {

    }

    @Override public void onMinimize() {

    }

    @Override public void onClose() {

    }
}
